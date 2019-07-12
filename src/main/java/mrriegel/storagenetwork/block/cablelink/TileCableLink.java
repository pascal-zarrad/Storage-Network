package mrriegel.storagenetwork.block.cablelink;

import javax.annotation.Nullable;

import mrriegel.storagenetwork.block.TileCableWithFacing;
import mrriegel.storagenetwork.capabilities.CapabilityConnectable;
import mrriegel.storagenetwork.capabilities.CapabilityConnectableLink;
import mrriegel.storagenetwork.capabilities.StorageNetworkCapabilities;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableLink extends TileCableWithFacing {

  protected CapabilityConnectableLink itemStorage;

  public TileCableLink() {
    super(ModBlocks.storagekabeltile);
    this.itemStorage = new CapabilityConnectableLink(this);
//    this.itemStorage.filters.setMatchOreDict(false);
//    this.itemStorage.filters.setMatchMeta(true);
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    this.itemStorage.deserializeNBT(compound.getCompound("itemStorage"));
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT result = super.write(compound);
    result.put("itemStorage", itemStorage.serializeNBT());
    return result;
  }

  @Override
  public void setDirection(@Nullable Direction direction) {
    super.setDirection(direction);
    this.itemStorage.setInventoryFace(direction);
  }
//
//  @Override
//  public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
//    if (capability == StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY) {
//      return true;
//    }
//    return super.hasCapability(capability, facing);
//  }

  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {



    if (capability == StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY) {

      LazyOptional<CapabilityConnectableLink> cap = LazyOptional.of(() -> itemStorage);

      return (LazyOptional<T>) cap;
    }
    return super.getCapability(capability, facing);
  }
}
