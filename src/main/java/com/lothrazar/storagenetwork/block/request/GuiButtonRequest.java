package com.lothrazar.storagenetwork.block.request;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonRequest extends GuiButtonExt {

  public ResourceLocation getTexture() {
    return texture;
  }

  public GuiButtonRequest setTexture(ResourceLocation texture) {
    this.texture = texture;
    return this;
  }

  private ResourceLocation texture;

  public GuiButtonRequest(int xPos, int yPos, String displayString, IPressable handler) {
    super(xPos, yPos, 14, 20, displayString, handler);
  }

  @Override public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    super.render(p_render_1_, p_render_2_, p_render_3_);
  }

  @Override
  public void renderButton(int mouseX, int mouseY, float partial) {
    if (texture == null) {
      super.renderButton(mouseX, mouseY, partial);
      return;
    }
    Minecraft minecraft = Minecraft.getInstance();
    FontRenderer fontrenderer = minecraft.fontRenderer;
    minecraft.getTextureManager().bindTexture(getTexture());
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
    int i = this.getYImage(this.isHovered());
    GlStateManager.enableBlend();
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    this.blit(this.x, this.y,
        width/2,     16+0+i * 16,//46 + ,
        width,height );
//    this.blit(this.x + this.width / 2, this.y,
//        16 - this.width / 2, i * 20,
//        this.width / 2, this.height);
    this.renderBg(minecraft, mouseX, mouseY);
    int j = getFGColor();
    if (this.getMessage() != null)
      this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
  }
}
