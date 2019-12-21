package com.lothrazar.storagenetwork.network;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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
      Minecraft mc = Minecraft.getInstance();//StorageNetwork.proxy.getMinecraft();
      if (mc.currentScreen instanceof IGuiNetwork) {
        IGuiNetwork gui = (IGuiNetwork) mc.currentScreen;
        gui.setStacks(message.stacks);
        //        gui.setCraftableStacks(message.craftableStacks);
      }
    });
  }

  public static void encode(StackRefreshClientMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.size);
    buf.writeInt(msg.csize);
    for (ItemStack stack : msg.stacks) {
      buf.writeCompoundTag(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
    for (ItemStack stack : msg.craftableStacks) {
      buf.writeCompoundTag(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
  }

  public static StackRefreshClientMessage decode(PacketBuffer buf) {
    int size = buf.readInt();
    int csize = buf.readInt();
    List stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      CompoundNBT stacktag = buf.readCompoundTag();
      ItemStack stack = ItemStack.read(stacktag);
      stack.setCount(buf.readInt());
      stacks.add(stack);
    }
    List craftableStacks = Lists.newArrayList();
    for (int i = 0; i < csize; i++) {
      ItemStack stack = ItemStack.read(buf.readCompoundTag());
      stack.setCount(buf.readInt());
      craftableStacks.add(stack);
    }
    return new StackRefreshClientMessage(stacks, craftableStacks);
  }
}
