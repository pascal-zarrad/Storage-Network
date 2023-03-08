package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.block.request.ContainerNetworkCraftingTable;
import com.lothrazar.storagenetwork.item.remote.ContainerNetworkCraftingRemote;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(StorageNetworkMod.MODID, "jei");
  }

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addUniversalRecipeTransferHandler(new RequestRecipeTransferHandler<>(ContainerNetworkCraftingTable.class));
    registration.addUniversalRecipeTransferHandler(new RequestRecipeTransferHandler<>(ContainerNetworkCraftingRemote.class));
  }
}
