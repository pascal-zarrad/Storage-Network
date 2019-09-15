package com.lothrazar.storagenetwork.gui;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.api.IGuiPrivate;
import com.lothrazar.storagenetwork.gui.inventory.ItemSlotNetwork;
import com.lothrazar.storagenetwork.jei.JeiHooks;
import com.lothrazar.storagenetwork.jei.JeiSettings;
import com.lothrazar.storagenetwork.network.RequestMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;

import java.util.List;

public class NetworkWidget {

  public TextFieldWidget searchBar;
  public long lastClick;
  int page = 1, maxPage = 1;
  public List<ItemStack> stacks;
  public List<ItemSlotNetwork> slots;
  private int lines = 4;
  private int columns = 9;

  public NetworkWidget() {
    stacks = Lists.newArrayList();
    slots = Lists.newArrayList();
    PacketRegistry.INSTANCE.sendToServer(new RequestMessage());
    lastClick = System.currentTimeMillis();
  }

  public boolean canClick() {
    return System.currentTimeMillis() > lastClick + 100L;
  }

  int getLines() {
    return lines;
  }

  int getColumns() {
    return columns;
  }
  public void setLines(int v) {
    lines = v;
  }
  void setColumns(int v) {
    columns = v;
  }

  public void applyScrollPaging(List<ItemStack> stacksToDisplay) {
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

  public void mouseScrolled(double mouseButton) {
    //<0 going down
    // >0 going up
    if (mouseButton > 0 && page > 1) {
      page--;
    }
    if (mouseButton < 0 && page < maxPage) {
      page++;
    }
  }

  public void rebuildItemSlots(List<ItemStack> stacksToDisplay, IGuiPrivate gui) {
    slots = Lists.newArrayList();
    int index = (page - 1) * (getColumns());
    for (int row = 0; row < getLines(); row++) {
      for (int col = 0; col < getColumns(); col++) {
        if (index >= stacksToDisplay.size()) {
          break;
        }
        int in = index;
        //        StorageNetwork.LOGGER.info(in + "GUI STORAGE rebuildItemSlots "+stacksToDisplay.get(in));
        slots.add(new ItemSlotNetwork(gui, stacksToDisplay.get(in),
            gui.getGuiLeft() + 8 + col * 18,
            gui.getGuiTop() + 10 + row * 18,
            stacksToDisplay.get(in).getCount(),
            gui.getGuiLeft(), gui.getGuiTop(), true));
        index++;
      }
    }
  }

  public void initSearchbar() {

    searchBar.setEnableBackgroundDrawing(false);
    searchBar.setVisible(true);
    searchBar.setTextColor(16777215);
    searchBar.setFocused2(true);
    if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
      searchBar.setText(JeiHooks.getFilterText());
    }
  }

  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    for (ItemSlotNetwork s : slots) {
      if (s != null && s.isMouseOverSlot(mouseX, mouseY)) {
        s.drawTooltip(mouseX, mouseY);
      }
    }
  }

  public boolean charTyped(char typedChar, int keyCode) {
    if (searchBar.isFocused() && searchBar.charTyped(typedChar, keyCode)) {
      PacketRegistry.INSTANCE.sendToServer(new RequestMessage(0, ItemStack.EMPTY, false, false));
      if (JeiSettings.isJeiLoaded() && JeiSettings.isJeiSearchSynced()) {
        JeiHooks.setFilterText(searchBar.getText());
      }
      return true;
    }
    return false;
  }
}
