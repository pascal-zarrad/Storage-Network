package com.lothrazar.storagenetwork.block.cable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public abstract class ContainerCable extends AbstractContainerMenu {

  protected static final int SQ = 18;
  protected Player player;
  protected Level world;

  protected ContainerCable(MenuType<?> type, int id) {
    super(type, id);
  }

  protected void bindPlayerInvo(Inventory playerInv) {
    this.player = playerInv.player;
    this.world = player.level;
    //player inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 89 + i * 18));
      }
    }
    //player hotbar
    for (int i = 0; i < 9; ++i) {
      addSlot(new Slot(playerInv, i, 8 + i * 18, 147));
    }
  }
}
