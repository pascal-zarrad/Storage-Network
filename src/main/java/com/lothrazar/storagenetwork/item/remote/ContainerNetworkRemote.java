package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ContainerNetworkRemote extends ContainerNetwork {

  private final TileMain root;
  private ItemStack remote;

  public ContainerNetworkRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.remote, id);
    this.remote = pInv.player.getHeldItem(Hand.MAIN_HAND);
    this.player = pInv.player;
    this.world = player.world;
    DimPos dp = ItemRemote.getPosStored(remote);
    this.root = dp.getTileEntity(TileMain.class, world);
    if (root == null) {
      StorageNetwork.LOGGER.error("Error:getTileentity main for world null: " + dp.toString());
    }
    this.playerInv = pInv;
    bindPlayerInvo(this.playerInv);
    bindHotbar();
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return playerIn.getHeldItem(Hand.MAIN_HAND) == remote;
  }

  @Override
  public TileMain getTileMain() {
    return root;
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean isCrafting() {
    return false;
  }
}
