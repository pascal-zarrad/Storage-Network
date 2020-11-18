package mrriegel.storagenetwork.block.inventory;

import java.util.List;
import javax.annotation.Nullable;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.capability.IConnectable;
import mrriegel.storagenetwork.block.AbstractBlockConnectable;
import mrriegel.storagenetwork.capabilities.StorageNetworkCapabilities;
import mrriegel.storagenetwork.gui.GuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockInventory extends AbstractBlockConnectable {

  public BlockInventory(String registryName) {
    super(Material.IRON, registryName);
    this.setHardness(3.0F);
    this.setCreativeTab(CreativeTab.tab);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileInventory();
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    TileEntity tileHere = worldIn.getTileEntity(pos);
    if (tileHere == null || !tileHere.hasCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null)) {
      return false;
    }
    IConnectable tile = tileHere.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null);
    if (!worldIn.isRemote && tile.getMasterPos() != null) {
      playerIn.openGui(StorageNetwork.instance, GuiHandler.GuiIDs.INVENTORY.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
      return true;
    }
    return true;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(I18n.format("tile.storagenetwork:inventory.tooltip"));
  }
}
