package com.lothrazar.storagenetwork.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.block.main.TileMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;

public class UtilTileEntity {

  private static final Map<Item, String> modNamesForIds = new HashMap<>();
  public static final int MOUSE_BTN_LEFT = 0;
  public static final int MOUSE_BTN_RIGHT = 1;
  public static final int MOUSE_BTN_MIDDLE_CLICK = 2;

  public static void chatMessage(PlayerEntity player, String message) {
    if (player.world.isRemote) {
      player.sendMessage(new TranslationTextComponent(message), player.getUniqueID());
    }
  }

  public static void statusMessage(PlayerEntity player, String message) {
    if (player.world.isRemote) {
      player.sendStatusMessage(new TranslationTextComponent(message), true);
    }
  }

  public static String lang(String message) {
    TranslationTextComponent t = new TranslationTextComponent(message);
    return t.getUnformattedComponentText();
  }

  /**
   * This can only be called on the server side! It returns the Main tile entity for the given connectable.
   *
   * @param connectable
   * @return
   */
  @Nullable
  public static TileMain getTileMainForConnectable(@Nonnull IConnectable connectable) {
    if (connectable == null || connectable.getMainPos() == null) {
      return null;
    }
    return connectable.getMainPos().getTileEntity(TileMain.class);
  }

  /**
   * Get mod id for item, but use cache to save time just in case it helps
   * 
   * @param theitem
   * @return
   */
  @Nonnull
  public static String getModNameForItem(@Nonnull Item theitem) {
    if (modNamesForIds.containsKey(theitem)) {
      return modNamesForIds.get(theitem);
    }
    String modId = theitem.getRegistryName().getNamespace();
    String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
    modNamesForIds.put(theitem, lowercaseModId);
    return lowercaseModId;
  }
}
