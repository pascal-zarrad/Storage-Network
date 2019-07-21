package com.lothrazar.storagenetwork.api.util;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.api.capability.IConnectable;
import com.lothrazar.storagenetwork.api.data.IItemStackMatcher;
import com.lothrazar.storagenetwork.api.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UtilTileEntity {

  private static final Map<String, String> modNamesForIds = new HashMap<>();
  public static final int MOUSE_BTN_LEFT = 0;
  public static final int MOUSE_BTN_RIGHT = 1;
  public static final int MOUSE_BTN_MIDDLE_CLICK = 2;

  public static void init() {
    //    Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
    //    for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
    //      String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
    //      String modName = modEntry.getValue().getName();
    //      modNamesForIds.put(lowercaseId, modName);
    //    }
  }

  /**
   * This can only be called on the server side! It returns the TileMaster tile entity for the given connectable.
   *
   * @param connectable
   * @return
   */
  @Nullable
  public static TileMaster getTileMasterForConnectable(@Nonnull IConnectable connectable) {
    if (connectable == null || connectable.getMasterPos() == null) {
      return null;
    }
    return connectable.getMasterPos().getTileEntity(TileMaster.class);
  }

  public static IItemStackMatcher createItemStackMatcher(ItemStack stack, boolean ore, boolean nbt) {
    return new ItemStackMatcher(stack,  ore, nbt);
  }

  @Nonnull
  public static String getModNameForItem(@Nonnull Object object) {
    ResourceLocation itemResourceLocation;
    if (object instanceof Item) {
      itemResourceLocation = ((Item) object).getRegistryName();
    }
    else if (object instanceof Block) {
      itemResourceLocation = ((Block) object).getRegistryName();
    }
    else {
      return null;
    }
    String modId = itemResourceLocation.getNamespace();
    String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
    String modName = modNamesForIds.get(lowercaseModId);
    if (modName == null) {
      modName = WordUtils.capitalize(modId);
      modNamesForIds.put(lowercaseModId, modName);
    }
    return modName;
  }

  public static <E> boolean contains(List<E> list, E e, Comparator<? super E> c) {
    for (E a : list) {
      if (c.compare(a, e) == 0) {
        return true;
      }
    }
    return false;
  }

  public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
    if (stack == null || stack.isEmpty() || worldIn.isRemote) {
      return;
    }
    float f = 0.1F;
    float f1 = 0.8F;
    float f2 = 0.1F;
    ItemEntity entityitem = new ItemEntity(worldIn, x + f, y + f1, z + f2, stack);
    worldIn.addEntity(entityitem);
  }

  public static List<BlockPos> getSides(BlockPos pos) {
    List<BlockPos> lis = Lists.newArrayList();
    for (Direction face : Direction.values()) {
      lis.add(pos.offset(face));
    }
    return lis;
  }

  public static void updateTile(World world, BlockPos pos) {
    //    if (world == null || world.isRemote || world.getTileEntity(pos) == null || !world.getChunkFromBlockCoords(pos).isLoaded())
    //      return;
    //    WorldServer w = (WorldServer) world;
    //    for (EntityPlayer p : w.playerEntities) {
    //      if (p.getPosition().getDistance(pos.getX(), pos.getY(), pos.getZ()) < 32) {
    //        try {
    //          ((EntityPlayerMP) p).connection.sendPacket(world.getTileEntity(pos).getUpdatePacket());
    //          world.markChunkDirty(pos, world.getTileEntity(pos));
    //        }
    //        catch (Error e) {
    //          StorageNetwork.instance.logger.error("Update Tile error", e);
    //        }
    //      }
    //    }
  }
}
