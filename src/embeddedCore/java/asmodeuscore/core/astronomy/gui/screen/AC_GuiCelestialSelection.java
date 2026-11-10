package asmodeuscore.core.astronomy.gui.screen;

import asmodeuscore.api.space.ICelestialRegistry;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.SpaceData;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.utils.ACCompatibilityManager;
import asmodeuscore.core.utils.UtilsGC;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Post;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class AC_GuiCelestialSelection extends GuiCelestialSelection {
   protected EnumView viewState = EnumView.PREVIEW;
   protected Random rand = new Random();
   private String galaxy = GalacticraftCore.planetOverworld.getParentSolarSystem().getUnlocalizedParentGalaxyName();
   private List<String> galaxylist = new ArrayList<>();
   private List<SolarSystem> starlist = new ArrayList<>();
   private static ResourceLocation guiMain2 = new ResourceLocation("asmodeuscore", "textures/gui/celestialselection2.png");
   private static ResourceLocation guiImg = new ResourceLocation("asmodeuscore", "textures/gui/galaxymap_1.png");
   private double xImgOffset = 0.0;
   private double yImgOffset = 0.0;
   private Vector3 nebula_color = new Vector3((double)this.rand.nextFloat(), (double)this.rand.nextFloat(), (double)this.rand.nextFloat());
   private int nebula_img = this.rand.nextInt(2);
   private boolean enableGrid = true;

   public AC_GuiCelestialSelection(boolean mapMode, List<CelestialBody> possibleBodies, boolean canCreateStations) {
      super(mapMode, possibleBodies, canCreateStations);
   }

   public void initGui() {
      super.initGui();
      BORDER_SIZE = this.width / 65;
      BORDER_EDGE_SIZE = BORDER_SIZE / 4;
      this.refreshBodies();
   }

   public void updateScreen() {
      super.updateScreen();
      if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
         this.xImgOffset = this.xImgOffset + (double)this.translation.x;
         this.yImgOffset = this.yImgOffset + (double)this.translation.y;
      }

      if (this.xImgOffset > 20.0) {
         this.xImgOffset = 20.0;
      }

      if (this.xImgOffset < -150.0) {
         this.xImgOffset = -150.0;
      }

      if (this.yImgOffset > 20.0) {
         this.yImgOffset = 20.0;
      }

      if (this.yImgOffset < -80.0) {
         this.yImgOffset = -80.0;
      }
   }

   public void drawScreen(int mousePosX, int mousePosY, float partialTicks) {
      super.drawScreen(mousePosX, mousePosY, partialTicks);
   }

   public void setBlackBackground() {
      super.setBlackBackground();
      if (AsmodeusConfig.enableImgOnGalaxyMap) {
         GL11.glPushMatrix();
         this.enableGrid = false;
         this.setImgBackground(guiImg);
         GL11.glPopMatrix();
      }
   }

   public void drawGrid(float gridSize, float gridScale) {
      if (this.enableGrid) {
         super.drawGrid(gridSize, gridScale);
      }
   }

   public void drawCircles() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glLineWidth(2.0F);
      int count = 0;
      float theta = 0.06981317F;
      float cos = (float)Math.cos(0.06981317F);
      float sin = (float)Math.sin(0.06981317F);

      for (CelestialBody body : this.bodiesToRender) {
         Vector3f systemOffset = new Vector3f(0.0F, 0.0F, 0.0F);
         if (body instanceof IChildBody) {
            systemOffset = this.getCelestialBodyPosition(((IChildBody)body).getParentPlanet());
         } else if (body instanceof Planet) {
            systemOffset = this.getCelestialBodyPosition(((Planet)body).getParentSolarSystem().getMainStar());
         } else if (body instanceof Star) {
            systemOffset = this.getCelestialBodyPosition((Star)body);
         } else if (body instanceof ICelestialRegistry && !(body instanceof IChildBody)) {
            systemOffset = this.getCelestialBodyPosition(((ICelestialRegistry)body).getParentSolarSystem().getMainStar());
         }

         float orbit_ext_x = body instanceof IExBody ? ((IExBody)body).getXOrbitEccentricity() : 1.0F;
         float orbit_ext_y = body instanceof IExBody ? ((IExBody)body).getYOrbitEccentricity() : 1.0F;
         float orbit_offset_x = body instanceof IExBody ? ((IExBody)body).getXOrbitOffset() : 0.0F;
         float orbit_offset_y = body instanceof IExBody ? ((IExBody)body).getYOrbitOffset() : 0.0F;
         float x = this.getScale(body);
         float y = 0.0F;
         float alpha = this.getAlpha(body);
         if (alpha > 0.0F) {
            switch (count % 2) {
               case 0:
                  GL11.glColor4f(0.0F, 0.2857143F, 0.64285713F, alpha / 2.4F);
                  break;
               case 1:
                  GL11.glColor4f(0.0F, 0.4F, 0.9F, alpha / 2.4F);
            }

            if (body.equals(this.selectedBody)) {
               GL11.glColor4f(0.0F, 0.4F, 0.9F, 1.0F);
            }

            Pre preEvent = new Pre(body, systemOffset);
            MinecraftForge.EVENT_BUS.post(preEvent);
            if (!preEvent.isCanceled()) {
               GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z);
               GL11.glBegin(2);

               for (int i = 0; i < 90; i++) {
                  GL11.glVertex2f(
                     (x + orbit_offset_x) * (orbit_ext_x > 0.0F ? orbit_ext_x : 1.0F), (y + orbit_offset_y) * (orbit_ext_y > 0.0F ? orbit_ext_y : 1.0F)
                  );
                  float temp = x;
                  x = cos * x - sin * y;
                  y = sin * temp + cos * y;
               }

               GL11.glEnd();
               GL11.glTranslatef(-systemOffset.x, -systemOffset.y, -systemOffset.z);
               count++;
            }

            Post postEvent = new Post(body);
            MinecraftForge.EVENT_BUS.post(postEvent);
         }
      }

      GL11.glLineWidth(1.0F);
      if (this.selectedBody instanceof Star) {
         this.drawAgeCircle();
      }
   }

   public void drawButtons(int mousePosX, int mousePosY) {
      super.drawButtons(mousePosX, mousePosY);
      int LHS = BORDER_SIZE + BORDER_EDGE_SIZE;
      int RHS = this.width - LHS;
      int BOT = this.height - LHS;
      if (this.selectedBody != null) {
         this.mc.renderEngine.bindTexture(guiMain2);
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         int sliderPos = this.zoomTooltipPos;
         if (this.zoomTooltipPos != 125) {
            sliderPos = (int)Math.min(this.ticksSinceSelectionF * 8.0F, 125.0F);
            this.zoomTooltipPos = sliderPos;
         }

         int menuTopLeft = LHS + 100;
         int posX = RHS - sliderPos;
         this.drawTexturedModalRect(posX - 7, menuTopLeft + 13, 133, 120, 0, 79, 264, 182, true, false);
         this.drawTexturedModalRect(posX - 7, menuTopLeft + 153, 133, 17, 0, 472, 266, 40, true, false);
      }
   }

   private void drawAgeCircle() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glLineWidth(2.0F);
      float theta = 0.06981317F;
      float cos = (float)Math.cos(0.06981317F);
      float sin = (float)Math.sin(0.06981317F);
      CelestialBody body = this.selectedBody;
      new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f systemOffset = this.getCelestialBodyPosition((Star)body);
      float x = 1000.0F;
      float y = 0.0F;
      GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z);

      for (int count_rings = 1; count_rings < 5; count_rings++) {
         x = (float)(1000 * count_rings);
         GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.5F);
         GL11.glBegin(2);

         for (int i = 0; i < 90; i++) {
            GL11.glVertex2f(x * 1.0F, y * 1.0F);
            float temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
         }

         GL11.glEnd();
         GlStateManager.pushMatrix();
         GL11.glEnable(3553);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.fontRenderer.drawString((int)(x / 1000.0F) * 5 + " l.y.", (int)x, (int)y, 16777215);
         GL11.glDisable(3553);
         GlStateManager.popMatrix();
      }

      GL11.glTranslatef(-systemOffset.x, -systemOffset.y, -systemOffset.z);
      GL11.glLineWidth(1.0F);
   }

   private void setImgBackground(ResourceLocation galaxy) {
      GL11.glEnable(2929);
      GL11.glDepthMask(false);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3008);
      GL11.glEnable(3553);
      GL11.glTranslated(-40.0, -40.0, 0.0);
      GL11.glTranslated(this.xImgOffset, this.yImgOffset, 0.0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(galaxy);
      int width = Display.getWidth();
      int height = Display.getHeight();
      this.drawTexturedModalRect(0.0F, 0.0F, (float)width, (float)height, 0.0F, 0.0F, (float)(width * 2), (float)(height * 2), false, false, 1024.0F, 1024.0F);
      ResourceLocation guiImg_2 = new ResourceLocation("asmodeuscore", "textures/gui/galaxymap_nebula_" + this.nebula_img + ".png");
      this.mc.renderEngine.bindTexture(guiImg_2);
      GL11.glTranslated(this.xImgOffset * 1.5, this.yImgOffset * 1.5, 0.0);
      GL11.glColor4f(0.5F, 0.4F, this.nebula_color.floatZ(), 0.5F);
      this.drawTexturedModalRect(0.0F, 0.0F, (float)width, (float)height, 0.0F, 0.0F, 1024.0F, 1024.0F, false, false, 1024.0F, 1024.0F);
      GL11.glDepthMask(true);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDisable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   protected Vector3f getCelestialBodyPosition(CelestialBody cBody) {
      if (cBody == null) {
         return new Vector3f(0.0F, 0.0F, 0.0F);
      } else if (cBody instanceof Star) {
         return cBody.getUnlocalizedName().equalsIgnoreCase("star.sol") ? new Vector3f() : ((Star)cBody).getParentSolarSystem().getMapPosition().toVector3f();
      } else {
         float timeScale = cBody instanceof Planet ? 200.0F : 2.0F;
         float orbit_ext_x = cBody instanceof IExBody ? ((IExBody)cBody).getXOrbitEccentricity() : 1.0F;
         float orbit_ext_y = cBody instanceof IExBody ? ((IExBody)cBody).getYOrbitEccentricity() : 1.0F;
         float orbit_offset_x = cBody instanceof IExBody ? ((IExBody)cBody).getXOrbitOffset() : 0.0F;
         float orbit_offset_y = cBody instanceof IExBody ? ((IExBody)cBody).getYOrbitOffset() : 0.0F;
         float distanceFromCenter = this.getScale(cBody);
         float x = (float)Math.sin((double)(this.ticksTotalF / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift())) * distanceFromCenter;
         float y = (float)Math.cos((double)(this.ticksTotalF / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift())) * distanceFromCenter;
         Vector3f cBodyPos = new Vector3f(
            (x + orbit_offset_x) * (orbit_ext_x > 0.0F ? orbit_ext_x : 1.0F), (y + orbit_offset_y) * (orbit_ext_y > 0.0F ? orbit_ext_y : 1.0F), 0.0F
         );
         if (cBody instanceof Planet) {
            Vector3f parentVec = this.getCelestialBodyPosition(((Planet)cBody).getParentSolarSystem().getMainStar());
            return Vector3f.add(cBodyPos, parentVec, null);
         } else if (cBody instanceof IChildBody) {
            Vector3f parentVec = this.getCelestialBodyPosition(((IChildBody)cBody).getParentPlanet());
            return Vector3f.add(cBodyPos, parentVec, null);
         } else if (cBody instanceof ICelestialRegistry) {
            Vector3f parentVec = this.getCelestialBodyPosition(((ICelestialRegistry)cBody).getParentSolarSystem().getMainStar());
            return Vector3f.add(cBodyPos, parentVec, null);
         } else {
            return cBodyPos;
         }
      }
   }

   protected float getScale(CelestialBody celestialBody) {
      float scale = 0.2F;
      if (celestialBody instanceof Planet) {
         scale = 25.0F;
      }

      if (celestialBody instanceof ICelestialRegistry && !((ICelestialRegistry)celestialBody instanceof IChildBody)) {
         scale = 25.0F;
      }

      return 3.0F * celestialBody.getRelativeDistanceFromCenter().unScaledDistance * scale;
   }

   private void refreshBodies() {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      this.bodiesToRender.clear();
      this.galaxylist.clear();
      this.starlist.clear();

      for (SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values()) {
         if (!this.galaxylist.contains(solarSystem.getUnlocalizedParentGalaxyName())) {
            this.galaxylist.add(solarSystem.getUnlocalizedParentGalaxyName());
         }

         if (solarSystem.getUnlocalizedParentGalaxyName().equals(this.galaxy)) {
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
               if (UtilsGC.hasUnlocks(player, solarSystem)) {
                  if (solarSystem.getUnlocalizedParentGalaxyName().equals(this.galaxy)) {
                     this.starlist.add(solarSystem);
                  }

                  this.bodiesToRender.add(solarSystem.getMainStar());
               }
            } else {
               if (solarSystem.getUnlocalizedParentGalaxyName().equals(this.galaxy)) {
                  this.starlist.add(solarSystem);
               }

               this.bodiesToRender.add(solarSystem.getMainStar());
            }
         }
      }

      for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
         if (planet.getParentSolarSystem().getUnlocalizedParentGalaxyName().equals(this.galaxy)) {
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
               if (UtilsGC.isReasearched(player, planet)) {
                  this.bodiesToRender.add(planet);
               }
            } else {
               this.bodiesToRender.add(planet);
            }
         }
      }

      for (Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
         if (moon.getParentPlanet().getParentSolarSystem().getUnlocalizedParentGalaxyName().equals(this.galaxy)) {
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
               if (UtilsGC.isReasearched(player, moon.getParentPlanet()) && UtilsGC.isReasearched(player, moon)) {
                  this.bodiesToRender.add(moon);
               }
            } else {
               this.bodiesToRender.add(moon);
            }
         }
      }

      for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
         if (satellite.getParentPlanet().getParentSolarSystem().getUnlocalizedParentGalaxyName().equals(this.galaxy)) {
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
               if (UtilsGC.isReasearched(player, satellite.getParentPlanet())) {
                  this.bodiesToRender.add(satellite);
               }
            } else {
               this.bodiesToRender.add(satellite);
            }
         }
      }

      for (Map<String, ? extends CelestialBody> bodies : BodiesRegistry.getBodies()) {
         for (CelestialBody body : bodies.values()) {
            if (body instanceof ICelestialRegistry) {
               if (body instanceof IChildBody) {
                  if (((IChildBody)body).getParentPlanet().getParentSolarSystem().getUnlocalizedParentGalaxyName().equals(this.galaxy)
                     && ((ICelestialRegistry)body).canRegistry()) {
                     this.bodiesToRender.add(body);
                  }
               } else if (((ICelestialRegistry)body).getParentSolarSystem().getUnlocalizedParentGalaxyName().equals(this.galaxy)
                  && ((ICelestialRegistry)body).canRegistry()) {
                  this.bodiesToRender.add(body);
               }
            }
         }
      }

      Collections.sort(this.starlist, new Comparator<SolarSystem>() {
         public int compare(SolarSystem lhs, SolarSystem rhs) {
            return lhs.getName().compareTo(rhs.getName());
         }
      });
   }

   protected List<CelestialBody> getChildren(Object object) {
      return this.getChildren(object, 0, 0, false);
   }

   protected List<CelestialBody> getChildren(Object object, int start, int size) {
      return this.getChildren(object, start, size, false);
   }

   protected List<CelestialBody> getChildren(Object object, int start, int size, boolean check) {
      List<CelestialBody> bodyList = Lists.newArrayList();
      SpaceData.refreshGalaxies();
      if (object instanceof Planet) {
         List<Moon> moons = GalaxyRegistry.getMoonsForPlanet((Planet)object);
         if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
            for (Moon moon : moons) {
               if (UtilsGC.isReasearched(this.mc.player, moon)) {
                  bodyList.add(moon);
               }
            }
         } else {
            bodyList.addAll(moons);
         }

         for (CelestialBody body : SpaceData.getMoonsForPlanet((Planet)object)) {
            if (body instanceof ICelestialRegistry && body instanceof IChildBody) {
               bodyList.add(body);
            }
         }
      } else if (object instanceof SolarSystem) {
         List<Planet> planets = GalaxyRegistry.getPlanetsForSolarSystem((SolarSystem)object);
         if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
            for (Planet planet : planets) {
               if (UtilsGC.isReasearched(this.mc.player, planet)) {
                  bodyList.add(planet);
               }
            }
         } else {
            bodyList.addAll(planets);
         }

         for (CelestialBody bodyx : SpaceData.getBodiesForSolarSystem((SolarSystem)object)) {
            if (bodyx instanceof ICelestialRegistry && !(bodyx instanceof IChildBody)) {
               bodyList.add(bodyx);
            }
         }
      }

      Collections.sort(bodyList);
      if (!check) {
         return bodyList;
      } else {
         List<CelestialBody> doneList = Lists.newArrayList();
         int startPos = start;
         int getSize = size;
         if (start >= bodyList.size()) {
            startPos = 0;
         }

         if (size >= bodyList.size()) {
            getSize = bodyList.size();
         }

         for (int i = 0; i < getSize; i++) {
            doneList.add(i, bodyList.get(i + startPos));
         }

         return doneList;
      }
   }
}
