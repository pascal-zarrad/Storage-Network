package com.lothrazar.storagenetwork.block.request;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ContainerNetworkCraftingTable extends ContainerNetwork {

  private final TileRequest tileRequest;

  public ContainerNetworkCraftingTable(int windowId, Level world, BlockPos pos, Inventory playerInv, Player player) {
    super(SsnRegistry.Menus.REQUEST.get(), windowId);
    tileRequest = (TileRequest) world.getBlockEntity(pos);
    matrix = new NetworkCraftingInventory(this, tileRequest.matrix);
    this.playerInv = playerInv;
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMain(getTileMain());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    slotsChanged(matrix);
  }

  @Override
  public boolean isCrafting() {
    return true;
  }

  @Override
  public void slotChanged() {
    //parent is abstract
    //seems to not happen from -shiftclick- crafting
    for (int i = 0; i < matrix.getContainerSize(); i++) {
      getTileRequest().matrix.put(i, matrix.getItem(i));
    }
  }

  @Override
  public boolean stillValid(Player playerIn) {
    //    TileMain main = getTileMain();
    TileRequest table = getTileRequest();
    BlockPos pos = table.getBlockPos();
    return playerIn.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  public TileMain getTileMain() {
    if (getTileRequest() == null || getTileRequest().getMain() == null) {
      return null;
    }
    return getTileRequest().getMain().getTileEntity(TileMain.class);
  }

  public TileRequest getTileRequest() {
    return tileRequest;
  }
}
