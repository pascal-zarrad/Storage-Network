package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.api.data.DimPos;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ContainerNetworkRemote extends ContainerNetwork {

  private final TileMaster master;
  private ItemStack remote;

  public ContainerNetworkRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.remote, id);
    this.remote = pInv.player.getHeldItem(Hand.MAIN_HAND);
    this.player = pInv.player;
    this.world = player.world;
    DimPos dp = ItemRemote.getPosStored(remote);
    this.master = dp.getTileEntity(TileMaster.class, world);
    //    StorageNetwork.log("Container: testmaster ?" + testmaster);
    //    TileEntity te = pInv.player.world.getTileEntity(dp.getBlockPos());
    //    StorageNetwork.log("tile entityin remote ?" + te);
    //    this.master = (TileMaster) te;
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
