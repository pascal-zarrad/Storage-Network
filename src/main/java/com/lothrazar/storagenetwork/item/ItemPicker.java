package com.lothrazar.storagenetwork.item;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
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

  public static final String NBT_Z = "Z";
  public static final String NBT_Y = "Y";
  public static final String NBT_X = "X";
  public static final String NBT_DIM = "dimension";
  public static final String NBT_BOUND = "bound";

  public ItemPicker(Properties properties) {
    super(properties.maxStackSize(1));
  }

  public static void putPos(ItemStack stack, BlockPos pos) {
    CompoundNBT tag = stack.getOrCreateTag();
    tag.putInt(NBT_X, pos.getX());
    tag.putInt(NBT_Y, pos.getY());
    tag.putInt(NBT_Z, pos.getZ());
  }

  public static BlockPos getPos(ItemStack stack) {
    return null;
  }

  public static String getDim(ItemStack stack) {
    return stack.getOrCreateTag().getString(NBT_DIM);
  }

  public static void putDim(ItemStack stack, World world) {
    stack.getOrCreateTag().putString(NBT_DIM, DimPos.dimensionToString(world));
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    Hand hand = context.getHand();
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    PlayerEntity player = context.getPlayer();
    if (world.getTileEntity(pos) instanceof TileMain) {
      ItemStack stack = player.getHeldItem(hand);
      CompoundNBT tag = stack.getOrCreateTag();
      putPos(stack, pos);
      tag.putBoolean(NBT_BOUND, true);
      putDim(stack, world);
      stack.setTag(tag);
      UtilTileEntity.statusMessage(player, "item.remote.connected");
      return ActionResultType.SUCCESS;
    }
    else {
      ItemStack stack = player.getHeldItem(hand);
      DimPos dp = getPosStored(stack);
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
            StorageNetwork.log("Found " + found);
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
        else {//no main
          player.sendStatusMessage(new TranslationTextComponent("item.remote.notfound"), true);
        }
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t;
    if (stack.hasTag()) {
      CompoundNBT tag = stack.getOrCreateTag();
      int x = tag.getInt(NBT_X);
      int y = tag.getInt(NBT_Y);
      int z = tag.getInt(NBT_Z);
      String dim = tag.getString(NBT_DIM);
      t = new TranslationTextComponent("[" + x + ", " + y + ", " + z + ", " + dim + "]");
      t.mergeStyle(TextFormatting.GRAY);
      tooltip.add(t);
    }
    t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  public static DimPos getPosStored(ItemStack itemStackIn) {
    if (!itemStackIn.getOrCreateTag().getBoolean(NBT_BOUND)) {
      return null;
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    return new DimPos(tag);
  }
}
