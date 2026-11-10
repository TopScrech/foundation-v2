package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity

import java.util.ArrayList
import java.util.Random

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.perlin.NoiseModule
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import java.util.*

open class RenderAstroMiner constructor(renderManager: RenderManager) : Render<EntityAstroMiner>(renderManager) {
    private var lastPartTime = 0f

    private var mainModel: OBJModel.OBJBakedModel? = null
    private var hoverPadMain: OBJModel.OBJBakedModel? = null
    private var hoverPadGlow: OBJModel.OBJBakedModel? = null
    private var mainModelInactive: OBJModel.OBJBakedModel? = null
    private var modellaser1: OBJModel.OBJBakedModel? = null
    private var modellaser3: OBJModel.OBJBakedModel? = null
    private var modellasergl: OBJModel.OBJBakedModel? = null

    private val wobbleX: NoiseModule
    private val wobbleY: NoiseModule
    private val wobbleZ: NoiseModule
    private val wobbleXX: NoiseModule
    private val wobbleYY: NoiseModule
    private val wobbleZZ: NoiseModule

    private fun updateModels() {
        if (this.mainModel == null) {
            try {
                var model: IModel = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "astro_miner_full.obj"
                    )
                )
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }
                this.mainModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Hull", "Lasers"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.hoverPadMain = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("HoverPad"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.hoverPadGlow = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Glow"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.modellaser1 = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Main_Laser_Front"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.modellaser3 = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Main_Laser_Center"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.modellasergl = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Main_Laser_Left_Guard"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel

                model = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "astro_miner_full_off.obj"
                    )
                )
                this.mainModelInactive = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Hull", "Lasers", "HoverPad"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    init {
        this.shadowSize = 2f

        val rand: Random = Random()
        this.wobbleX = Gradient(rand.nextLong(), 2, 1f)
        this.wobbleX.amplitude = 0.5f
        this.wobbleX.frequencyX = 0.025f

        this.wobbleY = Gradient(rand.nextLong(), 2, 1f)
        this.wobbleY.amplitude = 0.6f
        this.wobbleY.frequencyX = 0.025f

        this.wobbleZ = Gradient(rand.nextLong(), 2, 1f)
        this.wobbleZ.amplitude = 0.1f
        this.wobbleZ.frequencyX = 0.025f

        this.wobbleXX = Gradient(rand.nextLong(), 2, 1f)
        this.wobbleXX.amplitude = 0.1f
        this.wobbleXX.frequencyX = 0.8f

        this.wobbleYY = Gradient(rand.nextLong(), 2, 1f)
        this.wobbleYY.amplitude = 0.15f
        this.wobbleYY.frequencyX = 0.8f

        this.wobbleZZ = Gradient(rand.nextLong(), 2, 1f)
        this.wobbleZZ.amplitude = 0.04f
        this.wobbleZZ.frequencyX = 0.8f
    }

    override fun doRender(
        astroMiner: EntityAstroMiner,
        x: Double,
        y: Double,
        z: Double,
        f: Float,
        partialTickTime: Float
    ) {
        val ais: Int = astroMiner.AIstate
        val active: Boolean = ais > EntityAstroMiner.Companion.AISTATE_ATBASE
        val time: Float = astroMiner.ticksExisted + partialTickTime
        val sinOfTheTime: Float = (MathHelper.sin(time / 4) + 1f) / 4f + 0.5f
        val wx: Float = if (active) this.wobbleX.getNoise(time) + this.wobbleXX.getNoise(time) else 0f
        val wy: Float = if (active) this.wobbleY.getNoise(time) + this.wobbleYY.getNoise(time) else 0f
        val wz: Float = if (active) this.wobbleZ.getNoise(time) + this.wobbleZZ.getNoise(time) else 0f
        var partTime: Float = partialTickTime - this.lastPartTime
        this.lastPartTime = partialTickTime

        while (partTime < 0) {
            partTime += 1f
        }

        this.updateModels()
        this.bindEntityTexture(astroMiner)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        GlStateManager.enableRescaleNormal()
        GlStateManager.pushMatrix()

        val rotPitch: Float =
            astroMiner.prevRotationPitch + (astroMiner.rotationPitch - astroMiner.prevRotationPitch) * partialTickTime
        val rotYaw: Float =
            astroMiner.prevRotationYaw + (astroMiner.rotationYaw - astroMiner.prevRotationYaw) * partialTickTime

        GlStateManager.translate(x.toFloat(), y.toFloat() + 1.4f, z.toFloat())
        var partBlock: Float

        when (astroMiner.facing) {
            EnumFacing.DOWN -> partBlock = (astroMiner.posY % 2.0).toFloat()
            EnumFacing.UP -> partBlock = 1f - (astroMiner.posY % 2.0).toFloat()
            EnumFacing.NORTH -> partBlock = (astroMiner.posZ % 2.0).toFloat()
            EnumFacing.SOUTH -> partBlock = 1f - (astroMiner.posZ % 2.0).toFloat()
            EnumFacing.WEST -> partBlock = (astroMiner.posX % 2.0).toFloat()
            EnumFacing.EAST -> partBlock = 1f - (astroMiner.posX % 2.0).toFloat()
            else -> partBlock = 0f
        }
        partBlock /= 0.06f

        GlStateManager.rotate(rotYaw + 180f, 0f, 1f, 0f)

        if (rotPitch != 0f) {
            GlStateManager.translate(-0.65f, -0.65f, 0f)
            GlStateManager.rotate(rotPitch / 4f, 1f, 0f, 0f)
            GlStateManager.translate(0.65f, 0.65f, 0f)
        }

        GlStateManager.translate(0f, -0.42f, 0.28f)
        GlStateManager.scale(0.0495f, 0.0495f, 0.0495f)
        GlStateManager.translate(wx, wy, wz)

        if (active) {
            ClientUtil.drawBakedModel((this.mainModel)!!)
            this.renderLaserModel(astroMiner.retraction)

            val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
            val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
            GlStateManager.disableLighting()
            GlStateManager.color(sinOfTheTime, sinOfTheTime, sinOfTheTime, 1.0f)
            ClientUtil.drawBakedModel((this.hoverPadMain)!!)

            GlStateManager.disableCull()
            GlStateManager.disableAlpha()
            GlStateManager.depthMask(false)
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GlStateManager.enableBlend()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GlStateManager.color(sinOfTheTime, sinOfTheTime, sinOfTheTime, 0.6f)
            ClientUtil.drawBakedModel((this.hoverPadGlow)!!)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

            if (ais < EntityAstroMiner.Companion.AISTATE_DOCKING) {
                //This is the scanning lasers:
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(scanTexture)
                val tess: Tessellator = Tessellator.getInstance()
                GlStateManager.color(0f, 0.6f, 1.0f, 0.2f)
                val worldRenderer: BufferBuilder = tess.getBuffer()
                val scanProgress: Float = (MathHelper.cos(partBlock * 0.012f * 6.283f)) * 0.747f
                val scanAngle: Float = 0.69866f - scanProgress * scanProgress
                val scanEndX: Float = 38.77f * MathHelper.sin(scanAngle)
                val scanEndY: Float = 32f
                var scanEndZ: Float = 38.77f * MathHelper.cos(scanAngle)
                scanEndZ += 20f
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(15.6, -0.6, -20.0).tex(0.0, 0.0).endVertex()
                worldRenderer.pos((15.6f + scanEndX).toDouble(), (scanEndY - 0.6f).toDouble(), -scanEndZ.toDouble())
                    .tex(1.0, 0.0).endVertex()
                worldRenderer.pos((15.6f + scanEndX).toDouble(), (-0.6f - scanEndY).toDouble(), -scanEndZ.toDouble())
                    .tex(1.0, 1.0).endVertex()
                worldRenderer.pos(15.6, -0.7, -20.0).tex(0.0, 1.0).endVertex()
                tess.draw()
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(-15.6, -0.6, -20.0).tex(0.0, 0.0).endVertex()
                worldRenderer.pos((-15.6f - scanEndX).toDouble(), (scanEndY - 0.6f).toDouble(), -scanEndZ.toDouble())
                    .tex(1.0, 0.0).endVertex()
                worldRenderer.pos((-15.6f - scanEndX).toDouble(), (-0.6f - scanEndY).toDouble(), -scanEndZ.toDouble())
                    .tex(1.0, 1.0).endVertex()
                worldRenderer.pos(-15.6, -0.7, -20.0).tex(0.0, 1.0).endVertex()
                tess.draw()

                var removeCount: Int = 0
                var afterglowCount: Int = 0
                GlStateManager.popMatrix()
                GlStateManager.pushMatrix()
                GlStateManager.translate(
                    (x - astroMiner.posX).toFloat(),
                    (y - astroMiner.posY).toFloat(),
                    (z - astroMiner.posZ).toFloat()
                )

                for (blockTime: Int in ArrayList<Int>(astroMiner.laserTimes)) {
                    if (blockTime < astroMiner.ticksExisted - 19) {
                        removeCount++
                    } else if (blockTime < astroMiner.ticksExisted - 3) {
                        afterglowCount++
                    }
                }
                if (removeCount > 0) {
                    astroMiner.removeLaserBlocks(removeCount)
                }
                var count: Int = 0
                for (blockLaser: BlockVec3 in ArrayList<BlockVec3>(astroMiner.laserBlocks)) {
                    if (count < afterglowCount) {
                        var fade: Int = astroMiner.ticksExisted - astroMiner.laserTimes.get(count) - 8
                        if (fade < 0) {
                            fade = 0
                        }
                        this.doAfterGlow(blockLaser, fade)
                    } else {
                        this.doLaser(astroMiner, blockLaser)
                    }
                    count++
                }
                if (astroMiner.retraction > 0f) {
                    astroMiner.retraction -= RETRACTIONSPEED * partTime
                    if (astroMiner.retraction < 0f) {
                        astroMiner.retraction = 0f
                    }
                }
                GlStateManager.popMatrix()
            } else {
                if (astroMiner.retraction < 1f) {
                    astroMiner.retraction += RETRACTIONSPEED * partTime
                    if (astroMiner.retraction > 1f) {
                        astroMiner.retraction = 1f
                    }
                }
                GlStateManager.popMatrix()
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.enableCull()
            GlStateManager.enableAlpha()
            GlStateManager.enableLighting()
            GlStateManager.depthMask(true)
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)
        } else {
            this.bindEntityTexture(astroMiner)
            ClientUtil.drawBakedModel((this.mainModelInactive)!!)
            this.renderLaserModel(astroMiner.retraction)
            if (astroMiner.retraction < 1f) {
                astroMiner.retraction += RETRACTIONSPEED * partTime
                if (astroMiner.retraction > 1f) {
                    astroMiner.retraction = 1f
                }
            }
            GlStateManager.popMatrix()
        }
    }

    private fun doAfterGlow(blockLaser: BlockVec3, level: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(blockLaser.x.toFloat(), blockLaser.y.toFloat(), blockLaser.z.toFloat())
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GlStateManager.color(1.0f, 0.7f, 0.7f, 0.016667f * (12 - level))
        val cA: Float = -0.01f
        val cB: Float = 1.01f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cA.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cB.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 0.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cB.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 1.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 1.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(1.0, cA.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(1.0, 1.0, cA.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cA.toDouble(), 1.0, cA.toDouble()).tex(1.0, 1.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(1.0, cA.toDouble(), 1.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), 1.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), 1.0, 1.0).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(1.0, 1.0, 1.0).tex(0.0, 1.0).endVertex()
        tess.draw()
        GlStateManager.popMatrix()
    }

    private fun doLaser(entity: EntityAstroMiner, blockLaser: BlockVec3) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(blockLaser.x.toFloat(), blockLaser.y.toFloat(), blockLaser.z.toFloat())
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GlStateManager.color(1.0f, 0.7f, 0.7f, 0.2f)
        val cA: Float = -0.01f
        val cB: Float = 1.01f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cA.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cB.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 0.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cB.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 1.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cA.toDouble(), cB.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cB.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(cB.toDouble(), cB.toDouble(), cA.toDouble()).tex(0.0, 1.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), cA.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(1.0, cA.toDouble(), cA.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(1.0, 1.0, cA.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(cA.toDouble(), 1.0, cA.toDouble()).tex(1.0, 1.0).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(1.0, cA.toDouble(), 1.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(cA.toDouble(), cA.toDouble(), 1.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(cA.toDouble(), 1.0, 1.0).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(1.0, 1.0, 1.0).tex(0.0, 1.0).endVertex()
        tess.draw()

        GlStateManager.color(1.0f, 0.79f, 0.79f, 0.17f)
        val bb: Float = 1.7f
        val cc: Float = 0.4f
        val radiansYaw: Float = entity.rotationYaw / Constants.RADIANS_TO_DEGREES
        val radiansPitch: Float = entity.rotationPitch / Constants.RADIANS_TO_DEGREES / 4f
        var mainLaserX: Float = bb * MathHelper.sin(radiansYaw) * MathHelper.cos(radiansPitch)
        var mainLaserY: Float = cc + bb * MathHelper.sin(radiansPitch)
        var mainLaserZ: Float = bb * MathHelper.cos(radiansYaw) * MathHelper.cos(radiansPitch)

        mainLaserX += (entity.posX - blockLaser.x).toFloat()
        mainLaserY += (entity.posY - blockLaser.y).toFloat()
        mainLaserZ += (entity.posZ - blockLaser.z).toFloat()

        val xD: Float = mainLaserX - 0.5f
        val yD: Float = mainLaserY - 0.5f
        val zD: Float = mainLaserZ - 0.5f
        val xx: Float
        val yy: Float
        val zz: Float

        if (entity.facing.getIndex() > EnumFacing.SOUTH.getIndex()) {
            xx = if (xD < 0) cA else cB
            this.drawLaserX(mainLaserX, mainLaserY, mainLaserZ, xx, 0.5f, 0.5f)
        } else if (entity.facing.getIndex() <= EnumFacing.UP.getIndex()) {
            yy = if (yD < 0) cA else cB
            this.drawLaserY(mainLaserX, mainLaserY, mainLaserZ, 0.5f, yy, 0.5f)
        } else {
            zz = if (zD < 0) cA else cB
            this.drawLaserZ(mainLaserX, mainLaserY, mainLaserZ, 0.5f, 0.5f, zz)
        }

        GlStateManager.popMatrix()
    }

    private fun drawLaserX(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float) {
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(x1.toDouble(), (y1 - 0.01f).toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 - LSIZE).toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 + LSIZE).toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x1.toDouble(), (y1 + 0.01f).toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(x1.toDouble(), (y1 - 0.01f).toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 - LSIZE).toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 + LSIZE).toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x1.toDouble(), (y1 + 0.01f).toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(x1.toDouble(), (y1 - 0.01f).toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 - LSIZE).toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 - LSIZE).toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x1.toDouble(), (y1 - 0.01f).toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(x1.toDouble(), (y1 + 0.01f).toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 + LSIZE).toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 + LSIZE).toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x1.toDouble(), (y1 + 0.01f).toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        tess.draw()
    }

    private fun drawLaserY(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float) {
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 - 0.01f).toDouble(), y1.toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), y2.toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), y2.toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x1 + 0.01f).toDouble(), y1.toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 - 0.01f).toDouble(), y1.toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), y2.toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), y2.toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x1 + 0.01f).toDouble(), y1.toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 - 0.01f).toDouble(), y1.toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), y2.toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), y2.toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x1 - 0.01f).toDouble(), y1.toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 + 0.01f).toDouble(), y1.toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), y2.toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), y2.toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos((x1 + 0.01f).toDouble(), y1.toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        tess.draw()
    }

    private fun drawLaserZ(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float) {
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 - 0.01f).toDouble(), (y1 - 0.01f).toDouble(), z1.toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), (y2 - LSIZE).toDouble(), z2.toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), (y2 + LSIZE).toDouble(), z2.toDouble()).endVertex()
        worldRenderer.pos((x1 - 0.01f).toDouble(), (y1 + 0.01f).toDouble(), z1.toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 + 0.01f).toDouble(), (y1 - 0.01f).toDouble(), z1.toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), (y2 - LSIZE).toDouble(), z2.toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), (y2 + LSIZE).toDouble(), z2.toDouble()).endVertex()
        worldRenderer.pos((x1 + 0.01f).toDouble(), (y1 + 0.01f).toDouble(), z1.toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos((x1 - 0.01f).toDouble(), (y1 - 0.01f).toDouble(), z1.toDouble()).endVertex()
        worldRenderer.pos((x2 - LSIZE).toDouble(), (y2 - LSIZE).toDouble(), z2.toDouble()).endVertex()
        worldRenderer.pos((x2 + LSIZE).toDouble(), (y2 - LSIZE).toDouble(), z2.toDouble()).endVertex()
        worldRenderer.pos((x1 + 0.01f).toDouble(), (y1 - 0.01f).toDouble(), z1.toDouble()).endVertex()
        tess.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(x1.toDouble(), (y1 + 0.01f).toDouble(), (z1 + 0.01f).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 + LSIZE).toDouble(), (z2 + LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x2.toDouble(), (y2 + LSIZE).toDouble(), (z2 - LSIZE).toDouble()).endVertex()
        worldRenderer.pos(x1.toDouble(), (y1 + 0.01f).toDouble(), (z1 - 0.01f).toDouble()).endVertex()
        tess.draw()
    }

    private fun renderLaserModel(retraction: Float) {
        var laserretraction: Float = retraction / 0.8f
        if (laserretraction > 1f) {
            laserretraction = 1f
        }
        var guardmovement: Float = (retraction - 0.6f) / 0.4f * 1.875f
        if (guardmovement < 0f) {
            guardmovement = 0f
        }
        GlStateManager.pushMatrix()
        var zadjust: Float = laserretraction * 5f
        var yadjust: Float = zadjust

        if (yadjust > 0.938f) {
            yadjust = 0.938f
            zadjust = (zadjust - yadjust) * 2.5f + yadjust
        }
        GlStateManager.translate(0f, yadjust, zadjust)
        ClientUtil.drawBakedModel((this.modellaser1)!!)
        if (yadjust == 0.938f) {
            //Do not move laser centre into body
            GlStateManager.translate(0f, 0f, -zadjust + 0.938f)
        }
        ClientUtil.drawBakedModel((this.modellaser3)!!)
        GlStateManager.popMatrix()
        GlStateManager.pushMatrix()
        GlStateManager.translate(guardmovement, 0f, 0f)
        ClientUtil.drawBakedModel((this.modellasergl)!!)
        GlStateManager.translate(-2 * guardmovement + 8.75f, 0f, 0f)
        ClientUtil.drawBakedModel((this.modellasergl)!!)
        GlStateManager.popMatrix()
    }

    override fun getEntityTexture(entity: EntityAstroMiner): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun shouldRender(
        miner: EntityAstroMiner,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        return miner.isInRangeToRender3d(camX, camY, camZ)
    }

    companion object {
        private const val LSIZE = 0.12f
        private const val RETRACTIONSPEED = 0.02f

        @JvmField
        var scanTexture: ResourceLocation

        init {
            scanTexture = ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/misc/gradient.png")
        }
    }
}
