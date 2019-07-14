package com.lothrazar.storagenetwork.block.cableio;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockCableIO extends BlockCable {

  public BlockCableIO(String registryName) {
    super(registryName);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileCableIO();
  }
}
