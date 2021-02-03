package com.lothrazar.storagenetwork.block.request;

import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;

public class SlotCraftingNetwork extends CraftingResultSlot {

  private TileMain tileMain;
  private final ContainerNetwork parent;

  public SlotCraftingNetwork(ContainerNetwork parent, PlayerEntity player,
      CraftingInventory craftingInventory, IInventory inventoryIn,
      int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
    this.parent = parent;
  }

  @Override
  public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
    if (playerIn.world.isRemote) {
      return stack;
    }
    List<ItemStack> lis = Lists.newArrayList();
    for (int i = 0; i < parent.matrix.getSizeInventory(); i++) {
      lis.add(parent.matrix.getStackInSlot(i).copy());
    }
    super.onTake(playerIn, stack);
    parent.detectAndSendChanges();
    for (int i = 0; i < parent.matrix.getSizeInventory(); i++) {
      if (parent.matrix.getStackInSlot(i).isEmpty() && getTileMain() != null) {
        ItemStack req = getTileMain().request(
            !lis.get(i).isEmpty() ? new ItemStackMatcher(lis.get(i), false, false) : null, 1, false);
        if (!req.isEmpty()) {
          parent.matrix.setInventorySlotContents(i, req);
        }
      }
    }
    parent.detectAndSendChanges();
    return stack;
  }

  public TileMain getTileMain() {
    return tileMain;
  }

  public void setTileMain(TileMain in) {
    this.tileMain = in;
  }
}
