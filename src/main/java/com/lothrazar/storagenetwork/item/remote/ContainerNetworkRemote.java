package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

public class ContainerNetworkRemote extends ContainerNetwork {

  private final TileMain root;
  private ItemStack remote;
  private int remoteHash;

  public ContainerNetworkRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.REMOTE, id);
    this.remote = getCurioRemote(pInv.player);
    this.remoteHash = remote.hashCode();
    this.player = pInv.player;
    this.world = player.world;
    DimPos dp = ItemRemote.getPosStored(remote);
    if (dp == null) {
      StorageNetwork.LOGGER.error("Remote opening with null pos Stored {} ", remote);
    }
    this.root = dp.getTileEntity(TileMain.class, world);
    this.playerInv = pInv;
    bindPlayerInvo(this.playerInv);
    bindHotbar();
  }

  public static ItemStack getCurioRemote(PlayerEntity player) {
    if (ModList.get().isLoaded("curios")) {
      ItemRemote itemRemote = SsnRegistry.CRAFTING_REMOTE;
      //
      //
      ImmutableTriple<String, Integer, ItemStack> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(itemRemote, player).orElse(null);
      if (equipped != null && !equipped.getRight().isEmpty()) {
        ItemStack remote = equipped.getRight();
        //success: try to insert items to network thru this remote 
        return remote;
      }
      //
      //try other kind
      itemRemote = SsnRegistry.INVENTORY_REMOTE;
      equipped = CuriosApi.getCuriosHelper().findEquippedCurio(itemRemote, player).orElse(null);
      if (equipped != null && !equipped.getRight().isEmpty()) {
        ItemStack remote = equipped.getRight();
        //success: try to insert items to network thru this remote 
        return remote;
      }
    }
    //default
    if (player.getHeldItemMainhand().getItem() instanceof ItemRemote) {
      return player.getHeldItemMainhand();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return remoteHash == ContainerNetworkRemote.getCurioRemote(player).hashCode();
    //    return playerIn.getHeldItem(Hand.MAIN_HAND) == remote;
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
