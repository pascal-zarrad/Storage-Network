package com.lothrazar.storagenetwork.block.cable.export;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.block.cable.ContainerCable;
import com.lothrazar.storagenetwork.capabilities.CapabilityConnectableAutoIO;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCableExportFilter extends ContainerCable {

  public final TileCableExport tile;
  @Nullable
  public CapabilityConnectableAutoIO cap;

  public ContainerCableExportFilter(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.filterexportContainer, windowId);
    tile = (TileCableExport) world.getTileEntity(pos);
    tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_AUTO_IO).ifPresent(h -> {
      this.cap = (CapabilityConnectableAutoIO) h;
    });
    tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
      for (int i = 0; i < h.getSlots(); i++) {
        this.addSlot(new SlotItemHandler(h, i, 98 + (i + 0) * sq, 6) {

          @Override
          public int getSlotStackLimit() {
            return 1;
          }

          @Override
          public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof ItemUpgrade;
          }
        });
      }
    });
    this.bindPlayerInvo(playerInv);
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
