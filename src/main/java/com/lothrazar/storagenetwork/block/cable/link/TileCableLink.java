package com.lothrazar.storagenetwork.block.cable.link;

import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableLink extends TileCableWithFacing implements TickableBlockEntity {

  protected CapabilityConnectableLink itemStorage;

  public TileCableLink() {
    super(SsnRegistry.STORAGEKABELTILE);
    this.itemStorage = new CapabilityConnectableLink(this);
  }

  @Override
  public void load(BlockState bs, CompoundTag compound) {
    super.load(bs, compound);
    this.itemStorage.deserializeNBT(compound.getCompound("capability"));
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    CompoundTag result = super.save(compound);
    result.put("capability", itemStorage.serializeNBT());
    return result;
  }

  @Override
  public void setDirection(Direction direction) {
    super.setDirection(direction);
    this.itemStorage.setInventoryFace(direction);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY) {
      LazyOptional<CapabilityConnectableLink> cap = LazyOptional.of(() -> itemStorage);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void tick() {
    super.refreshDirection();
  }
}
