package com.lothrazar.storagenetwork.block.cable;

import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for TileCable
 *
 */
public class TileCable extends TileConnectable {

  public TileCable(BlockPos pos, BlockState state) {
    super(SsnRegistry.Tiles.KABEL.get(), pos, state);
  }
}
