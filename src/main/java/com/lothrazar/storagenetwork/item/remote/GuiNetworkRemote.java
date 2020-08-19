package com.lothrazar.storagenetwork.item.remote;

import java.util.List;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import com.lothrazar.storagenetwork.jei.JeiHooks;
import com.lothrazar.storagenetwork.jei.JeiSettings;
import com.lothrazar.storagenetwork.network.SortMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiNetworkRemote extends ContainerScreen<ContainerNetworkRemote> implements IGuiNetwork {

  private static final int HEIGHT = 256;
  private static final int WIDTH = 176;
  private static final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID,
      "textures/gui/inventory.png");
  private final NetworkWidget network;
  private final ItemStack remote;

  public GuiNetworkRemote(ContainerNetworkRemote screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    //since the rightclick action forces only MAIN_HAND openings, is ok
    this.remote = inv.player.getHeldItem(Hand.MAIN_HAND);
    network = new NetworkWidget(this);
    network.setLines(8);
    this.xSize = WIDTH;
    this.ySize = HEIGHT;
    network.fieldHeight = 180;
  }

  @Override
  public void renderStackTooltip(MatrixStack ms, ItemStack stack, int mousex, int mousey) {
    super.renderTooltip(ms, stack, mousex, mousey);
  }

  @Override
  public void setStacks(List<ItemStack> stacks) {
    network.stacks = stacks;
  }

  @Override
  public boolean getDownwards() {
    return ItemRemote.getDownwards(remote);
  }

  @Override
  public void setDownwards(boolean val) {
    ItemRemote.setDownwards(remote, val);
  }

  @Override
  public EnumSortType getSort() {
    return ItemRemote.getSort(remote);
  }

  @Override
  public void setSort(EnumSortType val) {
    ItemRemote.setSort(remote, val);
  }

  @Override
  public void init() {
    super.init();
    int searchLeft = guiLeft + 81, searchTop = guiTop + 160, width = 85;
    network.searchBar = new TextFieldWidget(font,
        searchLeft, searchTop,
        width, font.FONT_HEIGHT, null);
    network.searchBar.setMaxStringLength(30);
    network.initSearchbar();
    network.initButtons();
    this.addButton(network.directionBtn);
    this.addButton(network.sortBtn);
    if (JeiSettings.isJeiLoaded()) {
      addButton(network.jeiBtn);
    }
  }

  @Override
  public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.func_230459_a_(ms, mouseX, mouseY); //    this.renderHoveredToolTip(mouseX, mouseY);
    network.searchBar.render(ms, mouseX, mouseY, partialTicks);
    network.render();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
    this.minecraft.getTextureManager().bindTexture(texture);
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    this.blit(ms, k, l, 0, 0, this.xSize, this.ySize);
    network.applySearchTextToSlots();
    network.renderItemSlots(ms, mouseX, mouseY, font);
  }

  @Override
  public void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
    //    super.func_230451_b_(ms, mouseX, mouseY);
    network.drawGuiContainerForegroundLayer(ms, mouseX, mouseY, font);
  }

  boolean isScrollable(double x, double y) {
    int scrollHeight = 152;
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
    network.mouseClicked(mouseX, mouseY, mouseButton);
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
      if (keyCode == 259) {// BACKSPACE
        network.syncTextToJei();
      }
      network.searchBar.keyPressed(keyCode, scanCode, b);
      return true;
    }
    else if (network.stackUnderMouse.isEmpty()) {
      try {
        JeiHooks.testJeiKeybind(mouseKey, network.stackUnderMouse);
      }
      catch (Throwable e) {
        StorageNetwork.LOGGER.error("JEI compat issue ", e);
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
    //
    //    this.fillGradient(p_238468_1_, p_238468_2_, p_238468_3_, p_238468_4_, p_238468_5_, p_238468_6_, p_238468_7_);
  }

  @Override
  public void drawGradient(MatrixStack ms, int x, int y, int x2, int y2, int u, int v) {
    super.fillGradient(ms, x, y, x2, y2, u, v);
  }
  //
  //  @Override
  //  public void renderStackToolTip(ItemStack stack, int x, int y) {
  //    super.renderTooltip(stack, x, y);
  //  }
  //
  //  @Override
  //  public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
  //    super.fillGradient(left, top, right, bottom, startColor, endColor);
  //  }

  @Override
  public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
    return super.isPointInRegion(x, y, width, height, mouseX, mouseY);
  }

  @Override
  public void syncDataToServer() {
    PacketRegistry.INSTANCE.sendToServer(new SortMessage(null, getDownwards(), getSort()));
  }
}
