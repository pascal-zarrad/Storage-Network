package com.lothrazar.storagenetwork;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.block.cablefilter.BlockCableFilter;
import com.lothrazar.storagenetwork.block.cablefilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.cablefilter.TileCableFilter;
import com.lothrazar.storagenetwork.block.cableio.BlockCableIO;
import com.lothrazar.storagenetwork.block.cableio.TileCableIO;
import com.lothrazar.storagenetwork.block.cablelink.BlockCableLink;
import com.lothrazar.storagenetwork.block.cablelink.TileCableLink;
import com.lothrazar.storagenetwork.block.master.BlockMaster;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.block.request.BlockRequest;
import com.lothrazar.storagenetwork.block.request.ContainerRequest;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.capabilities.StorageNetworkCapabilities;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StorageNetwork.MODID)
public class StorageNetwork {

  private String certificateFingerprint = "@FINGERPRINT@";
  public static final String MODID = "storagenetwork";
  public static final Logger LOGGER = LogManager.getLogger();
  //  private static final PluginRegistry pluginRegistry = new PluginRegistry();
  //  public static UtilTileEntity helpers = new UtilTileEntity();
  static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  public StorageNetwork() {
    // Register the setup method for modloading
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
  public static void onServerStarting(FMLServerStartingEvent event) {
    // do something when the server starts
  }

  static boolean logspam = true;

  public static void log(String s) {
    if (logspam) {
      LOGGER.info(s);
    }
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
      event.getRegistry().register(new BlockMaster());
      event.getRegistry().register(new BlockRequest());
      //event.getRegistry().register(new BlockInventory());
      event.getRegistry().register(new BlockCable("kabel"));
      event.getRegistry().register(new BlockCableLink("storage_kabel"));
      event.getRegistry().register(new BlockCableIO("import_kabel"));
      event.getRegistry().register(new BlockCableFilter("filter_kabel"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().group(SsnRegistry.itemGroup);
      event.getRegistry().register(new BlockItem(SsnRegistry.master, properties).setRegistryName("master"));
      event.getRegistry().register(new BlockItem(SsnRegistry.request, properties).setRegistryName("request"));
      //      event.getRegistry().register(new BlockItem(SsnRegistry.inventory, properties).setRegistryName("inventory"));
      event.getRegistry().register(new BlockItem(SsnRegistry.kabel, properties).setRegistryName("kabel"));
      event.getRegistry().register(new BlockItem(SsnRegistry.storagekabel, properties).setRegistryName("storage_kabel"));
      event.getRegistry().register(new BlockItem(SsnRegistry.importkabel, properties).setRegistryName("import_kabel"));
      event.getRegistry().register(new BlockItem(SsnRegistry.filterkabel, properties).setRegistryName("filter_kabel"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
      event.getRegistry().register(TileEntityType.Builder.create(TileMaster::new, SsnRegistry.master).build(null).setRegistryName("master"));
      event.getRegistry().register(TileEntityType.Builder.create(TileRequest::new, SsnRegistry.request).build(null).setRegistryName("request"));
      //event.getRegistry().register(TileEntityType.Builder.create(TileInventory::new, SsnRegistry.inventory).build(null).setRegistryName("inventory"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCable::new, SsnRegistry.kabel).build(null).setRegistryName("kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableLink::new, SsnRegistry.storagekabel).build(null).setRegistryName("storage_kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableIO::new, SsnRegistry.importkabel).build(null).setRegistryName("import_kabel"));
      event.getRegistry().register(TileEntityType.Builder.create(TileCableFilter::new, SsnRegistry.filterkabel).build(null).setRegistryName("filter_kabel"));
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
