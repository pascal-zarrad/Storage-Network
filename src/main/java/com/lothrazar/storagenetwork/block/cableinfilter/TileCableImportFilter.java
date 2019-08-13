package com.lothrazar.storagenetwork.block.cableinfilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.api.data.EnumStorageDirection;
import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileCableImportFilter extends TileCableWithFacing implements ITickableTileEntity, INamedContainerProvider {

  private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
  protected CapabilityConnectableAutoIO ioStorage;

  public TileCableImportFilter() {
    super(SsnRegistry.filterimportkabeltile);
    this.ioStorage = new CapabilityConnectableAutoIO(this, EnumStorageDirection.IN);
  }

  private IItemHandler createHandler() {
    return new ItemStackHandler(SsnRegistry.UPGRADE_COUNT) {

      @Override
      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemUpgrade;
      }
    };
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerCableImportFilter(i, world, pos, playerInventory, playerEntity);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new StringTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public void setDirection(@Nullable Direction direction) {
    super.setDirection(direction);
    this.ioStorage.setInventoryFace(direction);
  }

  @Override
  public void read(CompoundNBT compound) {
    this.ioStorage.deserializeNBT(compound.getCompound("ioStorage"));
    handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(compound));
    super.read(compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT result = super.write(compound);
    result.put("ioStorage", this.ioStorage.serializeNBT());
    handler.ifPresent(h -> {
      CompoundNBT cc = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
      result.put("inv", cc);
    });
    return result;
  }

  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_AUTO_IO) {
      LazyOptional<CapabilityConnectableAutoIO> cap = LazyOptional.of(() -> ioStorage);
      return cap.cast();
      //      return (LazyOptional<T>) cap;
    }
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return handler.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void tick() {
    if (this.getDirection() == null) {
      this.findNewDirection();
      if (getDirection() != null) {
        BlockState newState = BlockCable.cleanBlockState(this.getBlockState());
        newState = newState.with(BlockCable.FACING_TO_PROPERTY_MAP.get(getDirection()), BlockCable.EnumConnectType.CABLE);
        world.setBlockState(pos, newState);
      }
    }
  }
}
