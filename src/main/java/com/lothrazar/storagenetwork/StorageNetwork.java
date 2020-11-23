package com.lothrazar.storagenetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.storagenetwork.jei.JeiSettings;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.setup.ClientProxy;
import com.lothrazar.storagenetwork.setup.IProxy;
import com.lothrazar.storagenetwork.setup.ServerProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(StorageNetwork.MODID)
public class StorageNetwork {

  public static final String MODID = "storagenetwork";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
  public static ConfigRegistry config;

  public StorageNetwork() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(StorageNetwork::setup);
    MinecraftForge.EVENT_BUS.register(new SsnRegistry.RegistryEvents());
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    SsnRegistry.collector_remote.onEntityItemPickupEvent(event);
  }

  private static void setup(FMLCommonSetupEvent event) {
    PacketRegistry.init();
    StorageNetworkCapabilities.initCapabilities();
    proxy.init();
    config = new ConfigRegistry(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
    JeiSettings.setJeiLoaded(true);
  }

  public static void log(String s) {
    if (config.logspam()) {
      LOGGER.info(s);
    }
  }
}
