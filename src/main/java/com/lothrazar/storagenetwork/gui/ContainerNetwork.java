package com.lothrazar.storagenetwork.gui;
import java.util.Optional;
import javax.annotation.Nullable;

import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.inventory.InventoryCraftingNetwork;
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

  public abstract TileMaster getTileMaster();

  public abstract void slotChanged();

  protected final CraftResultInventory resultInventory;
  protected PlayerInventory playerInv;
  protected CraftingResultSlot result;
  protected boolean recipeLocked = false;
  protected PlayerEntity player;
  protected World world;
  protected ICraftingRecipe recipeCurrent;
  public InventoryCraftingNetwork matrix;

  protected ContainerNetwork(@Nullable ContainerType<?> type, int id) {
    super(type, id);
    this.resultInventory = new CraftResultInventory();
  }

  public CraftingInventory getCraftMatrix() {
    return matrix;
  }

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
  private void findMatchingRecipe(int number, World world, PlayerEntity player, CraftingInventory inventory, CraftResultInventory result) {
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
}
