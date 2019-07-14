package com.lothrazar.storagenetwork.apiimpl;
import com.lothrazar.storagenetwork.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.api.capability.IConnectable;
import com.lothrazar.storagenetwork.api.data.IItemStackMatcher;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StorageNetworkHelpers {

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

  public static IItemStackMatcher createItemStackMatcher(ItemStack stack, boolean ore, boolean nbt, boolean meta) {
    return new ItemStackMatcher(stack, meta, ore, nbt);
  }
}
