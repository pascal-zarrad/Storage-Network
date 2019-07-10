package mrriegel.storagenetwork;
import mrriegel.storagenetwork.apiimpl.StorageNetworkHelpers;
import mrriegel.storagenetwork.block.master.BlockMaster;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.block.request.BlockRequest;
import mrriegel.storagenetwork.block.request.ContainerRequest;
import mrriegel.storagenetwork.block.request.TileRequest;
import mrriegel.storagenetwork.registry.ModBlocks;
import mrriegel.storagenetwork.registry.PacketRegistry;
import mrriegel.storagenetwork.setup.ClientProxy;
import mrriegel.storagenetwork.setup.IProxy;
import mrriegel.storagenetwork.setup.ServerProxy;
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
//, name = StorageNetwork.MODNAME, certificateFingerprint = "@FINGERPRINT@", version = StorageNetwork.VERSION, updateJSON = "https://raw.githubusercontent.com/Lothrazar/Storage-Network/master/update.json")
public class StorageNetwork {

  public static final String MODID = "storagenetwork";
  public static final Logger LOGGER = LogManager.getLogger();
  //  private static final PluginRegistry pluginRegistry = new PluginRegistry();
  public static StorageNetworkHelpers helpers = new StorageNetworkHelpers();
  static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  public StorageNetwork() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(StorageNetwork::setup);
    //only for server starting
    // Load all the plugins by instantiating all annotated instances of IStorageNetworkPlugin
    // AnnotatedInstanceUtil.asmDataTable = event.getAsmData();
    //    pluginRegistry.loadStorageNetworkPlugins();

    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new RegistryEvents());
    PacketRegistry.init();
  }

  private static void setup(FMLCommonSetupEvent event) {
    // some preinit code
    LOGGER.info("HELLO FROM PREINIT");
  }

  @SubscribeEvent
  public static void onServerStarting(FMLServerStartingEvent event) {
    // do something when the server starts
    LOGGER.info("HELLO from server starting");
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
      event.getRegistry().register(new BlockMaster());
      event.getRegistry().register(new BlockRequest());
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().group(ModBlocks.itemGroup);
      event.getRegistry().register(new BlockItem(ModBlocks.master, properties).setRegistryName("master"));
      event.getRegistry().register(new BlockItem(ModBlocks.request, properties).setRegistryName("request"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {

      event.getRegistry().register(TileEntityType.Builder.create(TileMaster::new, ModBlocks.master).build(null).setRegistryName("master"));
      event.getRegistry().register(TileEntityType.Builder.create(TileRequest::new, ModBlocks.request).build(null).setRegistryName("request"));
    }

    @SubscribeEvent
    public static void onContainerRegistry(RegistryEvent.Register<ContainerType<?>> event) {
      event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerRequest(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("request"));
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
