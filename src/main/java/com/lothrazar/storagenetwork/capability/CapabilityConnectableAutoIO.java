package com.lothrazar.storagenetwork.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumStorageDirection;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableItemAutoIO;
import com.lothrazar.storagenetwork.api.IItemStackMatcher;
import com.lothrazar.storagenetwork.api.OpCompareType;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.FilterItemStackHandler;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.capability.handler.UpgradesItemStackHandler;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CapabilityConnectableAutoIO implements INBTSerializable<CompoundTag>, IConnectableItemAutoIO {

  public static final int DEFAULT_ITEMS_PER = 4;
  public static final int IO_DEFAULT_SPEED = 30; // TODO CONFIG

  public static class Factory implements Callable<IConnectableItemAutoIO> {

    @Override
    public IConnectableItemAutoIO call() throws Exception {
      return new CapabilityConnectableAutoIO(EnumStorageDirection.IN);
    }
  }

  public final IConnectable connectable;
  public EnumStorageDirection direction;
  public final UpgradesItemStackHandler upgrades = new UpgradesItemStackHandler();
  private final FilterItemStackHandler filters = new FilterItemStackHandler();
  private int priority = 0;
  private Direction inventoryFace;
  private boolean needsRedstone = false;
  public ItemStack operationStack = ItemStack.EMPTY;
  public int operationLimit = 0;
  public int operationType = OpCompareType.LESS.ordinal();

  CapabilityConnectableAutoIO(EnumStorageDirection direction) {
    connectable = new CapabilityConnectable();
    this.direction = direction;
  }

  @Override
  public void toggleNeedsRedstone() {
    needsRedstone = !needsRedstone;
  }

  @Override
  public boolean needsRedstone() {
    return this.needsRedstone;
  }

  @Override
  public void needsRedstone(boolean in) {
    this.needsRedstone = in;
  }

  public FilterItemStackHandler getFilter() {
    return filters;
  }

  //TODO: share with ConnectableLink  @Override
  public List<ItemStack> getStoredStacks(boolean isFiltered) {
    if (inventoryFace == null) {
      return Collections.emptyList();
    }
    DimPos inventoryPos = connectable.getPos().offset(inventoryFace);
    // Test whether the connected block has the IItemHandler capability
    IItemHandler itemHandler = inventoryPos.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryFace.getOpposite());
    if (itemHandler == null) {
      return Collections.emptyList();
    }
    // If it does, iterate its stacks, filter them and add them to the result list
    List<ItemStack> result = new ArrayList<>();
    for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
      ItemStack stack = itemHandler.getStackInSlot(slot);
      if (stack == null || stack.isEmpty()) {
        continue;
      }
      if (isFiltered && filters.isStackFiltered(stack)) {
        continue;
      }
      result.add(stack.copy());
    }
    return result;
  }

  //TODO: share with ConnectableLink
  public void setPriority(int value) {
    this.priority = value;
  }

  public void setFilter(int value, ItemStack stack) {
    filters.setStackInSlot(value, stack);
    filters.getStacks().set(value, stack);
  }

  public CapabilityConnectableAutoIO(BlockEntity tile, EnumStorageDirection direction) {
    connectable = tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null).orElse(null);
    this.direction = direction;
    // Set some defaults
    if (direction == EnumStorageDirection.OUT) {
      filters.setIsAllowlist(true);
    }
    else {
      filters.setIsAllowlist(false);
    }
  }

  public void setInventoryFace(Direction inventoryFace) {
    this.inventoryFace = inventoryFace;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag result = new CompoundTag();
    result.put("upgrades", this.upgrades.serializeNBT());
    result.put("filters", this.filters.serializeNBT());
    result.putInt("prio", priority);
    if (inventoryFace != null) {
      result.putString("inventoryFace", inventoryFace.toString());
    }
    result.putBoolean("needsRedstone", this.needsRedstone());
    CompoundTag operation = new CompoundTag();
    operation.put("stack", operationStack.serializeNBT());
    operation.putInt("operationType", operationType);
    operation.putInt("limit", operationLimit);
    result.put("operation", operation);
    return result;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    CompoundTag upgrades = nbt.getCompound("upgrades");
    if (upgrades != null) {
      this.upgrades.deserializeNBT(upgrades);
    }
    CompoundTag filters = nbt.getCompound("filters");
    if (filters != null) {
      this.filters.deserializeNBT(filters);
    }
    priority = nbt.getInt("prio");
    if (nbt.contains("inventoryFace")) {
      inventoryFace = Direction.byName(nbt.getString("inventoryFace"));
    }
    this.needsRedstone(nbt.getBoolean("needsRedstone"));
    CompoundTag operation = nbt.getCompound("operation");
    this.operationLimit = operation.getInt("limit");
    this.operationType = operation.getInt("operationType");
    if (operation.contains("stack")) {
      this.operationStack = ItemStack.of(operation.getCompound("stack"));
    }
    else {
      this.operationStack = ItemStack.EMPTY;
    }
  }

  @Override
  public EnumStorageDirection ioDirection() {
    return direction;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public ItemStack insertStack(ItemStack stack, boolean simulate) {
    // If this storage is configured to only import into the network, do not
    // insert into the storage, but abort immediately.
    if (direction == EnumStorageDirection.IN) {
      return stack;
    }
    if (inventoryFace == null) {
      return stack;
    }
    DimPos inventoryPos = connectable.getPos().offset(inventoryFace);
    // Test whether the connected block has the IItemHandler capability
    IItemHandler itemHandler = inventoryPos.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryFace.getOpposite());
    if (itemHandler == null) {
      return stack;
    }
    return ItemHandlerHelper.insertItemStacked(itemHandler, stack, simulate);
  }

  public List<ItemStack> getStacksForFilter() {
    if (inventoryFace == null) {
      return Collections.emptyList();
    }
    DimPos inventoryPos = connectable.getPos().offset(inventoryFace);
    // Test whether the connected block has the IItemHandler capability
    IItemHandler itemHandler = inventoryPos.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryFace.getOpposite());
    if (itemHandler == null) {
      return Collections.emptyList();
    }
    // If it does, iterate its stacks, filter them and add them to the result list
    List<ItemStack> result = new ArrayList<>();
    for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
      ItemStack stack = itemHandler.getStackInSlot(slot);
      if (stack == null || stack.isEmpty()) {
        continue;
      }
      if (filters.exactStackAlreadyInList(stack)) {
        continue;
      }
      result.add(stack.copy());
      // We can abort after we've found FILTER_SIZE stacks; we don't have more filter slots anyway
      if (result.size() >= FilterItemStackHandler.FILTER_SIZE) {
        return result;
      }
    }
    return result;
  }

  @Override
  public FilterItemStackHandler getFilters() {
    return filters;
  }

  @Override
  public IItemHandler getItemHandler() {
    if (inventoryFace == null || direction == EnumStorageDirection.OUT) {
      return null;
    }
    DimPos inventoryPos = connectable.getPos().offset(inventoryFace);
    return inventoryPos.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryFace.getOpposite());
  }

  @Deprecated
  @Override
  public ItemStack extractNextStack(final int amtToRequestIn, boolean simulate) {
    //op mode override
    int amtToRequest = amtToRequestIn;
    boolean operationMode = isOperationMode();
    // If this storage is configured to only export from the network, do not
    // extract from the storage, but abort immediately.
    if (direction == EnumStorageDirection.OUT) {
      return ItemStack.EMPTY;
    }
    if (inventoryFace == null) {
      return ItemStack.EMPTY;
    }
    DimPos inventoryPos = connectable.getPos().offset(inventoryFace);
    // Test whether the connected block has the IItemHandler capability
    IItemHandler itemHandler = inventoryPos.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryFace.getOpposite());
    if (itemHandler == null) {
      return ItemStack.EMPTY;
    }
    for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
      ItemStack stack = itemHandler.getStackInSlot(slot);
      if (stack == null || stack.isEmpty()) {
        continue;
      }
      // Ignore stacks that are filtered
      if (filters.isStackFiltered(stack)) {
        continue;
      }
      if (operationMode && filters.isAllowList) {
        IItemStackMatcher matcher = filters.getFirstMatcher(stack);
        //if filters are also in allow list mode
        //then get the filter matching stack, and get the count of that filter
        if (matcher != null && matcher.getStack().getCount() > 0) {
          amtToRequest = matcher.getStack().getCount(); // the 63 haha
        }
      }
      int extractSize = Math.min(amtToRequest, stack.getCount());
      return itemHandler.extractItem(slot, extractSize, simulate);
    }
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isStockMode() {
    return getUpgrades().hasUpgradesOfType(SsnRegistry.Items.STOCK_UPGRADE.get());
  }

  @Override
  public boolean isOperationMode() {
    return getUpgrades().hasUpgradesOfType(SsnRegistry.Items.OP_U.get());
  }

  @Override
  public int getTransferRate() {
    if (upgrades.hasUpgradesOfType(SsnRegistry.Items.SINGLE_UPGRADE.get())) {
      return 1; //override both others
    }
    return upgrades.hasUpgradesOfType(SsnRegistry.Items.STACK_UPGRADE.get()) ? 64 : DEFAULT_ITEMS_PER;
  }

  private boolean doesPassOperationFilterLimit(TileMain master) {
    if (upgrades.getUpgradesOfType(SsnRegistry.Items.OP_U.get()) < 1) {
      return true;
    }
    if (operationStack == null || operationStack.isEmpty()) {
      return true;
    }
    // TODO: Investigate whether the operation limiter should consider the filter toggles
    int countYourItemInNetwork = master.nw.getAmount(new ItemStackMatcher(operationStack, filters.tags, filters.nbt));
    switch (OpCompareType.get(operationType)) {
      case EQUAL:
        return countYourItemInNetwork == operationLimit;
      case GREATER:
        //true yes allowed to run if SLOT > textbox
        return countYourItemInNetwork > operationLimit;
      case LESS:
        //true yes allowed to run if SLOT < textbox
        return countYourItemInNetwork < operationLimit;
    }
    return false;
  }

  @Override
  public boolean runNow(DimPos connectablePos, TileMain main) {
    int speedUpgrades = upgrades.getUpgradesOfType(SsnRegistry.Items.SPEED_UPGRADE.get());
    int slowUpgrades = upgrades.getUpgradesOfType(SsnRegistry.Items.SLOW_UPGRADE.get());
    int speedRatio = IO_DEFAULT_SPEED; // no upgrades
    if (speedUpgrades > 0) {
      //so 1 speed upgrade is run every 30/2=15t, two is 30/3 ticks etc
      speedRatio = IO_DEFAULT_SPEED / (speedUpgrades + 1);
    }
    else if (slowUpgrades > 0) {
      //meaning IF one or more speed upgrades are present, then all slowness upgrades are IGNORED
      //so 1 Slow upgrade is run every 30*2=60t, two is 30*3=90 ticks 
      speedRatio = IO_DEFAULT_SPEED * (slowUpgrades + 1);
    }
    if (speedRatio < 1) {
      speedRatio = 1; // 0 wont happen but idk maybe
    }
    boolean cooldownOk = (connectablePos.getWorld().getGameTime() % speedRatio == 0);
    if (!cooldownOk) {
      return false;
    }
    //opt: dont check operation count if the cooldown is bad anyway
    boolean operationLimitOk = doesPassOperationFilterLimit(main);
    //    StorageNetwork.log("OP allowed to runNow = " + operationLimitOk);
    return operationLimitOk;
  }

  @Override
  public List<IItemStackMatcher> getAutoExportList() {
    return filters.getStackMatchers();
  }

  @Override
  public Direction facingInventory() {
    return inventoryFace;
  }

  public UpgradesItemStackHandler getUpgrades() {
    return upgrades;
  }

  public void extractFromSlot(int slot){
    
  }

}
