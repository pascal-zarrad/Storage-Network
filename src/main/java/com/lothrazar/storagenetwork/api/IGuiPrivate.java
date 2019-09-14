package com.lothrazar.storagenetwork.api;

import net.minecraft.item.ItemStack;

public interface IGuiPrivate {
  //force expose public methods

  boolean isInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY);

  void renderStackToolTip(ItemStack stack, int x, int y);

  void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor);

  int getGuiTop();

  int getGuiLeft();
}
