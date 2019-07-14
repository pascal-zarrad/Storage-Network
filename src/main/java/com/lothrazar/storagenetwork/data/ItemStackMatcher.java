package com.lothrazar.storagenetwork.data;
import com.lothrazar.storagenetwork.api.data.IItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class ItemStackMatcher implements IItemStackMatcher {

  private ItemStack stack;
  private boolean meta, ore, nbt;

  public ItemStackMatcher(ItemStack stack) {
    //so glad meta is ded
    // stack != null ? stack.getItemDamage() != OreDictionary.WILDCARD_VALUE : true
    this(stack, false, false, false);
  }

  public ItemStackMatcher(ItemStack stack, boolean meta, boolean ore, boolean nbt) {
    this.stack = stack;
    this.meta = meta;
    this.ore = ore;
    this.nbt = nbt;
  }

  private ItemStackMatcher() {}

  public void readFromNBT(CompoundNBT compound) {
    CompoundNBT c = (CompoundNBT) compound.get("stack");
    stack = ItemStack.read(c);
    meta = compound.getBoolean("meta");
    ore = compound.getBoolean("ore");
    nbt = compound.getBoolean("nbt");
  }

  public CompoundNBT writeToNBT(CompoundNBT compound) {
    CompoundNBT c = new CompoundNBT();
    stack.write(c);
    compound.put("stack", c);
    compound.putBoolean("meta", meta);
    compound.putBoolean("ore", ore);
    compound.putBoolean("nbt", nbt);
    return c;
  }

  @Override
  public String toString() {
    return "ItemStackMatcher [stack=" + stack + ", meta=" + meta + ", ore=" + ore + ", nbt=" + nbt + "]";
  }

  @Override public ItemStack getStack() {
    return stack;
  }

  public void setStack(@Nonnull ItemStack stack) {
    this.stack = stack;
  }

  public boolean isMeta() {
    return meta;
  }

  public void setMeta(boolean meta) {
    this.meta = meta;
  }

  public boolean isOre() {
    return ore;
  }

  public void setOre(boolean ore) {
    this.ore = ore;
  }

  public boolean isNbt() {
    return nbt;
  }

  public void setNbt(boolean nbt) {
    this.nbt = nbt;
  }

  public static ItemStackMatcher loadFilterItemFromNBT(CompoundNBT nbt) {
    ItemStackMatcher fil = new ItemStackMatcher();
    fil.readFromNBT(nbt);
    return fil.getStack() != null && fil.getStack().getItem() != null ? fil : null;
  }

  @Override
  public boolean match(@Nonnull ItemStack stackIn) {
    if (stackIn.isEmpty()) {
      return false;
    }
    // TODO: TAGS
    //    if (ore && UtilTileEntity.equalOreDict(stackIn, stack)) {
    //      return true;
    //    }
    if (nbt && !ItemStack.areItemStackTagsEqual(stack, stackIn)) {
      return false;
    }
    //meta ded
    //    if (meta && stackIn.getItemDamage() != stack.getItemDamage()) {
    //      return false;
    //    }
    return stackIn.getItem() == stack.getItem();
  }
}