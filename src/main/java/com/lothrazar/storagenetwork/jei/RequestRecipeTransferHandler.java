package com.lothrazar.storagenetwork.jei;

import com.lothrazar.storagenetwork.network.RecipeMessage;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;

public class RequestRecipeTransferHandler<C extends AbstractContainerMenu> implements IRecipeTransferHandler<C> {

  private Class<C> clazz;

  public RequestRecipeTransferHandler(Class<C> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Class<C> getContainerClass() {
    return clazz;
  }

  public static CompoundTag recipeToTag(AbstractContainerMenu container, IRecipeLayout recipeLayout) {
    CompoundTag nbt = new CompoundTag();
    Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs = recipeLayout.getItemStacks().getGuiIngredients();
    for (Slot slot : container.slots) {
      if (slot.container instanceof net.minecraft.world.inventory.CraftingContainer) {
        //for some reason it was looping like this  (int j = 1; j < 10; j++)
        IGuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex() + 1);
        if (ingredient == null) {
          continue;
        }
        List<ItemStack> possibleItems = ingredient.getAllIngredients();
        if (possibleItems == null || possibleItems.isEmpty()) {
          continue;
        }
        ListTag invList = new ListTag();
        for (int i = 0; i < possibleItems.size(); i++) {
          if (i >= ConfigRegistry.RECIPEMAXTAGS.get()) {
            break;
          }
          ItemStack itemStack = possibleItems.get(i);
          if (!itemStack.isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            itemStack.save(stackTag);
            invList.add(stackTag);
          }
        }
        nbt.put("s" + (slot.getSlotIndex()), invList);
      }
    }
    return nbt;
  }

  @Override
  public IRecipeTransferError transferRecipe(C c, IRecipeLayout iRecipeLayout, Player playerEntity,
      boolean maxTransfer, boolean doTransfer) {
    if (doTransfer) {
      CompoundTag nbt = RequestRecipeTransferHandler.recipeToTag(c, iRecipeLayout);
      PacketRegistry.INSTANCE.sendToServer(new RecipeMessage(nbt));
    }
    return null;
  }
}
