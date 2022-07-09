package com.lothrazar.storagenetwork.block;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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
    tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
  }
}
