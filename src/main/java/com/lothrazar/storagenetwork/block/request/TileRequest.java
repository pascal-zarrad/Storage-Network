package com.lothrazar.storagenetwork.block.request;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.storagenetwork.api.data.EnumSortType;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

public class TileRequest extends TileConnectable implements INamedContainerProvider {

  Map<Integer, ItemStack> matrix = new HashMap<>();
  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;

  public TileRequest() {
    super(SsnRegistry.requesttile);
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    setDownwards(compound.getBoolean("dir"));
    if (compound.contains("sort")) {
      setSort(EnumSortType.values()[compound.getInt("sort")]);
    }
    ListNBT invList = compound.getList("matrix", Constants.NBT.TAG_COMPOUND);
    matrix = new HashMap<>();
    for (int i = 0; i < invList.size(); i++) {
      CompoundNBT stackTag = invList.getCompound(i);
      int slot = stackTag.getByte("Slot");
      ItemStack s = ItemStack.read(stackTag);
      matrix.put(slot, s);
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    super.write(compound);
    compound.putBoolean("dir", isDownwards());
    compound.putInt("sort", getSort().ordinal());
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
    return compound;
  }

  boolean isDownwards() {
    return downwards;
  }

  public void setDownwards(boolean downwards) {
    this.downwards = downwards;
  }

  EnumSortType getSort() {
    return sort;
  }

  public void setSort(EnumSortType sort) {
    this.sort = sort;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new StringTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerRequest(i, world, pos, playerInventory, playerEntity);
  }
}
