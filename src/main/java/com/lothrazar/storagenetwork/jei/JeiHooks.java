package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.config.KeyBindings;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class JeiHooks {

  private static boolean isJeiLoaded() {
    return ModList.get().isLoaded("jei");
  }

  public static String getFilterText() {
    try {
      if (isJeiLoaded()) {
        return getJeiTextInternal();
      }
    }
    catch (Exception e) {
      StorageNetworkMod.LOGGER.info(" mezz.jei.Internal not found " + e);
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
      StorageNetworkMod.LOGGER.info(" mezz.jei.Internal not found " + e);
    }
  }

  private static void setJeiTextInternal(String s) {
    mezz.jei.Internal.getRuntime().getIngredientFilter().setFilterText(s);
  }

  private static String getJeiTextInternal() {
    return mezz.jei.Internal.getRuntime().getIngredientFilter().getFilterText();
  }

  public static void testJeiKeybind(InputConstants.Key keyCode, ItemStack stackUnderMouse) {
    if (!isJeiLoaded()) {
      return;
    }
    final boolean showRecipe = KeyBindings.showRecipe.get(0).isActiveAndMatches(keyCode)
        || KeyBindings.showRecipe.get(1).isActiveAndMatches(keyCode);
    final boolean showUses = KeyBindings.showUses.get(0).isActiveAndMatches(keyCode)
        || KeyBindings.showUses.get(1).isActiveAndMatches(keyCode);
    if (showRecipe || showUses) {
      RecipeIngredientRole mode = showRecipe ? RecipeIngredientRole.OUTPUT : RecipeIngredientRole.INPUT;
      var focus = mezz.jei.Internal.getRuntime().getJeiHelpers().getFocusFactory().createFocus(mode, VanillaTypes.ITEM_STACK, stackUnderMouse);
      mezz.jei.Internal.getRuntime().getRecipesGui().show(focus);
    }
  }
}
