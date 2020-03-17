package com.lothrazar.storagenetwork.item.remote;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.data.DimPos;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.block.request.SlotCraftingNetwork;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.inventory.InventoryCraftingNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;

public class ContainerNetworkCraftingRemote extends ContainerNetwork {

  Map<Integer, ItemStack> matrixStacks = new HashMap<>();
  private final TileMaster master;
  private ItemStack remote;

  public ContainerNetworkCraftingRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.craftingremote, id);
    this.remote = pInv.player.getHeldItem(Hand.MAIN_HAND);
    DimPos dp = ItemRemote.getPosStored(remote);
    TileEntity te = pInv.player.world.getTileEntity(dp.getBlockPos());
    this.master = (TileMaster) te;
    matrix = new InventoryCraftingNetwork(this, matrixStacks);
    this.playerInv = pInv;
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMaster(getTileMaster());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    for (int i = 0; i < matrix.getSizeInventory(); i++) {
      //      me = matrix.getStackInSlot(i);
      //      CompoundNBT here = me.write(new CompoundNBT());
      if (remote.hasTag() && remote.getTag().contains("matrix" + i)) {
        CompoundNBT tag = remote.getTag().getCompound("matrix" + i);
        ItemStack stackSaved = ItemStack.read(tag);
        if (!stackSaved.isEmpty())
          matrix.setInventorySlotContents(i, stackSaved);
      }
    }
    onCraftMatrixChanged(matrix);
  }
  //  @Override
  //  public Slot getSlot(int slotId) {
  //    if (slotId >= this.inventorySlots.size()) {
  //      System.out.println("Where are you coming from");
  //      return null;
  //    }
  //    return super.getSlot(slotId);
  //  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return playerIn.getHeldItem(Hand.MAIN_HAND) == remote;
  }

  @Override
  public TileMaster getTileMaster() {
    return master;
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    if (recipeLocked) {
      StorageNetwork.log("recipe locked so onCraftMatrixChanged cancelled");
      return;
    }
    //    findMatchingRecipe(matrix);
    super.onCraftMatrixChanged(inventoryIn);
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    super.onContainerClosed(playerIn);
    ItemStack me;
    //    CompoundNBT tags = new CompoundNBT();
    for (int i = 0; i < matrix.getSizeInventory(); i++) {
      me = matrix.getStackInSlot(i);
      CompoundNBT here = me.write(new CompoundNBT());
      this.remote.getTag().put("matrix" + i, here);
    }
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean isCrafting() {
    return true;
  }
}
