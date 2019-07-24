package com.lothrazar.storagenetwork.network;
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

public class InsertMessage {

  private int dim, mouseButton;
  private ItemStack stack;

  public InsertMessage(int dim, int buttonID, ItemStack stack) {
    this.dim = dim;
    this.stack = stack;
    mouseButton = buttonID;
  }

  private InsertMessage() {
  }

  public static void handle(InsertMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();

      TileMaster tileMaster = null;
      if (player.openContainer instanceof ContainerNetworkBase) {
        tileMaster = ((ContainerNetworkBase) player.openContainer).getTileMaster();
      }
      int rest;
      ItemStack send = ItemStack.EMPTY;
      if (message.mouseButton == UtilTileEntity.MOUSE_BTN_LEFT) {//TODO ENUM OR SOMETHING
        rest = tileMaster.insertStack(message.stack, false);
        if (rest != 0) {
          send = ItemHandlerHelper.copyStackWithSize(message.stack, rest);
        }
      }
      else if (message.mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT) {
        ItemStack stack1 = message.stack.copy();
        stack1.setCount(1);
        message.stack.shrink(1);
        rest = tileMaster.insertStack(stack1, false) + message.stack.getCount();
        if (rest != 0) {
          send = ItemHandlerHelper.copyStackWithSize(message.stack, rest);
        }
      }
      //TODO: WHY TWO messages/?
      player.inventory.setItemStack(send);
      PacketRegistry.INSTANCE.sendTo(new StackResponseClientMessage(send),
          player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
      List<ItemStack> list = tileMaster.getStacks();
      PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
          player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
      player.openContainer.detectAndSendChanges();
    });
  }

  public static InsertMessage decode(PacketBuffer buf) {
    InsertMessage message = new InsertMessage();
    message.dim = buf.readInt();
    message.stack = ItemStack.read(buf.readCompoundTag());
    message.mouseButton = buf.readInt();
    return message;
  }

  public static void encode(InsertMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.dim);
    buf.writeCompoundTag(msg.stack.serializeNBT());
    buf.writeInt(msg.mouseButton);
  }
}
