package mrriegel.storagenetwork.registry;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.network.CableControlMessage;
import mrriegel.storagenetwork.network.CableDataMessage;
import mrriegel.storagenetwork.network.CableFilterMessage;
import mrriegel.storagenetwork.network.CableLimitMessage;
import mrriegel.storagenetwork.network.CableRefreshClientMessage;
import mrriegel.storagenetwork.network.ClearRecipeMessage;
import mrriegel.storagenetwork.network.InsertMessage;
import mrriegel.storagenetwork.network.RecipeMessage;
import mrriegel.storagenetwork.network.RefreshFilterClientMessage;
import mrriegel.storagenetwork.network.RequestCableMessage;
import mrriegel.storagenetwork.network.RequestMessage;
import mrriegel.storagenetwork.network.SortMessage;
import mrriegel.storagenetwork.network.StackRefreshClientMessage;
import mrriegel.storagenetwork.network.StackResponseClientMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketRegistry {

  private static final String PROTOCOL_VERSION = Integer.toString(1);
  //  public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(StorageNetwork.MODID);
  private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
      .named(new ResourceLocation(StorageNetwork.MODID, "main_channel"))
      .clientAcceptedVersions(PROTOCOL_VERSION::equals)
      .serverAcceptedVersions(PROTOCOL_VERSION::equals)
      .networkProtocolVersion(() -> PROTOCOL_VERSION)
      .simpleChannel();

  public static void init() {
    // TODO:
    //	HANDLER.registerMessage(disc++, KeyPressPKT.class, KeyPressPKT::encode, KeyPressPKT::decode, KeyPressPKT.Handler::handle);
    //https://gist.github.com/williewillus/353c872bcf1a6ace9921189f6100d09a
    int id = 0;
    INSTANCE.registerMessage(id++, CableDataMessage.class, CableDataMessage::encode, CableDataMessage::decode, CableDataMessage.Handler::handle);
    INSTANCE.registerMessage(StackRefreshClientMessage.class, StackRefreshClientMessage.class, id++, Dist.CLIENT);
    INSTANCE.registerMessage(RequestMessage.class, RequestMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(ClearRecipeMessage.class, ClearRecipeMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(SortMessage.class, SortMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(RecipeMessage.class, RecipeMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(CableLimitMessage.class, CableLimitMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(InsertMessage.class, InsertMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(StackResponseClientMessage.class, StackResponseClientMessage.class, id++, Dist.CLIENT);
    INSTANCE.registerMessage(CableFilterMessage.class, CableFilterMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(CableRefreshClientMessage.class, CableRefreshClientMessage.class, id++, Dist.CLIENT);
    INSTANCE.registerMessage(RequestCableMessage.class, RequestCableMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(CableControlMessage.class, CableControlMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(RefreshFilterClientMessage.class, RefreshFilterClientMessage.class, id++, Dist.CLIENT);
  }
}
