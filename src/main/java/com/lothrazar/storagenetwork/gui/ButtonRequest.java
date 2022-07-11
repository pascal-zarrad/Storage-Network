package com.lothrazar.storagenetwork.gui;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonRequest extends Button {

  public static enum TextureEnum {

    ALLOWLIST, IGNORELIST, SORT_AMT, SORT_MOD, SORT_NAME, SORT_UP, SORT_DOWN, JEI_RED, JEI_GREEN, IMPORT, PLUS, MINUS, REDSTONETRUE, REDSTONEFALSE, RED, GREY;

    public int getX() {
      switch (this) {
        case REDSTONETRUE:
          return 192;
        case REDSTONEFALSE:
          return 176;
        case IGNORELIST:
          return 195;
        case ALLOWLIST:
          return 177;
        case RED:
          return 179;
        case GREY:
          return 197;
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
        case IMPORT:
          return 176;
        case PLUS:
          return 196;
        case MINUS:
          return 177;
        default:
          return 0;
      }
    }

    public int getY() {
      switch (this) {
        case REDSTONETRUE:
          return 96;
        case REDSTONEFALSE:
          return 96;
        case IGNORELIST:
        case ALLOWLIST:
          return 80;
        case RED:
        case GREY:
          return 82;
        case SORT_UP:
        case SORT_DOWN:
        case SORT_AMT:
        case SORT_MOD:
        case SORT_NAME:
          return 127;
        case JEI_RED:
        case JEI_GREEN:
          return 140;
        case IMPORT:
          return 156;
        case PLUS:
          return 13;
        case MINUS:
          return 13;
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

  public ButtonRequest(int xPos, int yPos, String displayString, OnPress handler) {
    super(xPos, yPos, SIZE, SIZE, Component.translatable(displayString), handler);
    texture = new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/cable.png");
  }

  @Override
  public void render(PoseStack ms, int mx, int my, float pt) {
    super.render(ms, mx, my, pt);
  }

  @Override
  public void renderButton(PoseStack ms, int mouseX, int mouseY, float partial) {
    if (texture == null) {
      super.renderButton(ms, mouseX, mouseY, partial);
      return;
    }
    Minecraft minecraft = Minecraft.getInstance();
    //    minecraft.getTextureManager().bind(getTexture());
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, getTexture());
    int k = this.getYImage(this.isHovered);
    RenderSystem.enableBlend();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    this.blit(ms, this.x, this.y,
        160 + SIZE * k, 52,
        width, height);
    if (textureId != null) {
      this.blit(ms, this.x, this.y,
          textureId.getX(), textureId.getY(),
          width, height);
    }
    else {
      drawCenteredString(ms, Minecraft.getInstance().font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, 2210752);
    }
    this.renderBg(ms, minecraft, mouseX, mouseY);
  }

  public TextureEnum getTextureId() {
    return textureId;
  }

  public void setTextureId(TextureEnum textureId) {
    this.textureId = textureId;
  }
}
