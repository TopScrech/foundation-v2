package asmodeuscore.core.astronomy.gui.screen;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.api.space.ICelestialRegistry;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.BodiesData;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.SpaceData;
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.network.packet.ACPacketSimple;
import asmodeuscore.core.proxy.ClientProxy;
import asmodeuscore.core.utils.ACCompatibilityManager;
import asmodeuscore.core.utils.Utils;
import asmodeuscore.core.utils.UtilsGC;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.Post;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.Pre;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.api.world.SpaceStationType;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection.StationDataGUI;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class NewGuiCelestialSelection extends GuiCelestialSelection {
   protected NewGuiCelestialSelection.EnumView viewState = NewGuiCelestialSelection.EnumView.GS;
   private String galaxy = GalacticraftCore.planetOverworld.getParentSolarSystem().getUnlocalizedParentGalaxyName();
   private List<String> galaxylist = new ArrayList<>();
   private List<SolarSystem> starlist = new ArrayList<>();
   private boolean showStarList = false;
   private boolean showGalaxyList = false;
   private boolean enableNewTierSystem;
   private double isometx;
   private double isometz;
   private double mindistance;
   private int currenttier;
   private int tierneed;
   private double timer;
   private double traveltime;
   private boolean enableTraveltime;
   private int coef;
   private boolean canTravel;
   private int small_page;
   private SpaceData.Engine_Type engine;
   public SpaceData data;
   private int fuelSet;
   private int fuelRocketLevel;
   protected static final int LIGHTBLUE = ColorUtil.to32BitColor(255, 0, 255, 255);
   protected static final int YELLOW = ColorUtil.to32BitColor(255, 255, 255, 0);
   public static ResourceLocation guiMain2 = new ResourceLocation("asmodeuscore", "textures/gui/celestialselection2.png");
   public static ResourceLocation guiImg = new ResourceLocation("asmodeuscore", "textures/gui/galaxymap_1.png");
   public static ResourceLocation vortexTexture = new ResourceLocation("asmodeuscore", "textures/gui/celestialbodies/vortex.png");
   private double xImgOffset;
   private double yImgOffset;
   private int scroll;
   private int max_scroll;
   private Vec3d nebula_color;
   private int nebula_img;
   private boolean isSecondPassenger;
   private Map<CelestialBody, Integer> celestialBodyTiers;

   public NewGuiCelestialSelection(boolean mapMode, List<CelestialBody> possibleBodies, boolean canCreateStations, SpaceData data) {
      super(mapMode, possibleBodies, canCreateStations);
      this.enableNewTierSystem = AsmodeusConfig.enableNewTierSystem;
      this.isometx = AsmodeusConfig.enable2DGalaxyMap ? 0.0 : 55.0;
      this.isometz = 45.0;
      this.mindistance = 900.0;
      this.currenttier = 0;
      this.tierneed = -1;
      this.enableTraveltime = false;
      this.canTravel = true;
      this.small_page = 0;
      this.engine = SpaceData.Engine_Type.FUEL_ENGINE;
      this.fuelSet = 0;
      this.fuelRocketLevel = 0;
      this.xImgOffset = 0.0;
      this.yImgOffset = 0.0;
      this.scroll = 0;
      this.max_scroll = 8;
      this.nebula_color = new Vec3d(1.0, 1.0, 1.0);
      this.nebula_img = 0;
      this.celestialBodyTiers = new HashMap<>();
      this.data = data;
      int tier = 0;
      if (this.data != null) {
         tier = (Integer)this.data.getValue("rocket_tier");
         this.engine = (SpaceData.Engine_Type)this.data.getValue("rocket_engine");
         this.fuelRocketLevel = (Integer)this.data.getValue("rocket_count_fuel");
         this.isSecondPassenger = (Boolean)this.data.getValue("second_passenger");
      }

      for (String config_body : AsmodeusConfig.celestialBodyTiers) {
         String[] split_config = config_body.split(":");
         CelestialBody body_config = GalaxyRegistry.getCelestialBodyFromUnlocalizedName(split_config[0]);
         Integer tier_config = Integer.parseInt(split_config[1]);
         this.celestialBodyTiers.put(body_config, tier_config);
      }

      this.currenttier = tier;
      this.coef = AsmodeusConfig.speedTimeTravel - 1 + tier;
      if (this.engine != null) {
         switch (this.engine) {
            case FUEL_ENGINE:
            default:
               break;
            case ION_ENGINE:
               this.coef += 4;
               break;
            case PLASMA_ENGINE:
               this.coef += 8;
               break;
            case SUBLIGHT_ENGINE:
               this.coef += 100;
               break;
            case BLACKHOLE_ENGINE:
               this.coef += 1000;
         }
      }

      Random rand = new Random();
      this.nebula_img = rand.nextInt(2);
      this.nebula_color = new Vec3d((double)rand.nextFloat(), (double)rand.nextFloat(), (double)rand.nextFloat());
   }

   public void initGui() {
      GuiCelestialSelection.BORDER_SIZE = this.width / 65;
      GuiCelestialSelection.BORDER_EDGE_SIZE = GuiCelestialSelection.BORDER_SIZE / 4;
      this.refreshBodies();
   }

   public void updateScreen() {
      this.ticksSinceMenuOpen++;
      this.ticksTotal++;
      if (this.ticksSinceMenuOpen < 20) {
         Mouse.setGrabbed(false);
      }

      if (this.selectedBody != null) {
         this.ticksSinceSelection++;
      }

      if (this.selectedBody == null && this.ticksSinceUnselection >= 0) {
         this.ticksSinceUnselection++;
      }

      if (!this.renamingSpaceStation && (this.selectedBody == null || !this.isZoomed())) {
         if (GameSettings.isKeyDown(KeyHandlerClient.leftKey)) {
            this.translation.x += -2.0F;
            this.translation.y += -2.0F;
            if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
               this.xImgOffset += -1.0;
            }
         }

         if (GameSettings.isKeyDown(KeyHandlerClient.rightKey)) {
            this.translation.x += 2.0F;
            this.translation.y += 2.0F;
            if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
               this.xImgOffset++;
            }
         }

         if (GameSettings.isKeyDown(KeyHandlerClient.upKey)) {
            this.translation.x += 2.0F;
            this.translation.y += -2.0F;
            if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
               this.yImgOffset += -1.0;
            }
         }

         if (GameSettings.isKeyDown(KeyHandlerClient.downKey)) {
            this.translation.x += -2.0F;
            this.translation.y += 2.0F;
            if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
               this.yImgOffset++;
            }
         }
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

   public void drawButtons(int mousePosX, int mousePosY) {
      int travel_modifier = 200;
      if (this.coef < 1) {
         this.coef = 1;
      }

      if (!this.mapMode
         && this.ticksSinceSelection % 1 == 0
         && !this.mc.player.capabilities.isCreativeMode
         && this.timer < this.traveltime
         && this.enableTraveltime) {
         this.timer = this.timer + (double)(1 * this.coef);
      }

      if (this.selectedBody != null) {
         double dist = 0.0;
         float idcel = 0.0F;
         SolarSystem star = GalacticraftCore.solarSystemSol;
         this.canTravel = true;
         if (this.selectedBody instanceof Planet) {
            if (!(this.mc.player.world.provider instanceof WorldProviderSurface) && this.mc.player.world.provider instanceof IGalacticraftWorldProvider) {
               CelestialBody body = ((IGalacticraftWorldProvider)this.mc.player.world.provider).getCelestialBody();
               if (body instanceof Planet) {
                  star = ((Planet)body).getParentSolarSystem();
                  idcel = body.getRelativeDistanceFromCenter().unScaledDistance;
                  dist = Math.abs(Math.floor((double)((idcel - this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance) * 100.0F))) / 100.0;
               }

               if (body instanceof IChildBody) {
                  star = ((IChildBody)body).getParentPlanet().getParentSolarSystem();
                  idcel = ((IChildBody)body).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
                  dist = Math.abs(Math.floor((double)((idcel - this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance) * 100.0F))) / 100.0
                     + (double)(body.getRelativeDistanceFromCenter().unScaledDistance / 100.0F);
               }

               if (this.selectedBody == body) {
                  dist = 0.0;
               }
            } else {
               idcel = GalacticraftCore.planetOverworld.getRelativeDistanceFromCenter().unScaledDistance;
               star = GalacticraftCore.solarSystemSol;
               dist = Math.abs(Math.floor((double)((idcel - this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance) * 100.0F))) / 100.0;
            }

            SolarSystem test = ((Planet)this.selectedBody).getParentSolarSystem();
            double distothersystem = Math.floor(Vector3.distance(star.getMapPosition(), test.getMapPosition()) / 100.0) * 100.0
               - Math.abs(Math.floor((double)((idcel + this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance) * 100.0F)));
            double time = star == test ? (double)Math.round(dist * 70.0) : distothersystem;
            if (!test.equals(star)) {
               time *= 10.0;
               if (this.engine.getID() < SpaceData.Engine_Type.SUBLIGHT_ENGINE.getID()
                  && ACCompatibilityManager.isGalaxySpaceLoaded()
                  && AsmodeusConfig.enableSublightEngine) {
                  this.canTravel = false;
               }
            }

            if (!star.getUnlocalizedParentGalaxyName().equals(test.getUnlocalizedParentGalaxyName())) {
               time *= 10.0;
               if (this.engine.getID() < SpaceData.Engine_Type.SUBLIGHT_ENGINE.getID()
                  && ACCompatibilityManager.isGalaxySpaceLoaded()
                  && AsmodeusConfig.enableSublightEngine) {
                  this.canTravel = false;
               }
            }

            this.traveltime = (double)((time <= 0.0 ? 10 : (int)time) * travel_modifier);

            for (int i = 0; i <= BodiesRegistry.getMaxTier(); i++) {
               if (dist >= 0.2 * (double)i && this.selectedBody.getReachable()) {
                  if (star.getUnlocalizedParentGalaxyName().equals(test.getUnlocalizedParentGalaxyName())) {
                     if (star == test) {
                        this.tierneed = i == BodiesRegistry.getMaxTier() ? i : i + 1;
                     } else {
                        this.tierneed = BodiesRegistry.getMaxTier();
                     }
                  } else {
                     this.tierneed = BodiesRegistry.getMaxTier();
                  }
               }
            }

            int fuelcount = (int)Math.round((double)this.fuelRocketLevel - (double)this.fuelRocketLevel * (dist / (double)this.currenttier));
            this.fuelSet = fuelcount;
         }

         if (this.selectedBody instanceof IChildBody) {
            if (!(this.mc.player.world.provider instanceof WorldProviderSurface) && this.mc.player.world.provider instanceof IGalacticraftWorldProvider) {
               CelestialBody bodyx = ((IGalacticraftWorldProvider)this.mc.player.world.provider).getCelestialBody();
               idcel = 0.0F;
               if (bodyx instanceof Planet) {
                  star = ((Planet)bodyx).getParentSolarSystem();
                  idcel = bodyx.getRelativeDistanceFromCenter().unScaledDistance;
                  dist = Math.abs(
                           Math.floor(
                              (double)((idcel - ((IChildBody)this.selectedBody).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance) * 100.0F)
                           )
                        )
                        / 100.0
                     + (double)(this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance / 100.0F);
               } else if (bodyx instanceof IChildBody) {
                  star = ((IChildBody)bodyx).getParentPlanet().getParentSolarSystem();
                  idcel = ((IChildBody)bodyx).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
                  dist = Math.abs(
                           Math.floor(
                              (double)((idcel - ((IChildBody)this.selectedBody).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance) * 100.0F)
                           )
                        )
                        / 100.0
                     + (double)(this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance / 100.0F);
               }

               if (this.selectedBody == bodyx) {
                  dist = 0.0;
               }
            } else {
               idcel = GalacticraftCore.planetOverworld.getRelativeDistanceFromCenter().unScaledDistance;
               star = GalacticraftCore.solarSystemSol;
               dist = Math.abs(
                        Math.floor(
                           (double)((idcel - ((IChildBody)this.selectedBody).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance) * 100.0F)
                        )
                     )
                     / 100.0
                  + (double)(this.selectedBody.getRelativeDistanceFromCenter().unScaledDistance / 100.0F);
            }

            SolarSystem testx = ((IChildBody)this.selectedBody).getParentPlanet().getParentSolarSystem();
            double timex = star == testx
               ? (double)Math.round(dist * 70.0)
               : Math.floor(Vector3.distance(star.getMapPosition(), testx.getMapPosition()) / 200.0)
                  * Math.floor((double)((((IChildBody)this.selectedBody).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance + idcel) * 100.0F));
            if (!testx.equals(star)) {
               timex *= 10.0;
               if (this.engine.getID() < SpaceData.Engine_Type.SUBLIGHT_ENGINE.getID()
                  && ACCompatibilityManager.isGalaxySpaceLoaded()
                  && AsmodeusConfig.enableSublightEngine) {
                  this.canTravel = false;
               }
            }

            if (!star.getUnlocalizedParentGalaxyName().equals(testx.getUnlocalizedParentGalaxyName())) {
               timex *= 1000.0;
               if (this.engine.getID() < SpaceData.Engine_Type.SUBLIGHT_ENGINE.getID()
                  && ACCompatibilityManager.isGalaxySpaceLoaded()
                  && AsmodeusConfig.enableSublightEngine) {
                  this.canTravel = false;
               }
            }

            this.traveltime = (double)((timex <= 0.0 ? 10 : (int)timex) * travel_modifier);

            for (int ix = 0; ix <= BodiesRegistry.getMaxTier(); ix++) {
               if (dist >= 0.25 * (double)ix && this.selectedBody.getReachable()) {
                  if (star.getUnlocalizedParentGalaxyName().equals(testx.getUnlocalizedParentGalaxyName())) {
                     if (star == testx) {
                        this.tierneed = ix == BodiesRegistry.getMaxTier() ? ix : ix + 1;
                     } else {
                        this.tierneed = BodiesRegistry.getMaxTier();
                     }
                  } else {
                     this.tierneed = BodiesRegistry.getMaxTier();
                  }
               }
            }

            int fuelcount = (int)Math.round((double)this.fuelRocketLevel - (double)this.fuelRocketLevel * (dist / (double)this.currenttier));
            this.fuelSet = fuelcount;
         }

         if (this.fuelSet < 0) {
            this.canTravel = false;
         }

         if (this.mc.player.capabilities.isCreativeMode) {
            this.canTravel = true;
         }

         if (this.celestialBodyTiers.containsKey(this.selectedBody)) {
            this.tierneed = this.celestialBodyTiers.get(this.selectedBody);
         }
      }

      this.zLevel = 0.0F;
      boolean handledSliderPos = false;
      int LHS = GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE;
      int RHS = this.width - LHS;
      int TOP = LHS;
      int BOT = this.height - LHS;
      if (this.viewState == NewGuiCelestialSelection.EnumView.GS) {
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         this.drawTexturedModalRect(LHS, LHS, 74, 11, 0, 392, 148, 22, false, false);
         String str = GCCoreUtil.translate("gui.message.catalog.name").toUpperCase();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.fontRenderer.drawString(str, LHS + 40 - this.fontRenderer.getStringWidth(str) / 2, LHS + 1, WHITE);
         int scale = (int)Math.min(95.0F, this.ticksSinceMenuOpenF * 12.0F);
         boolean planetZoomedNotMoon = this.isZoomed() && !(this.selectedParent instanceof Planet);
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         this.drawTexturedModalRect(LHS - 95 + scale, LHS + 12, 95, 41, 0, 436, 95, 41, false, false);
         GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
         if (GalaxyRegistry.getRegisteredSolarSystems().size() > 1) {
            this.drawTexturedModalRect(LHS + 2 - 13 + scale, LHS + 37, 5, 5, 110, 448, 5, 5, false, true);
         }

         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         str = planetZoomedNotMoon ? this.selectedBody.getLocalizedName() : this.getParentName();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.fontRenderer.drawString(str, LHS + 9 - 95 + scale, LHS + 34, YELLOW);
         GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         this.drawTexturedModalRect(LHS + 2 - 95 + scale, LHS + 14, 93, 17, 95, 436, 93, 17, false, false);
         if (this.galaxylist.size() > 1) {
            this.drawTexturedModalRect(LHS + 2 - 13 + scale, LHS + 19, 5, 5, 110, 448, 5, 5, false, true);
         }

         str = planetZoomedNotMoon ? this.getParentName() : this.getGrandparentName();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.fontRenderer.drawString(str, LHS + 7 - 95 + scale, LHS + 16, LIGHTBLUE);
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         if (this.showGalaxyList) {
            int y = 19;

            for (int ixx = 0; ixx < this.galaxylist.size(); ixx++) {
               ResourceLocation galaxy;
               if (BodiesRegistry.getGalaxy(this.galaxylist.get(ixx).replace("galaxy.", "")) != null
                  && BodiesRegistry.getGalaxy(this.galaxylist.get(ixx).replace("galaxy.", "")).getImage() != null) {
                  galaxy = BodiesRegistry.getGalaxy(this.galaxylist.get(ixx).replace("galaxy.", "")).getImage();
               } else {
                  galaxy = new ResourceLocation("asmodeuscore:textures/gui/galaxy/" + this.galaxylist.get(ixx).toLowerCase() + ".png");
               }

               if (galaxy != null) {
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  this.mc.renderEngine.bindTexture(galaxy);
                  this.drawTexturedModalRect(
                     (float)(LHS + 18 + scale + 70 * ixx), (float)(TOP + 4), 37.0F, 40.0F, 0.0F, 0.0F, 100.0F, 100.0F, false, false, 100.0F, 100.0F
                  );
               }

               this.mc.renderEngine.bindTexture(guiMain2);
               GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               this.drawTexturedModalRect(LHS + 15 + scale + 70 * ixx, TOP + 4, 43, 40, 270, 80, 85, 75, false, false);
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
               GL11.glColor4f(0.0F, 0.6F, 1.0F, (float)scale / 95.0F);
               this.drawTexturedModalRect(LHS + 5 + scale + 70 * ixx, TOP + 14 + 35, 43, 12, 95, 464, 43, 12, false, false);
               this.drawTexturedModalRect(LHS + 5 + scale + 43 + 70 * ixx, TOP + 14 + 35, 23, 12, 165, 464, 23, 12, false, false);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               str = GCCoreUtil.translate(this.galaxylist.get(ixx));
               this.fontRenderer.drawString(str, LHS + 10 + scale + 70 * ixx, TOP + 14 + 37, YELLOW);
            }
         }

         if (this.showStarList) {
            int l = 0;
            int posX = 0;

            for (SolarSystem s : this.starlist) {
               int y = 33;
               this.mc.renderEngine.bindTexture(guiMain2);
               GL11.glColor4f(0.0F, 0.6F, 1.0F, (float)scale / 95.0F);
               this.drawTexturedModalRect(LHS + posX * 150 + 3 + scale, TOP + 30 * l + 8, 140, 30, 275, 160, 140, 30, false, false);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               ResourceLocation starx = s.getMainStar().getBodyIcon();
               this.mc.renderEngine.bindTexture(starx);
               this.drawTexturedModalRect(
                  (float)(LHS + posX * 150 + 14 + scale), (float)(TOP + 30 * l + 15), 16.0F, 16.0F, 0.0F, 0.0F, 16.0F, 16.0F, false, false, 16.0F, 16.0F
               );
               str = s.getLocalizedName();
               this.fontRenderer.drawString(str, LHS + posX * 150 + 43 + scale, TOP + 30 * l + 19, YELLOW);
               if (++l % 7 == 0) {
                  posX++;
                  l = 0;
               }
            }
         }

         List<CelestialBody> children = this.getChildren(planetZoomedNotMoon ? this.selectedBody : this.selectedParent, this.scroll, this.max_scroll);
         this.drawChildren(children, 0, 0, true);
         if (this.mapMode) {
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            this.drawTexturedModalRect(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false);
            str = GCCoreUtil.translate("gui.message.exit.name").toUpperCase();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.fontRenderer.drawString(str, RHS - 40 - this.fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE);
         }

         int menuTopLeft1 = BOT - 22;
         int posX1 = RHS - 138;
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         this.drawTexturedModalRect(posX1 + 98, menuTopLeft1 - 22, 40, 12, 0, 456, 40, 12, true, false);
         this.drawTexturedModalRect(posX1 + 53, menuTopLeft1 - 22, 45, 12, 50, 456, 45, 12, true, false);
         str = ClientProxy.smallInfoOnMap ? "Small Mode" : "Normal Mode";
         GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
         this.fontRenderer.drawString(str, posX1 + 61, menuTopLeft1 - 20, 16777215);
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         this.drawTexturedModalRect(posX1 + 98, menuTopLeft1 - 8, 40, 12, 0, 456, 40, 12, true, false);
         this.drawTexturedModalRect(posX1 + 53, menuTopLeft1 - 8, 45, 12, 50, 456, 45, 12, true, false);
         str = ClientProxy.enableInfoOnMap ? "Disable Info" : "Enable Info";
         GL11.glColor4f(ClientProxy.enableInfoOnMap ? 1.0F : 0.0F, ClientProxy.enableInfoOnMap ? 0.0F : 1.0F, 0.0F, 1.0F);
         this.fontRenderer.drawString(str, posX1 + 61, menuTopLeft1 - 6, 16777215);
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         this.drawTexturedModalRect(posX1 + 98, menuTopLeft1 + 6, 40, 12, 0, 456, 40, 12, true, false);
         this.drawTexturedModalRect(posX1 + 53, menuTopLeft1 + 6, 45, 12, 50, 456, 45, 12, true, false);
         str = !ClientProxy.renderRingZones ? "Enable Zones" : "Disable Zones";
         GL11.glColor4f(ClientProxy.renderRingZones ? 1.0F : 0.0F, ClientProxy.renderRingZones ? 0.0F : 1.0F, 0.0F, 1.0F);
         this.fontRenderer.drawString(str, posX1 + 61, menuTopLeft1 + 8, 16777215);
         if (this.selectedBody != null) {
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
            GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
            if (this.selectedBody instanceof Satellite) {
               Satellite selectedSatellite = (Satellite)this.selectedBody;
               int stationListSize = ((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite))).size();
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
               int max = Math.min(this.height / 2 / 14, stationListSize);
               this.drawTexturedModalRect(RHS - 95, TOP, 95, 53, this.selectedStationOwner.length() == 0 ? 95 : 0, 186, 95, 53, false, false);
               if (this.spaceStationListOffset <= 0) {
                  GL11.glColor4f(0.65F, 0.65F, 0.65F, 1.0F);
               } else {
                  GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               }

               this.drawTexturedModalRect(RHS - 85, TOP + 45, 61, 4, 0, 239, 61, 4, false, false);
               if (max + this.spaceStationListOffset >= stationListSize) {
                  GL11.glColor4f(0.65F, 0.65F, 0.65F, 1.0F);
               } else {
                  GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               }

               this.drawTexturedModalRect(RHS - 85, TOP + 49 + max * 14, 61, 4, 0, 239, 61, 4, false, true);
               GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               if (((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite))).get(this.selectedStationOwner) == null) {
                  str = GCCoreUtil.translate("gui.message.select_ss.name");
                  this.drawSplitString(str, RHS - 47, TOP + 20, 91, WHITE, false, false);
               } else {
                  str = GCCoreUtil.translate("gui.message.ss_owner.name");
                  this.fontRenderer.drawString(str, RHS - 85, TOP + 18, WHITE);
                  str = this.selectedStationOwner;
                  this.fontRenderer.drawString(str, RHS - 47 - this.fontRenderer.getStringWidth(str) / 2, TOP + 30, WHITE);
               }

               Iterator<Entry<String, StationDataGUI>> it = ((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite))).entrySet().iterator();
               int ixx = 0;

               for (int j = 0; it.hasNext() && ixx < max; j++) {
                  Entry<String, StationDataGUI> e = it.next();
                  if (j >= this.spaceStationListOffset) {
                     this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                     GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
                     int xOffset = 0;
                     if (e.getKey().equalsIgnoreCase(this.selectedStationOwner)) {
                        xOffset -= 5;
                     }

                     this.drawTexturedModalRect(RHS - 95 + xOffset, TOP + 50 + ixx * 14, 93, 12, 95, 464, 93, 12, true, false);
                     str = "";
                     String str0 = e.getValue().getStationName();

                     for (int point = 0; this.fontRenderer.getStringWidth(str) < 80 && point < str0.length(); point++) {
                        str = str + str0.substring(point, point + 1);
                     }

                     if (this.fontRenderer.getStringWidth(str) >= 80) {
                        str = str.substring(0, str.length() - 3);
                        str = str + "...";
                     }

                     this.fontRenderer.drawString(str, RHS - 88 + xOffset, TOP + 52 + ixx * 14, WHITE);
                     ixx++;
                  }
               }
            } else if (ClientProxy.enableInfoOnMap) {
               this.mc.renderEngine.bindTexture(guiMain2);
               GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               int y = 100;
               int sliderPos = this.zoomTooltipPos;
               if (this.zoomTooltipPos != 125) {
                  sliderPos = (int)Math.min(this.ticksSinceSelectionF * 8.0F, 125.0F);
                  this.zoomTooltipPos = sliderPos;
               }

               int menuTopLeft = LHS + y;
               int posX = RHS - sliderPos;
               int posX2 = RHS - 18;
               if (!this.selectedBody.getReachable()) {
                  if (ClientProxy.smallInfoOnMap) {
                     menuTopLeft = this.height - BORDER_EDGE_SIZE - 250;
                  }

                  this.drawTexturedModalRect(posX - 7, menuTopLeft + 12, 133, 31, 0, 79, 266, 62, true, false);
                  this.drawTexturedModalRect(posX - 7, menuTopLeft + 43, 133, 37, 0, 442, 266, 70, true, false);
               } else if (!ClientProxy.smallInfoOnMap) {
                  this.drawTexturedModalRect(posX - 7, menuTopLeft + 13, 133, 237, 0, 79, 266, 433, true, false);
               } else {
                  menuTopLeft = this.height - BORDER_EDGE_SIZE - 250;
                  this.drawTexturedModalRect(posX - 7, menuTopLeft + 13, 133, 80, 0, 79, 266, 142, true, false);
                  this.drawTexturedModalRect(posX - 7, menuTopLeft + 93, 133, 17, 0, 472, 266, 40, true, false);
                  this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                  this.drawTexturedModalRect(posX + 5, menuTopLeft + 112, 30, 11, 0, 414, 60, 22, false, false);
                  this.drawTexturedModalRect(posX + 35, menuTopLeft + 112, 50, 11, 128, 414, 40, 22, false, false);
                  this.drawTexturedModalRect(posX + 85, menuTopLeft + 112, 30, 11, 128, 414, 60, 22, false, false);
                  this.fontRenderer.drawString("<-", posX + 13, menuTopLeft + 114, CYAN);
                  this.fontRenderer.drawString("Page: " + (this.small_page + 1), posX + 45, menuTopLeft + 114, CYAN);
                  this.fontRenderer.drawString("->", posX + 100, menuTopLeft + 114, CYAN);
                  this.mc.renderEngine.bindTexture(guiMain2);
               }

               y += 15;
               str = GCCoreUtil.translate("gui.message.generalinformation");
               if (!ClientProxy.smallInfoOnMap) {
                  this.fontRenderer.drawString(str, posX, TOP + y, CYAN);
               } else if (this.small_page == 0 || this.selectedBody instanceof Star) {
                  this.fontRenderer.drawString(str, posX, BOT - 225, CYAN);
               }

               if (this.selectedBody.getReachable()) {
                  str = GCCoreUtil.translate("gui.message.physicalparameters");
                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 40, CYAN);
                  } else if (this.small_page == 0) {
                     this.fontRenderer.drawString(str, posX, BOT - 185, CYAN);
                  }

                  str = GCCoreUtil.translate("gui.message.atmosphericparameters");
                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 82, CYAN);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225, CYAN);
                  }

                  str = GCCoreUtil.translate("gui.message.atmosphericcomponents");
                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 166, CYAN);
                  } else if (this.small_page == 2) {
                     this.fontRenderer.drawString(str, posX, BOT - 225, CYAN);
                  }
               }

               BodiesData data = BodiesRegistry.getData(this.selectedBody);
               if (this.selectedBody instanceof Star) {
                  str = GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.star.name");
               }

               if (this.selectedBody instanceof Planet) {
                  str = GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.planet.name");
               }

               if (this.selectedBody instanceof Moon) {
                  str = GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.moon.name");
               }

               if (this.selectedBody instanceof Satellite) {
                  str = GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.satellite.name");
               }

               if (this.selectedBody.getTierRequirement() == -2) {
                  str = GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.blackhole.name");
               }

               if (data != null && data.getType() != null) {
                  str = GCCoreUtil.translate("gui.message.type") + " " + BodiesRegistry.getLocale(data.getType());
               }

               if (!ClientProxy.smallInfoOnMap) {
                  this.fontRenderer.drawString(str, posX, TOP + y + 10, WHITE);
               } else if (this.small_page == 0 || this.selectedBody instanceof Star) {
                  this.fontRenderer.drawString(str, posX, BOT - 225 + 10, WHITE);
               }

               if (this.selectedBody instanceof Planet) {
                  this.galaxy = ((Planet)this.selectedBody).getParentSolarSystem().getUnlocalizedParentGalaxyName();
               } else if (this.selectedBody instanceof IChildBody) {
                  this.galaxy = ((IChildBody)this.selectedBody).getParentPlanet().getParentSolarSystem().getUnlocalizedParentGalaxyName();
               }

               WorldProvider dim = WorldUtil.getProviderForDimensionClient(this.selectedBody.getDimensionID());
               str = GCCoreUtil.translate("gui.message.class") + " " + BodiesRegistry.classPlanet(this.selectedBody, dim);
               if (!ClientProxy.smallInfoOnMap) {
                  this.fontRenderer.drawString(str, posX, TOP + y + 20, WHITE);
               } else if (this.small_page == 0 || this.selectedBody instanceof Star) {
                  this.fontRenderer.drawString(str, posX, BOT - 225 + 20, WHITE);
               }

               if (this.selectedBody.getReachable()) {
                  str = GCCoreUtil.translate("gui.message.hasdungeons") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody.getReachable() && this.selectedBody != GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.hasdungeons")
                        + " "
                        + Utils.localeBoolean(((IGalacticraftWorldProvider)dim).getDungeonSpacing() > 0);
                  }

                  if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.hasdungeons") + " " + Utils.localeBoolean(true);
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 29, WHITE);
                  } else if (this.small_page == 0) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 30, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.gravity") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody.getReachable() && this.selectedBody != GalacticraftCore.planetOverworld) {
                     float grav = (0.1F - ((IGalacticraftWorldProvider)dim).getGravity()) * 1000.0F;
                     if (((IGalacticraftWorldProvider)dim).getGravity() == 0.0F) {
                        grav = 0.0F;
                     }

                     str = GCCoreUtil.translate("gui.message.gravity") + " " + (int)grav + "%";
                  } else if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.gravity") + " 100%";
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 38 + 12, WHITE);
                  } else if (this.small_page == 0) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 38 + 12, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.daylength") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody.getReachable() && this.selectedBody != GalacticraftCore.planetOverworld) {
                     if (dim instanceof WorldProviderSpace) {
                        str = GCCoreUtil.translate("gui.message.daylength")
                           + " "
                           + ((WorldProviderSpace)dim).getDayLength() / 1000L
                           + "h "
                           + ((WorldProviderSpace)dim).getDayLength() % 100L
                           + "m";
                     } else if (dim instanceof WE_WorldProviderSpace) {
                        str = GCCoreUtil.translate("gui.message.daylength")
                           + " "
                           + ((WE_WorldProviderSpace)dim).getDayLength() / 1000L
                           + "h "
                           + ((WE_WorldProviderSpace)dim).getDayLength() % 100L
                           + "m";
                     } else if (this.selectedBody == GalacticraftCore.planetOverworld) {
                        str = GCCoreUtil.translate("gui.message.daylength") + " 24h 0m";
                     }
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 38 + 22, WHITE);
                  } else if (this.small_page == 0) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 38 + 22, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.atmopressure") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody instanceof IExBody) {
                     str = GCCoreUtil.translate("gui.message.atmopressure") + " " + ((IExBody)this.selectedBody).getAtmosphericPressure() + " P";
                  } else if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.atmopressure") + " P 1";
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 78 + 14, WHITE);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 10, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.temperature") + " " + GCCoreUtil.translate("gui.message.unknown");
                  float temp = 0.0F;

                  try {
                     temp = ((IGalacticraftWorldProvider)dim).getCelestialBody().atmosphere.thermalLevel();
                  } catch (Exception var40) {
                  }

                  temp = temp == 0.0F ? (float)BodiesRegistry.getDefaultDergrees : temp * (float)BodiesRegistry.getDefaultDergrees;
                  if (this.selectedBody.getReachable()) {
                     str = GCCoreUtil.translate("gui.message.temperature") + " " + temp + " C";
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 78 + 24, WHITE);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 20, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.windenergy") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody.getReachable() && this.selectedBody != GalacticraftCore.planetOverworld && dim instanceof IAdvancedSpace) {
                     str = GCCoreUtil.translate("gui.message.windenergy")
                        + " "
                        + (float)Math.round(((IAdvancedSpace)dim).getSolarWindMultiplier() * 1000.0) / 1000.0F;
                  } else if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.windenergy") + " " + 1.0;
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 76 + 36, WHITE);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 30, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.solarenergy") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (dim instanceof ISolarLevel) {
                     float boost = (float)Math.round((((ISolarLevel)dim).getSolarEnergyMultiplier() - 1.0) * 1000.0) / 10.0F;
                     str = GCCoreUtil.translate("gui.message.solarenergy") + " " + boost + "%";
                  }

                  if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.solarenergy") + " 0%";
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 76 + 48, WHITE);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 40, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.windspeed") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (dim instanceof IGalacticraftWorldProvider) {
                     str = GCCoreUtil.translate("gui.message.windspeed")
                        + " "
                        + ((IGalacticraftWorldProvider)dim).getCelestialBody().atmosphere.windLevel()
                        + " W/l";
                  }

                  if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.windspeed") + " 1.0 W/l";
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 74 + 60, WHITE);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 50, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.solarradiation") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody instanceof IExBody) {
                     str = GCCoreUtil.translate("gui.message.solarradiation") + " " + ((IExBody)this.selectedBody).isSolarRadiation();
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 78 + 66, WHITE);
                  } else if (this.small_page == 1) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 60, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.breathableatmo") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody.getReachable() && this.selectedBody != GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.breathableatmo") + " " + Utils.localeBoolean(this.selectedBody.atmosphere.isBreathable());
                  }

                  if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.breathableatmo") + " " + Utils.localeBoolean(true);
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 77 + 99, WHITE);
                  } else if (this.small_page == 2) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 10, WHITE);
                  }

                  str = GCCoreUtil.translate("gui.message.corrosiveatmo") + " " + GCCoreUtil.translate("gui.message.unknown");
                  if (this.selectedBody.getReachable() && this.selectedBody != GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.corrosiveatmo") + " " + Utils.localeBoolean(this.selectedBody.atmosphere.isCorrosive());
                  }

                  if (this.selectedBody == GalacticraftCore.planetOverworld) {
                     str = GCCoreUtil.translate("gui.message.corrosiveatmo") + " " + Utils.localeBoolean(false);
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 76 + 110, WHITE);
                  } else if (this.small_page == 2) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 20, WHITE);
                  }

                  if (this.selectedBody.getReachable() && !this.selectedBody.atmosphere.hasNoGases()) {
                     str = this.selectedBody.atmosphere.composition.toString();
                  } else {
                     str = GCCoreUtil.translate("gui.message.noatmosphere");
                  }

                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 75 + 121, WHITE);
                  } else if (this.small_page == 2) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 30, WHITE);
                  }
               }

               String travel = (int)(this.traveltime / 100.0) + "h " + (int)(this.traveltime % 100.0) + "m";
               str = GCCoreUtil.translate("gui.message.traveltime") + " " + travel;
               if (this.selectedBody.getReachable()) {
                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 74 + 143, ColorUtil.to32BitColor(255, 255, 255, 255));
                  } else if (this.small_page == 2) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 80, ColorUtil.to32BitColor(255, 255, 255, 255));
                  }
               }

               if (!(this.selectedBody instanceof Star)) {
                  if (this.selectedBody instanceof Planet && data != null && data.getType() != IAdvancedSpace.TypeBody.STAR) {
                     str = GCCoreUtil.translate("gui.message.moons") + " " + this.getChildren(this.selectedBody).size();
                     if (!this.selectedBody.getReachable()) {
                        if (ClientProxy.smallInfoOnMap) {
                           this.fontRenderer.drawString(str, posX, BOT - 225 + 29 + 10, WHITE);
                        } else {
                           this.fontRenderer.drawString(str, posX, TOP + y + 38, WHITE);
                        }
                     } else if (!ClientProxy.smallInfoOnMap) {
                        this.fontRenderer.drawString(str, posX, TOP + y + 76 + 132, WHITE);
                     } else if (this.small_page == 2) {
                        this.fontRenderer.drawString(str, posX, BOT - 225 + 70, WHITE);
                     }
                  }
               } else {
                  int x = 0;

                  for (CelestialBody bodyxx : this.getChildren(this.selectedParent)) {
                     if (bodyxx.getTierRequirement() >= 0) {
                        x++;
                     }
                  }

                  str = GCCoreUtil.translate("gui.message.planets") + " " + x;
                  if (ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 29 + 10, WHITE);
                  } else {
                     this.fontRenderer.drawString(str, posX, TOP + y + 38, WHITE);
                  }
               }

               str = GCCoreUtil.translate("gui.message.fuelset") + " " + this.fuelSet;
               if (this.selectedBody.getTierRequirement() != -1 && this.selectedBody.getReachable() && !this.mapMode) {
                  if (!ClientProxy.smallInfoOnMap) {
                     this.fontRenderer.drawString(str, posX, TOP + y + 83 + 143, this.fuelSet < 0 ? RED : WHITE);
                  } else if (this.small_page == 2) {
                     this.fontRenderer.drawString(str, posX, BOT - 225 + 40, this.fuelSet < 0 ? RED : WHITE);
                  }
               }

               if (this.zoomTooltipPos != 38) {
                  sliderPos = (int)Math.min(this.ticksSinceSelectionF * 2.0F, 38.0F);
                  this.zoomTooltipPos = sliderPos;
               }

               if (!(this.selectedBody instanceof Star)
                  && this.traveltime * (double)travel_modifier > this.mindistance * (double)travel_modifier * 70.0
                  && ACCompatibilityManager.isGalaxySpaceLoaded()
                  && AsmodeusConfig.enableSublightEngine) {
                  GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
                  this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                  this.drawTexturedModalRect(
                     RHS - 230,
                     GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + sliderPos - 39,
                     120,
                     38,
                     346,
                     436,
                     166,
                     76,
                     false,
                     true
                  );
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  str = GCCoreUtil.translate("gui.message.engine");
                  this.fontRenderer
                     .drawString(str, RHS - 200, GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + sliderPos - 35, GREY4);
                  str = GCCoreUtil.translate("gui.engine_type." + SpaceData.Engine_Type.SUBLIGHT_ENGINE.getName().toLowerCase());
                  int color = this.engine.getID() < SpaceData.Engine_Type.SUBLIGHT_ENGINE.getID() && !this.mc.player.capabilities.isCreativeMode ? RED : GREEN;
                  this.fontRenderer
                     .drawString(str, RHS - 206, GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + sliderPos - 20, color);
               }
            }

            BodiesData datax = BodiesRegistry.getData(this.selectedBody);
            if (this.enableTraveltime) {
               this.drawTransitBar(this.getScaledTravelTime(138));
            }

            if (this.canCreateSpaceStation(this.selectedBody) && !(this.selectedBody instanceof Satellite)) {
               GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
               int canCreateLength = Math.max(
                  0, this.drawSplitString(GCCoreUtil.translate("gui.message.can_create_space_station.name"), 0, 0, 91, 0, true, true) - 2
               );
               this.canCreateOffset = canCreateLength * this.fontRenderer.FONT_HEIGHT;
               this.drawTexturedModalRect(RHS - 95, TOP + 16, 93, 4, 159, 102, 93, 4, false, false);

               for (int barY = 0; barY < canCreateLength; barY++) {
                  this.drawTexturedModalRect(
                     RHS - 95,
                     TOP + 20 + barY * this.fontRenderer.FONT_HEIGHT,
                     93,
                     this.fontRenderer.FONT_HEIGHT,
                     159,
                     106,
                     93,
                     this.fontRenderer.FONT_HEIGHT,
                     false,
                     false
                  );
               }

               this.drawTexturedModalRect(RHS - 69, TOP + 12, 61, 4, 0, 170, 61, 4, false, false);
               SpaceStationRecipe recipe = WorldUtil.getSpaceStationRecipe(this.selectedBody.getDimensionID());
               int h = 1;
               if (recipe != null) {
                  h = Math.round((float)Math.ceil((double)((float)recipe.getRecipeSize() / 4.0F)) - 1.0F) * 25;
               }

               this.drawTexturedModalRect(RHS - 95, TOP + 20 + this.canCreateOffset, 93, 43 + h, 159, 106, 93, 43, false, false);
               if (recipe != null) {
                  GL11.glColor4f(0.0F, 1.0F, 0.1F, 1.0F);
                  boolean validInputMaterials = true;
                  int ixx = 0;
                  int yOff = 0;
                  Iterator var140 = recipe.getInput().entrySet().iterator();

                  label919:
                  while (true) {
                     while (true) {
                        if (!var140.hasNext()) {
                           if (!validInputMaterials && !this.mc.player.capabilities.isCreativeMode) {
                              GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
                           } else {
                              GL11.glColor4f(0.0F, 1.0F, 0.1F, 1.0F);
                           }

                           this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
                           this.canCreateOffset = (int)(
                              (long)this.canCreateOffset + Math.round(Math.ceil((double)((float)recipe.getRecipeSize() / 4.0F)) - 1.0) * 25L + 1L
                           );
                           if (!this.mapMode
                              && mousePosX >= RHS - 95
                              && mousePosX <= RHS
                              && mousePosY >= TOP + 62 + this.canCreateOffset
                              && mousePosY <= TOP + 62 + 12 + this.canCreateOffset) {
                              this.drawTexturedModalRect(RHS - 95, TOP + 62 + this.canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);
                           }

                           this.drawTexturedModalRect(RHS - 95, TOP + 62 + this.canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);
                           int color = (int)((Math.sin((double)this.ticksSinceSelectionF / 5.0) * 0.5 + 0.5) * 255.0);
                           this.drawSplitString(
                              GCCoreUtil.translate("gui.message.can_create_space_station.name"),
                              RHS - 48,
                              TOP + 18,
                              91,
                              ColorUtil.to32BitColor(255, color, 255, color),
                              true,
                              false
                           );
                           if (!this.mapMode) {
                              this.drawSplitString(
                                 GCCoreUtil.translate("gui.message.create_ss.name").toUpperCase(),
                                 RHS - 48,
                                 TOP + 65 + this.canCreateOffset,
                                 91,
                                 WHITE,
                                 false,
                                 false
                              );
                           }
                           break label919;
                        }

                        Entry<Object, Integer> e = (Entry<Object, Integer>)var140.next();
                        Object next = e.getKey();
                        int xPos = RHS - 95 + ixx % 4 * 93 / 4 + 5;
                        int yPos = TOP + 35 + this.canCreateOffset + 24 * (ixx >= 8 ? 2 : (ixx >= 4 ? 1 : 0));
                        if (next instanceof ItemStack) {
                           int amount = this.getAmountInInventory((ItemStack)next);
                           RenderHelper.enableGUIStandardItemLighting();
                           ItemStack toRender = ((ItemStack)next).copy();
                           this.itemRender.renderItemAndEffectIntoGUI(toRender, xPos, yPos);
                           this.itemRender.renderItemOverlayIntoGUI(this.mc.fontRenderer, toRender, xPos, yPos, null);
                           RenderHelper.disableStandardItemLighting();
                           GL11.glEnable(3042);
                           if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                              GL11.glDepthMask(true);
                              GL11.glEnable(2929);
                              GL11.glPushMatrix();
                              GL11.glTranslatef(0.0F, 0.0F, 300.0F);
                              int k = this.fontRenderer.getStringWidth(((ItemStack)next).getDisplayName());
                              int j2 = mousePosX - k / 2;
                              int k2 = mousePosY - 12;
                              int i1 = 8;
                              if (j2 + k > this.width) {
                                 j2 -= j2 - this.width + k;
                              }

                              if (k2 + i1 + 6 > this.height) {
                                 k2 = this.height - i1 - 6;
                              }

                              int j1 = ColorUtil.to32BitColor(190, 0, 153, 255);
                              this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                              this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                              this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                              this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                              this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                              int k1 = ColorUtil.to32BitColor(170, 0, 153, 255);
                              int l1 = (k1 & 16711422) >> 1 | k1 & 0xFF000000;
                              this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                              this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                              this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                              this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
                              this.fontRenderer.drawString(((ItemStack)next).getDisplayName(), j2, k2, WHITE);
                              GL11.glPopMatrix();
                           }

                           str = "" + e.getValue();
                           boolean valid = amount >= e.getValue();
                           if (!valid && validInputMaterials) {
                              validInputMaterials = false;
                           }

                           int color = valid | this.mc.player.capabilities.isCreativeMode ? GREEN : RED;
                           this.fontRenderer.drawString(str, xPos + 8 - this.fontRenderer.getStringWidth(str) / 2, yPos + 15, color);
                           break;
                        }

                        if (!(next instanceof Collection)) {
                           break;
                        }

                        Collection<ItemStack> items = (Collection<ItemStack>)next;
                        int amountx = 0;

                        for (ItemStack stack : items) {
                           amountx += this.getAmountInInventory(stack);
                        }

                        RenderHelper.enableGUIStandardItemLighting();
                        Iterator<ItemStack> it = items.iterator();
                        int count = 0;
                        int toRenderIndex = (int)(this.ticksSinceSelectionF / 20.0F % (float)items.size());

                        ItemStack toRenderx;
                        for (toRenderx = null; it.hasNext(); count++) {
                           ItemStack stack = it.next();
                           if (count == toRenderIndex) {
                              toRenderx = stack;
                              break;
                           }
                        }

                        if (toRenderx != null) {
                           this.itemRender.renderItemAndEffectIntoGUI(toRenderx, xPos, yPos);
                           this.itemRender.renderItemOverlayIntoGUI(this.mc.fontRenderer, toRenderx, xPos, yPos, null);
                           RenderHelper.disableStandardItemLighting();
                           GL11.glEnable(3042);
                           if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                              GL11.glDepthMask(true);
                              GL11.glEnable(2929);
                              GL11.glPushMatrix();
                              GL11.glTranslatef(0.0F, 0.0F, 300.0F);
                              int kx = this.fontRenderer.getStringWidth(toRenderx.getDisplayName());
                              int j2x = mousePosX - kx / 2;
                              int k2x = mousePosY - 12;
                              int i1x = 8;
                              if (j2x + kx > this.width) {
                                 j2x -= j2x - this.width + kx;
                              }

                              if (k2x + i1x + 6 > this.height) {
                                 k2x = this.height - i1x - 6;
                              }

                              int j1 = ColorUtil.to32BitColor(190, 0, 153, 255);
                              this.drawGradientRect(j2x - 3, k2x - 4, j2x + kx + 3, k2x - 3, j1, j1);
                              this.drawGradientRect(j2x - 3, k2x + i1x + 3, j2x + kx + 3, k2x + i1x + 4, j1, j1);
                              this.drawGradientRect(j2x - 3, k2x - 3, j2x + kx + 3, k2x + i1x + 3, j1, j1);
                              this.drawGradientRect(j2x - 4, k2x - 3, j2x - 3, k2x + i1x + 3, j1, j1);
                              this.drawGradientRect(j2x + kx + 3, k2x - 3, j2x + kx + 4, k2x + i1x + 3, j1, j1);
                              int k1 = ColorUtil.to32BitColor(170, 0, 153, 255);
                              int l1 = (k1 & 16711422) >> 1 | k1 & 0xFF000000;
                              this.drawGradientRect(j2x - 3, k2x - 3 + 1, j2x - 3 + 1, k2x + i1x + 3 - 1, k1, l1);
                              this.drawGradientRect(j2x + kx + 2, k2x - 3 + 1, j2x + kx + 3, k2x + i1x + 3 - 1, k1, l1);
                              this.drawGradientRect(j2x - 3, k2x - 3, j2x + kx + 3, k2x - 3 + 1, k1, k1);
                              this.drawGradientRect(j2x - 3, k2x + i1x + 2, j2x + kx + 3, k2x + i1x + 3, l1, l1);
                              this.fontRenderer.drawString(toRenderx.getDisplayName(), j2x, k2x, WHITE);
                              GL11.glPopMatrix();
                           }

                           str = "" + e.getValue();
                           boolean valid = amountx >= e.getValue();
                           if (!valid && validInputMaterials) {
                              validInputMaterials = false;
                           }

                           int color = valid | this.mc.player.capabilities.isCreativeMode ? GREEN : RED;
                           this.fontRenderer.drawString(str, xPos + 8 - this.fontRenderer.getStringWidth(str) / 2, yPos + 15, color);
                           break;
                        }
                     }

                     ixx++;
                  }
               } else {
                  this.drawSplitString(GCCoreUtil.translate("gui.message.cannot_create_space_station.name"), RHS - 48, TOP + 65, 91, WHITE, true, false);
               }
            }

            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F - Math.min(0.3F, this.ticksSinceSelectionF / 50.0F));
            this.drawTexturedModalRect(LHS, TOP, 74, 11, 0, 392, 148, 22, false, false);
            str = GCCoreUtil.translate("gui.message.catalog.name").toUpperCase();
            this.fontRenderer.drawString(str, LHS + 40 - this.fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE);
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
            if (this.selectedBody instanceof Satellite) {
               if (this.selectedStationOwner.length() != 0 && this.selectedStationOwner.equalsIgnoreCase(PlayerUtil.getName(this.mc.player))) {
                  GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
               }

               this.drawTexturedModalRect(this.width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false);
            } else {
               this.drawTexturedModalRect(this.width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false);
            }

            if (this.selectedBody.getTierRequirement() >= 0 && !(this.selectedBody instanceof Satellite)) {
               boolean canReach;
               if (this.selectedBody.getReachable()
                  && (
                     this.enableNewTierSystem
                        ? this.currenttier >= this.tierneed
                        : this.possibleBodies == null || this.possibleBodies.contains(this.selectedBody)
                  )) {
                  canReach = true;
                  GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  canReach = false;
                  GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
               }

               this.drawTexturedModalRect(this.width / 2 - 30, TOP + 11, 30, 11, 0, 414, 60, 22, false, false);
               this.drawTexturedModalRect(this.width / 2, TOP + 11, 30, 11, 128, 414, 60, 22, false, false);
               if (!this.enableNewTierSystem) {
                  str = GCCoreUtil.translateWithFormat(
                     "gui.message.tier.name", new Object[]{this.selectedBody.getTierRequirement() == 0 ? "?" : this.selectedBody.getTierRequirement()}
                  );
               } else {
                  str = GCCoreUtil.translateWithFormat(
                     "gui.message.tier.name", new Object[]{this.tierneed > 0 && this.selectedBody.getReachable() ? this.tierneed : "?"}
                  );
               }

               this.fontRenderer.drawString(str, this.width / 2 - this.fontRenderer.getStringWidth(str) / 2, TOP + 13, canReach ? GREY4 : RED3);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            str = this.selectedBody.getLocalizedName();
            if (this.selectedBody instanceof Satellite) {
               str = GCCoreUtil.translate("gui.message.rename.name").toUpperCase();
            }

            this.fontRenderer.drawString(str, this.width / 2 - this.fontRenderer.getStringWidth(str) / 2, TOP + 2, WHITE);
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
            this.drawTexturedModalRect(LHS + 4, TOP, 83, 12, 0, 477, 83, 12, false, false);
            if (!this.mapMode) {
               if (this.canTravel
                  && this.selectedBody.getReachable()
                  && (
                     this.enableNewTierSystem
                        ? this.currenttier >= this.tierneed
                        : this.possibleBodies == null || this.possibleBodies.contains(this.selectedBody)
                  )
                  && (!(this.selectedBody instanceof Satellite) || !this.selectedStationOwner.equals(""))) {
                  GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
               }

               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
               this.drawTexturedModalRect(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false);
               str = GCCoreUtil.translate("gui.message.launch.name").toUpperCase();
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.fontRenderer.drawString(str, RHS - 40 - this.fontRenderer.getStringWidth(str) / 2, TOP + 2, WHITE);
            }

            if (this.selectionState == EnumSelection.SELECTED && !(this.selectedBody instanceof Satellite)) {
               handledSliderPos = true;
               int sliderPosx = this.zoomTooltipPos;
               if (this.zoomTooltipPos != 38) {
                  sliderPosx = (int)Math.min(this.ticksSinceSelectionF * 2.0F, 38.0F);
                  this.zoomTooltipPos = sliderPosx;
               }

               int xOffsetx = 222;
               GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
               this.drawTexturedModalRect(
                  RHS - xOffsetx,
                  this.height - GuiCelestialSelection.BORDER_SIZE - GuiCelestialSelection.BORDER_EDGE_SIZE - sliderPosx,
                  83,
                  38,
                  346,
                  436,
                  166,
                  76,
                  true,
                  false
               );
               boolean flag0 = GalaxyRegistry.getSatellitesForCelestialBody(this.selectedBody).size() > 0;
               boolean flag1 = this.selectedBody instanceof Planet && GalaxyRegistry.getMoonsForPlanet((Planet)this.selectedBody).size() > 0;
               if (flag0 && flag1) {
                  this.drawSplitString(
                     GCCoreUtil.translate("gui.message.click_again.0.name"),
                     RHS - xOffsetx + 41,
                     this.height - GuiCelestialSelection.BORDER_SIZE - GuiCelestialSelection.BORDER_EDGE_SIZE + 2 - sliderPosx,
                     79,
                     GREY5,
                     false,
                     false
                  );
               } else if (!flag0 && flag1) {
                  this.drawSplitString(
                     GCCoreUtil.translate("gui.message.click_again.1.name"),
                     RHS - xOffsetx + 41,
                     this.height - GuiCelestialSelection.BORDER_SIZE - GuiCelestialSelection.BORDER_EDGE_SIZE + 6 - sliderPosx,
                     79,
                     GREY5,
                     false,
                     false
                  );
               } else if (flag0) {
                  this.drawSplitString(
                     GCCoreUtil.translate("gui.message.click_again.2.name"),
                     RHS - xOffsetx + 41,
                     this.height - GuiCelestialSelection.BORDER_SIZE - GuiCelestialSelection.BORDER_EDGE_SIZE + 6 - sliderPosx,
                     79,
                     GREY5,
                     false,
                     false
                  );
               } else {
                  this.drawSplitString(
                     GCCoreUtil.translate("gui.message.click_again.3.name"),
                     RHS - xOffsetx + 41,
                     this.height - GuiCelestialSelection.BORDER_SIZE - GuiCelestialSelection.BORDER_EDGE_SIZE + 11 - sliderPosx,
                     79,
                     GREY5,
                     false,
                     false
                  );
               }
            }

            if (this.selectedBody instanceof Satellite && this.renamingSpaceStation) {
               this.drawDefaultBackground();
               GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
               this.drawTexturedModalRect(this.width / 2 - 90, this.height / 2 - 38, 179, 67, 159, 0, 179, 67, false, false);
               this.drawTexturedModalRect(this.width / 2 - 90 + 4, this.height / 2 - 38 + 2, 171, 10, 159, 92, 171, 10, false, false);
               this.drawTexturedModalRect(this.width / 2 - 90 + 8, this.height / 2 - 38 + 18, 161, 13, 159, 67, 161, 13, false, false);
               this.drawTexturedModalRect(this.width / 2 - 90 + 17, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, true, false);
               this.drawTexturedModalRect(this.width / 2, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, false, false);
               str = GCCoreUtil.translate("gui.message.assign_name.name");
               this.fontRenderer.drawString(str, this.width / 2 - this.fontRenderer.getStringWidth(str) / 2, this.height / 2 - 35, WHITE);
               str = GCCoreUtil.translate("gui.message.apply.name");
               this.fontRenderer.drawString(str, this.width / 2 - this.fontRenderer.getStringWidth(str) / 2 - 36, this.height / 2 + 23, WHITE);
               str = GCCoreUtil.translate("gui.message.cancel.name");
               this.fontRenderer.drawString(str, this.width / 2 + 36 - this.fontRenderer.getStringWidth(str) / 2, this.height / 2 + 23, WHITE);
               if (this.renamingString == null) {
                  Satellite selectedSatellitex = (Satellite)this.selectedBody;
                  String playerName = PlayerUtil.getName(this.mc.player);
                  this.renamingString = ((StationDataGUI)((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellitex))).get(playerName))
                     .getStationName();
                  if (this.renamingString == null) {
                     this.renamingString = ((StationDataGUI)((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellitex)))
                           .get(playerName.toLowerCase()))
                        .getStationName();
                  }

                  if (this.renamingString == null) {
                     this.renamingString = "";
                  }
               }

               str = this.renamingString;
               String str0 = this.renamingString;
               if (this.ticksSinceSelectionF / 10.0F % 2.0F == 0.0F) {
                  str0 = str0 + "_";
               }

               this.fontRenderer.drawString(str0, this.width / 2 - this.fontRenderer.getStringWidth(str) / 2, this.height / 2 - 17, WHITE);
            }
         }

         str = EnumColor.ORANGE + "AsmodeusCore Galaxy Map";
         this.fontRenderer
            .drawString(
               str,
               LHS + 5,
               this.height - GuiCelestialSelection.BORDER_SIZE - GuiCelestialSelection.BORDER_EDGE_SIZE - 10,
               ColorUtil.to32BitColor(255, 255, 255, 255)
            );
         if (!handledSliderPos) {
            this.zoomTooltipPos = 0;
         }
      }
   }

   protected int drawChildren(List<CelestialBody> children, int xOffsetBase, int yOffsetPrior, boolean recursive) {
      xOffsetBase += GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE;
      int yOffsetBase = GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + 50 + yOffsetPrior;
      int yOffset = 0;
      int size = children.size();

      for (int i = 0; i < size; i++) {
         CelestialBody child = children.get(i);
         int xOffset = xOffsetBase + (child.equals(this.selectedBody) ? 5 : 0);
         int scale = (int)Math.min(95.0F, Math.max(0.0F, this.ticksSinceMenuOpenF * 25.0F - (float)(95 * i)));
         BodiesData data = BodiesRegistry.getData(child);
         this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
         float brightness = child.equals(this.selectedBody) ? 0.2F : 0.0F;
         if (child.getReachable()) {
            if (child.equals(this.selectedBody)) {
               GL11.glColor4f(0.0F, 1.0F, 1.0F, (float)scale / 95.0F);
            } else {
               GL11.glColor4f(0.0F, 0.6F + brightness, 0.0F, (float)scale / 95.0F);
            }
         } else {
            boolean checked = false;
            if (child instanceof Planet) {
               for (Moon moon : GalaxyRegistry.getMoonsForPlanet((Planet)child)) {
                  if (moon.getReachable()) {
                     checked = true;
                     break;
                  }
               }
            }

            if (data != null && data.getType() == IAdvancedSpace.TypeBody.STAR) {
               GL11.glColor4f(0.0F, 0.4F, 0.6F + brightness, (float)scale / 95.0F);
            } else if (child instanceof Planet && checked) {
               GL11.glColor4f(0.6F + brightness, 0.6F, 0.0F, (float)scale / 95.0F);
            } else {
               GL11.glColor4f(0.6F + brightness, 0.0F, 0.0F, (float)scale / 95.0F);
            }
         }

         this.drawTexturedModalRect(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, 0, 489, 86, 10, false, false);
         GL11.glColor4f(3.0F * brightness, 0.6F + 2.0F * brightness, 1.0F, (float)scale / 95.0F);
         this.drawTexturedModalRect(2 + xOffset, yOffsetBase + yOffset, 93, 12, 95, 464, 93, 12, false, false);
         if (scale > 0) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int color = 14737632;
            GlStateManager.pushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(child.getBodyIcon());
            this.drawTexturedModalRect(
               (float)(xOffset + 7), (float)(yOffsetBase + yOffset + 2), 8.0F, 8.0F, 0.0F, 0.0F, 32.0F, 32.0F, false, false, 32.0F, 32.0F
            );
            GlStateManager.popMatrix();
            this.fontRenderer.drawString(child.getLocalizedName(), 17 + xOffset, yOffsetBase + yOffset + 2, color);
         }

         yOffset += 14;
         if (recursive && child.equals(this.selectedBody)) {
            List<CelestialBody> grandchildren = this.getChildren(child, 0, this.max_scroll);
            if (grandchildren.size() > 0) {
               if (this.animateGrandchildren == 14 * grandchildren.size()) {
                  yOffset += this.drawChildren(grandchildren, 10, yOffset, false);
               } else {
                  if (this.animateGrandchildren >= 14) {
                     List<CelestialBody> partial = new LinkedList<>();

                     for (int j = 0; j < this.animateGrandchildren / 14; j++) {
                        partial.add(grandchildren.get(j));
                     }

                     this.drawChildren(partial, 10, yOffset, false);
                  }

                  yOffset += this.animateGrandchildren;
                  this.animateGrandchildren += 2;
               }
            }
         }
      }

      if (ClientProxy.smallInfoOnMap && recursive) {
         GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
         this.mc.renderEngine.bindTexture(guiMain2);
         this.drawTexturedModalRect(BORDER_SIZE + 18, yOffsetBase + yOffset - 1, 62, 4, 280, 74, 62, 4, false, true);
         GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
         boolean planetZoomedNotMoon = this.isZoomed() && !(this.selectedParent instanceof Planet);
         if (this.scroll != 0) {
            this.drawTexturedModalRect(BORDER_SIZE + 47, yOffsetBase - 4, 7, 3, 344, 74, 7, 3, false, false);
         }

         if (this.scroll != this.getChildren(planetZoomedNotMoon ? this.selectedBody : this.selectedParent).size() - this.max_scroll) {
            this.drawTexturedModalRect(BORDER_SIZE + 47, yOffsetBase + yOffset, 7, 3, 344, 74, 7, 3, false, true);
         }
      }

      return yOffset;
   }

   protected void mouseClicked(int x, int y, int button) {
      boolean clickHandled = false;
      int LHS = GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE;
      int RHS = this.width - LHS;
      int TOP = LHS;
      int BOT = this.height - LHS;
      if (x > RHS - 85 && x < RHS - 5 && y > this.height - LHS - 44 && y < this.height - LHS - 33) {
         ClientProxy.smallInfoOnMap = !ClientProxy.smallInfoOnMap;
      }

      if (x > RHS - 85 && x < RHS - 5 && y > this.height - LHS - 30 && y < this.height - LHS - 19) {
         ClientProxy.enableInfoOnMap = !ClientProxy.enableInfoOnMap;
      }

      if (x > RHS - 85 && x < RHS - 5 && y > this.height - LHS - 16 && y < this.height - LHS - 5) {
         ClientProxy.renderRingZones = !ClientProxy.renderRingZones;
      }

      if (x > LHS + 6 && x < LHS + 93 && y > LHS + 12 && y < LHS + 26 && !this.enableTraveltime) {
         if (this.galaxylist.size() > 1) {
            this.showGalaxyList = !this.showGalaxyList;
         }

         if (this.showStarList) {
            this.showStarList = false;
         }
      }

      if (x > LHS + 6 && x < LHS + 93 && y > LHS + 32 && y < LHS + 43 && !this.enableTraveltime) {
         if (GalaxyRegistry.getRegisteredSolarSystems().size() > 1) {
            this.showStarList = !this.showStarList;
         }

         if (this.showGalaxyList) {
            this.showGalaxyList = false;
         }
      }

      if (this.showStarList) {
         int l = 0;
         int posX = 0;

         for (SolarSystem s : this.starlist) {
            if (x > LHS + posX * 150 + 100 && x < LHS + posX * 150 + 193 && y > TOP + 30 * l + 8 && y < TOP + 30 * l + 37) {
               this.selectedParent = s;
               this.showStarList = false;
               this.selectedBody = s.getMainStar();
               this.selectionState = EnumSelection.SELECTED;
               this.zoom = -0.2F;
               if (ClientProxy.smallInfoOnMap) {
                  this.scroll = 0;
               }

               this.position = new Vector2f(this.getCelestialBodyPosition(this.selectedBody).x, this.getCelestialBodyPosition(this.selectedBody).y);
               clickHandled = true;
               if (this.selectedBody instanceof IChildBody) {
                  this.galaxy = ((IChildBody)this.selectedBody).getParentPlanet().getParentSolarSystem().getUnlocalizedParentGalaxyName();
               }

               if (this.selectedBody instanceof Planet) {
                  this.galaxy = ((Planet)this.selectedBody).getParentSolarSystem().getUnlocalizedParentGalaxyName();
               }

               if (this.selectedBody instanceof Star) {
                  this.galaxy = ((Star)this.selectedBody).getParentSolarSystem().getUnlocalizedParentGalaxyName();
               }
            }

            if (++l % 7 == 0) {
               posX++;
               l = 0;
            }
         }
      }

      if (this.showGalaxyList) {
         for (int i = 0; i < this.galaxylist.size(); i++) {
            if (x > LHS + 100 + 70 * i && x < LHS + 193 + 70 * i && y > TOP + 4 && y < TOP + 60) {
               this.galaxy = this.galaxylist.get(i);
               this.zoom = -1.0F;
               this.showGalaxyList = false;
               this.starlist.clear();
               this.refreshBodies();
               Iterator var24 = this.starlist.iterator();
               if (var24.hasNext()) {
                  SolarSystem s = (SolarSystem)var24.next();
                  this.selectedBody = s.getMainStar();
                  this.selectionState = EnumSelection.SELECTED;
                  this.zoom = -0.2F;
                  this.position = new Vector2f(this.getCelestialBodyPosition(this.selectedBody).x, this.getCelestialBodyPosition(this.selectedBody).y);
                  clickHandled = true;
               }
            }
         }
      }

      if (!this.enableTraveltime && this.selectedBody != null && x > LHS && x < LHS + 88 && y > TOP && y < TOP + 13) {
         this.unselectCelestialBody();
      } else {
         if (!this.mapMode
            && x >= RHS - 95
            && x < RHS
            && y > TOP + 62 + this.canCreateOffset
            && y < TOP + 62 + 12 + this.canCreateOffset
            && this.selectedBody != null) {
            SpaceStationRecipe recipe = WorldUtil.getSpaceStationRecipe(this.selectedBody.getDimensionID());
            if (recipe != null && this.canCreateSpaceStation(this.selectedBody)) {
               if (recipe.matches(this.mc.player, false) || this.mc.player.capabilities.isCreativeMode) {
                  GalacticraftCore.packetPipeline
                     .sendToServer(
                        new PacketSimple(
                           EnumSimplePacket.S_BIND_SPACE_STATION_ID, GCCoreUtil.getDimensionID(this.mc.world), new Object[]{this.selectedBody.getDimensionID()}
                        )
                     );
                  if (!this.isZoomed()) {
                     this.selectionState = EnumSelection.ZOOMED;
                     this.preSelectZoom = this.zoom;
                     this.preSelectPosition = this.position;
                     this.ticksSinceSelectionF = 0.0F;
                     this.ticksSinceSelection = 0;
                     this.doneZooming = false;
                  }

                  return;
               }

               clickHandled = true;
            }
         }

         if (this.mapMode && x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
            clickHandled = true;
         }

         if (this.selectedBody != null && !this.mapMode && !(this.selectedBody instanceof Star) && x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13) {
            if (!(this.selectedBody instanceof Satellite) || !this.selectedStationOwner.equals("")) {
               if (!this.mc.player.capabilities.isCreativeMode && this.currenttier >= this.tierneed && this.enableNewTierSystem) {
                  this.enableTraveltime = this.canTravel;
               } else {
                  this.teleportToSelectedBody();
               }
            }

            clickHandled = true;
         }

         int mouseX = Mouse.getX();
         int mouseY = Mouse.getY() * -1 + Minecraft.getMinecraft().displayHeight - 1;
         if (this.selectedBody instanceof Satellite) {
            if (this.renamingSpaceStation) {
               if (x >= this.width / 2 - 90 && x <= this.width / 2 + 90 && y >= this.height / 2 - 38 && y <= this.height / 2 + 38) {
                  if (x >= this.width / 2 - 90 + 17
                     && x <= this.width / 2 - 90 + 17 + 72
                     && y >= this.height / 2 - 38 + 59
                     && y <= this.height / 2 - 38 + 59 + 12) {
                     String strName = PlayerUtil.getName(this.mc.player);
                     Satellite selectedSatellite = (Satellite)this.selectedBody;
                     Integer spacestationID = ((StationDataGUI)((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite))).get(strName))
                        .getStationDimensionID();
                     if (spacestationID == null) {
                        spacestationID = ((StationDataGUI)((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)))
                              .get(strName.toLowerCase()))
                           .getStationDimensionID();
                     }

                     if (spacestationID != null) {
                        ((StationDataGUI)((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite))).get(strName))
                           .setStationName(this.renamingString);
                        GalacticraftCore.packetPipeline
                           .sendToServer(
                              new PacketSimple(
                                 EnumSimplePacket.S_RENAME_SPACE_STATION,
                                 GCCoreUtil.getDimensionID(this.mc.world),
                                 new Object[]{this.renamingString, spacestationID}
                              )
                           );
                     }

                     this.renamingSpaceStation = false;
                  }

                  if (x >= this.width / 2 && x <= this.width / 2 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                     this.renamingSpaceStation = false;
                  }

                  clickHandled = true;
               }
            } else {
               this.drawTexturedModalRect(this.width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false);
               if (x >= this.width / 2 - 47
                  && x <= this.width / 2 - 47 + 94
                  && y >= TOP
                  && y <= TOP + 11
                  && this.selectedStationOwner.length() != 0
                  && this.selectedStationOwner.equalsIgnoreCase(PlayerUtil.getName(this.mc.player))) {
                  this.renamingSpaceStation = true;
                  this.renamingString = null;
                  clickHandled = true;
               }

               Satellite selectedSatellitex = (Satellite)this.selectedBody;
               int stationListSize = ((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellitex))).size();
               int max = Math.min(this.height / 2 / 14, stationListSize);
               int xPos = RHS - 85;
               int yPos = TOP + 45;
               if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                  if (this.spaceStationListOffset > 0) {
                     this.spaceStationListOffset--;
                  }

                  clickHandled = true;
               }

               xPos = RHS - 85;
               yPos = TOP + 49 + max * 14;
               if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                  if (max + this.spaceStationListOffset < stationListSize) {
                     this.spaceStationListOffset++;
                  }

                  clickHandled = true;
               }

               Iterator<Entry<String, StationDataGUI>> it = ((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellitex)))
                  .entrySet()
                  .iterator();
               int ix = 0;

               for (int j = 0; it.hasNext() && ix < max; j++) {
                  Entry<String, StationDataGUI> e = it.next();
                  if (j >= this.spaceStationListOffset) {
                     int xOffset = 0;
                     if (e.getKey().equalsIgnoreCase(this.selectedStationOwner)) {
                        xOffset -= 5;
                     }

                     xPos = RHS - 95 + xOffset;
                     yPos = TOP + 50 + ix * 14;
                     if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
                        this.selectedStationOwner = e.getKey();
                        clickHandled = true;
                     }

                     ix++;
                  }
               }
            }
         }

         int xPosx = LHS + 2;
         int yPosx = TOP + 10;
         boolean planetZoomedMoon = this.isZoomed() && this.selectedParent instanceof Planet;
         if (x >= xPosx && x <= xPosx + 93 && y >= yPosx && y <= yPosx + 12 && this.selectedParent instanceof CelestialBody) {
            if (this.selectedBody == null) {
               this.preSelectZoom = this.zoom;
               this.preSelectPosition = this.position;
            }

            EnumSelection selectionCountOld = this.selectionState;
            if (!this.enableTraveltime && this.isSelected()) {
               this.unselectCelestialBody();
            }

            if (selectionCountOld == EnumSelection.ZOOMED) {
               this.selectionState = EnumSelection.SELECTED;
            }

            this.selectedBody = (CelestialBody)this.selectedParent;
            this.ticksSinceSelectionF = 0.0F;
            this.ticksSinceSelection = 0;
            this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
            if (this.isZoomed() && !planetZoomedMoon) {
               this.ticksSinceMenuOpenF = 0.0F;
               this.ticksSinceMenuOpen = 0;
            }

            clickHandled = true;
         }

         yPosx += 22;
         if (x >= xPosx && x <= xPosx + 93 && y >= yPosx && y <= yPosx + 12) {
            if (planetZoomedMoon) {
               if (this.selectedBody == null) {
                  this.preSelectZoom = this.zoom;
                  this.preSelectPosition = this.position;
               }

               EnumSelection selectionCountOldx = this.selectionState;
               if (!this.enableTraveltime && this.isSelected()) {
                  this.unselectCelestialBody();
               }

               if (selectionCountOldx == EnumSelection.ZOOMED) {
                  this.selectionState = EnumSelection.SELECTED;
               }

               this.selectedBody = (CelestialBody)this.selectedParent;
               this.ticksSinceSelectionF = 0.0F;
               this.ticksSinceSelection = 0;
               this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
            }

            clickHandled = true;
         }

         if (!clickHandled) {
            List<CelestialBody> children = this.getChildren(this.isZoomed() ? this.selectedBody : this.selectedParent, this.scroll, this.max_scroll);
            yPosx = TOP + 50;

            for (CelestialBody child : children) {
               clickHandled = this.testClicked(child, child.equals(this.selectedBody) ? 5 : 0, yPosx, x, y, false);
               yPosx += 14;
               if (!clickHandled && !this.isZoomed() && child.equals(this.selectedBody)) {
                  List<CelestialBody> grandchildren = this.getChildren(child, 0, this.max_scroll);
                  int gOffset = 0;

                  for (CelestialBody grandchild : grandchildren) {
                     if (gOffset + 14 > this.animateGrandchildren) {
                        break;
                     }

                     clickHandled = this.testClicked(grandchild, 10, yPosx, x, y, true);
                     yPosx += 14;
                     gOffset += 14;
                     if (clickHandled) {
                        break;
                     }
                  }

                  yPosx += this.animateGrandchildren - gOffset;
               }

               if (clickHandled) {
                  break;
               }
            }
         }

         if (!clickHandled) {
            for (Entry<CelestialBody, Vector3f> e : this.planetPosMap.entrySet()) {
               CelestialBody bodyClicked = e.getKey();
               if (this.selectedBody != null || !(bodyClicked instanceof IChildBody)) {
                  float iconSize = e.getValue().z;
                  if ((float)mouseX >= e.getValue().x - iconSize
                     && (float)mouseX <= e.getValue().x + iconSize
                     && (float)mouseY >= e.getValue().y - iconSize
                     && (float)mouseY <= e.getValue().y + iconSize
                     && (this.selectedBody != bodyClicked || !this.isZoomed())) {
                     if (this.isSelected() && this.selectedBody != bodyClicked) {
                        if (this.isZoomed()) {
                           this.selectionState = EnumSelection.SELECTED;
                        }

                        if (ClientProxy.smallInfoOnMap) {
                           this.scroll = 0;
                        }
                     }

                     if (bodyClicked != this.selectedBody) {
                        this.lastSelectedBody = this.selectedBody;
                        this.animateGrandchildren = 0;
                        if (!(this.selectedBody instanceof IChildBody) || ((IChildBody)this.selectedBody).getParentPlanet() != bodyClicked) {
                           this.selectionState = EnumSelection.UNSELECTED;
                        }
                     } else {
                        this.doneZooming = false;
                        this.planetZoom = 0.0F;
                     }

                     if (!this.enableTraveltime) {
                        this.selectedBody = bodyClicked;
                        this.ticksSinceSelectionF = 0.0F;
                        this.ticksSinceSelection = 0;
                        this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
                     }

                     if (this.isZoomed()) {
                        this.ticksSinceMenuOpenF = 0.0F;
                        this.ticksSinceMenuOpen = 0;
                     }

                     if (this.selectedBody instanceof Satellite
                        && ((Map)this.spaceStationMap.get(this.getSatelliteParentID((Satellite)this.selectedBody))).size() == 1) {
                        Iterator<Entry<String, StationDataGUI>> it = ((Map)this.spaceStationMap.get(this.getSatelliteParentID((Satellite)this.selectedBody)))
                           .entrySet()
                           .iterator();
                        this.selectedStationOwner = it.next().getKey();
                     }

                     clickHandled = true;
                     break;
                  }
               }
            }
         }

         if (!clickHandled) {
            if (!this.enableTraveltime && this.selectedBody != null) {
               if (x > RHS - 35 && x < RHS - 10 && y > BOT - 130 && y < BOT - 130 + 15) {
                  BodiesData data = BodiesRegistry.getData(this.selectedBody);
                  int k = 2;
                  if (this.small_page < k) {
                     this.small_page++;
                  }
               } else if (x <= RHS - 120 || x >= RHS - 90 || y <= BOT - 130 || y >= BOT - 130 + 15) {
                  this.unselectCelestialBody();
               } else if (this.small_page > 0) {
                  this.small_page--;
               }

               this.planetZoom = 0.0F;
            }

            this.mouseDragging = true;
         }

         Object selectedParent = this.selectedParent;
         if (this.selectedBody instanceof IChildBody) {
            selectedParent = ((IChildBody)this.selectedBody).getParentPlanet();
         } else if (this.selectedBody instanceof Planet) {
            selectedParent = ((Planet)this.selectedBody).getParentSolarSystem();
         } else if (this.selectedBody instanceof Star) {
            selectedParent = ((Star)this.selectedBody).getParentSolarSystem();
         } else if (this.selectedBody == null) {
            if (this.lastSelectedBody instanceof Planet) {
               selectedParent = ((Planet)this.lastSelectedBody).getParentSolarSystem();
            }

            if (this.lastSelectedBody instanceof IChildBody) {
               selectedParent = ((IChildBody)this.lastSelectedBody).getParentPlanet().getParentSolarSystem();
            }
         }

         if (this.selectedParent != selectedParent) {
            this.selectedParent = selectedParent;
            this.ticksSinceMenuOpenF = 0.0F;
            this.ticksSinceMenuOpen = 0;
         }
      }
   }

   protected boolean testClicked(CelestialBody body, int xOffset, int yPos, int x, int y, boolean grandchild) {
      int xPos = GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + 2 + xOffset;
      if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12 && (this.selectedBody != body || !this.isZoomed())) {
         if (this.selectedBody == null) {
            this.preSelectZoom = this.zoom;
            this.preSelectPosition = this.position;
         }

         EnumSelection selectionCountOld = this.selectionState;
         if (selectionCountOld == EnumSelection.ZOOMED) {
            this.selectionState = EnumSelection.SELECTED;
         }

         this.doneZooming = false;
         this.planetZoom = 0.0F;
         if (body != this.selectedBody) {
            this.lastSelectedBody = this.selectedBody;
            this.selectionState = EnumSelection.SELECTED;
            if (ClientProxy.smallInfoOnMap) {
               this.scroll = 0;
            }
         } else if (!this.enableTraveltime) {
            this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
         }

         if (!this.enableTraveltime) {
            this.selectedBody = body;
            this.ticksSinceSelectionF = 0.0F;
            this.ticksSinceSelection = 0;
            this.small_page = 0;
            if (grandchild) {
               this.selectionState = EnumSelection.ZOOMED;
            }
         }

         if (this.isZoomed()) {
            this.ticksSinceMenuOpenF = 0.0F;
            this.ticksSinceMenuOpen = 0;
         }

         this.animateGrandchildren = 0;
         return true;
      } else {
         return false;
      }
   }

   public void drawScreen(int mousePosX, int mousePosY, float partialTicks) {
      this.ticksSinceMenuOpenF += partialTicks;
      this.ticksTotalF += partialTicks;
      if (this.selectedBody != null) {
         this.ticksSinceSelectionF += partialTicks;
      }

      if (this.selectedBody == null && this.ticksSinceUnselectionF >= 0.0F) {
         this.ticksSinceUnselectionF += partialTicks;
      }

      if (Mouse.hasWheel()) {
         float wheel = (float)Mouse.getDWheel() / (this.selectedBody == null ? 500.0F : 250.0F);
         if (wheel != 0.0F) {
            if (mousePosX < 105 && ClientProxy.smallInfoOnMap) {
               boolean planetZoomedNotMoon = this.isZoomed() && !(this.selectedParent instanceof Planet);
               if (wheel > 0.0F && this.scroll > 0) {
                  this.scroll--;
               } else if (wheel < 0.0F
                  && this.scroll < this.getChildren(planetZoomedNotMoon ? this.selectedBody : this.selectedParent).size() - this.max_scroll) {
                  this.scroll++;
               }
            } else if (this.selectedBody != null && this.isZoomed()) {
               this.planetZoom = Math.min(Math.max(this.planetZoom + wheel, -4.9F), 5.0F);
            } else {
               this.zoom = Math.min(Math.max(this.zoom + wheel * (this.zoom + 2.0F) / 10.0F, -1.095F), 3.0F);
            }
         }
      }

      GL11.glPushMatrix();
      GL11.glEnable(3042);
      Matrix4f camMatrix = new Matrix4f();
      Matrix4f.translate(new Vector3f(0.0F, 0.0F, -9000.0F), camMatrix, camMatrix);
      Matrix4f viewMatrix = new Matrix4f();
      viewMatrix.m00 = 2.0F / (float)this.width;
      viewMatrix.m11 = 2.0F / (float)(-this.height);
      viewMatrix.m22 = -2.2222222E-4F;
      viewMatrix.m30 = -1.0F;
      viewMatrix.m31 = 1.0F;
      viewMatrix.m32 = -2.0F;
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      FloatBuffer fb = BufferUtils.createFloatBuffer(512);
      ((Buffer)fb).rewind();
      viewMatrix.store(fb);
      ((Buffer)fb).flip();
      GL11.glMultMatrix(fb);
      ((Buffer)fb).clear();
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      ((Buffer)fb).rewind();
      camMatrix.store(fb);
      ((Buffer)fb).flip();
      ((Buffer)fb).clear();
      GL11.glMultMatrix(fb);
      this.setBlackBackground();
      boolean drawGrid = true;
      if (AsmodeusConfig.enableImgOnGalaxyMap) {
         GL11.glPushMatrix();
         this.setImgBackground(guiImg);
         drawGrid = false;
         GL11.glPopMatrix();
      }

      GL11.glPushMatrix();
      Matrix4f worldMatrix = this.setIsometric(partialTicks);
      float gridSize = 14000.0F;
      if (drawGrid) {
         this.drawGrid(gridSize, (float)(this.height / 3) / 3.5F);
      }

      if (this.selectedBody instanceof Star) {
         this.drawAgeCircle(worldMatrix);
      }

      this.drawCircles();
      this.drawVortex();
      GL11.glPopMatrix();
      HashMap<CelestialBody, Matrix4f> matrixMap = this.drawCelestialBodies(worldMatrix);
      this.planetPosMap.clear();

      for (Entry<CelestialBody, Matrix4f> e : matrixMap.entrySet()) {
         Matrix4f planetMatrix = e.getValue();
         Matrix4f matrix0 = Matrix4f.mul(viewMatrix, planetMatrix, planetMatrix);
         int x = (int)Math.floor(((double)matrix0.m30 * 0.5 + 0.5) * (double)Minecraft.getMinecraft().displayWidth);
         int y = (int)Math.floor(
            (double)Minecraft.getMinecraft().displayHeight - ((double)matrix0.m31 * 0.5 + 0.5) * (double)Minecraft.getMinecraft().displayHeight
         );
         Vector2f vec = new Vector2f((float)x, (float)y);
         Matrix4f scaleVec = new Matrix4f();
         scaleVec.m00 = matrix0.m00;
         scaleVec.m11 = matrix0.m11;
         scaleVec.m22 = matrix0.m22;
         Vector4f newVec = Matrix4f.transform(scaleVec, new Vector4f(2.0F, -2.0F, 0.0F, 0.0F), null);
         float iconSize = newVec.y
            * ((float)Minecraft.getMinecraft().displayHeight / 2.0F)
            * (float)(e.getKey() instanceof Star ? 2 : 1)
            * (e.getKey() == this.selectedBody ? 1.5F : 1.0F);
         this.planetPosMap.put(e.getKey(), new Vector3f(vec.x, vec.y, iconSize));
      }

      this.drawSelectionCursor(fb, worldMatrix);

      try {
         this.drawButtons(mousePosX, mousePosY);
      } catch (Exception var21) {
         if (!this.errorLogged) {
            this.errorLogged = true;
            GCLog.severe("Problem identifying planet or dimension in an add on for Galacticraft!");
            GCLog.severe(
               "(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)"
            );
            var21.printStackTrace();
         }
      }

      this.drawBorder();
      GL11.glPopMatrix();
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
   }

   protected void drawSelectionCursor(FloatBuffer fb, Matrix4f worldMatrix) {
      GL11.glPushMatrix();
      switch (this.selectionState) {
         case SELECTED:
            if (this.selectedBody != null) {
               this.setupMatrix(this.selectedBody, worldMatrix, fb);
               ((Buffer)fb).clear();
               GL11.glScalef(0.06666667F, 0.06666667F, 1.0F);
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
               float colMod = this.getZoomAdvanced() < 4.9F ? (float)(Math.sin((double)(this.ticksSinceSelectionF / 2.0F)) * 0.5 + 0.5) : 1.0F;
               GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F * colMod);
               int width = (int)Math.floor(
                  (double)this.getWidthForCelestialBody(this.selectedBody) / 2.0 * (this.selectedBody instanceof IChildBody ? 9.0 : 30.0)
               );
               this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
               GL11.glPushMatrix();
               float size = this.selectedBody instanceof IChildBody ? 2.0F : 12.0F;
               GL11.glScalef(size, size, size);
               String name = this.selectedBody.getLocalizedName();
               int white = Utils.getIntColor(255, 255, 255, 255);
               int yellow = Utils.getIntColor(255, 255, 0, 255);
               this.drawString(
                  this.fontRenderer, name, 4 + this.getWidthForCelestialBody(this.selectedBody), -5, this.selectedBody instanceof Star ? yellow : white
               );
               GL11.glPopMatrix();
            }
            break;
         case ZOOMED:
            if (this.selectedBody != null) {
               this.setupMatrix(this.selectedBody, worldMatrix, fb);
               ((Buffer)fb).clear();
               float div = this.zoom + 1.0F - this.planetZoom;
               float scale = Math.max(0.3F, 1.5F / (this.ticksSinceSelectionF / 5.0F)) * 2.0F / div;
               GL11.glScalef(scale, scale, 1.0F);
               this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
               float colMod = this.getZoomAdvanced() < 4.9F ? (float)(Math.sin((double)(this.ticksSinceSelectionF / 1.0F)) * 0.5 + 0.5) : 1.0F;
               GL11.glColor4f(0.4F, 0.8F, 1.0F, 1.0F * colMod);
               int width = this.getWidthForCelestialBody(this.selectedBody) * 13;
               this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
            }
      }

      GL11.glPopMatrix();
   }

   public int getWidthForCelestialBody(CelestialBody celestialBody) {
      boolean zoomed = celestialBody == this.selectedBody && this.selectionState == EnumSelection.SELECTED;
      float size = celestialBody.getRelativeSize();
      if ((double)size < 0.9 && celestialBody instanceof IChildBody) {
         size = 1.0F;
      }

      if (size > 5.0F) {
         size = 5.0F;
      }

      if (size < 0.3F) {
         size = 0.3F;
      }

      return (int)(
         celestialBody instanceof Star
            ? (zoomed ? 12.0F * size : 8.0F * size)
            : (
               celestialBody instanceof Planet
                  ? (zoomed ? 6.0F * size : 4.0F * size)
                  : (celestialBody instanceof IChildBody ? (zoomed ? 6.0F * size : 4.0F * size) : 2.0F)
            )
      );
   }

   public HashMap<CelestialBody, Matrix4f> drawCelestialBodies(Matrix4f worldMatrix) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      FloatBuffer fb = BufferUtils.createFloatBuffer(512);
      HashMap<CelestialBody, Matrix4f> matrixMap = Maps.newHashMap();

      for (CelestialBody body : this.bodiesToRender) {
         boolean hasParent = body instanceof IChildBody;
         float alpha = this.getAlpha(body);
         if (body instanceof Satellite && alpha > 0.0F || !(body instanceof Satellite) && alpha >= 0.0F) {
            GlStateManager.pushMatrix();
            Matrix4f worldMatrixLocal = this.setupMatrix(body, worldMatrix, fb, hasParent ? 0.25F : 1.0F);
            Pre preEvent = new Pre(body, body.getBodyIcon(), 16);
            MinecraftForge.EVENT_BUS.post(preEvent);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (preEvent.celestialBodyTexture != null) {
               this.mc.renderEngine.bindTexture(preEvent.celestialBodyTexture);
            }

            if (!preEvent.isCanceled()) {
               int size = this.getWidthForCelestialBody(body);
               float xOffset = 0.0F;
               float yOffset = 0.0F;
               if (size % 2 != 0) {
                  xOffset = body.getRelativeSize() < 1.0F ? -0.5F : 0.0F;
                  yOffset = body.getRelativeSize() < 1.0F ? -0.5F : 0.0F;
               }

               BodiesData data = BodiesRegistry.getData(body);
               if (body instanceof Star || data != null && data.getType() == IAdvancedSpace.TypeBody.STAR && data.getStarColor() != null) {
                  GL11.glPushMatrix();
                  GL11.glShadeModel(7425);
                  GL11.glDisable(3553);
                  GL11.glEnable(3008);
                  GL11.glAlphaFunc(516, 0.0F);
                  float r = 1.0F;
                  float g = 1.0F;
                  float b = 0.8F;
                  float a = 0.7F;
                  float f10 = (float)(size * 4) * Math.min(2.0F - this.zoom / 2.0F, 2.0F);
                  if (data != null && data.getType() == IAdvancedSpace.TypeBody.STAR) {
                     if (data.getStarColor() != null) {
                        r = (float)data.getStarColor().getColor().getX() / 255.0F;
                        g = (float)data.getStarColor().getColor().getY() / 255.0F;
                        b = (float)data.getStarColor().getColor().getZ() / 255.0F;
                        a = 0.7F;
                     }

                     if (data.getStarType() == IAdvancedSpace.StarType.BLACKHOLE) {
                        a = 0.0F;
                        b = 0.0F;
                        g = 0.0F;
                        r = 0.0F;
                     }
                  }

                  float xSize = 0.0F;
                  float ySize = 0.0F;
                  GL11.glColor4f(r, g, b, a);
                  GL11.glDisable(2884);
                  GL11.glBegin(6);
                  GL11.glVertex2d((double)xSize, (double)ySize);

                  for (int angle = 0; angle <= 360; angle += 60) {
                     if (angle % 120 == 0) {
                        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
                     }

                     double x = (double)f10 * Math.cos((double)angle * Math.PI / 180.0);
                     double y = (double)f10 * Math.sin((double)angle * Math.PI / 180.0);
                     GL11.glVertex2d(x + (double)xSize, y + (double)ySize);
                  }

                  GL11.glEnd();
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glDisable(3008);
                  GL11.glShadeModel(7424);
                  GL11.glPopMatrix();
               }

               this.drawTexturedModalRect(
                  xOffset + (float)(-size / 2),
                  yOffset + (float)(-size / 2),
                  (float)size,
                  (float)size,
                  0.0F,
                  0.0F,
                  (float)preEvent.textureSize,
                  (float)preEvent.textureSize,
                  false,
                  false,
                  (float)preEvent.textureSize,
                  (float)preEvent.textureSize
               );
               matrixMap.put(body, worldMatrixLocal);
            }

            Post postEvent = new Post(body);
            MinecraftForge.EVENT_BUS.post(postEvent);
            GlStateManager.popMatrix();
         }
      }

      for (CelestialBody bodyx : this.bodiesToRender) {
         boolean hasParent = bodyx instanceof IChildBody;
         float alpha = this.getAlpha(bodyx);
         if (bodyx instanceof Satellite && alpha > 0.0F || !(bodyx instanceof Satellite) && alpha >= 0.0F) {
            GlStateManager.pushMatrix();
            Matrix4f worldMatrixLocalx = this.setupMatrix(bodyx, worldMatrix, fb, hasParent ? 0.25F : 1.0F);
            if (this.selectionState == EnumSelection.UNSELECTED && !hasParent || this.selectionState == EnumSelection.ZOOMED && hasParent) {
               float scale = 0.0F;
               if (this.zoom + 0.5F > 0.0F) {
                  scale = Math.min(this.zoom + 0.5F, 0.7F);
               }

               GL11.glScalef(scale, scale, 0.0F);
               String name = bodyx.getLocalizedName();
               int white = Utils.getIntColor(255, 255, 255, 255);
               int yellow = Utils.getIntColor(255, 255, 0, 255);
               this.drawString(this.fontRenderer, name, 4 + this.getWidthForCelestialBody(bodyx), -5, bodyx instanceof Star ? yellow : white);
               if (ClientProxyCore.playerHead != null) {
                  if (this.selectionState == EnumSelection.UNSELECTED
                     && this.mc.world.provider instanceof IGalacticraftWorldProvider
                     && ((IGalacticraftWorldProvider)this.mc.world.provider).getCelestialBody() instanceof IChildBody) {
                     if (bodyx == ((IChildBody)((IGalacticraftWorldProvider)this.mc.world.provider).getCelestialBody()).getParentPlanet()) {
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(770, 771);
                        GlStateManager.scale(0.25F, 0.25F, 0.25F);
                        this.mc.renderEngine.bindTexture(ClientProxyCore.playerHead);
                        this.drawTexturedModalRect(20 + this.fontRenderer.getStringWidth(name) * 5, -18, 32, 32, 32, 32);
                     }
                  } else if (this.mc.world.provider instanceof IGalacticraftWorldProvider
                     && bodyx != GalacticraftCore.planetOverworld
                     && ((IGalacticraftWorldProvider)this.mc.world.provider).getCelestialBody() == bodyx) {
                     GlStateManager.enableBlend();
                     GlStateManager.blendFunc(770, 771);
                     GlStateManager.scale(0.25F, 0.25F, 0.25F);
                     this.mc.renderEngine.bindTexture(ClientProxyCore.playerHead);
                     this.drawTexturedModalRect(20 + this.fontRenderer.getStringWidth(name) * 5, -18, 32, 32, 32, 32);
                  } else if (this.mc.world.provider instanceof WorldProviderSurface && bodyx == GalacticraftCore.planetOverworld) {
                     GlStateManager.enableBlend();
                     GlStateManager.blendFunc(770, 771);
                     GlStateManager.scale(0.25F, 0.25F, 0.25F);
                     this.mc.renderEngine.bindTexture(ClientProxyCore.playerHead);
                     this.drawTexturedModalRect(20 + this.fontRenderer.getStringWidth(name) * 5, -18, 32, 32, 32, 32);
                  }
               }
            }

            GlStateManager.popMatrix();
         }
      }

      return matrixMap;
   }

   protected Matrix4f setupMatrix(CelestialBody body, Matrix4f worldMatrix, FloatBuffer fb, float scaleXZ) {
      Matrix4f worldMatrix0 = new Matrix4f(worldMatrix);
      Matrix4f.translate(this.getCelestialBodyPosition(body), worldMatrix0, worldMatrix0);
      Matrix4f worldMatrix1 = new Matrix4f();
      Matrix4f.rotate((float)Math.toRadians(this.isometz), new Vector3f(0.0F, 0.0F, 1.0F), worldMatrix1, worldMatrix1);
      Matrix4f.rotate((float)Math.toRadians(-this.isometx), new Vector3f(1.0F, 0.0F, 0.0F), worldMatrix1, worldMatrix1);
      if (scaleXZ != 1.0F) {
         Matrix4f.scale(new Vector3f(scaleXZ, scaleXZ, 1.0F), worldMatrix1, worldMatrix1);
      }

      worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1);
      ((Buffer)fb).rewind();
      worldMatrix1.store(fb);
      ((Buffer)fb).flip();
      GL11.glMultMatrix(fb);
      return worldMatrix1;
   }

   protected void mouseClickMove(int x, int y, int lastButtonClicked, long timeSinceMouseClick) {
      if (this.mouseDragging && this.lastMovePosX != -1 && lastButtonClicked == 0) {
         int deltaX = x - this.lastMovePosX;
         int deltaY = y - this.lastMovePosY;
         float scollMultiplier = -Math.abs(this.zoom);
         if (this.zoom == -1.0F) {
            scollMultiplier = -10.5F;
         } else if (this.zoom >= -0.25F && this.zoom <= 0.15F) {
            scollMultiplier = -0.2F;
         }

         if (this.zoom >= 0.15F) {
            scollMultiplier = -0.25F;
         }

         this.translation.x = this.translation.x
            + (float)(deltaX - deltaY)
               * scollMultiplier
               * (ConfigManagerCore.invertMapMouseScroll ? -1.0F : 1.0F)
               * ConfigManagerCore.mapMouseScrollSensitivity
               * 0.2F;
         this.translation.y = this.translation.y
            + (float)(deltaY + deltaX)
               * scollMultiplier
               * (ConfigManagerCore.invertMapMouseScroll ? -1.0F : 1.0F)
               * ConfigManagerCore.mapMouseScrollSensitivity
               * 0.2F;
         if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
            this.xImgOffset += (double)((float)deltaX * (scollMultiplier / 4.0F));
            this.yImgOffset += (double)((float)deltaY * (scollMultiplier / 4.0F));
         }
      }

      this.lastMovePosX = x;
      this.lastMovePosY = y;
   }

   protected void mouseReleased(int x, int y, int button) {
      super.mouseReleased(x, y, button);
      this.mouseDragging = false;
      this.lastMovePosX = -1;
      this.lastMovePosY = -1;
   }

   public void setImgBackground(ResourceLocation galaxy) {
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
      GL11.glColor4f(0.5F, 0.4F, (float)this.nebula_color.z, 0.5F);
      this.drawTexturedModalRect(0.0F, 0.0F, (float)width, (float)height, 0.0F, 0.0F, 1024.0F, 1024.0F, false, false, 1024.0F, 1024.0F);
      GL11.glDepthMask(true);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDisable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public Matrix4f setIsometric(float partialTicks) {
      Matrix4f mat0 = new Matrix4f();
      Matrix4f.translate(new Vector3f((float)this.width / 2.0F, (float)(this.height / 2), 0.0F), mat0, mat0);
      Matrix4f.rotate((float)Math.toRadians(this.isometx), new Vector3f(1.0F, 0.0F, 0.0F), mat0, mat0);
      Matrix4f.rotate((float)Math.toRadians(-this.isometz), new Vector3f(0.0F, 0.0F, 1.0F), mat0, mat0);
      float zoomLocal = this.getZoomAdvanced();
      this.zoom = zoomLocal;
      Matrix4f.scale(new Vector3f(1.1F + zoomLocal, 1.1F + zoomLocal, 1.1F + zoomLocal), mat0, mat0);
      Vector2f cBodyPos = this.getTranslationAdvanced(partialTicks);
      this.position = this.getTranslationAdvanced(partialTicks);
      Matrix4f.translate(new Vector3f(-cBodyPos.x, -cBodyPos.y, 0.0F), mat0, mat0);
      FloatBuffer fb = BufferUtils.createFloatBuffer(16);
      ((Buffer)fb).rewind();
      mat0.store(fb);
      ((Buffer)fb).flip();
      GL11.glMultMatrix(fb);
      return mat0;
   }

   public float getAlpha(CelestialBody body) {
      float alpha = 1.0F;
      if (body instanceof IChildBody) {
         boolean selected = body == this.selectedBody
            || ((IChildBody)body).getParentPlanet() == this.selectedBody && this.selectionState != EnumSelection.SELECTED;
         boolean ready = this.lastSelectedBody != null || this.ticksSinceSelection > 35;
         boolean isSibling = this.getSiblings(this.selectedBody).contains(body);
         boolean isPossible = !(body instanceof Satellite) || this.possibleBodies != null && this.possibleBodies.contains(body);
         if ((selected || isSibling) && isPossible) {
            if (this.isZoomed() && (!selected || !ready) && !isSibling) {
               alpha = Math.min(Math.max((float)(this.ticksSinceSelection - 30) / 15.0F, 0.0F), 1.0F);
            }
         } else {
            alpha = 0.0F;
         }
      } else {
         boolean isSelected = this.selectedBody == body;
         boolean isChildSelected = this.selectedBody instanceof IChildBody;
         boolean isOwnChildSelected = isChildSelected && ((IChildBody)this.selectedBody).getParentPlanet() == body;
         if (!isSelected && !isOwnChildSelected && (this.isZoomed() || isChildSelected)) {
            if (this.lastSelectedBody == null && !(this.selectedBody instanceof IChildBody)) {
               alpha = 0.4F - Math.min((float)this.ticksSinceSelection / 25.0F, 0.4F);
            } else {
               alpha = 0.0F;
            }
         }
      }

      return alpha;
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
         alpha *= this.zoom + 0.8F;
         if (alpha > 0.0F) {
            switch (count++ % 2) {
               case 0:
                  GL11.glColor4f(0.0F, 0.2857143F, 0.64285713F, alpha / 1.4F);
                  break;
               case 1:
                  GL11.glColor4f(0.0F, 0.4F, 0.9F, alpha / 2.4F);
            }

            if (body.equals(this.selectedBody)) {
               GL11.glColor4f(0.0F, 0.4F, 0.9F, 1.0F);
            }

            micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre preEvent = new micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(
               body, systemOffset
            );
            if (ClientProxy.renderRingZones) {
               MinecraftForge.EVENT_BUS.post(preEvent);
            }

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
            }

            micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Post postEvent = new micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Post(
               body
            );
            MinecraftForge.EVENT_BUS.post(postEvent);
         }
      }

      GL11.glLineWidth(1.0F);
   }

   private void drawAgeCircle(Matrix4f worldMatrix) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glLineWidth(2.0F);
      float theta = 0.06981317F;
      float cos = (float)Math.cos(0.06981317F);
      float sin = (float)Math.sin(0.06981317F);
      CelestialBody body = this.selectedBody;
      Vector3f systemOffset = new Vector3f(0.0F, 0.0F, 0.0F);
      if (body instanceof Star) {
         systemOffset = this.getCelestialBodyPosition((Star)body);
      }

      float x = 1000.0F;
      float y = 0.0F;
      GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z);

      for (int count_rings = 1; count_rings <= 8; count_rings++) {
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

   private void drawVortex() {
      for (CelestialBody body : this.bodiesToRender) {
         GlStateManager.pushMatrix();
         new Vector3f(0.0F, 0.0F, 0.0F);
         BodiesData data = BodiesRegistry.getData(body);
         if (body instanceof Star && data != null && data.getStarType() == IAdvancedSpace.StarType.BLACKHOLE) {
            Vector3f systemOffset = this.getCelestialBodyPosition((Star)body);
            float size = (float)this.getWidthForCelestialBody(body) * 4.0F;
            float xOffset = systemOffset.x;
            float yOffset = systemOffset.y;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            float n = size / 2.0F;
            GlStateManager.translate(xOffset + -size / 2.0F + n, yOffset + -size / 2.0F + n, 0.0F);
            GlStateManager.rotate(this.ticksTotalF % 360.0F * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-(xOffset + -size / 2.0F) - n, -(yOffset + -size / 2.0F) - n, 0.0F);
            this.mc.renderEngine.bindTexture(vortexTexture);
            this.drawTexturedModalRect(xOffset + -size / 2.0F, yOffset + -size / 2.0F, size, size, 0.0F, 0.0F, 16.0F, 16.0F, false, false, 16.0F, 16.0F);
         }

         GlStateManager.popMatrix();
      }
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
         } else {
            if (cBody instanceof ICelestialRegistry) {
               ICelestialRegistry body = (ICelestialRegistry)cBody;
               if (body.getParentSolarSystem() != null) {
                  Vector3f parentVec = this.getCelestialBodyPosition(body.getParentSolarSystem().getMainStar());
                  return Vector3f.add(cBodyPos, parentVec, null);
               }
            }

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

   protected void keyTyped(char keyChar, int keyID) throws IOException {
      if (this.mapMode) {
         super.keyTyped(keyChar, keyID);
      }

      if (keyID == 1 && !this.enableTraveltime && this.selectedBody != null) {
         this.unselectCelestialBody();
      }

      if (this.renamingSpaceStation) {
         if (keyID == 14) {
            if (this.renamingString != null && this.renamingString.length() > 0) {
               String toBeParsed = this.renamingString.substring(0, this.renamingString.length() - 1);
               if (this.isValid(toBeParsed)) {
                  this.renamingString = toBeParsed;
               } else {
                  this.renamingString = "";
               }
            }
         } else if (keyChar == 22) {
            String pastestring = GuiScreen.getClipboardString();
            if (pastestring == null) {
               pastestring = "";
            }

            if (this.isValid(this.renamingString + pastestring)) {
               this.renamingString = this.renamingString + pastestring;
               this.renamingString = this.renamingString.substring(0, Math.min(String.valueOf(this.renamingString).length(), 32));
            }
         } else if (this.isValid(this.renamingString + keyChar)) {
            this.renamingString = this.renamingString + keyChar;
            this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), 32));
         }
      } else {
         if (keyID == 28 && !this.mc.player.capabilities.isCreativeMode && this.currenttier >= this.tierneed && this.enableNewTierSystem) {
            if (this.canTravel) {
               this.enableTraveltime = true;
            } else {
               this.teleportToSelectedBody();
            }
         }
      }
   }

   protected void teleportToSelectedBody() {
      if (this.selectedBody != null
         && this.selectedBody.getReachable()
         && (this.enableNewTierSystem ? this.currenttier >= this.tierneed : this.possibleBodies != null && this.possibleBodies.contains(this.selectedBody))) {
         try {
            String dimension;
            int dimensionID;
            if (this.selectedBody instanceof Satellite) {
               if (this.spaceStationMap == null) {
                  GCLog.severe("Please report as a BUG: spaceStationIDs was null.");
                  return;
               }

               Satellite selectedSatellite = (Satellite)this.selectedBody;
               Integer mapping = ((StationDataGUI)((Map)this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite))).get(this.selectedStationOwner))
                  .getStationDimensionID();
               if (mapping == null) {
                  GCLog.severe("Problem matching player name in space station check: " + this.selectedStationOwner);
                  return;
               }

               dimensionID = mapping;
               WorldProvider spacestation = WorldUtil.getProviderForDimensionClient(dimensionID);
               if (spacestation == null) {
                  GCLog.severe("Failed to find a spacestation with dimension " + dimensionID);
                  return;
               }

               dimension = "Space Station " + mapping;
            } else {
               dimensionID = this.selectedBody.getDimensionID();
               dimension = WorldUtil.getDimensionName(WorldUtil.getProviderForDimensionClient(dimensionID));
            }

            if (dimension.contains("$")) {
               this.mc.gameSettings.thirdPersonView = 0;
            }

            AsmodeusCore.packetPipeline
               .sendToServer(
                  new ACPacketSimple(ACPacketSimple.ACEnumSimplePacket.S_TELEPORT_ENTITY, GCCoreUtil.getDimensionID(this.mc.world), dimensionID, this.fuelSet)
               );
            this.mc.displayGuiScreen(new GuiCustomTeleporting(dimensionID));
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }
   }

   protected void drawTransitBar(int length) {
      int menuTopLeft = GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE;
      this.mc.renderEngine.bindTexture(guiMain2);
      GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
      this.drawTexturedModalRect(this.width / 2 - 113, menuTopLeft + 15, 225, 67, 0, 0, 225, 67, false, false);
      GL11.glColor4f(0.0F, 0.6F, 1.0F, 1.0F);
      this.drawTexturedModalRect(this.width / 2 - 50, menuTopLeft + 57, 0 + length, 10, 269, 0, 138, 10, false, false);
      String str;
      if (!(this.mc.player.world.provider instanceof WorldProviderSurface) && this.mc.player.world.provider instanceof IGalacticraftWorldProvider) {
         CelestialBody body = ((IGalacticraftWorldProvider)this.mc.player.world.provider).getCelestialBody();
         this.drawBodyOnGUI(body, this.width / 2 - 76, menuTopLeft + 54, 16, 16);
         str = GCCoreUtil.translate(body.getUnlocalizedName());
      } else {
         this.drawBodyOnGUI(GalacticraftCore.planetOverworld, this.width / 2 - 76, menuTopLeft + 54, 16, 16);
         str = GCCoreUtil.translate(GalacticraftCore.planetOverworld.getUnlocalizedName());
      }

      this.fontRenderer
         .drawString(
            str,
            this.width / 2 - 105,
            GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + 35,
            ColorUtil.to32BitColor(255, 255, 255, 255)
         );
      this.drawBodyOnGUI(this.selectedBody, this.width / 2 + 59, menuTopLeft + 54, 16, 16);
      str = GCCoreUtil.translate(this.selectedBody.getUnlocalizedName());
      this.fontRenderer
         .drawString(
            str,
            this.width / 2 + 50,
            GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + 35,
            ColorUtil.to32BitColor(255, 255, 255, 255)
         );
      str = "Boost: x" + this.coef;
      this.fontRenderer
         .drawString(
            str,
            this.width / 2 - (this.fontRenderer.getStringWidth(str) - 25),
            GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + 48,
            ColorUtil.to32BitColor(255, 0, 255, 255)
         );
      double time = this.traveltime - this.timer;
      str = (int)(time / 100.0) + "h " + (int)(time % 59.0) + "m";
      this.fontRenderer
         .drawString(
            str,
            this.width / 2 - (this.fontRenderer.getStringWidth(str) - 25),
            GuiCelestialSelection.BORDER_SIZE + GuiCelestialSelection.BORDER_EDGE_SIZE + 59,
            ColorUtil.to32BitColor(255, 0, 255, 255)
         );
      if (this.timer >= this.traveltime) {
         this.teleportToSelectedBody();
         this.enableTraveltime = false;
      }
   }

   protected int getScaledTravelTime(int barLength) {
      double relative = this.timer / this.traveltime;
      return (int)(relative * (double)barLength);
   }

   protected void drawBodyOnGUI(CelestialBody body, int x, int y, int w, int h) {
      if (body != null) {
         this.mc.renderEngine.bindTexture(body.getBodyIcon());
         this.drawFullSizedTexturedRect(x, y, w, h);
      }
   }

   public void drawFullSizedTexturedRect(int x, int y, int width, int height) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder worldRenderer = tessellator.getBuffer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos((double)x, (double)(y + height), (double)this.zLevel).tex(0.0, 0.0).endVertex();
      worldRenderer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex(1.0, 0.0).endVertex();
      worldRenderer.pos((double)(x + width), (double)y, (double)this.zLevel).tex(1.0, 1.0).endVertex();
      worldRenderer.pos((double)x, (double)y, (double)this.zLevel).tex(0.0, 1.0).endVertex();
      tessellator.draw();
   }

   protected boolean canCreateSpaceStation(CelestialBody atBody) {
      if (!this.mapMode && !ConfigManagerCore.disableSpaceStationCreation && this.canCreateStations) {
         if (this.enableNewTierSystem ? this.currenttier >= this.tierneed : this.possibleBodies == null || this.possibleBodies.contains(atBody)) {
            boolean foundRecipe = false;

            for (SpaceStationType type : GalacticraftRegistry.getSpaceStationData()) {
               if (type.getWorldToOrbitID() == atBody.getDimensionID()) {
                  foundRecipe = true;
               }
            }

            if (!foundRecipe) {
               return false;
            } else if (!ClientProxyCore.clientSpaceStationID.containsKey(atBody.getDimensionID())) {
               return true;
            } else {
               int resultID = (Integer)ClientProxyCore.clientSpaceStationID.get(atBody.getDimensionID());
               return resultID == 0 || resultID == -1;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected List<CelestialBody> getChildren(Object object, int start, int size) {
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
      if (ClientProxy.smallInfoOnMap) {
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
            if (bodyList.size() >= i + startPos) {
               doneList.add(i, bodyList.get(i + startPos));
            }
         }

         return doneList;
      } else {
         return bodyList;
      }
   }

   protected static enum EnumView {
      PREVIEW,
      PROFILE,
      GS;
   }
}
