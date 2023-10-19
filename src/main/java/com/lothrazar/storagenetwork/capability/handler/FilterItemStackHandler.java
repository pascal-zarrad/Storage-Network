package com.lothrazar.storagenetwork.capability.handler;

import java.util.List;
import java.util.stream.Collectors;

import com.lothrazar.storagenetwork.api.IItemStackMatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilterItemStackHandler extends ItemStackHandlerEx {

    public static final int FILTER_SIZE = 18;
    public boolean tags = false;
    public boolean nbt = false;
    public boolean isAllowList = true;

    private List<IItemStackMatcher> stackMatchers = null;

    public FilterItemStackHandler() {
        super(FILTER_SIZE);
    }

    public void setMatchOreDict(boolean ores) {
        this.tags = ores;
    }

    public void setMatchNbt(boolean nbt) {
        this.nbt = nbt;
    }

    public void setIsAllowlist(boolean is) {
        isAllowList = is;
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return 1;
    }

    public List<IItemStackMatcher> getStackMatchers() {
        if (stackMatchers == null) {
            this.stackMatchers = getStacks()
                    .stream()
                    .map(stack -> new ItemStackMatcher(stack, tags, nbt))
                    .collect(Collectors.toList());
        }

        return this.stackMatchers;
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.resetStackMatchers();

        super.onContentsChanged(slot);
    }

    public void resetStackMatchers() {
        System.out.println("RESET STACK MATCHERS");

        this.stackMatchers = null;
    }

    public void clear() {
        for (int slot = 0; slot < getSlots(); slot++) {
            setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    public boolean exactStackAlreadyInList(ItemStack stack) {
        // Should we want not to use the configured rules for nbt, oredict and meta, we can use this line instead, which really matches for the exact stack:
        //return getStacks().stream().map(filteredStack -> new ItemStackMatcher(filteredStack, true, false, true)).anyMatch(matcher -> matcher.match(stack));
        for (IItemStackMatcher matcher : getStackMatchers()) {
            if (matcher.match(stack)) {
                return true;
            }
        }

        return false;
    }

    public IItemStackMatcher getFirstMatcher(ItemStack stack) {
        for (IItemStackMatcher m : getStackMatchers()) {
            if (m.match(stack)) {
                return m;
            }
        }
        return null;
    }

    public boolean isStackFiltered(ItemStack stack) {
        if (isAllowList) {
            for (IItemStackMatcher matcher : getStackMatchers()) {
                if (matcher.match(stack)) {
                    return false;
                }
            }

            return true;
        }

        for (IItemStackMatcher matcher : getStackMatchers()) {
            if (matcher.match(stack)) {
                return true;
            }
        }

        return false;
    }

    public boolean allAreEmpty() {
        for (int slot = 0; slot < getSlots(); slot++) {
            if (!this.getStackInSlot(slot).isEmpty()) {
                //found something not empty. so allAreEmpty is false
                return false;
            }
        }
        //none found that were !empty. so allempty true
        return true;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        CompoundTag rulesTag = nbt.getCompound("rules");
        tags = rulesTag.getBoolean("tags");
        this.nbt = rulesTag.getBoolean("nbt");
        isAllowList = rulesTag.getBoolean("whitelist");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag result = super.serializeNBT();
        CompoundTag rulesTag = new CompoundTag();
        rulesTag.putBoolean("tags", tags);
        rulesTag.putBoolean("nbt", nbt);
        rulesTag.putBoolean("whitelist", isAllowList);
        result.put("rules", rulesTag);
        return result;
    }

    public int getStackCount(ItemStack stackCurrent) {
        int s = 0;
        for (IItemStackMatcher m : getStackMatchers()) {
            if (ItemHandlerHelper.canItemStacksStack(stackCurrent, m.getStack())) {
                return s += m.getStack().getCount();
            }
        }
        return s;
    }
}
