package com.lothrazar.storagenetwork.block.cable.inputfilter;
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

import javax.annotation.Nullable;

public class ContainerCableImportFilter extends ContainerCable {

  public final TileCableImportFilter tile;
  @Nullable
  public CapabilityConnectableAutoIO cap;

  public ContainerCableImportFilter(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.filterimportContainer, windowId);
    tile = (TileCableImportFilter) world.getTileEntity(pos);
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
