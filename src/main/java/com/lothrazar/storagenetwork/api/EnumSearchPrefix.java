package com.lothrazar.storagenetwork.api;
public enum EnumSearchPrefix {

  MOD, TOOLTIP, TAG;

  public String getPrefix() {
    switch (this) {
      case MOD:
        return "@";
      case TAG:
        return "$";
      case TOOLTIP:
        return "#";
    }
    return "";
  }
}
