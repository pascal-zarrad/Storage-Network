package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.request.ContainerNetworkTable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(StorageNetwork.MODID, "jei");
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registration) {}

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {}

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {}

  @Override
  public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {}

  @Override
  public void registerRecipes(IRecipeRegistration registration) {}

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addUniversalRecipeTransferHandler(new RequestRecipeTransferHandler<>(ContainerNetworkTable.class));
    //    registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RequestRecipeTransferHandlerRemote<>(ContainerNetworkRemote.class));
    //  @Override
    //  public void register(IModRegistry registry) {
    //    registry.addRecipeCatalyst(new ItemStack(SsnRegistry.request), VanillaRecipeCategoryUid.CRAFTING);
    //    if (GuiHandler.FB_LOADED) {
    //      registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RequestRecipeTransferHandler<>(ContainerFastRequest.class));
    //      registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RequestRecipeTransferHandlerRemote<>(ContainerFastRemote.class));
    //      registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RequestRecipeTransferHandler<>(ContainerFastRequest.Client.class));
    //      registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new RequestRecipeTransferHandlerRemote<>(ContainerFastRemote.Client.class));
    //    }
    //  }
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {}

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {}

  @Override
  public void registerAdvanced(IAdvancedRegistration registration) {}

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {}
}
