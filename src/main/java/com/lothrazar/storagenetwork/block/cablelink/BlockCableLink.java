package com.lothrazar.storagenetwork.block.cablelink;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockCableLink extends BlockCable {

  public BlockCableLink(String registryName) {
    super(registryName);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileCableLink();
  }
}
