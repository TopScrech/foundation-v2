package asmodeuscore.core.astronomy.gui.screen;

import asmodeuscore.core.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiTeleporting;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomTeleporting extends GuiTeleporting {
   private final int dimID;
   private static List<GuiCustomTeleporting.TeleportData> list = new ArrayList<>();
   private GuiCustomTeleporting.TeleportData data = new GuiCustomTeleporting.TeleportData(
      new ResourceLocation("asmodeuscore:textures/gui/celestial_teleport.png"),
      new String[]{"Taking one small step", "Taking one giant leap", "Prepare for entry!"},
      -1
   );
   private final String message;
   private Minecraft mc = Minecraft.getMinecraft();
   private int timer = 0;

   public GuiCustomTeleporting(int targetDimensionID) {
      super(targetDimensionID);
      this.dimID = targetDimensionID;

      for (GuiCustomTeleporting.TeleportData data : list) {
         if (data.dimid == targetDimensionID) {
            this.data = data;
            break;
         }

         this.data = list.get(0);
      }

      this.message = this.data.texts[(int)(Math.random() * (double)this.data.texts.length)];
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      ScaledResolution res = new ScaledResolution(this.mc);
      this.mc.getTextureManager().bindTexture(this.data.image);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Utils.drawTexturedModalRect(
         0.0F,
         0.0F,
         (float)this.width,
         (float)this.height,
         0.0F,
         0.0F,
         (float)this.width,
         (float)this.height,
         false,
         false,
         (float)this.width,
         (float)this.height
      );
      int size = this.message.length() * 5 + 40;
      this.drawHorizontalGradientRect(
         res.getScaledWidth() / 2 - size - 40,
         res.getScaledHeight() - 50,
         res.getScaledWidth() / 2 - size,
         res.getScaledHeight() - 20,
         Utils.getIntColorWHC(0, 0, 0, 0),
         -1442840576
      );
      drawRect(res.getScaledWidth() / 2 - size, res.getScaledHeight() - 50, res.getScaledWidth() / 2 + size, res.getScaledHeight() - 20, -1442840576);
      this.drawHorizontalGradientRect(
         res.getScaledWidth() / 2 + size,
         res.getScaledHeight() - 50,
         res.getScaledWidth() / 2 + size + 40,
         res.getScaledHeight() - 20,
         -1442840576,
         Utils.getIntColorWHC(0, 0, 0, 0)
      );
      this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.height - 40, ColorUtil.to32BitColor(255, 255, 255, 255));
   }

   public void updateScreen() {
      if (this.mc.player != null
         && this.mc.player.world != null
         && this.mc.player.world.provider.getDimension() == this.dimID
         && (this.mc.player.world.provider instanceof WorldProviderSpaceStation || this.mc.player.posY - this.mc.player.lastTickPosY < 0.0)) {
         this.mc.displayGuiScreen(null);
         TickHandlerClient.teleportingGui = null;
      }
   }

   public static List<GuiCustomTeleporting.TeleportData> getList() {
      return list;
   }

   private void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 0xFF) / 255.0F;
      float f1 = (float)(startColor >> 16 & 0xFF) / 255.0F;
      float f2 = (float)(startColor >> 8 & 0xFF) / 255.0F;
      float f3 = (float)(startColor & 0xFF) / 255.0F;
      float f4 = (float)(endColor >> 24 & 0xFF) / 255.0F;
      float f5 = (float)(endColor >> 16 & 0xFF) / 255.0F;
      float f6 = (float)(endColor >> 8 & 0xFF) / 255.0F;
      float f7 = (float)(endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)right, (double)top, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
      bufferbuilder.pos((double)left, (double)top, (double)this.zLevel).color(f1, f3, f3, f).endVertex();
      bufferbuilder.pos((double)left, (double)bottom, (double)this.zLevel).color(f1, f3, f3, f).endVertex();
      bufferbuilder.pos((double)right, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static class TeleportData {
      private final String[] texts;
      private final ResourceLocation image;
      private final int dimid;

      public TeleportData(ResourceLocation image, String[] texts, int dimID) {
         this.image = image;
         this.texts = texts;
         this.dimid = dimID;
      }
   }
}
