package com.lothrazar.storagenetwork.registry;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ClientEventRegistry {

  public static final KeyBinding INVENTORY_KEY = new KeyBinding("key.storagenetwork.remote", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.categories.inventory");
}
