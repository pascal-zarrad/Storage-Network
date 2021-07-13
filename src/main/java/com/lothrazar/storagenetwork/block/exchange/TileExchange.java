package com.lothrazar.storagenetwork.block.exchange;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileExchange extends TileConnectable {

  private ExchangeItemStackHandler itemHandler;

  public TileExchange() {
    super(SsnRegistry.EXCHANGETILE);
    itemHandler = new ExchangeItemStackHandler();
  }

  @Override
  public void read(BlockState bs, CompoundNBT compound) {
    super.read(bs, compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    return super.write(compound);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      try {
        IConnectable capabilityConnectable = super.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, side).orElse(null);
        DimPos m = getMain();
        if (capabilityConnectable != null && m != null
            && itemHandler != null &&
            itemHandler.tileMain == null) {
          TileMain tileMain = m.getTileEntity(TileMain.class);
          if (tileMain != null) {
            itemHandler.setMain(tileMain);
          }
        }
        return LazyOptional.of(new NonNullSupplier<T>() {

          public @Override T get() {
            return (T) itemHandler;
          }
        });
      }
      catch (Exception e) {
        StorageNetwork.LOGGER.error("Exchange caught error from a mod", e);
      }
    }
    return super.getCapability(cap, side);
  }
}
