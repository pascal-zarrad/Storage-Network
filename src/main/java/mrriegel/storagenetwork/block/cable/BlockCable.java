package mrriegel.storagenetwork.block.cable;
import com.google.common.collect.Maps;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.cablelink.TileCableLink;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.items.CapabilityItemHandler;

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
      return name().toLowerCase();
    }
  }

  private static final EnumProperty DOWN = EnumProperty.create("down", EnumConnectType.class);
  private static final EnumProperty UP = EnumProperty.create("up", EnumConnectType.class);
  private static final EnumProperty NORTH = EnumProperty.create("north", EnumConnectType.class);
  private static final EnumProperty SOUTH = EnumProperty.create("south", EnumConnectType.class);
  private static final EnumProperty WEST = EnumProperty.create("west", EnumConnectType.class);
  private static final EnumProperty EAST = EnumProperty.create("east", EnumConnectType.class);
  private static final Map<Direction, EnumProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (p) -> {
    p.put(Direction.NORTH, NORTH);
    p.put(Direction.EAST, EAST);
    p.put(Direction.SOUTH, SOUTH);
    p.put(Direction.WEST, WEST);
    p.put(Direction.UP, UP);
    p.put(Direction.DOWN, DOWN);
  });
  private static VoxelShape AABB;
  private static VoxelShape AABB_UP;
  private static VoxelShape AABB_DOWN;
  private static VoxelShape AABB_EAST;
  private static VoxelShape AABB_WEST;
  private static VoxelShape AABB_NORTH;
  private static VoxelShape AABB_SOUTH;

  public BlockCable(String registryName) {
    super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.2F));
    setRegistryName(registryName);
    setDefaultState(stateContainer.getBaseState()
        .with(NORTH, EnumConnectType.NONE).with(EAST, EnumConnectType.NONE)
        .with(SOUTH, EnumConnectType.NONE).with(WEST, EnumConnectType.NONE)
        .with(UP, EnumConnectType.NONE).with(DOWN, EnumConnectType.NONE));
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    makeShapes();
    //    Block block = state.getBlock();
    //
    //    if (block == BOPBlocks.desert_grass)
    //    {
    //      return SHORT;
    //    }
    if (state.get(UP).equals(EnumConnectType.CABLE)) {
      return AABB_UP;
    }
    if (state.get(DOWN).equals(EnumConnectType.CABLE)) {
      return AABB_DOWN;
    }
    if (state.get(WEST).equals(EnumConnectType.CABLE)) {
      return AABB_WEST;
    }
    if (state.get(EAST).equals(EnumConnectType.CABLE)) {
      return AABB_EAST;
    }
    if (state.get(NORTH).equals(EnumConnectType.CABLE)) {
      return AABB_NORTH;
    }
    if (state.get(SOUTH).equals(EnumConnectType.CABLE)) {
      return AABB_SOUTH;
    }
    return AABB;
  }

  private static void makeShapes() {
    double top = 16;
    double bot = 0;
    double C = 8;
    double w = 3;
    double sm = C - w;
    double lg = C + w;
    //(double x1, double y1, double z1, double x2, double y2, double z2)
    AABB = Block.makeCuboidShape(sm, sm, sm, lg, lg, lg);
    //Y for updown
    AABB_UP = Block.makeCuboidShape(sm, sm, sm, lg, top, lg);
    AABB_DOWN = Block.makeCuboidShape(sm, bot, sm, lg, lg, lg);
    //Z for n-s
    AABB_NORTH = Block.makeCuboidShape(sm, sm, bot, lg, lg, lg);
    AABB_SOUTH = Block.makeCuboidShape(sm, sm, sm, lg, lg, top);
    //X for e-w
    AABB_WEST = Block.makeCuboidShape(bot, sm, sm, lg, lg, lg);
    AABB_EAST = Block.makeCuboidShape(sm, sm, sm, top, lg, lg);
  }

  @Override public BlockRenderType getRenderType(BlockState p_149645_1_) {
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

  @Override public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
    return super.getExtendedState(state, world, pos);
  }

  @Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    EnumProperty property = FACING_TO_PROPERTY_MAP.get(facing);
    //TODO: api should come back here
    if (facingState.getBlock() instanceof BlockCable
        || facingState.getBlock() == ModBlocks.master
        || facingState.getBlock() == ModBlocks.request) {
      //dont set self to self
      return stateIn.with(property, EnumConnectType.CABLE);
    }
    else if (isValidLinkNeighbor(stateIn, facing, facingState, world, currentPos, facingPos)) {
      return stateIn.with(property, EnumConnectType.INVENTORY);
    }
    else {
      return stateIn.with(property, EnumConnectType.NONE);
    }
  }

  private static boolean isValidLinkNeighbor(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    if (facing == null) {
      return false;
    }
    if (!TileMaster.isTargetAllowed(facingState)) {
      return false;
    }
    TileEntity neighbor = world.getTileEntity(facingPos);
    if (neighbor != null
        && neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()) != null
        && stateIn.getBlock() == ModBlocks.storagekabel) {
      StorageNetwork.LOGGER.info("storage found " + neighbor + " DIR " + facing);
        StorageNetwork.LOGGER.info("storage got a direction");
        TileEntity myself  = world.getTileEntity(currentPos);
        if(myself instanceof TileCableLink){
          TileCableLink link=(TileCableLink) myself;
          StorageNetwork.LOGGER.info(" SET DIR " + facing );
          link.setDirection(facing);
        }
      return true;
    }
    return false;
  }
}
