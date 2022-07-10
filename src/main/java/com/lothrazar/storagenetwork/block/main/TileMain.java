package com.lothrazar.storagenetwork.block.main;

import java.util.List;
import java.util.Set;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumStorageDirection;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableItemAutoIO;
import com.lothrazar.storagenetwork.api.IConnectableLink;
import com.lothrazar.storagenetwork.api.IItemStackMatcher;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.util.UtilInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileMain extends BlockEntity {

  //currently this has one network
  public NetworkModule nw = new NetworkModule();

  public TileMain(BlockPos pos, BlockState state) {
    super(SsnRegistry.Tiles.MASTER.get(), pos, state);
  }

  public DimPos getDimPos() {
    return new DimPos(level, worldPosition);
  }

  public void clearCache() {
    nw.ch.clearCache();
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag nbt = new CompoundTag();
    this.saveAdditional(nbt);
    return nbt;
  }

  /**
   * returns countUnmoved , the number of items NOT inserted.
   */
  public int insertStack(ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return 0;
    }
    // 1. Try to insert into a recent slot for the same item.
    //    We do this to avoid having to search for the appropriate inventory repeatedly.
    String key = UtilInventory.getStackKey(stack);
    if (nw.ch.hasCachedSlot(stack)) {
      DimPos cachedStoragePos = nw.ch.getCachedSlot(stack);
      IConnectableLink storage = cachedStoragePos.getCapability(StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY, null);
      if (storage == null) {
        // The block at the cached position is not even an IConnectableLink anymore
        nw.ch.remove(key);
      }
      else {
        // But if it is, we test whether it can still import that particular stack and do so if it does.
        boolean canStillImport = storage.getSupportedTransferDirection().match(EnumStorageDirection.IN);
        if (canStillImport &&
            storage.insertStack(stack, true).getCount() < stack.getCount()) {
          stack = storage.insertStack(stack, simulate);
          StorageNetworkMod.log("cache success used on a insertStack " + key);
        }
        else {
          nw.ch.remove(key);
        }
      }
    }
    // 2. If everything got transferred into the cached storage, end here
    if (stack.isEmpty()) {
      return 0;
    }
    // 3. Otherwise try to find a new inventory that can take the remainder of the itemstack
    List<IConnectableLink> storages = nw.getSortedConnectableStorage();
    for (IConnectableLink storage : storages) {
      try {
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
        nw.ch.put(key, storage.getPos());
      }
      catch (Exception e) {
        StorageNetworkMod.LOGGER.error("insertStack container issue", e);
      }
    }
    return stack.getCount();
  }

  /**
   * Pull into the network from the relevant linked cables
   */
  private void updateImports() {
    for (IConnectable connectable : nw.getConnectables()) {
      if (connectable == null || connectable.getPos() == null) {
        continue;
      }
      IConnectableItemAutoIO storage = connectable.getPos().getCapability(StorageNetworkCapabilities.CONNECTABLE_AUTO_IO, null);
      if (storage == null) {
        continue;
      }
      //
      // We explicitely don't want to check whether this can do BOTH, because we don't
      // want to import what we've just exported in updateExports().
      if (storage.ioDirection() != EnumStorageDirection.IN) {
        continue;
      }
      // Give the storage a chance to have a cooldown or other conditions that prevent it from running
      if (!storage.runNow(connectable.getPos(), this)) {
        continue;
      }
      IItemHandler itemHandler = storage.getItemHandler();
      if (itemHandler == null) {
        continue;
      }
      if (storage.needsRedstone()) {
        boolean power = level.hasNeighborSignal(connectable.getPos().getBlockPos());
        if (power == false) {
          continue;
        }
      }
      for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
        if (itemHandler.getStackInSlot(slot).isEmpty()) {
          continue;
        }
        ItemStack stackCurrent = itemHandler.getStackInSlot(slot).copy();
        // Ignore stacks that are filtered
        if (storage.getFilters() == null || !storage.getFilters().isStackFiltered(stackCurrent)) {
          if (storage.isStockMode()) {
            int filterSize = storage.getFilters().getStackCount(stackCurrent);
            BlockEntity tileEntity = level.getBlockEntity(connectable.getPos().getBlockPos().relative(storage.facingInventory()));
            IItemHandler targetInventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
            //request with false to see how many even exist in there.
            int chestHowMany = UtilInventory.countHowMany(targetInventory, stackCurrent);
            //so if chest=37 items of that kind
            //and the filter is say filterSize == 20
            //we SHOULD import 37
            //as we want the STOCK of the chest to not go less than the filter number , just down to it
            if (chestHowMany > filterSize) {
              int realSize = Math.min(chestHowMany - filterSize, 64);
              StorageNetworkMod.log(" : stock mode import  realSize = " + realSize);
              stackCurrent.setCount(realSize);
            }
            else {
              StorageNetworkMod.log(" : stock mode CANCEL: ITS NOT ENOUGH chestHowMany <= filter size ");
              continue;
            }
          }
          //
          //
          //
          int extractSize = Math.min(storage.getTransferRate(), stackCurrent.getCount());
          ItemStack stackToImport = itemHandler.extractItem(slot, extractSize, true); //simulate to grab a reference
          if (stackToImport.isEmpty()) {
            continue; //continue back to itemHandler
          }
          // Then try to insert the stack into this masters network and store the number of remaining items in the stack
          int countUnmoved = this.insertStack(stackToImport, true);
          // Calculate how many items in the stack actually got moved
          int countMoved = stackToImport.getCount() - countUnmoved;
          if (countMoved <= 0) {
            continue; //continue back to itemHandler
          }
          // Alright, simulation says we're good, let's do it!
          // First extract from the storage
          ItemStack actuallyExtracted = itemHandler.extractItem(slot, countMoved, false);
          // Then insert into our network
          this.insertStack(actuallyExtracted, false);
          break; // break out of itemHandler loop, done processing this cable, so move to next
        } //end of checking on filter for this stack
      }
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
    Set<IConnectable> conSet = nw.getConnectables();
    for (IConnectable connectable : conSet) {
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
      if (storage.needsRedstone()) {
        boolean power = level.hasNeighborSignal(connectable.getPos().getBlockPos());
        if (power == false) {
          //  StorageNetwork.log(power + " Export pow here ; needs yes skip me");
          continue;
        }
      }
      for (IItemStackMatcher matcher : storage.getAutoExportList()) {
        if (matcher.getStack().isEmpty()) {
          continue;
        }
        //default amt to request. can be overriden by other upgrades
        int amtToRequest = storage.getTransferRate();
        //check operations upgrade for export 
        boolean stockMode = storage.isStockMode();
        if (stockMode) {
          StorageNetworkMod.log("stockMode == TRUE ; updateExports: attempt " + matcher.getStack());
          //STOCK upgrade means
          try {
            BlockEntity tileEntity = level.getBlockEntity(connectable.getPos().getBlockPos().relative(storage.facingInventory()));
            IItemHandler targetInventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
            //request with false to see how many even exist in there.
            int stillNeeds = UtilInventory.containsAtLeastHowManyNeeded(targetInventory, matcher.getStack(), matcher.getStack().getCount());
            if (stillNeeds == 0) {
              //they dont need any more, they have the stock they need
              StorageNetworkMod.log("stockMode continnue; canc");
              continue;
            }
            amtToRequest = Math.min(stillNeeds, amtToRequest);
            StorageNetworkMod.log("updateExports stock mode edited value: amtToRequest = " + amtToRequest);
          }
          catch (Throwable e) {
            StorageNetworkMod.LOGGER.error("Error thrown from a connected block" + e);
          }
        }
        if (matcher.getStack().isEmpty() || amtToRequest == 0) {
          //either the thing is empty or we are requesting none
          continue;
        }
        ItemStack requestedStack = this.request((ItemStackMatcher) matcher, amtToRequest, true);
        if (requestedStack.isEmpty()) {
          continue;
        }
        //     StorageNetwork.log("updateExports: found requestedStack = " + requestedStack);
        // The stack is available in the network, let's simulate inserting it into the storage
        ItemStack insertedSim = storage.insertStack(requestedStack, true);
        // Determine the amount of items moved in the stack
        if (!insertedSim.isEmpty()) {
          int movedItems = requestedStack.getCount() - insertedSim.getCount();
          if (movedItems <= 0) {
            continue;
          }
          requestedStack.setCount(movedItems);
        }
        // Alright, some items got moved in the simulation. Let's do it for real this time.
        ItemStack realExtractedStack = request(new ItemStackMatcher(requestedStack, false, true), requestedStack.getCount(), false);
        if (realExtractedStack.isEmpty()) {
          continue;
        }
        storage.insertStack(realExtractedStack, false);
        break;
      }
    }
  }

  public ItemStack request(ItemStackMatcher matcher, int size, boolean simulate) {
    if (size == 0 || matcher == null) {
      return ItemStack.EMPTY;
    }
    IItemStackMatcher usedMatcher = matcher;
    int alreadyTransferred = 0;
    for (IConnectableLink storage : nw.getSortedConnectableStorage()) {
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

  private void tick() {
    if (level == null || level.isClientSide) {
      return;
    }
    //refresh time in config, default 200 ticks aka 10 seconds
    if ((level.getGameTime() % StorageNetworkMod.CONFIG.refreshTicks() == 0)
        || nw.shouldRefresh()) {
      nw.doRefresh(this.getDimPos());
    }
    updateImports();
    updateExports();
    updateProcess();
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    saveWithFullMetadata();
    return ClientboundBlockEntityDataPacket.create(this); // new ClientboundBlockEntityDataPacket(worldPosition, 1, syncData);
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    load(pkt.getTag() == null ? new CompoundTag() : pkt.getTag());
    super.onDataPacket(net, pkt);
  }

  public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileMain tile) {}

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileMain tile) {
    tile.tick();
  }
}
