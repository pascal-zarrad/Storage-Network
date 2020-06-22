package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CableLimitMessage {

  private int limit;
  private ItemStack stack;

  private CableLimitMessage() {}

  public CableLimitMessage(int limit, ItemStack stack) {
    super();
    this.limit = limit;
    this.stack = stack;
  }

  public static void handle(CableLimitMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      //      ServerPlayerEntity player = ctx.get().getSender();
      //      if (player.openContainer instanceof ContainerCableIO) {
      //        ContainerCableIO con = (ContainerCableIO) player.openContainer;
      //        if (con == null || con.autoIO == null) {
      //          return;
      //        }
      //        con.autoIO.operationLimit = message.limit;
      //        con.autoIO.operationStack = message.stack;
      //        con.tile.markDirty();
      //      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static CableLimitMessage decode(PacketBuffer buf) {
    CableLimitMessage message = new CableLimitMessage();
    message.limit = buf.readInt();
    message.stack = ItemStack.read(buf.readCompoundTag());
    return message;
  }

  public static void encode(CableLimitMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.limit);
    buf.writeCompoundTag(msg.stack.serializeNBT());
  }
}
