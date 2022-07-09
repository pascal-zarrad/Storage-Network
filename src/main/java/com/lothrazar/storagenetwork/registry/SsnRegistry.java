package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.block.cable.export.BlockCableExport;
import com.lothrazar.storagenetwork.block.cable.export.ContainerCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.export.TileCableExport;
import com.lothrazar.storagenetwork.block.cable.input.BlockCableIO;
import com.lothrazar.storagenetwork.block.cable.input.TileCableIO;
import com.lothrazar.storagenetwork.block.cable.inputfilter.BlockCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.TileCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.link.BlockCableLink;
import com.lothrazar.storagenetwork.block.cable.link.TileCableLink;
import com.lothrazar.storagenetwork.block.cable.linkfilter.BlockCableFilter;
import com.lothrazar.storagenetwork.block.cable.linkfilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.cable.linkfilter.TileCableFilter;
import com.lothrazar.storagenetwork.block.collection.BlockCollection;
import com.lothrazar.storagenetwork.block.collection.ContainerCollectionFilter;
import com.lothrazar.storagenetwork.block.collection.TileCollection;
import com.lothrazar.storagenetwork.block.exchange.BlockExchange;
import com.lothrazar.storagenetwork.block.exchange.TileExchange;
import com.lothrazar.storagenetwork.block.inventory.BlockInventory;
import com.lothrazar.storagenetwork.block.inventory.ContainerNetworkInventory;
import com.lothrazar.storagenetwork.block.inventory.TileInventory;
import com.lothrazar.storagenetwork.block.main.BlockMain;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.block.request.BlockRequest;
import com.lothrazar.storagenetwork.block.request.ContainerNetworkCraftingTable;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.item.ItemBuilder;
import com.lothrazar.storagenetwork.item.ItemCollector;
import com.lothrazar.storagenetwork.item.ItemPicker;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import com.lothrazar.storagenetwork.item.remote.ContainerNetworkCraftingRemote;
import com.lothrazar.storagenetwork.item.remote.ContainerNetworkRemote;
import com.lothrazar.storagenetwork.item.remote.ItemStorageCraftingRemote;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class SsnRegistry {

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StorageNetwork.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StorageNetwork.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, StorageNetwork.MODID);
  public static CreativeModeTab TAB = new CreativeModeTab(StorageNetwork.MODID) {

    @Override
    public ItemStack makeIcon() {
      return new ItemStack(SsnRegistry.Items.REQUEST.get());
    }
  };
  @ObjectHolder(StorageNetwork.MODID + ":builder_remote")
  public static ItemBuilder BUILDER_REMOTE;
  @ObjectHolder(StorageNetwork.MODID + ":inventory_remote")
  public static ItemStorageCraftingRemote INVENTORY_REMOTE;
  @ObjectHolder(StorageNetwork.MODID + ":crafting_remote")
  public static ItemStorageCraftingRemote CRAFTING_REMOTE;
  @ObjectHolder(StorageNetwork.MODID + ":collector_remote")
  public static ItemCollector COLLECTOR_REMOTE;
  @ObjectHolder(StorageNetwork.MODID + ":slow_upgrade")
  public static ItemUpgrade SLOW_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":speed_upgrade")
  public static ItemUpgrade SPEED_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":single_upgrade")
  public static ItemUpgrade SINGLE_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":stack_upgrade")
  public static ItemUpgrade STACK_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":stock_upgrade")
  public static ItemUpgrade STOCK_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":operation_upgrade")
  public static ItemUpgrade OP_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static BlockEntityType<TileMain> MAINTILEENTITY;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static BlockEntityType<TileInventory> INVENTORYTILE;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static MenuType<ContainerNetworkInventory> INVENTORYCONTAINER;

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class Blocks {

    public static final RegistryObject<Block> REQUEST = BLOCKS.register("request", () -> new BlockRequest());
    public static final RegistryObject<Block> KABEL = BLOCKS.register("kabel", () -> new BlockCable());
    public static final RegistryObject<Block> MASTER = BLOCKS.register("master", () -> new BlockMain());
    public static final RegistryObject<Block> STORAGE_KABEL = BLOCKS.register("storage_kabel", () -> new BlockCableLink());
    public static final RegistryObject<Block> IMPORT_KABEL = BLOCKS.register("import_kabel", () -> new BlockCableIO());
    public static final RegistryObject<Block> IMPORT_FILTER_KABEL = BLOCKS.register("import_filter_kabel", () -> new BlockCableImportFilter());
    public static final RegistryObject<Block> FILTER_KABEL = BLOCKS.register("filter_kabel", () -> new BlockCableFilter());
    public static final RegistryObject<Block> EXPORT_KABEL = BLOCKS.register("export_kabel", () -> new BlockCableExport());
    public static final RegistryObject<Block> INVENTORY = BLOCKS.register("inventory", () -> new BlockInventory());
    public static final RegistryObject<Block> EXCHANGE = BLOCKS.register("exchange", () -> new BlockExchange());
    public static final RegistryObject<Block> COLLECTOR = BLOCKS.register("collector", () -> new BlockCollection());
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class Items {

    public static final RegistryObject<Item> REQUEST = ITEMS.register("request", () -> new BlockItem(Blocks.REQUEST.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> KABEL = ITEMS.register("kabel", () -> new BlockItem(Blocks.KABEL.get(), new Item.Properties().tab(TAB)));
  }

  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockEntityType<TileRequest> REQUESTTILE;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static MenuType<ContainerNetworkCraftingTable> REQUESTCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockEntityType<TileCable> KABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":exchange")
  public static BlockEntityType<TileExchange> EXCHANGETILE;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static BlockEntityType<TileCollection> COLLECTORTILE;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static BlockEntityType<TileCableLink> STORAGEKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":import_kabel")
  public static BlockEntityType<TileCableIO> IMPORTKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static BlockEntityType<TileCableFilter> FILTERKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static MenuType<ContainerCableFilter> FILTERCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static BlockEntityType<TileCableImportFilter> FILTERIMPORTKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static MenuType<ContainerCableImportFilter> FILTERIMPORTCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":export_kabel")
  public static BlockEntityType<TileCableExport> EXPORTKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":export_kabel")
  public static MenuType<ContainerCableExportFilter> FILTEREXPORTCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":inventory_remote")
  public static MenuType<ContainerNetworkRemote> REMOTE;
  @ObjectHolder(StorageNetwork.MODID + ":crafting_remote")
  public static MenuType<ContainerNetworkCraftingRemote> CRAFTINGREMOTE;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static MenuType<ContainerCollectionFilter> COLLECTORCTR;

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      r.register(new BlockItem(Blocks.INVENTORY.get(), new Item.Properties().tab(TAB)).setRegistryName("inventory"));
      r.register(new BlockItem(Blocks.MASTER.get(), new Item.Properties().tab(TAB)).setRegistryName("master"));
      r.register(new BlockItem(Blocks.STORAGE_KABEL.get(), new Item.Properties().tab(TAB)).setRegistryName("storage_kabel"));
      r.register(new BlockItem(Blocks.IMPORT_KABEL.get(), new Item.Properties().tab(TAB)).setRegistryName("import_kabel"));
      r.register(new BlockItem(Blocks.IMPORT_FILTER_KABEL.get(), new Item.Properties().tab(TAB)).setRegistryName("import_filter_kabel"));
      r.register(new BlockItem(Blocks.FILTER_KABEL.get(), new Item.Properties().tab(TAB)).setRegistryName("filter_kabel"));
      r.register(new BlockItem(Blocks.EXPORT_KABEL.get(), new Item.Properties().tab(TAB)).setRegistryName("export_kabel"));
      r.register(new BlockItem(Blocks.EXCHANGE.get(), new Item.Properties().tab(TAB)).setRegistryName("exchange"));
      r.register(new BlockItem(Blocks.COLLECTOR.get(), new Item.Properties().tab(TAB)).setRegistryName("collector"));
      r.register(new ItemUpgrade(new Item.Properties().tab(TAB)).setRegistryName("stack_upgrade"));
      r.register(new ItemUpgrade(new Item.Properties().tab(TAB)).setRegistryName("speed_upgrade"));
      r.register(new ItemUpgrade(new Item.Properties().tab(TAB)).setRegistryName("slow_upgrade"));
      r.register(new ItemUpgrade(new Item.Properties().tab(TAB)).setRegistryName("stock_upgrade"));
      r.register(new ItemUpgrade(new Item.Properties().tab(TAB)).setRegistryName("operation_upgrade"));
      r.register(new ItemUpgrade(new Item.Properties().tab(TAB)).setRegistryName("single_upgrade"));
      r.register(new ItemStorageCraftingRemote(new Item.Properties().tab(TAB)).setRegistryName("inventory_remote"));
      r.register(new ItemStorageCraftingRemote(new Item.Properties().tab(TAB)).setRegistryName("crafting_remote"));
      r.register(new ItemPicker(new Item.Properties().tab(TAB)).setRegistryName("picker_remote"));
      r.register(new ItemCollector(new Item.Properties().tab(TAB)).setRegistryName("collector_remote"));
      r.register(new ItemBuilder(new Item.Properties().tab(TAB)).setRegistryName("builder_remote"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<BlockEntityType<?>> event) {
      IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();
      r.register(BlockEntityType.Builder.of(TileMain::new, Blocks.MASTER.get()).build(null).setRegistryName("master"));
      r.register(BlockEntityType.Builder.of(TileInventory::new, Blocks.INVENTORY.get()).build(null).setRegistryName("inventory"));
      r.register(BlockEntityType.Builder.of(TileRequest::new, Blocks.REQUEST.get()).build(null).setRegistryName("request"));
      r.register(BlockEntityType.Builder.of(TileCable::new, SsnRegistry.Blocks.KABEL.get()).build(null).setRegistryName("kabel"));
      r.register(BlockEntityType.Builder.of(TileCableLink::new, Blocks.STORAGE_KABEL.get()).build(null).setRegistryName("storage_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableIO::new, Blocks.IMPORT_KABEL.get()).build(null).setRegistryName("import_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableImportFilter::new, Blocks.IMPORT_FILTER_KABEL.get()).build(null).setRegistryName("import_filter_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableFilter::new, Blocks.FILTER_KABEL.get()).build(null).setRegistryName("filter_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableExport::new, Blocks.EXPORT_KABEL.get()).build(null).setRegistryName("export_kabel"));
      r.register(BlockEntityType.Builder.of(TileExchange::new, Blocks.EXCHANGE.get()).build(null).setRegistryName("exchange"));
      r.register(BlockEntityType.Builder.of(TileCollection::new, Blocks.COLLECTOR.get()).build(null).setRegistryName("collector"));
    }

    @SubscribeEvent
    public static void onContainerRegistry(RegistryEvent.Register<MenuType<?>> event) {
      IForgeRegistry<MenuType<?>> r = event.getRegistry();
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerNetworkCraftingTable(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("request"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerCollectionFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("collector"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerCableFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("filter_kabel"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerCableImportFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("import_filter_kabel"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerCableExportFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("export_kabel"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerNetworkInventory(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("inventory"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerNetworkRemote(windowId, inv.player.getInventory());
      }).setRegistryName("inventory_remote"));
      r.register(IForgeMenuType.create((windowId, inv, data) -> {
        return new ContainerNetworkCraftingRemote(windowId, inv.player.getInventory());
      }).setRegistryName("crafting_remote"));
    }
  }
}
