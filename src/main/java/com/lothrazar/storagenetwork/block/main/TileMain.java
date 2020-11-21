package com.lothrazar.storagenetwork.block.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumStorageDirection;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableItemAutoIO;
import com.lothrazar.storagenetwork.api.IConnectableLink;
import com.lothrazar.storagenetwork.api.IItemStackMatcher;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.capability.handler.MasterItemStackHandler;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.util.UtilInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileMain extends TileEntity implements ITickableTileEntity {

  private Set<DimPos> connectables;
  private MasterItemStackHandler itemHandler;
  private Map<String, DimPos> importCache = new HashMap<>();
  private boolean shouldRefresh = true;

  private DimPos getDimPos() {
    return new DimPos(world, pos);
  }

  public TileMain() {
    super(SsnRegistry.mainTileentity);
    itemHandler = new MasterItemStackHandler(this);
  }

  public List<ItemStack> getStacks() {
    List<ItemStack> stacks = Lists.newArrayList();
    if (getConnectablePositions() == null) {
      refreshNetwork();
    }
    for (IConnectableLink storage : getSortedConnectableStorage()) {
      for (ItemStack stack : storage.getStoredStacks(true)) {
        if (stack == null || stack.isEmpty()) {
          continue;
        }
        addOrMergeIntoList(stacks, stack.copy());
      }
    }
    return stacks;
  }

  private static void addOrMergeIntoList(List<ItemStack> list, ItemStack stackToAdd) {
    boolean added = false;
    for (ItemStack stack : list) {
      if (ItemHandlerHelper.canItemStacksStack(stackToAdd, stack)) {
        stack.setCount(stack.getCount() + stackToAdd.getCount());
        added = true;
        break;
      }
    }
    if (!added) {
      list.add(stackToAdd);
    }
  }

  int emptySlots() {
    int countEmpty = 0;
    for (IConnectableLink storage : getSortedConnectableStorage()) {
      countEmpty += storage.getEmptySlots();
    }
    return countEmpty;
  }

  public int getAmount(ItemStackMatcher fil) {
    if (fil == null) {
      return 0;
    }
    int totalCount = 0;
    for (ItemStack stack : getStacks()) {
      if (!fil.match(stack)) {
        continue;
      }
      totalCount += stack.getCount();
    }
    return totalCount;
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return write(new CompoundNBT());
  }

  /**
   * This is a recursively called method that traverses all connectable blocks and stores them in this tiles connectables list.
   *
   * @param sourcePos
   */
  private Set<DimPos> getConnectables(DimPos sourcePos) {
    HashSet<DimPos> result = new HashSet<>();
    addConnectables(sourcePos, result);
    return result;
  }

  private void addConnectables(DimPos sourcePos, Set<DimPos> set) {
    if (sourcePos == null || sourcePos.getWorld() == null || !sourcePos.isLoaded()) {
      return;
    }
    // Look in all directions
    for (Direction direction : Direction.values()) {
      DimPos lookPos = sourcePos.offset(direction);
      if (!lookPos.isLoaded()) {
        continue;
      }
      IChunk chunk = lookPos.getChunk();
      if (chunk == null) {// || !chunk.isLoaded()) {
        continue;
      }
      // Prevent having multiple  on a network and break all others.
      TileMain maybeMain = lookPos.getTileEntity(TileMain.class);
      if (maybeMain != null && !lookPos.equals(world, pos)) {
        nukeAndDrop(lookPos);
        continue;
      }
      TileEntity tileHere = lookPos.getTileEntity(TileEntity.class);
      if (tileHere == null) {
        continue;
      }
      IConnectable capabilityConnectable = tileHere.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, direction.getOpposite()).orElse(null);
      if (capabilityConnectable == null) {
        continue;
      }
      //
      if (capabilityConnectable.getPos() == null) {
        //  1.15 hax
        // StorageNetwork.LOGGER.info("1.15 HAX NULL POS !! " + lookPos + "has tile " + tileHere);
        //wait what 
        capabilityConnectable.setPos(lookPos);
        capabilityConnectable.setMainPos(this.getDimPos());
      }
      //
      if (capabilityConnectable != null) {
        //        IConnectable capabilityConnectable = tileHere.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, direction.getOpposite());
        capabilityConnectable.setMainPos(getDimPos());
        DimPos realConnectablePos = capabilityConnectable.getPos();
        boolean beenHereBefore = set.contains(realConnectablePos);
        if (beenHereBefore) {
          continue;
        }
        if (realConnectablePos.getWorld() == null) {
          // StorageNetwork.LOGGER.info("1.15 realConnectablePos HAX NULL WORLD  " + realConnectablePos);
          realConnectablePos.setWorld(sourcePos.getWorld());
        }
        set.add(realConnectablePos);
        addConnectables(realConnectablePos, set);
        tileHere.markDirty();
        chunk.setModified(true);
      }
    }
  }

  private static void nukeAndDrop(DimPos lookPos) {
    lookPos.getWorld().destroyBlock(lookPos.getBlockPos(), true);
    lookPos.getWorld().removeTileEntity(lookPos.getBlockPos());
  }

  public static boolean isTargetAllowed(BlockState state) {
    if (state.getBlock() == Blocks.AIR) {
      return false;
    }
    String blockId = state.getBlock().getRegistryName().toString();
    for (String s : StorageNetwork.config.ignorelist()) {
      if (blockId.equals(s)) {
        return false;
      }
    }
    return true;
  }

  public void refreshNetwork() {
    if (world.isRemote) {
      return;
    }
    shouldRefresh = true;
  }

  private boolean hasCachedSlot(ItemStack stack) {
    return importCache.containsKey(getStackKey(stack));
  }

  private DimPos getCachedSlot(ItemStack stack) {
    return importCache.get(getStackKey(stack));
  }

  public int insertStack(ItemStack rawStack, boolean simulate) {
    if (rawStack.isEmpty()) {
      return 0;
    }
    ItemStack stack = rawStack.copy();
    // 1. Try to insert into a recent slot for the same item.
    //    We do this to avoid having to search for the appropriate inventory repeatedly.
    String key = getStackKey(stack);
    if (hasCachedSlot(stack)) {
      DimPos cachedStoragePos = getCachedSlot(stack);
      IConnectableLink storage = cachedStoragePos.getCapability(StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY, null);
      if (storage == null) {
        // The block at the cached position is not even an IConnectableLink anymore
        importCache.remove(key);
      }
      else {
        // But if it is, we test whether it can still import that particular stack and do so if it does.
        boolean canStillImport = storage.getSupportedTransferDirection().match(EnumStorageDirection.IN);
        if (canStillImport && storage.insertStack(stack, true).getCount() < stack.getCount()) {
          stack = storage.insertStack(stack, simulate);
        }
        else {
          importCache.remove(key);
        }
      }
    }
    // 2. If everything got transferred into the cached storage, end here
    if (stack.isEmpty()) {
      return 0;
    }
    // 3. Otherwise try to find a new inventory that can take the remainder of the itemstack
    List<IConnectableLink> storages = getSortedConnectableStorage();
    for (IConnectableLink storage : storages) {
      // Ignore storages that can not import
      if (!storage.getSupportedTransferDirection().match(EnumStorageDirection.IN)) {
        continue;
      }
      // The given import-capable storage can not import this particular stack
      if (storage.insertStack(stack, true).getCount() >= stack.getCount()) {
        continue;
      }
      // If it can we need to know, i.e. store the remainder
      stack = storage.insertStack(stack, simulate);
    }
    return stack.getCount();
  }

  private static String getStackKey(ItemStack stackInCopy) {
    return stackInCopy.getItem().getRegistryName().toString();// + "/" + stackInCopy.getItemDamage();
  }

  /**
   * Pull into the network from the relevant linked cables
   */
  private void updateImports() {
    for (IConnectable connectable : getConnectables()) {
      if (connectable == null || connectable.getPos() == null) {
        //        StorageNetwork.log("null connectable or pos : updateImports() ");
        continue;
      }
      IConnectableItemAutoIO storage = connectable.getPos().getCapability(StorageNetworkCapabilities.CONNECTABLE_AUTO_IO, null);
      if (storage == null) {
        continue;
      }
      // We explicitely don't want to check whether this can do BOTH, because we don't
      // want to import what we've just exported in updateExports().
      if (storage.ioDirection() != EnumStorageDirection.IN) {
        continue;
      }
      // Give the storage a chance to have a cooldown or other conditions that prevent it from running
      if (!storage.runNow(connectable.getPos(), this)) {
        continue;
      }
      // Do a simulation first and abort if we got an empty stack,
      ItemStack stack = storage.extractNextStack(storage.getTransferRate(), true);
      if (stack.isEmpty()) {
        continue;
      }
      // Then try to insert the stack into this network and store the number of remaining items in the stack
      int countUnmoved = insertStack(stack.copy(), true);
      // Calculate how many items in the stack actually got moved
      int countMoved = stack.getCount() - countUnmoved;
      if (countMoved <= 0) {
        continue;
      }
      // Alright, simulation says we're good, let's do it!
      // First extract from the storage
      ItemStack actuallyExtracted = storage.extractNextStack(countMoved, false);
      connectable.getPos().getWorld().getChunkAt(connectable.getPos().getBlockPos()).markDirty();
      // Then insert into our network
      insertStack(actuallyExtracted.copy(), false);
    }
  }

  private void updateProcess() {
    //    for (IConnectable connectable : getConnectables()) {
    //    if (connectable == null || connectable.getPos() == null) {
    //      //        StorageNetwork.log("null connectable or pos : updateProcess() ");
    //      continue;
    //    }
    //      TileCableProcess cableProcess = connectable.getPos().getTileEntity(TileCableProcess.class);
    //      if (cableProcess == null) {
    //        continue;
    //      }
    //      cableProcess.run();
    //    }
  }

  /**
   * push OUT of the network to attached export cables
   */
  private void updateExports() {
    for (IConnectable connectable : getConnectables()) {
      if (connectable == null || connectable.getPos() == null) {
        //        StorageNetwork.log("null connectable or pos : updateExports() ");
        continue;
      }
      IConnectableItemAutoIO storage = connectable.getPos().getCapability(StorageNetworkCapabilities.CONNECTABLE_AUTO_IO, null);
      if (storage == null) {
        continue;
      }
      // We explicitely don't want to check whether this can do BOTH, because we don't
      // want to import what we've just exported in updateExports().
      if (storage.ioDirection() != EnumStorageDirection.OUT) {
        continue;
      }
      // Give the storage a chance to have a cooldown
      if (!storage.runNow(connectable.getPos(), this)) {
        continue;
      }
      for (IItemStackMatcher matcher : storage.getAutoExportList()) {
        boolean stockMode = storage.isStockMode();
        int amtToRequest = storage.getTransferRate();
        if (stockMode) {
          try {
            //       StorageNetwork.log("updateExports: attempt " + matcher.getStack());
            TileEntity tileEntity = world.getTileEntity(connectable.getPos().getBlockPos().offset(storage.facingInventory()));
            IItemHandler targetInventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
            //request with false to see how many even exist in there.  
            int stillNeeds = UtilInventory.containsAtLeastHowManyNeeded(targetInventory, matcher.getStack(), matcher.getStack().getCount());
            if (stillNeeds == 0) {
              continue;
            }
            //  StorageNetwork.log("updateExports: amtToRequest " + amtToRequest);
            amtToRequest = Math.min(stillNeeds, amtToRequest);
          }
          catch (Throwable e) {
            StorageNetwork.LOGGER.error("Error thrown from a connected block" + e);
          }
        }
        if (matcher.getStack().isEmpty() || amtToRequest == 0) {
          //  StorageNetwork.log("updateExports: i have empty " +amtToRequest) ;
          continue;
        }
        ItemStack requestedStack = this.request((ItemStackMatcher) matcher, amtToRequest, true);
        if (requestedStack.isEmpty()) {
          //  StorageNetwork.log("updateExports: requestedStack is empty so nothing pushed " + matcher);
          continue;
        }
        //     StorageNetwork.log("updateExports: found requestedStack = " + requestedStack);
        // The stack is available in the network, let's simulate inserting it into the storage
        ItemStack insertedSim = storage.insertStack(requestedStack.copy(), true);
        // Determine the amount of items moved in the stack
        ItemStack targetStack = requestedStack.copy();
        if (!insertedSim.isEmpty()) {
          int movedItems = requestedStack.getCount() - insertedSim.getCount();
          if (movedItems <= 0) {
            continue;
          }
          targetStack.setCount(movedItems);
        }
        // Alright, some items got moved in the simulation. Let's do it for real this time.
        ItemStack realExtractedStack = request(new ItemStackMatcher(requestedStack, false, true), targetStack.getCount(), false);
        if (realExtractedStack.isEmpty()) {
          continue;
        }
        storage.insertStack(realExtractedStack.copy(), false);
        break;
      }
    }
  }

  public ItemStack request(ItemStackMatcher matcher, int size, boolean simulate) {
    if (size == 0 || matcher == null) {
      return ItemStack.EMPTY;
    }
    // TODO: Test against storage drawers. There was some issue with it: https://github.com/PrinceOfAmber/Storage-Network/issues/19
    IItemStackMatcher usedMatcher = matcher;
    int alreadyTransferred = 0;
    for (IConnectableLink storage : getSortedConnectableStorage()) {
      int req = size - alreadyTransferred;
      ItemStack simExtract = storage.extractStack(usedMatcher, req, simulate);
      if (simExtract.isEmpty()) {
        continue;
      }
      // Do not stack items of different types together, i.e. make the filter rules more strict for all further items
      usedMatcher = new ItemStackMatcher(simExtract, matcher.isOre(), matcher.isNbt());
      alreadyTransferred += simExtract.getCount();
      if (alreadyTransferred >= size) {
        break;
      }
    }
    if (alreadyTransferred <= 0) {
      return ItemStack.EMPTY;
    }
    return ItemHandlerHelper.copyStackWithSize(usedMatcher.getStack(), alreadyTransferred);
  }

  private Set<IConnectable> getConnectables() {
    Set<DimPos> positions = getConnectablePositions();
    if (positions == null) {
      return new HashSet<>();
    }
    Set<IConnectable> result = new HashSet<>();
    for (DimPos pos : positions) {
      if (!pos.isLoaded()) {
        continue;
      }
      TileEntity tileEntity = pos.getTileEntity(TileEntity.class);
      if (tileEntity == null) {
        continue;
      }
      IConnectable cap = tileEntity.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null).orElse(null);
      if (cap == null) {
        StorageNetwork.LOGGER.info("Somehow stored a dimpos that is not connectable... Skipping " + pos);
        continue;
      }
      result.add(cap);
    }
    return result;
  }

  private Set<IConnectableLink> getConnectableStorage() {
    Set<IConnectableLink> result = new HashSet<>();
    for (DimPos dimpos : getConnectablePositions()) {
      if (!dimpos.isLoaded()) {
        continue;
      }
      TileEntity tileEntity = dimpos.getTileEntity(TileEntity.class);
      if (tileEntity == null) {
        continue;
      }
      IConnectableLink capConnect = tileEntity.getCapability(StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY, null).orElse(null);
      if (capConnect == null) {
        continue;
      }
      result.add(capConnect);
    }
    return result;
  }

  private List<IConnectableLink> getSortedConnectableStorage() {
    return getConnectableStorage().stream().sorted(Comparator.comparingInt(IConnectableLink::getPriority)).collect(Collectors.toList());
  }

  @Override
  public void tick() {//was .update(
    if (world == null || world.isRemote) {
      return;
    }
    //refresh time in config, default 200 ticks aka 10 seconds
    if (getConnectablePositions() == null || (world.getGameTime() % (StorageNetwork.config.refreshTicks()) == 0) || shouldRefresh) {
      try {
        //        StorageNetwork.log("Network refreshing..." + getDimPos());
        connectables = getConnectables(getDimPos());
        shouldRefresh = false;
        world.getChunk(pos).setModified(true);//.setChunkModified();
      }
      catch (Throwable e) {
        StorageNetwork.LOGGER.info("Refresh network error ");
        e.printStackTrace();
      }
    }
    updateImports();
    updateExports();
    updateProcess();
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT syncData = new CompoundNBT();
    write(syncData);
    return new SUpdateTileEntityPacket(pos, 1, syncData);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    read(pkt.getNbtCompound());
  }

  public static boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
    return oldState.getBlock() != newSate.getBlock();
  }

  /**
   * dont create an iterator over the original one that is being modified
   *
   * @return
   */
  public Set<DimPos> getConnectablePositions() {
    if (connectables == null) {
      connectables = new HashSet<>();
    }
    return new HashSet<>(connectables);
  }

  public void clearCache() {
    importCache = new HashMap<>();
  }
  
  
  
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
      itemHandler.update(); // update items in itemHandler
      return LazyOptional.of(new NonNullSupplier<T>() {
        public @Override @Nonnull T get() { return (T) itemHandler; }
      });
    }
    return super.getCapability(cap, side);
  }
}
