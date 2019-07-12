package mrriegel.storagenetwork.block.request;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.gui.GuiContainerStorageInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
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

  @Override
  public boolean isScreenValid() {
    return true;
  }
}
