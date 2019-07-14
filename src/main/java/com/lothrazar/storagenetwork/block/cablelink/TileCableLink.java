package com.lothrazar.storagenetwork.block.cablelink;
import javax.annotation.Nullable;

import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.registry.ModBlocks;
import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableLink extends TileCableWithFacing implements ITickableTileEntity {

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

  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY) {
      LazyOptional<CapabilityConnectableLink> cap = LazyOptional.of(() -> itemStorage);
      return (LazyOptional<T>) cap;
    }
    return super.getCapability(capability, facing);
  }

  @Override public void tick() {
    if (this.getDirection() == null) {
      this.findNewDirection();
      if (getDirection() != null) {
        BlockState newState = BlockCable.cleanBlockState(this.getBlockState());
        newState = newState.with(BlockCable.FACING_TO_PROPERTY_MAP.get(getDirection()), BlockCable.EnumConnectType.INVENTORY);
        world.setBlockState(pos, newState);
      }
    }
  }
}
