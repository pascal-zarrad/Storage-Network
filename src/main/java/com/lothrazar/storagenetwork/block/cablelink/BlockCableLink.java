package com.lothrazar.storagenetwork.block.cablelink;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockCableLink extends BlockCable {

  public BlockCableLink(String registryName) {
    super(registryName);
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileCableLink();
  }
}
