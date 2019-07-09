package mrriegel.storagenetwork.network;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used by InsertMessage and RequestMessage as a response back to the client
 * 
 *
 */
public class StackResponseClientMessage {

  private ItemStack stack;

  private StackResponseClientMessage() {}

  StackResponseClientMessage(ItemStack a) {
    stack = a;
  }

  public static void handle(StackResponseClientMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      ServerWorld world = player.getServerWorld();
      Minecraft.getInstance().player.inventory.setItemStack(message.stack);
      //      }
    });
    //    return null;
  }

  public static StackResponseClientMessage decode(PacketBuffer buf) {
    StackResponseClientMessage message = new StackResponseClientMessage();
    message.stack = ItemStack.read(buf.readCompoundTag());
    return message;
  }

  public static void encode(StackResponseClientMessage msg, PacketBuffer buf) {
    buf.writeCompoundTag(msg.stack.serializeNBT());
  }
}
