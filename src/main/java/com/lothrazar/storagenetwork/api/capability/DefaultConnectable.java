package com.lothrazar.storagenetwork.api.capability;

import com.lothrazar.storagenetwork.api.data.DimPos;

public class DefaultConnectable implements IConnectable {

  DimPos main;
  DimPos self;

  @Override
  public DimPos getMainPos() {
    return main;
  }

  @Override
  public DimPos getPos() {
    return self;
  }

  @Override
  public void setMainPos(DimPos mainIn) {
    this.main = mainIn;
  }

  @Override
  public void setPos(DimPos pos) {
    this.self = pos;
  }
}
