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

public class KeybindCurioMessage {

  public KeybindCurioMessage() {}

  public static void handle(KeybindCurioMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      ServerWorld serverWorld = player.getServerWorld();
      ItemStack curioRemote = UtilInventory.getCurioRemote(player, SsnRegistry.INVENTORY_REMOTE).getRight();
      if (!curioRemote.isEmpty()) {
        ItemStorageCraftingRemote.openRemote(serverWorld, player, curioRemote, (ItemStorageCraftingRemote) curioRemote.getItem());
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
