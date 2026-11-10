package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.tile.ReceiverMode
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReceiver
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class TileEntityBeamReceiverRenderer : TileEntitySpecialRenderer<TileEntityBeamReceiver>() {
    private fun updateModels() {
        if (reflectorModelMain == null) {
            try {
                val model: IModel = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "block/receiver.obj"
                    )
                )
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                reflectorModelMain = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Main"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                reflectorModelReceiver = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Receiver"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                reflectorModelRing = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Ring"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        tile: TileEntityBeamReceiver,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        if (tile.facing == null) {
            return
        }

        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat(), z.toFloat() + 0.5f)
        GlStateManager.scale(0.85f, 0.85f, 0.85f)

        when (tile.facing) {
            EnumFacing.DOWN -> {
                GlStateManager.translate(0.7f, -0.15f, 0.0f)
                GlStateManager.rotate(90f, 0f, 0f, 1f)
            }

            EnumFacing.UP -> {
                GlStateManager.translate(-0.7f, 1.3f, 0.0f)
                GlStateManager.rotate(-90f, 0f, 0f, 1f)
            }

            EnumFacing.EAST -> {
                GlStateManager.translate(0.7f, -0.15f, 0.0f)
                GlStateManager.rotate(180f, 0f, 1f, 0f)
            }

            EnumFacing.SOUTH -> {
                GlStateManager.translate(0.0f, -0.15f, 0.7f)
                GlStateManager.rotate(90f, 0f, 1f, 0f)
            }

            EnumFacing.WEST -> {
                GlStateManager.translate(-0.7f, -0.15f, 0.0f)
                GlStateManager.rotate(0f, 0f, 1f, 0f)
            }

            EnumFacing.NORTH -> {
                GlStateManager.translate(0.0f, -0.15f, -0.7f)
                GlStateManager.rotate(270f, 0f, 1f, 0f)
            }

            else -> {
                GlStateManager.popMatrix()
                return
            }
        }

        this.updateModels()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        ClientUtil.drawBakedModel((reflectorModelMain)!!)

        val color: Int

        if (tile.modeReceive == ReceiverMode.RECEIVE.ordinal) {
            color = ColorUtil.to32BitColor(255, 0, 204, 0)
        } else if (tile.modeReceive == ReceiverMode.EXTRACT.ordinal) {
            color = ColorUtil.to32BitColor(255, 0, 0, 153)
        } else {
            color = ColorUtil.to32BitColor(255, 25, 25, 25)
        }

        GlStateManager.disableTexture2D()
        GlStateManager.disableCull()
        ClientUtil.drawBakedModelColored((reflectorModelReceiver)!!, color)
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        val dX: Float = 0.34772f
        val dY: Float = 0.75097f
        val dZ: Float = 0.0f
        GlStateManager.translate(dX, dY, dZ)

        if (tile.modeReceive != ReceiverMode.UNDEFINED.ordinal) {
            GlStateManager.rotate((-tile.ticks * 50).toFloat(), 1f, 0f, 0f)
        }

        GlStateManager.translate(-dX, -dY, -dZ)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        ClientUtil.drawBakedModel((reflectorModelRing)!!)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    companion object {
        private var reflectorModelMain: OBJModel.OBJBakedModel? = null
        private var reflectorModelReceiver: OBJModel.OBJBakedModel? = null
        private var reflectorModelRing: OBJModel.OBJBakedModel? = null
    }
}
