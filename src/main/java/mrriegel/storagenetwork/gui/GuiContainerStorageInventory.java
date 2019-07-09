package mrriegel.storagenetwork.gui;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.data.EnumSortType;
import mrriegel.storagenetwork.jei.JeiHooks;
import mrriegel.storagenetwork.jei.JeiSettings;
import mrriegel.storagenetwork.network.ClearRecipeMessage;
import mrriegel.storagenetwork.network.InsertMessage;
import mrriegel.storagenetwork.network.RequestMessage;
import mrriegel.storagenetwork.registry.PacketRegistry;
import mrriegel.storagenetwork.util.UtilTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for Request table inventory and Remote inventory
 *
 *
 */
public abstract class GuiContainerStorageInventory extends ContainerScreen<ContainerNetworkBase> {

  private static final int HEIGHT = 256;
  private static final int WIDTH = 176;
  private final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/request.png");
  private int page = 1, maxPage = 1;
  private List<ItemStack> stacks, craftableStacks;
  private ItemStack stackUnderMouse = ItemStack.EMPTY;
  private TextFieldWidget searchBar;
  private Button directionBtn, sortBtn, jeiBtn, clearTextBtn;
  private List<ItemSlotNetwork> slots;
  private long lastClick;
  private boolean forceFocus;
  private boolean isSimple;

  public GuiContainerStorageInventory(ContainerNetworkBase container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    xSize = WIDTH;
    ySize = HEIGHT;
    stacks = Lists.newArrayList();
    craftableStacks = Lists.newArrayList();
    PacketRegistry.INSTANCE.sendToServer(new RequestMessage());
    lastClick = System.currentTimeMillis();
  }

  private boolean canClick() {
    return System.currentTimeMillis() > lastClick + 100L;
  }

  //  @Override
  public void setStacks(List<ItemStack> stacks) {
    this.stacks = stacks;
  }

  //  @Override
  public void setCraftableStacks(List<ItemStack> stacks) {
    craftableStacks = stacks;
  }

  @Override
  public void init() {
    super.init();
    //    Keyboard.enableRepeatEvents(true);
    searchBar = new TextFieldWidget(font, guiLeft + 81, guiTop + 96, 85, font.FONT_HEIGHT, "");
    searchBar.setMaxStringLength(30);
    if (isSimple) {
      searchBar.x -= 71;
      searchBar.y += 64;
      //      searchBar.width += 74;
      searchBar.setWidth(searchBar.getWidth() + 74);
      searchBar.setMaxStringLength(60);
    }
    searchBar.setEnableBackgroundDrawing(false);
    searchBar.setVisible(true);
    searchBar.setTextColor(16777215);
    searchBar.setFocused2(true);
    if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
      searchBar.setText(JeiHooks.getFilterText());
    }
    if (!isSimple) {
      directionBtn = new GuiStorageButton(0, guiLeft + 7, searchBar.y - 3, "");
      addButton(directionBtn);
      sortBtn = new GuiStorageButton(1, guiLeft + 21, searchBar.y - 3, "");
      addButton(sortBtn);
      jeiBtn = new GuiStorageButton(4, guiLeft + 35, searchBar.y - 3, "");
      if (JeiSettings.isJeiLoaded()) {
        addButton(jeiBtn);
      }
      clearTextBtn = new GuiStorageButton(5, guiLeft + 64, searchBar.y - 3, "X");
      addButton(clearTextBtn);
    }
  }

  private int getLines() {
    return isSimple ? 8 : 4;
  }

  private static int getColumns() {
    return 9;
  }

  public abstract boolean getDownwards();

  public abstract void setDownwards(boolean d);

  public abstract EnumSortType getSort();

  public abstract void setSort(EnumSortType s);

  public abstract BlockPos getPos();

  protected abstract int getDim();

  private boolean inField(int mouseX, int mouseY) {
    int h = 90;
    if (isSimple) {
      h += 60;
    }
    return mouseX > (guiLeft + 7) && mouseX < (guiLeft + xSize - 7) && mouseY > (guiTop + 7) && mouseY < (guiTop + h);
  }

  private boolean inSearchbar(double mouseX, double mouseY) {
    return isPointInRegion(searchBar.x - guiLeft + 14, searchBar.y - guiTop, searchBar.getWidth(), font.FONT_HEIGHT + 6, mouseX, mouseY);
  }
  //
  //  @Override
  //  public void drawGradientRectP(int left, int top, int right, int bottom, int startColor, int endColor) {
  //    super.drawGradientRect(left, top, right, bottom, startColor, endColor);
  //  }
  //
  //  @Override
  //  public font getFont() {
  //    return font;
  //  }
  //
  //  @Override
  //  public boolean isPointInRegionP(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
  //    return super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
  //  }
  //
  //  @Override
  //  public void renderToolTipP(ItemStack stack, int x, int y) {
  //    super.renderToolTip(stack, x, y);
  //  }

  protected abstract boolean isScreenValid();

  private boolean doesStackMatchSearch(ItemStack stack) {
    String searchText = searchBar.getText();
    if (searchText.startsWith("@")) {
      String name = UtilTileEntity.getModNameForItem(stack.getItem());
      return name.toLowerCase().contains(searchText.toLowerCase().substring(1));
    }
    else if (searchText.startsWith("#")) {
      String tooltipString;
      Minecraft mc = Minecraft.getInstance();
      List<ITextComponent> tooltip = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
      tooltipString = Joiner.on(' ').join(tooltip).toLowerCase();
      //      tooltipString = ChatFormatting.stripFormatting(tooltipString);
      return tooltipString.toLowerCase().contains(searchText.toLowerCase().substring(1));
    }
    //    else if (searchText.startsWith("$")) {
    //      StringBuilder oreDictStringBuilder = new StringBuilder();
    //      for (int oreId : OreDictionary.getOreIDs(stack)) {
    //        String oreName = OreDictionary.getOreName(oreId);
    //        oreDictStringBuilder.append(oreName).append(' ');
    //      }
    //      return oreDictStringBuilder.toString().toLowerCase().contains(searchText.toLowerCase().substring(1));
    //    }
    //    else if (searchText.startsWith("%")) {
    //      StringBuilder creativeTabStringBuilder = new StringBuilder();
    //      for (CreativeTabs creativeTab : stack.getItem().getCreativeTabs()) {
    //        if (creativeTab != null) {
    //          String creativeTabName = creativeTab.getTranslatedTabLabel();
    //          creativeTabStringBuilder.append(creativeTabName).append(' ');
    //        }
    //      }
    //      return creativeTabStringBuilder.toString().toLowerCase().contains(searchText.toLowerCase().substring(1));
    //    }
    else {
      return stack.getDisplayName().toString().toLowerCase().contains(searchText.toLowerCase());
    }
  }

  @Override
  public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    if (isScreenValid() == false) {
      return;
    }
    renderBackground();// drawDefaultBackground();//dim the background as normal
    renderTextures();
    List<ItemStack> stacksToDisplay = applySearchTextToSlots();
    sortStackWrappers(stacksToDisplay);
    applyScrollPaging(stacksToDisplay);
    rebuildItemSlots(stacksToDisplay);
    renderItemSlots(mouseX, mouseY);
    //    searchBar.render();
  }

  private void renderTextures() {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    minecraft.getTextureManager().bindTexture(texture);
    int xCenter = (width - xSize) / 2;
    int yCenter = (height - ySize) / 2;
    blit(xCenter, yCenter, 0, 0, xSize, ySize);
  }

  private List<ItemStack> applySearchTextToSlots() {
    String searchText = searchBar.getText();
    List<ItemStack> stacksToDisplay = searchText.equals("") ? Lists.newArrayList(stacks) : Lists.newArrayList();
    if (!searchText.equals("")) {
      for (ItemStack stack : stacks) {
        if (doesStackMatchSearch(stack)) {
          stacksToDisplay.add(stack);
        }
      }
    }
    return stacksToDisplay;
  }

  private void renderItemSlots(int mouseX, int mouseY) {
    stackUnderMouse = ItemStack.EMPTY;
    for (ItemSlotNetwork slot : slots) {
      slot.drawSlot(mouseX, mouseY);
      if (ItemSlotNetwork.isMouseOverSlot(mouseX, mouseY)) {
        stackUnderMouse = slot.getStack();
        //        break;
      }
    }
    if (slots.isEmpty()) {
      stackUnderMouse = ItemStack.EMPTY;
    }
  }

  private void rebuildItemSlots(List<ItemStack> stacksToDisplay) {
    slots = Lists.newArrayList();
    int index = (page - 1) * (getColumns());
    for (int row = 0; row < getLines(); row++) {
      for (int col = 0; col < getColumns(); col++) {
        if (index >= stacksToDisplay.size()) {
          break;
        }
        int in = index;
        slots.add(new ItemSlotNetwork(this, stacksToDisplay.get(in), guiLeft + 8 + col * 18, guiTop + 10 + row * 18, stacksToDisplay.get(in).getCount(), guiLeft, guiTop, true));
        index++;
      }
    }
  }

  private void applyScrollPaging(List<ItemStack> stacksToDisplay) {
    maxPage = stacksToDisplay.size() / (getColumns());
    if (stacksToDisplay.size() % (getColumns()) != 0) {
      maxPage++;
    }
    maxPage -= (getLines() - 1);
    if (maxPage < 1) {
      maxPage = 1;
    }
    if (page < 1) {
      page = 1;
    }
    if (page > maxPage) {
      page = maxPage;
    }
  }

  private void sortStackWrappers(List<ItemStack> stacksToDisplay) {
    Collections.sort(stacksToDisplay, new Comparator<ItemStack>() {

      final int mul = getDownwards() ? -1 : 1;

      @Override
      public int compare(ItemStack o2, ItemStack o1) {
        switch (getSort()) {
          case AMOUNT:
            return Integer.compare(o1.getCount(), o2.getCount()) * mul;
          case NAME:
            return o2.getDisplayName().toString().compareToIgnoreCase(o1.getDisplayName().toString()) * mul;
          case MOD:
            return UtilTileEntity.getModNameForItem(o2.getItem()).compareToIgnoreCase(UtilTileEntity.getModNameForItem(o1.getItem())) * mul;
        }
        return 0;
      }
    });
  }


  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    if (isScreenValid() == false) {
      return;
    }
    if (forceFocus) {
      searchBar.setFocused2(true);
      if (searchBar.isFocused()) {
        forceFocus = false;
      }
    }
    //    @Override
    //    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    //      super.drawScreen(mouseX, mouseY, partialTicks);
    super.renderHoveredToolTip(mouseX, mouseY);
    if (isScreenValid() == false) {
      minecraft.player.closeScreen();
      return;
    }
    try {
      drawTooltips(mouseX, mouseY);
    }
    catch (Throwable e) {
      StorageNetwork.LOGGER.error(e.getMessage());
    }
  }

  private void drawTooltips(int mouseX, int mouseY) {
    for (ItemSlotNetwork s : slots) {
      if (s != null && ItemSlotNetwork.isMouseOverSlot(mouseX, mouseY)) {
        s.drawTooltip(mouseX, mouseY);
      }
    }
    if (inSearchbar(mouseX, mouseY)) {
      List<String> lis = Lists.newArrayList();
      //      this.keyPressed()
      if (!Screen.hasShiftDown()) {
        lis.add(I18n.format("gui.storagenetwork.shift"));
      }
      else {
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_0"));
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_1"));
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_2"));
        lis.add(I18n.format("gui.storagenetwork.fil.tooltip_3"));
      }
      renderTooltip(lis, mouseX, mouseY);
    }
    if (clearTextBtn != null && clearTextBtn.isMouseOver(mouseX, mouseY)) {
      renderTooltip(Lists.newArrayList(I18n.format("gui.storagenetwork.tooltip_clear")), mouseX, mouseY);
    }
    if (sortBtn != null && sortBtn.isMouseOver(mouseX, mouseY)) {
      renderTooltip(Lists.newArrayList(I18n.format("gui.storagenetwork.req.tooltip_" + getSort())), mouseX, mouseY);
    }
    if (directionBtn != null && directionBtn.isMouseOver(mouseX, mouseY)) {
      renderTooltip(Lists.newArrayList(I18n.format("gui.storagenetwork.sort")), mouseX, mouseY);
    }
    if (jeiBtn != null && jeiBtn.isMouseOver(mouseX, mouseY)) {
      String s = I18n.format(JeiSettings.isJeiSearchSynced() ? "gui.storagenetwork.fil.tooltip_jei_on" : "gui.storagenetwork.fil.tooltip_jei_off");
      renderTooltip(Lists.newArrayList(s), mouseX, mouseY);
    }
  }

  @Override
  public void onClose() {
    super.onClose();
    //    Keyboard.enableRepeatEvents(false);
  }
  //  @Override
  //  public void actionPerformed(Button button) throws IOException {
  //    super.actionPerformed(button);
  //    if (button == null) {
  //      return;
  //    }
  //    boolean doSort = true;
  //    if (button.id == directionBtn.id) {
  //      setDownwards(!getDownwards());
  //    }
  //    else if (button.id == sortBtn.id) {
  //      setSort(getSort().next());
  //    }
  //    else if (button.id == jeiBtn.id) {
  //      doSort = false;
  //      JeiSettings.setJeiSearchSync(!JeiSettings.isJeiSearchSynced());
  //    }
  //    else if (button.id == clearTextBtn.id) {
  //      doSort = false;
  //      clearSearch();
  //      //      this.fieldOperationLimit.setFocused(true);//doesnt work..somethings overriding it?
  //      forceFocus = true;//we have to force it to go next-tick
  //    }
  //    if (doSort) {
  //      PacketRegistry.INSTANCE.sendToServer(new SortMessage(getPos(), getDownwards(), getSort()));
  //    }
  //  }

  private void clearSearch() {
    searchBar.setText("");
    if (JeiSettings.isJeiSearchSynced()) {
      JeiHooks.setFilterText("");
    }
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    searchBar.setFocused2(false);
    int rectX = 63;
    int rectY = 110;
    if (inSearchbar(mouseX, mouseY)) {
      searchBar.setFocused2(true);
      if (mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT) {
        clearSearch();
      }
    }
    else if (!isSimple && isPointInRegion(rectX, rectY, 7, 7, mouseX, mouseY)) {
      PacketRegistry.INSTANCE.sendToServer(new ClearRecipeMessage());
      PacketRegistry.INSTANCE.sendToServer(new RequestMessage(0, ItemStack.EMPTY, false, false));
    }
    else {
      ItemStack stackCarriedByMouse = minecraft.player.inventory.getItemStack();
      if (!stackUnderMouse.isEmpty()
          && (mouseButton == UtilTileEntity.MOUSE_BTN_LEFT || mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT
              || mouseButton == UtilTileEntity.MOUSE_BTN_MIDDLE_CLICK)
          && stackCarriedByMouse.isEmpty() && canClick()) {
        PacketRegistry.INSTANCE.sendToServer(new RequestMessage(mouseButton, stackUnderMouse, hasShiftDown(),
            mouseButton == UtilTileEntity.MOUSE_BTN_MIDDLE_CLICK));
        lastClick = System.currentTimeMillis();
      }
      else if (!stackCarriedByMouse.isEmpty() && inField(mouseX, mouseY) && canClick()) {
        PacketRegistry.INSTANCE.sendToServer(new InsertMessage(getDim(), mouseButton, stackCarriedByMouse));
        lastClick = System.currentTimeMillis();
      }
    }
  }

  @Override
  public boolean charTyped(char typedChar, int keyCode) {
    //    super.keyPressed()
    //func_195363_d
    //    if (!checkHotbarKeys(keyCode)) {
    //      Keyboard.enableRepeatEvents(true);
    if (searchBar.isFocused() && searchBar.charTyped(typedChar, keyCode)) {
        PacketRegistry.INSTANCE.sendToServer(new RequestMessage(0, ItemStack.EMPTY, false, false));
        if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
          JeiHooks.setFilterText(searchBar.getText());
        }
      }
      else if (stackUnderMouse.isEmpty() == false) {
        try {
          JeiHooks.testJeiKeybind(keyCode, stackUnderMouse);
        }
        catch (Throwable e) {
          //its ok JEI not installed for maybe an addon mod is ok
        }
      }
      else {
      //      super.keyPressed(typedChar, keyCode, whatami);
      }
    //    }
    return super.charTyped(typedChar, keyCode);
  }
  //  @Override
  //  public void updateScreen() {
  //
  //    super.updateScreen();
  //    if (searchBar != null) {
  //      searchBar.updateCursorCounter();
  //    }
  //  }

  @Override
  public boolean mouseClicked(double x, double y, int mouse) {
    super.mouseClicked(x, y, mouse);
    double i = x * width / minecraft.mainWindow.getWidth();
    double j = height - y * height / minecraft.mainWindow.getHeight() - 1;
    if (inField((int) i, (int) j)) {
      //      int mouse = Mouse.getEventDWheel();
      if (mouse > 0 && page > 1) {
        page--;
      }
      if (mouse < 0 && page < maxPage) {
        page++;
      }
    }
    return true;
  }
}
