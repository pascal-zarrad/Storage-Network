package com.lothrazar.storagenetwork.block.cable.export;
import com.lothrazar.storagenetwork.block.cableinfilter.TileCableImportFilter;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.gui.ContainerCable;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ContainerCableExportFilter extends ContainerCable {

  public final TileCableExport tile;


  @Nullable
  public CapabilityConnectableAutoIO link;

  public ContainerCableExportFilter(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.filterexportContainer, windowId);
    tile = (TileCableExport) world.getTileEntity(pos);

    this.link =(CapabilityConnectableAutoIO)
        tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_AUTO_IO, null).orElse(null);

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
