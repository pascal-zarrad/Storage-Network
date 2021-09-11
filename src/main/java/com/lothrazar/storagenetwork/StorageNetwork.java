package com.lothrazar.storagenetwork;

import com.lothrazar.storagenetwork.block.cable.export.GuiCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.GuiCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.linkfilter.GuiCableFilter;
import com.lothrazar.storagenetwork.block.collection.GuiCollectionFilter;
import com.lothrazar.storagenetwork.block.inventory.GuiNetworkInventory;
import com.lothrazar.storagenetwork.block.request.GuiNetworkTable;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkCraftingRemote;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkRemote;
import com.lothrazar.storagenetwork.registry.ClientEventRegistry;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.registry.SsnEvents;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(StorageNetwork.MODID)
public class StorageNetwork {

  public static final String MODID = "storagenetwork";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigRegistry CONFIG;

  public StorageNetwork() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(StorageNetwork::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(new SsnRegistry.RegistryEvents());
    MinecraftForge.EVENT_BUS.register(new SsnEvents());
  }

  private static void setup(FMLCommonSetupEvent event) {
    PacketRegistry.init();
    StorageNetworkCapabilities.initCapabilities();
    CONFIG = new ConfigRegistry(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
    //
    InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("charm").size(2).build());
  }

  private void setupClient(final FMLClientSetupEvent event) {
    MenuScreens.register(SsnRegistry.REQUESTCONTAINER, GuiNetworkTable::new);
    MenuScreens.register(SsnRegistry.FILTERCONTAINER, GuiCableFilter::new);
    MenuScreens.register(SsnRegistry.FILTERIMPORTCONTAINER, GuiCableImportFilter::new);
    MenuScreens.register(SsnRegistry.FILTEREXPORTCONTAINER, GuiCableExportFilter::new);
    MenuScreens.register(SsnRegistry.REMOTE, GuiNetworkRemote::new);
    MenuScreens.register(SsnRegistry.CRAFTINGREMOTE, GuiNetworkCraftingRemote::new);
    MenuScreens.register(SsnRegistry.INVENTORYCONTAINER, GuiNetworkInventory::new);
    MenuScreens.register(SsnRegistry.COLLECTORCTR, GuiCollectionFilter::new);
    ClientRegistry.registerKeyBinding(ClientEventRegistry.INVENTORY_KEY);
  }

  public static void log(String s) {
    if (CONFIG.logspam()) {
      LOGGER.info(s);
    }
  }
}
