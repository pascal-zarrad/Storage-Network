package com.lothrazar.storagenetwork.block.main;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class RecentSlotPointer {

  private BlockPos pos;
  private int slot = -1;

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public BlockPos getPos() {
    return pos;
  }

  public void setPos(BlockPos pos) {
    this.pos = pos;
  }

  public static class StackSlot {

    public int slot = -1;
    public ItemStack stack = ItemStack.EMPTY;
  }
}
