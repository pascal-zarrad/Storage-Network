package com.lothrazar.storagenetwork.block.collection;

import com.lothrazar.storagenetwork.block.BaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockCollection extends BaseBlock {

  public BlockCollection() {
    super(Material.IRON, "collector");
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileCollection();
  }
}
