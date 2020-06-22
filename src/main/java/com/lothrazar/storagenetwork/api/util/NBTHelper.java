package com.lothrazar.storagenetwork.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/** by Pahimar */
public class NBTHelper {

  public static void removeTag(CompoundNBT nbt, String keyName) {
    if (nbt != null) {
      nbt.remove(keyName);
    }
  }

  private static void initNBTTagCompound(ItemStack stack) {
    if (stack.getTag() == null) {
      stack.setTag(new CompoundNBT());
    }
  }

  public static String getString(ItemStack stack, String keyName) {
    initNBTTagCompound(stack);
    if (!stack.getTag().contains(keyName)) {
      return null;
    }
    return stack.getTag().getString(keyName);
  }

  public static void setString(ItemStack stack, String keyName, String keyValue) {
    initNBTTagCompound(stack);
    if (keyValue != null) {
      stack.getTag().putString(keyName, keyValue);
    }
  }

  public static void setBoolean(ItemStack stack, String keyName, boolean keyValue) {
    initNBTTagCompound(stack);
    stack.getTag().putBoolean(keyName, keyValue);
  }
}
