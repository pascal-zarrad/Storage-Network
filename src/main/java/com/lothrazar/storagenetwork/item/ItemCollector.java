package com.lothrazar.storagenetwork.item;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

public class ItemCollector extends Item {

  public static final String NBT_Z = "Z";
  public static final String NBT_Y = "Y";
  public static final String NBT_X = "X";
  public static final String NBT_DIM = "dimension";
  public static final String NBT_BOUND = "bound";

  public ItemCollector(Properties properties) {
    super(properties.maxStackSize(1));
  }

  protected ItemStack findAmmo(PlayerEntity player, Item item) {
    //is curios installed?
    if (ModList.get().isLoaded("curios")) {
      ImmutableTriple<String, Integer, ItemStack> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(item, Minecraft.getInstance().player).orElse(null);
      if (equipped != null && !equipped.getRight().isEmpty()) {
        ItemStack remote = equipped.getRight();
        //success: try to insert items to network thru this remote
        return remote;
      }
    }
    for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
      ItemStack itemstack = player.inventory.getStackInSlot(i);
      if (itemstack.getItem() == item) {
        return itemstack;
      }
    }
    return ItemStack.EMPTY;
  }

  // not subscribe, called from SsnEvents.java 
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    if (event.getEntityLiving() instanceof PlayerEntity &&
        event.getItem() != null &&
        event.getItem().getItem().isEmpty() == false) {
      ItemStack item = event.getItem().getItem();
      PlayerEntity player = (PlayerEntity) event.getEntityLiving();
      World world = player.world;
      //      ItemStack stackThis = this.findAmmo(player, this);
      DimPos dp = getPosStored(this.findAmmo(player, this));
      if (dp != null && !world.isRemote) {
        ServerWorld serverTargetWorld = DimPos.stringDimensionLookup(dp.getDimension(), world.getServer());
        if (serverTargetWorld == null) {
          StorageNetwork.LOGGER.error("Missing dimension key " + dp.getDimension());
          return;
        }
        TileEntity tile = serverTargetWorld.getTileEntity(dp.getBlockPos());
        if (tile instanceof TileMain) {
          TileMain network = (TileMain) tile;
          //
          int countUnmoved = network.insertStack(item, false);
          if (countUnmoved == 0) {
            // StorageNetwork.log("unmoved is zero so all gone" + item);
            item.setCount(0);
            event.getItem().setItem(item);
            event.getItem().remove();
          }
        }
        else {
          StorageNetwork.log("item.remote.notfound");
        }
      }
    }
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
      t.mergeStyle(TextFormatting.GRAY);
      tooltip.add(t);
    }
    t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  public DimPos getPosStored(ItemStack stack) {
    if (stack.isEmpty() ||
        stack.getItem() != this ||
        !stack.getOrCreateTag().getBoolean(NBT_BOUND)) {
      return null;
    }
    CompoundNBT tag = stack.getOrCreateTag();
    return new DimPos(tag);
  }
}
