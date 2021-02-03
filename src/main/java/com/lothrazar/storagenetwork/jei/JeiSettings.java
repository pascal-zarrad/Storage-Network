package com.lothrazar.storagenetwork.jei;

import net.minecraftforge.fml.ModList;

public class JeiSettings {

  private static boolean JEISEARCHSYNC = true;

  public static boolean isJeiLoaded() {
    return ModList.get().isLoaded("jei");
  }

  public static boolean isJeiSearchSynced() {
    return JEISEARCHSYNC;
  }

  public static void setJeiSearchSync(boolean jeiSearch) {
    JeiSettings.JEISEARCHSYNC = jeiSearch;
  }
}
