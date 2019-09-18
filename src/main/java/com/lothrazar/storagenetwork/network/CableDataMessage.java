package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.storagefilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CableDataMessage {

  public enum CableMessageType {
    SYNC_DATA, IMPORT_FILTER, SAVE_FITLER;
  }

  private boolean whitelist;
  private final int id;
  private int value = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public CableDataMessage(int id) {
    this.id = id;
  }

  public CableDataMessage(int id, int value, boolean whitelist) {
    this(id);
    this.value = value;
    this.whitelist = whitelist;
  }

  public CableDataMessage(int id, int value, ItemStack whitelist) {
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

    public static void handle(CableDataMessage message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        ServerPlayerEntity player = ctx.get().getSender();
        CapabilityConnectableLink link = null;
        ContainerCableImportFilter y; // also must import
        if (player.openContainer instanceof ContainerCableImportFilter) {
          //then 
          ContainerCableImportFilter ctr = (ContainerCableImportFilter) player.openContainer;
          CapabilityConnectableAutoIO link2 = ctr.cap;
          ///
          // //TODO: INHERITACNE FROM
          //  link=link2;//
        }
        ContainerCableFilter container = (ContainerCableFilter) player.openContainer;
        if (container == null || container.link == null) {
          return;
        }
        link = container.link;
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
        player.connection.sendPacket(container.tile.getUpdatePacket());
        //
      });
    }
  }

  public static void encode(CableDataMessage msg, PacketBuffer buffer) {
    buffer.writeInt(msg.id);
    buffer.writeInt(msg.value);
    buffer.writeBoolean(msg.whitelist);
    buffer.writeCompoundTag(msg.stack.write(new CompoundNBT()));
  }

  public static CableDataMessage decode(PacketBuffer buffer) {
    CableDataMessage c = new CableDataMessage(buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    c.stack = ItemStack.read(buffer.readCompoundTag());
    return c;
  }
}
