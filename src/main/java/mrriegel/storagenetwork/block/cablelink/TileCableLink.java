package mrriegel.storagenetwork.block.cablelink;
import javax.annotation.Nullable;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.TileCableWithFacing;
import mrriegel.storagenetwork.block.cable.BlockCable;
import mrriegel.storagenetwork.capabilities.CapabilityConnectable;
import mrriegel.storagenetwork.capabilities.CapabilityConnectableLink;
import mrriegel.storagenetwork.capabilities.StorageNetworkCapabilities;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

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
      StorageNetwork.log("Found dir " + getDirection());
      if (getDirection() != null) {
        BlockState newState = BlockCable.emptyBlockState(this.getBlockState());
        newState = newState.with(BlockCable.FACING_TO_PROPERTY_MAP.get(getDirection()), BlockCable.EnumConnectType.CABLE);
        world.setBlockState(pos, newState);
        StorageNetwork.log("Found new state  " + newState);
      }
    }
  }
}
