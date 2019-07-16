package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.apiimpl.StorageNetworkHelpers;
import com.lothrazar.storagenetwork.block.cablefilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.lwjgl.system.CallbackI;

import java.util.function.Supplier;

public class CableDataMessage {

  public enum CableMessageType {
    SYNC_DATA, IMPORT_FILTER;
  }

  private boolean whitelist;
  private final int id;
  private int value = 0;

  public CableDataMessage(int id) {
    this.id = id;
  }

  public CableDataMessage(int id, int value, boolean whitelist) {
    this(id);
    this.value = value;
    this.whitelist = whitelist;
  }

  public static class Handler {

    public static void handle(CableDataMessage message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        ServerPlayerEntity player = ctx.get().getSender();
        StorageNetwork.log(message.value + "cable data msg " + message.id);
        ContainerCableFilter con = (ContainerCableFilter) player.openContainer;
        if (con == null || con.link == null) {
          return;
        }
        TileMaster master = StorageNetworkHelpers.getTileMasterForConnectable(con.link.connectable);
        //        INetworkMaster master = StorageNetworkHelpers.getTileMasterForConnectable(con.autoIO.connectable);
        CableMessageType type = CableMessageType.values()[message.id];
        switch (type) {
          case IMPORT_FILTER:
            //TODO: Fix this not auto sync to client
            //TODO: Fix this not auto sync to client
            con.link.getFilter().clear();
            int targetSlot = 0;
            for (ItemStack filterSuggestion : con.link.getStoredStacks()) {
              // Ignore stacks that are already filtered
              if (con.link.getFilter().exactStackAlreadyInList(filterSuggestion)) {
                continue;
              }
              con.link.getFilter().setStackInSlot(targetSlot, filterSuggestion.copy());
              targetSlot++;
              if (targetSlot >= con.link.getFilter().getSlots()) {
                continue;
              }
            }
            PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(con.link.getFilter().getStacks()),
                player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            StorageNetwork.log("Send new refresh client msg, stacks " + con.link.getFilter().getStacks().size());
            con.tile.markDirty();
            break;
          case SYNC_DATA:
            con.link.setPriority(con.link.getPriority() + message.value);
            StorageNetwork.log("PRI after set  " + con.link.getPriority());
            con.link.getFilter().setIsWhitelist(message.whitelist);
            if (master != null) {
              master.clearCache();
            }
            break;
        }
      });
    }
    //
    //    private static void updateCableLink(ServerPlayerEntity player, CableMessageType type) {
    //        ContainerCableFilter con = (ContainerCableFilter) player.openContainer;
    //        if (con == null || con.link == null) {
    //          return;
    //        }
    //        switch (type) {
    //          case TOGGLE_WAY:
    //            con.link.filterDirection = con.link.filterDirection.next();
    //          break;
    //          case TOGGLE_WHITELIST:
    //            con.link.filters.isWhitelist = !con.link.filters.isWhitelist;
    //          break;
    //          case PRIORITY_UP:
    //            con.link.priority++;
    //            if (master != null) {
    //              master.clearCache();
    //            }
    //          break;
    //          case PRIORITY_DOWN:
    //            con.link.priority--;
    //            if (master != null) {
    //              master.clearCache();
    //            }
    //          break;
    //          case IMPORT_FILTER:
    //            // First clear out all filters
    //            //TODO: Fix this not auto sync to client
    //            //TODO: Fix this not auto sync to client
    //
    //          break;
    //        }
    //      }
    //
    //    private static void updateCableIO(ServerPlayerEntity player, CableMessageType type) {
    //        ContainerCableIO con = (ContainerCableIO) player.openContainer;
    //        if (con == null || con.autoIO == null) {
    //          return;
    //        }
    //        INetworkMaster master = StorageNetworkHelpers.getTileMasterForConnectable(con.autoIO.connectable);
    //        switch (type) {
    //          case TOGGLE_MODE:
    //            con.autoIO.operationMustBeSmaller = !con.autoIO.operationMustBeSmaller;
    //          break;
    //          case TOGGLE_WHITELIST:
    //            con.autoIO.filters.isWhitelist = !con.autoIO.filters.isWhitelist;
    //          break;
    //          case PRIORITY_UP:
    //            con.autoIO.priority++;
    //            if (master != null) {
    //              master.clearCache();
    //            }
    //          break;
    //          case PRIORITY_DOWN:
    //            con.autoIO.priority--;
    //            if (master != null) {
    //              master.clearCache();
    //            }
    //          break;
    //          case IMPORT_FILTER:
    //            //TODO: Fix this not auto sync to client
    //            //TODO: Fix this not auto sync to client
    //            int targetSlot = 0;
    //            for (ItemStack filterSuggestion : con.autoIO.getStacksForFilter()) {
    //              // Ignore stacks that are already filtered
    //              if (con.autoIO.filters.exactStackAlreadyInList(filterSuggestion)) {
    //                continue;
    //              }
    //              con.autoIO.filters.setStackInSlot(targetSlot, filterSuggestion.copy());
    //              targetSlot++;
    //              if (targetSlot >= con.autoIO.filters.getSlots()) {
    //                continue;
    //              }
    //            }
    //          break;
    //        }
    //        StorageNetwork.log("Send new refresh client msg");
    //        PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(con.autoIO.filters.getStacks()), player);
    //        con.tile.markDirty();
    //      }
  }

  public static void encode(CableDataMessage msg, PacketBuffer buffer) {
    buffer.writeInt(msg.id);
    buffer.writeInt(msg.value);
    buffer.writeBoolean(msg.whitelist);
  }

  public static CableDataMessage decode(PacketBuffer buffer) {
    return new CableDataMessage(buffer.readInt(), buffer.readInt(), buffer.readBoolean());
  }
}
