package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.network.RecipeMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;

public class RequestRecipeTransferHandler<C extends Container> implements IRecipeTransferHandler<C> {

  private Class<C> clazz;

  public RequestRecipeTransferHandler(Class<C> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Class<C> getContainerClass() {
    return clazz;
  }

  public static CompoundNBT recipeToTag(Container container, IRecipeLayout recipeLayout) {
    CompoundNBT nbt = new CompoundNBT();
    Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs = recipeLayout.getItemStacks().getGuiIngredients();
    for (Slot slot : container.inventorySlots) {
      if (slot.inventory instanceof net.minecraft.inventory.CraftingInventory) {
        //for some reason it was looping like this  (int j = 1; j < 10; j++)
        IGuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex() + 1);
        if (ingredient == null) {
          continue;
        }
        List<ItemStack> possibleItems = ingredient.getAllIngredients();
        if (possibleItems == null) {
          continue;
        }
        ListNBT invList = new ListNBT();
        for (int i = 0; i < possibleItems.size(); i++) {
          if (i >= 5) {
            break; // Max 5 possible items to avoid reaching max network packet size
          }
          ItemStack itemStack = possibleItems.get(i);
          if (!itemStack.isEmpty()) {
            CompoundNBT stackTag = new CompoundNBT();
            itemStack.write(stackTag);
            invList.add(stackTag);
          }
        }
        nbt.put("s" + (slot.getSlotIndex()), invList);
      }
    }
    return nbt;
  }

  @Override
  public IRecipeTransferError transferRecipe(C c, IRecipeLayout iRecipeLayout, PlayerEntity playerEntity,
      boolean maxTransfer, boolean doTransfer) {
    if (doTransfer) {
      CompoundNBT nbt = RequestRecipeTransferHandler.recipeToTag(c, iRecipeLayout);
      PacketRegistry.INSTANCE.sendToServer(new RecipeMessage(nbt));
    }
    return null;
  }
}
