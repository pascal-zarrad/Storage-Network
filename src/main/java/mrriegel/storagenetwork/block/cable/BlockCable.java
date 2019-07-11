package mrriegel.storagenetwork.block.cable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import mrriegel.storagenetwork.block.AbstractBlockConnectable;
import mrriegel.storagenetwork.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
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
  }
