package com.lothrazar.storagenetwork.network;
import com.lothrazar.storagenetwork.gui.ContainerNetworkBase;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClearRecipeMessage {

  public static void handle(ClearRecipeMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      ServerWorld world = player.getServerWorld();
        ClearRecipeMessage.clearContainerRecipe(player, true);
    });
  }

  public static ClearRecipeMessage decode(PacketBuffer buf) {
    return new ClearRecipeMessage();
  }

  public static void encode(ClearRecipeMessage msg, PacketBuffer buf) {
  }
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
  static void clearContainerRecipe(ServerPlayerEntity player, boolean doRefresh) {
    if (player.openContainer instanceof ContainerNetworkBase) {
      ContainerNetworkBase container = (ContainerNetworkBase) player.openContainer;
      CraftingInventory craftMatrix = container.getCraftMatrix();
      TileMaster tileMaster = container.getTileMaster();
      for (int i = 0; i < 9; i++) {
        if (tileMaster == null) {
          break;
        }
        ItemStack stackInSlot = craftMatrix.getStackInSlot(i);
        if (stackInSlot.isEmpty()) {
          continue;
        }
        int numBeforeInsert = stackInSlot.getCount();
        int remainingAfter = tileMaster.insertStack(stackInSlot.copy(), false);
        if (numBeforeInsert == remainingAfter) {
          continue;
        }
        if (remainingAfter == 0) {
          craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        else {
          craftMatrix.setInventorySlotContents(i, ItemHandlerHelper.copyStackWithSize(stackInSlot, remainingAfter));
        }
      }
      if (doRefresh) {
        List<ItemStack> list = tileMaster.getStacks();
        PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
            player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        container.detectAndSendChanges();
      }
    }
  }
}
