package com.lothrazar.storagenetwork.gui;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ButtonRequest extends Button {

  public static enum TextureEnum {

    WHITELIST, BLACKLIST, SORT_AMT, SORT_MOD, SORT_NAME, SORT_UP, SORT_DOWN, JEI_RED, JEI_GREEN;

    public int getX() {
      switch (this) {
        case BLACKLIST:
          return 189;
        case WHITELIST:
          return 175;
        case SORT_NAME:
          return 198;
        case SORT_AMT:
          return 209;
        case SORT_MOD:
          return 221;
        case JEI_RED:
          return 187;
        case JEI_GREEN:
          return 175;
        case SORT_UP:
          return 187;
        case SORT_DOWN:
          return 175;
        default:
          return 0;
      }
    }

    public int getY() {
      switch (this) {
        case BLACKLIST:
        case WHITELIST:
          return 80;
        case SORT_UP:
        case SORT_DOWN:
        case SORT_AMT:
        case SORT_MOD:
        case SORT_NAME:
          return 127;
        case JEI_RED:
        case JEI_GREEN:
          return 140;
        default:
          return 0;
      }
    }
  }

  private static final int SIZE = 16;

  public ButtonRequest setTexture(ResourceLocation texture) {
    this.texture = texture;
    return this;
  }

  private ResourceLocation texture;
  private TextureEnum textureId = null;

  public ResourceLocation getTexture() {
    return texture;
  }

  public ButtonRequest(int xPos, int yPos, String displayString, IPressable handler) {
    super(xPos, yPos, SIZE, SIZE, displayString, handler);
    texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/cable.png");
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
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
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
    int k = this.getYImage(this.isHovered());
    RenderSystem.enableBlend();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    this.blit(this.x, this.y,
        160 + SIZE * k, 52,
        width, height);
    if (textureId != null) {
      //
      this.blit(this.x, this.y,
          textureId.getX(), textureId.getY(),
          width, height);
      //   
    }
    this.renderBg(minecraft, mouseX, mouseY);
    int j = getFGColor();
    if (this.getMessage() != null)
      this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
  }

  public TextureEnum getTextureId() {
    return textureId;
  }

  public void setTextureId(TextureEnum textureId) {
    this.textureId = textureId;
  }
}
