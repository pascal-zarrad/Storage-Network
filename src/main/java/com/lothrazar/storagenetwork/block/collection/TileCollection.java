package com.lothrazar.storagenetwork.block.collection;

import com.lothrazar.storagenetwork.api.DimPos;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.block.TileConnectable;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class TileCollection extends TileConnectable implements MenuProvider {

  private CollectionItemStackHandler itemHandler;

  public TileCollection(BlockPos pos, BlockState state) {
    super(SsnRegistry.Tiles.COLLECTOR.get(), pos, state);
    itemHandler = new CollectionItemStackHandler();
    itemHandler.tile = this;
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
  }

  @Override
  public void saveAdditional(CompoundTag compound) {
    super.saveAdditional(compound);
  }

  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
    return new ContainerCollectionFilter(i, level, worldPosition, playerInventory, playerEntity);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("block.storagenetwork.collector");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) {
      IConnectable capabilityConnectable = super.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, side).orElse(null);
      DimPos m = getMain();
      if (capabilityConnectable != null && m != null) {
        TileMain tileMain = m.getTileEntity(TileMain.class);
        itemHandler.setMain(tileMain);
      }
      return LazyOptional.of(new NonNullSupplier<T>() {

        public @Override T get() {
          return (T) itemHandler;
        }
      });
    }
    return super.getCapability(cap, side);
  }
}
