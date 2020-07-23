package com.lothrazar.storagenetwork.api;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IGuiNetwork extends IGuiPrivate {

  void setStacks(List<ItemStack> stacks);

  boolean getDownwards();

  void setDownwards(boolean val);

  EnumSortType getSort();

  void syncDataToServer();

  void setSort(EnumSortType val);
}
