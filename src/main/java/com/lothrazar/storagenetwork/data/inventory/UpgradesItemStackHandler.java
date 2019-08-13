package com.lothrazar.storagenetwork.data.inventory;

import javax.annotation.Nonnull;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.item.ItemStack;

public class UpgradesItemStackHandler extends ItemStackHandlerEx {

  public UpgradesItemStackHandler() {
    super(SsnRegistry.UPGRADE_COUNT);
  }

  @Override
  protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
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
