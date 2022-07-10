package com.lothrazar.storagenetwork.block;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.CapabilityConnectable;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Base class for Cable, Control, Request
 */
public class TileConnectable extends BlockEntity {

  private final CapabilityConnectable connectable;

  public TileConnectable(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
    super(tileEntityTypeIn, pos, state);
    connectable = new CapabilityConnectable();
  }

  @Override
  public void setChanged() {
    super.setChanged();
    // super.setPosition(posIn);
    //   StorageNetwork.log("TILE CONNECTABLE :: SET POS on the capability" + posIn + "?" + world);
    connectable.setPos(new DimPos(level, worldPosition));
  }

  @Override
  public void load(CompoundTag compound) {
    if (compound.contains("connectable")) {
      connectable.deserializeNBT(compound.getCompound("connectable"));
    }
    super.load(compound);
  }

  @Override
  public void saveAdditional(CompoundTag compound) {
    compound.put("connectable", connectable.serializeNBT());
    super.saveAdditional(compound);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this); //new ClientboundBlockEntityDataPacket(worldPosition, 0, syncData);
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag updateTag = new CompoundTag();
    this.saveAdditional(updateTag);
    return updateTag;
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    load(pkt.getTag());
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    if (StorageNetworkMod.CONFIG.doReloadOnChunk() && connectable != null && connectable.getMainPos() != null) {
      try {
        TileMain maybe = UtilTileEntity.getTileMainForConnectable(connectable);
        if (maybe != null) {
          maybe.setShouldRefresh();
        }
      }
      catch (Exception e) {
        StorageNetworkMod.LOGGER.info("Error on chunk unload " + e);
      }
    }
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_CAPABILITY) {
      LazyOptional<CapabilityConnectable> cap = LazyOptional.of(() -> connectable);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  public DimPos getMain() {
    if (connectable == null) {
      return null;
    }
    return connectable.getMainPos();
  }
}
