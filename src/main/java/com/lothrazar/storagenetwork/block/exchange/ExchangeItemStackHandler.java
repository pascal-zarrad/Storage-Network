package com.lothrazar.storagenetwork.block.exchange;

import javax.annotation.Nonnull;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackHandlerEx;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author ajsnarr98 Created MasterItemStackHandler * ItemStackHandler used for interfacing with TileMain like a chest. https://github.com/ajsnarr98/Storage-Network/tree/ajsnarr98-inventory
 * 
 */
public class ExchangeItemStackHandler extends ItemStackHandlerEx {

  private TileMain tileMain;

  public ExchangeItemStackHandler() {
    super(ConfigRegistry.EXCHANGEBUFFER.get());
    System.out.println("ConfigRegistry.EXCHANGEBUFFER.get()" + ConfigRegistry.EXCHANGEBUFFER.get());
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
    if (tileMain == null) {
      return;
    }
    //    this.stacks.clear(); 
    int i = 0;
    for (ItemStack stack : tileMain.getStacks()) {
      if (i >= this.stacks.size()) {
        break;
      }
      this.stacks.set(i, stack);
      i++;
    }
  }

  /**
   * @param slot
   *          may not end up in the exact slot specified
   * 
   * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack). May be the same as the input ItemStack if unchanged, otherwise a new
   *         ItemStack. The returned ItemStack can be safely modified after.
   */
  @Override
  @Nonnull
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty() || tileMain == null || !isItemValid(slot, stack)) {
      return stack;
    }
    validateSlotIndex(slot);
    int remaining = tileMain.insertStack(stack, simulate);
    StorageNetwork.log("exchange: insertItem " + stack + " remain " + remaining);
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
    if (tileMain == null) {
      //            super.extractItem(slot, amount, simulate);
      return ItemStack.EMPTY;
    }
    ItemStackMatcher matcher = new ItemStackMatcher(getStackInSlot(slot));
    //    StorageNetwork.log("extractItem " + matcher.getStack());
    ItemStack stack = tileMain.request(matcher, amount, simulate);
    update();
    StorageNetwork.log("exchange: extractItem; after " + stack);
    return stack;
  }

  @Override
  protected void onLoad() {
    update();
  }
}
