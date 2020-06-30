package com.lothrazar.storagenetwork.capability.handler;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import com.lothrazar.storagenetwork.block.main.TileMain;

/**
 * ItemStackHandler used for interfacing with TileMain like a chest.
 */
public class MasterItemStackHandler extends ItemStackHandlerEx {
  
  private TileMain tileMain;
  
  public MasterItemStackHandler(TileMain tileMain) {
    this(tileMain, Integer.MAX_VALUE);
  }
  
  public MasterItemStackHandler(TileMain tileMain, int size) {
    super(size);
    this.tileMain = tileMain;
    
    update();
  }
  
  /**
   * Updates items in the handler based on outside storage.
   */
  public void update() {
    this.stacks.clear();
    int i = 0;
    for (ItemStack stack : tileMain.getStacks()) {
      this.stacks.set(i, stack);
      i++;
    }
  }
  
  @Override
  @Nonnull
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) return ItemStack.EMPTY;
    if (!isItemValid(slot, stack)) return stack;
    validateSlotIndex(slot);
    
    int remaining = tileMain.insertStack(stack, simulate);
    
    if (remaining > 0) {
      // if failed, refresh whole list
      update();
      return ItemHandlerHelper.copyStackWithSize(stack, remaining);
    
    } else {
      // if succesful, update internal list
      super.insertItem(slot, stack, simulate);
      return ItemStack.EMPTY;
    }
  }
  
  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    validateSlotIndex(slot);
    ItemStackMatcher matcher = new ItemStackMatcher(getStackInSlot(slot));
    ItemStack stack =  tileMain.request(matcher, amount, simulate);
    
    if (stack.isEmpty()) {
      // if failed, refresh whole list
      update();
    } else {
      // if succesful, update that slot in internal list
      super.extractItem(slot, amount, simulate);
    }
    
    return stack;
  }
  
  @Override
  public int getSlotLimit(int slot)
  {
    return Integer.MAX_VALUE;
  }
  
  @Override
  protected void onLoad() {
    update();
  }
}