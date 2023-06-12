package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.block.cable.export.ContainerCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.collection.ContainerCollectionFilter;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.CapabilityConnectable;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class CableIOMessage {

  public enum CableMessageType {
    SYNC_DATA, IMPORT_FILTER, SAVE_FITLER, REDSTONE, SYNC_OP, SYNC_OP_TEXT, SYNC_OP_STACK;
  }

  private boolean isAllowlist;
  private final int id;
  private int value = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public CableIOMessage(int id) {
    this.id = id;
  }

  public CableIOMessage(int id, int value, boolean isall) {
    this(id);
    this.value = value;
    this.isAllowlist = isall;
  }

  public CableIOMessage(int id, int value, ItemStack stackin) {
    this(id);
    this.value = value;
    stack = stackin;
  }

  public CableIOMessage(int id, ItemStack s) {
    this(id);
    this.stack = s;
  }

  @Override
  public String toString() {
    return "CableDataMessage{" +
        "isAllowlist=" + isAllowlist +
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
      handleInternal(message, ctx);
    });
    ctx.get().setPacketHandled(true);
  }

  private static void handleInternal(CableIOMessage message, Supplier<NetworkEvent.Context> ctx) {
    ServerPlayer player = ctx.get().getSender();
    CapabilityConnectableAutoIO link = null;
    TileConnectable tile = null;
    CapabilityConnectable connectable = null;
    //TODO: how to refactor
    if (player.containerMenu instanceof ContainerCableExportFilter) {
      ContainerCableExportFilter ctr = (ContainerCableExportFilter) player.containerMenu;
      link = ctr.cap;
      tile = ctr.tile;
    }
    if (player.containerMenu instanceof ContainerCableImportFilter) {
      ContainerCableImportFilter ctr = (ContainerCableImportFilter) player.containerMenu;
      link = ctr.cap;
      tile = ctr.tile;
    }
    if (player.containerMenu instanceof ContainerCollectionFilter) {
      ContainerCollectionFilter ctr = (ContainerCollectionFilter) player.containerMenu;
      connectable = ctr.cap;
      tile = ctr.tile;
    }
    TileMain root = null;
    if (link != null) {
      root = UtilTileEntity.getTileMainForConnectable(link.connectable);
    }
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
            StorageNetworkMod.LOGGER.error("Exception saving filter slot ", message);
          }
        }
        PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(link.getFilter().getStacks()),
            player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      break;
      case SYNC_DATA:
        link.setPriority(link.getPriority() + message.value);
        link.getFilter().setIsAllowlist(message.isAllowlist);
        if (root != null) {
          root.clearCache();
        }
      //        link.operationMustBeSmaller = message.op
      break;
      case SAVE_FITLER:
        if (link != null) {
          link.setFilter(message.value, message.stack.copy());
        }
        else if (connectable != null) {
          connectable.setFilter(message.value, message.stack.copy());
        }
      break;
      case REDSTONE:
        if (link != null) {
          //          StorageNetwork.log("redstone link test " + link.needsRedstone());
          link.toggleNeedsRedstone();
        }
        if (connectable != null) {
          //          StorageNetwork.log("redstone toggle test " + message.value + "?" + connectable.needsRedstone());
          connectable.toggleNeedsRedstone();
        }
      break;
      case SYNC_OP:
        link.operationType = message.value;
      break;
      case SYNC_OP_STACK:
        link.operationStack = message.stack;
      break;
      case SYNC_OP_TEXT:
        link.operationLimit = message.value;
      break;
      default:
      break;
    }
    tile.setChanged();
    player.connection.send(tile.getUpdatePacket());
  }

  public static void encode(CableIOMessage msg, FriendlyByteBuf buffer) {
    buffer.writeInt(msg.id);
    buffer.writeInt(msg.value);
    buffer.writeBoolean(msg.isAllowlist);
    buffer.writeNbt(msg.stack.save(new CompoundTag()));
  }

  public static CableIOMessage decode(FriendlyByteBuf buffer) {
    CableIOMessage c = new CableIOMessage(buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    c.stack = ItemStack.of(buffer.readNbt());
    return c;
  }
}
