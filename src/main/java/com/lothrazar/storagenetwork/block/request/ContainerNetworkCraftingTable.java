package com.lothrazar.storagenetwork.block.request;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerNetworkCraftingTable extends ContainerNetwork {

  private final TileRequest tileRequest;

  public ContainerNetworkCraftingTable(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.requestcontainer, windowId);
    tileRequest = (TileRequest) world.getTileEntity(pos);
    matrix = new NetworkCraftingInventory(this, tileRequest.matrix);
    this.playerInv = playerInv;
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMain(getTileMain());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    onCraftMatrixChanged(matrix);
  }

  @Override
  public boolean isCrafting() {
    return true;
  }

  @Override
  public void slotChanged() {
    //parent is abstract
    //seems to not happen from -shiftclick- crafting
    for (int i = 0; i < matrix.getSizeInventory(); i++) {
      getTileRequest().matrix.put(i, matrix.getStackInSlot(i));
    }
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    //    TileMain main = getTileMain();
    TileRequest table = getTileRequest();
    BlockPos pos = table.getPos();
    return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
