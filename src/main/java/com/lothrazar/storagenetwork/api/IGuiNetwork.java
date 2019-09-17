package com.lothrazar.storagenetwork.api;
import com.lothrazar.storagenetwork.api.data.EnumSortType;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IGuiNetwork extends IGuiPrivate {

  void setStacks(List<ItemStack> stacks);

  boolean getDownwards();

  void setDownwards(boolean val);

  EnumSortType getSort();

  void syncData();

  void setSort(EnumSortType val);
}
