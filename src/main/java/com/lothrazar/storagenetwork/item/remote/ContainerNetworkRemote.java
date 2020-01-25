package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.api.data.DimPos;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;

public class ContainerNetworkRemote extends ContainerNetwork {

  private final TileMaster master;
  private ItemStack remote;

  public ContainerNetworkRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.remote, id);
    this.remote = pInv.player.getHeldItem(Hand.MAIN_HAND);
    DimPos dp = ItemRemote.getPosStored(remote);
    TileEntity te = pInv.player.world.getTileEntity(dp.getBlockPos());
    this.master = (TileMaster) te;
    this.playerInv = pInv;
    bindPlayerInvo(this.playerInv);
    bindHotbar();
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return playerIn.getHeldItem(Hand.MAIN_HAND) == remote;
  }

  @Override
  public TileMaster getTileMaster() {
    return master;
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean isCrafting() {
    return false;
  }
}
