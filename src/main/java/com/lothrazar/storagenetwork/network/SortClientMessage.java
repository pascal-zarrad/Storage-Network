package com.lothrazar.storagenetwork.network;

import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.ITileNetworkSync;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SortClientMessage {

  //sync sort data TO client gui FROM server
  private BlockPos pos;
  private boolean direction;
  private EnumSortType sort;

  private SortClientMessage() {}

  public SortClientMessage(BlockPos pos, boolean direction, EnumSortType sort) {
    this.pos = pos;
    this.direction = direction;
    this.sort = sort;
  }

  public static void handle(SortClientMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();
      BlockEntity tileEntity = mc.level.getBlockEntity(message.pos);
      if (tileEntity instanceof ITileNetworkSync) {
        ITileNetworkSync ts = (ITileNetworkSync) tileEntity;
        ts.setDownwards(message.direction);
        ts.setSort(message.sort);
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static SortClientMessage decode(FriendlyByteBuf buf) {
    SortClientMessage message = new SortClientMessage();
    message.direction = buf.readBoolean();
    int sort = buf.readInt();
    message.sort = EnumSortType.values()[sort];
    message.pos = buf.readBlockPos();
    return message;
  }

  public static void encode(SortClientMessage msg, FriendlyByteBuf buf) {
    buf.writeBoolean(msg.direction);
    buf.writeInt(msg.sort.ordinal());
    if (msg.pos != null) {
      buf.writeBlockPos(msg.pos);
    }
    else { // to avoid null values // inconsistent buffer size 
      buf.writeBlockPos(BlockPos.ZERO);
    }
  }
}
