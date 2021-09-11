package com.lothrazar.storagenetwork.block.cable.input;

import com.lothrazar.storagenetwork.api.EnumStorageDirection;
import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.EnumConnectType;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableIO extends TileCableWithFacing {

  protected CapabilityConnectableAutoIO ioStorage;

  public TileCableIO(BlockPos pos, BlockState state) {
    super(SsnRegistry.IMPORTKABELTILE, pos, state);
    this.ioStorage = new CapabilityConnectableAutoIO(this, EnumStorageDirection.IN);
  }

  @Override
  public void setDirection(Direction direction) {
    super.setDirection(direction);
    this.ioStorage.setInventoryFace(direction);
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
    this.ioStorage.deserializeNBT(compound.getCompound("ioStorage"));
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    CompoundTag result = super.save(compound);
    result.put("ioStorage", this.ioStorage.serializeNBT());
    return result;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_AUTO_IO) {
      LazyOptional<CapabilityConnectableAutoIO> cap = LazyOptional.of(() -> ioStorage);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  private void tick() {
    if (this.getDirection() == null) {
      this.findNewDirection();
      if (getDirection() != null) {
        BlockState newState = BlockCable.cleanBlockState(this.getBlockState());
        newState = newState.setValue(BlockCable.FACING_TO_PROPERTY_MAP.get(getDirection()), EnumConnectType.CABLE);
        level.setBlockAndUpdate(worldPosition, newState);
      }
    }
  }

  public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileCableIO tile) {
    tile.tick();
  }

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileCableIO tile) {
    tile.tick();
  }
}
