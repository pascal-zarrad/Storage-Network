package com.lothrazar.storagenetwork.util;

import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class UtilInventory {

  public static String formatLargeNumber(int size) {
    if (size < Math.pow(10, 3)) {
      return size + "";
    }
    else if (size < Math.pow(10, 6)) {
      //      float r = (size) / 1000.0F;
      int rounded = Math.round(size / 1000.0F); //so 1600 => 1.6 and then rounded to become 2.
      return rounded + "K";
    }
    else if (size < Math.pow(10, 9)) {
      int rounded = Math.round(size / (float) Math.pow(10, 6));
      return rounded + "M";
    }
    else if (size < Math.pow(10, 12)) {
      int rounded = Math.round(size / (float) Math.pow(10, 9));
      return rounded + "B";
    }
    return size + "";
  }

  public static int containsAtLeastHowManyNeeded(IItemHandler inv, ItemStack stack, int minimumCount) {
    int found = 0;
    for (int i = 0; i < inv.getSlots(); i++) {
      if (ItemHandlerHelper.canItemStacksStack(inv.getStackInSlot(i), stack)) {
        found += inv.getStackInSlot(i).getCount();
      }
    }
    //do you have all 4? or do you need 2 still
    if (found >= minimumCount) {
      return 0;
    }
    return minimumCount - found;
  }

  public static ItemStack extractItem(IItemHandler inv, ItemStackMatcher fil, int num, boolean simulate) {
    if (inv == null || fil == null) {
      return ItemStack.EMPTY;
    }
    int extracted = 0;
    for (int i = 0; i < inv.getSlots(); i++) {
      ItemStack slot = inv.getStackInSlot(i);
      if (fil.match(slot)) {
        ItemStack ex = inv.extractItem(i, 1, simulate);
        if (!ex.isEmpty()) {
          extracted++;
          if (extracted == num) {
            return ItemHandlerHelper.copyStackWithSize(slot, num);
          }
          else {
            i--;
          }
        }
      }
    }
    return ItemStack.EMPTY;
  }
}
