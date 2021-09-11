package com.lothrazar.storagenetwork.network;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class ClearRecipeMessage {

  public static void handle(ClearRecipeMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      ClearRecipeMessage.clearContainerRecipe(player, true);
    });
    ctx.get().setPacketHandled(true);
  }

  public static ClearRecipeMessage decode(FriendlyByteBuf buf) {
    return new ClearRecipeMessage();
  }

  public static void encode(ClearRecipeMessage msg, FriendlyByteBuf buf) {}

  /**
   * Should be in a public util.
   * 
   * Clears recipe and puts ingredients back in the network. If possible.
   * 
   * May stop partway and leave items in if network is disconnected.
   * 
   * @param player
   * @param doRefresh
   */
  static void clearContainerRecipe(ServerPlayer player, boolean doRefresh) {
    if (player.containerMenu instanceof ContainerNetwork) {
      ContainerNetwork container = (ContainerNetwork) player.containerMenu;
      CraftingContainer craftMatrix = container.getCraftMatrix();
      TileMain root = container.getTileMain();
      for (int i = 0; i < 9; i++) {
        if (root == null) {
          break;
        }
        ItemStack stackInSlot = craftMatrix.getItem(i);
        if (stackInSlot.isEmpty()) {
          continue;
        }
        int numBeforeInsert = stackInSlot.getCount();
        int remainingAfter = root.insertStack(stackInSlot.copy(), false);
        if (numBeforeInsert == remainingAfter) {
          continue;
        }
        if (remainingAfter == 0) {
          craftMatrix.setItem(i, ItemStack.EMPTY);
        }
        else {
          craftMatrix.setItem(i, ItemHandlerHelper.copyStackWithSize(stackInSlot, remainingAfter));
        }
      }
      if (doRefresh) {
        List<ItemStack> list = root.getStacks();
        PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
            player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        container.broadcastChanges();
      }
    }
  }
}
