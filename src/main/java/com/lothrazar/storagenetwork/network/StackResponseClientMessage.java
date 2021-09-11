package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Used by InsertMessage and RequestMessage as a response back to the client
 * 
 *
 */
public class StackResponseClientMessage {

  private ItemStack stack;

  private StackResponseClientMessage() {}

  StackResponseClientMessage(ItemStack a) {
    stack = a;
  }

  public static void handle(StackResponseClientMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Minecraft.getInstance().player.inventory.setCarried(message.stack);
    });
    ctx.get().setPacketHandled(true);
  }

  public static StackResponseClientMessage decode(FriendlyByteBuf buf) {
    StackResponseClientMessage message = new StackResponseClientMessage();
    message.stack = ItemStack.of(buf.readNbt());
    return message;
  }

  public static void encode(StackResponseClientMessage msg, FriendlyByteBuf buf) {
    buf.writeNbt(msg.stack.serializeNBT());
  }
}
