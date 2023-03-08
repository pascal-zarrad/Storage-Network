package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.extensions.IForgeKeyMapping;
import net.minecraftforge.fml.ModList;

public class JeiHooks {

  private static IJeiRuntime jeiRuntime;

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
      StorageNetworkMod.LOGGER.info(" runtime not found " + e);
    }
    return "";
  }

  public static void setJeiRuntime(IJeiRuntime jeiRuntime) {
    JeiHooks.jeiRuntime = jeiRuntime;
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
      StorageNetworkMod.LOGGER.info(" runtime not found " + e);
    }
  }

  private static void setJeiTextInternal(String s) {
    jeiRuntime.getIngredientFilter().setFilterText(s);
  }

  private static String getJeiTextInternal() {
    return jeiRuntime.getIngredientFilter().getFilterText();
  }

  public static void testJeiKeybind(InputConstants.Key keyCode, ItemStack stackUnderMouse) {
    if (!isJeiLoaded()) {
      return;
    }
    if (stackUnderMouse.is(Items.AIR)) {
      return;
    }
    
    final boolean showRecipe = InputConstants.KEY_R == keyCode.getValue();
    final boolean showUses = InputConstants.KEY_U == keyCode.getValue();
    if (showRecipe || showUses) {
      RecipeIngredientRole mode = showRecipe ? RecipeIngredientRole.OUTPUT : RecipeIngredientRole.INPUT;
      var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(mode, VanillaTypes.ITEM_STACK, stackUnderMouse);
      jeiRuntime.getRecipesGui().show(focus);
    }
  }
}
