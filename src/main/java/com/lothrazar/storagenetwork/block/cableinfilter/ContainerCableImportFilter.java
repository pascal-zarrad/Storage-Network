package com.lothrazar.storagenetwork.block.cableinfilter;
import com.lothrazar.storagenetwork.api.capability.IConnectableLink;
import com.lothrazar.storagenetwork.block.cablefilter.TileCableFilter;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.gui.ContainerCable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ContainerCableImportFilter extends ContainerCable {

  public final TileCableImportFilter tile;
  @Nullable
  public CapabilityConnectableLink link;

  @Nullable
  public CapabilityConnectableAutoIO ioStorage;

  public ContainerCableImportFilter(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.filterimportContainer, windowId);
    tile = (TileCableImportFilter) world.getTileEntity(pos);

    //y not both
    this.ioStorage =(CapabilityConnectableAutoIO)
        tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_AUTO_IO, null).orElse(null);



//is null
    //io should hve its own filter
    IConnectableLink rawLink = tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY, null).orElse(null);
//    if (!(rawLink instanceof CapabilityConnectableLink)) {
//      return;
//    }
    this.link = (CapabilityConnectableLink) rawLink;
    this.bindPlayerInvo(playerInv);

  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
    return ItemStack.EMPTY;
  }

  @Override public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
