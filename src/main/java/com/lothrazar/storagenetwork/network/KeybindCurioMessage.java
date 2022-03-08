package com.lothrazar.storagenetwork.network;

import com.lothrazar.storagenetwork.item.remote.ItemStorageCraftingRemote;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.util.UtilInventory;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Triple;

public class KeybindCurioMessage {

  public KeybindCurioMessage() {}

  public static void handle(KeybindCurioMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      ServerWorld serverWorld = player.getServerWorld();
      Triple<String, Integer, ItemStack> searchCrafting = UtilInventory.getCurioRemote(player, SsnRegistry.CRAFTING_REMOTE);
      ItemStack craftingRemote = searchCrafting.getRight();
      if (!craftingRemote.isEmpty()) {
        ItemStorageCraftingRemote.openRemote(serverWorld, player, craftingRemote, SsnRegistry.CRAFTING_REMOTE);
      }
      else { //crafting is the upgrade, so otherwise do regular 
        Triple<String, Integer, ItemStack> searchResult = UtilInventory.getCurioRemote(player, SsnRegistry.INVENTORY_REMOTE);
        ItemStack curioRemote = searchResult.getRight();
        if (!curioRemote.isEmpty()) {
          ItemStorageCraftingRemote.openRemote(serverWorld, player, curioRemote, SsnRegistry.INVENTORY_REMOTE);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static KeybindCurioMessage decode(PacketBuffer buf) {
    KeybindCurioMessage message = new KeybindCurioMessage();
    return message;
  }

  public static void encode(KeybindCurioMessage msg, PacketBuffer buf) {
    //    buf.writeBoolean(msg.direction);
  }
}
