package mrriegel.storagenetwork.block.request;
import mrriegel.storagenetwork.block.AbstractBlockConnectable;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class BlockRequest extends AbstractBlockConnectable {
  //private static final PropertyDirection FACING = PropertyDirection.create("facing");

  public BlockRequest(String registryName) {
    super(Material.IRON, registryName);
    //    setHardness(3.0F);
    //    setCreativeTab(CreativeTab.tab);
    //    setDefaultState(this.blockState.getBaseState().withProperty(FACING, Direction.NORTH));
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileRequest();
  }
  //  @Override
  //  public EnumBlockRenderType getRenderType(BlockState state) {
  //    return EnumBlockRenderType.MODEL;
  //  }
  //  @Override
  //  public BlockState getStateFromMeta(int meta) {
  //    Direction Direction = Direction.getFront(meta);
  //    if (Direction.getAxis() == Direction.Axis.Y) {
  //      Direction = Direction.NORTH;
  //    }
  //    return getDefaultState().withProperty(FACING, Direction);
  //  }
  //  public static int getMetaFromState(BlockState state) {
  //    return state.getValue(FACING).getIndex();
  //  }
  //  @Override
  //  protected BlockStateContainer createBlockState() {
  //    return new BlockStateContainer(this, new IProperty[] { FACING });
  //  }
  //  @Override
  //  public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
  //    return getDefaultState().withProperty(FACING, facing.getOpposite());
  //  }
  //  public static boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, EntityPlayer playerIn, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
  //    TileEntity tileHere = worldIn.getTileEntity(pos);
  //    if (tileHere == null || !tileHere.hasCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null)) {
  //      return false;
  //    }
  //    IConnectable tile = tileHere.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null);
  //    if (!worldIn.isRemote && tile.getMasterPos() != null) {
  //      playerIn.openGui(StorageNetwork.instance, GuiHandler.GuiIDs.REQUEST.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
  //      return true;
  //    }
  //    return true;
  //  }
  //  @Override
  //  public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
  //    TileEntity tileentity = worldIn.getTileEntity(pos);
  //    if (tileentity instanceof TileRequest) {
  //      TileRequest tile = (TileRequest) tileentity;
  //      for (int i = 0; i < 9; i++) {
  //        UtilTileEntity.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.matrix.get(i));
  //      }
  //    }
  //    super.breakBlock(worldIn, pos, state);
  //  }

  @Override
  public void addInformation(ItemStack stack, @Nullable IBlockReader playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(new TranslationTextComponent("tooltip.storagenetwork.request"));
  }
}
