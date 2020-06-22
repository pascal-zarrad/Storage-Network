package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.block.cable.export.ContainerCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
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

  /**
   * TODO: mostly duplicate of CableDataMessage, needs merge or refactor
   * 
   * @param message
   * @param ctx
   */
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
      TileMain root = UtilTileEntity.getTileMainForConnectable(link.connectable);
      //
      CableMessageType type = CableMessageType.values()[message.id];
      switch (type) {
        case IMPORT_FILTER:
          link.getFilter().clear();
          int targetSlot = 0;
          for (ItemStack filterSuggestion : link.getStoredStacks(false)) {
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
            catch (Exception ex) {
              //fail slot
              StorageNetwork.LOGGER.error("Exception saving filter slot ", message);
            }
          }
          PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(link.getFilter().getStacks()),
              player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        break;
        case SYNC_DATA:
          link.setPriority(link.getPriority() + message.value);
          link.getFilter().setIsWhitelist(message.whitelist);
          if (root != null) {
            root.clearCache();
          }
        break;
        case SAVE_FITLER:
          link.setFilter(message.value, message.stack.copy());
        break;
      }
      //
      player.connection.sendPacket(tile.getUpdatePacket());
      //
    });
    ctx.get().setPacketHandled(true);
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
