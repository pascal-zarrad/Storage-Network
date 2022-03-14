package com.lothrazar.storagenetwork.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public interface IGuiPrivate {
  //  void renderStackToolTip(ItemStack stack, int x, int y);
  //  void renderTooltip(List<String> t, int x, int y);
  //  void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor);

  int getGuiTop();

  //jei is forcing crashes for negative values so dodge that arbitrary made up rule java.lang.IllegalArgumentException: guiTop must be >= 0
  //  at mezz.jei.gui.overlay.GuiProperties.<init>(GuiProperties.java:110) ~[jei-1.18.2-9.5.0.132_mapped_official_1.18.2.jar%2382!/:9.5.0.132] {re:classloading}
  default int getGuiTopFixJei() {
    return getGuiTop(); // default if no fix override -8 needed
  }

  int getGuiLeft();

  boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY);

  void drawGradient(PoseStack ms, int j1, int k1, int i, int j, int k, int l);

  void renderStackTooltip(PoseStack ms, ItemStack stack, int i, int j);
}
