package mrriegel.storagenetwork.block.inventory;
import mrriegel.storagenetwork.block.request.ContainerRequest;
import mrriegel.storagenetwork.block.request.TileRequest;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.gui.ContainerNetworkBase;
import mrriegel.storagenetwork.gui.GuiContainerStorageInventory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class GuiInventory extends ContainerScreen<ContainerInventory> {

  private TileInventory tile;

  public GuiInventory(ContainerInventory container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    tile = container.tile;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//  super.drawGuiContainerBackgroundLayer(partialTicks,mouseX,mouseY);
  }
}
