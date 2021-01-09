package com.lothrazar.storagenetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.storagenetwork.block.cable.export.GuiCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.GuiCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.linkfilter.GuiCableFilter;
import com.lothrazar.storagenetwork.block.collection.GuiCollectionFilter;
import com.lothrazar.storagenetwork.block.inventory.GuiNetworkInventory;
import com.lothrazar.storagenetwork.block.request.GuiNetworkTable;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkCraftingRemote;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkRemote;
import com.lothrazar.storagenetwork.jei.JeiSettings;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.registry.SsnEvents;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(StorageNetwork.MODID)
public class StorageNetwork {

  public static final String MODID = "storagenetwork";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigRegistry config;

  public StorageNetwork() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(StorageNetwork::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(new SsnRegistry.RegistryEvents());
    MinecraftForge.EVENT_BUS.register(new SsnEvents());
  }

  private static void setup(FMLCommonSetupEvent event) {
    PacketRegistry.init();
    StorageNetworkCapabilities.initCapabilities();
    config = new ConfigRegistry(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
    JeiSettings.setJeiLoaded(ModList.get().isLoaded("jei"));
  }

  private void setupClient(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(SsnRegistry.requestcontainer, GuiNetworkTable::new);
    ScreenManager.registerFactory(SsnRegistry.filterContainer, GuiCableFilter::new);
    ScreenManager.registerFactory(SsnRegistry.filterimportContainer, GuiCableImportFilter::new);
    ScreenManager.registerFactory(SsnRegistry.filterexportContainer, GuiCableExportFilter::new);
    ScreenManager.registerFactory(SsnRegistry.remote, GuiNetworkRemote::new);
    ScreenManager.registerFactory(SsnRegistry.craftingremote, GuiNetworkCraftingRemote::new);
    ScreenManager.registerFactory(SsnRegistry.inventorycontainer, GuiNetworkInventory::new);
    ScreenManager.registerFactory(SsnRegistry.collectorCtr, GuiCollectionFilter::new);
  }

  public static void log(String s) {
    if (config.logspam()) {
      LOGGER.info(s);
    }
  }
}
