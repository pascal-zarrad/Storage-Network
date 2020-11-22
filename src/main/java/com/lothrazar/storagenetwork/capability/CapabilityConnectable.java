package com.lothrazar.storagenetwork.capability;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.IConnectable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityConnectable extends DefaultConnectable implements INBTSerializable<CompoundNBT> {

  public CapabilityConnectable() {
    filters.setIsAllowlist(true);
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT result = new CompoundNBT();
    if (getMainPos() == null) {
      return result;
    }
    result.put("master", getMainPos().serializeNBT());
    if (getPos() != null) {
      result.put("self", getPos().serializeNBT());
    }
    CompoundNBT filters = this.filters.serializeNBT();
    result.put("filters", filters);
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    setMainPos(new DimPos(nbt.getCompound("master")));
    if (nbt.contains("self")) {
      setPos(new DimPos(nbt.getCompound("self")));
    }
    if (nbt.contains("filters")) {
      CompoundNBT filters = nbt.getCompound("filters");
      this.filters.deserializeNBT(filters);
    }
  }

  public static class Storage implements Capability.IStorage<IConnectable> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IConnectable> capability, IConnectable instance, Direction side) {
      CapabilityConnectable i = (CapabilityConnectable) instance;
      return i.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IConnectable> capability, IConnectable instance, Direction side, INBT nbt) {
      CapabilityConnectable i = (CapabilityConnectable) instance;
      i.deserializeNBT((CompoundNBT) nbt);
    }
  }
}
