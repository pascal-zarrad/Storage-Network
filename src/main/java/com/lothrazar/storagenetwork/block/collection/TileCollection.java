package com.lothrazar.storagenetwork.block.collection;

import javax.annotation.Nonnull;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileCollection extends TileConnectable implements INamedContainerProvider {

  private CollectionItemStackHandler itemHandler;

  public TileCollection() {
    super(SsnRegistry.collectortile);
    itemHandler = new CollectionItemStackHandler();
    itemHandler.tile = this;
  }

  @Override
  public void read(BlockState bs, CompoundNBT compound) {
    super.read(bs, compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    return super.write(compound);
  }

  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ContainerCollectionFilter(i, world, pos, playerInventory, playerEntity);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new StringTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      IConnectable capabilityConnectable = this.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, side).orElse(null);
      if (capabilityConnectable != null && getMain() != null) {
        //        //
        //        DimPos mainpos = capabilityConnectable.getMainPos();// this.getMain();
        TileMain tileMain = getMain().getTileEntity(TileMain.class);
        itemHandler.setMain(tileMain);
      }
      else {
        StorageNetwork.log("Found NO CAP!!! for collector ");
      }
      return LazyOptional.of(new NonNullSupplier<T>() {

        public @Override @Nonnull T get() {
          return (T) itemHandler;
        }
      });
    }
    return super.getCapability(cap, side);
  }
}
