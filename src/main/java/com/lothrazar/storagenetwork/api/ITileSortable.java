package com.lothrazar.storagenetwork.api;
public interface ITileSortable {

  boolean isDownwards();

  void setDownwards(boolean downwards);

  EnumSortType getSort();

  void setSort(EnumSortType sort);
}
