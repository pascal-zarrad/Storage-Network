package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableItemAutoIO;
import com.lothrazar.storagenetwork.api.IConnectableLink;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StorageNetworkCapabilities {

  public static final Capability<IConnectable> CONNECTABLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });
  public static final Capability<IConnectableLink> CONNECTABLE_ITEM_STORAGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });
  public static final Capability<IConnectableItemAutoIO> CONNECTABLE_AUTO_IO = CapabilityManager.get(new CapabilityToken<>() { });

  @SubscribeEvent
  public static void initCapabilities(RegisterCapabilitiesEvent event) {
    event.register(IConnectable.class);
    event.register(IConnectableLink.class);
    event.register(IConnectableItemAutoIO.class);
  }
}
