package com.lothrazar.storagenetwork.block.cable;

import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;

/**
 * Base class for TileCable
 *
 */
public class TileCable extends TileConnectable {

  public TileCable() {
    super(SsnRegistry.KABELTILE);
  }
}
