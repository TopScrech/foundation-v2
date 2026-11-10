package micdoodle8.mods.galacticraft.core.client

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import java.util.ArrayList
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.cos
import kotlin.math.sin

object FootprintRenderer {
    @JvmField
    var footprints: MutableMap<Long, MutableList<Footprint?>> = ConcurrentHashMap<Long, MutableList<Footprint?>>()
    private val footprintTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/footprint.png")

    @JvmStatic
    fun renderFootprints(player: EntityPlayer, partialTicks: Float) {
        val dimActive: Int = GCCoreUtil.getDimensionID(player.world)
        val footprintsToDraw: MutableList<Footprint> = LinkedList<Footprint>()

        for (footprintList in footprints.values) {
            for (footprint in footprintList) {
                if ((footprint)!!.dimension == dimActive) {
                    footprintsToDraw.add((footprint)!!)
                }
            }
        }

        if (footprintsToDraw.isEmpty()) {
            return
        }

        GlStateManager.pushMatrix()
        val interpPosX: Double = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val interpPosY: Double = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val interpPosZ: Double = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(footprintTexture)

        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableCull()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        val tessellator: Tessellator = Tessellator.getInstance()
        val f7: Float = 1.0f
        val f6: Float = 0.0f
        val f8: Float = 0.0f
        val f9: Float = 1.0f

        val f10: Float = 0.4f
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
        val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
        val sensorGlasses: Boolean = OverlaySensorGlasses.overrideMobTexture()
        if (sensorGlasses) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f)
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        for (footprint: Footprint in footprintsToDraw) {
            GL11.glPushMatrix()

            if (!sensorGlasses) {
                val j: Int = footprint.lightmapVal % 65536
                val k: Int = footprint.lightmapVal / 65536
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat(), k.toFloat())
            }

            val ageScale: Float = footprint.age / Footprint.MAX_AGE.toFloat()
            val worldRenderer: BufferBuilder = tessellator.getBuffer()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

            val f11: Float = ((footprint.position)!!.x - interpPosX).toFloat()
            val f12: Float = ((footprint.position)!!.y - interpPosY).toFloat() + 0.001f
            val f13: Float = ((footprint.position)!!.z - interpPosZ).toFloat()

            GL11.glTranslatef(f11, f12, f13)

            val brightness: Int = (100 + ageScale * 155).toInt()
            //                    worldRenderer.putBrightness4(brightness, brightness, brightness, brightness);
            GlStateManager.color(1f - ageScale, 1f - ageScale, 1f - ageScale, 1f - ageScale)
            val footprintScale: Double = 0.5
            worldRenderer.pos(
                sin((45 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale,
                0.0,
                cos((45 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale
            ).tex(f7.toDouble(), f9.toDouble()).endVertex()
            worldRenderer.pos(
                sin((135 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale,
                0.0,
                cos((135 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale
            ).tex(f7.toDouble(), f8.toDouble()).endVertex()
            worldRenderer.pos(
                sin((225 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale,
                0.0,
                cos((225 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale
            ).tex(f6.toDouble(), f8.toDouble()).endVertex()
            worldRenderer.pos(
                sin((315 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale,
                0.0,
                cos((315 - footprint.rotation) / Constants.RADIANS_TO_DEGREES_D) * footprintScale
            ).tex(f6.toDouble(), f9.toDouble()).endVertex()

            tessellator.draw()
            GlStateManager.popMatrix()
        }

        if (sensorGlasses) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)
        }

        GlStateManager.popMatrix()
    }

    @JvmStatic
    fun addFootprint(chunkKey: Long, footprint: Footprint) {
        var footprintList: MutableList<Footprint?>? = footprints.get(chunkKey)

        if (footprintList == null) {
            footprintList = ArrayList<Footprint?>()
        }

        footprintList.add(
            Footprint(
                footprint.dimension,
                footprint.position,
                footprint.rotation,
                footprint.owner,
                footprint.lightmapVal
            )
        )
        footprints.put(chunkKey, footprintList)
    }

    @JvmStatic
    fun addFootprint(
        chunkKey: Long,
        dimension: Int,
        position: Vector3?,
        rotation: Float,
        owner: String?,
        lightmapVal: Int
    ) {
        addFootprint(chunkKey, Footprint(dimension, position, rotation, owner, lightmapVal))
    }

    @JvmStatic
    fun setFootprints(chunkKey: Long, prints: MutableList<Footprint?>?) {
        var footprintList: MutableList<Footprint?>? = footprints.get(chunkKey)

        if (footprintList == null) {
            footprintList = ArrayList<Footprint?>()
        }

        val i: MutableIterator<Footprint> = (footprintList.iterator() as MutableIterator<Footprint>)
        while (i.hasNext()) {
            val print: Footprint = i.next()
            if (print.owner != FMLClientHandler.instance().getClient().player.getName()) {
                i.remove()
            }
        }

        footprintList.addAll(prints!!)
        footprints.put(chunkKey, footprintList)
    }
}
