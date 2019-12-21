package com.lothrazar.storagenetwork.block;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {

  public BaseBlock(Material materialIn, String registryName) {
    super(Block.Properties.create(materialIn).hardnessAndResistance(0.5F).sound(SoundType.STONE));
    setRegistryName(registryName);
  }

  public BaseBlock(Block.Properties prop, String registryName) {
    super(prop);
    setRegistryName(registryName);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable IBlockReader playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.applyTextStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  protected void updateConnection(World worldIn, BlockPos pos, BlockState stateIn) {
    BlockState facingState;
    for (Direction d : Direction.values()) {
      BlockPos facingPos = pos.offset(d);
      facingState = worldIn.getBlockState(facingPos);
      if (facingState.getBlock() instanceof BlockCable) {
        BlockCable c = (BlockCable) facingState.getBlock();
        c.updatePostPlacement(facingState, d.getOpposite(), stateIn, worldIn, facingPos, pos);
      }
    }
  }
}
