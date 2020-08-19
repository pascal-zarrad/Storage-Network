package com.lothrazar.storagenetwork.item.remote;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemRemote extends Item implements INamedContainerProvider {

  public ItemRemote(Properties properties) {
    super(properties.maxStackSize(1));
  }

  public static boolean getDownwards(ItemStack stack) {
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains("down")) {
      return tag.getBoolean("down");
    }
    return false;
  }

  public static void setDownwards(ItemStack stack, boolean val) {
    stack.getOrCreateTag().putBoolean("down", val);
  }

  public static EnumSortType getSort(ItemStack stack) {
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains("sort")) {
      int sort = tag.getInt("sort");
      return EnumSortType.values()[sort];
    }
    return EnumSortType.NAME;
  }

  public static void setSort(ItemStack stack, EnumSortType val) {
    stack.getOrCreateTag().putInt("sort", val.ordinal());
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
      tag.putInt("X", pos.getX());
      tag.putInt("Y", pos.getY());
      tag.putInt("Z", pos.getZ());
      tag.putBoolean("bound", true);
      //set the dimension 
      tag.putString("dimension", DimPos.dimensionToString(world));
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
      int x = tag.getInt("X");
      int y = tag.getInt("Y");
      int z = tag.getInt("Z");
      String dim = tag.getString("dimension");
      t = new TranslationTextComponent("[" + x + ", " + y + ", " + z + ", " + dim + "]");
    }
    else {
      t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    }
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  public static DimPos getPosStored(ItemStack itemStackIn) {
    if (!itemStackIn.getOrCreateTag().getBoolean("bound")) {
      return null;
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    return new DimPos(tag);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    if (hand != Hand.MAIN_HAND) {
      //no offhand openings
      return super.onItemRightClick(world, player, hand);
    }
    ItemStack itemStackIn = player.getHeldItem(hand);
    if (!itemStackIn.getOrCreateTag().getBoolean("bound")) {
      //unbound or invalid data
      UtilTileEntity.statusMessage(player, "item.remote.notconnected");
      return super.onItemRightClick(world, player, hand);
    }
    if (world.isRemote) {
      return super.onItemRightClick(world, player, hand);
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    int x = tag.getInt("X");
    int y = tag.getInt("Y");
    int z = tag.getInt("Z");
    //assume we are in the same world
    World serverTargetWorld = world;//for now 
    if (//dim != world.dimension.getType().getId() && 
    tag.contains("dimension")) {
      try {
        serverTargetWorld = DimPos.stringDimensionLookup(tag.getString("dimension"), world.getServer());
        if (serverTargetWorld != null) {
          serverTargetWorld = world;
        }
      }
      catch (Exception e) {
        //
        StorageNetwork.LOGGER.error("why is cross dim broken ", e);
        return super.onItemRightClick(world, player, hand);
      }
    }
    BlockPos posTarget = new BlockPos(x, y, z);
    if (!serverTargetWorld.isAreaLoaded(posTarget, 1)) {
      UtilTileEntity.chatMessage(player, "item.remote.notloaded");
      return super.onItemRightClick(world, player, hand);
    }
    TileEntity tile = serverTargetWorld.getTileEntity(posTarget);
    if (tile instanceof TileMain) {
      NetworkHooks.openGui((ServerPlayerEntity) player, this);
    }
    else {
      //      UtilTileEntity.statusMessage(player, "item.remote.notconnected");
    }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public ITextComponent getDisplayName() {
    TranslationTextComponent t = new TranslationTextComponent(this.getTranslationKey());
    return t;
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    boolean crafting = (this == SsnRegistry.crafting_remote);
    if (crafting)
      return new ContainerNetworkCraftingRemote(id, inv);
    else
      return new ContainerNetworkRemote(id, inv);
  }
}
