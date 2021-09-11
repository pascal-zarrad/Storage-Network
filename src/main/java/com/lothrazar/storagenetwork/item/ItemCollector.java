package com.lothrazar.storagenetwork.item;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.util.UtilInventory;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.apache.commons.lang3.tuple.Triple;

public class ItemCollector extends Item {

  public static final String NBT_BOUND = "bound";

  public ItemCollector(Properties properties) {
    super(properties.stacksTo(1));
  }

  protected ItemStack findAmmo(Player player, Item item) {
    //is curios installed? doesnt matter this is safe
    Triple<String, Integer, ItemStack> remote = UtilInventory.getCurioRemote(player, item);
    return remote.getRight();
  }

  // not subscribe, called from SsnEvents.java 
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    if (event.getEntityLiving() instanceof Player &&
        event.getItem() != null &&
        event.getItem().getItem().isEmpty() == false) {
      ItemStack item = event.getItem().getItem();
      Player player = (Player) event.getEntityLiving();
      Level world = player.level;
      DimPos dp = DimPos.getPosStored(this.findAmmo(player, this));
      if (dp != null && !world.isClientSide) {
        ServerLevel serverTargetWorld = DimPos.stringDimensionLookup(dp.getDimension(), world.getServer());
        if (serverTargetWorld == null) {
          StorageNetwork.LOGGER.error("Missing dimension key " + dp.getDimension());
          return;
        }
        BlockEntity tile = serverTargetWorld.getBlockEntity(dp.getBlockPos());
        if (tile instanceof TileMain) {
          TileMain network = (TileMain) tile;
          //
          int countUnmoved = network.insertStack(item, false);
          if (countUnmoved == 0) {
            item.setCount(0);
            event.getItem().setItem(item);
            event.getItem().remove(Entity.RemovalReason.KILLED);
            UtilTileEntity.playSoundFromServer((ServerPlayer) player, SoundEvents.ITEM_PICKUP, 0.2F);
          }
        }
        else {
          StorageNetwork.LOGGER.error("item.remote.notfound");
        }
      }
    }
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
}
