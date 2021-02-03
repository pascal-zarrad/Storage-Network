package com.lothrazar.storagenetwork.block.cable;

import java.util.Locale;
import net.minecraft.util.IStringSerializable;

public enum EnumConnectType implements IStringSerializable {

  NONE, CABLE, INVENTORY, BLOCKED;

  public boolean isHollow() {
    return this == NONE || this == BLOCKED;
  }

  public boolean isInventory() {
    return this == INVENTORY;
  }

  @Override
  public String getString() {
    return getName();
  }

  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
