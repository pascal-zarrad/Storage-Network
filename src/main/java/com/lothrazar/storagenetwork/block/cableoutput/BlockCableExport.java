package com.lothrazar.storagenetwork.block.cableoutput;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockCableExport extends BlockCable {

  public BlockCableExport(String registryName) {
    super(registryName);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileCableExport();
  }
}
