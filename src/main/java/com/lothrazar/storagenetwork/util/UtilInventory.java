package com.lothrazar.storagenetwork.util;

import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.curios.api.CuriosApi;

public class UtilInventory {

  /**
   * First check curios. Then ender chest, Then player inventory. Then left/right hands
   * 
   * @param player
   * @param remote
   * @return
   */
  public static Triple<String, Integer, ItemStack> getCurioRemote(Player player, Item remote) {
    Triple<String, Integer, ItemStack> stackFound = Triple.of("", -1, ItemStack.EMPTY);
    if (ModList.get().isLoaded("curios")) {
      //check curios slots
      final ImmutableTriple<String, Integer, ItemStack> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(remote, player).orElse(null);
      if (equipped != null && isRemote(equipped.right, remote)) {
        //success: try to insert items to network thru this remote 
        return Triple.of("curios", equipped.middle, equipped.right);
      }
    }
    //not curios, check others
    if (remote != SsnRegistry.COLLECTOR_REMOTE) {
      for (int i = 0; i < player.getEnderChestInventory().getContainerSize(); i++) {
        ItemStack temp = player.getEnderChestInventory().getItem(i);
        if (isRemote(temp, remote)) {
          return Triple.of("ender", i, temp);
        }
      }
    }
    for (int i = 0; i < player.inventory.getContainerSize(); i++) {
      ItemStack temp = player.inventory.getItem(i);
      if (isRemote(temp, remote)) {
        return Triple.of("player", i, temp);
      }
    }
    //default
    if (isRemote(player.getOffhandItem(), remote)) {
      return Triple.of("offhand", -1, player.getOffhandItem());
    }
    if (isRemote(player.getMainHandItem(), remote)) {
      return Triple.of("hand", -1, player.getMainHandItem());
    }
    return stackFound;
  }

  private static boolean isRemote(ItemStack temp, Item remote) {
    return temp.getItem() == remote;
  }

  public static String formatLargeNumber(int size) {
    if (size < Math.pow(10, 3)) {
      return size + "";
    }
    else if (size < Math.pow(10, 6)) {
      //      float r = (size) / 1000.0F;
      int rounded = Math.round(size / 1000.0F); //so 1600 => 1.6 and then rounded to become 2.
      return rounded + "K";
    }
    else if (size < Math.pow(10, 9)) {
      int rounded = Math.round(size / (float) Math.pow(10, 6));
      return rounded + "M";
    }
    else if (size < Math.pow(10, 12)) {
      int rounded = Math.round(size / (float) Math.pow(10, 9));
      return rounded + "B";
    }
    return size + "";
  }

  public static int containsAtLeastHowManyNeeded(IItemHandler inv, ItemStack stack, int minimumCount) {
    int found = 0;
    for (int i = 0; i < inv.getSlots(); i++) {
      if (ItemHandlerHelper.canItemStacksStack(inv.getStackInSlot(i), stack)) {
        found += inv.getStackInSlot(i).getCount();
      }
    }
    //do you have all 4? or do you need 2 still
    if (found >= minimumCount) {
      return 0;
    }
    return minimumCount - found;
  }

  public static ItemStack extractItem(IItemHandler inv, ItemStackMatcher fil, int num, boolean simulate) {
    if (inv == null || fil == null) {
      return ItemStack.EMPTY;
    }
    int extracted = 0;
    for (int i = 0; i < inv.getSlots(); i++) {
      ItemStack slot = inv.getStackInSlot(i);
      if (fil.match(slot)) {
        ItemStack ex = inv.extractItem(i, 1, simulate);
        if (!ex.isEmpty()) {
          extracted++;
          if (extracted == num) {
            return ItemHandlerHelper.copyStackWithSize(slot, num);
          }
          else {
            i--;
          }
        }
      }
    }
    return ItemStack.EMPTY;
  }
}
