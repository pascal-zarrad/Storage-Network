package com.lothrazar.storagenetwork.gui;

import java.util.Optional;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;

public abstract class ContainerNetwork extends Container {

  protected PlayerInventory playerInv;
  protected CraftingResultSlot result;
  protected final CraftResultInventory resultInventory;//field_75160_f;
  public InventoryCraftingNetwork matrix;
  protected boolean recipeLocked = false;
  protected boolean isSimple;
  protected PlayerEntity player;
  protected World world;
  protected ICraftingRecipe recipeCurrent;

  protected ContainerNetwork(@Nullable ContainerType<?> type, int id) {
    super(type, id);
    this.resultInventory = new CraftResultInventory();
  }

  public CraftingInventory getCraftMatrix() {
    return matrix;
  }

  public abstract TileMaster getTileMaster();

//  public abstract void bindHotbar();

  public abstract void slotChanged();

//  boolean test = false;

  protected void bindPlayerInvo(PlayerInventory playerInv) {
    this.player = playerInv.player;
    this.world = player.world;
    //player inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
      }
    }
  }

  protected void bindGrid() {
    int index = 0;
    //3x3 crafting grid
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 3; ++j) {
        addSlot(new Slot(matrix, index++, 8 + j * 18, 110 + i * 18));
      }
    }
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    slotChanged();
    super.onContainerClosed(playerIn);
  }

  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    super.onCraftMatrixChanged(inventoryIn);
    this.recipeCurrent = null;
    findMatchingRecipe(this.windowId, world, this.player, this.matrix, this.resultInventory);
  }

  //it runs on server tho
  protected void findMatchingRecipeClient(World world, CraftingInventory inventory, CraftResultInventory result) {
    Optional<ICraftingRecipe> optional = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, world);
    if (optional.isPresent()) {
      ICraftingRecipe icraftingrecipe = optional.get();
      this.recipeCurrent = icraftingrecipe;
    }
  }

  //from WorkbenchContainer::func_217066_a
  protected void findMatchingRecipe(int number, World world, PlayerEntity player, CraftingInventory inventory, CraftResultInventory result) {
    if (!world.isRemote) {
      ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
      ItemStack itemstack = ItemStack.EMPTY;
      Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, world);
      if (optional.isPresent()) {
        ICraftingRecipe icraftingrecipe = optional.get();
        if (result.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
          itemstack = icraftingrecipe.getCraftingResult(inventory);
          //          itemstack = icraftingrecipe.func_77572_b(inventory);
          //save for next time
          this.recipeCurrent = icraftingrecipe;
        }
      }
      result.setInventorySlotContents(0, itemstack);
      serverplayerentity.connection.sendPacket(new SSetSlotPacket(number, 0, itemstack));
    }
  }
  /**
   * A note on the shift-craft delay bug root cause was ANY interaction with matrix (setting contents etc) was causing triggers/events to do a recipe lookup. Meaning during this shift-click action you
   * can get up to 9x64 FULL recipe scans Solution is just to disable all those triggers but only for duration of this action
   *
   */
  //  private void craftShift(PlayerEntity player, TileMaster tile) {
  //    if (matrix == null) {
  //      return;
  //    }
  //    IRecipe recipeCurrent = CraftingManager.findMatchingRecipe(matrix, player.world);
  //    if (recipeCurrent == null) {
  //      return;
  //    }
  //    recipeLocked = true;
  //    int crafted = 0;
  //    List<ItemStack> recipeCopy = Lists.newArrayList();
  //    for (int i = 0; i < matrix.getSizeInventory(); i++) {
  //      recipeCopy.add(matrix.getStackInSlot(i).copy());
  //    }
  //    ItemStack res = recipeCurrent.getCraftingResult(matrix);
  //    if (res.isEmpty()) {
  //      //  StorageNetwork.instance.logger.error("Recipe output is an empty stack " + recipeCurrent);
  //      return;
  //    }
  //    int sizePerCraft = res.getCount();
  //
  //    while (crafted + sizePerCraft <= res.getMaxStackSize()) {
  //      res = recipeCurrent.getCraftingResult(matrix);
  //      //  StorageNetwork.log("[craftShift]  crafted = " + crafted + " ; res.count() = " + res.getCount() + " MAX=" + res.getMaxStackSize());
  //      if (!ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(playerInv), res, true).isEmpty()) {
  //        //  StorageNetwork.log("[craftShift] cannot insert more, end");
  //        break;
  //      }
  //      //stop if empty
  //      if (recipeCurrent.matches(matrix, player.world) == false) {
  //        //      StorageNetwork.log("[craftShift] recipe doesnt match i quit");
  //        break;
  //      }
  //      //onTake replaced with this handcoded rewrite
  //      //  StorageNetwork.log("[craftShift] addItemStackToInventory " + res);
  //      if (!player.inventory.addItemStackToInventory(res)) {
  //        player.dropItem(res, false);
  //      }
  //      NonNullList<ItemStack> remainder = CraftingManager.getRemainingItems(matrix, player.world);
  //      //  StorageNetwork.log("[craftShift] getRemainingItems ");
  //      for (int i = 0; i < remainder.size(); ++i) {
  //        ItemStack remainderCurrent = remainder.get(i);
  //        ItemStack slot = matrix.getStackInSlot(i);
  //        if (remainderCurrent.isEmpty()) {
  //          //     StorageNetwork.log("[craftShift] getRemainingItems  set empty " + i);
  //          matrix.getStackInSlot(i).shrink(1);
  //          continue;
  //        }
  //        if (remainderCurrent.isItemDamaged() && remainderCurrent.getItemDamage() > remainderCurrent.getMaxDamage()) {
  //          remainderCurrent = ItemStack.EMPTY;
  //        }
  //        if (slot.getItem().getContainerItem() != null) { //is the fix for milk and similar
  //          slot = new ItemStack(slot.getItem().getContainerItem());
  //          matrix.setInventorySlotContents(i, slot);
  //        }
  //        else if (!slot.getItem().getContainerItem(slot).isEmpty()) { //is the fix for milk and similar
  //          slot = slot.getItem().getContainerItem(slot);
  //          matrix.setInventorySlotContents(i, slot);
  //        }
  //        else if (!remainderCurrent.isEmpty()) {
  //          //   StorageNetwork.log("[craftShift] NONEMPTY " + remainderCurrent);
  //          if (slot.isEmpty()) {
  //            matrix.setInventorySlotContents(i, remainderCurrent);
  //          }
  //          else if (ItemStack.areItemsEqual(slot, remainderCurrent) && ItemStack.areItemStackTagsEqual(slot, remainderCurrent)) {
  //            remainderCurrent.grow(slot.getCount());
  //            matrix.setInventorySlotContents(i, remainderCurrent);
  //          }
  //          else if (ItemStack.areItemsEqualIgnoreDurability(slot, remainderCurrent)) {
  //            //crafting that consumes durability
  //            matrix.setInventorySlotContents(i, remainderCurrent);
  //          }
  //          else {
  //            if (!player.inventory.addItemStackToInventory(remainderCurrent)) {
  //              player.dropItem(remainderCurrent, false);
  //            }
  //          }
  //        }
  //        else if (!slot.isEmpty()) {
  //          matrix.decrStackSize(i, 1);
  //          slot = matrix.getStackInSlot(i);
  //        }
  //      } //end loop on remiainder
  //      //END onTake redo
  //      crafted += sizePerCraft;
  //      ItemStack stackInSlot;
  //      ItemStack recipeStack;
  //      ItemStackMatcher itemStackMatcherCurrent;
  //      for (int i = 0; i < matrix.getSizeInventory(); i++) {
  //        stackInSlot = matrix.getStackInSlot(i);
  //        if (stackInSlot.isEmpty()) {
  //          recipeStack = recipeCopy.get(i);
  //          //////////////// booleans are meta, ore(?ignored?), nbt
  //          itemStackMatcherCurrent = !recipeStack.isEmpty() ? new ItemStackMatcher(recipeStack, true, false, false) : null;
  //          //false here means dont simulate
  //          ItemStack req = tile.request(itemStackMatcherCurrent, 1, false);
  //          matrix.setInventorySlotContents(i, req);
  //        }
  //      }
  //      onCraftMatrixChanged(matrix);
  //    }
  //    detectAndSendChanges();
  //    recipeLocked = false;
  //    //update recipe again in case remnants left : IE hammer and such
  //    onCraftMatrixChanged(matrix);
  //  }
  //  @Override
  //  public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
  //    if (playerIn.world.isRemote) {
  //      return ItemStack.EMPTY;
  //    }
  //    ItemStack itemstack = ItemStack.EMPTY;
  //    Slot slot = inventorySlots.get(slotIndex);
  //    if (slot != null && slot.getHasStack()) {
  //      ItemStack itemstack1 = slot.getStack();
  //      itemstack = itemstack1.copy();
  //      TileMaster tileMaster = getTileMaster();
  //      if (slotIndex == 0) {
  //        craftShift(playerIn, tileMaster);
  //        return ItemStack.EMPTY;
  //      }
  //      else if (tileMaster != null) {
  //        int rest = tileMaster.insertStack(itemstack1, false);
  //        ItemStack stack = rest == 0 ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(itemstack1, rest);
  //        slot.putStack(stack);
  //        detectAndSendChanges();
  //        List<ItemStack> list = tileMaster.getStacks();
  //        PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()), (EntityPlayerMP) playerIn);
  //        if (stack.isEmpty()) {
  //          return ItemStack.EMPTY;
  //        }
  //        slot.onTake(playerIn, itemstack1);
  //        return ItemStack.EMPTY;
  //      }
  //      if (itemstack1.getCount() == 0) {
  //        slot.putStack(ItemStack.EMPTY);
  //      }
  //      else {
  //        slot.onSlotChanged();
  //      }
  //      if (itemstack1.getCount() == itemstack.getCount()) {
  //        return ItemStack.EMPTY;
  //      }
  //      slot.onTake(playerIn, itemstack1);
  //    }
  //    return itemstack;
  //  }
}
