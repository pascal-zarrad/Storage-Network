package mrriegel.storagenetwork.gui;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * used as the MAIN grid in the network item display
 * 
 * also as ghost/filter items in the cable filter slots
 * 
 * @author
 *
 */
public class ItemSlotNetwork {

  private final int x;
  private final int y;
  private final int size;
  private final int guiLeft;
  private final int guiTop;
  private boolean showNumbers;
  private final Container parent;
  private ItemStack stack;

  public ItemSlotNetwork(Container parent, @Nonnull ItemStack stack, int x, int y, int size, int guiLeft, int guiTop, boolean number) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.guiLeft = guiLeft;
    this.guiTop = guiTop;
    setShowNumbers(number);
    this.parent = parent;
    setStack(stack);
  }

  public static boolean isMouseOverSlot(int mouseX, int mouseY) {
    // TODO: this
    return false;//parent.isPointInRegionP(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
  }

  public void drawSlot(int mx, int my) {
    // TODO: renderItem and keyboard isKeyDown issues
    //    GlStateManager.pushMatrix();
    //    if (!getStack().isEmpty()) {
    //      RenderHelper.enableGUIStandardItemLighting();
    //      Minecraft.getInstance().getRenderItem().renderItemAndEffectIntoGUI(getStack(), x, y);
    //      String amount;
    //      //cant sneak in gui
    //      //default to short form, show full amount if sneak
    //      if (GLFW.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
    //        amount = size + "";
    //      }
    //      else {
    //        amount = UtilInventory.formatLargeNumber(size);
    //      }
    //      if (isShowNumbers()) {
    //        GlStateManager.pushMatrix();
    //        GlStateManager.scale(.5f, .5f, .5f);
    //        mc.getRenderItem().renderItemOverlayIntoGUI(parent.getFont(), stack, x * 2 + 16, y * 2 + 16, amount);
    //        GlStateManager.popMatrix();
    //      }
    //    }
    //    if (isMouseOverSlot(mx, my)) {
    //      GlStateManager.disableLighting();
    //      GlStateManager.disableDepth();
    //      int j1 = x;
    //      int k1 = y;
    //      GlStateManager.colorMask(true, true, true, false);
    //      parent.drawGradientRectP(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
    //      GlStateManager.colorMask(true, true, true, true);
    //      GlStateManager.enableLighting();
    //      GlStateManager.enableDepth();
    //    }
    //    GlStateManager.popMatrix();
  }

  public void drawTooltip(int mx, int my) {
    if (isMouseOverSlot(mx, my) && !getStack().isEmpty()) {
      // TODO this tooltip
      //      parent.renderToolTipP(getStack(), mx, my);
    }
  }

  public ItemStack getStack() {
    return stack;
  }

  public void setStack(ItemStack stack) {
    this.stack = stack;
  }

  private boolean isShowNumbers() {
    return showNumbers;
  }

  public void setShowNumbers(boolean showNumbers) {
    this.showNumbers = showNumbers;
  }
}
