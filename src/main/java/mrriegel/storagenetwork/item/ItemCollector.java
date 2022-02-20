package mrriegel.storagenetwork.item;

import java.util.List;
import javax.annotation.Nullable;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.util.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCollector extends Item {

  public ItemCollector() {
    super();
    this.setCreativeTab(CreativeTab.tab);
    this.setRegistryName("collector_remote");
    this.setTranslationKey(getRegistryName().toString());
    this.setMaxStackSize(1);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
    tooltip.add(I18n.format("item.storagenetwork:collector_remote.tooltip"));
    if (stack.hasTagCompound() && NBTHelper.getBoolean(stack, "bound")) {
      // "Dimension: " +
      tooltip.add(NBTHelper.getInteger(stack, "dim") + ", x: " + NBTHelper.getInteger(stack, "x") + ", y: " + NBTHelper.getInteger(stack, "y") + ", z: " + NBTHelper.getInteger(stack, "z"));
    }
  }

  protected ItemStack findAmmo(EntityPlayer player, Item item) {
    for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
      ItemStack itemstack = player.inventory.getStackInSlot(i);
      if (itemstack.getItem() == item) {
        return itemstack;
      }
    }
    return ItemStack.EMPTY;
  }

  @SubscribeEvent
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    if (event.getEntityLiving() instanceof EntityPlayer &&
        event.getItem() != null &&
        event.getItem().getItem().isEmpty() == false) {
      ItemStack item = event.getItem().getItem();
      EntityPlayer player = (EntityPlayer) event.getEntityLiving();
      World world = player.world;
      //      ItemStack stackThis = this.findAmmo(player, this); 
      ItemStack itemStackIn = this.findAmmo(player, this);
      //      DimPos dp = getPosStored();
      BlockPos targetPos = ItemPicker.getBlockPosStored(itemStackIn);
      int itemStackDim = NBTHelper.getInteger(itemStackIn, "dim");
      World serverTargetWorld;
      if (targetPos != null && !world.isRemote) {
        serverTargetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(itemStackDim);
        //        World serverTargetWorld = DimPos.stringDimensionLookup(dp.getDimension(), world.getMinecraftServer());
        if (serverTargetWorld == null) {
          StorageNetwork.instance.logger.error("Missing dimension key " + itemStackDim);
          return;
        }
        TileEntity tile = serverTargetWorld.getTileEntity(targetPos);
        if (tile instanceof TileMaster) {
          TileMaster network = (TileMaster) tile;
          //
          int countUnmoved = network.insertStack(item.copy(), false);
          if (countUnmoved == 0) {
            item.setCount(0);
            event.getItem().setItem(item);
            world.removeEntity(event.getItem());
          }
        }
      }
    }
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if (world.getTileEntity(pos) instanceof TileMaster) {
      NBTHelper.setInteger(stack, "x", pos.getX());
      NBTHelper.setInteger(stack, "y", pos.getY());
      NBTHelper.setInteger(stack, "z", pos.getZ());
      NBTHelper.setBoolean(stack, "bound", true);
      NBTHelper.setInteger(stack, "dim", world.provider.getDimension());
      NBTHelper.setString(stack, "sort", EnumSortType.NAME.toString());
      return EnumActionResult.SUCCESS;
    }
    return super.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
  }
}
