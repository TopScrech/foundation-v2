package asmodeuscore.core.astronomy.sky

import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import org.lwjgl.opengl.GL11
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.util.glu.Project
import asmodeuscore.api.dimension.IProviderFog
import asmodeuscore.core.astronomy.sky.SkyProviderBase.StarCount
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.core.utils.Utils
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Calendar
import java.util.Random
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SideOnly(Side.CLIENT)
abstract class SkyProviderBase : IRenderHandler() {
    protected enum class ModeLight {
        DEFAULT, ALWAYS_LIGHT, WITHOUT_SUN
    }

    protected enum class StarCount(private val count: Int) {
        SMALL(200), MEDIUM(500), LARGE(1000);

        fun getCount(): Int {
            return this.count
        }
    }

    private val planetToRender = ResourceLocation(
        Constants.ASSET_PREFIX,
        "textures/gui/celestialbodies/earth.png"
    )

    private var displayGalaxyImg = false

    @JvmField
protected var sunSize: Float = 0f

    @JvmField
protected var ticks: Float = 0f
    private val afloat = FloatArray(4)

    @JvmField
    protected var mc: Minecraft = Minecraft.getMinecraft()

    @JvmField
    protected var current_galaxy: Galaxies? = Galaxies.MILKYWAY

    private fun initializeDisplayLists() {
        val displayLists: Int = GLAllocation.generateDisplayLists(3)
        starList = displayLists
        glSkyList = displayLists + 1
        glSkyList2 = displayLists + 2

        GL11.glPushMatrix()
        GL11.glNewList(starList, GL11.GL_COMPILE)
        if (enableStar()) this.renderStars()
        GL11.glEndList()
        GL11.glPopMatrix()

        val tessellator: Tessellator = Tessellator.getInstance()
        GL11.glNewList(glSkyList, GL11.GL_COMPILE)
        val byte2: Byte = 64
        val i: Int = 256 / byte2 + 2
        var f: Float = 16f
        var worldRenderer: BufferBuilder = tessellator.getBuffer()

        run {
            var j: Int = -byte2 * i
            while (j <= byte2 * i) {
                var l: Int = -byte2 * i
                while (l <= byte2 * i) {
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos((j + 0).toDouble(), f.toDouble(), (l + 0).toDouble()).endVertex()
                    worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + 0).toDouble()).endVertex()
                    worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                    worldRenderer.pos((j + 0).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                    tessellator.draw()
                    l += byte2.toInt()
                }
                j += byte2.toInt()
            }
        }
        GL11.glEndList()
        GL11.glNewList(glSkyList2, GL11.GL_COMPILE)
        f = -16f
        worldRenderer = tessellator.getBuffer()
        var j: Int = -byte2 * i
        while (j <= byte2 * i) {
            var l: Int = -byte2 * i
            while (l <= byte2 * i) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                worldRenderer.pos((j + 0).toDouble(), f.toDouble(), (l + 0).toDouble()).endVertex()
                worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + 0).toDouble()).endVertex()
                worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                worldRenderer.pos((j + 0).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                tessellator.draw()
                l += byte2.toInt()
            }
            j += byte2.toInt()
        }
        GL11.glEndList()

        displayListIsGen = true
    }

    private var fovModPrev = 0f
    private var fovMod = 0f

    init {
        if (!displayListIsGen) initializeDisplayLists()
    }

    private fun updateModifer() {
        var f: Float = 1.0f
        if (this.mc.getRenderViewEntity() is AbstractClientPlayer) {
            val abstractclientplayer: AbstractClientPlayer? = this.mc.getRenderViewEntity() as AbstractClientPlayer?
            f = abstractclientplayer!!.getFovModifier()
        }

        fovModPrev = fovMod
        fovMod += (f - fovMod) * 0.5f
        if (fovMod > 1.5f) fovMod = 1.5f
        if (fovMod < 0.1f) fovMod = 0.1f
    }

    private fun getFOV(mc: Minecraft, partial: Float, b: Boolean): Float {
        var fov: Float = 70.0f
        val entity: Entity? = mc.getRenderViewEntity()

        if (b) {
            fov = mc.gameSettings.fovSetting
            fov = fov * (this.fovModPrev + (fovMod - fovModPrev) * partial)
        }

        if (entity is EntityLivingBase && entity.getHealth() <= 0.0f) {
            val f: Float = entity.deathTime + partial
            fov /= (1.0f - 500.0f / (f + 500.0f)) * 2.0f + 1.0f
        }
        val iblockstate: IBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partial)

        if (iblockstate.getMaterial() === Material.WATER) fov = fov * 60.0f / 70.0f

        return ForgeHooksClient.getFOVModifier(mc.entityRenderer, entity, iblockstate, partial.toDouble(), fov)
    }

    fun configureProjectionMatrix(mc: Minecraft, partialTicks: Float) {
        updateModifer()
        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GlStateManager.loadIdentity()

        Project.gluPerspective(
            getFOV(mc, partialTicks, true), mc.displayWidth.toFloat() / mc.displayHeight.toFloat(),
            0.05f, (this.mc.gameSettings.renderDistanceChunks * 16).toFloat() * 8
        )

        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
    }

    override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
        this.ticks = partialTicks


        if (!displayGalaxyImg) {
            if (mc.world != null && mc.world.provider is IGalacticraftWorldProvider) current_galaxy = BodiesRegistry
                .getGalaxyFromBody((this.mc.world.provider as IGalacticraftWorldProvider).getCelestialBody())

            for (i: Int in 0..5) galaxyTextures[i] = ResourceLocation(
                AsmodeusCore.ASSET_PREFIX, ("textures/environment/background/"
                        + current_galaxy!!.getName() + "/" + current_galaxy!!.getName() + "_" + (i + 1) + ".png")
            )
            displayGalaxyImg = true
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val vec3: Vec3d = world.getSkyColor(mc.getRenderViewEntity(), partialTicks)
        var f1: Float = (vec3.x).toFloat()
        var f2: Float = (vec3.y).toFloat()
        var f3: Float = (vec3.z).toFloat()
        var f6: Float

        if (mc.gameSettings.anaglyph) {
            val f4: Float = (f1 * 30.0f + f2 * 59.0f + f3 * 11.0f) / 100.0f
            val f5: Float = (f1 * 30.0f + f2 * 70.0f) / 100.0f
            f6 = (f1 * 30.0f + f3 * 70.0f) / 100.0f
            f1 = f4
            f2 = f5
            f3 = f6
        }

        val temp: Float = (mc.player.posY / 400).toFloat()
        GL11.glColor3f(f1 - temp, f2 - temp, f3 - temp)
        val tessellator1: Tessellator = Tessellator.getInstance()
        val worldRenderer1: BufferBuilder = tessellator1.getBuffer()
        GL11.glDepthMask(false)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glCallList(glSkyList)
        GL11.glDisable(GL11.GL_FOG)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        // OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )

        RenderHelper.disableStandardItemLighting()
        var f7: Float
        var f8: Float
        var f9: Float = 0f
        var size: Float = 0f

        var rain: Float = 1.0f - world.getRainStrength(partialTicks)

        if (rain < 0.3f) rain = 0.3f
        var starBrightness: Float = if (this.mc.world.isRaining())
            this.mc.world.getStarBrightness(partialTicks) * rain
        else
            this.mc.world.getStarBrightness(partialTicks)
        val sunBrightness: Float = if (this.mc.world.isRaining())
            this.mc.world.getSunBrightness(partialTicks) * rain
        else
            this.mc.world.getSunBrightness(partialTicks)

        if (this.mc.world.provider is IProviderFog) {
            val fog: Float = (this.mc.world.provider as IProviderFog).getFogDensity(0, 0, 0)

            starBrightness = starBrightness - (1f - fog)
        }
        // TODO: Stars
        if (this.mc.player.posY > 150f || starBrightness > 0.0f && !inWater(this.mc.player)) {
            if (this.mc.player.posY > 150f) starBrightness = starBrightness + (this.mc.player.posY / 1000f).toFloat()

            GL11.glPushMatrix()

            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(this.mc.world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)

            // GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);
            var count: Int = 5
            val starcount: StarCount? = getStarCount()
            if (starcount != null) {
                when (starcount) {
                    StarCount.SMALL -> count = 2
                    StarCount.MEDIUM -> count = 5
                    StarCount.LARGE -> count = 8
                }
            }

            renderStarsOnSky(count, StarColor.WHITE, starBrightness) // white
            if (AsmodeusConfig.enableColorStars) {
                renderStarsOnSky(2, StarColor.ORANGE, starBrightness) // orange
                renderStarsOnSky(1, StarColor.BLUE, starBrightness) // blue
                renderStarsOnSky(1, StarColor.LIGHTBLUE, starBrightness) // blue
            }
            GL11.glPopMatrix()
        }

        // TODO Sun Aura
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glShadeModel(GL11.GL_SMOOTH)

        afloat[0] = 255 / 255.0f
        afloat[1] = 194 / 255.0f
        afloat[2] = 180 / 255.0f
        afloat[3] = 0.6f

        if (this.colorSunAura() != null) {
            afloat[0] = this.colorSunAura()!!.getColor().getX() / 255.0f
            afloat[1] = this.colorSunAura()!!.getColor().getY() / 255.0f
            afloat[2] = this.colorSunAura()!!.getColor().getZ() / 255.0f
        }
        f6 = afloat[0]
        f7 = afloat[1]
        f8 = afloat[2]

        if (mc.gameSettings.anaglyph) {
            f9 = (f6 * 30.0f + f7 * 59.0f + f8 * 11.0f) / 100.0f
            size = (f6 * 30.0f + f7 * 70.0f) / 100.0f
            val f11: Float = (f6 * 30.0f + f8 * 70.0f) / 100.0f
            f6 = f9
            f7 = size
            f8 = f11
        }

        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(this.mc.world.getCelestialAngle(this.ticks) * 360.0f, 1.0f, 0.0f, 0.0f)
        size = sunSize() + 5.5f
        if (!inWater(this.mc.player) && this.mc.world.provider !is WorldProviderSurface) this.renderSunAura(
            tessellator1,
            size + expandSizeAura(),
            sunBrightness
        )
        GL11.glShadeModel(GL11.GL_FLAT)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO)
        f7 = 0.0f
        f8 = 0.0f
        f9 = 0.0f
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - mc.world.getRainStrength(partialTicks))
        GL11.glTranslatef(f7, f8, f9)

        // TODO: Render sun
        if (this.modeLight() != ModeLight.WITHOUT_SUN && !inWater(this.mc.player)) {
            val calendar: Calendar = Calendar.getInstance()
            var flag: Boolean = false
            if (calendar.get(2) + 1 == 10 && calendar.get(5) >= 30 && calendar.get(5) <= 31
                || calendar.get(2) + 1 == 11 && calendar.get(5) <= 1
            ) {
                flag = true
            }
            size = sunSize()
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(this.mc.world.getCelestialAngle(this.ticks) * 360.0f, 1.0f, 0.0f, 0.0f)
            GL11.glEnable(GL11.GL_ALPHA_TEST)
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)

            FMLClientHandler.instance().getClient().renderEngine
                .bindTexture(if (flag) pumpkinsunTexture else if (sunImage() != null) sunImage() else sunTexture)
            if (enableSmoothRender()) GL11.glDisable(GL11.GL_BLEND)
            worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer1.pos(-size.toDouble(), 99.9, -size.toDouble()).tex(0.0, 0.0).endVertex()
            worldRenderer1.pos(size.toDouble(), 99.9, -size.toDouble()).tex(1.0, 0.0).endVertex()
            worldRenderer1.pos(size.toDouble(), 99.9, size.toDouble()).tex(1.0, 1.0).endVertex()
            worldRenderer1.pos(-size.toDouble(), 99.9, size.toDouble()).tex(0.0, 1.0).endVertex()
            tessellator1.draw()
            if (enableSmoothRender()) GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_ALPHA_TEST)

            // this.renderImage(sunTexture, world.getCelestialAngle(partialTicks) * 360.0F,
            // -90F, 0, sunSize());
        }

        if (this.enableBaseImages() && !inWater(this.mc.player)) {
            var light: Float = 0.0f

            if (this.modeLight() == ModeLight.DEFAULT) light = starBrightness
            if (this.modeLight() == ModeLight.ALWAYS_LIGHT) light = 1.0f

            // GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            if (current_galaxy == Galaxies.MILKYWAY) {
                this.renderImage(lmcTexture, -150f, 20.0f, 0.0f, 15.0f, light)
                this.renderImage(smcTexture, 88f, -40.0f, 0.0f, 5.0f, light)
                this.renderImage(andromedaTexture, -70.0f, -150.0f, 20.0f, 3.0f, light)
                this.renderImage(barnardaloopTexture, 35.0f, -120.0f, 0.0f, 40.0f, light)
                this.renderImage(eta_carinaeTexture, 35.0f, 120.0f, 0.0f, 20.0f, light)
                this.renderImage(pleiadesTexture, -75.0f, 120.0f, 0.0f, 6.0f, light)
                this.renderImage(triangulumTexture, 50f, -160.0f, -55.0f, 2.0f, light)
            } else if (current_galaxy == Galaxies.ANDROMEDA) {
                this.renderImage(milkyway_smallTexture, -30.0f, -60.0f, -0.0f, 5.0f, light)
                this.renderImage(m110Texture, -30f, -80.0f, 0.0f, 20.0f, light)
                this.renderImage(triangulumTexture, -20f, 10.0f, 0.0f, 10.0f, light)
                this.renderImage(m110Texture, 30f, 75.0f, 0.0f, 5.0f, light) // M32
            }

            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f)
            val x: Float = 50.0f
            val y: Float = -90f
            light -= 0.2f
            this.renderImage(galaxyTextures[0], (-4.0f + x) % 360, y, 0.0f, 60.0f, light)
            this.renderImage(galaxyTextures[1], (58.0f + x) % 360, y, 0.0f, 60.0f, light)
            this.renderImage(galaxyTextures[2], (120.0f + x) % 360, y, 0.0f, 60.2f, light)
            this.renderImage(galaxyTextures[3], (182.0f + x) % 360, y, 0.0f, 60.0f, light)
            this.renderImage(galaxyTextures[4], (244.0f + x) % 360, y, 0.0f, 60.2f, light)
            this.renderImage(galaxyTextures[5], (306.0f + x) % 360, y, 0.0f, 60.0f, light)
        }
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()

        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.rendererSky(tessellator1, worldRenderer1, size, partialTicks)
        GL11.glDisable(GL11.GL_ALPHA_TEST)

        GL11.glPushMatrix()
        if (mc.player.posY >= AsmodeusConfig.renderBodyPosY && enableRenderPlanet()
            && AsmodeusConfig.enableRenderBody
        ) {
            configureProjectionMatrix(mc, partialTicks)
            GL11.glEnable(GL11.GL_ALPHA_TEST)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ZERO,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            )

            // GL11.glEnable(GL11.GL_TEXTURE_2D);
            // if(mc.player.posY < 500)
            // size = 600 - (float) mc.player.posY;

            // else size = 100;
            size = 800f
            val color: Float = mc.world.getSunBrightness(ticks)
            GL11.glColor4f(color, color, color, (mc.player.posY / 255f - 0.3f).toFloat())
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(-360f, 1.0f, 0.0f, 0.0f)

            GL11.glTranslatef(0.0f, -mc.player.posY.toFloat() / 4, 0.0f)

            if (mc.world.provider !is WorldProviderSurface) mc.renderEngine
                .bindTexture(((mc.world.provider as IGalacticraftWorldProvider).getCelestialBody())!!.getBodyIcon())
            else {
                if (!ClientProxyCore.overworldTextureRequestSent) {
                    (GalacticraftCore.packetPipeline)!!
                        .sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                                GCCoreUtil.getDimensionID(mc.world), arrayOf<Any?>()
                            )
                        )
                    ClientProxyCore.overworldTextureRequestSent = true
                }
                if (ClientProxyCore.overworldTexturesValid) GL11.glBindTexture(
                    GL11.GL_TEXTURE_2D,
                    (ClientProxyCore.overworldTextureClient)!!.getGlTextureId()
                )
                else mc.renderEngine.bindTexture(this.planetToRender)
            }

            worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer1.pos(-size.toDouble(), -100.0, size.toDouble()).tex(0.0, 1.0).endVertex()
            worldRenderer1.pos(size.toDouble(), -100.0, size.toDouble()).tex(1.0, 1.0).endVertex()
            worldRenderer1.pos(size.toDouble(), -100.0, -size.toDouble()).tex(1.0, 0.0).endVertex()
            worldRenderer1.pos(-size.toDouble(), -100.0, -size.toDouble()).tex(0.0, 0.0).endVertex()
            tessellator1.draw()
            GL11.glDisable(GL11.GL_ALPHA_TEST)
            GlStateManager.disableBlend()
            this.renderAtmo(tessellator1, 0.0f, 0.0f, size + 1, this.getAtmosphereColor())
            // GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glPopMatrix()

        GlStateManager.disableTexture2D()
        GL11.glColor3f(0.0f, 0.0f, 0.0f)
        val d0: Double = mc.player.getPosition().getY() - world.getHorizon()
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
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableTexture2D()
        GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        GL11.glDepthMask(true)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        // GL11.glDisable(GL11.GL_BLEND);
    }

    protected fun renderImage(
        image: ResourceLocation?,
        x: Float,
        y: Float,
        z: Float,
        size: Float,
        alpha: Float = FMLClientHandler.instance().getClient().world.getStarBrightness(1.0f),
        shadow: Float = 0.0f
    ) {
        GL11.glPushMatrix()
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()

        GL11.glRotatef(x, 0.0f, 1.0f, 0.0f) // ������������ �� X
        GL11.glRotatef(y, 1.0f, 0.0f, 0.0f) // ������������ �� Y
        GL11.glRotatef(z, 0.0f, 0.0f, 1.0f) // ������������ �� Z
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(image)

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(-size.toDouble(), -100.0, size.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(size.toDouble(), -100.0, size.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(size.toDouble(), -100.0, -size.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(-size.toDouble(), -100.0, -size.toDouble()).tex(0.0, 0.0).endVertex()
        tessellator.draw()

        if (shadow > 0.0f) {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glColor4f(0.0f, 0.0f, 0.0f, shadow)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(-size.toDouble(), -100.0, size.toDouble()).endVertex()
            worldRenderer.pos(size.toDouble(), -100.0, size.toDouble()).endVertex()
            worldRenderer.pos(size.toDouble(), -100.0, -size.toDouble()).endVertex()
            worldRenderer.pos(-size.toDouble(), -100.0, -size.toDouble()).endVertex()
            tessellator.draw()
            GL11.glDisable(GL11.GL_BLEND)
        }
        GL11.glPopMatrix()
    }

    private fun renderStarsOnSky(count: Int, color: StarColor, starBrightness: Float) {
        for (i: Int in 0..<count) {
            val x: Float = color.getColor().getX() / 255f
            val y: Float = color.getColor().getY() / 255f
            val z: Float = color.getColor().getZ() / 255f

            GL11.glColor4f(1f, 1f, 1f, starBrightness)

            if (AsmodeusConfig.enableColorStars) GL11.glColor4f(x, y, z, starBrightness)

            // if (AsmodeusConfig.enableRotateStars) {
            // if(count % 5 == 0)
            GL11.glRotatef(-37.0f, 1f, 0f, 0f)

            // if(count % 3 == 0)
            GL11.glRotatef(-15.0f * i, 0.0f, 1.0f, 0.0f)
            // }
            GL11.glCallList(starList)
        }
    }

    private fun renderStars() {
        val rand: Random = Random(10842L)
        val tesselator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tesselator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        val star_count: Int = if (ConfigManagerCore.moreStars) 1000 else 500

        for (starIndex: Int in 0..<star_count) {
            var var4: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            var var6: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            var var8: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            val var10: Double = (0.15f + rand.nextFloat() * 0.1f).toDouble()
            var var12: Double = var4 * var4 + var6 * var6 + var8 * var8

            if (var12 < 1.0 && var12 > 0.01) {
                var12 = 1.0 / sqrt(var12)
                var4 *= var12
                var6 *= var12
                var8 *= var12
                val var14: Double =
                    var4 * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 150.0 + 130.0 else 100.0)
                val var16: Double =
                    var6 * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 150.0 + 130.0 else 100.0)
                val var18: Double =
                    var8 * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 150.0 + 130.0 else 100.0)
                val var20: Double = atan2(var4, var8)
                val var22: Double = sin(var20)
                val var24: Double = cos(var20)
                val var26: Double = atan2(sqrt(var4 * var4 + var8 * var8), var6)
                val var28: Double = sin(var26)
                val var30: Double = cos(var26)
                val var32: Double = rand.nextDouble() * Math.PI * 2.0
                val var34: Double = sin(var32)
                val var36: Double = cos(var32)

                for (var38: Int in 0..3) {
                    val var39: Double = 0.0
                    val var41: Double = ((var38 and 2) - 1) * var10
                    val var43: Double = ((var38 + 1 and 2) - 1) * var10
                    val var47: Double = var41 * var36 - var43 * var34
                    val var49: Double = var43 * var36 + var41 * var34
                    val var53: Double = var47 * var28 + var39 * var30
                    val var55: Double = var39 * var28 - var47 * var30
                    val var57: Double = var55 * var22 - var49 * var24
                    val var61: Double = var49 * var22 + var55 * var24

                    worldRenderer.pos(var14 + var57, var16 + var53, var18 + var61) /* .color(r, g, b, starBrightness) */
                        .endVertex()
                }
            }
        }

        tesselator.draw()
    }

    @Deprecated("")
    protected fun renderAtmo(tessellator: Tessellator, x: Float, y: Float, f10: Float, vec: Vector3?) {
        this.renderAtmo(tessellator, x, y, f10, if (vec == null) null else vec.toVec3())
    }

    protected fun renderAtmo(tessellator1: Tessellator, x: Float, y: Float, f10: Float, vec: Vec3d?) {
        if (vec != null && AsmodeusConfig.enableRenderAtmosphere) {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_ALPHA_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glRotatef(x, 0.0f, 1.0f, 0.0f) // ������������ �� X
            GL11.glRotatef(y, 1.0f, 0.0f, 0.0f) // ������������ �� Y

            val planetOrbitalDistance: Double = 5.0

            val dist: Double = ((-64.0 - 4 * (planetOrbitalDistance) / 2.0)
                    - (mc.player.posY.toFloat() / (mc.player.posY.toFloat())))
            val scalingMult: Double = 1.0 - 0.9 * (planetOrbitalDistance)

            val Xoffset: Float = ((System.currentTimeMillis() / 1000000.0 % 1)).toFloat()

            val f14: Float = 1f + Xoffset
            val f15: Float = 0f + Xoffset
            val f16: Float = f15
            val f17: Float = f14
            val color: FloatArray = FloatArray(4)

            color[0] = vec.x.toFloat()
            color[1] = vec.y.toFloat()
            color[2] = vec.z.toFloat()
            color[3] = 0.09f

            val worldRenderer: BufferBuilder = tessellator1.getBuffer()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)

            // worldRenderer.color(color[0], color[1], color[2], 0.09F);
            for (i: Int in 0..7) {
                renderTestWithUV(
                    worldRenderer,
                    color,
                    dist + i * scalingMult,
                    -f10.toDouble(),
                    -f10.toDouble(),
                    0.0,
                    0.0,
                    f14.toDouble(),
                    f15.toDouble(),
                    f16.toDouble(),
                    f17.toDouble()
                )
                renderTestWithUV(
                    worldRenderer,
                    color,
                    dist + i * scalingMult,
                    0.0,
                    0.0,
                    f10.toDouble(),
                    f10.toDouble(),
                    f14.toDouble(),
                    f15.toDouble(),
                    f16.toDouble(),
                    f17.toDouble()
                )
                renderTestWithUV(
                    worldRenderer,
                    color,
                    dist + i * scalingMult,
                    -f10.toDouble(),
                    0.0,
                    0.0,
                    f10.toDouble(),
                    f14.toDouble(),
                    f15.toDouble(),
                    f16.toDouble(),
                    f17.toDouble()
                )
                renderTestWithUV(
                    worldRenderer,
                    color,
                    dist + i * scalingMult,
                    0.0,
                    -f10.toDouble(),
                    f10.toDouble(),
                    0.0,
                    f14.toDouble(),
                    f15.toDouble(),
                    f16.toDouble(),
                    f17.toDouble()
                )
            }
            tessellator1.draw()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        }
    }

    protected fun renderSunAura(
        tessellator1: Tessellator,
        f10: Float,
        f18: Float,
        color: StarColor? = this.colorSunAura()
    ) {
        var f10: Float = f10
        var f18: Float = f18
        val worldRenderer: BufferBuilder = tessellator1.getBuffer()
        // GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        val vec3: Vec3d = this.mc.world.getSkyColor(mc.getRenderViewEntity(), this.ticks)
        var f1: Float = vec3.x.toFloat()
        var f2: Float = vec3.y.toFloat()
        var f3: Float = vec3.z.toFloat()
        var f6: Float
        var f7: Float
        var f8: Float
        var f9: Float = 0f
        val f11: Float

        if (mc.gameSettings.anaglyph) {
            val f4: Float = (f1 * 30.0f + f2 * 59.0f + f3 * 11.0f) / 100.0f
            val f5: Float = (f1 * 30.0f + f2 * 70.0f) / 100.0f
            f6 = (f1 * 30.0f + f3 * 70.0f) / 100.0f
            f1 = f4
            f2 = f5
            f3 = f6
        }

        afloat[0] = 255 / 255.0f
        afloat[1] = 194 / 255.0f
        afloat[2] = 180 / 255.0f
        afloat[3] = 0.5f
        if (color != null) {
            afloat[0] = color.getColor().getX() / 255.0f
            afloat[1] = color.getColor().getY() / 255.0f
            afloat[2] = color.getColor().getZ() / 255.0f
        }

        f6 = afloat[0]
        f7 = afloat[1]
        f8 = afloat[2]

        if (mc.gameSettings.anaglyph) {
            f9 = (f6 * 30.0f + f7 * 59.0f + f8 * 11.0f) / 100.0f
            f10 = (f6 * 30.0f + f7 * 70.0f) / 100.0f
            f11 = (f6 * 30.0f + f8 * 70.0f) / 100.0f
            f6 = f9
            f7 = f10
            f8 = f11
        }

        if (f18 > 1.0f) f18 = 1.0f

        var r: Float = f6 * f18
        var g: Float = f7 * f18
        var b: Float = f8 * f18
        var a: Float = afloat[3] * 2 / f18 - Minecraft.getMinecraft().world.getRainStrength(this.ticks)

        if (this.modeLight() != ModeLight.WITHOUT_SUN) {
            worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)

            worldRenderer.pos(0.0, 100.0, 0.0).color(r, g, b, a).endVertex()

            val b0: Byte = 16
            r = afloat[0] * f18
            g = afloat[1] * f18
            b = afloat[2] * f18
            a = 0.0f

            // Render sun aura
            worldRenderer.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(0.0, 100.0, -f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
            worldRenderer.pos(f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
            worldRenderer.pos(f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(0.0, 100.0, f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
            worldRenderer.pos(-f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(-f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
            worldRenderer.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()

            tessellator1.draw()
        }

        if (enableSmoothRender()) {
            GL11.glLineWidth(2f)
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        }

        if (enableLargeSunAura()) {
            worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)

            r = f6 * f18
            g = f7 * f18
            b = f8 * f18
            a = if (enableSmoothRender())
                afloat[3] * 2 / f18 - Minecraft.getMinecraft().world.getRainStrength(this.ticks)
            else
                afloat[3] * f18

            worldRenderer.pos(0.0, 100.0, 0.0).color(r, g, b, a).endVertex()

            r = afloat[0] * f18
            g = afloat[1] * f18
            b = afloat[2] * f18
            a = 0.0f

            // Render larger sun aura
            f10 = f10 + 5.0f
            val i: Int = if (enableSmoothRender()) 8 else 0
            f10 += i.toFloat()
            worldRenderer.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(0.0, 100.0, -f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
            f10 -= (i + i).toFloat()
            worldRenderer.pos(f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
            f10 += i.toFloat()
            worldRenderer.pos(f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(0.0, 100.0, f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
            f10 -= i.toFloat()
            worldRenderer.pos(-f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
            worldRenderer.pos(-f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
            worldRenderer.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()

            tessellator1.draw()
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
    }

    protected fun inWater(player: EntityPlayer): Boolean {
        val pos: BlockPos = player.getPosition()

        return mc.world.getBlockState(pos.up()).getMaterial() === Material.WATER
    }

    protected fun getCelestialAngle(daylength: Long): Float {
        return Utils.calculateCelestialAngle(
            this.mc.world.getWorldTime(),
            this.ticks,
            daylength.toInt().toFloat()
        ) * 360.0f
    }

    protected fun getDayLength(): Long {
        var daylength: Long = 24000L
        if (this.mc.world.provider is WorldProviderSpace) daylength =
            (this.mc.world.provider as WorldProviderSpace).getDayLength()
        else if (this.mc.world.provider is WE_WorldProviderSpace) daylength =
            (this.mc.world.provider as WE_WorldProviderSpace).getDayLength()

        return daylength
    }

    protected abstract fun rendererSky(tessellator: Tessellator?, buffer: BufferBuilder?, size: Float, ticks: Float)

    protected abstract fun enableBaseImages(): Boolean

    protected abstract fun modeLight(): ModeLight?

    protected abstract fun sunSize(): Float

    protected abstract fun enableStar(): Boolean

    protected abstract fun sunImage(): ResourceLocation?

    protected abstract fun colorSunAura(): StarColor?

    protected abstract fun getAtmosphereColor(): Vector3?

    open fun enableLargeSunAura(): Boolean {
        return true
    }

    open fun enableSmoothRender(): Boolean {
        return false
    }

    open fun enableRenderPlanet(): Boolean {
        return true
    }

    open fun expandSizeAura(): Int {
        return 0
    }

    protected open fun getStarCount(): StarCount {
        return StarCount.MEDIUM
    }

    companion object {
        private val sunTexture = ResourceLocation("textures/environment/sun.png")
        private val pumpkinsunTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/pumkinsun.png"
        )

        private val lmcTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/lmc.png"
        )
        private val smcTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/smc.png"
        )
        private val andromedaTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/andromeda.png"
        )
        private val barnardaloopTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/barnardaloop.png"
        )
        private val eta_carinaeTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/eta_carinae.png"
        )
        private val nebulaTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/nebula.png"
        )
        private val pleiadesTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/pleiades.png"
        )
        private val triangulumTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/triangulum.png"
        )

        private val milkyway_smallTexture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/milkyway_small.png"
        )
        private val m110Texture = ResourceLocation(
            AsmodeusCore.ASSET_PREFIX,
            "textures/environment/background/M110.png"
        )

        @JvmField
        protected val galaxyTextures: Array<ResourceLocation?> = arrayOfNulls<ResourceLocation>(6)

        private var displayListIsGen = false

        @JvmField
var starList: Int = 0

        @JvmField
var glSkyList: Int = 0

        @JvmField
var glSkyList2: Int = 0

        @JvmStatic
        private fun renderTestWithUV(
            buff: BufferBuilder, color: FloatArray, yMax: Double, xMin: Double, zMin: Double,
            xMax: Double, zMax: Double, uMin: Double, uMax: Double, vMin: Double, vMax: Double
        ) {
            buff.pos(xMin, yMax, zMin).tex(uMin, vMin).color(color[0], color[1], color[2], color[3]).endVertex()
            buff.pos(xMin, yMax, zMax).tex(uMin, vMax).color(color[0], color[1], color[2], color[3]).endVertex()
            buff.pos(xMax, yMax, zMax).tex(uMax, vMax).color(color[0], color[1], color[2], color[3]).endVertex()
            buff.pos(xMax, yMax, zMin).tex(uMax, vMin).color(color[0], color[1], color[2], color[3]).endVertex()
        }
    }
}
