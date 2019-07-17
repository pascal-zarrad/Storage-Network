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

}
