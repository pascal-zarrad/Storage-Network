package com.lothrazar.storagenetwork.block.exchange;

import java.util.List;
import com.lothrazar.storagenetwork.block.BaseBlock;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockExchange extends BaseBlock {

  public BlockExchange() {
    super(Block.Properties.of(Material.METAL).strength(0.5F).sound(SoundType.STONE));
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, SsnRegistry.EXCHANGETILE, world.isClientSide ? TileExchange::clientTick : TileExchange::serverTick);
  }

  @Override
  public void appendHoverText(ItemStack stack, BlockGetter playerIn, List<Component> tooltip, TooltipFlag advanced) {
    super.appendHoverText(stack, playerIn, tooltip, advanced);
    tooltip.add(new TranslatableComponent("[WARNING: laggy on large networks] ").withStyle(ChatFormatting.DARK_GRAY));
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileExchange(pos, state);
  }
}
