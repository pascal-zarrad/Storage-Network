package com.lothrazar.storagenetwork.block;

import java.util.List;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public abstract class BaseBlock extends BaseEntityBlock {

  public BaseBlock(Material materialIn, String registryName) {
    super(Block.Properties.of(materialIn).strength(0.5F).sound(SoundType.STONE));
    setRegistryName(registryName);
  }

  public BaseBlock(Block.Properties prop, String registryName) {
    super(prop);
    setRegistryName(registryName);
  }

  @Override
  public void appendHoverText(ItemStack stack, BlockGetter playerIn, List<Component> tooltip, TooltipFlag advanced) {
    super.appendHoverText(stack, playerIn, tooltip, advanced);
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
  }

  protected void updateConnection(Level worldIn, BlockPos pos, BlockState stateIn) {
    BlockState facingState;
    for (Direction d : Direction.values()) {
      BlockPos facingPos = pos.relative(d);
      facingState = worldIn.getBlockState(facingPos);
      if (facingState.getBlock() instanceof BlockCable) {
        BlockCable c = (BlockCable) facingState.getBlock();
        c.updateShape(facingState, d.getOpposite(), stateIn, worldIn, facingPos, pos);
      }
    }
  }
}
