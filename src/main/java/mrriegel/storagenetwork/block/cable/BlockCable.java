package mrriegel.storagenetwork.block.cable;

import com.google.common.collect.Maps;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraft.tileentity.TileEntity;

import java.util.Map;

public class BlockCable extends Block {
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
  public static final  EnumProperty DOWN= EnumProperty.create("down", EnumConnectType.class);
  public static final  EnumProperty UP= EnumProperty.create("up", EnumConnectType.class);
  public static final  EnumProperty NORTH= EnumProperty.create("north", EnumConnectType.class);
  public static final  EnumProperty SOUTH= EnumProperty.create("south", EnumConnectType.class);
  public static final  EnumProperty WEST= EnumProperty.create("west", EnumConnectType.class);
  public static final  EnumProperty EAST= EnumProperty.create("east", EnumConnectType.class);

  public static final Map<Direction, EnumProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (p) -> {
    p.put(Direction.NORTH, NORTH);
    p.put(Direction.EAST, EAST);
    p.put(Direction.SOUTH, SOUTH);
    p.put(Direction.WEST, WEST);
    p.put(Direction.UP, UP);
    p.put(Direction.DOWN, DOWN);
  });
//  protected static final Map<Direction, EnumProperty<EnumConnectType>> PROPERTIES = Maps.newEnumMap(
//      new ImmutableMap.Builder<Direction, EnumProperty<EnumConnectType>>()
//          .put(Direction.DOWN,DOWN)
//          .put(Direction.UP, UP)
//          .put(Direction.NORTH, NORTH)
//          .put(Direction.SOUTH, SOUTH)
//          .put(Direction.WEST, WEST)
//          .put(Direction.EAST, EAST)
//          .build());

 public BlockCable(String registryName) {

   super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.2F));
   this.setRegistryName(registryName);

   this.setDefaultState(this.stateContainer.getBaseState()
       .with(NORTH, EnumConnectType.NONE).with(EAST, EnumConnectType.NONE)
       .with(SOUTH, EnumConnectType.NONE).with(WEST, EnumConnectType.NONE)
       .with(UP, EnumConnectType.NONE).with(DOWN,EnumConnectType.NONE));


   HugeMushroomBlock x;
   FenceBlock y;
  }

  @Override public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }


  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
   super.fillStateContainer(builder);
    builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);


  }
  @Override
  public void onNeighborChange(BlockState stateIn, IWorldReader world, BlockPos pos, BlockPos facingState) {
  super.onNeighborChange(stateIn, world, pos, facingState);
  if (!(world instanceof ServerWorld)) {
    return;
  }
  StorageNetwork.LOGGER.info("nb change"+pos);

 Direction found =  findNewDirection(world, pos);
    StorageNetwork.LOGGER.info("FOUND  change"+found);

((ServerWorld) world).setBlockState(pos, stateIn.with(FACING_TO_PROPERTY_MAP.get(found),EnumConnectType.INVENTORY));
 }

  public Direction findNewDirection(IWorldReader worldIn, BlockPos pos) {
//    if (isValidLinkNeighbor()) {// for myslef?
//      return;
//    }
    for (Direction facing : Direction.values()) {
      if (isValidLinkNeighbor(worldIn,pos, facing)) {
//        setDirection(facing);

        return facing ;
      }
    }
//    setDirection(null);
    return null;
  }
  protected boolean isValidLinkNeighbor(IWorldReader world,BlockPos pos,Direction facing) {
    if (facing == null) {
      return false;
    }
    if (!TileMaster.isTargetAllowed(world.getBlockState(pos.offset(facing)))) {
      return false;
    }
    TileEntity neighbor = world.getTileEntity(pos.offset(facing));
    if (neighbor != null
        && neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()) != null) {
      return true;
    }
    return false;
  }


}
