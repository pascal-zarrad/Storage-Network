package com.lothrazar.storagenetwork.block.exchange;

import com.lothrazar.storagenetwork.block.BaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockExchange extends BaseBlock {

  public BlockExchange() {
    super(Material.IRON, "exchange");
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileExchange();
  }
}
