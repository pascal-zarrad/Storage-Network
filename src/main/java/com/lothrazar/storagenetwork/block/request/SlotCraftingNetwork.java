package com.lothrazar.storagenetwork.block.request;

import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

public class SlotCraftingNetwork extends ResultSlot {

  private TileMain tileMain;
  private final ContainerNetwork parent;

  public SlotCraftingNetwork(ContainerNetwork parent, Player player,
      CraftingContainer craftingInventory, Container inventoryIn,
      int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
    this.parent = parent;
  }

  @Override
  public ItemStack onTake(Player playerIn, ItemStack stack) {
    if (playerIn.level.isClientSide) {
      return stack;
    }
    List<ItemStack> lis = Lists.newArrayList();
    for (int i = 0; i < parent.matrix.getContainerSize(); i++) {
      lis.add(parent.matrix.getItem(i).copy());
    }
    super.onTake(playerIn, stack);
    parent.broadcastChanges();
    for (int i = 0; i < parent.matrix.getContainerSize(); i++) {
      if (parent.matrix.getItem(i).isEmpty() && getTileMain() != null) {
        ItemStack req = getTileMain().request(
            !lis.get(i).isEmpty() ? new ItemStackMatcher(lis.get(i), false, false) : null, 1, false);
        if (!req.isEmpty()) {
          parent.matrix.setItem(i, req);
        }
      }
    }
    parent.broadcastChanges();
    return stack;
  }

  public TileMain getTileMain() {
    return tileMain;
  }

  public void setTileMain(TileMain in) {
    this.tileMain = in;
  }
}
