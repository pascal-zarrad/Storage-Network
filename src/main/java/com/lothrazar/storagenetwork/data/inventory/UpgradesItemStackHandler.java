package com.lothrazar.storagenetwork.data.inventory;
import com.lothrazar.storagenetwork.api.data.EnumUpgradeType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
//import mrriegel.storagenetwork.block.cable.io.ContainerCableIO;
//import mrriegel.storagenetwork.registry.ModItems;

public class UpgradesItemStackHandler extends ItemStackHandlerEx {

  public UpgradesItemStackHandler() {
    super(1);//ContainerCableIO.UPGRADE_COUNT);
  }

  @Override
  protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
    return 1;
  }

  public int getUpgradesOfType(EnumUpgradeType upgradeType) {
    int res = 0;
    for (ItemStack stack : getStacks()) {
      //      if (stack.getItem() == ModItems.upgrade) {
      //        res += Math.max(stack.getCount(), 0);
      //      }
    }
    return res;
  }
}
