package com.lothrazar.storagenetwork.block.cablefilter;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.request.GuiButtonRequest;
import com.lothrazar.storagenetwork.network.CableDataMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.util.inventory.FilterItemStackHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class GuiCableFilter extends ContainerScreen<ContainerCableFilter> {

  private final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/cable.png");

  //  protected GuiCableButton btnInputOutputStorage;
  ContainerCableFilter containerCableLink;
  private GuiButtonRequest btnMinus;
  private GuiButtonRequest btnPlus;
  private GuiButtonRequest btnWhite;
  private GuiButtonRequest btnImport;
  private boolean isWhitelist;

  public GuiCableFilter(ContainerCableFilter containerCableFilter, PlayerInventory inv, ITextComponent name) {
    super(containerCableFilter, inv, name);
    this.containerCableLink = containerCableFilter;
  }

  @Override
  public void init() {
    super.init();
    this.isWhitelist = containerCableLink.link.getFilter().isWhitelist;
    int x = guiLeft + 7, y = guiTop + 8;
    btnMinus = addButton(new GuiButtonRequest(x, y, "-", (p) -> {

      this.syncData(-1);
    }));
    x += 30;
    btnPlus = addButton(new GuiButtonRequest(x, y, "+", (p) -> {

      this.syncData(+1);
    }));
    x += 30;
    btnWhite = addButton(new GuiButtonRequest(x, y, "", (p) -> {
      this.isWhitelist = !this.isWhitelist;
      this.syncData(0);
    }));
    x += 30;
    btnImport = addButton(new GuiButtonRequest(x, y, "", (p) -> {
      syncFilterSlots();
    }));
  }

  private void syncFilterSlots() {
    PacketRegistry.INSTANCE.sendToServer(new CableDataMessage(CableDataMessage.CableMessageType.IMPORT_FILTER.ordinal()));
  }

  private void syncData(int priority) {
//    containerCableLink.link.setPriority(priority);
    containerCableLink.link.getFilter().isWhitelist = this.isWhitelist;
    PacketRegistry.INSTANCE.sendToServer(new CableDataMessage(CableDataMessage.CableMessageType.SYNC_DATA.ordinal(),
        priority, isWhitelist));
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    renderHoveredToolTip(mouseX, mouseY);
    if (containerCableLink == null || containerCableLink.link == null) {
      return;
    }
    btnWhite.setMessage(this.isWhitelist ? "w" : "b");
  }

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    int priority = containerCableLink.link.getPriority();
    font.drawString(String.valueOf(priority),
        30 - font.getStringWidth(String.valueOf(priority)) / 2,
        5,// btnMinus.y,
        4210752);
    this.drawTooltips(mouseX, mouseY);
  }

  private void drawTooltips(final int mouseX, final int mouseY) {
    //    "gui.storagenetwork.gui.import": "Import Filter",
    //        "gui.storagenetwork.gui.whitelist": "Whitelist",
    //        "gui.storagenetwork.gui.blacklist": "Blacklist",
    if (btnMinus != null && btnMinus.isMouseOver(mouseX, mouseY)) {
      renderTooltip(Lists.newArrayList(I18n.format("gui.storagenetwork.priority.down")), mouseX - guiLeft, mouseY);
    }
    if (btnPlus != null && btnPlus.isMouseOver(mouseX, mouseY)) {
      renderTooltip(Lists.newArrayList(I18n.format("gui.storagenetwork.priority.up")), mouseX - guiLeft, mouseY);
    }
  }

  @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    minecraft.getTextureManager().bindTexture(texture);
    int xCenter = (width - xSize) / 2;
    int yCenter = (height - ySize) / 2;
    blit(xCenter, yCenter, 0, 0, xSize, ySize);
  }

  public void setFilterItems(List<ItemStack> stacks) {
    FilterItemStackHandler filter = this.containerCableLink.link.getFilter();
    for (int i = 0; i < stacks.size(); i++) {
      ItemStack s = stacks.get(i);
      filter.setStackInSlot(i, s);
    }
  }
}
