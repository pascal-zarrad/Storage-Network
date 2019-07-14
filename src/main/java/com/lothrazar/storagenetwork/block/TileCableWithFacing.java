package com.lothrazar.storagenetwork.block;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import com.lothrazar.storagenetwork.block.master.TileMaster;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileCableWithFacing extends TileConnectable {

  @Nullable
  Direction direction = null;

  public TileCableWithFacing(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  public Direction getDirection() {
    return direction;
  }

  public BlockPos getFacingPosition() {
    return this.getPos().offset(direction);
  }

  public void setDirection(@Nullable Direction direction) {
    this.direction = direction;
  }

  protected boolean isValidLinkNeighbor(Direction facing) {
    if (facing == null) {
      return false;
    }
    if (!TileMaster.isTargetAllowed(world.getBlockState(pos.offset(facing)))) {
      return false;
    }
    TileEntity neighbor = world.getTileEntity(pos.offset(facing));
    if (neighbor != null && neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null) != null) {
      return true;
    }
    return false;
  }

  public void findNewDirection() {

//    if (isValidLinkNeighbor(direction)) {
//      return;
//    }
    for (Direction facing : Direction.values()) {
      if (isValidLinkNeighbor(facing)) {
        setDirection(facing);
        return;
      }
    }
    setDirection(null);
  }

  public void rotate() {
    Direction previous = direction;
    List<Direction> targetFaces = Arrays.asList(Direction.values());
    Collections.shuffle(targetFaces);
    for (Direction facing : Direction.values()) {
      if (previous == facing) {
        continue;
      }
      if (isValidLinkNeighbor(facing)) {
        setDirection(facing);
        this.markDirty();
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
  public void read(CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("direction")) {
      this.direction = Direction.values()[(compound.getInt("direction"))];
    }
    else {
      this.direction = null;
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    if (direction != null) {
      compound.putInt("direction", this.direction.ordinal());
    }
    return super.write(compound);
  }
}
