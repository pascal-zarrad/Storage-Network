package com.lothrazar.storagenetwork.block.inventory;

import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileSortable;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileInventory extends TileConnectable implements INamedContainerProvider, ITileSortable {

  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;

  public TileInventory() {
    super(SsnRegistry.inventorytile);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerNetworkInventory(i, world, pos, playerInventory, playerEntity);
  }

  @Override
  public void read(BlockState bs, CompoundNBT compound) {
    super.read(bs, compound);
    setDownwards(compound.getBoolean("dir"));
    setSort(EnumSortType.values()[compound.getInt("sort")]);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    super.write(compound);
    compound.putBoolean("dir", isDownwards());
    compound.putInt("sort", getSort().ordinal());
    return compound;
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
}
