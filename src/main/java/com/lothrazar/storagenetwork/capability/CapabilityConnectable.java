package com.lothrazar.storagenetwork.capability;

import com.lothrazar.storagenetwork.api.DimPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityConnectable extends DefaultConnectable implements INBTSerializable<CompoundTag> {

  public CapabilityConnectable() {
    filters.setIsAllowlist(true);
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag result = new CompoundTag();
    if (getMainPos() == null) {
      return result;
    }
    result.put("master", getMainPos().serializeNBT());
    if (getPos() != null) {
      result.put("self", getPos().serializeNBT());
    }
    CompoundTag filters = this.filters.serializeNBT();
    result.put("filters", filters);
    result.putBoolean("needsRedstone", this.needsRedstone());
    return result;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    setMainPos(new DimPos(nbt.getCompound("master")));
    if (nbt.contains("self")) {
      setPos(new DimPos(nbt.getCompound("self")));
    }
    if (nbt.contains("filters")) {
      CompoundTag filters = nbt.getCompound("filters");
      this.filters.deserializeNBT(filters);
    }
    this.needsRedstone(nbt.getBoolean("needsRedstone"));
  }
}
