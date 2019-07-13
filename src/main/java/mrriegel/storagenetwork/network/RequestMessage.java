package mrriegel.storagenetwork.network;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.master.TileMaster;
import mrriegel.storagenetwork.data.ItemStackMatcher;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.registry.PacketRegistry;
import mrriegel.storagenetwork.util.UtilTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RequestMessage {

  private int mouseButton = 0;
  private ItemStack stack = ItemStack.EMPTY;
  private boolean shift, ctrl;

  @Override
  public String toString() {
    return "RequestMessage [mouseButton=" + mouseButton + ", shift=" + shift + ", ctrl=" + ctrl + ", stack=" + stack.toString() + "]";
  }

  public RequestMessage() {}

  public RequestMessage(int id, ItemStack stack, boolean shift, boolean ctrl) {
    mouseButton = id;
    this.stack = new ItemStack(stack.getItem());
    this.shift = shift;
    this.ctrl = ctrl;
    StorageNetwork.LOGGER.info(" RequestMessage CONSTRUCTOR  "+this.stack);
  }

  public static void handle(RequestMessage message, Supplier<NetworkEvent.Context> ctx) {
    //HOW AND WHY IS THIS -128 AIR
    StorageNetwork.LOGGER.info(" RequestMessage HANDLE"+message.toString());
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
    //  ServerWorld world = player.getServerWorld();
      TileMaster tileMaster = null;
      if (player.openContainer instanceof ContainerNetworkBase) {
        ContainerNetworkBase ctr = (ContainerNetworkBase) player.openContainer;
        tileMaster = ctr.getTileMaster();
      }
      if (tileMaster == null) {
        //maybe the table broke after doing this, rare case
        return;
      }
      int in = tileMaster.getAmount(new ItemStackMatcher(message.stack, true, false, true));
      // int in = tile.getAmount(new ItemStackMatcher(message.stack, true, false, true));
      ItemStack stack;
      boolean isLeftClick = message.mouseButton == UtilTileEntity.MOUSE_BTN_LEFT;
      boolean isRightClick = message.mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT;
      int sizeRequested = 0;
      if (message.ctrl) {
        sizeRequested = 1;
      }
      else if (isLeftClick) {
        sizeRequested = message.stack.getMaxStackSize();
      }
      else if (isRightClick) {
        sizeRequested = Math.min(message.stack.getMaxStackSize() / 2, in / 2);
      }
      sizeRequested = Math.max(sizeRequested, 1);

      StorageNetwork.LOGGER.info(" RequestMessage sizeRequested "+sizeRequested);
      stack = tileMaster.request(
          new ItemStackMatcher(message.stack, true, false, true),
          sizeRequested, false);
      StorageNetwork.LOGGER.info(" requestMessage request resolt   "+stack);
      if (stack.isEmpty()) {
        //try again with NBT as false
        stack = tileMaster.request(
            new ItemStackMatcher(message.stack, true, false, false),
            sizeRequested, false);
      }
      if (!stack.isEmpty()) {
        if (message.shift) {
          ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
        else {
          //when player TAKES an item, go here
          player.inventory.setItemStack(stack);
          PacketRegistry.INSTANCE.sendTo(new StackResponseClientMessage(stack),
              player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
      }
      List<ItemStack> list = tileMaster.getStacks();

      StorageNetwork.LOGGER.info("RequestMessage:  send to client  "+list.size());
      PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
          player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
      player.openContainer.detectAndSendChanges();
    });
  }

  public static RequestMessage decode(PacketBuffer buf) {
    RequestMessage msg = new RequestMessage();
    msg.mouseButton = buf.readInt();
    msg.stack = ItemStack.read(buf.readCompoundTag());
    //    msg.stack.setCount(buf.readInt());
    msg.shift = buf.readBoolean();
    msg.ctrl = buf.readBoolean();
    StorageNetwork.LOGGER.info(" RequestMessage DECODE  "+msg.stack);
    return msg;
  }

  public static void encode(RequestMessage msg, PacketBuffer buf) {
    StorageNetwork.LOGGER.info(" RequestMessage ENCODE "+msg.stack);
    buf.writeInt(msg.mouseButton);
    //    ByteBufUtils.writeItemStack(buf, stack);

    buf.writeCompoundTag(msg.stack.serializeNBT());
    buf.writeBoolean(msg.shift);
    buf.writeBoolean(msg.ctrl);
  }
}
