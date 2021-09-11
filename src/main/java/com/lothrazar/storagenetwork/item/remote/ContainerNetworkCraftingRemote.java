package com.lothrazar.storagenetwork.item.remote;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.block.request.SlotCraftingNetwork;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.nbt.CompoundTag;
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
    this.remote = pInv.player.getMainHandItem();
    this.player = pInv.player;
    this.world = player.level;
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
    for (int i = 0; i < matrix.getContainerSize(); i++) {
      if (remote.hasTag() && remote.getTag().contains("matrix" + i)) {
        CompoundTag tag = remote.getTag().getCompound("matrix" + i);
        ItemStack stackSaved = ItemStack.of(tag);
        if (!stackSaved.isEmpty()) {
          matrix.setItem(i, stackSaved);
        }
      }
    }
    slotsChanged(matrix);
  }

  @Override
  public boolean stillValid(Player playerIn) {
    return remote == player.getMainHandItem();
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
    ItemStack me;
    for (int i = 0; i < matrix.getContainerSize(); i++) {
      me = matrix.getItem(i);
      CompoundTag here = me.save(new CompoundTag());
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
