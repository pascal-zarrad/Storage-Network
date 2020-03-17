package com.lothrazar.storagenetwork.item.remote;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.data.DimPos;
import com.lothrazar.storagenetwork.api.data.EnumSortType;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
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
    if (world.getTileEntity(pos) instanceof TileMaster) {
      ItemStack stack = player.getHeldItem(hand);
      CompoundNBT tag = stack.getOrCreateTag();
      tag.putInt("x", pos.getX());
      tag.putInt("y", pos.getY());
      tag.putInt("z", pos.getZ());
      tag.putBoolean("bound", true);
      //set the dimension
      DimensionType dimType = world.getDimension().getType();
      tag.putInt("dim", dimType.getId());
      tag.putString("dimension", dimType.getRegistryName().toString());
      stack.setTag(tag);
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
      int x = tag.getInt("x");
      int y = tag.getInt("y");
      int z = tag.getInt("z");
      int dim = tag.getInt("dim");
      t = new TranslationTextComponent("[ x: " + x + ", y: " + y + ", z: " + z + ", d: " + dim + " ]");
    }
    else {
      t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    }
    t.applyTextStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  public static DimPos getPosStored(ItemStack itemStackIn) {
    if (!itemStackIn.getOrCreateTag().getBoolean("bound")) {
      return null;
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    int x = tag.getInt("x");
    int y = tag.getInt("y");
    int z = tag.getInt("z");
    int dim = tag.getInt("dim");
    BlockPos posTarget = new BlockPos(x, y, z);
    return new DimPos(dim, tag.getString("dimension"), posTarget);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack itemStackIn = player.getHeldItem(hand);
    if (world.isRemote || hand != Hand.MAIN_HAND) {
      //no offhand openings
      return super.onItemRightClick(world, player, hand);
    }
    if (!itemStackIn.getOrCreateTag().getBoolean("bound")) {
      //unbound or invalid data
      StorageNetwork.chatMessage(player, "item.remote.notbound");
      return super.onItemRightClick(world, player, hand);
    }
    CompoundNBT tag = itemStackIn.getOrCreateTag();
    int x = tag.getInt("x");
    int y = tag.getInt("y");
    int z = tag.getInt("z");
    int dim = tag.getInt("dim");
    //assume we are in the same world
    World serverTargetWorld = world;//for now
    if (dim != world.dimension.getType().getId() && tag.contains("dimension")) {
      try {
        String dimension = tag.getString("dimension");
        ResourceLocation res = new ResourceLocation(dimension);
        // ok 
        boolean resetUnloadDelay = true;
        boolean forceLoad = true;
        DimensionType dimFromRemote = DimensionType.byName(res);
        if (dimFromRemote != null) {
          ServerWorld dimWorld = DimensionManager.getWorld(world.getServer(), dimFromRemote, resetUnloadDelay, forceLoad);
          if (dimWorld != null) {
            // found it
            serverTargetWorld = dimWorld.getWorld();
          }
        }
      }
      catch (Exception e) {
        //
        StorageNetwork.log("why is cross dim broken " + e.getLocalizedMessage());
        return super.onItemRightClick(world, player, hand);
      }
    }
    ////    DimensionType type = DimensionManager.getRegistry().getByValue(dim);
    //   World serverTargetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
    //    serverTargetWorld = Minecraft.getInstance().getIntegratedServer().getWorld(type);
    //    System.out.println(type + "?" + serverTargetWorld);
    //
    //
    BlockPos posTarget = new BlockPos(x, y, z);
    if (!serverTargetWorld.isAreaLoaded(posTarget, 1)) {
      StorageNetwork.chatMessage(player, "item.remote.notloaded");
      return super.onItemRightClick(world, player, hand);
    }
    TileEntity tile = serverTargetWorld.getTileEntity(posTarget);
    if (tile instanceof TileMaster) {
      NetworkHooks.openGui((ServerPlayerEntity) player, this);
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
