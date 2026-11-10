package galaxyspace.systems.SolarSystem.planets.overworld.render.tile

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import com.google.common.collect.ImmutableList
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicFarm
import galaxyspace.core.util.GSUtils
import com.google.common.base.Function
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import java.lang.Exception
import java.lang.RuntimeException

open class TileEntityHydroponicFarmRenderer : TileEntitySpecialRenderer<TileEntityHydroponicFarm>() {
    private var main: OBJModel.OBJBakedModel? = null
    private var block: Block? = null

    private fun updateModels() {
        if (main == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation(GalaxySpace.MODID, "hydroponic_farm.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                main = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("ferma_2"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        farm: TileEntityHydroponicFarm,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        partial: Float
    ) {
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F, 240F);

        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()

        GlStateManager.enableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableBlend()

        this.updateModels()

        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())

        GlStateManager.pushMatrix()
        RenderHelper.disableStandardItemLighting()
        block = farm.getPlant()
        val factor: Double = 0.65
        GlStateManager.scale(factor, factor, factor)
        GlStateManager.translate(0.8f, -0.45f, 0.8f)
        if (block != null) renderWheat(
            block!!,
            farm.getMetaPlant(),
            0.0 / factor - 0.5,
            0.65 / factor,
            0.0 / factor - 0.5
        )
        RenderHelper.enableStandardItemLighting()
        GlStateManager.popMatrix()

        GlStateManager.translate(0.5f, -1.0f, 0.5f)
        GlStateManager.scale(0.01f, 0.01f, 0.01f)

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        ClientUtil.drawBakedModel((main)!!)

        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
    }

    private fun renderWheat(block: Block, meta: Int, x: Double, y: Double, z: Double) {
        var sprite: TextureAtlasSprite

        sprite = (GSUtils.getBlockTexture(block, "_stage_" + meta))!!
        if (block === Blocks.MELON_STEM || block === Blocks.PUMPKIN_STEM) {
            val texMap: TextureMap = Minecraft.getMinecraft().getTextureMapBlocks()
            sprite = texMap.getAtlasSprite("minecraft" + ":blocks/melon_stem_disconnected")
            GL11.glColor3f(1.0f, 1.0f, 0.0f)
        }
        if (block === Blocks.CACTUS) {
            val texMap: TextureMap = Minecraft.getMinecraft().getTextureMapBlocks()
            sprite = texMap.getAtlasSprite("minecraft" + ":blocks/cactus_side")
            GL11.glColor3f(1.0f, 1.0f, 1.0f)
        }
        if (block === Blocks.BROWN_MUSHROOM) {
            val texMap: TextureMap = Minecraft.getMinecraft().getTextureMapBlocks()
            sprite = texMap.getAtlasSprite("minecraft" + ":blocks/mushroom_brown")
            GL11.glColor3f(1.0f, 1.0f, 1.0f)
        }
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        val d3: Double = sprite.getMinU().toDouble()
        val d4: Double = sprite.getMinV().toDouble()
        val d5: Double = sprite.getMaxU().toDouble()
        val d6: Double = sprite.getMaxV().toDouble()

        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()


        var d7: Double = x + 0.5 - 0.25
        var d8: Double = x + 0.5 + 0.25
        var d9: Double = z + 0.5 - 0.5
        var d10: Double = z + 0.5 + 0.5

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        bufferbuilder.pos(d7, y + 1.0, d9).tex(d3, d4).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d9).tex(d3, d6).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d10).tex(d5, d6).endVertex()
        bufferbuilder.pos(d7, y + 1.0, d10).tex(d5, d4).endVertex()
        bufferbuilder.pos(d7, y + 1.0, d10).tex(d3, d4).endVertex()

        bufferbuilder.pos(d7, y + 0.0, d10).tex(d3, d6).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d9).tex(d5, d6).endVertex()
        bufferbuilder.pos(d7, y + 1.0, d9).tex(d5, d4).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d10).tex(d3, d4).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d10).tex(d3, d6).endVertex()

        bufferbuilder.pos(d8, y + 0.0, d9).tex(d5, d6).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d9).tex(d5, d4).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d9).tex(d3, d4).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d9).tex(d3, d6).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d10).tex(d5, d6).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d10).tex(d5, d4).endVertex()

        d7 = x + 0.5 - 0.5
        d8 = x + 0.5 + 0.5
        d9 = z + 0.5 - 0.25
        d10 = z + 0.5 + 0.25

        bufferbuilder.pos(d7, y + 1.0, d9).tex(d3, d4).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d9).tex(d3, d6).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d9).tex(d5, d6).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d9).tex(d5, d4).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d9).tex(d3, d4).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d9).tex(d3, d6).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d9).tex(d5, d6).endVertex()
        bufferbuilder.pos(d7, y + 1.0, d9).tex(d5, d4).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d10).tex(d3, d4).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d10).tex(d3, d6).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d10).tex(d5, d6).endVertex()
        bufferbuilder.pos(d7, y + 1.0, d10).tex(d5, d4).endVertex()
        bufferbuilder.pos(d7, y + 1.0, d10).tex(d3, d4).endVertex()
        bufferbuilder.pos(d7, y + 0.0, d10).tex(d3, d6).endVertex()
        bufferbuilder.pos(d8, y + 0.0, d10).tex(d5, d6).endVertex()
        bufferbuilder.pos(d8, y + 1.0, d10).tex(d5, d4).endVertex()
        tessellator.draw()
    }
}
