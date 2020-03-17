package com.lothrazar.storagenetwork.setup;

import com.lothrazar.storagenetwork.block.cable.export.GuiCableExportFilter;
import com.lothrazar.storagenetwork.block.cable.inputfilter.GuiCableImportFilter;
import com.lothrazar.storagenetwork.block.cable.storagefilter.GuiCableFilter;
import com.lothrazar.storagenetwork.block.inventory.GuiNetworkInventory;
import com.lothrazar.storagenetwork.block.request.GuiNetworkTable;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkCraftingRemote;
import com.lothrazar.storagenetwork.item.remote.GuiNetworkRemote;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

  @Override
  public void init() {
    ScreenManager.registerFactory(SsnRegistry.requestcontainer, GuiNetworkTable::new);
    ScreenManager.registerFactory(SsnRegistry.filterContainer, GuiCableFilter::new);
    ScreenManager.registerFactory(SsnRegistry.filterimportContainer, GuiCableImportFilter::new);
    ScreenManager.registerFactory(SsnRegistry.filterexportContainer, GuiCableExportFilter::new);
    ScreenManager.registerFactory(SsnRegistry.remote, GuiNetworkRemote::new);
    ScreenManager.registerFactory(SsnRegistry.craftingremote, GuiNetworkCraftingRemote::new);
    ScreenManager.registerFactory(SsnRegistry.inventorycontainer, GuiNetworkInventory::new);
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
