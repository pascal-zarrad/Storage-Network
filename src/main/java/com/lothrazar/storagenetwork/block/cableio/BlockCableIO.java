package com.lothrazar.storagenetwork.block.cableio;
import javax.annotation.Nullable;

import com.lothrazar.storagenetwork.api.data.EnumStorageDirection;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
