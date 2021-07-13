package com.lothrazar.storagenetwork.network;

import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileNetworkSync;
import com.lothrazar.storagenetwork.item.remote.ItemStorageCraftingRemote;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SettingsSyncMessage {

  private BlockPos pos;
  private boolean direction;
  private EnumSortType sort;
  private boolean targetTileEntity;
  private boolean jeiSync;

  private SettingsSyncMessage() {}

  public SettingsSyncMessage(BlockPos pos, boolean direction, EnumSortType sort, boolean jeiSync) {
    this.pos = pos;
    this.direction = direction;
    this.sort = sort;
    this.jeiSync = jeiSync;
  }

  public static void handle(SettingsSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      if (message.targetTileEntity) {
        TileEntity tileEntity = player.world.getTileEntity(message.pos);
        if (tileEntity instanceof ITileNetworkSync) {
          ITileNetworkSync tile = (ITileNetworkSync) tileEntity;
          tile.setSort(message.sort);
          tile.setDownwards(message.direction);
          tile.setJeiSearchSynced(message.jeiSync);
          tileEntity.markDirty();
        }
      }
      else {
        ItemStack stackPlayerHeld = player.inventory.getCurrentItem();
        if (stackPlayerHeld.getItem() instanceof ItemStorageCraftingRemote) {
          ItemStorageCraftingRemote.setSort(stackPlayerHeld, message.sort);
          ItemStorageCraftingRemote.setDownwards(stackPlayerHeld, message.direction);
          ItemStorageCraftingRemote.setJeiSearchSynced(stackPlayerHeld, message.jeiSync);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static SettingsSyncMessage decode(PacketBuffer buf) {
    SettingsSyncMessage message = new SettingsSyncMessage();
    message.direction = buf.readBoolean();
    int sort = buf.readInt();
    message.sort = EnumSortType.values()[sort];
    message.targetTileEntity = buf.readBoolean();
    message.pos = buf.readBlockPos();
    message.jeiSync = buf.readBoolean();
    return message;
  }

  public static void encode(SettingsSyncMessage msg, PacketBuffer buf) {
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
    buf.writeBoolean(msg.jeiSync);
  }
}
