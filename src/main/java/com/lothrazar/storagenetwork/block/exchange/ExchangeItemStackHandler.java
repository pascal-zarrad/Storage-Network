package com.lothrazar.storagenetwork.block.exchange;

import javax.annotation.Nonnull;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackHandlerEx;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author ajsnarr98 Created MasterItemStackHandler * ItemStackHandler used for interfacing with TileMain like a chest. https://github.com/ajsnarr98/Storage-Network/tree/ajsnarr98-inventory
 * 
 */
public class ExchangeItemStackHandler extends ItemStackHandlerEx {

  private TileMain tileMain;
  boolean allowExtract = false;

  public ExchangeItemStackHandler(int size, boolean allowExtract) {
    super(size);
    this.allowExtract = allowExtract;
    update();
  }

  public void setMain(TileMain main) {
    this.tileMain = main;
    update();
  }

  /**
   * Updates items in the handler based on outside storage.
   */
  public void update() {
    this.stacks.clear();
    int i = 0;
    if (tileMain != null)
      for (ItemStack stack : tileMain.getStacks()) {
        if (i >= this.stacks.size()) {
          break;
        }
        this.stacks.set(i, stack);
        i++;
      }
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty() || tileMain == null) return ItemStack.EMPTY;
    //    if (!isItemValid(slot, stack)) return stack;
    //    validateSlotIndex(slot);
    StorageNetwork.log("insertItem " + stack);
    int remaining = tileMain.insertStack(stack, simulate);
    if (remaining > 0) {
      // if failed, refresh whole list
      update();
      return ItemHandlerHelper.copyStackWithSize(stack, remaining);
    }
    else {
      // if succesful, update internal list
      //      super.insertItem(slot, stack, simulate);
      update();
      return ItemStack.EMPTY;
    }
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (tileMain == null || !allowExtract) {
      //      super.extractItem(slot, amount, simulate);
      return ItemStack.EMPTY;
    }
    ItemStackMatcher matcher = new ItemStackMatcher(getStackInSlot(slot));
    StorageNetwork.log("extractItem " + matcher.getStack());
    ItemStack stack = tileMain.request(matcher, amount, simulate);
    if (stack.isEmpty()) {
      // if failed, refresh whole list
      update();
    }
    else {
      update();
    }
    return stack;
  }

  @Override
  protected void onLoad() {
    update();
  }
}
