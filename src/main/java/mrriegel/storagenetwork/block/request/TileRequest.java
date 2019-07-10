package mrriegel.storagenetwork.block.request;
import mrriegel.storagenetwork.block.TileConnectable;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class TileRequest extends TileConnectable {

  Map<Integer, ItemStack> matrix = new HashMap<>();
  private boolean downwards;
  private EnumSortType sort = EnumSortType.NAME;

  public TileRequest() {
    super(ModBlocks.requesttile);
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
}
