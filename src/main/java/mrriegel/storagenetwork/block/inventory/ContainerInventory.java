package mrriegel.storagenetwork.block.inventory;

import java.util.ArrayList;
import java.util.List;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.network.StackRefreshClientMessage;
import mrriegel.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ContainerInventory extends ContainerNetworkBase {

  private TileInventory tileRequest;

  public ContainerInventory(final TileInventory tile, final InventoryPlayer playerInv) {
    this.tileRequest = tile;
    this.playerInv = playerInv;
    this.isSimple = true;
    bindPlayerInvo(playerInv);
    bindHotbar();
  }

  @Override
  public void bindHotbar() {
    //player hotbar
    for (int i = 0; i < 9; ++i) {
      this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    TileMaster tileMaster = this.getTileMaster();
    TileInventory table = getTileRequest();
    if (tileMaster != null &&
        !table.getWorld().isRemote && table.getWorld().getTotalWorldTime() % 40 == 0) {
      List<ItemStack> list = tileMaster.getStacks();
      PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()), (EntityPlayerMP) playerIn);
    }
    BlockPos pos = table.getPos();
    return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot.inventory != this.result && super.canMergeSlot(stack, slot);
  }

  @Override
  public TileMaster getTileMaster() {
    if (getTileRequest() == null || getTileRequest().getMaster() == null) {
      return null;
    }
    return getTileRequest().getMaster().getTileEntity(TileMaster.class);
  }

  public TileInventory getTileRequest() {
    return tileRequest;
  }

  @Override
  public boolean isRequest() {
    return true;//false means it looks for an item, true means it uses TileConnectable
  }

  @Override
  public void slotChanged() {}
}
