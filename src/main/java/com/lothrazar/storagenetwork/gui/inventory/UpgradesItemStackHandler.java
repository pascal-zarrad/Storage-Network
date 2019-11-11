package com.lothrazar.storagenetwork.gui.inventory;

import com.lothrazar.storagenetwork.api.data.ItemStackHandlerEx;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.item.ItemStack;

public class UpgradesItemStackHandler extends ItemStackHandlerEx {

  public UpgradesItemStackHandler() {
    super(SsnRegistry.UPGRADE_COUNT);
  }

  public UpgradesItemStackHandler(int size) {
    this();
    //    super(SsnRegistry.UPGRADE_COUNT);
  }

  @Override
  protected void validateSlotIndex(int slot) {
    if (stacks.size() == 1) {
      this.setSize(SsnRegistry.UPGRADE_COUNT);
    }
    super.validateSlotIndex(slot);
  }

  @Override
  public int getSlotLimit(int slot) {
    return 1;
  }

  public int getUpgradesOfType(ItemUpgrade upgradeType) {
    int res = 0;
    for (ItemStack stack : getStacks()) {
      if (stack.getItem() == upgradeType) {
        res += Math.max(stack.getCount(), 0);
      }
    }
    return res;
  }
}
