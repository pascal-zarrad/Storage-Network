package com.lothrazar.storagenetwork.block.request;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileNetworkSync;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

public class TileRequest extends TileConnectable implements INamedContainerProvider, ITileNetworkSync {

  public static final String NBT_JEI = StorageNetwork.MODID + "jei";
  private static final String NBT_DIR = StorageNetwork.MODID + "dir";
  private static final String NBT_SORT = StorageNetwork.MODID + "sort";
  Map<Integer, ItemStack> matrix = new HashMap<>();
  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;
  private boolean isJeiSearchSynced;

  public TileRequest() {
    super(SsnRegistry.REQUESTTILE);
  }

  @Override
  public void read(BlockState bs, CompoundNBT compound) {
    setDownwards(compound.getBoolean(NBT_DIR));
    if (compound.contains(NBT_SORT)) {
      setSort(EnumSortType.values()[compound.getInt(NBT_SORT)]);
    }
    if (compound.contains(NBT_JEI)) {
      this.setJeiSearchSynced(compound.getBoolean(NBT_JEI));
    }
    ListNBT invList = compound.getList("matrix", Constants.NBT.TAG_COMPOUND);
    matrix = new HashMap<>();
    for (int i = 0; i < invList.size(); i++) {
      CompoundNBT stackTag = invList.getCompound(i);
      int slot = stackTag.getByte("Slot");
      ItemStack s = ItemStack.read(stackTag);
      matrix.put(slot, s);
    }
    super.read(bs, compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound.putBoolean(NBT_DIR, isDownwards());
    compound.putInt(NBT_SORT, getSort().ordinal());
    compound.putBoolean(NBT_JEI, this.isJeiSearchSynced());
    ListNBT invList = new ListNBT();
    for (int i = 0; i < 9; i++) {
      if (matrix.get(i) != null && matrix.get(i).isEmpty() == false) {
        CompoundNBT stackTag = new CompoundNBT();
        stackTag.putByte("Slot", (byte) i);
        matrix.get(i).write(stackTag);
        invList.add(stackTag);
      }
    }
    compound.put("matrix", invList);
    return super.write(compound);
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
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerNetworkCraftingTable(i, world, pos, playerInventory, playerEntity);
  }

  public boolean isJeiSearchSynced() {
    return isJeiSearchSynced;
  }

  public void setJeiSearchSynced(boolean val) {
    isJeiSearchSynced = val;
  }
}
