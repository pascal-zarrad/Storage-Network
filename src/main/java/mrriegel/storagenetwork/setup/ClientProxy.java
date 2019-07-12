package mrriegel.storagenetwork.setup;

import mrriegel.storagenetwork.block.request.GuiRequest;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {


	@Override
	public void init() {
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
