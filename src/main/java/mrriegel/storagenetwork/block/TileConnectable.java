package mrriegel.storagenetwork.block;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.data.DimPos;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.capabilities.CapabilityConnectable;
import mrriegel.storagenetwork.capabilities.StorageNetworkCapabilities;
import mrriegel.storagenetwork.config.ConfigHandler;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * Base class for Cable, Control, Request
 *
 */
public class TileConnectable extends TileEntity {

  // TODO: This is only required for backwards compatibility! Remove in 1.13
  private World worldCreate;
  private final CapabilityConnectable connectable;

  public TileConnectable() {
    connectable = new CapabilityConnectable();
  }

  private DimPos getDimPos() {
    return new DimPos(world == null ? worldCreate : world, pos);
  }

  @Override
  public void setPos(BlockPos posIn) {
    super.setPos(posIn);
    connectable.setPos(getDimPos());
  }

  @Override
  protected void setWorldCreate(World worldIn) {
    super.setWorldCreate(worldIn);
    worldCreate = worldIn;
  }

  @Override
  public void readFromNBT(CompoundNBT compound) {
    super.readFromNBT(compound);
    if (compound.hasKey("connectable")) {
      connectable.deserializeNBT(compound.getCompoundTag("connectable"));
    }
  }

  @Override
  public CompoundNBT writeToNBT(CompoundNBT compound) {
    CompoundNBT result = super.writeToNBT(compound);
    result.setTag("connectable", connectable.serializeNBT());
    return result;
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return writeToNBT(new CompoundNBT());
  }

  public static boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
    return oldState.getBlock() != newSate.getBlock();
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT syncData = new CompoundNBT();
    writeToNBT(syncData);
    return new SUpdateTileEntityPacket(pos, 1, syncData);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    readFromNBT(pkt.getNbtCompound());
  }

  @Override
  public void onChunkUnload() {
    if (ConfigHandler.reloadNetworkWhenUnloadChunk && connectable != null && connectable.getMasterPos() != null) {
      try {
        TileMaster maybeMaster = StorageNetwork.helpers.getTileMasterForConnectable(connectable);
        if (maybeMaster != null) {
          maybeMaster.refreshNetwork();
        }
      }
      catch (Exception e) {
        StorageNetwork.LOGGER.error("Error on chunk unload ", e);
      }
    }
  }
  //  @Override
  //  public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
  //    if (capability == StorageNetworkCapabilities.CONNECTABLE_CAPABILITY) {
  //      return true;
  //    }
  //    return super.hasCapability(capability, facing);
  //  }

  @Nullable
  @Override
  public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_CAPABILITY) {
      return (T) connectable;
    }
    return super.getCapability(capability, facing);
  }

  public DimPos getMaster() {
    return connectable.getMasterPos();
  }
}
