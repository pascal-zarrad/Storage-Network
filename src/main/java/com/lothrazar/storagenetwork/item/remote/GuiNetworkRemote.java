package com.lothrazar.storagenetwork.item.remote;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.api.IGuiPrivate;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.gui.inventory.ItemSlotNetwork;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import com.lothrazar.storagenetwork.network.InsertMessage;
import com.lothrazar.storagenetwork.network.RequestMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class GuiNetworkRemote extends ContainerScreen<ContainerNetworkRemote> implements IGuiPrivate, IGuiNetwork {

  private static final int HEIGHT = 256;
  private static final int WIDTH = 176;
  private static final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/inventory.png");
  private final NetworkWidget network;
  private ItemStack stackUnderMouse;
private int scrollHeight = 135;
  public GuiNetworkRemote(ContainerNetworkRemote screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    network = new NetworkWidget();
    network.setLines(9);
    this.xSize = WIDTH;
    this.ySize = HEIGHT;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    for (ItemSlotNetwork s : network.slots) {
      if (s != null && s.isMouseOverSlot(mouseX, mouseY)) {
        s.drawTooltip(mouseX, mouseY);
      }
    }
  }

  boolean isScrollable(double x, double y) {
    scrollHeight=170;
    return isPointInRegion(0, 0,
        this.width - 8, scrollHeight,
        x, y);
  }

  @Override
  public boolean mouseScrolled(double x, double y, double mouseButton) {
    super.mouseScrolled(x, y, mouseButton);
    //<0 going down
    // >0 going up
    if (isScrollable(x, y) && mouseButton != 0) {
      network.mouseScrolled(mouseButton);
    }
    return true;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    ItemStack stackCarriedByMouse = minecraft.player.inventory.getItemStack();
    if (!stackUnderMouse.isEmpty()
        && (mouseButton == UtilTileEntity.MOUSE_BTN_LEFT || mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT)
        && stackCarriedByMouse.isEmpty() &&
        network.canClick()) {
      ItemStack copyNotNegativeAir = new ItemStack(stackUnderMouse.getItem());
      PacketRegistry.INSTANCE.sendToServer(new RequestMessage(mouseButton, copyNotNegativeAir, Screen.hasShiftDown(),
          Screen.hasAltDown() || Screen.hasControlDown()));
      network.lastClick = System.currentTimeMillis();
    }
    else if (!stackCarriedByMouse.isEmpty() && inField((int) mouseX, (int) mouseY) &&
        network.canClick()) {
      PacketRegistry.INSTANCE.sendToServer(new InsertMessage(getDim(), mouseButton));
      network.lastClick = System.currentTimeMillis();
    }
    return true;
  }

  private static int getDim() {
    return 0;//TODO
  }

  private boolean inField(int mouseX, int mouseY) {
    int h = 90;
    return mouseX > (guiLeft + 7) && mouseX < (guiLeft + xSize - 7) && mouseY > (guiTop + 7) && mouseY < (guiTop + h);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.minecraft.getTextureManager().bindTexture(texture);
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    GlStateManager.color3f(1, 1, 1);
    this.blit(k, l, 0, 0, this.xSize, this.ySize);
    //

    List<ItemStack> stacksToDisplay = network.stacks;//applySearchTextToSlots();


//    sortStackWrappers(stacksToDisplay);
    network.applyScrollPaging(stacksToDisplay);
    network.rebuildItemSlots(stacksToDisplay, this);
    renderItemSlots(mouseX, mouseY);
  }

  /**
   * copied
   *
   * @param mouseX
   * @param mouseY
   */
  private void renderItemSlots(int mouseX, int mouseY) {
    stackUnderMouse = ItemStack.EMPTY;
    for (ItemSlotNetwork slot : network.slots) {
      slot.drawSlot(font, mouseX, mouseY);
      if (slot.isMouseOverSlot(mouseX, mouseY)) {
        stackUnderMouse = slot.getStack();
      }
    }
    if (network.slots.isEmpty()) {
      stackUnderMouse = ItemStack.EMPTY;
    }
  }

  @Override public void setStacks(List<ItemStack> stacks) {
    network.stacks = stacks;
  }

  @Override
  public boolean isInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
    return super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
  }

  @Override
  public void renderStackToolTip(ItemStack stack, int x, int y) {
    super.renderTooltip(stack, x, y);
  }

  @Override
  public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
    super.fillGradient(left, top, right, bottom, startColor, endColor);
  }
}
