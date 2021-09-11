package com.lothrazar.storagenetwork.block.cable.input;

import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class BlockCableIO extends BlockCable {

  public BlockCableIO(String registryName) {
    super(registryName);
  }

  @Override
  public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
    return new TileCableIO();
  }
}
