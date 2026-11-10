package asmodeuscore.core.astronomy.sky;

import java.util.Calendar;
import java.util.Random;

import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor;
import asmodeuscore.api.dimension.IProviderFog;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies;
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.utils.Utils;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public abstract class SkyProviderBase extends IRenderHandler {

    protected enum ModeLight {
        DEFAULT, ALWAYS_LIGHT, WITHOUT_SUN;
    }

    protected enum StarCount {
        SMALL(200), MEDIUM(500), LARGE(1000);

        private int count;

        StarCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }

    private static final ResourceLocation sunTexture = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation pumpkinsunTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/pumkinsun.png");

    private static final ResourceLocation lmcTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/lmc.png");
    private static final ResourceLocation smcTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/smc.png");
    private static final ResourceLocation andromedaTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/andromeda.png");
    private static final ResourceLocation barnardaloopTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/barnardaloop.png");
    private static final ResourceLocation eta_carinaeTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/eta_carinae.png");
    private static final ResourceLocation nebulaTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/nebula.png");
    private static final ResourceLocation pleiadesTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/pleiades.png");
    private static final ResourceLocation triangulumTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/triangulum.png");

    private static final ResourceLocation milkyway_smallTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/milkyway_small.png");
    private static final ResourceLocation m110Texture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/M110.png");

    protected static final ResourceLocation[] galaxyTextures = new ResourceLocation[6];

    private final ResourceLocation planetToRender = new ResourceLocation(Constants.ASSET_PREFIX,
            "textures/gui/celestialbodies/earth.png");

    private static boolean displayListIsGen;
    private boolean displayGalaxyImg;

    public static int starList, glSkyList, glSkyList2;
    protected float sunSize, ticks;
    private float[] afloat = new float[4];

    protected Minecraft mc = Minecraft.getMinecraft();
    protected Galaxies current_galaxy = Galaxies.MILKYWAY;

    public SkyProviderBase() {
        if (!displayListIsGen)
            initializeDisplayLists();

    }

    private void initializeDisplayLists() {
        int displayLists = GLAllocation.generateDisplayLists(3);
        this.starList = displayLists;
        this.glSkyList = displayLists + 1;
        this.glSkyList2 = displayLists + 2;

        GL11.glPushMatrix();
        GL11.glNewList(this.starList, GL11.GL_COMPILE);
        if (enableStar())
            this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();

        final Tessellator tessellator = Tessellator.getInstance();
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        byte byte2 = 64;
        int i = 256 / byte2 + 2;
        float f = 16F;
        BufferBuilder worldRenderer = tessellator.getBuffer();

        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                worldRenderer.pos(j + 0, f, l + 0).endVertex();
                worldRenderer.pos(j + byte2, f, l + 0).endVertex();
                worldRenderer.pos(j + byte2, f, l + byte2).endVertex();
                worldRenderer.pos(j + 0, f, l + byte2).endVertex();
                tessellator.draw();
            }
        }
        GL11.glEndList();
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        f = -16F;
        worldRenderer = tessellator.getBuffer();
        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                worldRenderer.pos(j + 0, f, l + 0).endVertex();
                worldRenderer.pos(j + byte2, f, l + 0).endVertex();
                worldRenderer.pos(j + byte2, f, l + byte2).endVertex();
                worldRenderer.pos(j + 0, f, l + byte2).endVertex();
                tessellator.draw();
            }
        }
        GL11.glEndList();

        displayListIsGen = true;
    }

    private float fovModPrev, fovMod;

    private void updateModifer() {
        float f = 1.0F;
        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) this.mc.getRenderViewEntity();
            f = abstractclientplayer.getFovModifier();
        }

        fovModPrev = fovMod;
        fovMod += (f - fovMod) * 0.5F;
        if (fovMod > 1.5F)
            fovMod = 1.5F;
        if (fovMod < 0.1F)
            fovMod = 0.1F;
    }

    private float getFOV(Minecraft mc, float partial, boolean b) {
        float fov = 70.0F;
        Entity entity = mc.getRenderViewEntity();

        if (b) {
            fov = mc.gameSettings.fovSetting;
            fov = fov * (this.fovModPrev + (fovMod - fovModPrev) * partial);
        }

        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0.0F) {
            float f = ((EntityLivingBase) entity).deathTime + partial;
            fov /= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
        }
        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partial);

        if (iblockstate.getMaterial() == Material.WATER)
            fov = fov * 60.0F / 70.0F;

        return ForgeHooksClient.getFOVModifier(mc.entityRenderer, entity, iblockstate, partial, fov);
    }

    public void configureProjectionMatrix(Minecraft mc, float partialTicks) {
        updateModifer();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();

        Project.gluPerspective(getFOV(mc, partialTicks, true), (float) mc.displayWidth / (float) mc.displayHeight,
                0.05F, (float) (this.mc.gameSettings.renderDistanceChunks * 16) * 8);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        this.ticks = partialTicks;


        if (!displayGalaxyImg) {
            if (mc.world != null && mc.world.provider instanceof IGalacticraftWorldProvider)
                current_galaxy = BodiesRegistry
                        .getGalaxyFromBody(((IGalacticraftWorldProvider) this.mc.world.provider).getCelestialBody());

            for (int i = 0; i < 6; i++)
                galaxyTextures[i] = new ResourceLocation(AsmodeusCore.ASSET_PREFIX, "textures/environment/background/"
                        + current_galaxy.getName() + "/" + current_galaxy.getName() + "_" + (i + 1) + ".png");
            displayGalaxyImg = true;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Vec3d vec3 = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
        float f1 = (float) (vec3.x);
        float f2 = (float) (vec3.y);
        float f3 = (float) (vec3.z);
        float f6;

        if (mc.gameSettings.anaglyph) {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        float temp = (float) (mc.player.posY / 400);
        GL11.glColor3f(f1 - temp, f2 - temp, f3 - temp);
        Tessellator tessellator1 = Tessellator.getInstance();
        BufferBuilder worldRenderer1 = tessellator1.getBuffer();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        // OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        RenderHelper.disableStandardItemLighting();
        float f7;
        float f8;
        float f9 = 0;
        float size = 0;

        float rain = 1.0F - world.getRainStrength(partialTicks);

        if (rain < 0.3F)
            rain = 0.3F;
        float starBrightness = this.mc.world.isRaining() ? this.mc.world.getStarBrightness(partialTicks) * rain
                : this.mc.world.getStarBrightness(partialTicks);
        float sunBrightness = this.mc.world.isRaining() ? this.mc.world.getSunBrightness(partialTicks) * rain
                : this.mc.world.getSunBrightness(partialTicks);

        if (this.mc.world.provider instanceof IProviderFog) {
            float fog = ((IProviderFog) this.mc.world.provider).getFogDensity(0, 0, 0);

            starBrightness = starBrightness - (1F - fog);
        }
        // TODO: Stars
        if (this.mc.player.posY > 150F || starBrightness > 0.0F && !inWater(this.mc.player)) {

            if (this.mc.player.posY > 150F)
                starBrightness = starBrightness + (float) (this.mc.player.posY / 1000F);

            GL11.glPushMatrix();

            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.mc.world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

            // GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);

            int count = 5;
            StarCount starcount = getStarCount();
            if (starcount != null) {
                switch (starcount) {
                    case SMALL:
                        count = 2;
                        break;
                    case MEDIUM:
                        count = 5;
                        break;
                    case LARGE:
                        count = 8;
                        break;
                }
            }

            renderStarsOnSky(count, StarColor.WHITE, starBrightness); // white
            if (AsmodeusConfig.enableColorStars) {
                renderStarsOnSky(2, StarColor.ORANGE, starBrightness); // orange
                renderStarsOnSky(1, StarColor.BLUE, starBrightness); // blue
                renderStarsOnSky(1, StarColor.LIGHTBLUE, starBrightness); // blue
            }
            GL11.glPopMatrix();
        }

        // TODO Sun Aura
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        afloat[0] = 255 / 255.0F;
        afloat[1] = 194 / 255.0F;
        afloat[2] = 180 / 255.0F;
        afloat[3] = 0.6F;

        if (this.colorSunAura() != null) {
            afloat[0] = this.colorSunAura().getColor().getX() / 255.0F;
            afloat[1] = this.colorSunAura().getColor().getY() / 255.0F;
            afloat[2] = this.colorSunAura().getColor().getZ() / 255.0F;
        }
        f6 = afloat[0];
        f7 = afloat[1];
        f8 = afloat[2];

        if (mc.gameSettings.anaglyph) {
            f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            size = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = size;
            f8 = f11;
        }

        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(this.mc.world.getCelestialAngle(this.ticks) * 360.0F, 1.0F, 0.0F, 0.0F);
        size = sunSize() + 5.5F;
        if (!inWater(this.mc.player) && !(this.mc.world.provider instanceof WorldProviderSurface))
            this.renderSunAura(tessellator1, size + expandSizeAura(), sunBrightness);
        GL11.glShadeModel(GL11.GL_FLAT);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        f7 = 0.0F;
        f8 = 0.0F;
        f9 = 0.0F;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F - mc.world.getRainStrength(partialTicks));
        GL11.glTranslatef(f7, f8, f9);

        // TODO: Render sun
        if (this.modeLight() != ModeLight.WITHOUT_SUN && !inWater(this.mc.player)) {
            Calendar calendar = Calendar.getInstance();
            boolean flag = false;
            if (calendar.get(2) + 1 == 10 && calendar.get(5) >= 30 && calendar.get(5) <= 31
                    || calendar.get(2) + 1 == 11 && calendar.get(5) <= 1) {
                flag = true;
            }
            size = sunSize();
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.mc.world.getCelestialAngle(this.ticks) * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

            FMLClientHandler.instance().getClient().renderEngine
                    .bindTexture(flag ? pumpkinsunTexture : sunImage() != null ? sunImage() : sunTexture);
            if (enableSmoothRender())
                GL11.glDisable(GL11.GL_BLEND);
            worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldRenderer1.pos(-size, 99.9D, -size).tex(0, 0).endVertex();
            worldRenderer1.pos(size, 99.9D, -size).tex(1, 0).endVertex();
            worldRenderer1.pos(size, 99.9D, size).tex(1, 1).endVertex();
            worldRenderer1.pos(-size, 99.9D, size).tex(0, 1).endVertex();
            tessellator1.draw();
            if (enableSmoothRender())
                GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);

            // this.renderImage(sunTexture, world.getCelestialAngle(partialTicks) * 360.0F,
            // -90F, 0, sunSize());
        }

        if (this.enableBaseImages() && !inWater(this.mc.player)) {
            float light = 0.0F;

            if (this.modeLight() == ModeLight.DEFAULT)
                light = starBrightness;
            if (this.modeLight() == ModeLight.ALWAYS_LIGHT)
                light = 1.0F;

            // GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

            if (current_galaxy == Galaxies.MILKYWAY) {
                this.renderImage(this.lmcTexture, -150F, 20.0F, 0.0F, 15.0F, light);
                this.renderImage(this.smcTexture, 88, -40.0F, 0.0F, 5.0F, light);
                this.renderImage(this.andromedaTexture, -70.0F, -150.0F, 20.0F, 3.0F, light);
                this.renderImage(this.barnardaloopTexture, 35.0F, -120.0F, 0.0F, 40.0F, light);
                this.renderImage(this.eta_carinaeTexture, 35.0F, 120.0F, 0.0F, 20.0F, light);
                this.renderImage(this.pleiadesTexture, -75.0F, 120.0F, 0.0F, 6.0F, light);
                this.renderImage(this.triangulumTexture, 50F, -160.0F, -55.0F, 2.0F, light);
            } else if (current_galaxy == Galaxies.ANDROMEDA) {
                this.renderImage(this.milkyway_smallTexture, -30.0F, -60.0F, -0.0F, 5.0F, light);
                this.renderImage(this.m110Texture, -30F, -80.0F, 0.0F, 20.0F, light);
                this.renderImage(this.triangulumTexture, -20F, 10.0F, 0.0F, 10.0F, light);
                this.renderImage(this.m110Texture, 30F, 75.0F, 0.0F, 5.0F, light);// M32
            }

            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            float x = 50.0F;
            float y = -90F;
            light -= 0.2F;
            this.renderImage(this.galaxyTextures[0], (-4.0F + x) % 360, y, 0.0F, 60.0F, light);
            this.renderImage(this.galaxyTextures[1], (58.0F + x) % 360, y, 0.0F, 60.0F, light);
            this.renderImage(this.galaxyTextures[2], (120.0F + x) % 360, y, 0.0F, 60.2F, light);
            this.renderImage(this.galaxyTextures[3], (182.0F + x) % 360, y, 0.0F, 60.0F, light);
            this.renderImage(this.galaxyTextures[4], (244.0F + x) % 360, y, 0.0F, 60.2F, light);
            this.renderImage(this.galaxyTextures[5], (306.0F + x) % 360, y, 0.0F, 60.0F, light);

        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.rendererSky(tessellator1, worldRenderer1, size, partialTicks);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        GL11.glPushMatrix();
        if (mc.player.posY >= AsmodeusConfig.renderBodyPosY && enableRenderPlanet()
                && AsmodeusConfig.enableRenderBody) {
            configureProjectionMatrix(mc, partialTicks);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ZERO,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            // GL11.glEnable(GL11.GL_TEXTURE_2D);
            // if(mc.player.posY < 500)
            // size = 600 - (float) mc.player.posY;

            // else size = 100;
            size = 800;
            float color = mc.world.getSunBrightness(ticks);
            GL11.glColor4f(color, color, color, (float) (mc.player.posY / 255F - 0.3F));
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-360F, 1.0F, 0.0F, 0.0F);

            GL11.glTranslatef(0.0F, -(float) mc.player.posY / 4, 0.0F);

            if (!(mc.world.provider instanceof WorldProviderSurface))
                mc.renderEngine
                        .bindTexture(((IGalacticraftWorldProvider) mc.world.provider).getCelestialBody().getBodyIcon());
            else {
                if (!ClientProxyCore.overworldTextureRequestSent) {
                    GalacticraftCore.packetPipeline
                            .sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                                    GCCoreUtil.getDimensionID(mc.world), new Object[]{}));
                    ClientProxyCore.overworldTextureRequestSent = true;
                }
                if (ClientProxyCore.overworldTexturesValid)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, ClientProxyCore.overworldTextureClient.getGlTextureId());
                else
                    mc.renderEngine.bindTexture(this.planetToRender);
            }

            worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldRenderer1.pos(-size, -100.0D, size).tex(0D, 1.0D).endVertex();
            worldRenderer1.pos(size, -100.0D, size).tex(1.0D, 1.0D).endVertex();
            worldRenderer1.pos(size, -100.0D, -size).tex(1.0D, 0D).endVertex();
            worldRenderer1.pos(-size, -100.0D, -size).tex(0D, 0D).endVertex();
            tessellator1.draw();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GlStateManager.disableBlend();
            this.renderAtmo(tessellator1, 0.0F, 0.0F, size + 1, this.getAtmosphereColor());
            // GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();

        GlStateManager.disableTexture2D();
        GL11.glColor3f(0.0F, 0.0F, 0.0F);
        final double d0 = mc.player.getPosition().getY() - world.getHorizon();
        // System.out.println(var25);
        /*
         * if (d0 < 0.0D) { GL11.glPushMatrix(); GL11.glTranslatef(0.0F, 12.0F, 0.0F);
         * GL11.glCallList(this.glSkyList2); GL11.glPopMatrix(); f8 = 1.0F; f9 =
         * -((float)(d0 + 65.0D)); size = -f8; worldRenderer1.begin(GL11.GL_QUADS,
         * DefaultVertexFormats.POSITION);
         *
         * worldRenderer1.color(0,0,0,1f); worldRenderer1.pos((double)(-f8), (double)f9,
         * (double)f8).endVertex(); worldRenderer1.pos((double)f8, (double)f9,
         * (double)f8).endVertex(); worldRenderer1.pos((double)f8, (double)size,
         * (double)f8).endVertex(); worldRenderer1.pos((double)(-f8), (double)size,
         * (double)f8).endVertex(); worldRenderer1.pos((double)(-f8), (double)size,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)f8, (double)size,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)f8, (double)f9,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)(-f8), (double)f9,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)f8, (double)size,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)f8, (double)size,
         * (double)f8).endVertex(); worldRenderer1.pos((double)f8, (double)f9,
         * (double)f8).endVertex(); worldRenderer1.pos((double)f8, (double)f9,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)(-f8), (double)f9,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)(-f8), (double)f9,
         * (double)f8).endVertex(); worldRenderer1.pos((double)(-f8), (double)size,
         * (double)f8).endVertex(); worldRenderer1.pos((double)(-f8), (double)size,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)(-f8), (double)size,
         * (double)(-f8)).endVertex(); worldRenderer1.pos((double)(-f8), (double)size,
         * (double)f8).endVertex(); worldRenderer1.pos((double)f8, (double)size,
         * (double)f8).endVertex(); worldRenderer1.pos((double)f8, (double)size,
         * (double)(-f8)).endVertex();
         *
         * tessellator1.draw(); }
         *
         * GL11.glColor3f(70F / 256F, 0, 0);
         *
         * GL11.glPushMatrix(); GL11.glTranslatef(0.0F, -((float) (d0 - 16.0D)), 0.0F);
         * GL11.glCallList(this.glSkyList2); GL11.glPopMatrix();
         */
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // GL11.glDisable(GL11.GL_BLEND);
    }

    protected void renderImage(ResourceLocation image, float x, float y, float z, float size) {
        this.renderImage(image, x, y, z, size, FMLClientHandler.instance().getClient().world.getStarBrightness(1.0F));
    }

    protected void renderImage(ResourceLocation image, float x, float y, float z, float size, float alpha) {
        this.renderImage(image, x, y, z, size, alpha, 0.0F);
    }

    protected void renderImage(ResourceLocation image, float x, float y, float z, float size, float alpha,
                               float shadow) {

        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        GL11.glRotatef(x, 0.0F, 1.0F, 0.0F); // ������������ �� X
        GL11.glRotatef(y, 1.0F, 0.0F, 0.0F); // ������������ �� Y
        GL11.glRotatef(z, 0.0F, 0.0F, 1.0F); // ������������ �� Z
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(image);

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(-size, -100.0D, size).tex(0, 1).endVertex();
        worldRenderer.pos(size, -100.0D, size).tex(1, 1).endVertex();
        worldRenderer.pos(size, -100.0D, -size).tex(1, 0).endVertex();
        worldRenderer.pos(-size, -100.0D, -size).tex(0, 0).endVertex();
        tessellator.draw();

        if (shadow > 0.0F) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, shadow);
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            worldRenderer.pos(-size, -100.0D, size).endVertex();
            worldRenderer.pos(size, -100.0D, size).endVertex();
            worldRenderer.pos(size, -100.0D, -size).endVertex();
            worldRenderer.pos(-size, -100.0D, -size).endVertex();
            tessellator.draw();
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }

    private void renderStarsOnSky(int count, StarColor color, float starBrightness) {
        for (int i = 0; i < count; i++) {
            float x = color.getColor().getX() / 255F;
            float y = color.getColor().getY() / 255F;
            float z = color.getColor().getZ() / 255F;

            GL11.glColor4f(1, 1, 1, starBrightness);

            if (AsmodeusConfig.enableColorStars)
                GL11.glColor4f(x, y, z, starBrightness);

            // if (AsmodeusConfig.enableRotateStars) {
            // if(count % 5 == 0)
            GL11.glRotatef(-37.0F, 1, 0, 0);

            // if(count % 3 == 0)
            GL11.glRotatef(-15.0F * i, 0.0F, 1.0F, 0.0F);
            // }
            GL11.glCallList(this.starList);

        }
    }

    private void renderStars() {
        final Random rand = new Random(10842L);
        final Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tesselator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        int star_count = ConfigManagerCore.moreStars ? 1000 : 500;

        for (int starIndex = 0; starIndex < star_count; ++starIndex) {

            double var4 = rand.nextFloat() * 2.0F - 1.0F;
            double var6 = rand.nextFloat() * 2.0F - 1.0F;
            double var8 = rand.nextFloat() * 2.0F - 1.0F;
            final double var10 = 0.15F + rand.nextFloat() * 0.1F;
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D) {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                final double var14 = var4 * (ConfigManagerCore.moreStars ? rand.nextDouble() * 150D + 130D : 100.0D);
                final double var16 = var6 * (ConfigManagerCore.moreStars ? rand.nextDouble() * 150D + 130D : 100.0D);
                final double var18 = var8 * (ConfigManagerCore.moreStars ? rand.nextDouble() * 150D + 130D : 100.0D);
                final double var20 = Math.atan2(var4, var8);
                final double var22 = Math.sin(var20);
                final double var24 = Math.cos(var20);
                final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                final double var28 = Math.sin(var26);
                final double var30 = Math.cos(var26);
                final double var32 = rand.nextDouble() * Math.PI * 2.0D;
                final double var34 = Math.sin(var32);
                final double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38) {
                    final double var39 = 0.0D;
                    final double var41 = ((var38 & 2) - 1) * var10;
                    final double var43 = ((var38 + 1 & 2) - 1) * var10;
                    final double var47 = var41 * var36 - var43 * var34;
                    final double var49 = var43 * var36 + var41 * var34;
                    final double var53 = var47 * var28 + var39 * var30;
                    final double var55 = var39 * var28 - var47 * var30;
                    final double var57 = var55 * var22 - var49 * var24;
                    final double var61 = var49 * var22 + var55 * var24;

                    worldRenderer.pos(var14 + var57, var16 + var53, var18 + var61)
                            /* .color(r, g, b, starBrightness) */.endVertex();
                }
            }

        }

        tesselator.draw();
    }

    @Deprecated
    protected void renderAtmo(Tessellator tessellator, float x, float y, float f10, Vector3 vec) {
        this.renderAtmo(tessellator,x,y,f10, vec == null ? null : vec.toVec3()) ;
    }

    protected void renderAtmo(Tessellator tessellator1, float x, float y, float f10, Vec3d vec) {
        if (vec != null && AsmodeusConfig.enableRenderAtmosphere) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glRotatef(x, 0.0F, 1.0F, 0.0F); // ������������ �� X
            GL11.glRotatef(y, 1.0F, 0.0F, 0.0F); // ������������ �� Y

            double planetOrbitalDistance = 5.0D;

            double dist = (-64D - 4 * (planetOrbitalDistance) / 2D)
                    - ((float) mc.player.posY / ((float) mc.player.posY));
            double scalingMult = 1D - 0.9 * (planetOrbitalDistance);

            float Xoffset = (float) ((System.currentTimeMillis() / 1000000d % 1));

            float f14 = 1f + Xoffset;
            float f15 = 0f + Xoffset;
            float f16 = f15;
            float f17 = f14;
            float[] color = new float[4];

            color[0] = (float) vec.x;
            color[1] = (float) vec.y;
            color[2] = (float) vec.z;
            color[3] = 0.09F;

            BufferBuilder worldRenderer = tessellator1.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            // worldRenderer.color(color[0], color[1], color[2], 0.09F);

            for (int i = 0; i < 8; i++) {
                renderTestWithUV(worldRenderer, color, dist + i * scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17);
                renderTestWithUV(worldRenderer, color, dist + i * scalingMult, 0, 0, f10, f10, f14, f15, f16, f17);
                renderTestWithUV(worldRenderer, color, dist + i * scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17);
                renderTestWithUV(worldRenderer, color, dist + i * scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17);
            }
            tessellator1.draw();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

    }

    private static void renderTestWithUV(BufferBuilder buff, float[] color, double yMax, double xMin, double zMin,
                                         double xMax, double zMax, double uMin, double uMax, double vMin, double vMax) {

        buff.pos(xMin, yMax, zMin).tex(uMin, vMin).color(color[0], color[1], color[2], color[3]).endVertex();
        buff.pos(xMin, yMax, zMax).tex(uMin, vMax).color(color[0], color[1], color[2], color[3]).endVertex();
        buff.pos(xMax, yMax, zMax).tex(uMax, vMax).color(color[0], color[1], color[2], color[3]).endVertex();
        buff.pos(xMax, yMax, zMin).tex(uMax, vMin).color(color[0], color[1], color[2], color[3]).endVertex();

    }

    protected void renderSunAura(Tessellator tessellator1, float f10, float f18) {
        this.renderSunAura(tessellator1, f10, f18, this.colorSunAura());
    }

    protected void renderSunAura(Tessellator tessellator1, float f10, float f18, StarColor color) {
        BufferBuilder worldRenderer = tessellator1.getBuffer();
        // GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Vec3d vec3 = this.mc.world.getSkyColor(mc.getRenderViewEntity(), this.ticks);
        float f1 = (float) vec3.x;
        float f2 = (float) vec3.y;
        float f3 = (float) vec3.z;
        float f6;
        float f7;
        float f8;
        float f9 = 0;
        float f11;

        if (mc.gameSettings.anaglyph) {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        afloat[0] = 255 / 255.0F;
        afloat[1] = 194 / 255.0F;
        afloat[2] = 180 / 255.0F;
        afloat[3] = 0.5F;
        if (color != null) {
            afloat[0] = color.getColor().getX() / 255.0F;
            afloat[1] = color.getColor().getY() / 255.0F;
            afloat[2] = color.getColor().getZ() / 255.0F;
        }

        f6 = afloat[0];
        f7 = afloat[1];
        f8 = afloat[2];

        if (mc.gameSettings.anaglyph) {
            f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = f10;
            f8 = f11;
        }

        if (f18 > 1.0F)
            f18 = 1.0F;

        float r = f6 * f18;
        float g = f7 * f18;
        float b = f8 * f18;
        float a = afloat[3] * 2 / f18 - Minecraft.getMinecraft().world.getRainStrength(this.ticks);

        if (this.modeLight() != ModeLight.WITHOUT_SUN) {
            worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

            worldRenderer.pos(0.0D, 100.0D, 0.0D).color(r, g, b, a).endVertex();

            byte b0 = 16;
            r = afloat[0] * f18;
            g = afloat[1] * f18;
            b = afloat[2] * f18;
            a = 0.0F;

            // Render sun aura
            worldRenderer.pos(-f10, 100.0D, -f10).color(r, g, b, a).endVertex();
            worldRenderer.pos(0, 100.0D, (double) -f10 * 1.5F).color(r, g, b, a).endVertex();
            worldRenderer.pos(f10, 100.0D, -f10).color(r, g, b, a).endVertex();
            worldRenderer.pos((double) f10 * 1.5F, 100.0D, 0).color(r, g, b, a).endVertex();
            worldRenderer.pos(f10, 100.0D, f10).color(r, g, b, a).endVertex();
            worldRenderer.pos(0, 100.0D, (double) f10 * 1.5F).color(r, g, b, a).endVertex();
            worldRenderer.pos(-f10, 100.0D, f10).color(r, g, b, a).endVertex();
            worldRenderer.pos((double) -f10 * 1.5F, 100.0D, 0).color(r, g, b, a).endVertex();
            worldRenderer.pos(-f10, 100.0D, -f10).color(r, g, b, a).endVertex();

            tessellator1.draw();
        }

        if (enableSmoothRender()) {
            GL11.glLineWidth(2);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }

        if (enableLargeSunAura()) {
            worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

            r = f6 * f18;
            g = f7 * f18;
            b = f8 * f18;
            a = enableSmoothRender() ? afloat[3] * 2 / f18 - Minecraft.getMinecraft().world.getRainStrength(this.ticks)
                    : afloat[3] * f18;

            worldRenderer.pos(0.0D, 100.0D, 0.0D).color(r, g, b, a).endVertex();

            r = afloat[0] * f18;
            g = afloat[1] * f18;
            b = afloat[2] * f18;
            a = 0.0F;

            // Render larger sun aura
            f10 = f10 + 5.0F;
            int i = enableSmoothRender() ? 8 : 0;
            f10 += i;
            worldRenderer.pos(-f10, 100.0D, -f10).color(r, g, b, a).endVertex();
            worldRenderer.pos(0, 100.0D, (double) -f10 * 1.5F).color(r, g, b, a).endVertex();
            f10 -= i + i;
            worldRenderer.pos(f10, 100.0D, -f10).color(r, g, b, a).endVertex();
            worldRenderer.pos((double) f10 * 1.5F, 100.0D, 0).color(r, g, b, a).endVertex();
            f10 += i;
            worldRenderer.pos(f10, 100.0D, f10).color(r, g, b, a).endVertex();
            worldRenderer.pos(0, 100.0D, (double) f10 * 1.5F).color(r, g, b, a).endVertex();
            f10 -= i;
            worldRenderer.pos(-f10, 100.0D, f10).color(r, g, b, a).endVertex();
            worldRenderer.pos((double) -f10 * 1.5F, 100.0D, 0).color(r, g, b, a).endVertex();
            worldRenderer.pos(-f10, 100.0D, -f10).color(r, g, b, a).endVertex();

            tessellator1.draw();
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

    }

    protected boolean inWater(EntityPlayer player) {
        BlockPos pos = player.getPosition();

        return mc.world.getBlockState(pos.up()).getMaterial() == Material.WATER;
    }

    protected float getCelestialAngle(long daylength) {
        return Utils.calculateCelestialAngle(this.mc.world.getWorldTime(), this.ticks, (int) daylength) * 360.0F;
    }

    protected long getDayLength() {
        long daylength = 24000L;
        if (this.mc.world.provider instanceof WorldProviderSpace)
            daylength = ((WorldProviderSpace) this.mc.world.provider).getDayLength();
        else if (this.mc.world.provider instanceof WE_WorldProviderSpace)
            daylength = ((WE_WorldProviderSpace) this.mc.world.provider).getDayLength();

        return daylength;
    }

    protected abstract void rendererSky(Tessellator tessellator, BufferBuilder buffer, float size, float ticks);

    protected abstract boolean enableBaseImages();

    protected abstract ModeLight modeLight();

    protected abstract float sunSize();

    protected abstract boolean enableStar();

    protected abstract ResourceLocation sunImage();

    protected abstract StarColor colorSunAura();

    protected abstract Vector3 getAtmosphereColor();

    public boolean enableLargeSunAura() {
        return true;
    }

    public boolean enableSmoothRender() {
        return false;
    }

    public boolean enableRenderPlanet() {
        return true;
    }

    public int expandSizeAura() {
        return 0;
    }

    protected StarCount getStarCount() {
        return StarCount.MEDIUM;
    }

}
