package mrriegel.storagenetwork.block.cable;
import mrriegel.storagenetwork.block.master.TileMaster;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TileCableWithFacing extends TileCable {

  @Nullable private
  Direction direction = null;

  protected boolean hasDirection() {
    return direction != null;
  }

  public Direction getDirection() {
    return direction;
  }

  protected BlockPos getFacingPosition() {
    return getPos().offset(direction);
  }

  public void setDirection(@Nullable Direction direction) {
    this.direction = direction;
  }

  private boolean isValidLinkNeighbor(Direction facing) {
    if (facing == null) {
      return false;
    }
    if (!TileMaster.isTargetAllowed(world.getBlockState(pos.offset(facing)))) {
      return false;
    }
    TileEntity neighbor = world.getTileEntity(pos.offset(facing));
    IItemHandler cap = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
    if (neighbor != null && cap != null) {
      return true;
    }
    return false;
  }

  void findNewDirection() {
    if (isValidLinkNeighbor(direction)) {
      return;
    }
    for (Direction facing : Direction.values()) {
      if (isValidLinkNeighbor(facing)) {
        setDirection(facing);
        return;
      }
    }
    setDirection(null);
  }

  void rotate() {
    Direction previous = direction;
    List<Direction> targetFaces = Arrays.asList(Direction.values());
    Collections.shuffle(targetFaces);
    for (Direction facing : Direction.values()) {
      if (previous == facing) {
        continue;
      }
      if (isValidLinkNeighbor(facing)) {
        setDirection(facing);
        markDirty();
        if (previous != direction) {
          TileMaster master = getTileMaster();
          if (master != null) {
            master.refreshNetwork();
          }
        }
        return;
      }
    }
  }

  public TileMaster getTileMaster() {
    if (getMaster() == null) {
      return null;
    }
    return getMaster().getTileEntity(TileMaster.class);
  }

  @Override
  public void readFromNBT(CompoundNBT compound) {
    super.readFromNBT(compound);
    if (compound.contains("direction")) {
      direction = Direction.getFront(compound.getInt("direction"));
    }
    else {
      direction = null;
    }
  }

  @Override
  public CompoundNBT writeToNBT(CompoundNBT compound) {
    if (direction != null) {
      compound.putInt("direction", direction.ordinal());
    }
    return super.writeToNBT(compound);
  }
}
