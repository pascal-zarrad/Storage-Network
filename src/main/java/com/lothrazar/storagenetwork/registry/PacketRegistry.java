package com.lothrazar.storagenetwork.registry;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.network.CableDataMessage;
import com.lothrazar.storagenetwork.network.CableFilterMessage;
import com.lothrazar.storagenetwork.network.CableLimitMessage;
import com.lothrazar.storagenetwork.network.ClearRecipeMessage;
import com.lothrazar.storagenetwork.network.InsertMessage;
import com.lothrazar.storagenetwork.network.RecipeMessage;
import com.lothrazar.storagenetwork.network.RefreshFilterClientMessage;
import com.lothrazar.storagenetwork.network.RequestMessage;
import com.lothrazar.storagenetwork.network.SortMessage;
import com.lothrazar.storagenetwork.network.StackRefreshClientMessage;
import com.lothrazar.storagenetwork.network.StackResponseClientMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketRegistry {

  private static final String PROTOCOL_VERSION = Integer.toString(1);
  //  public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(StorageNetwork.MODID);
  public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
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
    INSTANCE.registerMessage(id++, StackRefreshClientMessage.class, StackRefreshClientMessage::encode, StackRefreshClientMessage::decode, StackRefreshClientMessage::handle);
    INSTANCE.registerMessage(id++, InsertMessage.class, InsertMessage::encode, InsertMessage::decode, InsertMessage::handle);
    INSTANCE.registerMessage(id++, RequestMessage.class, RequestMessage::encode, RequestMessage::decode, RequestMessage::handle);
    INSTANCE.registerMessage(id++, ClearRecipeMessage.class, ClearRecipeMessage::encode, ClearRecipeMessage::decode, ClearRecipeMessage::handle);
    INSTANCE.registerMessage(id++, SortMessage.class, SortMessage::encode, SortMessage::decode, SortMessage::handle);
    INSTANCE.registerMessage(id++, RecipeMessage.class, RecipeMessage::encode, RecipeMessage::decode, RecipeMessage::handle);
    INSTANCE.registerMessage(id++, CableFilterMessage.class, CableFilterMessage::encode, CableFilterMessage::decode, CableFilterMessage::handle);
    INSTANCE.registerMessage(id++, CableLimitMessage.class, CableLimitMessage::encode, CableLimitMessage::decode, CableLimitMessage::handle);
    INSTANCE.registerMessage(id++, StackResponseClientMessage.class, StackResponseClientMessage::encode, StackResponseClientMessage::decode, StackResponseClientMessage::handle);
    //    INSTANCE.registerMessage(CableRefreshClientMessage.class, CableRefreshClientMessage.class, id++, Dist.CLIENT);
    //    INSTANCE.registerMessage(RequestCableMessage.class, RequestCableMessage.class, id++, Dist.DEDICATED_SERVER);
    //    INSTANCE.registerMessage(CableControlMessage.class, CableControlMessage.class, id++, Dist.DEDICATED_SERVER);
    INSTANCE.registerMessage(id++, RefreshFilterClientMessage.class, RefreshFilterClientMessage::encode, RefreshFilterClientMessage::decode, RefreshFilterClientMessage::handle);
  }
}
