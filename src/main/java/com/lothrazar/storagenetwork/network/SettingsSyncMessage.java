package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileNetworkSync;
import com.lothrazar.storagenetwork.item.remote.ItemStorageCraftingRemote;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

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
      ServerPlayer player = ctx.get().getSender();
      if (message.targetTileEntity) {
        BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
        if (tileEntity instanceof ITileNetworkSync) {
          ITileNetworkSync tile = (ITileNetworkSync) tileEntity;
          tile.setSort(message.sort);
          tile.setDownwards(message.direction);
          tile.setJeiSearchSynced(message.jeiSync);
          tileEntity.setChanged();
        }
      }
      else {
        ItemStack stackPlayerHeld = player.containerMenu.getCarried();
        if (stackPlayerHeld.getItem() instanceof ItemStorageCraftingRemote) {
          ItemStorageCraftingRemote.setSort(stackPlayerHeld, message.sort);
          ItemStorageCraftingRemote.setDownwards(stackPlayerHeld, message.direction);
          ItemStorageCraftingRemote.setJeiSearchSynced(stackPlayerHeld, message.jeiSync);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static SettingsSyncMessage decode(FriendlyByteBuf buf) {
    SettingsSyncMessage message = new SettingsSyncMessage();
    message.direction = buf.readBoolean();
    int sort = buf.readInt();
    message.sort = EnumSortType.values()[sort];
    message.targetTileEntity = buf.readBoolean();
    message.pos = buf.readBlockPos();
    message.jeiSync = buf.readBoolean();
    return message;
  }

  public static void encode(SettingsSyncMessage msg, FriendlyByteBuf buf) {
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
