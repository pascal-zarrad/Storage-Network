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

            break;
          case SYNC_DATA:
            con.link.setPriority(con.link.getPriority() + message.value);
            con.link.getFilter().setIsWhitelist(message.whitelist);
            if (master != null) {
              master.clearCache();
            }
            break;
        }
//
        player.connection.sendPacket(con.tile.getUpdatePacket());
//
      });
    }
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
