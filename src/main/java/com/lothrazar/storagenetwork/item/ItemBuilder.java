package com.lothrazar.storagenetwork.item;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;

public class ItemBuilder extends Item {

  public static final String NBTBLOCKSTATE = "blockstate";
  public static final String NBT_Z = "Z";
  public static final String NBT_Y = "Y";
  public static final String NBT_X = "X";
  public static final String NBT_DIM = "dimension";
  public static final String NBT_BOUND = "bound";

  public ItemBuilder(Properties properties) {
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

  public static void setBlockState(ItemStack wand, BlockState target) {
    CompoundNBT encoded = NBTUtil.writeBlockState(target);
    wand.getOrCreateTag().put(NBTBLOCKSTATE, encoded);
  }

  public static BlockState getBlockState(ItemStack wand) {
    if (!wand.getOrCreateTag().contains(NBTBLOCKSTATE)) {
      return null;
    }
    return NBTUtil.readBlockState(wand.getOrCreateTag().getCompound(NBTBLOCKSTATE));
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    Hand hand = context.getHand();
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    PlayerEntity player = context.getPlayer();
    BlockPos buildAt = pos.offset(context.getFace());
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
    else if (world.isAirBlock(buildAt) || world.getBlockState(buildAt).getMaterial().isLiquid()) {
      player.swingArm(hand);
      ItemStack stack = player.getHeldItem(hand);
      //succeed or fail
      DimPos dp = getPosStored(stack);
      if (dp != null && hand == Hand.MAIN_HAND && !world.isRemote) {
        ServerWorld serverTargetWorld = DimPos.stringDimensionLookup(dp.getDimension(), world.getServer());
        if (serverTargetWorld == null) {
          StorageNetwork.LOGGER.error("Missing dimension key " + dp.getDimension());
          return ActionResultType.PASS;
        }
        TileEntity tile = serverTargetWorld.getTileEntity(dp.getBlockPos());
        BlockState targetState = ItemBuilder.getBlockState(stack);
        if (tile instanceof TileMain && targetState != null) {
          TileMain network = (TileMain) tile;
          BlockState bs = world.getBlockState(pos);
          ItemStackMatcher matcher = new ItemStackMatcher(new ItemStack(targetState.getBlock()), false, false);
          ItemStack found = network.request(matcher, 1, true);
          //SIMULATED, see if materials are available
          if (!found.isEmpty()) {
            // yes materials are available
            boolean success = placeStateSafe(world, player, buildAt, bs);
            if (success) {
              network.request(matcher, 1, false);
              //NOT SIMULATED, extract item from network
            }
          }
          else {
            player.sendStatusMessage(new TranslationTextComponent("item.remote.notfound.item"), true);
          }
        }
        else {
          player.sendStatusMessage(new TranslationTextComponent("item.remote.notfound"), true);
        }
      }
    }
    //else something non-air and non-liquid is in the way, flower etc
    return ActionResultType.PASS;
  }

  private boolean placeStateSafe(World world, PlayerEntity player, BlockPos placePos, BlockState placeState) {
    BlockState stateHere = world.getBlockState(placePos);
    if (stateHere.getBlock() == Blocks.AIR || stateHere.getMaterial().isLiquid()) {
      return world.setBlockState(placePos, placeState, 3);
    }
    return false;
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
      t.mergeStyle(TextFormatting.DARK_GRAY);
      tooltip.add(t);
    }
    else {
      t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
      t.mergeStyle(TextFormatting.GRAY);
      tooltip.add(t);
    }
    BlockState target = ItemBuilder.getBlockState(stack);
    if (target != null) {
      String block = target.getBlock().getTranslationKey();
      tooltip.add(new TranslationTextComponent(TextFormatting.AQUA + UtilTileEntity.lang(block)));
    }
    else {
      t = new TranslationTextComponent(getTranslationKey() + ".blockstate");
      t.mergeStyle(TextFormatting.DARK_GRAY);
      tooltip.add(t);
    }
  }

  public static DimPos getPosStored(ItemStack itemStackIn) {
    if (!itemStackIn.getOrCreateTag().getBoolean(NBT_BOUND)) {
      return null;
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    return new DimPos(tag);
  }

  public static void onLeftClickBlock(LeftClickBlock event) {
    PlayerEntity player = event.getPlayer();
    ItemStack held = player.getHeldItem(event.getHand());
    if (held.isEmpty()) {
      return;
    }
    World world = player.getEntityWorld();
    StorageNetwork.log(SsnRegistry.BUILDER_REMOTE + " bs builder saved " + held);
    if (held.getItem() == SsnRegistry.BUILDER_REMOTE) {
      // && player.isCrouching()
      BlockState target = world.getBlockState(event.getPos());
      ItemBuilder.setBlockState(held, target);
      StorageNetwork.log("bs builder saved " + target);
      event.setResult(Result.DENY);
    }
  }
}
