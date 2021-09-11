package com.lothrazar.storagenetwork.network;

import com.lothrazar.storagenetwork.item.remote.ItemStorageCraftingRemote;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilInventory;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Triple;

public class KeybindCurioMessage {

  public KeybindCurioMessage() {}

  public static void handle(KeybindCurioMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      ServerLevel serverWorld = player.getLevel();
      Triple<String, Integer, ItemStack> searchResult = UtilInventory.getCurioRemote(player, SsnRegistry.INVENTORY_REMOTE);
      ItemStack curioRemote = searchResult.getRight();
      if (!curioRemote.isEmpty() && curioRemote.getItem() instanceof ItemStorageCraftingRemote) {
        ItemStorageCraftingRemote.openRemote(serverWorld, player, curioRemote, (ItemStorageCraftingRemote) curioRemote.getItem());
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static KeybindCurioMessage decode(FriendlyByteBuf buf) {
    KeybindCurioMessage message = new KeybindCurioMessage();
    return message;
  }

  public static void encode(KeybindCurioMessage msg, FriendlyByteBuf buf) {
    //    buf.writeBoolean(msg.direction);
  }
}
