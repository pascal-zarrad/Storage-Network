package com.lothrazar.storagenetwork.gui;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;

import java.util.List;

public class NetworkWidget {

  int page = 1, maxPage = 1;
  List<ItemStack> stacks;
    List<ItemSlotNetwork> slots;

  public NetworkWidget() {
    stacks = Lists.newArrayList();
    slots = Lists.newArrayList();
  }

  int getLines() {
    return 4;

  }

  static int getColumns() {

    return 9;
  }

  void applyScrollPaging(List<ItemStack> stacksToDisplay) {
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

    void rebuildItemSlots(List<ItemStack> stacksToDisplay, IGuiPrivate gui) {
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
}
