package com.lothrazar.storagenetwork.block;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;

public abstract class BaseBlock extends BaseEntityBlock {

  public BaseBlock(Properties prop) {
    super(prop);
  }

  @Override
  public void appendHoverText(ItemStack stack, BlockGetter playerIn, List<Component> tooltip, TooltipFlag advanced) {
    super.appendHoverText(stack, playerIn, tooltip, advanced);
    tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
  }
}
