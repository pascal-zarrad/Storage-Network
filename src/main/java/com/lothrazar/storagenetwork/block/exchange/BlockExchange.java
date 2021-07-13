package com.lothrazar.storagenetwork.block.exchange;

import com.lothrazar.storagenetwork.block.BaseBlock;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class BlockExchange extends BaseBlock {

  public BlockExchange() {
    super(Material.IRON, "exchange");
  }

  @Override
  public void addInformation(ItemStack stack, IBlockReader playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(new TranslationTextComponent("[WARNING: laggy on large networks] ").mergeStyle(TextFormatting.DARK_GRAY));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileExchange();
  }
}
