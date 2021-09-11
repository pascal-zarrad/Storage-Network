package com.lothrazar.storagenetwork.block;

import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.EnumConnectType;
import com.lothrazar.storagenetwork.block.main.TileMain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileCableWithFacing extends TileConnectable {

  Direction direction = null;

  public TileCableWithFacing(BlockEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  public Direction getDirection() {
    return direction;
  }

  public BlockPos getFacingPosition() {
    return this.getBlockPos().relative(direction);
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  protected boolean isValidLinkNeighbor(Direction facing) {
    if (facing == null) {
      return false;
    }
    if (!TileMain.isTargetAllowed(level.getBlockState(worldPosition.relative(facing)))) {
      return false;
    }
    BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(facing));
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
        this.setChanged();
        if (previous != direction) {
          TileMain mainNode = getTileMain();
          if (mainNode != null) {
            mainNode.refreshNetwork();
          }
        }
        return;
      }
    }
  }

  public void refreshDirection() {
    if (this.getDirection() == null) {
      this.findNewDirection();
      if (getDirection() != null) {
        BlockState newState = BlockCable.cleanBlockState(this.getBlockState());
        newState = newState.setValue(BlockCable.FACING_TO_PROPERTY_MAP.get(getDirection()), EnumConnectType.INVENTORY);
        level.setBlockAndUpdate(worldPosition, newState);
      }
    }
  }

  public TileMain getTileMain() {
    if (getMain() == null) {
      return null;
    }
    return getMain().getTileEntity(TileMain.class);
  }

  @Override // read
  public void load(BlockState bs, CompoundTag compound) {
    super.load(bs, compound);
    if (compound.contains("direction")) {
      this.direction = Direction.values()[(compound.getInt("direction"))];
    }
    else {
      this.direction = null;
    }
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    if (direction != null) {
      compound.putInt("direction", this.direction.ordinal());
    }
    return super.save(compound);
  }
}
