package com.lothrazar.storagenetwork.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public abstract class BaseBlock extends Block {

  public BaseBlock(Material materialIn, String registryName) {
    super(Block.Properties.create(materialIn).hardnessAndResistance(0.5F).sound(SoundType.STONE));
    setRegistryName(registryName);
  }
}
