package com.lothrazar.storagenetwork.network;

import java.util.function.Supplier;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.ITileSortable;
import com.lothrazar.storagenetwork.api.data.EnumSortType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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
    StorageNetwork.log("handle sortlcient ");
    ctx.get().enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();//StorageNetwork.proxy.getMinecraft();
      TileEntity tileEntity = mc.world.getTileEntity(message.pos);
      if (tileEntity instanceof ITileSortable) {
        ITileSortable ts = (ITileSortable) tileEntity;
        ts.setDownwards(message.direction);
        ts.setSort(message.sort);
      }
    });
  }

  public static SortClientMessage decode(PacketBuffer buf) {
    SortClientMessage message = new SortClientMessage();
    message.direction = buf.readBoolean();
    int sort = buf.readInt();
    message.sort = EnumSortType.values()[sort];
    message.pos = buf.readBlockPos();
    return message;
  }

  public static void encode(SortClientMessage msg, PacketBuffer buf) {
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
