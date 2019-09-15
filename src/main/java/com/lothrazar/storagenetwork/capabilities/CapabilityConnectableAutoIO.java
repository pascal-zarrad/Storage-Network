package com.lothrazar.storagenetwork.capabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.capability.IConnectable;
import com.lothrazar.storagenetwork.api.capability.IConnectableItemAutoIO;
import com.lothrazar.storagenetwork.api.data.DimPos;
import com.lothrazar.storagenetwork.api.data.EnumStorageDirection;
import com.lothrazar.storagenetwork.api.data.IItemStackMatcher;
import com.lothrazar.storagenetwork.api.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.inventory.FilterItemStackHandler;
import com.lothrazar.storagenetwork.gui.inventory.UpgradesItemStackHandler;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CapabilityConnectableAutoIO implements INBTSerializable<CompoundNBT>, IConnectableItemAutoIO {

  public final IConnectable connectable;
  public EnumStorageDirection direction;
  public final UpgradesItemStackHandler upgrades = new UpgradesItemStackHandler();
  private final FilterItemStackHandler filters = new FilterItemStackHandler();
  private ItemStack operationStack = ItemStack.EMPTY;
  private int operationLimit = 0;
  private boolean operationMustBeSmaller = true;
  private int priority = 0;
  private Direction inventoryFace;

  CapabilityConnectableAutoIO(EnumStorageDirection direction) {
    connectable = new CapabilityConnectable();
    this.direction = direction;
  }

  public FilterItemStackHandler getFilter() {
    return filters;
  }

  //TODO: shrae with ConnectableLink  @Override
  public List<ItemStack> getStoredStacks() {
    if (inventoryFace == null) {
      return Collections.EMPTY_LIST;
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
      if (filters.isStackFiltered(stack)) {
        continue;
      }
      result.add(stack.copy());
    }
    return result;
  }

  //TODO: shrae with ConnectableLink
  public void setPriority(int value) {
    this.priority = value;
  }

  public void setFilter(int value, ItemStack stack) {
    filters.setStackInSlot(value, stack);
    filters.getStacks().set(value, stack);
  }

  public CapabilityConnectableAutoIO(TileEntity tile, EnumStorageDirection direction) {
    connectable = tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null).orElse(null);
    this.direction = direction;
    // Set some defaults
    if (direction == EnumStorageDirection.OUT) {
      filters.setIsWhitelist(true);
    }
    else {
      filters.setIsWhitelist(false);
    }
  }

  public void setInventoryFace(Direction inventoryFace) {
    this.inventoryFace = inventoryFace;
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT result = new CompoundNBT();
    result.put("upgrades", this.upgrades.serializeNBT());
    result.put("filters", this.filters.serializeNBT());
    CompoundNBT operation = new CompoundNBT();
    operation.put("stack", operationStack.serializeNBT());
    operation.putBoolean("mustBeSmaller", operationMustBeSmaller);
    operation.putInt("limit", operationLimit);
    result.put("operation", operation);
    result.putInt("prio", priority);
    if (inventoryFace != null) {
      result.putString("inventoryFace", inventoryFace.toString());
    }
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    CompoundNBT upgrades = nbt.getCompound("upgrades");
    if (upgrades != null) {
      this.upgrades.deserializeNBT(upgrades);
    }
    CompoundNBT filters = nbt.getCompound("filters");
    if (filters != null) {
      this.filters.deserializeNBT(filters);
    }
    CompoundNBT operation = nbt.getCompound("operation");
    operationStack = ItemStack.EMPTY;
    if (operation != null) {
      operationLimit = operation.getInt("limit");
      operationMustBeSmaller = operation.getBoolean("mustBeSmaller");
      if (operation.contains("stack", Constants.NBT.TAG_COMPOUND)) {
        operationStack = ItemStack.read((CompoundNBT) operation.get("stack"));
      }
      //      else {
      //        operationStack = ItemStack.EMPTY;
      //      }
    }
    priority = nbt.getInt("prio");
    if (nbt.contains("inventoryFace")) {
      inventoryFace = Direction.byName(nbt.getString("inventoryFace"));
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
  public int getTransferRate() {
    return upgrades.getUpgradesOfType(SsnRegistry.stack_upgrade) > 0 ? 64 : 4;
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
      StorageNetwork.LOGGER.info("getStacksForFilter    null itemhandler connection ");
      return Collections.emptyList();
    }
    // If it does, iterate its stacks, filter them and add them to the result list
    List<ItemStack> result = new ArrayList<>();
    for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
      ItemStack stack = itemHandler.getStackInSlot(slot);
      if (stack == null || stack.isEmpty()) {
        continue;
      }
      StorageNetwork.LOGGER.info(slot + "getStacksForFilter    " + stack
          + filters.exactStackAlreadyInList(stack));
      if (filters.exactStackAlreadyInList(stack)) {
        continue;
      }
      result.add(stack.copy());
      StorageNetwork.LOGGER.info("getStacksForFilter   size up   " + result.size());
      // We can abort after we've found FILTER_SIZE stacks; we don't have more filter slots anyway
      if (result.size() >= FilterItemStackHandler.FILTER_SIZE) {
        return result;
      }
    }
    return result;
  }

  @Override
  public ItemStack extractNextStack(int size, boolean simulate) {
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
      int extractSize = Math.min(size, stack.getCount());
      return itemHandler.extractItem(slot, extractSize, simulate);
    }
    return ItemStack.EMPTY;
  }

  private int countOps(){
    return 0;// upgrades.getUpgradesOfType(SsnRegistry.operation_upgrade);
  }


  private boolean doesPassOperationFilterLimit(TileMaster master) {
    if (countOps() < 1) {
      return true;
    }
    if (operationStack == null || operationStack.isEmpty()) {
      return true;
    }
    // TODO: Investigate whether the operation limiter should consider the filter toggles
    int availableStack = master.getAmount(new ItemStackMatcher(operationStack, filters.tags, filters.nbt));
    if (operationMustBeSmaller) {
      return operationLimit >= availableStack;
    }
    else {
      return operationLimit < availableStack;
    }
  }

  @Override
  public boolean runNow(DimPos connectablePos, TileMaster master) {
    int speed = Math.max(upgrades.getUpgradesOfType(SsnRegistry.speed_upgrade) + 1, 1);
    int speedRatio = (30 / speed);
    if (speedRatio <= 1) {
      speedRatio = 1;
    }
    boolean cooldownOk = (connectablePos.getWorld().getGameTime() % speedRatio == 0);
    boolean operationLimitOk = doesPassOperationFilterLimit(master);
    return cooldownOk && operationLimitOk;
  }

  @Override
  public List<IItemStackMatcher> getAutoExportList() {
    return filters.getStackMatchers();
  }

  public static class Factory implements Callable<IConnectableItemAutoIO> {

    @Override
    public IConnectableItemAutoIO call() throws Exception {
      return new CapabilityConnectableAutoIO(EnumStorageDirection.IN);
    }
  }

  public static class Storage implements Capability.IStorage<IConnectableItemAutoIO> {

    @Override
    @Nullable
    public INBT writeNBT(Capability<IConnectableItemAutoIO> capability, IConnectableItemAutoIO rawInstance, Direction side) {
      CapabilityConnectableAutoIO instance = (CapabilityConnectableAutoIO) rawInstance;
      return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IConnectableItemAutoIO> capability, IConnectableItemAutoIO rawInstance, Direction side, INBT nbt) {
      CapabilityConnectableAutoIO instance = (CapabilityConnectableAutoIO) rawInstance;
      instance.deserializeNBT((CompoundNBT) nbt);
    }
  }

  @Override
  public boolean isStockMode() {
    return false;//TODO: make this work upgrades.getUpgradesOfType(SsnRegistry.stock_upgrade) > 0;
  }

  @Override
  public Direction facingInventory() {
    return inventoryFace;
  }
}
