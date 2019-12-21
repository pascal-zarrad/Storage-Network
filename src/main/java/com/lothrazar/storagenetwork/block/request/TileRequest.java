package com.lothrazar.storagenetwork.block.request;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.ITileSortable;
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

public class TileRequest extends TileConnectable implements INamedContainerProvider, ITileSortable {

  private static final String NBT_DIR = StorageNetwork.MODID + "dir";
  private static final String NBT_SORT = StorageNetwork.MODID + "sort";
  Map<Integer, ItemStack> matrix = new HashMap<>();
  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;

  public TileRequest() {
    super(SsnRegistry.requesttile);
  }

  @Override
  public void read(CompoundNBT compound) {
    System.out.println("READ" + compound);
    setDownwards(compound.getBoolean(NBT_DIR));
    if (compound.contains(NBT_SORT)) {
      setSort(EnumSortType.values()[compound.getInt(NBT_SORT)]);
    }
    ListNBT invList = compound.getList("matrix", Constants.NBT.TAG_COMPOUND);
    matrix = new HashMap<>();
    for (int i = 0; i < invList.size(); i++) {
      CompoundNBT stackTag = invList.getCompound(i);
      int slot = stackTag.getByte("Slot");
      ItemStack s = ItemStack.read(stackTag);
      matrix.put(slot, s);
    }
    super.read(compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound.putBoolean(NBT_DIR, isDownwards());
    compound.putInt(NBT_SORT, getSort().ordinal());
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
    System.out.println("WRITE" + compound);
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
    return new StringTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerNetworkCraftingTable(i, world, pos, playerInventory, playerEntity);
  }
}
