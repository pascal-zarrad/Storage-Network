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
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

public class SsnRegistry {

  public static final int UPGRADE_COUNT = 4;
  public static CreativeModeTab TAB = new CreativeModeTab(StorageNetwork.MODID) {

    @Override
    public ItemStack makeIcon() {
      return new ItemStack(SsnRegistry.REQUEST);
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
  //
  @ObjectHolder(StorageNetwork.MODID + ":speed_upgrade")
  public static ItemUpgrade SPEED_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":stack_upgrade")
  public static ItemUpgrade STACK_UPGRADE;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static BlockEntityType<TileMain> MAINTILEENTITY;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static BlockMain MAIN;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static Block INVENTORY;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static BlockEntityType<TileInventory> INVENTORYTILE;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static MenuType<ContainerNetworkInventory> INVENTORYCONTAINER;
  //request
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockRequest REQUEST;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockEntityType<TileRequest> REQUESTTILE;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static MenuType<ContainerNetworkCraftingTable> REQUESTCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockCable KABEL;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockEntityType<TileCable> KABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":exchange")
  public static Block EXCHANGE;
  @ObjectHolder(StorageNetwork.MODID + ":exchange")
  public static BlockEntityType<TileExchange> EXCHANGETILE;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static Block COLLECTOR;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static BlockEntityType<TileCollection> COLLECTORTILE;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static Block STORAGEKABEL;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static BlockEntityType<TileCableLink> STORAGEKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":import_kabel")
  public static Block IMPORTKABEL;
  @ObjectHolder(StorageNetwork.MODID + ":import_kabel")
  public static BlockEntityType<TileCableIO> IMPORTKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static Block FILTERKABEL;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static BlockEntityType<TileCableFilter> FILTERKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static MenuType<ContainerCableFilter> FILTERCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static Block IMPORTFILTERKABEL;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static BlockEntityType<TileCableImportFilter> FILTERIMPORTKABELTILE;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static MenuType<ContainerCableImportFilter> FILTERIMPORTCONTAINER;
  @ObjectHolder(StorageNetwork.MODID + ":export_kabel")
  public static Block EXPORTKABEL;
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
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
      IForgeRegistry<Block> r = event.getRegistry();
      r.register(new BlockMain());
      r.register(new BlockRequest());
      r.register(new BlockCable("kabel"));
      r.register(new BlockCableLink("storage_kabel"));
      r.register(new BlockCableIO("import_kabel"));
      r.register(new BlockCableImportFilter("import_filter_kabel"));
      r.register(new BlockCableFilter("filter_kabel"));
      r.register(new BlockCableExport("export_kabel"));
      r.register(new BlockInventory("inventory"));
      r.register(new BlockExchange());
      r.register(new BlockCollection());
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().tab(SsnRegistry.TAB);
      IForgeRegistry<Item> r = event.getRegistry();
      r.register(new BlockItem(SsnRegistry.INVENTORY, properties).setRegistryName("inventory"));
      r.register(new BlockItem(SsnRegistry.MAIN, properties).setRegistryName("master"));
      r.register(new BlockItem(SsnRegistry.REQUEST, properties).setRegistryName("request"));
      r.register(new BlockItem(SsnRegistry.KABEL, properties).setRegistryName("kabel"));
      r.register(new BlockItem(SsnRegistry.STORAGEKABEL, properties).setRegistryName("storage_kabel"));
      r.register(new BlockItem(SsnRegistry.IMPORTKABEL, properties).setRegistryName("import_kabel"));
      r.register(new BlockItem(SsnRegistry.IMPORTFILTERKABEL, properties).setRegistryName("import_filter_kabel"));
      r.register(new BlockItem(SsnRegistry.FILTERKABEL, properties).setRegistryName("filter_kabel"));
      r.register(new BlockItem(SsnRegistry.EXPORTKABEL, properties).setRegistryName("export_kabel"));
      r.register(new BlockItem(SsnRegistry.EXCHANGE, properties).setRegistryName("exchange"));
      r.register(new BlockItem(SsnRegistry.COLLECTOR, properties).setRegistryName("collector"));
      //
      r.register(new ItemUpgrade(properties).setRegistryName("stack_upgrade"));
      r.register(new ItemUpgrade(properties).setRegistryName("speed_upgrade"));
      r.register(new ItemStorageCraftingRemote(properties).setRegistryName("inventory_remote"));
      r.register(new ItemStorageCraftingRemote(properties).setRegistryName("crafting_remote"));
      r.register(new ItemPicker(properties).setRegistryName("picker_remote"));
      r.register(new ItemCollector(properties).setRegistryName("collector_remote"));
      r.register(new ItemBuilder(properties).setRegistryName("builder_remote"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<BlockEntityType<?>> event) {
      IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();
      r.register(BlockEntityType.Builder.of(TileMain::new, SsnRegistry.MAIN).build(null).setRegistryName("master"));
      r.register(BlockEntityType.Builder.of(TileInventory::new, SsnRegistry.INVENTORY).build(null).setRegistryName("inventory"));
      r.register(BlockEntityType.Builder.of(TileRequest::new, SsnRegistry.REQUEST).build(null).setRegistryName("request"));
      r.register(BlockEntityType.Builder.of(TileCable::new, SsnRegistry.KABEL).build(null).setRegistryName("kabel"));
      r.register(BlockEntityType.Builder.of(TileCableLink::new, SsnRegistry.STORAGEKABEL).build(null).setRegistryName("storage_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableIO::new, SsnRegistry.IMPORTKABEL).build(null).setRegistryName("import_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableImportFilter::new, SsnRegistry.IMPORTFILTERKABEL).build(null).setRegistryName("import_filter_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableFilter::new, SsnRegistry.FILTERKABEL).build(null).setRegistryName("filter_kabel"));
      r.register(BlockEntityType.Builder.of(TileCableExport::new, SsnRegistry.EXPORTKABEL).build(null).setRegistryName("export_kabel"));
      r.register(BlockEntityType.Builder.of(TileExchange::new, SsnRegistry.EXCHANGE).build(null).setRegistryName("exchange"));
      r.register(BlockEntityType.Builder.of(TileCollection::new, SsnRegistry.COLLECTOR).build(null).setRegistryName("collector"));
    }

    @SubscribeEvent
    public static void onContainerRegistry(RegistryEvent.Register<MenuType<?>> event) {
      IForgeRegistry<MenuType<?>> r = event.getRegistry();
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkCraftingTable(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("request"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerCollectionFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("collector"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerCableFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("filter_kabel"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerCableImportFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("import_filter_kabel"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerCableExportFilter(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("export_kabel"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkInventory(windowId, inv.player.level, data.readBlockPos(), inv, inv.player);
      }).setRegistryName("inventory"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkRemote(windowId, inv.player.getInventory());
      }).setRegistryName("inventory_remote"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkCraftingRemote(windowId, inv.player.getInventory());
      }).setRegistryName("crafting_remote"));
    }
  }
}
