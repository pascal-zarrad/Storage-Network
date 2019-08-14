package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.block.cable.export.ContainerCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class CableIOMessage {

  public enum CableMessageType {
    SYNC_DATA, IMPORT_FILTER, SAVE_FITLER;
  }

  private boolean whitelist;
  private final int id;
  private int value = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public CableIOMessage(int id) {
    this.id = id;
  }

  public CableIOMessage(int id, int value, boolean whitelist) {
    this(id);
    this.value = value;
    this.whitelist = whitelist;
  }

  public CableIOMessage(int id, int value, ItemStack whitelist) {
    this(id);
    this.value = value;
    stack = whitelist;
  }

  @Override
  public String toString() {
    return "CableDataMessage{" +
        "whitelist=" + whitelist +
        ", id=" + id +
        ", value=" + value +
        ", stack=" + stack +
        '}';
  }

  public static class Handler {

    public static void handle(CableIOMessage message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        ServerPlayerEntity player = ctx.get().getSender();
        CapabilityConnectableAutoIO link = null;
        TileCableWithFacing tile = null;
        //super super HACK TODO: this is hacky
        if (player.openContainer instanceof ContainerCableExportFilter) {
          ContainerCableExportFilter ctr = (ContainerCableExportFilter) player.openContainer;
          link = ctr.cap;
          tile = ctr.tile;
        }
        if (player.openContainer instanceof ContainerCableImportFilter) {
          ContainerCableImportFilter ctr = (ContainerCableImportFilter) player.openContainer;
          link = ctr.cap;
          tile = ctr.tile;
        }
        TileMaster master = UtilTileEntity.getTileMasterForConnectable(link.connectable);
        //        INetworkMaster master = StorageNetworkHelpers.getTileMasterForConnectable(con.autoIO.connectable);
        CableMessageType type = CableMessageType.values()[message.id];
        switch (type) {
          case IMPORT_FILTER:
            //TODO: Fix this not auto sync to client
            link.getFilter().clear();
            int targetSlot = 0;
            for (ItemStack filterSuggestion : link.getStoredStacks()) {
              // Ignore stacks that are already filtered
              if (link.getFilter().exactStackAlreadyInList(filterSuggestion)) {
                continue;
              }
              //int over max
              try {
                link.getFilter().setStackInSlot(targetSlot, filterSuggestion.copy());
                targetSlot++;
                if (targetSlot >= link.getFilter().getSlots()) {
                  continue;
                }
              }
              catch (RuntimeException ex) {
                //fail slot
                StorageNetwork.log("Exception saving slot " + message);
              }
            }
            PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(link.getFilter().getStacks()),
                player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
          break;
          case SYNC_DATA:
            link.setPriority(link.getPriority() + message.value);
            link.getFilter().setIsWhitelist(message.whitelist);
            if (master != null) {
              master.clearCache();
            }
          break;
          case SAVE_FITLER:
            //            FilterItemStackHandler list = con.link.getFilter();
            link.setFilter(message.value, message.stack.copy());
          break;
        }
        //
        player.connection.sendPacket(tile.getUpdatePacket());
        //
      });
    }
  }

  public static void encode(CableIOMessage msg, PacketBuffer buffer) {
    buffer.writeInt(msg.id);
    buffer.writeInt(msg.value);
    buffer.writeBoolean(msg.whitelist);
    buffer.writeCompoundTag(msg.stack.write(new CompoundNBT()));
  }

  public static CableIOMessage decode(PacketBuffer buffer) {
    CableIOMessage c = new CableIOMessage(buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    c.stack = ItemStack.read(buffer.readCompoundTag());
    return c;
  }
}
