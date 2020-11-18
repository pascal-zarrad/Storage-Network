package mrriegel.storagenetwork.block.inventory;

import mrriegel.storagenetwork.block.TileConnectable;
import mrriegel.storagenetwork.data.EnumSortType;
import net.minecraft.nbt.NBTTagCompound;

public class TileInventory extends TileConnectable {

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    setDownwards(compound.getBoolean("dir"));
    setSort(EnumSortType.valueOf(compound.getString("sort")));
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    compound.setBoolean("dir", isDownwards());
    compound.setString("sort", getSort().toString());
    return compound;
  }
}
