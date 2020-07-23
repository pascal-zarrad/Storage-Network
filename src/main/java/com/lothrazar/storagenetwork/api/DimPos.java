package com.lothrazar.storagenetwork.api;

import javax.annotation.Nullable;
import com.google.common.base.Objects;
import com.lothrazar.storagenetwork.StorageNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class DimPos implements INBTSerializable<CompoundNBT> {

  private String dimension;
  private BlockPos pos = new BlockPos(0, 0, 0);
  private World world;

  public DimPos(CompoundNBT tag) {
    deserializeNBT(tag);
  }
  //  public DimPos(String d, BlockPos pos) {
  //    this.dimension = d;
  //    this.pos = pos;
  //  }

  public DimPos(World world, BlockPos pos) {
    this.pos = pos;
    this.setWorld(world);
    if (world != null) {
      dimension = dimensionToString(world);
    }
  }

  @Nullable
  public World getWorld() {
    return world;
  }

  public BlockPos getBlockPos() {
    return pos;
  }

  public BlockState getBlockState() {
    return getWorld().getBlockState(getBlockPos());
  }

  @Nullable
  public <V> V getTileEntity(Class<V> tileEntityClassOrInterface) {
    return getTileEntity(tileEntityClassOrInterface, getWorld());
  }

  public static String dimensionToString(World w) {
    //example: returns "minecraft:overworld" resource location
    return w.func_234923_W_().func_240901_a_().toString();
  }

  public static ServerWorld stringDimensionLookup(String s, MinecraftServer serv) {
    RegistryKey<World> worldKey = RegistryKey.func_240903_a_(Registry.WORLD_KEY, new ResourceLocation(s));
    if (worldKey == null) {
      return null;
    }
    return serv.getWorld(worldKey);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public <V> V getTileEntity(Class<V> tileEntityClassOrInterface, World world) {
    if (world == null || getBlockPos() == null) {
      return null;
    }
    //refresh server world 
    if (dimension != null && world.getServer() != null) {//&& dim != world.dimension.getType().getId()) {
      ServerWorld dimWorld = stringDimensionLookup(this.dimension, world.getServer());
      //reach across to the other dimension
      //      DimensionType dimTarget = DimensionType.byName(new ResourceLocation(dimension));
      //      boolean resetUnloadDelay = true;
      //      boolean forceLoad = true;
      //      ServerWorld dimWorld = null;//DimensionManager.getWorld(world.getServer(), dimTarget, resetUnloadDelay, forceLoad);
      if (dimWorld != null) {
        world = dimWorld.getWorld();
      }
      else {
        StorageNetwork.log(" Dimworld NOT FOUND for " + dimension);
      }
    }
    //end refresh srever world
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
    return tileEntity.getCapability(capability, side).orElse(null);
  }

  @SuppressWarnings("deprecation")
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
    return dimension.equals(dimPos.dimension) &&
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
        ", world=" + getWorld() +
        ']';
  }

  @Override
  public CompoundNBT serializeNBT() {
    if (pos == null) {
      pos = new BlockPos(0, 0, 0);
    }
    CompoundNBT result = NBTUtil.writeBlockPos(pos);
    result.putString("dimension", dimension);
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    pos = NBTUtil.readBlockPos(nbt);
    dimension = nbt.getString("dimension");
  }

  public DimPos offset(Direction direction) {
    if (pos == null || direction == null) {
      StorageNetwork.LOGGER.info("Error: null offset in DimPos " + direction);
      return null;
    }
    return new DimPos(getWorld(), pos.offset(direction));
  }

  public IChunk getChunk() {
    return getWorld().getChunk(pos);
  }

  public void setWorld(World world) {
    this.world = world;
  }
}
