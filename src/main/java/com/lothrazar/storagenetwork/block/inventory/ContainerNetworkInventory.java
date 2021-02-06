package com.lothrazar.storagenetwork.block.inventory;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerNetworkInventory extends ContainerNetwork {

  final TileInventory tile;

  public ContainerNetworkInventory(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.INVENTORYCONTAINER, windowId);
    tile = (TileInventory) world.getTileEntity(pos);
    this.playerInv = playerInv;
    bindPlayerInvo(this.playerInv);
    bindHotbar();
  }

  @Override
  public boolean isCrafting() {
    return false;
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }

  @Override
  public TileMain getTileMain() {
    if (tile == null || tile.getMain() == null) {
      //refresh delay, new chunk load or block placement
      return null;
    }
    return tile.getMain().getTileEntity(TileMain.class);
  }
}
