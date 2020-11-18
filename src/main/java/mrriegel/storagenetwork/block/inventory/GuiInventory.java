package mrriegel.storagenetwork.block.inventory;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.gui.GuiContainerStorageInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiInventory extends GuiContainerStorageInventory {

  private TileInventory tile;

  public GuiInventory(ContainerInventory inventorySlotsIn) {
    super(inventorySlotsIn);
    tile = inventorySlotsIn.getTileRequest();
    this.isSimple = true;
    texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/request_full.png");
  }

  @Override
  public void initGui() {
    super.initGui();
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
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

  @Override
  protected int getDim() {
    return tile.getWorld().provider.getDimension();
  }

  @Override
  protected boolean isScreenValid() {
    return true;
  }
}
