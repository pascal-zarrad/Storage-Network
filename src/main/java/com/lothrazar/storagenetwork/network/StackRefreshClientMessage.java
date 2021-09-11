package com.lothrazar.storagenetwork.network;

import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * Refresh the current screen with large data set of stacks.
 *
 * Used by Containers displaying network inventory as well as most other packets that perform small actions
 *
 */
public class StackRefreshClientMessage {

  private final int size;
  private final int csize;
  private final List<ItemStack> stacks;
  private final List<ItemStack> craftableStacks;

  public StackRefreshClientMessage(List<ItemStack> stacks, List<ItemStack> craftableStacks) {
    super();
    this.stacks = stacks;
    this.craftableStacks = craftableStacks;
    size = stacks.size();
    csize = craftableStacks.size();
  }

  public static void handle(StackRefreshClientMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof IGuiNetwork) {
        IGuiNetwork gui = (IGuiNetwork) mc.screen;
        gui.setStacks(message.stacks);
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static void encode(StackRefreshClientMessage msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.size);
    buf.writeInt(msg.csize);
    for (ItemStack stack : msg.stacks) {
      buf.writeNbt(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
    for (ItemStack stack : msg.craftableStacks) {
      buf.writeNbt(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
  }

  public static StackRefreshClientMessage decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    int csize = buf.readInt();
    List<ItemStack> stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      CompoundTag stacktag = buf.readNbt();
      ItemStack stack = ItemStack.of(stacktag);
      stack.setCount(buf.readInt());
      stacks.add(stack);
    }
    List<ItemStack> craftableStacks = Lists.newArrayList();
    for (int i = 0; i < csize; i++) {
      ItemStack stack = ItemStack.of(buf.readNbt());
      stack.setCount(buf.readInt());
      craftableStacks.add(stack);
    }
    return new StackRefreshClientMessage(stacks, craftableStacks);
  }
}
