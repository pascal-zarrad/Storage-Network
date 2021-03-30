package com.lothrazar.storagenetwork;

import com.lothrazar.storagenetwork.block.cable.export.GuiCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.GuiCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.linkfilter.GuiCableFilter;
import com.lothrazar.storagenetwork.block.collection.GuiCollectionFilter;
import com.lothrazar.storagenetwork.block.inventory.GuiNetworkInventory;
import com.lothrazar.storagenetwork.block.request.GuiNetworkTable;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkCraftingRemote;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkRemote;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.registry.SsnEvents;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(StorageNetwork.MODID)
public class StorageNetwork {

  public static final String MODID = "storagenetwork";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigRegistry CONFIG;
  public static final KeyBinding TEST = new KeyBinding("key.storagenetwork.remote", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.categories.inventory");

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
    ScreenManager.registerFactory(SsnRegistry.REQUESTCONTAINER, GuiNetworkTable::new);
    ScreenManager.registerFactory(SsnRegistry.FILTERCONTAINER, GuiCableFilter::new);
    ScreenManager.registerFactory(SsnRegistry.FILTERIMPORTCONTAINER, GuiCableImportFilter::new);
    ScreenManager.registerFactory(SsnRegistry.FILTEREXPORTCONTAINER, GuiCableExportFilter::new);
    ScreenManager.registerFactory(SsnRegistry.REMOTE, GuiNetworkRemote::new);
    ScreenManager.registerFactory(SsnRegistry.CRAFTINGREMOTE, GuiNetworkCraftingRemote::new);
    ScreenManager.registerFactory(SsnRegistry.INVENTORYCONTAINER, GuiNetworkInventory::new);
    ScreenManager.registerFactory(SsnRegistry.COLLECTORCTR, GuiCollectionFilter::new);
    //
    ClientRegistry.registerKeyBinding(TEST);
  }

  public static void log(String s) {
    if (CONFIG.logspam()) {
      LOGGER.info(s);
    }
  }
}
