package com.lothrazar.storagenetwork.api.data;

import com.google.common.base.Objects;
import com.lothrazar.storagenetwork.StorageNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import javax.annotation.Nullable;

public class DimPos implements INBTSerializable<CompoundNBT> {

  private int dimension;
  private BlockPos pos = new BlockPos(0, 0, 0);
  private World world;

  public DimPos() {}

  public DimPos(CompoundNBT tag) {
    deserializeNBT(tag);
  }

  public DimPos(ByteBuf buf) {
    dimension = buf.readInt();
    pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
  }

  public DimPos(int dimension, BlockPos pos) {
    this.dimension = dimension;
    this.pos = pos;
  }

  public DimPos(World world, BlockPos pos) {
    this.pos = pos;
    this.world = world;
    if (world != null && world.getDimension() != null && world.getDimension().getType() != null)
      dimension = world.getDimension().getType().getId();
  }

  @Nullable
  public World getWorld() {
    //    if (world != null) {
    return world;
    //    }
    //    MinecraftServer x
    //    DimensionManager.getWorld(MinecraftServer.)
    //    return DimensionManager.getWorld(dimension);
  }

  public BlockPos getBlockPos() {
    return pos;
  }

  public BlockState getBlockState() {
    return getWorld().getBlockState(getBlockPos());
  }

  @Nullable
  public <V> V getTileEntity(Class<V> tileEntityClassOrInterface) {
    World world = getWorld();
    if (world == null || getBlockPos() == null) {
      return null;
    }
    TileEntity tileEntity = world.getTileEntity(getBlockPos());
    if (tileEntity == null) {
      return null;
    }
    if (!tileEntityClassOrInterface.isAssignableFrom(tileEntity.getClass())) {
      return null;
    }
    return (V) tileEntity;
  }

  @Nullable
  public <V> V getCapability(Capability<V> capability, Direction side) {
    World world = getWorld();
    if (world == null || getBlockPos() == null) {
      return null;
    }
    TileEntity tileEntity = world.getTileEntity(getBlockPos());
    if (tileEntity == null) {
      return null;
    }
    //    if (!tileEntity.hasCapability(capability, side)) {
    //      return null;
    //    }
    return tileEntity.getCapability(capability, side).orElse(null);
  }

  public boolean isLoaded() {
    if (getWorld() == null) {
      return false;
    }
    return getWorld().isBlockLoaded(pos);
  }

  public boolean equals(World world, BlockPos pos) {
    //    world.dimension
    //    return dimension == world.provider.getDimension() &&
    //
    return pos.equals(this.pos);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DimPos dimPos = (DimPos) o;
    return dimension == dimPos.dimension &&
        Objects.equal(pos, dimPos.pos);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dimension, pos);
  }

  @Override
  public String toString() {
    return "[" +
        "dimension=" + dimension +
        ", pos=" + pos +
        ']';
  }

  public void writeToByteBuf(ByteBuf buf) {
    buf.writeInt(dimension);
    buf.writeInt(pos.getX());
    buf.writeInt(pos.getY());
    buf.writeInt(pos.getZ());
  }

  @Override
  public CompoundNBT serializeNBT() {
    if (pos == null) {
      pos = new BlockPos(0, 0, 0);
    }
    CompoundNBT result = NBTUtil.writeBlockPos(pos);
    //    result.setInteger("Dim", dimension);
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    pos = NBTUtil.readBlockPos(nbt);
    //    dimension = nbt.getInteger("Dim");
  }

  public DimPos offset(Direction direction) {
    if (pos == null || direction == null) {
      StorageNetwork.LOGGER.info("Error: null offset in DimPos " + direction);
      return null;
    }
    return new DimPos(world, pos.offset(direction));
  }

  public IChunk getChunk() {
    return getWorld().getChunk(pos);
  }
}
