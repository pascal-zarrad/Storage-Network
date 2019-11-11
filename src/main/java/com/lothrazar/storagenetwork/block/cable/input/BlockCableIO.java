package com.lothrazar.storagenetwork.block.cable.input;

import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockCableIO extends BlockCable {

  public BlockCableIO(String registryName) {
    super(registryName);
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileCableIO();
  }
}
