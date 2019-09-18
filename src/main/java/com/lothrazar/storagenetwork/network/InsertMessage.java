package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
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

  public InsertMessage(int dim, int buttonID) {
    this.dim = dim;
    this.mouseButton = buttonID;
  }

  private InsertMessage() {}

  public static void handle(InsertMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      TileMaster tileMaster = null;
      if (player.openContainer instanceof ContainerNetwork) {
        tileMaster = ((ContainerNetwork) player.openContainer).getTileMaster();
      }
      int rest;
      ItemStack send = ItemStack.EMPTY;
      ItemStack stack = player.inventory.getItemStack();
      if (message.mouseButton == UtilTileEntity.MOUSE_BTN_LEFT) {//TODO ENUM OR SOMETHING
        rest = tileMaster.insertStack(stack, false);
        if (rest != 0) {
          send = ItemHandlerHelper.copyStackWithSize(stack, rest);
        }
      }
      else if (message.mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT) {
        ItemStack stack1 = stack.copy();
        stack1.setCount(1);
        stack.shrink(1);
        rest = tileMaster.insertStack(stack1, false) + stack.getCount();
        if (rest != 0) {
          send = ItemHandlerHelper.copyStackWithSize(stack, rest);
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
    message.mouseButton = buf.readInt();
    return message;
  }

  public static void encode(InsertMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.dim);
    buf.writeInt(msg.mouseButton);
  }
}
