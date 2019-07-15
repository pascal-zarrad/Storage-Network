package com.lothrazar.storagenetwork.block.cable;

import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.block.TileConnectable;

/**
 * Base class for TileCable
 *
 */
public class TileCable extends TileConnectable {

  public TileCable() {
    super(SsnRegistry.kabeltile);
  }

//  @Override
//  public AxisAlignedBB getRenderBoundingBox() {
//    double renderExtention = 1.0d;
//    AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - renderExtention, pos.getY() - renderExtention, pos.getZ() - renderExtention, pos.getX() + 1 + renderExtention, pos.getY() + 1 + renderExtention, pos.getZ() + 1 + renderExtention);
//    return bb;
//  }
}
