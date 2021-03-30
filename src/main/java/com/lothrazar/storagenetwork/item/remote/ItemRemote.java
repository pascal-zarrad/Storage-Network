package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
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
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemRemote extends Item implements INamedContainerProvider {

  public static final String NBT_Z = "Z";
  public static final String NBT_Y = "Y";
  public static final String NBT_X = "X";
  public static final String NBT_DIM = "dimension";
  public static final String NBT_BOUND = "bound";
  public static final String NBT_SORT = "sort";
  public static final String NBT_DOWN = "down";

  public ItemRemote(Properties properties) {
    super(properties.maxStackSize(1));
  }

  public static boolean getDownwards(ItemStack stack) {
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains(NBT_DOWN)) {
      return tag.getBoolean(NBT_DOWN);
    }
    return false;
  }

  public static void setDownwards(ItemStack stack, boolean val) {
    stack.getOrCreateTag().putBoolean(NBT_DOWN, val);
  }

  public static EnumSortType getSort(ItemStack stack) {
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains(NBT_SORT)) {
      int sort = tag.getInt(NBT_SORT);
      return EnumSortType.values()[sort];
    }
    return EnumSortType.NAME;
  }

  public static void setSort(ItemStack stack, EnumSortType val) {
    stack.getOrCreateTag().putInt(NBT_SORT, val.ordinal());
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
    }
    else {
      t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    }
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  public static DimPos getPosStored(ItemStack itemStackIn) {
    //    if (!itemStackIn.getOrCreateTag().getBoolean(NBT_BOUND)) {
    //      return null;
    //    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    return new DimPos(tag);
  }

  public static boolean openRemote(World world, PlayerEntity player, ItemStack itemStackIn, ItemRemote thiss) {
    if (!itemStackIn.getOrCreateTag().getBoolean(NBT_BOUND)) {
      //unbound or invalid data
      UtilTileEntity.statusMessage(player, "item.remote.notconnected");
      return false;
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    int x = tag.getInt(NBT_X);
    int y = tag.getInt(NBT_Y);
    int z = tag.getInt(NBT_Z);
    //assume we are in the same world
    BlockPos posTarget = new BlockPos(x, y, z);
    if (ConfigRegistry.ITEMRANGE.get() != -1) {
      double distance = player.getDistanceSq(posTarget.getX() + 0.5D, posTarget.getY() + 0.5D, posTarget.getZ() + 0.5D);
      if (distance >= ConfigRegistry.ITEMRANGE.get()) {
        UtilTileEntity.statusMessage(player, "item.remote.outofrange");
        return false;
      }
    }
    //else it is -1 so dont even check distance
    //k now server only 
    if (world.isRemote) {
      return false;
    }
    //now check the dimension world
    ServerWorld serverTargetWorld = null;
    if (tag.contains(NBT_DIM)) {
      try {
        serverTargetWorld = DimPos.stringDimensionLookup(tag.getString(NBT_DIM), world.getServer());
        if (serverTargetWorld == null) {
          StorageNetwork.LOGGER.error("Missing dimension key " + tag.getString(NBT_DIM));
        }
      }
      catch (Exception e) {
        //
        StorageNetwork.LOGGER.error("why is cross dim broken for " + tag.getString(NBT_DIM), e);
        return false;
      }
    }
    //now check is the area chunk loaded
    if (!serverTargetWorld.isAreaLoaded(posTarget, 1)) {
      UtilTileEntity.chatMessage(player, "item.remote.notloaded");
      StorageNetwork.LOGGER.info(UtilTileEntity.lang("item.remote.notloaded") + posTarget);
      return false;
    }
    TileEntity tile = serverTargetWorld.getTileEntity(posTarget);
    if (tile instanceof TileMain) {
      NetworkHooks.openGui((ServerPlayerEntity) player, thiss);
      return true;
    }
    else {
      player.sendStatusMessage(new TranslationTextComponent("item.remote.notfound"), true);
      return false;
    }
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    if (hand != Hand.MAIN_HAND) {
      //no offhand openings
      return super.onItemRightClick(world, player, hand);
    }
    ItemStack itemStackIn = player.getHeldItem(hand);
    //
    //
    if (openRemote(world, player, itemStackIn, this)) {
      // ok great 
    }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(this.getTranslationKey());
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    boolean crafting = (this == SsnRegistry.CRAFTING_REMOTE);
    if (crafting) {
      return new ContainerNetworkCraftingRemote(id, inv);
    }
    else {
      return new ContainerNetworkRemote(id, inv);
    }
  }
}
