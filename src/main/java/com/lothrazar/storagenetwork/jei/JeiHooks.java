package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
      StorageNetworkMod.LOGGER.info(e);
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
      StorageNetworkMod.LOGGER.info(e);
    }
  }

  private static void setJeiTextInternal(String s) {
    getRuntime().getIngredientFilter().setFilterText(s);
  }

  private static IJeiRuntime getRuntime() {
    if (!isJeiLoaded()) {
      return null;
    }
    try {
      return JeiPlugin.runtime;
    }
    catch (Exception e) {
      return null;
    }
  }

  private static String getJeiTextInternal() {
    return getRuntime().getIngredientFilter().getFilterText();
  }

  public static void testJeiKeybind(InputConstants.Key keyCode, ItemStack stackUnderMouse) {
    try {
      if (!isJeiLoaded() || getRuntime() == null) {
        return;
      }
      if (stackUnderMouse.is(Items.AIR)) {
        return;
      }
      // old 1.18 way
      //  final boolean showRecipe =  KeyBindings.showRecipe.get(0).isActiveAndMatches(keyCode) || KeyBindings.showRecipe.get(1).isActiveAndMatches(keyCode);
      //  final boolean showUses =  KeyBindings.showUses.get(0).isActiveAndMatches(keyCode) || KeyBindings.showUses.get(1).isActiveAndMatches(keyCode);
      //      IRecipesGui gui = getRuntime().getRecipesGui();
      //      IJeiHelpers helpers = getRuntime().getJeiHelpers();
      //      //TODO: 
      //      //problem: they are private final and non-static
      //      //guess i have to build my own?? 
      //      //      mezz.jei.common.config.KeyBindings b = null; // TODO find me new mezz.jei.common.config.KeyBindings();
      //      final boolean showRecipe = false;//b.getShowRecipe().get(0).isActiveAndMatches(keyCode) || b.getShowRecipe().get(1).isActiveAndMatches(keyCode);
      //      final boolean showUses = false;//b.getShowUses().get(0).isActiveAndMatches(keyCode) || b.getShowUses().get(1).isActiveAndMatches(keyCode);
      //      if (showRecipe || showUses) {
      //        RecipeIngredientRole mode = showRecipe ? RecipeIngredientRole.OUTPUT : RecipeIngredientRole.INPUT;
      //        var focus = helpers.getFocusFactory().createFocus(mode, VanillaTypes.ITEM_STACK, stackUnderMouse);
      //        gui.show(focus);
      //      }
    }
    catch (Exception e) {
      // JEI not installed i guess lol
    }
  }
}
