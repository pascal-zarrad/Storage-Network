package com.lothrazar.storagenetwork.gui;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public abstract class ContainerCable extends Container {

//  public TileCable tile;
  protected int sq = 18;

  protected ContainerCable(@Nullable ContainerType<?> type, int id) {
    super(type, id);
  }

//    this.tile = tile;
    //player inventory
//    for (int i = 0; i < 3; ++i) {
//      for (int j = 0; j < 9; ++j) {
//        this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * sq, 55 + 34 + i * sq));
//      }
//    }
//    //player hotbar
//    for (int i = 0; i < 9; ++i) {
//      this.addSlotToContainer(new Slot(playerInv, i, 8 + i * sq, 113 + 34));
//    }



}
