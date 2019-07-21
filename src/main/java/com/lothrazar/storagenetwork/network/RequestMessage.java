package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.api.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetworkBase;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RequestMessage {

  private int mouseButton = 0;
  private ItemStack stack = ItemStack.EMPTY;
  private boolean shift, ctrl;

  @Override
  public String toString() {
    return "RequestMessage [mouseButton=" + mouseButton + ", shift=" + shift + ", ctrl=" + ctrl + ", stack=" + stack.toString() + "]";
  }

  public RequestMessage() {}

  public RequestMessage(int id, ItemStack stack, boolean shift, boolean ctrl) {
    mouseButton = id;
    this.stack = new ItemStack(stack.getItem());
    this.shift = shift;
    this.ctrl = ctrl;
  }

  public static void handle(RequestMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      TileMaster tileMaster = null;
      if (player.openContainer instanceof ContainerNetworkBase) {
        ContainerNetworkBase ctr = (ContainerNetworkBase) player.openContainer;
        tileMaster = ctr.getTileMaster();
      }
      if (tileMaster == null) {
        //maybe the table broke after doing this, rare case
        return;
      }
      int in = tileMaster.getAmount(new ItemStackMatcher(message.stack,  false, true));
      ItemStack stack;
      boolean isLeftClick = message.mouseButton == UtilTileEntity.MOUSE_BTN_LEFT;
      boolean isRightClick = message.mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT;
      int sizeRequested = 0;
      if (message.ctrl) {
        sizeRequested = 1;
      }
      else if (isLeftClick) {
        sizeRequested = message.stack.getMaxStackSize();
      }
      else if (isRightClick) {
        sizeRequested = Math.min(message.stack.getMaxStackSize() / 2, in / 2);
      }
      sizeRequested = Math.max(sizeRequested, 1);
      stack = tileMaster.request(
          new ItemStackMatcher(message.stack,  false, true),
          sizeRequested, false);
      if (stack.isEmpty()) {
        //try again with NBT as false
        stack = tileMaster.request(
            new ItemStackMatcher(message.stack,  false, false),
            sizeRequested, false);
      }
      if (!stack.isEmpty()) {
        if (message.shift) {
          ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
        else {
          //when player TAKES an item, go here
          player.inventory.setItemStack(stack);
          PacketRegistry.INSTANCE.sendTo(new StackResponseClientMessage(stack),
              player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
      }
      List<ItemStack> list = tileMaster.getStacks();
      PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
          player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
      player.openContainer.detectAndSendChanges();
    });
  }

  public static RequestMessage decode(PacketBuffer buf) {
    RequestMessage msg = new RequestMessage();
    msg.mouseButton = buf.readInt();
    msg.stack = ItemStack.read(buf.readCompoundTag());
    msg.shift = buf.readBoolean();
    msg.ctrl = buf.readBoolean();
    return msg;
  }

  public static void encode(RequestMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.mouseButton);
    buf.writeCompoundTag(msg.stack.serializeNBT());
    buf.writeBoolean(msg.shift);
    buf.writeBoolean(msg.ctrl);
  }
}
