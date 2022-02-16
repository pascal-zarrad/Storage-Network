package com.lothrazar.storagenetwork.item.remote;

import org.apache.commons.lang3.tuple.Triple;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ContainerNetworkRemote extends ContainerNetwork {

  private TileMain root;
  private ItemStack remote;

  public ContainerNetworkRemote(int id, Inventory pInv) {
    super(SsnRegistry.REMOTE, id);
    this.player = pInv.player;
    this.world = player.level;
    Triple<String, Integer, ItemStack> result = UtilInventory.getCurioRemote(pInv.player, SsnRegistry.INVENTORY_REMOTE);
    this.remote = result.getRight();
    DimPos dp = DimPos.getPosStored(remote);
    if (dp == null) {
      StorageNetwork.LOGGER.error(world.isClientSide + "=client||Remote opening with null pos Stored {} ", result);
    }
    else {
      this.root = dp.getTileEntity(TileMain.class, world);
    }
    if (root == null) {
      //maybe the table broke after doing this, rare case
      StorageNetwork.log("CONTAINER NETWORK REMOTE null tile");
    }
    this.playerInv = pInv;
    bindPlayerInvo(this.playerInv);
    bindHotbar();
  }

  @Override
  public boolean stillValid(Player playerIn) {
    //does not store itemstack inventory, and opens from curios so no security here. unless it dissapears
    return !getRemote().isEmpty();
  }

  public ItemStack getRemote() {
    return remote;
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
