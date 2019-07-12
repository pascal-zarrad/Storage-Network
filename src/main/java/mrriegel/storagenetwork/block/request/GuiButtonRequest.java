package mrriegel.storagenetwork.block.request;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonRequest extends GuiButtonExt {

  private final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/request.png");
  public GuiButtonRequest(int xPos, int yPos, int width, int height, String displayString, IPressable handler) {
    super(xPos, yPos, width, height, displayString, handler);
    this.render();
  }


  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    Minecraft.getInstance().getTextureManager().bindTexture(texture);
    super.render(p_render_1_,p_render_2_,p_render_3_);
  }

  @Override
  public void renderButton(int mouseX, int mouseY, float partial) {
    Minecraft.getInstance().getTextureManager().bindTexture(texture);
    super.renderButton(mouseX, mouseY, partial);
  }
}
