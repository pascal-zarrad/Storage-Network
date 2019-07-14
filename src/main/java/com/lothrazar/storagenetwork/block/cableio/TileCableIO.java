package com.lothrazar.storagenetwork.block.cableio;

import javax.annotation.Nullable;

import com.lothrazar.storagenetwork.api.data.EnumStorageDirection;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableIO extends TileCable {

  protected CapabilityConnectableAutoIO ioStorage;

  public TileCableIO() {
    this.ioStorage = new CapabilityConnectableAutoIO(this, EnumStorageDirection.BOTH);
  }


  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    this.ioStorage.deserializeNBT(compound.getCompound("ioStorage"));
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT result = super.write(compound);
    result.put("ioStorage", this.ioStorage.serializeNBT());
    return result;
  }

  public void setDirection(@Nullable Direction direction) {
    this.ioStorage.setInventoryFace(direction);
  }


  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_AUTO_IO) {
      LazyOptional<CapabilityConnectableAutoIO> cap = LazyOptional.of(() -> ioStorage);
      return (LazyOptional<T>) cap;
    }
    return super.getCapability(capability, facing);
  }


}
