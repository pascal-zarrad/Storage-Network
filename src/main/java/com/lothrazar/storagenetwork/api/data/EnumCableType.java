package com.lothrazar.storagenetwork.api.data;

import net.minecraft.util.IStringSerializable;

public enum EnumCableType implements IStringSerializable {
  CONNECT, STORAGE, NULL, PROCESS;

  @Override
  public String getName() {
    return name().toLowerCase();
  }
}