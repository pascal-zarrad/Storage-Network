package com.lothrazar.storagenetwork.block.cable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;

/**
 * SOURCE https://github.com/refinedmods/refinedstorage/commit/a9bfe70587fdea0b5c5c253ede4ae3908793a8b6
 */
public class ShapeCache {

  private static final Map<BlockState, VoxelShape> CACHE = new HashMap<>();

  public static VoxelShape getOrCreate(BlockState state, Function<BlockState, VoxelShape> shapeFactory) {
    return CACHE.computeIfAbsent(state, shapeFactory);
  }
}