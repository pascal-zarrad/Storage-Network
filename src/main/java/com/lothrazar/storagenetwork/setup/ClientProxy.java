package com.lothrazar.storagenetwork.setup;

import com.lothrazar.storagenetwork.registry.ModBlocks;
import com.lothrazar.storagenetwork.block.request.GuiRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {


	@Override
	public void init()
	{
		ScreenManager.registerFactory(ModBlocks.requestcontainer , GuiRequest::new);
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
