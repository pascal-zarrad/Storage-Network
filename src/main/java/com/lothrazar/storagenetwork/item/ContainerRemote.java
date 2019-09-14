package com.lothrazar.storagenetwork.item;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public class ContainerRemote extends Container {

  public ContainerRemote(int id, PlayerInventory pInv) {
    super(SsnRegistry.remote, id);
  }

  @Override public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
