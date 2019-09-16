package com.lothrazar.storagenetwork.block.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.inventory.InventoryCraftingNetwork;
import com.lothrazar.storagenetwork.network.StackRefreshClientMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
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

public class ContainerNetworkTable extends ContainerNetwork {

  private final TileRequest tileRequest;

  public ContainerNetworkTable(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.requestcontainer, windowId);
    tileRequest = (TileRequest) world.getTileEntity(pos);
    matrix = new InventoryCraftingNetwork(this, tileRequest.matrix);
    this.playerInv = playerInv;

    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMaster(getTileMaster());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    onCraftMatrixChanged(matrix);
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    if (recipeLocked) {
      //StorageNetwork.log("recipe locked so onCraftMatrixChanged cancelled");
      return;
    }
    //    findMatchingRecipe(matrix);
    super.onCraftMatrixChanged(inventoryIn);
  }

  @Override
  public void slotChanged() {
    //parent is abstract
    //seems to not happen from -shiftclick- crafting
    for (int i = 0; i < 9; i++) {
      getTileRequest().matrix.put(i, matrix.getStackInSlot(i));
    }
    UtilTileEntity.updateTile(getTileRequest().getWorld(), getTileRequest().getPos());
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    TileMaster tileMaster = getTileMaster();
    TileRequest table = getTileRequest();
    if (tileMaster != null &&
        !table.getWorld().isRemote && table.getWorld().getGameTime() % 40 == 0) {
      List<ItemStack> list = tileMaster.getStacks();
      // TODO: packets
      //   PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()), (PlayerEntityMP) playerIn);
    }
    BlockPos pos = table.getPos();
    return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot.inventory != result && super.canMergeSlot(stack, slot);
  }

  @Override
  public TileMaster getTileMaster() {
    if (getTileRequest() == null || getTileRequest().getMaster() == null) {
      return null;
    }
    return getTileRequest().getMaster().getTileEntity(TileMaster.class);
  }

  public TileRequest getTileRequest() {
    return tileRequest;
  }



  /**
   * A note on the shift-craft delay bug root cause was ANY interaction with matrix (setting contents etc) was causing triggers/events to do a recipe lookup. Meaning during this shift-click action you
   * can get up to 9x64 FULL recipe scans Solution is just to disable all those triggers but only for duration of this action
   *
   * @param player
   * @param tile
   */
  @Override
  protected void craftShift(PlayerEntity player, TileMaster tile) {
    if (matrix == null) {
      return;
    }
    recipeCurrent = null;
    this.findMatchingRecipeClient(player.world, this.matrix, this.resultInventory);
    if (recipeCurrent == null) {
      return;
    }
    this.recipeLocked = true;
    int crafted = 0;
    List<ItemStack> recipeCopy = Lists.newArrayList();
    for (int i = 0; i < matrix.getSizeInventory(); i++) {
      recipeCopy.add(matrix.getStackInSlot(i).copy());
    }
    ItemStack res = recipeCurrent.getCraftingResult(matrix);
    if (res.isEmpty()) {
      StorageNetwork.LOGGER.info("err Recipe output is an empty stack " + recipeCurrent);
      return;
    }
    int sizePerCraft = res.getCount();
    StorageNetwork.log("[craftShift] sizePerCraft = " + sizePerCraft + " for stack " + res);
    while (crafted + sizePerCraft <= res.getMaxStackSize()) {
      res = recipeCurrent.getCraftingResult(matrix);
      StorageNetwork.log("[craftShift]  crafted = " + crafted + " ; res.count() = " + res.getCount() + " MAX=" + res.getMaxStackSize());
      if (!ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(playerInv), res, true).isEmpty()) {
        StorageNetwork.log("[craftShift] cannot insert more, end");
        break;
      }
      //stop if empty
      if (recipeCurrent.matches(matrix, player.world) == false) {
        StorageNetwork.log("[craftShift] recipe doesnt match i quit");
        break;
      }
      //onTake replaced with this handcoded rewrite
      StorageNetwork.log("[craftShift] addItemStackToInventory " + res);
      if (!player.inventory.addItemStackToInventory(res)) {
        player.dropItem(res, false);
      }
      NonNullList<ItemStack> remainder = recipeCurrent.getRemainingItems(this.matrix);//raftingManager.getRemainingItems(matrix, player.world);
      StorageNetwork.log("[craftShift] getRemainingItems " + remainder);
      for (int i = 0; i < remainder.size(); ++i) {
        ItemStack remainderCurrent = remainder.get(i);
        ItemStack slot = this.matrix.getStackInSlot(i);
        if (remainderCurrent.isEmpty()) {
          StorageNetwork.log("[craftShift] getRemainingItems  set empty " + i);
          matrix.getStackInSlot(i).shrink(1);
          continue;
        }
        //        if (remainderCurrent.isItemDamaged() && remainderCurrent.getItemDamage() > remainderCurrent.getMaxDamage()) {
        //          remainderCurrent = ItemStack.EMPTY;
        //        }
        if (slot.getItem().getContainerItem() != null) { //is the fix for milk and similar
          slot = new ItemStack(slot.getItem().getContainerItem());
          matrix.setInventorySlotContents(i, slot);
        }
        else if (!slot.getItem().getContainerItem(slot).isEmpty()) { //is the fix for milk and similar
          slot = slot.getItem().getContainerItem(slot);
          matrix.setInventorySlotContents(i, slot);
        }
        else if (!remainderCurrent.isEmpty()) {
          StorageNetwork.log("[craftShift] NONEMPTY " + remainderCurrent);
          if (slot.isEmpty()) {
            this.matrix.setInventorySlotContents(i, remainderCurrent);
          }
          else if (ItemStack.areItemsEqual(slot, remainderCurrent) && ItemStack.areItemStackTagsEqual(slot, remainderCurrent)) {
            remainderCurrent.grow(slot.getCount());
            this.matrix.setInventorySlotContents(i, remainderCurrent);
          }
          else if (ItemStack.areItemsEqualIgnoreDurability(slot, remainderCurrent)) {
            //crafting that consumes durability
            this.matrix.setInventorySlotContents(i, remainderCurrent);
          }
          else {
            if (!player.inventory.addItemStackToInventory(remainderCurrent)) {
              player.dropItem(remainderCurrent, false);
            }
          }
        }
        else if (!slot.isEmpty()) {
          this.matrix.decrStackSize(i, 1);
          slot = this.matrix.getStackInSlot(i);
        }
      } //end loop on remiainder
      //END onTake redo
      crafted += sizePerCraft;
      ItemStack stackInSlot;
      ItemStack recipeStack;
      ItemStackMatcher itemStackMatcherCurrent;
      for (int i = 0; i < matrix.getSizeInventory(); i++) {
        stackInSlot = matrix.getStackInSlot(i);
        if (stackInSlot.isEmpty()) {
          recipeStack = recipeCopy.get(i);
          //////////////// booleans are meta, ore(?ignored?), nbt
          itemStackMatcherCurrent = !recipeStack.isEmpty() ? new ItemStackMatcher(recipeStack, false, false) : null;
          //false here means dont simulate
          ItemStack req = tile.request(itemStackMatcherCurrent, 1, false);
          matrix.setInventorySlotContents(i, req);
        }
      }
      onCraftMatrixChanged(matrix);
    }
    detectAndSendChanges();
    this.recipeLocked = false;
    //update recipe again in case remnants left : IE hammer and such
    this.onCraftMatrixChanged(this.matrix);
  }
}
