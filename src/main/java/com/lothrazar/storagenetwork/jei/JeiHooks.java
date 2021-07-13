package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.StorageNetwork;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.Focus;

public class JeiHooks {

  public static boolean isJeiLoaded() {
    return ModList.get().isLoaded("jei");
  }

  public static String getFilterText() {
    try {
      if (isJeiLoaded()) {
        return getJeiTextInternal();
      }
    }
    catch (Exception e) {
      StorageNetwork.LOGGER.info(" mezz.jei.Internal not found " + e);
    }
    return "";
  }

  /**
   * so if JEI is not loaded, this will be called but then its an empty FN
   * 
   * @param s
   */
  public static void setFilterText(String s) {
    try {
      if (isJeiLoaded()) {
        setJeiTextInternal(s);
      }
    }
    catch (Exception e) {
      StorageNetwork.LOGGER.info(" mezz.jei.Internal not found " + e);
    }
  }

  private static void setJeiTextInternal(String s) {
    mezz.jei.Internal.getRuntime().getIngredientFilter().setFilterText(s);
  }

  private static String getJeiTextInternal() {
    return mezz.jei.Internal.getRuntime().getIngredientFilter().getFilterText();
  }

  public static void testJeiKeybind(InputMappings.Input keyCode, ItemStack stackUnderMouse) {
    if (!isJeiLoaded()) {
      return;
    }
    final boolean showRecipe = KeyBindings.showRecipe.isActiveAndMatches(keyCode);
    final boolean showUses = KeyBindings.showUses.isActiveAndMatches(keyCode);
    if (showRecipe || showUses) {
      IFocus.Mode mode = showRecipe ? IFocus.Mode.OUTPUT : IFocus.Mode.INPUT;
      mezz.jei.Internal.getRuntime().getRecipesGui().show(new Focus<ItemStack>(mode, stackUnderMouse));
    }
  }
}
