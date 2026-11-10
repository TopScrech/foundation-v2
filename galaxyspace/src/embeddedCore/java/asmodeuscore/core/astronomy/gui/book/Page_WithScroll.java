package asmodeuscore.core.astronomy.gui.book;

import asmodeuscore.api.space.IBookPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public abstract class Page_WithScroll implements IBookPage {
   private static int scroll = 0;
   private static int maxscroll = 1;
   protected static ResourceLocation bookPageTexture = new ResourceLocation("asmodeuscore", "textures/gui/tablet.png");
   private Minecraft mc = Minecraft.getMinecraft();

   public Page_WithScroll() {
      scroll = 0;
   }

   @Override
   public abstract String titlePage();

   @Override
   public abstract ResourceLocation iconTitle();

   @Override
   public void drawPage(int x, int y, FontRenderer font, int mouseX, int mouseY) {
      if (Mouse.hasWheel()) {
         int dWheel = Mouse.getDWheel();
         if (dWheel > 0 && scroll > 0) {
            scroll--;
         } else if (dWheel < 0 && scroll < this.getMaxScroll()) {
            scroll++;
         }
      }

      this.mc.getTextureManager().bindTexture(bookPageTexture);
      this.drawTexturedModalRect((float)(x + 417), (float)(y + 36), 17.0F, 186.0F, 492.0F, 0.0F, 14.0F, 185.0F, false, false, 512.0F, 256.0F);
      this.drawTexturedModalRect((float)(x + 417), (float)(y + 36), 17.0F, 28.0F, 400.0F, 68.0F, 15.0F, 28.0F, false, false, 512.0F, 256.0F);
      int pos = this.getMaxScroll() > 0 ? Math.round((float)(116 / this.getMaxScroll())) : this.getMaxScroll();
      this.drawTexturedModalRect((float)(x + 420), (float)(y + 58 + scroll * pos), 15.0F, 28.0F, 429.0F, 68.0F, 14.0F, 28.0F, false, false, 512.0F, 256.0F);
      this.drawTexturedModalRect((float)(x + 419), (float)(y + 194), 16.0F, 28.0F, 400.0F, 68.0F, 14.0F, 28.0F, true, true, 512.0F, 256.0F);
   }

   @Override
   public void mouseClick(int mouseX, int mouseY, int mouseButton, int x, int y) {
      if (this.checkClick(mouseX, mouseY, x + 420, y + 40, 10, 15) && scroll > 0) {
         scroll--;
      }

      if (this.checkClick(mouseX, mouseY, x + 420, y + 198, 10, 20) && scroll < this.getMaxScroll()) {
         scroll++;
      }
   }

   @Override
   public abstract String getCategory();

   public abstract int getMaxScroll();

   public int setScroll(int count) {
      scroll = count;
      return count;
   }

   public int getScroll() {
      return scroll;
   }

   private boolean checkClick(int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
      return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY;
   }
}
