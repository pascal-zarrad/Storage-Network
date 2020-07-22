package com.lothrazar.storagenetwork.block;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.CapabilityConnectable;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Base class for Cable, Control, Request
 */
public class TileConnectable extends TileEntity {

  private final CapabilityConnectable connectable;

  public TileConnectable(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
    connectable = new CapabilityConnectable();
  }

  @Override
  public void setPos(BlockPos posIn) {
    super.setPos(posIn);
    //   StorageNetwork.log("TILE CONNECTABLE :: SET POS on the capability" + posIn + "?" + world);
    connectable.setPos(new DimPos(world, pos));
  }

  @Override
  public void read(BlockState bs, CompoundNBT compound) {
    if (compound.contains("connectable")) {
      connectable.deserializeNBT(compound.getCompound("connectable"));
    }
    super.read(bs, compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound.put("connectable", connectable.serializeNBT());
    return super.write(compound);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT syncData = new CompoundNBT();
    write(syncData);
    return new SUpdateTileEntityPacket(pos, 0, syncData);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    read(this.getBlockState(), pkt.getNbtCompound());
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    if (StorageNetwork.config.doReloadOnChunk() && connectable != null && connectable.getMainPos() != null) {
      try {
        TileMain maybe = UtilTileEntity.getTileMainForConnectable(connectable);
        if (maybe != null) {
          maybe.refreshNetwork();
        }
      }
      catch (Exception e) {
        StorageNetwork.LOGGER.info("Error on chunk unload " + e);
      }
    }
  }

  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_CAPABILITY) {
      LazyOptional<CapabilityConnectable> cap = LazyOptional.of(() -> connectable);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  public DimPos getMain() {
    return connectable.getMainPos();
  }
}
