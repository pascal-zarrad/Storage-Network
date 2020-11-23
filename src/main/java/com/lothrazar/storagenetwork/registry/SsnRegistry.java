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
import com.lothrazar.storagenetwork.item.remote.ItemRemote;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

public class SsnRegistry {

  public static final int UPGRADE_COUNT = 4;
  public static ItemGroup itemGroup = new ItemGroup(StorageNetwork.MODID) {

    @Override
    public ItemStack createIcon() {
      return new ItemStack(SsnRegistry.request);
    }
  };
  @ObjectHolder(StorageNetwork.MODID + ":inventory_remote")
  public static Item inventory_remote;
  @ObjectHolder(StorageNetwork.MODID + ":crafting_remote")
  public static Item crafting_remote;
  @ObjectHolder(StorageNetwork.MODID + ":collector_remote")
  public static ItemCollector collector_remote;
  //
  @ObjectHolder(StorageNetwork.MODID + ":speed_upgrade")
  public static ItemUpgrade speed_upgrade;
  @ObjectHolder(StorageNetwork.MODID + ":stack_upgrade")
  public static ItemUpgrade stack_upgrade;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static TileEntityType<TileMain> mainTileentity;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static BlockMain main;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static Block inventory;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static TileEntityType<TileInventory> inventorytile;
  @ObjectHolder(StorageNetwork.MODID + ":inventory")
  public static ContainerType<ContainerNetworkInventory> inventorycontainer;
  //request
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockRequest request;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static TileEntityType<TileRequest> requesttile;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static ContainerType<ContainerNetworkCraftingTable> requestcontainer;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockCable kabel;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static TileEntityType<TileCable> kabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":exchange")
  public static Block exchange;
  @ObjectHolder(StorageNetwork.MODID + ":exchange")
  public static TileEntityType<TileExchange> exchangetile;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static Block collector;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static TileEntityType<TileCollection> collectortile;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static Block storagekabel;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static TileEntityType<TileCableLink> storagekabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":import_kabel")
  public static Block importkabel;
  @ObjectHolder(StorageNetwork.MODID + ":import_kabel")
  public static TileEntityType<TileCableIO> importkabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static Block filterkabel;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static TileEntityType<TileCableFilter> filterkabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":filter_kabel")
  public static ContainerType<ContainerCableFilter> filterContainer;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static Block importfilterkabel;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static TileEntityType<TileCableImportFilter> filterimportkabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":import_filter_kabel")
  public static ContainerType<ContainerCableImportFilter> filterimportContainer;
  @ObjectHolder(StorageNetwork.MODID + ":export_kabel")
  public static Block exportkabel;
  @ObjectHolder(StorageNetwork.MODID + ":export_kabel")
  public static TileEntityType<TileCableImportFilter> exportkabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":export_kabel")
  public static ContainerType<ContainerCableExportFilter> filterexportContainer;
  @ObjectHolder(StorageNetwork.MODID + ":inventory_remote")
  public static ContainerType<ContainerNetworkRemote> remote;
  @ObjectHolder(StorageNetwork.MODID + ":crafting_remote")
  public static ContainerType<ContainerNetworkCraftingRemote> craftingremote;
  @ObjectHolder(StorageNetwork.MODID + ":collector")
  public static ContainerType<ContainerCollectionFilter> collectorCtr;

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
      Item.Properties properties = new Item.Properties().group(SsnRegistry.itemGroup);
      IForgeRegistry<Item> r = event.getRegistry();
      r.register(new BlockItem(SsnRegistry.inventory, properties).setRegistryName("inventory"));
      r.register(new BlockItem(SsnRegistry.main, properties).setRegistryName("master"));
      r.register(new BlockItem(SsnRegistry.request, properties).setRegistryName("request"));
      r.register(new BlockItem(SsnRegistry.kabel, properties).setRegistryName("kabel"));
      r.register(new BlockItem(SsnRegistry.storagekabel, properties).setRegistryName("storage_kabel"));
      r.register(new BlockItem(SsnRegistry.importkabel, properties).setRegistryName("import_kabel"));
      r.register(new BlockItem(SsnRegistry.importfilterkabel, properties).setRegistryName("import_filter_kabel"));
      r.register(new BlockItem(SsnRegistry.filterkabel, properties).setRegistryName("filter_kabel"));
      r.register(new BlockItem(SsnRegistry.exportkabel, properties).setRegistryName("export_kabel"));
      r.register(new BlockItem(SsnRegistry.exchange, properties).setRegistryName("exchange"));
      r.register(new BlockItem(SsnRegistry.collector, properties).setRegistryName("collector"));
      //
      r.register(new ItemUpgrade(properties).setRegistryName("stack_upgrade"));
      r.register(new ItemUpgrade(properties).setRegistryName("speed_upgrade"));
      r.register(new ItemRemote(properties).setRegistryName("inventory_remote"));
      r.register(new ItemRemote(properties).setRegistryName("crafting_remote"));
      r.register(new ItemPicker(properties).setRegistryName("picker_remote"));
      r.register(new ItemCollector(properties).setRegistryName("collector_remote"));
      r.register(new ItemBuilder(properties).setRegistryName("builder_remote"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
      IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
      r.register(TileEntityType.Builder.create(TileMain::new, SsnRegistry.main).build(null).setRegistryName("master"));
      r.register(TileEntityType.Builder.create(TileInventory::new, SsnRegistry.inventory).build(null).setRegistryName("inventory"));
      r.register(TileEntityType.Builder.create(TileRequest::new, SsnRegistry.request).build(null).setRegistryName("request"));
      r.register(TileEntityType.Builder.create(TileCable::new, SsnRegistry.kabel).build(null).setRegistryName("kabel"));
      r.register(TileEntityType.Builder.create(TileCableLink::new, SsnRegistry.storagekabel).build(null).setRegistryName("storage_kabel"));
      r.register(TileEntityType.Builder.create(TileCableIO::new, SsnRegistry.importkabel).build(null).setRegistryName("import_kabel"));
      r.register(TileEntityType.Builder.create(TileCableImportFilter::new, SsnRegistry.importfilterkabel).build(null).setRegistryName("import_filter_kabel"));
      r.register(TileEntityType.Builder.create(TileCableFilter::new, SsnRegistry.filterkabel).build(null).setRegistryName("filter_kabel"));
      r.register(TileEntityType.Builder.create(TileCableExport::new, SsnRegistry.exportkabel).build(null).setRegistryName("export_kabel"));
      r.register(TileEntityType.Builder.create(TileExchange::new, SsnRegistry.exchange).build(null).setRegistryName("exchange"));
      r.register(TileEntityType.Builder.create(TileCollection::new, SsnRegistry.collector).build(null).setRegistryName("collector"));
    }

    @SubscribeEvent
    public static void onContainerRegistry(RegistryEvent.Register<ContainerType<?>> event) {
      IForgeRegistry<ContainerType<?>> r = event.getRegistry();
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkCraftingTable(windowId, StorageNetwork.proxy.getClientWorld(), data.readBlockPos(), inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("request"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerCollectionFilter(windowId, StorageNetwork.proxy.getClientWorld(), data.readBlockPos(), inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("collector"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerCableFilter(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("filter_kabel"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerCableImportFilter(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("import_filter_kabel"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerCableExportFilter(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("export_kabel"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new ContainerNetworkInventory(windowId, StorageNetwork.proxy.getClientWorld(), pos, inv, StorageNetwork.proxy.getClientPlayer());
      }).setRegistryName("inventory"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkRemote(windowId, StorageNetwork.proxy.getClientPlayer().inventory);
      }).setRegistryName("inventory_remote"));
      r.register(IForgeContainerType.create((windowId, inv, data) -> {
        return new ContainerNetworkCraftingRemote(windowId, StorageNetwork.proxy.getClientPlayer().inventory);
      }).setRegistryName("crafting_remote"));
    }
  }
}
