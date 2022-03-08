package com.lothrazar.storagenetwork.api;

import com.google.common.base.Objects;
import com.lothrazar.storagenetwork.StorageNetwork;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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

  public DimPos(World world, BlockPos pos) {
    this.pos = pos;
    this.setWorld(world);
    if (world != null) {
      dimension = dimensionToString(world);
    }
  }

  public static DimPos getPosStored(ItemStack itemStackIn) {
    if (itemStackIn.getTag() == null || !itemStackIn.getOrCreateTag().getBoolean(NBT_BOUND)) {
      return null;
    }
    return new DimPos(itemStackIn.getOrCreateTag());
  }

  public World getWorld() {
    return world;
  }

  public BlockPos getBlockPos() {
    return pos;
  }

  public BlockState getBlockState() {
    return getWorld().getBlockState(getBlockPos());
  }

  public <V> V getTileEntity(Class<V> tileEntityClassOrInterface) {
    return getTileEntity(tileEntityClassOrInterface, getWorld());
  }

  public static String dimensionToString(World w) {
    //example: returns "minecraft:overworld" resource location
    return w.getDimensionKey().getLocation().toString();
  }

  public static final String NBT_Z = "Z";
  public static final String NBT_Y = "Y";
  public static final String NBT_X = "X";
  public static final String NBT_DIM = "dimension";
  public static final String NBT_BOUND = "bound";

  public static void putPos(ItemStack stack, BlockPos pos, World world) {
    CompoundNBT tag = stack.getOrCreateTag();
    tag.putInt(NBT_X, pos.getX());
    tag.putInt(NBT_Y, pos.getY());
    tag.putInt(NBT_Z, pos.getZ());
    tag.putString(NBT_DIM, DimPos.dimensionToString(world));
    tag.putBoolean(NBT_BOUND, true);
  }

  public static String getDim(ItemStack stack) {
    return stack.getOrCreateTag().getString(NBT_DIM);
  }

  public static void putDim(ItemStack stack, World world) {
    stack.getOrCreateTag().putString(NBT_DIM, DimPos.dimensionToString(world));
  }

  public static ServerWorld stringDimensionLookup(String s, MinecraftServer serv) {
    return stringDimensionLookup(ResourceLocation.tryCreate(s), serv);
  }

  public static ServerWorld stringDimensionLookup(ResourceLocation s, MinecraftServer serv) {
    RegistryKey<World> worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, s);
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
    if (dimension != null && world.getServer() != null
        && dimension.isEmpty() == false) {
      ServerWorld dimWorld = stringDimensionLookup(this.dimension, world.getServer());
      //reach across to the other dimension
      if (dimWorld != null) {
        world = dimWorld.getWorld();
      }
      else {
        StorageNetwork.LOGGER.error(" Dimworld NOT FOUND for " + dimension);
      }
    }
    //end refresh srever world
    BlockPos tilePos = getBlockPos();
    if (tilePos == null) {
      return null;
    }
    TileEntity tileEntity = world.getTileEntity(tilePos);
    if (tileEntity == null) {
      return null;
    }
    if (!tileEntityClassOrInterface.isAssignableFrom(tileEntity.getClass())) {
      return null;
    }
    return (V) tileEntity;
  }

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
    if (world == null) {
      return false;
    }
    return world.isBlockLoaded(pos);
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
    result.putString(NBT_DIM, dimension);
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    pos = NBTUtil.readBlockPos(nbt);
    dimension = nbt.getString(NBT_DIM);
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

  public String getDimension() {
    return dimension;
  }

  public ITextComponent makeTooltip() {
    TranslationTextComponent t = new TranslationTextComponent("[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + dimension + "]");
    t.mergeStyle(TextFormatting.DARK_GRAY);
    return t;
  }
}
