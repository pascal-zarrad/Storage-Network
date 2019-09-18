package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.block.cable.storagefilter.GuiCableFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Refresh the current screen with large data set of stacks.
 * <p>
 * Used by Containers displaying network inventory as well as most other packets that perform small actions
 */
public class RefreshFilterClientMessage {

  private int size;
  private List<ItemStack> stacks;

  private RefreshFilterClientMessage() {}

  public RefreshFilterClientMessage(List<ItemStack> stacks) {
    super();
    this.stacks = stacks;
    size = stacks.size();
  }

  public static void handle(RefreshFilterClientMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (Minecraft.getInstance().currentScreen instanceof GuiCableFilter) {
        GuiCableFilter gui = (GuiCableFilter) Minecraft.getInstance().currentScreen;
        gui.setFilterItems(message.stacks);
      }
    });
  }

  public static RefreshFilterClientMessage decode(PacketBuffer buf) {
    RefreshFilterClientMessage message = new RefreshFilterClientMessage();
    message.size = buf.readInt();
    message.stacks = new ArrayList<>();
    for (int i = 0; i < message.size; i++) {
      ItemStack stack = ItemStack.read(buf.readCompoundTag());
      stack.setCount(buf.readInt());
      message.stacks.add(stack);
    }
    return message;
  }

  public static void encode(RefreshFilterClientMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.size);
    for (ItemStack stack : msg.stacks) {
      buf.writeCompoundTag(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
  }
}
