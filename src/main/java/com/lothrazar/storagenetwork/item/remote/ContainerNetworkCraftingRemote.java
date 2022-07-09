package com.lothrazar.storagenetwork.item.remote;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.block.request.SlotCraftingNetwork;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ContainerNetworkCraftingRemote extends ContainerNetwork {

  Map<Integer, ItemStack> matrixStacks = new HashMap<>();
  private TileMain root;
  private ItemStack remote;

  public ContainerNetworkCraftingRemote(int id, Inventory pInv) {
    super(SsnRegistry.CRAFTINGREMOTE, id);
    this.player = pInv.player;
    this.remote = pInv.player.getMainHandItem();
    if (this.remote.getItem() != SsnRegistry.Items.CRAFTING_REMOTE.get()) {
      Triple<String, Integer, ItemStack> result = UtilInventory.getCurioRemote(player, SsnRegistry.Items.CRAFTING_REMOTE.get());
      this.remote = result.getRight();
    }
    this.world = player.level;
    DimPos dp = DimPos.getPosStored(remote);
    if (dp == null) {
      StorageNetwork.LOGGER.error("Remote opening with null pos Stored {} ", remote);
    }
    else {
      this.root = dp.getTileEntity(TileMain.class, world);
    }
    matrix = new NetworkCraftingInventory(this, matrixStacks);
    this.playerInv = pInv;
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMain(getTileMain());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    slotsChanged(matrix);
  }

  @Override
  public boolean stillValid(Player playerIn) {
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
  public void slotsChanged(Container inventoryIn) {
    if (recipeLocked) {
      //      StorageNetwork.log("recipe locked so onCraftMatrixChanged cancelled");
      return;
    }
    //    findMatchingRecipe(matrix);
    super.slotsChanged(inventoryIn);
  }

  @Override
  public void removed(Player playerIn) {
    super.removed(playerIn);
    for (int i = 0; i < matrix.getContainerSize(); i++) {
      UtilInventory.dropItem(world, playerIn.blockPosition(), matrix.getItem(i));
    }
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean isCrafting() {
    return true;
  }

  public ItemStack getRemote() {
    return remote;
  }
}
