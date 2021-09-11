package com.lothrazar.storagenetwork.block.cable.inputfilter;

import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class BlockCableImportFilter extends BlockCable {

  public BlockCableImportFilter(String registryName) {
    super(registryName);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileCableImportFilter(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, SsnRegistry.FILTERIMPORTKABELTILE, world.isClientSide ? TileCableImportFilter::clientTick : TileCableImportFilter::serverTick);
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
