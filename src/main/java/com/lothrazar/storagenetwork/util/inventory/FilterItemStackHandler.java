package com.lothrazar.storagenetwork.util.inventory;
import com.lothrazar.storagenetwork.api.data.IItemStackMatcher;
import com.lothrazar.storagenetwork.data.ItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class FilterItemStackHandler extends ItemStackHandlerEx {

  public static final int FILTER_SIZE = 18;
  public boolean tags = false;
  public boolean nbt = false;
  public boolean isWhitelist = true;

  public FilterItemStackHandler() {
    super(FILTER_SIZE);
  }

  public void setMatchOreDict(boolean ores) {
    this.tags = ores;
  }


  public void setMatchNbt(boolean nbt) {
    this.nbt = nbt;
  }

  public void setIsWhitelist(boolean whitelist) {
    isWhitelist = whitelist;
  }

  @Override
  protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
    return 1;
  }

  public List<IItemStackMatcher> getStackMatchers() {
    return getStacks().stream().map(stack -> new ItemStackMatcher(stack,  tags, nbt)).collect(Collectors.toList());
  }

  public void clear() {
    for (int slot = 0; slot < getSlots(); slot++) {
      setStackInSlot(slot, ItemStack.EMPTY);
    }
  }

  public boolean exactStackAlreadyInList(ItemStack stack) {
    // Should we want not to use the configured rules for nbt, oredict and meta, we can use this line instead, which really matches for the exact stack:
    //return getStacks().stream().map(filteredStack -> new ItemStackMatcher(filteredStack, true, false, true)).anyMatch(matcher -> matcher.match(stack));
    return getStackMatchers().stream().anyMatch(matcher -> matcher.match(stack));
  }

  public boolean isStackFiltered(ItemStack stack) {
    if (isWhitelist) {
      return getStackMatchers().stream().noneMatch(matcher -> matcher.match(stack));
    }
    return getStackMatchers().stream().anyMatch(matcher -> matcher.match(stack));
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    super.deserializeNBT(nbt);
    CompoundNBT rulesTag = nbt.getCompound("rules");
    tags = rulesTag.getBoolean("tags");
    this.nbt = rulesTag.getBoolean("nbt");
    isWhitelist = rulesTag.getBoolean("whitelist");
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT result = super.serializeNBT();
    CompoundNBT rulesTag = new CompoundNBT();
    rulesTag.putBoolean("tags", tags);
    rulesTag.putBoolean("nbt", nbt);
    rulesTag.putBoolean("whitelist", isWhitelist);
    result.put("rules", rulesTag);
    return result;
  }
}
