package mrriegel.storagenetwork.network;
import mrriegel.storagenetwork.block.request.TileRequest;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SortMessage {

  private BlockPos pos;
  private boolean direction;
  private EnumSortType sort;

  private SortMessage() {}

  public SortMessage(BlockPos pos, boolean direction, EnumSortType sort) {
    this.pos = pos;
    this.direction = direction;
    this.sort = sort;
  }

  public static void handle(SortMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      ServerWorld world = player.getServerWorld();
      if (player.openContainer instanceof ContainerNetworkBase) {
        //          if (((ContainerNetworkBase) player.openContainer).isRequest()) {
            TileEntity tileEntity = player.world.getTileEntity(message.pos);
            if (tileEntity instanceof TileRequest) {
              TileRequest tile = (TileRequest) tileEntity;
              tile.setSort(message.sort);
              tile.setDownwards(message.direction);
            }
            tileEntity.markDirty();
          }
          else {
            ItemStack stackPlayerHeld = player.inventory.getCurrentItem();
            NBTHelper.setBoolean(stackPlayerHeld, "down", message.direction);
            NBTHelper.setString(stackPlayerHeld, "sort", message.sort.toString());
            return;
          }
      //      }
    });
    //    return null;
  }

  public static SortMessage decode(PacketBuffer buf) {
    SortMessage message = new SortMessage();
    message.pos = buf.readBlockPos();
    message.direction = buf.readBoolean();
    int sort = buf.readInt();
    message.sort = EnumSortType.values()[sort];
    return message;
  }

  public static void encode(SortMessage msg, PacketBuffer buf) {
    buf.writeBlockPos(msg.pos);
    buf.writeBoolean(msg.direction);
    buf.writeInt(msg.sort.ordinal());
    //    ByteBufUtils.writeItemStack(buf, stack);
    //    buf.writeCompoundTag(msg.stack.serializeNBT());
    //    buf.writeInt(msg.mouseButton);
  }
  //  @Override
  //  public void fromBytes(ByteBuf buf) {
  //    pos = BlockPos.fromLong(buf.readLong());
  //    direction = buf.readBoolean();
  //    sort = EnumSortType.valueOf(ByteBufUtils.readUTF8String(buf));
  //  }
  //
  //  @Override
  //  public void toBytes(ByteBuf buf) {
  //    buf.writeLong(pos.toLong());
  //    buf.writeBoolean(direction);
  //    ByteBufUtils.writeUTF8String(buf, sort.toString());
  //  }
}
