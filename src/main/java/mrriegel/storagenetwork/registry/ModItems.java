package mrriegel.storagenetwork.registry;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.item.ItemUpgrade;
import mrriegel.storagenetwork.item.remote.ItemRemote;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(StorageNetwork.MODID)
public class ModItems {

  @GameRegistry.ObjectHolder("upgrade")
  public static ItemUpgrade upgrade;
  @GameRegistry.ObjectHolder("remote")
  public static ItemRemote remote;
  @GameRegistry.ObjectHolder("picker_remote")
  public static Item picker_remote;
  @GameRegistry.ObjectHolder("collector_remote")
  public static Item collector_remote;
}
