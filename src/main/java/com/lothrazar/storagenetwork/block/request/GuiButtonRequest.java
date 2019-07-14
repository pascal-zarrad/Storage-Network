package com.lothrazar.storagenetwork.block.request;
import com.lothrazar.storagenetwork.StorageNetwork;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonRequest extends GuiButtonExt {

  private final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/widgets.png");
  public GuiButtonRequest(int xPos, int yPos, String displayString, IPressable handler) {
    super(xPos, yPos, 14, 14,  displayString, handler);
  }

  @Override public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    super.render(p_render_1_,p_render_2_,p_render_3_);
  }

  @Override
  public void renderButton(int mouseX, int mouseY, float partial) {
    super.renderButton(mouseX, mouseY, partial);
    //    Minecraft minecraft = Minecraft.getInstance();
    //    FontRenderer fontrenderer = minecraft.fontRenderer;
    //    minecraft.getTextureManager().bindTexture(texture);
    //    //    minecraft.getTextureManager().bindTexture(Widget.WIDGETS_LOCATION);
    //    GlStateManager.color4f(1.0F, 1.0F, 1.0F, alpha);
    //    int i = getYImage(isHovered());
    //    GlStateManager.enableBlend();
    //    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    //    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    //    blit(x, y,
    //        0, 0,//46 + i * 20,
    //        width / 2, height);
    //    blit(x + width / 2, y, 200 - width / 2,
    //        46 + i * 20, width / 2, height);
    //    renderBg(minecraft, mouseX, mouseY);
    //    int j = getFGColor();
    //    drawCenteredString(fontrenderer, getMessage()
    //        , x + width / 2,
    //        y + (height - 8) / 2,
    //        j | MathHelper.ceil(alpha * 255.0F) << 24);
  }
}
