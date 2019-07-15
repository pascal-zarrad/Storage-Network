package com.lothrazar.storagenetwork.block.request;
import com.lothrazar.storagenetwork.data.EnumSortType;
import com.lothrazar.storagenetwork.gui.GuiContainerStorageInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class GuiRequest extends GuiContainerStorageInventory {

  private TileRequest tile;

  public GuiRequest(ContainerRequest container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    tile = container.getTileRequest();
    this.isSimple = false;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public boolean getDownwards() {
    return tile.isDownwards();
  }

  @Override
  public void setDownwards(boolean d) {
    tile.setDownwards(d);
  }

  @Override
  public EnumSortType getSort() {
    return tile.getSort();
  }

  @Override
  public void setSort(EnumSortType s) {
    tile.setSort(s);
  }

  @Override
  public BlockPos getPos() {
    return tile.getPos();
  }

}
