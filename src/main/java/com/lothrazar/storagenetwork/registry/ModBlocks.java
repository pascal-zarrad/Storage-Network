package com.lothrazar.storagenetwork.registry;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.cable.BlockCable;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.block.cablelink.TileCableLink;
import com.lothrazar.storagenetwork.block.master.BlockMaster;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.block.request.BlockRequest;
import com.lothrazar.storagenetwork.block.request.ContainerRequest;
import com.lothrazar.storagenetwork.block.request.TileRequest;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

  public static ItemGroup itemGroup = new ItemGroup(StorageNetwork.MODID) {

    @Override
    public ItemStack createIcon() {
      return new ItemStack(ModBlocks.master);
    }
  };
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static TileEntityType<TileMaster> mastertile;
  @ObjectHolder(StorageNetwork.MODID + ":master")
  public static BlockMaster master;
  //
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockRequest request;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static TileEntityType<TileRequest> requesttile;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static ContainerType<ContainerRequest> requestcontainer;
  //
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockCable kabel;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static TileEntityType<TileCable> kabeltile;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static Block storagekabel;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static TileEntityType<TileCableLink> storagekabeltile;
  //
  @ObjectHolder(StorageNetwork.MODID + ":import_kabel")
  public static Block importkabel;
  //
  //  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  //  public static BlockCable kabel;
  //  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  //  public static BlockCableLink storageKabel;
  // @GameRegistry.ObjectHolder("storage_kabel_plain")
  // public static BlockCableLinkPlain storage_kabel_plain;
  //  @ObjectHolder("ex_kabel")
  //  public static BlockCableIO exKabel;
  //  @ObjectHolder("im_kabel")
  //  public static BlockCableIO imKabel;
  //  @ObjectHolder("process_kabel")
  //  public static BlockCable processKabel;
  //  @ObjectHolder("controller")
  //  public static BlockControl controller;
}
