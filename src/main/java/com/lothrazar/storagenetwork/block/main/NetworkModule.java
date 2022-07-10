package com.lothrazar.storagenetwork.block.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumStorageDirection;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableLink;
import com.lothrazar.storagenetwork.api.IItemStackMatcher;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.util.UtilInventory;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Responsible for network connection list, cache, requests and single inserts.
 * 
 * Not responsible for processing, import cables, or export cables
 * 
 * @author lothr
 *
 */
public class NetworkModule {

  NetworkCache ch = new NetworkCache();
  private Set<DimPos> connectables = new HashSet<>();
  private boolean shouldRefresh = true;

  public void setShouldRefresh() {
    shouldRefresh = true;
  }

  public int getConnectableSize() {
    return connectables == null ? 0 : connectables.size();
  }

  Set<IConnectable> getConnectables() {
    Set<DimPos> positions = new HashSet<>(connectables);
    Set<IConnectable> result = new HashSet<>();
    for (DimPos pos : positions) {
      if (!pos.isLoaded()) {
        continue;
      }
      BlockEntity tileEntity = pos.getTileEntity(BlockEntity.class);
      if (tileEntity == null) {
        continue;
      }
      IConnectable cap = tileEntity.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null).orElse(null);
      if (cap == null) {
        StorageNetworkMod.LOGGER.info("Somehow stored a dimpos that is not connectable... Skipping " + pos);
        continue;
      }
      result.add(cap);
    }
    return result;
  }

  void addConnectables(DimPos sourcePos, Set<DimPos> set, DimPos masterPos) {
    if (sourcePos == null || sourcePos.getWorld() == null || !sourcePos.isLoaded()) {
      return;
    }
    // Look in all directions
    for (Direction direction : Direction.values()) {
      DimPos lookPos = sourcePos.offset(direction);
      if (!lookPos.isLoaded()) {
        continue;
      }
      ChunkAccess chunk = lookPos.getChunk();
      if (chunk == null) {
        continue;
      }
      // Prevent having multiple  on a network and break all others.
      TileMain maybeMain = lookPos.getTileEntity(TileMain.class);
      if (maybeMain != null && !lookPos.equals(masterPos.getWorld(), masterPos.getBlockPos())) {
        UtilInventory.nukeAndDrop(lookPos);
        continue;
      }
      BlockEntity tileHere = lookPos.getTileEntity(BlockEntity.class);
      if (tileHere == null) {
        continue;
      }
      IConnectable capabilityConnectable = tileHere.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, direction.getOpposite()).orElse(null);
      if (capabilityConnectable == null) {
        continue;
      }
      if (capabilityConnectable.getPos() == null) {
        capabilityConnectable.setPos(lookPos);
        capabilityConnectable.setMainPos(masterPos);
      }
      if (capabilityConnectable != null) {
        capabilityConnectable.setMainPos(masterPos);
        DimPos realConnectablePos = capabilityConnectable.getPos();
        boolean beenHereBefore = set.contains(realConnectablePos);
        if (beenHereBefore) {
          continue;
        }
        if (realConnectablePos.getWorld() == null) {
          realConnectablePos.setWorld(sourcePos.getWorld());
        }
        set.add(realConnectablePos);
        addConnectables(realConnectablePos, set, masterPos);
        tileHere.setChanged();
        chunk.setUnsaved(true);
      }
    }
  }

  /**
   * This is a recursively called method that traverses all connectable blocks and stores them in this tiles connectables list.
   *
   * @param sourcePos
   */
  Set<DimPos> getConnectables(DimPos masterPos) {
    HashSet<DimPos> result = new HashSet<>();
    addConnectables(masterPos, result, masterPos);
    return result;
  }

  public List<ItemStack> getSortedStacks() {
    List<ItemStack> stacks = Lists.newArrayList();
    try {
      if (connectables == null) {
        setShouldRefresh();
      }
    }
    catch (Exception e) {
      //since this has external mod connections, if they break then catch it
      //      for example, AE2 can break with  Ticking GridNode
      StorageNetworkMod.LOGGER.info("3rd party storage mod has an error", e);
    }
    try {
      for (IConnectableLink storage : getSortedConnectableStorage()) {
        for (ItemStack stack : storage.getStoredStacks(true)) {
          if (stack == null || stack.isEmpty()) {
            continue;
          }
          UtilTileEntity.addOrMergeIntoList(stacks, stack);
        }
      }
    }
    catch (Exception e) {
      StorageNetworkMod.LOGGER.info("3rd party storage mod has an error", e);
    }
    return stacks;
  }

  public List<ItemStack> getStacks() {
    List<ItemStack> stacks = Lists.newArrayList();
    try {
      for (IConnectableLink storage : getConnectableStorage()) {
        for (ItemStack stack : storage.getStoredStacks(true)) {
          if (stack == null || stack.isEmpty()) {
            continue;
          }
          UtilTileEntity.addOrMergeIntoList(stacks, stack);
        }
      }
    }
    catch (Exception e) {
      StorageNetworkMod.LOGGER.info("3rd party storage mod has an error", e);
    }
    return stacks;
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

  public List<Entry<String, Integer>> getDisplayStrings() {
    Map<String, Integer> mapNamesToCount = new HashMap<>();
    Iterator<DimPos> iter = new HashSet<>(this.connectables).iterator();
    Block bl;
    DimPos p;
    String blockName;
    while (iter.hasNext()) {
      p = iter.next();
      bl = p.getBlockState().getBlock();
      //getTranslatedName client only thanks mojang lol
      blockName = (new TranslatableComponent(bl.getDescriptionId())).getString();
      int count = mapNamesToCount.get(blockName) != null ? (mapNamesToCount.get(blockName) + 1) : 1;
      mapNamesToCount.put(blockName, count);
    }
    List<Entry<String, Integer>> listDisplayStrings = Lists.newArrayList();
    for (Entry<String, Integer> e : mapNamesToCount.entrySet()) {
      listDisplayStrings.add(e);
    }
    Collections.sort(listDisplayStrings, new Comparator<Entry<String, Integer>>() {

      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return Integer.compare(o2.getValue(), o1.getValue());
      }
    });
    return listDisplayStrings;
  }

  public int emptySlots() {
    int countEmpty = 0;
    for (IConnectableLink storage : getSortedConnectableStorage()) {
      countEmpty += storage.getEmptySlots();
    }
    return countEmpty;
  }

  List<IConnectableLink> getSortedConnectableStorage() {
    try {
      Set<IConnectableLink> storage = getConnectableStorage();
      Stream<IConnectableLink> stream = storage.stream();
      List<IConnectableLink> sorted = stream.sorted(Comparator.comparingInt(IConnectableLink::getPriority)).collect(Collectors.toList());
      return sorted;
    }
    catch (Exception e) {
      //trying to avoid 
      //java.lang.StackOverflowError: Ticking block entity
      //and similar issues
      StorageNetworkMod.LOGGER.error("Error: network get sorted by priority error, some network components are disconnected ", e);
      return new ArrayList<>();
    }
  }

  private Set<IConnectableLink> getConnectableStorage() {
    Set<DimPos> conSet = new HashSet<>(connectables);
    Set<IConnectableLink> result = new HashSet<>();
    for (DimPos dimpos : conSet) {
      if (!dimpos.isLoaded()) {
        continue;
      }
      BlockEntity tileEntity = dimpos.getTileEntity(BlockEntity.class);
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

  public void doRefresh(DimPos masterPos) {
    try {
      this.connectables = this.getConnectables(masterPos);
      this.shouldRefresh = false;
      masterPos.getWorld().getChunk(masterPos.getBlockPos()).setUnsaved(true);
    }
    catch (Throwable e) {
      StorageNetworkMod.LOGGER.info("Refresh network error ", e);
    }
  }

  public boolean shouldRefresh() {
    return this.shouldRefresh;
  }

  public int insertStack(ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return 0;
    }
    // 1. Try to insert into a recent slot for the same item.
    //    We do this to avoid having to search for the appropriate inventory repeatedly.
    String key = UtilInventory.getStackKey(stack);
    if (ch.hasCachedSlot(stack)) {
      DimPos cachedStoragePos = ch.getCachedSlot(stack);
      IConnectableLink storage = cachedStoragePos.getCapability(StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY, null);
      if (storage == null) {
        // The block at the cached position is not even an IConnectableLink anymore
        ch.remove(key);
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
          ch.remove(key);
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
        ch.put(key, storage.getPos());
      }
      catch (Exception e) {
        StorageNetworkMod.LOGGER.error("insertStack container issue", e);
      }
    }
    return stack.getCount();
  }

  public ItemStack request(ItemStackMatcher matcher, int size, boolean simulate) {
    if (size == 0 || matcher == null) {
      return ItemStack.EMPTY;
    }
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
}
