package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.block.cable.export.ContainerCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.input.TileCableIO;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ContainerCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.TileCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.storagefilter.ContainerCableFilter;
import com.lothrazar.storagenetwork.block.cable.storagefilter.TileCableFilter;
import com.lothrazar.storagenetwork.block.cablelink.TileCableLink;
import com.lothrazar.storagenetwork.block.master.BlockMaster;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.block.request.BlockRequest;
import com.lothrazar.storagenetwork.block.request.ContainerRequest;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import com.lothrazar.storagenetwork.item.ItemUpgrade;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class SsnRegistry {

  public static final int UPGRADE_COUNT = 4;
  public static ItemGroup itemGroup = new ItemGroup(StorageNetwork.MODID) {

    @Override
    public ItemStack createIcon() {
      return new ItemStack(SsnRegistry.request);
    }
  };
  @ObjectHolder(StorageNetwork.MODID + ":speed_upgrade")
  public static ItemUpgrade speed_upgrade;
  @ObjectHolder(StorageNetwork.MODID + ":stack_upgrade")
  public static ItemUpgrade stack_upgrade;
  @ObjectHolder(StorageNetwork.MODID + ":stock_upgrade")
  public static ItemUpgrade stock_upgrade;
  @ObjectHolder(StorageNetwork.MODID + ":operation_upgrade")
  public static ItemUpgrade operation_upgrade;
  // master
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static TileEntityType<TileMaster> mastertile;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static BlockMaster master;
  //request
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockRequest request;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static TileEntityType<TileRequest> requesttile;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static ContainerType<ContainerRequest> requestcontainer;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockCable kabel;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static TileEntityType<TileCable> kabeltile;
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
}
