package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile

import com.google.common.base.Function
import com.google.common.collect.ImmutableMap
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class TileEntityMinerBaseRenderer : TileEntitySpecialRenderer<TileEntityMinerBase>() {
    open fun getBakedModel(): IBakedModel {
        if (minerBaseModelBaked == null) {
            var minerBaseModel: OBJModel
            try {
                minerBaseModel = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "minerbase0.obj"
                    )
                ) as OBJModel
                minerBaseModel =
                    minerBaseModel.process(ImmutableMap.of<String?, String?>("ambient", "false")) as OBJModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                Function { location: ResourceLocation? ->
                    Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                }
            minerBaseModelBaked = minerBaseModel.bake(
                TRSRTransformation.identity(),
                DefaultVertexFormats.ITEM,
                spriteFunction
            ) as OBJModel.OBJBakedModel
        }
        return minerBaseModelBaked!!
    }

    override fun render(
        tile: TileEntityMinerBase,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        if (!tile.isMaster) {
            return
        }

        var j: Int = 0
        var k: Int = 0
        var light: Int = tile.getWorld().getCombinedLight(tile.getPos(), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().add(1, 0, 0), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().add(0, 0, 1), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().add(1, 0, 1), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().up(), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().add(1, 1, 0), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().add(0, 1, 1), 0)
        j += light % 65536
        k += light / 65536
        light = tile.getWorld().getCombinedLight(tile.getPos().add(1, 1, 1), 0)
        j += light % 65536
        k += light / 65536
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 8.0f, k / 8.0f)

        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        GL11.glTranslatef(x.toFloat() + 1f, y.toFloat() + 1f, z.toFloat() + 1f)
        GL11.glScalef(0.05f, 0.05f, 0.05f)

        when (tile.facing) {
            EnumFacing.SOUTH -> GL11.glRotatef(180f, 0f, 1f, 0f)
            EnumFacing.WEST -> GL11.glRotatef(90f, 0f, 1f, 0f)
            EnumFacing.NORTH -> {}
            EnumFacing.EAST -> GL11.glRotatef(270f, 0f, 1f, 0f)
        
else -> {}
}

        RenderHelper.disableStandardItemLighting()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        val tessellator: Tessellator = Tessellator.getInstance()
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
        GlStateManager.translate(
            -tile.getPos().getX().toFloat(),
            -tile.getPos().getY().toFloat(),
            -tile.getPos().getZ().toFloat()
        )
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
            tile.getWorld(),
            getBakedModel(),
            tile.getWorld().getBlockState(tile.getPos()),
            tile.getPos(),
            tessellator.getBuffer(),
            false
        )
        tessellator.draw()

        RenderHelper.enableStandardItemLighting()
        GL11.glPopMatrix()
    }

    companion object {
        @JvmField
        var minerBaseModelBaked: OBJModel.OBJBakedModel? = null
    }
}
