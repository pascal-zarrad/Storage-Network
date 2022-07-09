package com.lothrazar.storagenetwork.api;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum EnumConnectType implements StringRepresentable {

  NONE, CABLE, INVENTORY, BLOCKED;

  public boolean isHollow() {
    return this == NONE || this == BLOCKED;
  }

  public boolean isInventory() {
    return this == INVENTORY;
  }

  @Override
  public String getSerializedName() {
    return getName();
  }

  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
