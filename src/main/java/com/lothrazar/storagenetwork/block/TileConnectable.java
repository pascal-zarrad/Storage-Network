package com.lothrazar.storagenetwork.block;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.data.DimPos;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectable;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.config.ConfigHandler;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

/**
 * Base class for Cable, Control, Request
 */
public class TileConnectable extends TileEntity {

  private final CapabilityConnectable connectable;

  public TileConnectable(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
    connectable = new CapabilityConnectable();
  }

  private DimPos getDimPos() {
    return new DimPos(world, pos);
  }

  @Override
  public void setPos(BlockPos posIn) {
    super.setPos(posIn);
    connectable.setPos(getDimPos());
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("connectable")) {
      connectable.deserializeNBT((CompoundNBT) compound.get("connectable"));
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT result = super.write(compound);
    result.put("connectable", connectable.serializeNBT());
    return result;
  }

//  public static boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
//    return oldState.getBlock() != newSate.getBlock();
//  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT syncData = new CompoundNBT();
    write(syncData);
    return new SUpdateTileEntityPacket(pos, 0, syncData);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    read(pkt.getNbtCompound());
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    if (ConfigHandler.reloadNetworkWhenUnloadChunk && connectable != null && connectable.getMasterPos() != null) {
      try {
        TileMaster maybeMaster = UtilTileEntity.getTileMasterForConnectable(connectable);
        if (maybeMaster != null) {
          maybeMaster.refreshNetwork();
        }
      }
      catch (Exception e) {
        StorageNetwork.LOGGER.info("Error on chunk unload "+ e);
      }
    }
  }

  @Nullable
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_CAPABILITY) {
      LazyOptional<CapabilityConnectable> cap = LazyOptional.of(() -> connectable);
      return (LazyOptional<T>) cap;
    }
    return super.getCapability(capability, facing);
  }

  public DimPos getMaster() {
    return connectable.getMasterPos();
  }
}
