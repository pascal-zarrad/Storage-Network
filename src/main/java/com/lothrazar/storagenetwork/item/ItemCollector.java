package com.lothrazar.storagenetwork.item;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.util.UtilInventory;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.apache.commons.lang3.tuple.Triple;

public class ItemCollector extends Item {

  public static final String NBT_BOUND = "bound";

  public ItemCollector(Properties properties) {
    super(properties.maxStackSize(1));
  }

  protected ItemStack findAmmo(PlayerEntity player, Item item) {
    //is curios installed? doesnt matter this is safe
    Triple<String, Integer, ItemStack> remote = UtilInventory.getCurioRemote(player, item);
    return remote.getRight();
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
      DimPos dp = DimPos.getPosStored(this.findAmmo(player, this));
      StorageNetwork.log("dp collector " + dp);
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
    return ActionResultType.PASS;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
    if (stack.hasTag()) {
      DimPos dp = DimPos.getPosStored(stack);
      tooltip.add(dp.makeTooltip());
    }
  }
}
