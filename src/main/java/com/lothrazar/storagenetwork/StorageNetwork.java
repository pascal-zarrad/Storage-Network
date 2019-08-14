package com.lothrazar.storagenetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.block.cable.export.BlockCableExport;
import com.lothrazar.storagenetwork.block.cable.export.ContainerCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.export.TileCableExport;
import com.lothrazar.storagenetwork.block.cablefilter.BlockCableFilter;
import com.lothrazar.storagenetwork.block.cablefilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.cablefilter.TileCableFilter;
import com.lothrazar.storagenetwork.block.cablein.BlockCableIO;
import com.lothrazar.storagenetwork.block.cablein.TileCableIO;
import com.lothrazar.storagenetwork.block.cableinfilter.BlockCableImportFilter;
import com.lothrazar.storagenetwork.block.cableinfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.cableinfilter.TileCableImportFilter;
import com.lothrazar.storagenetwork.block.cablelink.BlockCableLink;
import com.lothrazar.storagenetwork.block.cablelink.TileCableLink;
import com.lothrazar.storagenetwork.block.master.BlockMaster;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.block.request.BlockRequest;
import com.lothrazar.storagenetwork.block.request.ContainerRequest;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.setup.ClientProxy;
import com.lothrazar.storagenetwork.setup.IProxy;
import com.lothrazar.storagenetwork.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(StorageNetwork.MODID)
public class StorageNetwork {

  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MODID = "storagenetwork";
  static final String certificateFingerprint = "@FINGERPRINT@";
  static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  public StorageNetwork() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(StorageNetwork::setup);
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new RegistryEvents());
  }

  private static void setup(FMLCommonSetupEvent event) {
    PacketRegistry.init();
    StorageNetworkCapabilities.initCapabilities();
    proxy.init();
  }

  @SubscribeEvent
  public static void onServerStarting(FMLServerStartingEvent event) {}

  static boolean logspam = true;

  public static void log(String s) {
    if (logspam) {
      LOGGER.info(s);
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
      event.getRegistry().register(new BlockMaster());
      event.getRegistry().register(new BlockRequest());
      event.getRegistry().register(new BlockCable("kabel"));
      event.getRegistry().register(new BlockCableLink("storage_kabel"));
      event.getRegistry().register(new BlockCableIO("import_kabel"));
      event.getRegistry().register(new BlockCableImportFilter("import_filter_kabel"));
      event.getRegistry().register(new BlockCableFilter("filter_kabel"));
      event.getRegistry().register(new BlockCableExport("export_kabel"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().group(SsnRegistry.itemGroup);
      IForgeRegistry<Item> r = event.getRegistry();
      r.register(new BlockItem(SsnRegistry.master, properties).setRegistryName("master"));
      r.register(new BlockItem(SsnRegistry.request, properties).setRegistryName("request"));
      r.register(new BlockItem(SsnRegistry.kabel, properties).setRegistryName("kabel"));
      r.register(new BlockItem(SsnRegistry.storagekabel, properties).setRegistryName("storage_kabel"));
      r.register(new BlockItem(SsnRegistry.importkabel, properties).setRegistryName("import_kabel"));
      r.register(new BlockItem(SsnRegistry.importfilterkabel, properties).setRegistryName("import_filter_kabel"));
      r.register(new BlockItem(SsnRegistry.filterkabel, properties).setRegistryName("filter_kabel"));
      r.register(new BlockItem(SsnRegistry.exportkabel, properties).setRegistryName("export_kabel"));
      //up 
      //      r.register(new ItemUpgrade(properties).setRegistryName("operation_upgrade"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("stock_upgrade"));
      r.register(new ItemUpgrade(properties).setRegistryName("stack_upgrade"));
      r.register(new ItemUpgrade(properties).setRegistryName("speed_upgrade"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("dimension_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("inventory_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("shortrange_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("longrange_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("dimension_craft_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("inventory_craft_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("shortrange_craft_remote"));
      //      r.register(new ItemUpgrade(properties).setRegistryName("longrange_craft_remote"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
      event.getRegistry().register(TileEntityType.Builder.create(TileMaster::new, SsnRegistry.master).build(null).setRegistryName("master"));
      event.getRegistry().register(TileEntityType.Builder.create(TileRequest::new, SsnRegistry.request).build(null).setRegistryName("request"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCable::new, SsnRegistry.kabel).build(null).setRegistryName("kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableLink::new, SsnRegistry.storagekabel).build(null).setRegistryName("storage_kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableIO::new, SsnRegistry.importkabel).build(null).setRegistryName("import_kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableImportFilter::new, SsnRegistry.importfilterkabel).build(null).setRegistryName("import_filter_kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableFilter::new, SsnRegistry.filterkabel).build(null).setRegistryName("filter_kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableExport::new, SsnRegistry.exportkabel).build(null).setRegistryName("export_kabel"));
    }

    @SubscribeEvent
    public static void onContainerRegistry(RegistryEvent.Register<ContainerType<?>> event) {
      event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerRequest(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("request"));
      //
      event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerCableFilter(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("filter_kabel"));
      event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerCableImportFilter(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("import_filter_kabel"));
      event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerCableExportFilter(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("export_kabel"));
    }
  }

  @SubscribeEvent
  public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
    // https://tutorials.darkhax.net/tutorials/jar_signing/
    String source = (event.getSource() == null) ? "" : event.getSource().getName() + " ";
    String msg = "Storage Network: Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
    System.out.println(msg);
  }

  public static void chatMessage(PlayerEntity player, String message) {
    if (player.world.isRemote) {
      player.sendMessage(new TranslationTextComponent((message)));
    }
  }

  public static void statusMessage(PlayerEntity player, String message) {
    if (player.world.isRemote) {
      player.sendStatusMessage(new TranslationTextComponent((message)), true);
    }
  }

  public static String lang(String message) {
    TranslationTextComponent t = new TranslationTextComponent(message);
    return t.getFormattedText();
  }
}
