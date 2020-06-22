package com.lothrazar.storagenetwork.capabilities;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.api.capability.DefaultConnectable;
import com.lothrazar.storagenetwork.api.capability.IConnectable;
import com.lothrazar.storagenetwork.api.data.DimPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityConnectable extends DefaultConnectable implements INBTSerializable<CompoundNBT> {

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
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    setMainPos(new DimPos(nbt.getCompound("master")));
    if (nbt.contains("self")) {
      setPos(new DimPos(nbt.getCompound("self")));
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
