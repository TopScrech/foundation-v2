package asmodeuscore.core.astronomy.gui.book;

import asmodeuscore.api.space.IBookPage;
import asmodeuscore.core.utils.BookUtils;
import java.io.IOException;
import java.util.Map.Entry;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ACGuiGuideBook extends GuiScreen {
   private final int bookImageHeight = 250;
   private final int bookImageWidth = 450;
   private static ResourceLocation bookPageTexture = new ResourceLocation("asmodeuscore", "textures/gui/tablet.png");
   private int scroll = 0;
   private ACGuiGuideBook.Mode mode = ACGuiGuideBook.Mode.CATEGORIES;
   private String category = BookUtils.Book_Cateroies.GENERAL.getName();
   private static IBookPage page;
   private int maxX = 0;

   public ACGuiGuideBook() {
   }

   public ACGuiGuideBook(ACGuiGuideBook.Mode mode, String category, IBookPage page) {
      this.mode = mode;
      this.category = category;
      ACGuiGuideBook.page = page;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(bookPageTexture);
      int offsetFromScreenLeft = (this.width - 450) / 2;
      int offsetFromScreenTop = (this.height - 250) / 2;
      this.drawTexturedModalRect(
         (float)offsetFromScreenLeft, (float)offsetFromScreenTop, 450.0F, 256.0F, 0.0F, 0.0F, 400.0F, 256.0F, false, false, 512.0F, 256.0F
      );
      int posY = 0;
      if (mouseX >= offsetFromScreenLeft + 25 && mouseX < offsetFromScreenLeft + 58 && mouseY >= offsetFromScreenTop + 18 && mouseY <= offsetFromScreenTop + 32
         )
       {
         posY = 17;
      }

      if (this.mode != ACGuiGuideBook.Mode.CATEGORIES) {
         this.drawTexturedModalRect(
            (float)(offsetFromScreenLeft + 25),
            (float)(offsetFromScreenTop + 18),
            30.0F,
            15.0F,
            400.0F,
            (float)posY,
            20.0F,
            15.0F,
            false,
            false,
            512.0F,
            256.0F
         );
         this.drawTexturedModalRect(
            (float)(offsetFromScreenLeft + 40),
            (float)(offsetFromScreenTop + 18),
            30.0F,
            15.0F,
            467.0F,
            (float)posY,
            20.0F,
            15.0F,
            false,
            false,
            512.0F,
            256.0F
         );
         this.fontRenderer.drawString(GCCoreUtil.translate("book.button.back.name"), offsetFromScreenLeft + 39, offsetFromScreenTop + 22, 16777215);
      }

      if (this.mode == ACGuiGuideBook.Mode.CATEGORIES) {
         int i = 0;
         int offsetX = 65;
         int offsetY = 0;

         for (Entry<String, ResourceLocation> entry : BookUtils.GUIDE_BOOK_CATEGORIES.entrySet()) {
            offsetY = i / 6 * 60;
            int k = i > i % 6 ? i - 6 * (i / 6) : i;
            offsetX = 65 * k;
            this.mc.getTextureManager().bindTexture(bookPageTexture);
            this.drawTexturedModalRect(
               (float)(offsetFromScreenLeft + 42 + offsetX),
               (float)(offsetFromScreenTop + 52 + offsetY),
               36.0F,
               35.0F,
               460.0F,
               34.0F,
               22.0F,
               24.0F,
               false,
               false,
               512.0F,
               256.0F
            );
            this.mc.getTextureManager().bindTexture(entry.getValue());
            this.drawTexturedModalRect(
               (float)(offsetFromScreenLeft + 51 + offsetX),
               (float)(offsetFromScreenTop + 60 + offsetY),
               16.0F,
               16.0F,
               0.0F,
               0.0F,
               16.0F,
               16.0F,
               false,
               false,
               16.0F,
               16.0F
            );
            this.fontRenderer
               .drawSplitString(
                  GCCoreUtil.translate("book.category." + entry.getKey() + ".name"),
                  offsetFromScreenLeft + offsetX + 60 - this.fontRenderer.getStringWidth(entry.getKey()) / 2,
                  offsetFromScreenTop + 85 + offsetY,
                  50,
                  16777215
               );
            i++;
         }
      } else if (this.mode == ACGuiGuideBook.Mode.PAGES) {
         this.mc.getTextureManager().bindTexture(bookPageTexture);
         String s = GCCoreUtil.translate("book.category." + this.category + ".name");
         this.fontRenderer.drawString(s, this.width / 2 - this.fontRenderer.getStringWidth(s) / 2, offsetFromScreenTop + 20, 16777215);
         int getMaxScroll = 0;
         this.maxX = 0;

         for (IBookPage page : BookUtils.GUIDE_BOOK_PAGES) {
            if (page.getCategory().equals(this.category)) {
               int currX = this.fontRenderer
                  .getStringWidth(page.rawTitle() ? GCCoreUtil.translate(page.titlePage()) : GCCoreUtil.translate("book.page." + page.titlePage()));
               this.maxX = this.maxX < currX ? currX : this.maxX;
               getMaxScroll++;
            }
         }

         getMaxScroll -= 7;
         if (getMaxScroll > 8) {
            this.mc.getTextureManager().bindTexture(bookPageTexture);
            this.drawTexturedModalRect(
               (float)(offsetFromScreenLeft + 417), (float)(offsetFromScreenTop + 36), 17.0F, 186.0F, 492.0F, 0.0F, 14.0F, 185.0F, false, false, 512.0F, 256.0F
            );
            this.drawTexturedModalRect(
               (float)(offsetFromScreenLeft + 417), (float)(offsetFromScreenTop + 36), 17.0F, 28.0F, 400.0F, 68.0F, 15.0F, 28.0F, false, false, 512.0F, 256.0F
            );
            this.drawTexturedModalRect(
               (float)(offsetFromScreenLeft + 419), (float)(offsetFromScreenTop + 194), 16.0F, 28.0F, 400.0F, 68.0F, 14.0F, 28.0F, true, true, 512.0F, 256.0F
            );
            int pos = getMaxScroll > 0 ? Math.round((float)(118 / getMaxScroll)) : getMaxScroll;
            this.drawTexturedModalRect(
               (float)(offsetFromScreenLeft + 420),
               (float)(offsetFromScreenTop + 60 + this.scroll * pos),
               15.0F,
               28.0F,
               429.0F,
               68.0F,
               14.0F,
               28.0F,
               false,
               false,
               512.0F,
               256.0F
            );
         }

         if (Mouse.hasWheel()) {
            int dWheel = Mouse.getDWheel();
            if (dWheel > 0 && this.scroll > 0) {
               this.scroll--;
            } else if (dWheel < 0 && this.scroll < getMaxScroll) {
               this.scroll++;
            }
         }

         int i = 0;
         int offsetX = 21;

         for (IBookPage pagex : BookUtils.GUIDE_BOOK_PAGES) {
            if (pagex.getCategory().equals(this.category)) {
               if (i >= this.scroll && i <= 7 + this.scroll) {
                  this.mc.getTextureManager().bindTexture(bookPageTexture);
                  int var13 = 0;
                  if (mouseX >= offsetFromScreenLeft + 44
                     && mouseX < offsetFromScreenLeft + 40 + 25 + this.maxX
                     && mouseY >= offsetFromScreenTop + 41 + offsetX * (i - this.scroll)
                     && mouseY <= offsetFromScreenTop + 41 + offsetX * (i - this.scroll) + 16) {
                     var13 = 17;
                  }

                  this.drawTexturedModalRect(
                     (float)(offsetFromScreenLeft + 40),
                     (float)(offsetFromScreenTop + 41 + offsetX * (i - this.scroll)),
                     25.0F,
                     16.0F,
                     400.0F,
                     (float)var13,
                     20.0F,
                     15.0F,
                     false,
                     false,
                     512.0F,
                     256.0F
                  );
                  this.drawTexturedModalRect(
                     (float)(offsetFromScreenLeft + 40 + 16),
                     (float)(offsetFromScreenTop + 41 + offsetX * (i - this.scroll)),
                     (float)this.maxX,
                     16.0F,
                     410.0F,
                     (float)var13,
                     70.0F,
                     15.0F,
                     false,
                     false,
                     512.0F,
                     256.0F
                  );
                  this.drawTexturedModalRect(
                     (float)(offsetFromScreenLeft + 40 + this.maxX),
                     (float)(offsetFromScreenTop + 41 + offsetX * (i - this.scroll)),
                     25.0F,
                     16.0F,
                     467.0F,
                     (float)var13,
                     20.0F,
                     15.0F,
                     false,
                     false,
                     512.0F,
                     256.0F
                  );
                  if (pagex.iconTitle() != null) {
                     this.mc.getTextureManager().bindTexture(bookPageTexture);
                     this.drawTexturedModalRect(
                        (float)(offsetFromScreenLeft + 20),
                        (float)(offsetFromScreenTop + 41 + offsetX * (i - this.scroll)),
                        21.0F,
                        21.0F,
                        461.0F,
                        35.0F,
                        18.0F,
                        21.0F,
                        false,
                        false,
                        512.0F,
                        256.0F
                     );
                     this.mc.getTextureManager().bindTexture(pagex.iconTitle());
                     this.drawTexturedModalRect(
                        (float)(offsetFromScreenLeft + 25),
                        (float)(offsetFromScreenTop + 45 + offsetX * (i - this.scroll)),
                        12.0F,
                        12.0F,
                        0.0F,
                        0.0F,
                        16.0F,
                        16.0F,
                        false,
                        false,
                        16.0F,
                        16.0F
                     );
                  }

                  if (pagex.itemIcon() != ItemStack.EMPTY) {
                     this.mc.getTextureManager().bindTexture(bookPageTexture);
                     this.drawTexturedModalRect(
                        (float)(offsetFromScreenLeft + 20),
                        (float)(offsetFromScreenTop + 41 + offsetX * (i - this.scroll)),
                        21.0F,
                        21.0F,
                        461.0F,
                        35.0F,
                        18.0F,
                        21.0F,
                        false,
                        false,
                        512.0F,
                        256.0F
                     );
                     RenderHelper.enableGUIStandardItemLighting();
                     this.mc
                        .getRenderItem()
                        .renderItemAndEffectIntoGUI(pagex.itemIcon(), offsetFromScreenLeft + 23, offsetFromScreenTop + 43 + offsetX * (i - this.scroll));
                     RenderHelper.disableStandardItemLighting();
                  }

                  this.fontRenderer
                     .drawString(
                        pagex.rawTitle() ? GCCoreUtil.translate(pagex.titlePage()) : GCCoreUtil.translate("book.page." + pagex.titlePage() + ".name"),
                        offsetFromScreenLeft + 52,
                        offsetFromScreenTop + 45 + offsetX * (i - this.scroll),
                        16777215
                     );
                  i++;
               } else {
                  i++;
               }
            }
         }

         if (i == 0) {
            this.fontRenderer
               .drawString(
                  "Whoops! Empty category! We will work on pages from this category!",
                  offsetFromScreenLeft + 52,
                  offsetFromScreenTop + 45 + offsetX * i,
                  16777215
               );
         }
      } else if (this.mode == ACGuiGuideBook.Mode.TEXT) {
         String s = GCCoreUtil.translate("book.page." + ACGuiGuideBook.page.titlePage() + ".name");
         if (ACGuiGuideBook.page.rawTitle()) {
            s = GCCoreUtil.translate(ACGuiGuideBook.page.titlePage());
         }

         this.fontRenderer.drawString(s, this.width / 2 - this.fontRenderer.getStringWidth(s) / 2, offsetFromScreenTop + 20, 16777215);
         ACGuiGuideBook.page.drawPage(offsetFromScreenLeft, offsetFromScreenTop, this.fontRenderer, mouseX, mouseY);
      }

      this.fontRenderer.drawString(EnumColor.ORANGE + "Book in Beta", offsetFromScreenLeft + 370, offsetFromScreenTop + 22, 16777215);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      int offsetFromScreenLeft = (this.width - 450) / 2;
      int offsetFromScreenTop = (this.height - 250) / 2;
      if (this.mode == ACGuiGuideBook.Mode.CATEGORIES) {
         int i = 0;
         int offsetX = 65;
         int offsetY = 0;

         for (Entry<String, ResourceLocation> entry : BookUtils.GUIDE_BOOK_CATEGORIES.entrySet()) {
            offsetY = i / 6 * 60;
            int k = i > i % 6 ? i - 6 * (i / 6) : i;
            offsetX = 65 * k;
            if (mouseX >= offsetFromScreenLeft + 30 + offsetX
               && mouseX <= offsetFromScreenLeft + 70 + offsetX + 10
               && mouseY >= offsetFromScreenTop + 50 + offsetY
               && mouseY <= offsetFromScreenTop + 100 + offsetY) {
               this.category = entry.getKey();
               this.mode = ACGuiGuideBook.Mode.PAGES;
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            i++;
         }
      } else if (this.mode == ACGuiGuideBook.Mode.PAGES) {
         int i = 0;
         int offsetX = 21;

         for (IBookPage entry : BookUtils.GUIDE_BOOK_PAGES) {
            if (entry.getCategory().equals(this.category)) {
               if (i >= this.scroll && i <= 7 + this.scroll) {
                  if (mouseX >= offsetFromScreenLeft + 44
                     && mouseX < offsetFromScreenLeft + 40 + 25 + this.maxX
                     && mouseY >= offsetFromScreenTop + 40 + offsetX * (i - this.scroll)
                     && mouseY <= offsetFromScreenTop + 56 + offsetX * (i - this.scroll)) {
                     try {
                        page = (IBookPage)entry.getClass().newInstance();
                     } catch (IllegalAccessException | InstantiationException var12) {
                        var12.printStackTrace();
                     }

                     this.mode = ACGuiGuideBook.Mode.TEXT;
                     Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                  }

                  i++;
               } else {
                  i++;
               }
            }
         }

         if (mouseX >= offsetFromScreenLeft + 25
            && mouseX < offsetFromScreenLeft + 58
            && mouseY >= offsetFromScreenTop + 18
            && mouseY <= offsetFromScreenTop + 32) {
            this.mode = ACGuiGuideBook.Mode.CATEGORIES;
            this.scroll = 0;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      } else if (this.mode == ACGuiGuideBook.Mode.TEXT) {
         page.mouseClick(mouseX, mouseY, mouseButton, offsetFromScreenLeft, offsetFromScreenTop);
         if (mouseX >= offsetFromScreenLeft + 25
            && mouseX < offsetFromScreenLeft + 58
            && mouseY >= offsetFromScreenTop + 18
            && mouseY <= offsetFromScreenTop + 32) {
            if (!page.hookBackButton()) {
               this.mode = ACGuiGuideBook.Mode.PAGES;
            }

            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawTexturedModalRect(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, boolean invertX, boolean invertY) {
      this.drawTexturedModalRect(
         (float)x, (float)y, (float)width, (float)height, (float)u, (float)v, (float)uWidth, (float)vHeight, invertX, invertY, 512.0F, 512.0F
      );
   }

   public void drawTexturedModalRect(
      float x,
      float y,
      float width,
      float height,
      float u,
      float v,
      float uWidth,
      float vHeight,
      boolean invertX,
      boolean invertY,
      float texSizeX,
      float texSizeY
   ) {
      float texModX = 1.0F / texSizeX;
      float texModY = 1.0F / texSizeY;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      float height0 = invertY ? 0.0F : vHeight;
      float height1 = invertY ? vHeight : 0.0F;
      float width0 = invertX ? uWidth : 0.0F;
      float width1 = invertX ? 0.0F : uWidth;
      vertexbuffer.pos((double)x, (double)(y + height), (double)this.zLevel)
         .tex((double)((u + width0) * texModX), (double)((v + height0) * texModY))
         .endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel)
         .tex((double)((u + width1) * texModX), (double)((v + height0) * texModY))
         .endVertex();
      vertexbuffer.pos((double)(x + width), (double)y, (double)this.zLevel)
         .tex((double)((u + width1) * texModX), (double)((v + height1) * texModY))
         .endVertex();
      vertexbuffer.pos((double)x, (double)y, (double)this.zLevel).tex((double)((u + width0) * texModX), (double)((v + height1) * texModY)).endVertex();
      tessellator.draw();
   }

   public static enum Mode {
      CATEGORIES,
      PAGES,
      TEXT;
   }
}
