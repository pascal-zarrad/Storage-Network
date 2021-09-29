package com.lothrazar.storagenetwork.block.request;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileNetworkSync;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Constants;

public class TileRequest extends TileConnectable implements MenuProvider, ITileNetworkSync {

  public static final String NBT_JEI = StorageNetwork.MODID + "jei";
  private static final String NBT_DIR = StorageNetwork.MODID + "dir";
  private static final String NBT_SORT = StorageNetwork.MODID + "sort";
  public Map<Integer, ItemStack> matrix = new HashMap<>();
  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;
  private boolean isJeiSearchSynced;

  public TileRequest(BlockPos pos, BlockState state) {
    super(SsnRegistry.REQUESTTILE, pos, state);
  }

  @Override
  public void load(CompoundTag compound) {
    setDownwards(compound.getBoolean(NBT_DIR));
    if (compound.contains(NBT_SORT)) {
      setSort(EnumSortType.values()[compound.getInt(NBT_SORT)]);
    }
    if (compound.contains(NBT_JEI)) {
      this.setJeiSearchSynced(compound.getBoolean(NBT_JEI));
    }
    ListTag invList = compound.getList("matrix", Constants.NBT.TAG_COMPOUND);
    matrix = new HashMap<>();
    for (int i = 0; i < invList.size(); i++) {
      CompoundTag stackTag = invList.getCompound(i);
      int slot = stackTag.getByte("Slot");
      ItemStack s = ItemStack.of(stackTag);
      matrix.put(slot, s);
    }
    super.load(compound);
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    compound.putBoolean(NBT_DIR, isDownwards());
    compound.putInt(NBT_SORT, getSort().ordinal());
    compound.putBoolean(NBT_JEI, this.isJeiSearchSynced());
    ListTag invList = new ListTag();
    for (int i = 0; i < 9; i++) {
      if (matrix.get(i) != null && matrix.get(i).isEmpty() == false) {
        CompoundTag stackTag = new CompoundTag();
        stackTag.putByte("Slot", (byte) i);
        matrix.get(i).save(stackTag);
        invList.add(stackTag);
      }
    }
    compound.put("matrix", invList);
    return super.save(compound);
  }

  @Override
  public boolean isDownwards() {
    return downwards;
  }

  @Override
  public void setDownwards(boolean downwards) {
    this.downwards = downwards;
  }

  @Override
  public EnumSortType getSort() {
    return sort;
  }

  @Override
  public void setSort(EnumSortType sort) {
    this.sort = sort;
  }

  @Override
  public Component getDisplayName() {
    return new TranslatableComponent(getType().getRegistryName().getPath());
  }

  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
    return new ContainerNetworkCraftingTable(i, level, worldPosition, playerInventory, playerEntity);
  }

  public boolean isJeiSearchSynced() {
    return isJeiSearchSynced;
  }

  @Override
  public void setJeiSearchSynced(boolean val) {
    isJeiSearchSynced = val;
  }
}
