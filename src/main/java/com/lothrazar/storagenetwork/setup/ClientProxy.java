package com.lothrazar.storagenetwork.setup;
import com.lothrazar.storagenetwork.block.cablefilter.GuiCableFilter;
import com.lothrazar.storagenetwork.block.cableinfilter.GuiCableImportFilter;
import com.lothrazar.storagenetwork.block.request.GuiRequest;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

  @Override
  public void init() {
    ScreenManager.registerFactory(SsnRegistry.requestcontainer, GuiRequest::new);
    ScreenManager.registerFactory(SsnRegistry.filterContainer, GuiCableFilter::new);
    ScreenManager.registerFactory(SsnRegistry.filterimportContainer, GuiCableImportFilter::new);
  }

  @Override
  public World getClientWorld() {
    return Minecraft.getInstance().world;
  }

  @Override
  public PlayerEntity getClientPlayer() {
    return Minecraft.getInstance().player;
  }
}
