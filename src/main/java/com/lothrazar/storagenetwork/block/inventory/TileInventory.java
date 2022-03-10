package com.lothrazar.storagenetwork.block.inventory;

import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileNetworkSync;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class TileInventory extends TileConnectable implements MenuProvider, ITileNetworkSync {

  public static final String NBT_JEI = TileRequest.NBT_JEI;
  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;
  private boolean isJeiSearchSynced;

  public TileInventory(BlockPos pos, BlockState state) {
    super(SsnRegistry.INVENTORYTILE, pos, state);
  }

  @Override
  public Component getDisplayName() {
    return new TranslatableComponent(getType().getRegistryName().getPath());
  }

  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
    return new ContainerNetworkInventory(i, level, worldPosition, playerInventory, playerEntity);
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
    setDownwards(compound.getBoolean("dir"));
    setSort(EnumSortType.values()[compound.getInt("sort")]);
    if (compound.contains(NBT_JEI)) {
      this.setJeiSearchSynced(compound.getBoolean(NBT_JEI));
    }
  }

  @Override
  public void saveAdditional(CompoundTag compound) {
    super.saveAdditional(compound);
    compound.putBoolean("dir", isDownwards());
    compound.putInt("sort", getSort().ordinal());
    compound.putBoolean(NBT_JEI, this.isJeiSearchSynced());
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

  public boolean isJeiSearchSynced() {
    return isJeiSearchSynced;
  }

  @Override
  public void setJeiSearchSynced(boolean val) {
    isJeiSearchSynced = val;
  }
}
