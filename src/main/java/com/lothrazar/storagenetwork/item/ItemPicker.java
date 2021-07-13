package com.lothrazar.storagenetwork.item;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPicker extends Item {

  public static final String NBT_BOUND = "bound";

  public ItemPicker(Properties properties) {
    super(properties.maxStackSize(1));
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    Hand hand = context.getHand();
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    PlayerEntity player = context.getPlayer();
    if (world.getTileEntity(pos) instanceof TileMain) {
      ItemStack stack = player.getHeldItem(hand);
      DimPos.putPos(stack, pos, world);
      UtilTileEntity.statusMessage(player, "item.remote.connected");
      return ActionResultType.SUCCESS;
    }
    else {
      ItemStack stack = player.getHeldItem(hand);
      DimPos dp = DimPos.getPosStored(stack);
      if (dp != null && hand == Hand.MAIN_HAND && !world.isRemote) {
        ServerWorld serverTargetWorld = DimPos.stringDimensionLookup(dp.getDimension(), world.getServer());
        if (serverTargetWorld == null) {
          StorageNetwork.LOGGER.error("Missing dimension key " + dp.getDimension());
          return ActionResultType.PASS;
        }
        TileEntity tile = serverTargetWorld.getTileEntity(dp.getBlockPos());
        if (tile instanceof TileMain) {
          TileMain network = (TileMain) tile;
          BlockState bs = world.getBlockState(pos);
          ItemStackMatcher matcher = new ItemStackMatcher(new ItemStack(bs.getBlock()), false, false);
          int size = player.isCrouching() ? 1 : 64;
          ItemStack found = network.request(matcher, size, false);
          if (!found.isEmpty()) {
            player.sendStatusMessage(new TranslationTextComponent("item.remote.found"), true);
            //using add will bypass the collector so try if possible
            if (!player.addItemStackToInventory(found)) {
              player.entityDropItem(found);
            }
          }
          else {
            player.sendStatusMessage(new TranslationTextComponent("item.remote.notfound.item"), true);
          }
        }
        else {
          //no main
          player.sendStatusMessage(new TranslationTextComponent("item.remote.notfound"), true);
        }
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t;
    t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
    if (stack.hasTag()) {
      DimPos dp = DimPos.getPosStored(stack);
      tooltip.add(dp.makeTooltip());
    }
  }
}
