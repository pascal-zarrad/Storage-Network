package com.lothrazar.storagenetwork.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * SOURCE https://github.com/refinedmods/refinedstorage/commit/a9bfe70587fdea0b5c5c253ede4ae3908793a8b6
 */
public class ShapeCache {

  private static final Map<BlockState, VoxelShape> CACHE = new HashMap<>();

  public static VoxelShape getOrCreate(BlockState state, Function<BlockState, VoxelShape> shapeFactory) {
    return CACHE.computeIfAbsent(state, shapeFactory);
  }
}
