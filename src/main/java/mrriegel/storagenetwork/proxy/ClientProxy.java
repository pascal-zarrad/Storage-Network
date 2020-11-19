package mrriegel.storagenetwork.proxy;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.cable.TesrCable;
import mrriegel.storagenetwork.block.cable.TileCable;
import mrriegel.storagenetwork.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().player;
  }

  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
    ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new TesrCable());
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    TesrCable.addCableRender(ModBlocks.kabel, new ResourceLocation(StorageNetwork.MODID, "textures/tile/link.png"));
    TesrCable.addCableRender(ModBlocks.exKabel, new ResourceLocation(StorageNetwork.MODID, "textures/tile/ex.png"));
    TesrCable.addCableRender(ModBlocks.imKabel, new ResourceLocation(StorageNetwork.MODID, "textures/tile/im.png"));
    TesrCable.addCableRender(ModBlocks.storageKabel, new ResourceLocation(StorageNetwork.MODID, "textures/tile/storage.png"));
    TesrCable.addCableRender(ModBlocks.processKabel, new ResourceLocation(StorageNetwork.MODID, "textures/tile/process.png"));
    TesrCable.addCableRender(ModBlocks.simple_kabel, new ResourceLocation(StorageNetwork.MODID, "textures/tile/simple.png"));
  }
}
