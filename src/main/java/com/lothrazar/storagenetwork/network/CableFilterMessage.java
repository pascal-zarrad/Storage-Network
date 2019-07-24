package com.lothrazar.storagenetwork.network;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CableFilterMessage {

  private int index;
  private ItemStack stack;
  private boolean ore, nbt;

  private CableFilterMessage() {}

  public CableFilterMessage(int index, ItemStack stack, boolean ore, boolean nbt) {
    this.index = index;
    this.stack = stack;
    this.ore = ore;
    this.nbt = nbt;
  }

  public static void handle(CableFilterMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();

      //      if (player.openContainer instanceof ContainerCable) {
      //        TileCable tileCable = ((ContainerCable) player.openContainer).tile;
      //        if (tileCable instanceof TileCableProcess) {
      //          TileCableProcess processCable = (TileCableProcess) tileCable;
      //          processCable.filters.tags = message.ore;
      //          processCable.filters.meta = message.meta;
      //          processCable.filters.nbt = message.nbt;
      //          processCable.markDirty();
      //        }
      //      }
      //      if (player.openContainer instanceof ContainerCableFilter) {
      //        ContainerCableFilter con = (ContainerCableFilter) player.openContainer;
      //        if (con == null || con.link == null) {
      //          return;
      //        }
      //        if (message.stack != null && message.index >= 0) {
      //          con.link.filters.setStackInSlot(message.index, message.stack);
      //        }
      //        con.link.filters.tags = message.ore;
      //        con.link.filters.meta = message.meta;
      //        con.link.filters.nbt = message.nbt;
      //        con.tile.markDirty();
      //      }
      //      if (player.openContainer instanceof ContainerCableIO) {
      //        ContainerCableIO con = (ContainerCableIO) player.openContainer;
      //        if (con == null || con.autoIO == null) {
      //          return;
      //        }
      //        if (message.stack != null && message.index >= 0) {
      //          con.autoIO.filters.setStackInSlot(message.index, message.stack);
      //        }
      //        con.autoIO.filters.tags = message.ore;
      //        con.autoIO.filters.meta = message.meta;
      //        con.autoIO.filters.nbt = message.nbt;
      //        con.tile.markDirty();
      //        UtilTileEntity.updateTile(con.tile.getWorld(), con.tile.getPos());
      //      }
      //      if (player.openContainer instanceof ContainerCableProcessing) {
      //        ContainerCableProcessing con = (ContainerCableProcessing) player.openContainer;
      //        if (!(con.tile instanceof TileCableProcess)) {
      //          return;
      //        }
      //        TileCableProcess tileCable = (TileCableProcess) con.tile;
      //        if (message.stack != null && message.index >= 0) {
      //          tileCable.filters.setStackInSlot(message.index, message.stack);
      //        }
      //        tileCable.filters.tags = message.ore;
      //        tileCable.filters.meta = message.meta;
      //        tileCable.filters.nbt = message.nbt;
      //        tileCable.markDirty();
      //      }
    });
    //    return null;
  }

  public static CableFilterMessage decode(PacketBuffer buf) {
    CableFilterMessage message = new CableFilterMessage();
    message.index = buf.readInt();
    message.ore = buf.readBoolean();
    message.nbt = buf.readBoolean();
    message.stack = ItemStack.read(buf.readCompoundTag());
    return message;
  }

  public static void encode(CableFilterMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.index);
    buf.writeBoolean(msg.ore);
    buf.writeBoolean(msg.nbt);
    buf.writeCompoundTag(msg.stack.serializeNBT());
  }
}
