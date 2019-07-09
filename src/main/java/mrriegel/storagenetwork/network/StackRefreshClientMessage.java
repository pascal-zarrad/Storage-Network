package mrriegel.storagenetwork.network;
import com.google.common.collect.Lists;
import mrriegel.storagenetwork.gui.GuiContainerStorageInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

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

  StackRefreshClientMessage(List<ItemStack> stacks, List<ItemStack> craftableStacks) {
    super();
    this.stacks = stacks;
    this.craftableStacks = craftableStacks;
    size = stacks.size();
    csize = craftableStacks.size();
  }

  public static void handle(StackRefreshClientMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      Minecraft mc = Minecraft.getInstance();//StorageNetwork.proxy.getMinecraft();
      // TODO: IStorageInventory API
      if (mc.currentScreen instanceof GuiContainerStorageInventory) {
        GuiContainerStorageInventory gui = (GuiContainerStorageInventory) mc.currentScreen;
          gui.setStacks(message.stacks);
          gui.setCraftableStacks(message.craftableStacks);
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
      ItemStack stack = ItemStack.read(buf.readCompoundTag());
      stacks.add(stack);
    }
    List craftableStacks = Lists.newArrayList();
    for (int i = 0; i < csize; i++) {
      ItemStack stack = ItemStack.read(buf.readCompoundTag());
      craftableStacks.add(stack);
    }
    return new StackRefreshClientMessage(stacks, craftableStacks);
  }
}
