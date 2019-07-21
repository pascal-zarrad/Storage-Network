package com.lothrazar.storagenetwork.block.cableinfilter;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cableio.TileCableIO;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockCableIOFilter extends BlockCable {

  public BlockCableIOFilter(String registryName) {
    super(registryName);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileCableIOFilter();
  }
}
