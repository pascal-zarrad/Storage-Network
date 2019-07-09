package mrriegel.storagenetwork.network;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CableDataMessage {

  // TODO: This message handling should be split up into multiple messages
  public enum CableMessageType {
    PRIORITY_DOWN, PRIORITY_UP, P_ONOFF, TOGGLE_WHITELIST, TOGGLE_MODE, IMPORT_FILTER, TOGGLE_WAY, P_FACE_TOP, P_FACE_BOTTOM, TOGGLE_P_RESTARTTRIGGER, P_CTRL_MORE, P_CTRL_LESS
  }

  private final int id;
  private int value = 0;

  private CableDataMessage(int id) {
    this.id = id;
  }

  private CableDataMessage(int id, int value) {
    this(id);
    this.value = value;
  }

  public static class Handler {

    public static void handle(CableDataMessage message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
        ServerPlayerEntity player = ctx.get().getSender();
        CableMessageType type = CableMessageType.values()[message.id];
        //        if (player.openContainer instanceof ContainerCableIO) {
        //          updateCableIO(player, type);
        //        }
        //        if (player.openContainer instanceof ContainerCableLink) {
        //          updateCableLink(player, type);
        //        }
        //        if (player.openContainer instanceof ContainerCableProcessing) {
        //          updateProcessing(message, player, type);
        //        }
      });
    }
    //
    //    private static void updateProcessing(CableDataMessage message, ServerPlayerEntity player, CableMessageType type) {
    //        ContainerCableProcessing con = (ContainerCableProcessing) player.openContainer;
    //        if (!(con.tile instanceof TileCableProcess)) {
    //          return;
    //        }
    //        TileCableProcess tileCable = (TileCableProcess) con.tile;
    //        switch (type) {
    //          case TOGGLE_P_RESTARTTRIGGER:
    //            //stop listening for result, export recipe into block
    //            tileCable.getRequest().setStatus(ProcessRequestModel.ProcessStatus.EXPORTING);
    //          break;
    //          case P_FACE_BOTTOM:
    //            tileCable.processingBottom = EnumFacing.values()[message.value];
    //          break;
    //          case P_FACE_TOP:
    //            tileCable.processingTop = EnumFacing.values()[message.value];
    //          //                StorageNetwork.log(tileCable.processingTop.name() + " server is ?" + message.value);
    //          break;
    //        }
    //        tileCable.markDirty();
    //        UtilTileEntity.updateTile(tileCable.getWorld(), tileCable.getPos());
    //      }
    //
    //    private static void updateCableLink(ServerPlayerEntity player, CableMessageType type) {
    //        ContainerCableLink con = (ContainerCableLink) player.openContainer;
    //        if (con == null || con.link == null) {
    //          return;
    //        }
    //        INetworkMaster master = StorageNetworkHelpers.getTileMasterForConnectable(con.link.connectable);
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
    //            //            con.link.filters.clear();
    //            //TODO: Fix this not auto sync to client
    //            //TODO: Fix this not auto sync to client
    //            int targetSlot = 0;
    //            for (ItemStack filterSuggestion : con.link.getStoredStacks()) {
    //              // Ignore stacks that are already filtered
    //              if (con.link.filters.exactStackAlreadyInList(filterSuggestion)) {
    //                continue;
    //              }
    //              con.link.filters.setStackInSlot(targetSlot, filterSuggestion.copy());
    //              targetSlot++;
    //              if (targetSlot >= con.link.filters.getSlots()) {
    //                continue;
    //              }
    //            }
    //            PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(con.link.filters.getStacks()), player);
    //            con.tile.markDirty();
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
  }

  public static CableDataMessage decode(PacketBuffer buffer) {
    return new CableDataMessage(buffer.readInt(), buffer.readInt());
  }
}
