package com.lothrazar.storagenetwork.api.util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/** by Pahimar */
public class NBTHelper {

  private static boolean hasTag(CompoundNBT nbt, String keyName) {
    return nbt != null &&
        nbt.contains(keyName);
  }

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

  // list
  //  public static NBTTagList getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
  //    initNBTTagCompound(stack);
  //    return hasTag(stack.getTagCompound(), tag) ? stack.getTagCompound().getTagList(tag, objtype) : nullifyOnFail ? null : new NBTTagList();
  //  }
  //
  //  public static void setList(ItemStack stack, String tag, NBTTagList list) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setTag(tag, list);
  //  }
  //
  //  public static NBTTagList createList(NBTBase... list) {
  //    NBTTagList tagList = new NBTTagList();
  //    for (NBTBase b : list) {
  //      tagList.appendTag(b);
  //    }
  //    return tagList;
  //  }
  //
  //  public static NBTTagList createList(List<NBTBase> list) {
  //    NBTTagList tagList = new NBTTagList();
  //    for (NBTBase b : list) {
  //      tagList.appendTag(b);
  //    }
  //    return tagList;
  //  }

  // String
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

  //  // boolean
  //  public static boolean getBoolean(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setBoolean(stack, keyName, false);
  //    }
  //    return stack.getTagCompound().getBoolean(keyName);
  //  }
  //
  public static void setBoolean(ItemStack stack, String keyName, boolean keyValue) {
    initNBTTagCompound(stack);
    stack.getTag().putBoolean(keyName, keyValue);
  }
  //
  //  // byte
  //  public static byte getByte(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setByte(stack, keyName, (byte) 0);
  //    }
  //    return stack.getTagCompound().getByte(keyName);
  //  }
  //
  //  private static void setByte(ItemStack stack, String keyName, byte keyValue) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setByte(keyName, keyValue);
  //  }
  //
  //  // short
  //  public static short getShort(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setShort(stack, keyName, (short) 0);
  //    }
  //    return stack.getTagCompound().getShort(keyName);
  //  }
  //
  //  private static void setShort(ItemStack stack, String keyName, short keyValue) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setShort(keyName, keyValue);
  //  }
  //
  //  // int
  //  public static int getInteger(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setInteger(stack, keyName, 0);
  //    }
  //    return stack.getTagCompound().getInteger(keyName);
  //  }
  //
  //  public static void setInteger(ItemStack stack, String keyName, int keyValue) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setInteger(keyName, keyValue);
  //  }
  //
  //  // long
  //  public static long getLong(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setLong(stack, keyName, 0);
  //    }
  //    return stack.getTagCompound().getLong(keyName);
  //  }
  //
  //  private static void setLong(ItemStack stack, String keyName, long keyValue) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setLong(keyName, keyValue);
  //  }
  //
  //  // float
  //  public static float getFloat(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setFloat(stack, keyName, 0);
  //    }
  //    return stack.getTagCompound().getFloat(keyName);
  //  }
  //
  //  private static void setFloat(ItemStack stack, String keyName, float keyValue) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setFloat(keyName, keyValue);
  //  }
  //
  //  // double
  //  private static double getDouble(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setDouble(stack, keyName, 0);
  //    }
  //    return stack.getTagCompound().getDouble(keyName);
  //  }
  //
  //  private static void setDouble(ItemStack stack, String keyName, double keyValue) {
  //    initNBTTagCompound(stack);
  //    stack.getTagCompound().setDouble(keyName, keyValue);
  //  }
  //
  //  // itemstack
  //  public static ItemStack getItemStack(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      setItemStack(stack, keyName, null);
  //    }
  //    NBTTagCompound res = (NBTTagCompound) stack.getTagCompound().getTag(keyName);
  //    return new ItemStack(res);
  //  }
  //
  //  public static void setItemStack(ItemStack stack, String keyName, ItemStack keyValue) {
  //    initNBTTagCompound(stack);
  //    NBTTagCompound res = new NBTTagCompound();
  //    if (keyValue != null) {
  //      keyValue.writeToNBT(res);
  //    }
  //    stack.getTagCompound().setTag(keyName, res);
  //  }
  //
  //  // enum
  //  public static <E extends Enum<E>> E getEnum(ItemStack stack, String keyName, Class<E> clazz) {
  //    initNBTTagCompound(stack);
  //    if (!stack.getTagCompound().hasKey(keyName)) {
  //      return null;
  //    }
  //    String s = stack.getTagCompound().getString(keyName);
  //    for (E e : clazz.getEnumConstants()) {
  //      if (e.toString().equals(s)) {
  //        return e;
  //      }
  //    }
  //    return null;
  //  }
  //
  //  public static <E extends Enum<E>> void setEnum(ItemStack stack, String keyName, E keyValue) {
  //    initNBTTagCompound(stack);
  //    if (keyValue != null) {
  //      stack.getTagCompound().setString(keyName, keyValue.toString());
  //    }
  //  }
  //
  //  // Stringlist
  //  public static List<String> getStringList(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    ArrayList<String> lis = new ArrayList<>();
  //    int size = getInteger(stack, keyName + "Size");
  //    for (int i = 0; i < size; i++) {
  //      lis.add(getString(stack, keyName + ":" + i));
  //    }
  //    return lis;
  //  }
  //
  //  public static void setStringList(ItemStack stack, String keyName, List<String> keyValue) {
  //    initNBTTagCompound(stack);
  //    if (keyValue != null) {
  //      setInteger(stack, keyName + "Size", keyValue.size());
  //      for (int i = 0; i < keyValue.size(); i++) {
  //        String s = keyValue.get(i);
  //        if (s != null) {
  //          setString(stack, keyName + ":" + i, s);
  //        }
  //      }
  //    }
  //  }
  //
  //  // Booleanlist
  //  public static List<Boolean> getBooleanList(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    ArrayList<Boolean> lis = new ArrayList<>();
  //    int size = getInteger(stack, keyName + "Size");
  //    for (int i = 0; i < size; i++) {
  //      lis.add(getBoolean(stack, keyName + ":" + i));
  //    }
  //    return lis;
  //  }
  //
  //  public static void setBooleanList(ItemStack stack, String keyName, List<Boolean> keyValue) {
  //    initNBTTagCompound(stack);
  //    if (keyValue != null) {
  //      setInteger(stack, keyName + "Size", keyValue.size());
  //      for (int i = 0; i < keyValue.size(); i++) {
  //        Boolean s = keyValue.get(i);
  //        if (s != null) {
  //          setBoolean(stack, keyName + ":" + i, s);
  //        }
  //      }
  //    }
  //  }
  //
  //  // Integerlist
  //  public static List<Integer> getIntegerList(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    ArrayList<Integer> lis = new ArrayList<>();
  //    int size = getInteger(stack, keyName + "Size");
  //    for (int i = 0; i < size; i++) {
  //      lis.add(getInteger(stack, keyName + ":" + i));
  //    }
  //    return lis;
  //  }
  //
  //  public static void setIntegerList(ItemStack stack, String keyName, List<Integer> keyValue) {
  //    initNBTTagCompound(stack);
  //    if (keyValue != null) {
  //      setInteger(stack, keyName + "Size", keyValue.size());
  //      for (int i = 0; i < keyValue.size(); i++) {
  //        Integer s = keyValue.get(i);
  //        if (s != null) {
  //          setInteger(stack, keyName + ":" + i, s);
  //        }
  //      }
  //    }
  //  }
  //
  //  // Doublelist
  //  public static List<Double> getDoubleList(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    ArrayList<Double> lis = new ArrayList<>();
  //    int size = getInteger(stack, keyName + "Size");
  //    for (int i = 0; i < size; i++) {
  //      lis.add(getDouble(stack, keyName + ":" + i));
  //    }
  //    return lis;
  //  }
  //
  //  public static void setDoubleList(ItemStack stack, String keyName, List<Double> keyValue) {
  //    initNBTTagCompound(stack);
  //    if (keyValue != null) {
  //      setInteger(stack, keyName + "Size", keyValue.size());
  //      for (int i = 0; i < keyValue.size(); i++) {
  //        Double s = keyValue.get(i);
  //        if (s != null) {
  //          setDouble(stack, keyName + ":" + i, s);
  //        }
  //      }
  //    }
  //  }
  //
  //  // Stacklist
  //  public static List<ItemStack> getItemStackList(ItemStack stack, String keyName) {
  //    initNBTTagCompound(stack);
  //    ArrayList<ItemStack> lis = new ArrayList<>();
  //    int size = getInteger(stack, keyName + "Size");
  //    for (int i = 0; i < size; i++) {
  //      lis.add(getItemStack(stack, keyName + ":" + i));
  //    }
  //    return lis;
  //  }
  //
  //  public static void setItemStackList(ItemStack stack, String keyName, List<ItemStack> keyValue) {
  //    initNBTTagCompound(stack);
  //    if (keyValue != null) {
  //      setInteger(stack, keyName + "Size", keyValue.size());
  //      for (int i = 0; i < keyValue.size(); i++) {
  //        ItemStack s = keyValue.get(i);
  //        if (s != null) {
  //          setItemStack(stack, keyName + ":" + i, s);
  //        }
  //      }
  //    }
  //  }
}
