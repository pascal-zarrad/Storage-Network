package com.lothrazar.storagenetwork.api;

import com.lothrazar.storagenetwork.api.data.EnumSortType;

public interface ITileSortable {

  boolean isDownwards();

  void setDownwards(boolean downwards);

  EnumSortType getSort();

  void setSort(EnumSortType sort);
}
