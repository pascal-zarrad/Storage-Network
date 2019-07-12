package mrriegel.storagenetwork.block.cable;
import com.google.common.collect.Maps;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockCable extends ContainerBlock {

  public enum EnumConnectType implements IStringSerializable {
    NONE, CABLE, INVENTORY, BLOCKED;

    public boolean isHollow() {
      return this == NONE || this == BLOCKED;
    }

    @Override
    public String getName() {
      return this.name().toLowerCase();
    }
  }

  public static final EnumProperty DOWN = EnumProperty.create("down", EnumConnectType.class);
  public static final EnumProperty UP = EnumProperty.create("up", EnumConnectType.class);
  public static final EnumProperty NORTH = EnumProperty.create("north", EnumConnectType.class);
  public static final EnumProperty SOUTH = EnumProperty.create("south", EnumConnectType.class);
  public static final EnumProperty WEST = EnumProperty.create("west", EnumConnectType.class);
  public static final EnumProperty EAST = EnumProperty.create("east", EnumConnectType.class);
  public static final Map<Direction, EnumProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (p) -> {
    p.put(Direction.NORTH, NORTH);
    p.put(Direction.EAST, EAST);
    p.put(Direction.SOUTH, SOUTH);
    p.put(Direction.WEST, WEST);
    p.put(Direction.UP, UP);
    p.put(Direction.DOWN, DOWN);
  });

  public BlockCable(String registryName) {
    super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.2F));
    this.setRegistryName(registryName);
    this.setDefaultState(this.stateContainer.getBaseState()
        .with(NORTH, EnumConnectType.NONE).with(EAST, EnumConnectType.NONE)
        .with(SOUTH, EnumConnectType.NONE).with(WEST, EnumConnectType.NONE)
        .with(UP, EnumConnectType.NONE).with(DOWN, EnumConnectType.NONE));
    HugeMushroomBlock x;
    FenceBlock y;
  }

  public BlockRenderType getRenderType(BlockState p_149645_1_) {
    return BlockRenderType.MODEL;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileCable();
  }

  public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
    return super.getExtendedState(state, world, pos);
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
  }
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    EnumProperty property = FACING_TO_PROPERTY_MAP.get(facing);
    if (world.getBlockState(currentPos.offset(facing)).getBlock() instanceof BlockCable) {
      //dont set self to self
      return stateIn.with(property, EnumConnectType.CABLE);
    }
    else if (isValidLinkNeighbor(world, facingState , facing, facingPos)
      //          && stateIn.get(property) != EnumConnectType.INVENTORY
    ) {
      return stateIn.with(property, EnumConnectType.INVENTORY);
    }
    else {
      return stateIn.with(property, EnumConnectType.NONE);
    }

  }



  protected boolean isValidLinkNeighbor(IWorldReader world, BlockState facingState, Direction facing, BlockPos facingPos) {
    if (facing == null) {
      return false;
    }
    if (!TileMaster.isTargetAllowed( facingState)) {
      return false;
    }
    TileEntity neighbor = world.getTileEntity(facingPos.offset(facing));
    if (neighbor != null
        && neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()) != null) {
      return true;
    }
    return false;
  }
}
