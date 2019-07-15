package com.lothrazar.storagenetwork.gui;
public class GuiHandler {//implements IGuiHandler
  //  public enum GuiIDs {
  //    LINK, IMPORT, EXPORT, PROCESSING, REQUEST, REMOTE, CONTROLLER
  //  }
  //
  //  public static final boolean FB_LOADED = Loader.isModLoaded("fastbench");
  //
  //  @Override
  //  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
  //    BlockPos pos = new BlockPos(x, y, z);
  //    UtilTileEntity.updateTile(world, pos);
  //    if (ID == GuiIDs.LINK.ordinal()) {
  //      return new ContainerCableFilter((TileCable) world.getTileEntity(pos), player.inventory);
  //    }
  //    if (ID == GuiIDs.IMPORT.ordinal()) {
  //      return new ContainerCableIO((TileCable) world.getTileEntity(pos), player.inventory);
  //    }
  //    if (ID == GuiIDs.EXPORT.ordinal()) {
  //      return new ContainerCableIO((TileCable) world.getTileEntity(pos), player.inventory);
  //    }
  //    if (ID == GuiIDs.PROCESSING.ordinal()) {
  //      return new ContainerCableProcessing((TileCable) world.getTileEntity(pos), player.inventory);
  //    }
  //    if (ID == GuiIDs.CONTROLLER.ordinal()) {
  //      TileMaster master = ((TileControl) world.getTileEntity(pos)).getMaster().getTileEntity(TileMaster.class);
  //      return new ContainerControl(master, player.inventory);
  //    }
  //    if (ID == GuiIDs.REQUEST.ordinal()) {
  //      if (FB_LOADED && ConfigHandler.allowFastWorkBenchIntegration) {
  //        return new ContainerFastRequest((TileRequest) world.getTileEntity(pos), player, world, pos);
  //      }
  //      else {
  //        return new ContainerRequest((TileRequest) world.getTileEntity(pos), player.inventory);
  //      }
  //    }
  //    if (ID == GuiIDs.REMOTE.ordinal()) {
  //      EnumHand hand = EnumHand.values()[x];
  //      if (FB_LOADED && ConfigHandler.allowFastWorkBenchIntegration && player.getHeldItem(hand).getMetadata() != RemoteType.SIMPLE.ordinal()) {
  //        return new ContainerFastRemote(player, world, hand);
  //      }
  //      else {
  //        return new ContainerRemote(player.inventory, hand);
  //      }
  //    }
  //    return null;
  //  }
  //
  //  @SideOnly(Side.CLIENT)
  //  @Override
  //  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
  //    BlockPos pos = new BlockPos(x, y, z);
  //    if (ID == GuiIDs.LINK.ordinal()) {
  //      TileCable tile = (TileCable) world.getTileEntity(pos);
  //      return new GuiCableFilter(new ContainerCableFilter(tile, player.inventory));
  //    }
  //    if (ID == GuiIDs.IMPORT.ordinal()) {
  //      TileCable tile = (TileCable) world.getTileEntity(pos);
  //      return new GuiCableIO(new ContainerCableIO(tile, player.inventory));
  //    }
  //    if (ID == GuiIDs.EXPORT.ordinal()) {
  //      TileCable tile = (TileCable) world.getTileEntity(pos);
  //      return new GuiCableIO(new ContainerCableIO(tile, player.inventory));
  //    }
  //    if (ID == GuiIDs.PROCESSING.ordinal()) {
  //      return new GuiCableProcessing((TileCableProcess) world.getTileEntity(pos), new ContainerCableProcessing((TileCable) world.getTileEntity(pos), player.inventory));
  //    }
  //    if (ID == GuiIDs.CONTROLLER.ordinal()) {
  //      TileMaster master = ((TileControl) world.getTileEntity(pos)).getMaster().getTileEntity(TileMaster.class);
  //      return new GuiControl(new ContainerControl(master, player.inventory));
  //    }
  //    //todo new ID similar to CONTROLLER but pass master from itemstack not tile
  //    // for button on a gui
  //    if (ID == GuiIDs.REQUEST.ordinal()) {
  //      if (FB_LOADED && ConfigHandler.allowFastWorkBenchIntegration) {
  //        return new GuiFastRequest(player, world, pos);
  //      }
  //      else {
  //        return new GuiRequest(new ContainerRequest((TileRequest) world.getTileEntity(pos), player.inventory));
  //      }
  //    }
  //    if (ID == GuiIDs.REMOTE.ordinal()) {
  //      EnumHand hand = EnumHand.values()[x];
  //      if (FB_LOADED && ConfigHandler.allowFastWorkBenchIntegration && player.getHeldItem(hand).getMetadata() != RemoteType.SIMPLE.ordinal()) {
  //        return new GuiFastRemote(player, world, hand);
  //      }
  //      else {
  //        return new GuiRemote(new ContainerRemote(player.inventory, hand));
  //      }
  //    }
  //    return null;
  //  }
}
