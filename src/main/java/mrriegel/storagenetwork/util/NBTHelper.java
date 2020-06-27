package mrriegel.storagenetwork.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** by Pahimar */
public class NBTHelper {

  public static boolean hasTag(NBTTagCompound nbt, String keyName) {
    return nbt != null && nbt.hasKey(keyName);
  }

  private static void initNBTTagCompound(ItemStack stack) {
    if (stack.getTagCompound() == null) {
      stack.setTagCompound(new NBTTagCompound());
    }
  }

  // String
  public static String getString(ItemStack stack, String keyName) {
    initNBTTagCompound(stack);
    if (!stack.getTagCompound().hasKey(keyName)) {
      return null;
    }
    return stack.getTagCompound().getString(keyName);
  }

  public static void setString(ItemStack stack, String keyName, String keyValue) {
    initNBTTagCompound(stack);
    if (keyValue != null)
      stack.getTagCompound().setString(keyName, keyValue);
  }

  // boolean
  public static boolean getBoolean(ItemStack stack, String keyName) {
    initNBTTagCompound(stack);
    if (!stack.getTagCompound().hasKey(keyName)) {
      setBoolean(stack, keyName, false);
    }
    return stack.getTagCompound().getBoolean(keyName);
  }

  public static void setBoolean(ItemStack stack, String keyName, boolean keyValue) {
    initNBTTagCompound(stack);
    stack.getTagCompound().setBoolean(keyName, keyValue);
  }

  // int
  public static int getInteger(ItemStack stack, String keyName) {
    initNBTTagCompound(stack);
    if (!stack.getTagCompound().hasKey(keyName)) {
      setInteger(stack, keyName, 0);
    }
    return stack.getTagCompound().getInteger(keyName);
  }

  public static void setInteger(ItemStack stack, String keyName, int keyValue) {
    initNBTTagCompound(stack);
    stack.getTagCompound().setInteger(keyName, keyValue);
  }

  // itemstack
  public static ItemStack getItemStack(ItemStack stack, String keyName) {
    initNBTTagCompound(stack);
    if (!stack.getTagCompound().hasKey(keyName)) {
      setItemStack(stack, keyName, null);
    }
    NBTTagCompound res = (NBTTagCompound) stack.getTagCompound().getTag(keyName);
    return new ItemStack(res);
  }

  public static void setItemStack(ItemStack stack, String keyName, ItemStack keyValue) {
    initNBTTagCompound(stack);
    NBTTagCompound res = new NBTTagCompound();
    if (keyValue != null) {
      keyValue.writeToNBT(res);
    }
    stack.getTagCompound().setTag(keyName, res);
  }
}
