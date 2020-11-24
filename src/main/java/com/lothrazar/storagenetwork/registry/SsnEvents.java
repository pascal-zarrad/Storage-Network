package com.lothrazar.storagenetwork.registry;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SsnEvents {

  @SubscribeEvent
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    SsnRegistry.collector_remote.onEntityItemPickupEvent(event);
  }
}
