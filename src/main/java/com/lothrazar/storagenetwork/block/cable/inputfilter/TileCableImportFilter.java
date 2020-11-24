package com.lothrazar.storagenetwork.block.cable.inputfilter;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.api.EnumStorageDirection;
import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.EnumConnectType;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileCableImportFilter extends TileCableWithFacing implements ITickableTileEntity, INamedContainerProvider {

  protected CapabilityConnectableAutoIO ioStorage;

  public TileCableImportFilter() {
    super(SsnRegistry.filterimportkabeltile);
    this.ioStorage = new CapabilityConnectableAutoIO(this, EnumStorageDirection.IN);
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerCableImportFilter(i, world, pos, playerInventory, playerEntity);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public void setDirection(@Nullable Direction direction) {
    super.setDirection(direction);
    this.ioStorage.setInventoryFace(direction);
  }

  @Override
  public void read(BlockState bs, CompoundNBT compound) {
    this.ioStorage.deserializeNBT(compound.getCompound("ioStorage"));
    ioStorage.upgrades.deserializeNBT(compound.getCompound("upgrades"));
    super.read(bs, compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT result = super.write(compound);
    result.put("ioStorage", this.ioStorage.serializeNBT());
    result.put("upgrades", ioStorage.upgrades.serializeNBT());
    return result;
  }

  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_AUTO_IO) {
      LazyOptional<CapabilityConnectableAutoIO> cap = LazyOptional.of(() -> ioStorage);
      return cap.cast();
    }
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      LazyOptional<IItemHandler> cap = LazyOptional.of(() -> ioStorage.upgrades);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void tick() {
    if (this.getDirection() == null) {
      this.findNewDirection();
      if (getDirection() != null) {
        BlockState newState = BlockCable.cleanBlockState(this.getBlockState());
        newState = newState.with(BlockCable.FACING_TO_PROPERTY_MAP.get(getDirection()), EnumConnectType.CABLE);
        world.setBlockState(pos, newState);
      }
    }
  }
}
