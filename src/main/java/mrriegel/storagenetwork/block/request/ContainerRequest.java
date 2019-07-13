package mrriegel.storagenetwork.block.request;
import com.google.common.collect.Lists;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.data.ItemStackMatcher;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.gui.InventoryCraftingNetwork;
import mrriegel.storagenetwork.network.StackRefreshClientMessage;
import mrriegel.storagenetwork.registry.ModBlocks;
import mrriegel.storagenetwork.registry.PacketRegistry;
import mrriegel.storagenetwork.util.UtilTileEntity;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.command.impl.RecipeCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerRequest extends ContainerNetworkBase {

  private final TileRequest tileRequest;

  public ContainerRequest(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(ModBlocks.requestcontainer, windowId);
    tileRequest = (TileRequest) world.getTileEntity(pos);
    matrix = new InventoryCraftingNetwork(this, tileRequest.matrix);
    this.playerInv = playerInv;
    //    result = new CraftingResultSlot();
    //        public SlotCraftingNetwork(PlayerEntity player,
    //        CraftingInventory craftingInventory, IInventory inventoryIn,
    //    int slotIndex, int xPosition, int yPosition) {
    //temporary
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, matrix, resultInventory, 0, 101, 128);
    slotCraftOutput.setTileMaster(getTileMaster());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(playerInv);
    bindHotbar();
    onCraftMatrixChanged(matrix);
  }

  @Override
  public void bindHotbar() {
    //player hotbar
    for (int i = 0; i < 9; ++i) {
      addSlot(new Slot(playerInv, i, 8 + i * 18, 232));
    }
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

  TileRequest getTileRequest() {
    return tileRequest;
  }

  public static boolean isRequest() {
    return true;
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
      if (slotIndex == 0) {
        StorageNetwork.log(" craftShift !!! " + tileMaster);
        craftShift(playerIn, tileMaster);
        return ItemStack.EMPTY;
      }
      else if (tileMaster != null) {
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

  //STEAL FROM WORKBENCH CONTAINER
  protected static void func_217066_a(int p_217066_0_, World p_217066_1_, PlayerEntity p_217066_2_, CraftingInventory p_217066_3_, CraftResultInventory p_217066_4_) {
    if (!p_217066_1_.isRemote) {
      ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity) p_217066_2_;
      ItemStack lvt_6_1_ = ItemStack.EMPTY;
      Optional<ICraftingRecipe> lvt_7_1_ = p_217066_1_.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, p_217066_3_, p_217066_1_);
      if (lvt_7_1_.isPresent()) {
        ICraftingRecipe lvt_8_1_ = (ICraftingRecipe) lvt_7_1_.get();
        if (p_217066_4_.canUseRecipe(p_217066_1_, lvt_5_1_, lvt_8_1_)) {
          lvt_6_1_ = lvt_8_1_.getCraftingResult(p_217066_3_);
        }
      }
      p_217066_4_.setInventorySlotContents(0, lvt_6_1_);
      lvt_5_1_.connection.sendPacket(new SSetSlotPacket(p_217066_0_, 0, lvt_6_1_));
    }
  }

  /**
   * A note on the shift-craft delay bug root cause was ANY interaction with matrix (setting contents etc) was causing triggers/events to do a recipe lookup. Meaning during this shift-click action you
   * can get up to 9x64 FULL recipe scans Solution is just to disable all those triggers but only for duration of this action
   *
   * @param player
   * @param tile
   */
  protected void craftShift(PlayerEntity player, TileMaster tile) {
    if (matrix == null) {
      return;
    }
    recipeCurrent = null;
    this.findMatchingRecipeClient(player.world, this.matrix, this.resultInventory);
    if (recipeCurrent == null) {
      StorageNetwork.log("current is null");
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
        StorageNetwork.log("err Recipe output is an empty stack " + recipeCurrent);
      return;
    }
    int sizePerCraft = res.getCount();
    // int sizeFull = res.getMaxStackSize();
    //   int numberToCraft = sizeFull / sizePerCraft;
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
        StorageNetwork.log("[craftShift] getRemainingItems "+remainder);
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
          itemStackMatcherCurrent = !recipeStack.isEmpty() ? new ItemStackMatcher(recipeStack, true, false, false) : null;
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
