package asmodeuscore.core.astronomy.gui.overlay;

import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.event.AsmodeusClientEvent;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.Overlay;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class OverlayDetectors extends Overlay {
   private static Minecraft minecraft = FMLClientHandler.instance().getClient();

   public static void renderPressureIndicator(float pressureLevel, boolean invalid, boolean right, boolean top) {
      EntityPlayerSP player = minecraft.player;
      ResourceLocation guiTexture = new ResourceLocation("asmodeuscore", "textures/gui/gui.png");
      ScaledResolution scaledresolution = ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
      int width = scaledresolution.getScaledWidth();
      int height = scaledresolution.getScaledHeight();
      minecraft.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.disableLighting();
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture);
      int minLeftX = 0;
      int maxLeftX = 0;
      int minRightX = 0;
      int maxRightX = 0;
      double bottomY = 0.0;
      double topY = 0.0;
      double zLevel = -190.0;
      float texMod = 0.00390625F;
      if (top) {
         topY = 0.0;
      } else {
         topY = (double)(height - 68);
      }

      if (OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
         bottomY = topY + 57.5;
      } else {
         bottomY = topY + 5.5;
      }

      if (right) {
         minLeftX = width - 49;
         maxLeftX = width - 40;
         minRightX = width - 59;
         maxRightX = width - 74;
      } else {
         minLeftX = 10;
         int var24 = 29;
         minRightX = 10;
         int var27 = 49;
      }

      if (pressureLevel > 48.0F) {
         pressureLevel = 48.0F;
      }

      pressureLevel *= 0.5F;
      drawTexturedModalRect((double)((float)minRightX), (double)((float)bottomY + 1.0F), 49.0F, 9.0F, 0.0F, 0.0F, 64.0F, 9.0F, false, false, 256.0F, 256.0F);
      drawTexturedModalRect(
         (double)((float)minRightX),
         (double)((float)bottomY + 2.0F),
         pressureLevel,
         6.0F,
         0.0F,
         9.0F,
         pressureLevel + 14.0F,
         6.0F,
         false,
         false,
         256.0F,
         256.0F
      );
      drawTexturedModalRect((double)((float)minRightX), (double)((float)bottomY + 4.0F), 50.0F, 5.0F, 0.0F, 15.0F, 64.0F, 5.0F, false, false, 256.0F, 256.0F);
      if (invalid) {
         String value = GCCoreUtil.translate("gui.warning.atmosphericpressure");
         if (right) {
            minecraft.fontRenderer
               .drawString(
                  value,
                  minLeftX + 40 - minecraft.fontRenderer.getStringWidth(value),
                  (int)bottomY + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                  ColorUtil.to32BitColor(255, 255, 10, 10)
               );
         } else {
            minecraft.fontRenderer
               .drawString(value, minLeftX + 60, (int)bottomY + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1, ColorUtil.to32BitColor(255, 255, 10, 10));
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   public static void renderWaterPressureIndicator(float pressureLevel, boolean invalid, boolean right, boolean top) {
      EntityPlayerSP player = minecraft.player;
      ResourceLocation guiTexture = new ResourceLocation("asmodeuscore", "textures/gui/gui.png");
      ScaledResolution scaledresolution = ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
      int width = scaledresolution.getScaledWidth();
      int height = scaledresolution.getScaledHeight();
      minecraft.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.disableLighting();
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture);
      int minLeftX = 0;
      int maxLeftX = 0;
      int minRightX = 0;
      int maxRightX = 0;
      double bottomY = 0.0;
      double topY = 0.0;
      double zLevel = -190.0;
      float texMod = 0.00390625F;
      if (top) {
         topY = 10.0;
      } else {
         topY = (double)(height - 60);
      }

      if (right) {
         minLeftX = width - 49;
         maxLeftX = width - 40;
         minRightX = width - 79;
         maxRightX = width - 74;
      } else {
         minLeftX = 10;
         int var23 = 29;
         minRightX = 79;
         int var26 = 49;
      }

      if (pressureLevel > 52.0F) {
         pressureLevel = 52.0F;
      }

      drawTexturedModalRect((double)((float)minRightX), (double)((float)topY + 0.5F), 16.0F, 56.0F, 73.0F, 55.0F, 16.0F, 56.0F, false, false, 256.0F, 256.0F);
      GlStateManager.color(1.0F, 0.0F, 0.0F);
      drawTexturedModalRect(
         (double)((float)minRightX + 1.0F),
         (double)((float)topY + 1.0F + pressureLevel + 0.5F),
         6.0F,
         1.0F,
         16.0F,
         9.0F,
         1.0F,
         1.0F,
         false,
         false,
         256.0F,
         256.0F
      );
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      if (invalid) {
         String value = GCCoreUtil.translate("gui.warning.waterpressure");
         if (right) {
            minecraft.fontRenderer
               .drawString(
                  value,
                  minLeftX - 40 - minecraft.fontRenderer.getStringWidth(value),
                  (int)bottomY + 54 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                  ColorUtil.to32BitColor(255, 255, 10, 10)
               );
         } else {
            minecraft.fontRenderer
               .drawString(value, minLeftX + 60, (int)bottomY + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1, ColorUtil.to32BitColor(255, 255, 10, 10));
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   public static void renderRadiationIndicator(int radLevel, boolean invalid, boolean right, boolean top) {
      EntityPlayerSP player = minecraft.player;
      ResourceLocation guiTexture = new ResourceLocation("asmodeuscore", "textures/gui/gui.png");
      ScaledResolution scaledresolution = ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
      int width = scaledresolution.getScaledWidth();
      int height = scaledresolution.getScaledHeight();
      minecraft.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.disableLighting();
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture);
      int minLeftX = 0;
      int maxLeftX = 0;
      int minRightX = 0;
      int maxRightX = 0;
      double bottomY = 0.0;
      double topY = 0.0;
      double zLevel = -190.0;
      float texMod = 0.00390625F;
      if (top) {
         topY = 0.0;
      } else {
         topY = (double)(height - 68);
      }

      if (OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
         bottomY = topY + 10.5;
      } else {
         bottomY = topY + 5.5;
      }

      if (right) {
         minLeftX = width - 49;
         maxLeftX = width - 40;
         minRightX = width - 69;
         maxRightX = width - 69;
      } else {
         minLeftX = 10;
         int var25 = 29;
         minRightX = 69;
         maxRightX = 69;
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder worldRenderer = tessellator.getBuffer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos((double)maxRightX, bottomY + 56.0, zLevel).tex((double)(65.0F * texMod), (double)(56.0F * texMod)).endVertex();
      worldRenderer.pos((double)(maxRightX + 8), bottomY + 56.0, zLevel).tex((double)(73.0F * texMod), (double)(56.0F * texMod)).endVertex();
      worldRenderer.pos((double)(maxRightX + 8), bottomY, zLevel).tex((double)(73.0F * texMod), (double)(0.0F * texMod)).endVertex();
      worldRenderer.pos((double)maxRightX, bottomY, zLevel).tex((double)(65.0F * texMod), (double)(0.0F * texMod)).endVertex();
      tessellator.draw();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos((double)minRightX, bottomY + 56.0, zLevel).tex((double)(73.0F * texMod), (double)(56.0F * texMod)).endVertex();
      worldRenderer.pos((double)(maxRightX + 8), bottomY + 56.0, zLevel).tex((double)(81.0F * texMod), (double)(56.0F * texMod)).endVertex();
      worldRenderer.pos((double)(maxRightX + 8), bottomY + 56.0 - (double)radLevel, zLevel)
         .tex((double)(81.0F * texMod), (double)((float)(56 - radLevel) * texMod))
         .endVertex();
      worldRenderer.pos((double)minRightX, bottomY + 56.0 - (double)radLevel, zLevel)
         .tex((double)(73.0F * texMod), (double)((float)(56 - radLevel) * texMod))
         .endVertex();
      tessellator.draw();
      if (invalid) {
         String value = GCCoreUtil.translate("gui.warning.solarradiation");
         if (right) {
            minecraft.fontRenderer
               .drawString(
                  value,
                  minLeftX - 22 - minecraft.fontRenderer.getStringWidth(value),
                  (int)bottomY + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                  ColorUtil.to32BitColor(255, 255, 255, 10)
               );
         } else {
            minecraft.fontRenderer
               .drawString(value, minLeftX + 75, (int)bottomY + 7 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1, ColorUtil.to32BitColor(255, 255, 255, 10));
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   public static void renderInfoPanel(float tempLevel, boolean invalid, boolean right, boolean top) {
      EntityPlayerSP player = minecraft.player;
      GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
      ResourceLocation guiTexture = new ResourceLocation("asmodeuscore", "textures/gui/gui.png");
      ScaledResolution scaledresolution = ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
      int width = scaledresolution.getScaledWidth();
      int height = scaledresolution.getScaledHeight();
      minecraft.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.disableLighting();
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture);
      int minLeftX = 0;
      int maxLeftX = 0;
      int minRightX = 0;
      int maxRightX = 0;
      double bottomY = 0.0;
      double topY = 0.0;
      double zLevel = -190.0;
      float texMod = 0.00390625F;
      topY = top ? 0.0 : (double)(height - 160);
      if (!OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
         bottomY = topY + 15.5;
      } else {
         bottomY = topY + 67.0;
      }

      bottomY += (double)AsmodeusConfig.tempPosY;
      if (right) {
         minLeftX = width - 49;
         maxLeftX = width - 40;
         minRightX = width - 72 + AsmodeusConfig.tempPosX;
         maxRightX = width - 69;
      } else {
         int var23 = 10;
         int var25 = 29;
         minRightX = 3;
         int var28 = 69;
      }

      drawTexturedModalRect((double)((float)minRightX), (double)((float)bottomY + 1.0F), 65.0F, 26.0F, 0.0F, 21.0F, 65.0F, 22.0F, !right, false, 256.0F, 256.0F);
      drawTexturedModalRect(
         (double)((float)minRightX + 50.0F + (float)(!right ? -5 : 3)),
         (double)((float)bottomY + 6.0F),
         12.0F,
         15.0F,
         81.0F,
         0.0F,
         12.0F,
         14.0F,
         !right,
         false,
         256.0F,
         256.0F
      );
      String degree = AsmodeusConfig.enableRenderDegreeSymbol ? "°" : "";
      if (player != null && AsmodeusClientEvent.inOxygenBlock) {
         minecraft.fontRenderer.drawString("20.0" + degree + "C", minRightX + 10 + (!right ? 12 : 0), (int)bottomY + 10, 65280);
      } else {
         minecraft.fontRenderer.drawString(String.format("%.1f", tempLevel) + degree + "C", minRightX + 5 + (!right ? 5 : 0), (int)bottomY + 10, 16777215);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   public static void drawTexturedModalRect(
      double xOffset,
      double yOffset,
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
      double zLevel = -200.0;
      GL11.glShadeModel(7424);
      GL11.glEnable(3042);
      GL11.glEnable(3008);
      GL11.glEnable(3553);
      float texModX = 1.0F / texSizeX;
      float texModY = 1.0F / texSizeY;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder worldRenderer = tessellator.getBuffer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      float height0 = invertY ? 0.0F : vHeight;
      float height1 = invertY ? vHeight : 0.0F;
      float width0 = invertX ? uWidth : 0.0F;
      float width1 = invertX ? 0.0F : uWidth;
      worldRenderer.pos(xOffset, yOffset + (double)height, zLevel).tex((double)((u + width0) * texModX), (double)((v + height0) * texModY)).endVertex();
      worldRenderer.pos(xOffset + (double)width, yOffset + (double)height, zLevel)
         .tex((double)((u + width1) * texModX), (double)((v + height0) * texModY))
         .endVertex();
      worldRenderer.pos(xOffset + (double)width, yOffset, zLevel).tex((double)((u + width1) * texModX), (double)((v + height1) * texModY)).endVertex();
      worldRenderer.pos(xOffset, yOffset, zLevel).tex((double)((u + width0) * texModX), (double)((v + height1) * texModY)).endVertex();
      tessellator.draw();
   }
}
