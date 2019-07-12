package mrriegel.storagenetwork.block.cable;

import mrriegel.storagenetwork.block.TileConnectable;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.nbt.CompoundNBT;
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
