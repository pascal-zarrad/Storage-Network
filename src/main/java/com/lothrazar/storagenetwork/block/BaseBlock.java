package com.lothrazar.storagenetwork.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

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
}
