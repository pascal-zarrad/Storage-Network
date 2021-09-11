package com.lothrazar.storagenetwork.block.main;

import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.BaseBlock;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class BlockMain extends BaseBlock {

  public BlockMain() {
    super(Material.METAL, "master");
  }

  @Override
  public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(worldIn, pos, state, placer, stack);
    this.updateConnection(worldIn, pos, state);
    if (worldIn.isClientSide) {
      return;
    }
    BlockEntity tileAtPos = worldIn.getBlockEntity(pos);
    if (tileAtPos != null) {
      ((TileMain) tileAtPos).refreshNetwork();
    }
  }

  @Override
  public InteractionResult use(BlockState state, Level worldIn, BlockPos pos,
      Player playerIn, InteractionHand hand, BlockHitResult result) {
    if (worldIn.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    BlockEntity tileHere = worldIn.getBlockEntity(pos);
    if (!(tileHere instanceof TileMain)) {
      return InteractionResult.PASS;
    }
    //    float hitX, float hitY, float hitZ;
    if (hand == InteractionHand.MAIN_HAND && playerIn.getItemInHand(hand).isEmpty()) {
      displayConnections(playerIn, tileHere);
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  private void displayConnections(Player playerIn, BlockEntity tileHere) {
    TileMain tileMain = (TileMain) tileHere;
    int total = tileMain.getConnectablePositions().size();
    if (total == 0) {
      return;
    }
    playerIn.sendMessage(
        new TranslatableComponent(ChatFormatting.LIGHT_PURPLE +
            UtilTileEntity.lang("chat.main.emptyslots") + tileMain.emptySlots()),
        playerIn.getUUID());
    playerIn.sendMessage(new TranslatableComponent(ChatFormatting.DARK_AQUA +
        UtilTileEntity.lang("chat.main.connectables") + total), playerIn.getUUID());
    Map<String, Integer> mapNamesToCount = new HashMap<>();
    Iterator<DimPos> iter = tileMain.getConnectablePositions().iterator();
    Block bl;
    DimPos p;
    String blockName;
    while (iter.hasNext()) {
      p = iter.next();
      bl = p.getBlockState().getBlock();
      //getTranslatedName client only thanks mojang lol
      blockName = (new TranslatableComponent(bl.getDescriptionId())).getString();
      int count = mapNamesToCount.get(blockName) != null ? (mapNamesToCount.get(blockName) + 1) : 1;
      mapNamesToCount.put(blockName, count);
    }
    List<Entry<String, Integer>> listDisplayStrings = Lists.newArrayList();
    for (Entry<String, Integer> e : mapNamesToCount.entrySet()) {
      listDisplayStrings.add(e);
    }
    Collections.sort(listDisplayStrings, new Comparator<Entry<String, Integer>>() {

      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return Integer.compare(o2.getValue(), o1.getValue());
      }
    });
    for (Entry<String, Integer> e : listDisplayStrings) {
      playerIn.sendMessage(new TranslatableComponent(ChatFormatting.AQUA + "    " + e.getValue() + ": " + e.getKey()), playerIn.getUUID());
    }
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, SsnRegistry.MAINTILEENTITY, world.isClientSide ? TileMain::clientTick : TileMain::serverTick);
  }
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileMain(pos, state);
  }
}
