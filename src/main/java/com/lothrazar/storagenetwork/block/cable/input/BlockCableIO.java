package com.lothrazar.storagenetwork.block.cable.input;

import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.export.TileCableExport;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class BlockCableIO extends BlockCable {

  public BlockCableIO(String registryName) {
    super(registryName);
  }
  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, SsnRegistry.IMPORTKABELTILE, world.isClientSide ? TileCableIO::clientTick : TileCableIO::serverTick);
  }
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileCableIO(pos, state);
  }
}
