package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableItemAutoIO;
import com.lothrazar.storagenetwork.api.IConnectableLink;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StorageNetworkCapabilities {

  @CapabilityInject(IConnectable.class)
  public static Capability<IConnectable> CONNECTABLE_CAPABILITY = null;
  @CapabilityInject(IConnectableLink.class)
  public static Capability<IConnectableLink> CONNECTABLE_ITEM_STORAGE_CAPABILITY = null;
  @CapabilityInject(IConnectableItemAutoIO.class)
  public static Capability<IConnectableItemAutoIO> CONNECTABLE_AUTO_IO = null;

  @SubscribeEvent
  public static void initCapabilities(RegisterCapabilitiesEvent event) {
    event.register(IConnectable.class);
    event.register(IConnectableLink.class);
    event.register(IConnectableItemAutoIO.class);
  }
}
