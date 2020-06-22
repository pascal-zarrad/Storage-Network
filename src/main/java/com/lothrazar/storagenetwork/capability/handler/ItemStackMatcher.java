package com.lothrazar.storagenetwork.capability.handler;

import javax.annotation.Nonnull;
import com.lothrazar.storagenetwork.api.IItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemStackMatcher implements IItemStackMatcher {

  private ItemStack stack;
  private boolean ore, nbt;

  public ItemStackMatcher(ItemStack stack) {
    //so glad meta is ded
    // stack != null ? stack.getItemDamage() != OreDictionary.WILDCARD_VALUE : true
    this(stack, false, false);
  }

  public ItemStackMatcher(ItemStack stack, boolean ore, boolean nbt) {
    this.stack = stack;
    this.ore = ore;
    this.nbt = nbt;
  }

  private ItemStackMatcher() {}

  public void readFromNBT(CompoundNBT compound) {
    CompoundNBT c = (CompoundNBT) compound.get("stack");
    stack = ItemStack.read(c);
    //    meta = compound.getBoolean("meta");
    ore = compound.getBoolean("ore");
    nbt = compound.getBoolean("nbt");
  }

  public CompoundNBT writeToNBT(CompoundNBT compound) {
    CompoundNBT c = new CompoundNBT();
    stack.write(c);
    compound.put("stack", c);
    //    compound.putBoolean("meta", meta);
    compound.putBoolean("ore", ore);
    compound.putBoolean("nbt", nbt);
    return c;
  }

  @Override
  public String toString() {
    return "ItemStackMatcher [stack=" + stack + ", ore=" + ore + ", nbt=" + nbt + "]";
  }

  @Override
  public ItemStack getStack() {
    return stack;
  }

  public void setStack(@Nonnull ItemStack stack) {
    this.stack = stack;
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
      //      if (nbt) {
      //        StorageNetwork.LOGGER.info("Tags are not equal ");
      //        StorageNetwork.LOGGER.info(stack.getTag());
      //        StorageNetwork.LOGGER.info(stackIn.getTag());
      //      }
      return false;
    }
    return stackIn.getItem() == stack.getItem();
  }
}