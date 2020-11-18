package mrriegel.storagenetwork.item;

import java.util.List;
import javax.annotation.Nullable;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.data.ItemStackMatcher;
import mrriegel.storagenetwork.util.NBTHelper;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPicker extends Item {

  public ItemPicker() {
    super();
    this.setCreativeTab(CreativeTab.tab);
    this.setRegistryName("picker_remote");
    this.setTranslationKey(getRegistryName().toString());
    this.setMaxStackSize(1);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
    tooltip.add(I18n.format("item.storagenetwork:picker_remote.tooltip"));
    if (stack.hasTagCompound() && NBTHelper.getBoolean(stack, "bound")) {
      // "Dimension: " +
      tooltip.add(NBTHelper.getInteger(stack, "dim") + ", x: " + NBTHelper.getInteger(stack, "x") + ", y: " + NBTHelper.getInteger(stack, "y") + ", z: " + NBTHelper.getInteger(stack, "z"));
    }
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack itemStackIn = player.getHeldItem(hand);
    if (world.getTileEntity(pos) instanceof TileMaster) {
      NBTHelper.setInteger(itemStackIn, "x", pos.getX());
      NBTHelper.setInteger(itemStackIn, "y", pos.getY());
      NBTHelper.setInteger(itemStackIn, "z", pos.getZ());
      NBTHelper.setBoolean(itemStackIn, "bound", true);
      NBTHelper.setInteger(itemStackIn, "dim", world.provider.getDimension());
      NBTHelper.setString(itemStackIn, "sort", EnumSortType.NAME.toString());
      return EnumActionResult.SUCCESS;
    }
    //*** 
    if (world.isRemote || !NBTHelper.getBoolean(itemStackIn, "bound")) {
      //unbound or invalid data
      return EnumActionResult.PASS;
    }
    World serverTargetWorld;
    int x, y, z, itemStackDim;
    BlockPos targetPos;
    try {
      targetPos = getBlockPosStored(itemStackIn);
      itemStackDim = NBTHelper.getInteger(itemStackIn, "dim");
      // validate possible missing data
      if (NBTHelper.getString(itemStackIn, "sort") == null) {
        NBTHelper.setString(itemStackIn, "sort", EnumSortType.NAME.toString());
      }
      serverTargetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(itemStackDim);
    }
    catch (Throwable e) {
      StorageNetwork.instance.logger.error("Invalid remote data " + itemStackIn.getTagCompound(), e);
      return EnumActionResult.PASS;
    }
    if (!serverTargetWorld.getChunk(targetPos).isLoaded()) {
      StorageNetwork.chatMessage(player, "item.storagenetwork.picker_remote.notloaded");
      return EnumActionResult.PASS;
    }
    // first make sure area is loaded, BEFORE getting TE
    TileEntity tile = serverTargetWorld.getTileEntity(targetPos);
    if (tile instanceof TileMaster) {
      TileMaster network = (TileMaster) tile;
      //      boolean isSameDimension = (itemStackDim == world.provider.getDimension());
      //      boolean isWithinRange = (player.getDistance(x, y, z) <= ConfigHandler.rangeWirelessAccessor);
      //
      //
      IBlockState bs = world.getBlockState(pos);
      int damateDroppedBlock = bs.getBlock().damageDropped(bs);
      ItemStackMatcher matcher = new ItemStackMatcher(new ItemStack(bs.getBlock(), 1, damateDroppedBlock), true, false, false);
      int size = player.isSneaking() ? 1 : 64;
      ItemStack found = network.request(matcher, size, false);
      if (!found.isEmpty()) {
        player.sendStatusMessage(new TextComponentTranslation("item.storagenetwork:picker_remote.found", new Object[0]), true);
        //using add will bypass the collector so try if possible
        if (!player.addItemStackToInventory(found)) {
          player.dropItem(found, true);
        }
      }
      else {
        player.sendStatusMessage(new TextComponentTranslation("item.storagenetwork:picker_remote.notfound", new Object[0]), true);
      }
    }
    //***
    return super.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
  }

  public static BlockPos getBlockPosStored(ItemStack itemStackIn) {
    int x;
    int y;
    int z;
    BlockPos targetPos;
    x = NBTHelper.getInteger(itemStackIn, "x");
    y = NBTHelper.getInteger(itemStackIn, "y");
    z = NBTHelper.getInteger(itemStackIn, "z");
    targetPos = new BlockPos(x, y, z);
    return targetPos;
  }
}
