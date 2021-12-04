package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.cable.linkfilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class CableDataMessage {

  public enum CableMessageType {
    SYNC_DATA, IMPORT_FILTER, SAVE_FITLER;
  }

  private boolean isAllowlist;
  private final int id;
  private int value = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public CableDataMessage(int id) {
    this.id = id;
  }

  public CableDataMessage(int id, int value, boolean is) {
    this(id);
    this.value = value;
    this.isAllowlist = is;
  }

  public CableDataMessage(int id, int value, ItemStack mystack) {
    this(id);
    this.value = value;
    stack = mystack;
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

  public static void handle(CableDataMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      CapabilityConnectableLink link = null;
      ContainerCableFilter container = (ContainerCableFilter) player.containerMenu;
      if (container == null || container.cap == null) {
        return;
      }
      link = container.cap;
      TileMain root = UtilTileEntity.getTileMainForConnectable(link.connectable);
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
              StorageNetwork.LOGGER.error("Exception saving filter slot ", message);
            }
          }
          PacketRegistry.INSTANCE.sendTo(new RefreshFilterClientMessage(link.getFilter().getStacks()),
              player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        break;
        case SYNC_DATA:
          link.setPriority(link.getPriority() + message.value);
          link.getFilter().setIsAllowlist(message.isAllowlist);
          if (root != null) {
            root.clearCache();
          }
        break;
        case SAVE_FITLER:
          link.setFilter(message.value, message.stack.copy());
        break;
      }
      //
      player.connection.send(container.tile.getUpdatePacket());
      //
    });
    ctx.get().setPacketHandled(true);
  }

  public static void encode(CableDataMessage msg, FriendlyByteBuf buffer) {
    buffer.writeInt(msg.id);
    buffer.writeInt(msg.value);
    buffer.writeBoolean(msg.isAllowlist);
    buffer.writeNbt(msg.stack.save(new CompoundTag()));
  }

  public static CableDataMessage decode(FriendlyByteBuf buffer) {
    CableDataMessage c = new CableDataMessage(buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    c.stack = ItemStack.of(buffer.readNbt());
    return c;
  }
}
