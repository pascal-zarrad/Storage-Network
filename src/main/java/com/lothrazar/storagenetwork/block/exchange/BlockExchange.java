package com.lothrazar.storagenetwork.block.exchange;

import com.lothrazar.storagenetwork.block.BaseBlock;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;

public class BlockExchange extends BaseBlock {

  public BlockExchange() {
    super(Material.METAL, "exchange");
  }

  @Override
  public void appendHoverText(ItemStack stack, BlockGetter playerIn, List<Component> tooltip, TooltipFlag advanced) {
    super.appendHoverText(stack, playerIn, tooltip, advanced);
    tooltip.add(new TranslatableComponent("[WARNING: laggy on large networks] ").withStyle(ChatFormatting.DARK_GRAY));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
    return new TileExchange();
  }
}
