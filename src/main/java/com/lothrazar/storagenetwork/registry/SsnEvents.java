package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.StorageNetwork;
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
    //
    if (StorageNetwork.TEST.isPressed()) {
      //gogo 
      //
      PacketRegistry.INSTANCE.sendToServer(new KeybindCurioMessage());
      //      IItemHandlerModifiable all = CuriosApi.getCuriosHelper().getEquippedCurios(Minecraft.getInstance().player).orElse(null);
    }
  }
}
