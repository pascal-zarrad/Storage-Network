package com.lothrazar.storagenetwork.block.cable.storagefilter;

import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockCableFilter extends BlockCable {

  public BlockCableFilter(String registryName) {
    super(registryName);
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileCableFilter();
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult result) {
    if (!world.isRemote) {
      TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof INamedContainerProvider) {
        ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
        player.connection.sendPacket(tile.getUpdatePacket());
        //
        NetworkHooks.openGui(player, (INamedContainerProvider) tile, tile.getPos());
      }
      else {
        throw new IllegalStateException("Our named container provider is missing!" + tile);
      }
    }
    return ActionResultType.SUCCESS;
  }
}
