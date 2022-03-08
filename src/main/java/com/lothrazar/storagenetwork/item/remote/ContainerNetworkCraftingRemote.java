package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.block.request.SlotCraftingNetwork;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilInventory;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public class ContainerNetworkCraftingRemote extends ContainerNetwork {

  Map<Integer, ItemStack> matrixStacks = new HashMap<>();
  private TileMain root;
  private ItemStack remote;

  public ContainerNetworkCraftingRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.CRAFTINGREMOTE, id);
    this.player = pInv.player;
    this.world = player.world;
    this.remote = player.getHeldItemMainhand();
    if (this.remote.getItem() != SsnRegistry.CRAFTING_REMOTE) {
      Triple<String, Integer, ItemStack> result = UtilInventory.getCurioRemote(player, SsnRegistry.CRAFTING_REMOTE);
      this.remote = result.getRight();
    }
    DimPos dp = DimPos.getPosStored(remote);
    if (dp == null) {
      StorageNetwork.LOGGER.error("Remote opening with null pos Stored {} ", remote);
    }
    this.root = dp.getTileEntity(TileMain.class, world);
    matrix = new NetworkCraftingInventory(this, matrixStacks);
    this.playerInv = pInv;
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMain(getTileMain());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    onCraftMatrixChanged(matrix);
  }

  public ItemStack getRemote() {
    return remote;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return !remote.isEmpty();
  }

  @Override
  public TileMain getTileMain() {
    if (root == null) {
      DimPos dp = DimPos.getPosStored(remote);
      if (dp != null) {
        root = dp.getTileEntity(TileMain.class, world);
      }
    }
    return root;
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    if (recipeLocked) {
      //      StorageNetwork.log("recipe locked so onCraftMatrixChanged cancelled");
      return;
    }
    //    findMatchingRecipe(matrix);
    super.onCraftMatrixChanged(inventoryIn);
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    super.onContainerClosed(playerIn);
    for (int i = 0; i < matrix.getSizeInventory(); i++) {
      UtilInventory.dropItem(world, playerIn.getPosition(), matrix.getStackInSlot(i));
    }
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean isCrafting() {
    return true;
  }
}
