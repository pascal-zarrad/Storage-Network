package com.lothrazar.storagenetwork.registry;

import org.lwjgl.glfw.GLFW;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;

public class ClientEventRegistry {

  public static final ResourceLocation SLOT = new ResourceLocation(StorageNetwork.MODID, "textures/gui/slot.png");
  public static final KeyMapping INVENTORY_KEY = new KeyMapping("key.storagenetwork.remote", KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.categories.inventory");
}
