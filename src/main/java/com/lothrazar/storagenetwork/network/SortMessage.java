package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.api.ITileSortable;
import com.lothrazar.storagenetwork.api.data.EnumSortType;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.item.remote.ItemRemote;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SortMessage {

  private BlockPos pos;
  private boolean direction;
  private EnumSortType sort;
  private boolean targetTileEntity;

  private SortMessage() {}

  public SortMessage(BlockPos pos, boolean direction, EnumSortType sort) {
    this.pos = pos;
    this.direction = direction;
    this.sort = sort;
  }

  public static void handle(SortMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      if (message.targetTileEntity) {
        TileEntity tileEntity = player.world.getTileEntity(message.pos);
        if (tileEntity instanceof ITileSortable) {
          ITileSortable tile = (TileRequest) tileEntity;
          tile.setSort(message.sort);
          tile.setDownwards(message.direction);
          tileEntity.markDirty();
        }
      }
      else {
        ItemStack stackPlayerHeld = player.inventory.getCurrentItem();
        if (stackPlayerHeld.getItem() instanceof ItemRemote) {
          ItemRemote.setSort(stackPlayerHeld, message.sort);
          ItemRemote.setDownwards(stackPlayerHeld, message.direction);
        }
      }
    });
  }

  public static SortMessage decode(PacketBuffer buf) {
    SortMessage message = new SortMessage();
    message.direction = buf.readBoolean();
    int sort = buf.readInt();
    message.sort = EnumSortType.values()[sort];
    message.targetTileEntity = buf.readBoolean();
    message.pos = buf.readBlockPos();
    return message;
  }

  public static void encode(SortMessage msg, PacketBuffer buf) {
    buf.writeBoolean(msg.direction);
    buf.writeInt(msg.sort.ordinal());
    if (msg.pos != null) {
      buf.writeBoolean(true);
      buf.writeBlockPos(msg.pos);
    }
    else { // to avoid null values // inconsistent buffer size
      buf.writeBoolean(false);
      buf.writeBlockPos(BlockPos.ZERO);
    }
  }
}
