package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class ItemStorageCraftingRemote extends Item implements MenuProvider {

  public static final String NBT_JEI = TileRequest.NBT_JEI;
  public static final String NBT_BOUND = "bound";
  public static final String NBT_SORT = "sort";
  public static final String NBT_DOWN = "down";

  public ItemStorageCraftingRemote(Properties properties) {
    super(properties.stacksTo(1));
  }

  public static boolean isJeiSearchSynced(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    if (tag.contains(NBT_JEI)) {
      return tag.getBoolean(NBT_JEI);
    }
    return false;
  }

  public static void setJeiSearchSynced(ItemStack stack, boolean val) {
    stack.getOrCreateTag().putBoolean(NBT_JEI, val);
  }

  public static boolean getDownwards(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    if (tag.contains(NBT_DOWN)) {
      return tag.getBoolean(NBT_DOWN);
    }
    return false;
  }

  public static void setDownwards(ItemStack stack, boolean val) {
    stack.getOrCreateTag().putBoolean(NBT_DOWN, val);
  }

  public static EnumSortType getSort(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    if (tag.contains(NBT_SORT)) {
      int sort = tag.getInt(NBT_SORT);
      return EnumSortType.values()[sort];
    }
    return EnumSortType.NAME;
  }

  public static void setSort(ItemStack stack, EnumSortType val) {
    stack.getOrCreateTag().putInt(NBT_SORT, val.ordinal());
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    InteractionHand hand = context.getHand();
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Player player = context.getPlayer();
    if (world.getBlockEntity(pos) instanceof TileMain) {
      ItemStack stack = player.getItemInHand(hand);
      DimPos.putPos(stack, pos, world);
      UtilTileEntity.statusMessage(player, "item.remote.connected");
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
    if (stack.hasTag()) {
      DimPos dp = DimPos.getPosStored(stack);
      tooltip.add(dp.makeTooltip());
    }
  }

  public static boolean openRemote(Level world, Player player, ItemStack itemStackIn, ItemStorageCraftingRemote thiss) {
    DimPos dp = DimPos.getPosStored(itemStackIn);
    if (dp == null) {
      //unbound or invalid data
      UtilTileEntity.statusMessage(player, "item.remote.notconnected");
      return false;
    }
    //assume we are in the same world
    BlockPos posTarget = dp.getBlockPos();
    if (ConfigRegistry.ITEMRANGE.get() != -1) {
      double distance = player.distanceToSqr(posTarget.getX() + 0.5D, posTarget.getY() + 0.5D, posTarget.getZ() + 0.5D);
      if (distance >= ConfigRegistry.ITEMRANGE.get()) {
        UtilTileEntity.statusMessage(player, "item.remote.outofrange");
        return false;
      }
    }
    //else it is -1 so dont even check distance
    //k now server only 
    if (world.isClientSide) {
      return false;
    }
    //now check the dimension world
    ServerLevel serverTargetWorld = null;
    try {
      serverTargetWorld = DimPos.stringDimensionLookup(dp.getDimension(), world.getServer());
      if (serverTargetWorld == null) {
        StorageNetwork.LOGGER.error("Missing dimension key " + dp.getDimension());
      }
    }
    catch (Exception e) {
      //
      StorageNetwork.LOGGER.error("unknown exception on dim " + dp.getDimension(), e);
      return false;
    }
    //now check is the area chunk loaded
    if (!serverTargetWorld.isAreaLoaded(posTarget, 1)) {
      UtilTileEntity.chatMessage(player, "item.remote.notloaded");
      StorageNetwork.LOGGER.info(UtilTileEntity.lang("item.remote.notloaded") + posTarget);
      return false;
    }
    BlockEntity tile = serverTargetWorld.getBlockEntity(posTarget);
    if (tile instanceof TileMain) {
      NetworkHooks.openGui((ServerPlayer) player, thiss);
      return true;
    }
    else {
      player.displayClientMessage(new TranslatableComponent("item.remote.notfound"), true);
      return false;
    }
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    if (hand != InteractionHand.MAIN_HAND) {
      //no offhand openings
      return super.use(world, player, hand);
    }
    ItemStack itemStackIn = player.getItemInHand(hand);
    //
    if (openRemote(world, player, itemStackIn, this)) {
      // ok great 
      return InteractionResultHolder.success(itemStackIn);
    }
    return super.use(world, player, hand);
  }

  @Override
  public Component getDisplayName() {
    return new TranslatableComponent(this.getDescriptionId());
  }

  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    boolean crafting = (this == SsnRegistry.CRAFTING_REMOTE);
    if (crafting) {
      return new ContainerNetworkCraftingRemote(id, inv);
    }
    else {
      return new ContainerNetworkRemote(id, inv);
    }
  }
}
