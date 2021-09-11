package com.lothrazar.storagenetwork.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public interface IGuiPrivate {
  //  void renderStackToolTip(ItemStack stack, int x, int y);
  //  void renderTooltip(List<String> t, int x, int y);
  //  void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor);

  int getGuiTop();

  int getGuiLeft();

  boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY);

  void drawGradient(PoseStack ms, int j1, int k1, int i, int j, int k, int l);

  void renderStackTooltip(PoseStack ms, ItemStack stack, int i, int j);
}
