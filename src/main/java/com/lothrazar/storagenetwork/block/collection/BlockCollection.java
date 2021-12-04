package com.lothrazar.storagenetwork.block.collection;

import com.lothrazar.storagenetwork.block.BaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockCollection extends BaseBlock {

  public BlockCollection() {
    super(Material.METAL, "collector");
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileCollection(pos, state);
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult result) {
    if (!world.isClientSide) {
      BlockEntity tile = world.getBlockEntity(pos);
      if (tile instanceof MenuProvider) {
        ServerPlayer player = (ServerPlayer) playerIn;
        player.connection.send(tile.getUpdatePacket());
        NetworkHooks.openGui(player, (MenuProvider) tile, tile.getBlockPos());
      }
      else {
        throw new IllegalStateException("Our named container provider is missing!" + tile);
      }
    }
    return InteractionResult.SUCCESS;
  }
}
