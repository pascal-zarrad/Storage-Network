package com.lothrazar.storagenetwork.block.collection;

import java.util.List;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.IGuiPrivate;
import com.lothrazar.storagenetwork.capability.handler.FilterItemStackHandler;
import com.lothrazar.storagenetwork.gui.ItemSlotNetwork;
import com.lothrazar.storagenetwork.network.CableIOMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiCollectionFilter extends ContainerScreen<ContainerCollectionFilter> implements IGuiPrivate {

  private final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/plain_filter.png");
  ContainerCollectionFilter containerCableLink;
  private List<ItemSlotNetwork> itemSlotsGhost;

  public GuiCollectionFilter(ContainerCollectionFilter containerCableFilter, PlayerInventory inv, ITextComponent name) {
    super(containerCableFilter, inv, name);
    this.containerCableLink = containerCableFilter;
  }

  @Override
  public void renderStackTooltip(MatrixStack ms, ItemStack stack, int mousex, int mousey) {
    super.renderTooltip(ms, stack, mousex, mousey);
  }

  @Override
  public void drawGradient(MatrixStack ms, int x, int y, int x2, int y2, int u, int v) {
    super.fillGradient(ms, x, y, x2, y2, u, v);
  }

  @Override
  public void init() {
    super.init();
  }

  private void sendStackSlot(int value, ItemStack stack) {
    PacketRegistry.INSTANCE.sendToServer(new CableIOMessage(CableIOMessage.CableMessageType.SAVE_FITLER.ordinal(), value, stack));
  }

  @Override
  public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.renderHoveredTooltip(ms, mouseX, mouseY);
  }

  @Override
  public void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
    //    super.drawGuiContainerForegroundLayer(ms, mouseX, mouseY);
  }

  public static final int SLOT_SIZE = 18;

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
    minecraft.getTextureManager().bindTexture(texture);
    int xCenter = (width - xSize) / 2;
    int yCenter = (height - ySize) / 2;
    blit(ms, xCenter, yCenter, 0, 0, xSize, ySize);
    itemSlotsGhost = Lists.newArrayList();
    //TODO: shared with GuiCableIO
    int rows = 2;
    int cols = 9;
    int index = 0;
    int y = 35;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        ItemStack stack = containerCableLink.cap.getFilter().getStackInSlot(index);
        int x = 8 + col * SLOT_SIZE;
        itemSlotsGhost.add(new ItemSlotNetwork(this, stack, guiLeft + x, guiTop + y, stack.getCount(), guiLeft, guiTop, true));
        index++;
      }
      //move down to second row
      y += SLOT_SIZE;
    }
    for (ItemSlotNetwork s : itemSlotsGhost) {
      s.drawSlot(ms, font, mouseX, mouseY);
    }
  }

  public void setFilterItems(List<ItemStack> stacks) {
    FilterItemStackHandler filter = this.containerCableLink.cap.getFilter();
    for (int i = 0; i < stacks.size(); i++) {
      ItemStack s = stacks.get(i);
      filter.setStackInSlot(i, s);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    ItemStack mouse = minecraft.player.inventory.getItemStack();
    for (int i = 0; i < this.itemSlotsGhost.size(); i++) {
      ItemSlotNetwork slot = itemSlotsGhost.get(i);
      if (slot.isMouseOverSlot((int) mouseX, (int) mouseY)) {
        if (slot.getStack().isEmpty() == false) {
          //i hit non-empty slot, clear it no matter what
          if (mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT) {
            int direction = hasShiftDown() ? -1 : 1;
            int newCount = Math.min(64, slot.getStack().getCount() + direction);
            if (newCount < 1) {
              newCount = 1;
            }
            slot.getStack().setCount(newCount);
          }
          else {
            slot.setStack(ItemStack.EMPTY);
          }
          this.sendStackSlot(i, slot.getStack());
          return true;
        }
        else {
          //i hit an empty slot, save what im holding
          ItemStack cpy = mouse.copy();
          cpy.setCount(1);
          slot.setStack(cpy);
          this.sendStackSlot(i, cpy);
          return true;
        }
      }
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
    return super.isPointInRegion(x, y, width, height, mouseX, mouseY);
  }
}
