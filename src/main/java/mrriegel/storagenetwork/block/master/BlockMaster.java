package mrriegel.storagenetwork.block.master;
import com.google.common.collect.Lists;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.capability.IConnectable;
import mrriegel.storagenetwork.api.data.DimPos;
import mrriegel.storagenetwork.block.BaseBlock;
import mrriegel.storagenetwork.capabilities.StorageNetworkCapabilities;
import mrriegel.storagenetwork.util.UtilTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BlockMaster extends BaseBlock {

  public BlockMaster() {
    super(Material.IRON, "master");
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    DimPos masterPos = null;
    if (worldIn.isRemote) {
      return;
    }
    TileEntity tileHere = null;
    IConnectable connect = null;
    for (BlockPos p : UtilTileEntity.getSides(pos)) {
      tileHere = worldIn.getTileEntity(p);
      if (tileHere != null) {
        connect = tileHere.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null).orElse(null);
        if (connect != null && connect.getMasterPos() != null && connect.getMasterPos().equals(worldIn, pos)) {
          masterPos = connect.getMasterPos();
          break;
        }
      }
    }
    if (masterPos != null) {
      // we found an existing master on the network, cannot add a new one. so break this one
      //    TileMaster tileMaster = (TileMaster) worldIn.getTileEntity(masterPos);
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
      Block.spawnAsEntity(worldIn, pos, ItemHandlerHelper.copyStackWithSize(stack, 1));
      //      ((TileMaster) worldIn.getTileEntity(masterPos)).refreshNetwork();
    }
    else {//my position is tile so refresh myself
      TileEntity tileAtPos = worldIn.getTileEntity(pos);
      if (tileAtPos != null) {
        ((TileMaster) tileAtPos).refreshNetwork();
      }
    }
  }

  //
  //  @Override
  //  public BlockRenderType getRenderType(BlockState state) {
  //    return BlockRenderType.MODEL;
  //  }
  public static boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, Direction side, BlockRayTraceResult hit) {
    if (worldIn.isRemote) {
      return true;
    }
    TileEntity tileHere = worldIn.getTileEntity(pos);
    if (!(tileHere instanceof TileMaster)) {
      return false;
    }
    //    float hitX, float hitY, float hitZ;
    TileMaster tileMaster = (TileMaster) tileHere;
    playerIn.sendMessage(new TranslationTextComponent(TextFormatting.LIGHT_PURPLE + StorageNetwork.lang("chat.master.emptyslots") + tileMaster.emptySlots()));
    playerIn.sendMessage(new TranslationTextComponent(TextFormatting.DARK_AQUA + StorageNetwork.lang("chat.master.connectables") + tileMaster.getConnectablePositions().size()));
    Map<String, Integer> mapNamesToCount = new HashMap<>();
    Iterator<DimPos> iter = tileMaster.getConnectablePositions().iterator();
    while (iter.hasNext()) {
      DimPos p = iter.next();
      String block = p.getBlockState().getBlock().getRegistryName().toString();
      mapNamesToCount.put(block, mapNamesToCount.get(block) != null ? (mapNamesToCount.get(block) + 1) : 1);
    }
    List<Entry<String, Integer>> listDisplayStrings = Lists.newArrayList();
    for (Entry<String, Integer> e : mapNamesToCount.entrySet()) {
      listDisplayStrings.add(e);
    }
    Collections.sort(listDisplayStrings, new Comparator<Entry<String, Integer>>() {

      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return Integer.compare(o2.getValue(), o1.getValue());
      }
    });
    for (Entry<String, Integer> e : listDisplayStrings) {
      playerIn.sendMessage(new TranslationTextComponent(TextFormatting.AQUA + "    " + e.getKey() + ": " + e.getValue()));
    }
    return false;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable IBlockReader playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(new TranslationTextComponent("tooltip.storagenetwork.master"));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new TileMaster();
  }
}
