package com.lothrazar.storagenetwork.block.main;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.util.UtilInventory;
import net.minecraft.world.item.ItemStack;

public class NetworkCache {

  Map<String, DimPos> importCache = new HashMap<>();

  boolean hasCachedSlot(ItemStack stack) {
    return importCache.containsKey(UtilInventory.getStackKey(stack));
  }

  int size() {
    return this.importCache.size();
  }

  DimPos getCachedSlot(ItemStack stack) {
    return importCache.get(UtilInventory.getStackKey(stack));
  }

  void clearCache() {
    importCache = new HashMap<>();
  }

  public void remove(String key) {
    this.importCache.remove(key);
  }

  public void put(String key, DimPos pos) {
    this.importCache.put(key, pos);
  }
}
