package com.lothrazar.storagenetwork.block.cable.linkfilter;

import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableFilter extends TileCableWithFacing implements MenuProvider {

  protected CapabilityConnectableLink capability;

  public TileCableFilter(BlockPos pos, BlockState state) {
    super(SsnRegistry.FILTERKABELTILE, pos, state);
    this.capability = new CapabilityConnectableLink(this);
  }

  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
    return new ContainerCableFilter(i, level, worldPosition, playerInventory, playerEntity);
  }

  @Override
  public Component getDisplayName() {
    return new TextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
    this.capability.deserializeNBT(compound.getCompound("capability"));
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    CompoundTag result = super.save(compound);
    result.put("capability", capability.serializeNBT());
    return result;
  }

  @Override
  public void setDirection(Direction direction) {
    super.setDirection(direction);
    this.capability.setInventoryFace(direction);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY) {
      LazyOptional<CapabilityConnectableLink> cap = LazyOptional.of(() -> this.capability);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  private void tick() {
    super.refreshDirection();
  }

  public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileCableFilter tile) {
    tile.tick();
  }

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileCableFilter tile) {
    tile.tick();
  }
}
