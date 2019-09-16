package com.lothrazar.storagenetwork.item.remote;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.api.IGuiPrivate;
import com.lothrazar.storagenetwork.api.util.UtilTileEntity;
import com.lothrazar.storagenetwork.gui.inventory.ItemSlotNetwork;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import com.lothrazar.storagenetwork.jei.JeiHooks;
import com.lothrazar.storagenetwork.jei.JeiSettings;
import com.lothrazar.storagenetwork.network.InsertMessage;
import com.lothrazar.storagenetwork.network.RequestMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
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
  private ItemStack stackUnderMouse = ItemStack.EMPTY;

  public GuiNetworkRemote(ContainerNetworkRemote screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    network = new NetworkWidget(this);
    network.setLines(8);
    this.xSize = WIDTH;
    this.ySize = HEIGHT;
  }

  @Override public void setStacks(List<ItemStack> stacks) {
    network.stacks = stacks;
  }

  @Override
  public void init() {
    super.init();
    int searchLeft = guiLeft + 81, searchTop = guiTop + 160, width = 85;
    network.searchBar = new TextFieldWidget(font,
        searchLeft, searchTop,
        width, font.FONT_HEIGHT, "search");
    network.searchBar.setMaxStringLength(60);
    network.initSearchbar();
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
    network.searchBar.render(mouseX, mouseY, partialTicks);
  }

  private static int getDim() {
    return 0;//TODO
  }

  //TODO: COPIED
  private boolean inField(int mouseX, int mouseY) {
    int fieldHeight = 180;
    return mouseX > (guiLeft + 7) && mouseX < (guiLeft + xSize - 7) &&
        mouseY > (guiTop + 7) && mouseY < (guiTop + fieldHeight);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.minecraft.getTextureManager().bindTexture(texture);
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    GlStateManager.color3f(1, 1, 1);
    this.blit(k, l, 0, 0, this.xSize, this.ySize);
    List<ItemStack> stacksToDisplay = network.applySearchTextToSlots();
    //    sortStackWrappers(stacksToDisplay);
    network.applyScrollPaging(stacksToDisplay);
    network.rebuildItemSlots(stacksToDisplay);
    network.renderItemSlots(mouseX, mouseY, font);
  }

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    network.drawGuiContainerForegroundLayer(mouseX, mouseY);
    drawTooltips(mouseX, mouseY);
  }

  private void drawTooltips(final int mouseX, final int mouseY) {
    if (network.inSearchBar(mouseX, mouseY)) {
      List<String> lis = Lists.newArrayList();
      if (!Screen.hasShiftDown()) {
        lis.add(I18n.format("gui.storagenetwork.shift"));
      }
      else {
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_0"));//@
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_1"));//#
        //TODO: tag search
        //        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_2"));//$
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_3"));//clear
      }
      renderTooltip(lis, mouseX - this.guiLeft, mouseY - this.guiTop);
    }
  }

  boolean isScrollable(double x, double y) {
    int scrollHeight = 152 ;
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
    network.searchBar.setFocused2(false);
    if (network.inSearchBar(mouseX, mouseY)) {
      network.searchBar.setFocused2(true);
      if (mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT) {
        network.clearSearch();
      }
    }
    //
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

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int b) {
    InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
    if (keyCode == 256) {
      minecraft.player.closeScreen();
      return true; // Forge MC-146650: Needs to return true when the key is handled.
    }
    if (network.searchBar.isFocused()) {
      network.searchBar.keyPressed(keyCode, scanCode, b);
      return true;
    }
    else if (network.stackUnderMouse.isEmpty()) {
      try {
        System.out.println("jei key " + mouseKey);
        JeiHooks.testJeiKeybind(mouseKey, network.stackUnderMouse);
      }
      catch (Throwable e) {
        System.out.println("JEI compat issue " + e);
        //its ok JEI not installed for maybe an addon mod is ok
      }
    }
    //regardles of above branch, also check this
    if (minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
      minecraft.player.closeScreen();
      return true; // Forge MC-146650: Needs to return true when the key is handled.
    }
    return super.keyPressed(keyCode, scanCode, b);
  }

  @Override
  public boolean charTyped(char typedChar, int keyCode) {
    if (network.charTyped(typedChar, keyCode)) {
      return true;
    }
    return false;// super.charTyped(typedChar, keyCode);
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

  @Override public boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
    return super.isPointInRegion(x, y, width, height, mouseX, mouseY);
  }
}
