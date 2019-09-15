package com.lothrazar.storagenetwork.block.cable;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.world.World;

public abstract class ContainerCable extends Container {

  protected int sq = 18;
  protected PlayerEntity player;
  protected World world;

  protected ContainerCable(@Nullable ContainerType<?> type, int id) {
    super(type, id);
  }

  protected void bindPlayerInvo(PlayerInventory playerInv) {
    this.player = playerInv.player;
    this.world = player.world;
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
