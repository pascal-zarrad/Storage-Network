package com.lothrazar.storagenetwork.api.data;
public enum EnumSortType {

  AMOUNT, NAME, MOD;

  public EnumSortType next() {
    return values()[(this.ordinal() + 1) % values().length];
  }
}