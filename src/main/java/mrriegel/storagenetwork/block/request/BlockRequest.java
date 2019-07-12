package mrriegel.storagenetwork.block.request;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.BaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class BlockRequest extends BaseBlock {

  public BlockRequest() {
    super(Material.IRON, "request");
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileRequest();


  }

  @Override
  public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
    StorageNetwork.LOGGER.info("TODO: REQUEST GUI HERE ");
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
      } else {
        throw new IllegalStateException("Our named container provider is missing!");
      }
      return true;
    }
    return super.onBlockActivated(state, world, pos, player, hand, result);
  }
  @Override
  public void addInformation(ItemStack stack, @Nullable IBlockReader playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(new TranslationTextComponent("tooltip.storagenetwork.request"));
  }
}
