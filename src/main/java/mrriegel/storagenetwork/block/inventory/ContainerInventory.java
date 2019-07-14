package mrriegel.storagenetwork.block.inventory;
import com.google.common.collect.Lists;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.data.ItemStackMatcher;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.network.StackRefreshClientMessage;
import mrriegel.storagenetwork.registry.ModBlocks;
import mrriegel.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerInventory extends ContainerNetworkBase {

  final TileInventory tile;

  public ContainerInventory(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(ModBlocks.requestcontainer, windowId);
    tile = (TileInventory) world.getTileEntity(pos);
     this.playerInv = playerInv;
     bindGrid();
    bindPlayerInvo(playerInv);
    bindHotbar();
    onCraftMatrixChanged(matrix);
    this.isSimple = true;
  }

  @Override
  public void bindHotbar() {
    //player hotbar
    for (int i = 0; i < 9; ++i) {
      addSlot(new Slot(playerInv, i, 8 + i * 18, 232));
    }
  }

  @Override public void slotChanged() {
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    TileMaster tileMaster = getTileMaster();
    if (tileMaster != null &&
        !tile.getWorld().isRemote && tile.getWorld().getGameTime() % 40 == 0) {
      List<ItemStack> list = tileMaster.getStacks();
      // TODO: packets
      //   PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()), (PlayerEntityMP) playerIn);
    }
    BlockPos pos = tile.getPos();
    return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot.inventory != result && super.canMergeSlot(stack, slot);
  }

  @Override
  public TileMaster getTileMaster() {
    if (tile == null || tile.getMaster() == null) {
      return null;
    }
    return tile.getMaster().getTileEntity(TileMaster.class);
  }

  public static boolean isRequest() {
    return false;
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int slotIndex) {
    StorageNetwork.log("ContainerRequest transfer " + slotIndex);
    if (playerIn.world.isRemote) {
      return ItemStack.EMPTY;
    }
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(slotIndex);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      TileMaster tileMaster = this.getTileMaster();
//      if (slotIndex == 0) {
//        StorageNetwork.log(" craftShift !!! " + tileMaster);
//        craftShift(playerIn, tileMaster);
//        return ItemStack.EMPTY;
//      }
//      else
      if (tileMaster != null) {
        int rest = tileMaster.insertStack(itemstack1, false);
        ItemStack stack = rest == 0 ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(itemstack1, rest);
        slot.putStack(stack);
        detectAndSendChanges();
        List<ItemStack> list = tileMaster.getStacks();
        if (playerIn instanceof ServerPlayerEntity) {
          ServerPlayerEntity sp = (ServerPlayerEntity) playerIn;
          PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
              sp.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
        if (stack.isEmpty()) {
          return ItemStack.EMPTY;
        }
        slot.onTake(playerIn, itemstack1);
        return ItemStack.EMPTY;
      }
      if (itemstack1.getCount() == 0) {
        slot.putStack(ItemStack.EMPTY);
      }
      else {
        slot.onSlotChanged();
      }
      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(playerIn, itemstack1);
    }
    return itemstack;
  }


}
