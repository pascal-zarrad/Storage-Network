package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Triple;
import com.lothrazar.storagenetwork.item.remote.ItemStorageCraftingRemote;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class KeybindCurioMessage {

  public KeybindCurioMessage() {}

  public static void handle(KeybindCurioMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      ServerLevel serverWorld = player.getLevel();
      Triple<String, Integer, ItemStack> searchCrafting = UtilInventory.getCurioRemote(player, SsnRegistry.Items.CRAFTING_REMOTE.get());
      ItemStack craftingRemote = searchCrafting.getRight();
      if (!craftingRemote.isEmpty()) {
        ItemStorageCraftingRemote.openRemote(serverWorld, player, craftingRemote, SsnRegistry.Items.CRAFTING_REMOTE.get());
      }
      else { //crafting is the upgrade, so otherwise do regular 
        Triple<String, Integer, ItemStack> searchResult = UtilInventory.getCurioRemote(player, SsnRegistry.Items.INVENTORY_REMOTE.get());
        ItemStack curioRemote = searchResult.getRight();
        if (!curioRemote.isEmpty()) {
          ItemStorageCraftingRemote.openRemote(serverWorld, player, curioRemote, SsnRegistry.Items.INVENTORY_REMOTE.get());
        }
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
