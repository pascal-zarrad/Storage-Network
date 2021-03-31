package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.item.ItemBuilder;
import com.lothrazar.storagenetwork.network.KeybindCurioMessage;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SsnEvents {

  @SubscribeEvent
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    SsnRegistry.COLLECTOR_REMOTE.onEntityItemPickupEvent(event);
  }

  @SubscribeEvent
  public void onHit(PlayerInteractEvent.LeftClickBlock event) {
    ItemBuilder.onLeftClickBlock(event);
  }

  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {
    if (ClientEventRegistry.INVENTORY_KEY.isPressed()) {
      //gogo client -> server event
      PacketRegistry.INSTANCE.sendToServer(new KeybindCurioMessage());
    }
  }
}
