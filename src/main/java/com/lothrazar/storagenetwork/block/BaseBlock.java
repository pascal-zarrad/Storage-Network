package com.lothrazar.storagenetwork.block;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public abstract class BaseBlock extends ContainerBlock {

  public BaseBlock(Material materialIn, String registryName) {
    super(Block.Properties.create(materialIn).hardnessAndResistance(0.5F).sound(SoundType.STONE));
    setRegistryName(registryName);
  }

  @Override public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
}
