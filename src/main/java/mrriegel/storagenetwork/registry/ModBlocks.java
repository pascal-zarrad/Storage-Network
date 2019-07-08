package mrriegel.storagenetwork.registry;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.cable.BlockCable;
import mrriegel.storagenetwork.block.cable.link.BlockCableLink;
import mrriegel.storagenetwork.block.master.BlockMaster;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.block.request.BlockRequest;
import mrriegel.storagenetwork.block.request.TileRequest;
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
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static BlockRequest request;
  @ObjectHolder(StorageNetwork.MODID + ":request")
  public static TileEntityType<TileRequest> requesttile;
  @ObjectHolder(StorageNetwork.MODID + ":kabel")
  public static BlockCable kabel;
  @ObjectHolder(StorageNetwork.MODID + ":storage_kabel")
  public static BlockCableLink storageKabel;
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
