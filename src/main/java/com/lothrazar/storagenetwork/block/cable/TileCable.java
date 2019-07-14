package com.lothrazar.storagenetwork.block.cable;

import com.lothrazar.storagenetwork.registry.ModBlocks;
import com.lothrazar.storagenetwork.block.TileConnectable;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Base class for TileCable
 *
 */
public class TileCable extends TileConnectable {

  public TileCable() {
    super(ModBlocks.kabeltile);
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    double renderExtention = 1.0d;
    AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - renderExtention, pos.getY() - renderExtention, pos.getZ() - renderExtention, pos.getX() + 1 + renderExtention, pos.getY() + 1 + renderExtention, pos.getZ() + 1 + renderExtention);
    return bb;
  }
}
