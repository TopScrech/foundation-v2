package micdoodle8.mods.galacticraft.core.client.render.tile

import java.util.HashMap

import com.google.common.base.Function
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.transmission.tile.IBufferTransmitter
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11
import kotlin.math.max

open class TileEntityFluidPipeRenderer : TileEntitySpecialRenderer<TileEntityFluidPipe>() {
    private val stages = 100

    private fun updateModels() {
        if (pullConnectorModel[0] == null) {
            try {
                for (facing: EnumFacing in EnumFacing.VALUES) {
                    // Get the first character of the direction name (n/e/s/w/u/d)
                    val c: Char = facing.getName().get(0).lowercaseChar()
                    val model: IModel = ModelLoaderRegistry.getModel(
                        ResourceLocation(
                            Constants.ASSET_PREFIX,
                            "block/fluid_pipe_pull_" + c
                        )
                    )
                    val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                        Function { location: ResourceLocation? ->
                            Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                        }
                    pullConnectorModel[facing.ordinal] =
                        model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, spriteFunction)
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        pipe: TileEntityFluidPipe,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        updateModels()

        if (pipe.getBlockType() === GCBlocks.oxygenPipePull) {
            GL11.glPushMatrix()

            GL11.glTranslatef(x.toFloat(), y.toFloat(), z.toFloat())

            RenderHelper.disableStandardItemLighting()
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH)
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT)
            }

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            val adj: Array<TileEntity?> = OxygenUtil.getAdjacentFluidConnections(pipe)

            for (facing: EnumFacing in EnumFacing.VALUES) {
                val sideTile: TileEntity? = adj[facing.ordinal]

                if (sideTile != null && sideTile !is IBufferTransmitter<*>) {
                    GL11.glPushMatrix()
                    if (sideTile is TileEntityFluidTank) when (facing) {
                        EnumFacing.SOUTH -> GL11.glTranslatef(0f, 0f, 1 / 16f)
                        EnumFacing.NORTH -> GL11.glTranslatef(0f, 0f, -1 / 16f)
                        EnumFacing.EAST -> GL11.glTranslatef(1 / 16f, 0f, 0f)
                        EnumFacing.WEST -> GL11.glTranslatef(-1 / 16f, 0f, 0f)
                    
else -> {}
}
                    ClientUtil.drawBakedModel((pullConnectorModel[facing.ordinal])!!)
                    GL11.glPopMatrix()
                }
            }

            GL11.glPopMatrix()
        }

        val scale: Float

        if (pipe.hasNetwork()) {
            val network: FluidNetwork = pipe.getNetwork() as FluidNetwork
            scale = network.fluidScale
        } else {
            scale = pipe.buffer.getFluidAmount() / pipe.buffer.getCapacity().toFloat()
        }

        val fluid: Fluid?

        if (pipe.hasNetwork()) {
            val network: FluidNetwork = pipe.getNetwork() as FluidNetwork
            fluid = network.refFluid
        } else {
            fluid = if (pipe.getBuffer() == null) null else ((pipe.getBuffer())!!.getFluid())!!
        }

        if (fluid == null) {
            return
        }

        if (scale > 0.01) {
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GL11.glPushMatrix()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GL11.glTranslatef(x.toFloat(), y.toFloat() + 1.0f, z.toFloat() + 1.0f)
            GL11.glScalef(1.0f, -1.0f, -1.0f)
            GL11.glTranslatef(0.5f, 0.5f, 0.5f)

            GlStateManager.disableLighting()
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

            var opacity: Float = 1.0f

            val gas: Boolean = fluid.isGaseous()

            if (gas) {
                opacity = scale
            }

            GL11.glColor4f(1.0f, 1.0f, 1.0f, opacity)

            val connections: Array<TileEntity?> = OxygenUtil.getAdjacentFluidConnections(pipe)

            for (side: EnumFacing in EnumFacing.VALUES) {
                val sideTile: TileEntity? = connections[side.ordinal]
                if (sideTile != null) {
                    val displayLists: Array<Int>? = getListAndRender(side, fluid)

                    if (displayLists != null) {
                        if (!gas) {
                            val list: Int? = displayLists[max(3, (scale * (stages - 1)).toInt())]
                            GL11.glCallList(list!!)
                        } else {
                            val list: Int? = displayLists[stages - 1]
                            GL11.glCallList(list!!)
                        }
                    }
                }
            }

            val displayLists: Array<Int>? = getListAndRender(null, fluid)

            if (displayLists != null) {
                if (!gas) {
                    val list: Int? = displayLists[max(3, (scale * (stages - 1)).toInt())]
                    GL11.glCallList(list!!)
                } else {
                    val list: Int? = displayLists[stages - 1]
                    GL11.glCallList(list!!)
                }
            }

            GlStateManager.enableLighting()
            GlStateManager.disableBlend()

            GL11.glPopMatrix()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        }
    }

    private fun getListAndRender(side: EnumFacing?, fluid: Fluid?): Array<Int>? {
        if (fluid == null) {
            return null
        }

        val sprite: TextureAtlasSprite =
            Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString())
        val sideIndex: Int = if (side == null) 6 else side.ordinal

        if (cache.containsKey(sideIndex) && cache.get(sideIndex)!!.containsKey(fluid)) {
            return cache.get(sideIndex)!!.get(fluid)
        }

        val size: Float = 0.09f
        val mid: Float = 0.0f
        var minX: Float
        var maxX: Float
        var minY: Float
        var maxY: Float
        var minZ: Float
        var maxZ: Float

        val displayLists: Array<Int> = (arrayOfNulls<Int>(this.stages) as Array<Int>)

        if (cache.containsKey(sideIndex)) {
            cache.get(sideIndex)!!.put(fluid, displayLists)
        } else {
            val map: HashMap<Fluid?, Array<Int>?> = Maps.newHashMap<Fluid?, Array<Int>?>()
            map.put(fluid, displayLists)
            cache.put(sideIndex, map)
        }

        for (i: Int in 0..<stages) {
            displayLists[i] = GLAllocation.generateDisplayLists(1)
            GL11.glNewList(displayLists[i], GL11.GL_COMPILE)

            val level: Float = (i / stages.toFloat())

            when (sideIndex) {
                0 -> {
                    minX = mid - level * size
                    maxX = mid + level * size
                    minY = mid + size
                    maxY = mid + 0.5f
                    minZ = mid - level * size
                    maxZ = mid + level * size
                }

                1 -> {
                    minX = mid - level * size
                    maxX = mid + level * size
                    minY = mid - 0.5f
                    maxY = mid - size
                    minZ = mid - level * size
                    maxZ = mid + level * size
                }

                2 -> {
                    minX = mid - size
                    maxX = mid + size
                    maxY = mid + size
                    minY = mid + size - level * size * 2.0f
                    minZ = mid + size
                    maxZ = mid + 0.5f
                }

                3 -> {
                    minX = mid - size
                    maxX = mid + size
                    maxY = mid + size
                    minY = mid + size - level * size * 2.0f
                    minZ = mid - 0.5f
                    maxZ = mid - size
                }

                4 -> {
                    minX = mid - 0.5f
                    maxX = mid - size
                    maxY = mid + size
                    minY = mid + size - level * size * 2.0f
                    minZ = mid - size
                    maxZ = mid + size
                }

                5 -> {
                    minX = mid + size
                    maxX = mid + 0.5f
                    maxY = mid + size
                    minY = mid + size - level * size * 2.0f
                    minZ = mid - size
                    maxZ = mid + size
                }

                else -> {
                    minX = mid - size
                    maxX = mid + size
                    maxY = mid + size
                    minY = mid + size - level * size * 2.0f
                    minZ = mid - size
                    maxZ = mid + size
                }
            }

            renderBox(minX, maxX, minY, maxY, minZ, maxZ, level, sprite)
            GL11.glEndList()
        }

        return displayLists
    }

    private fun renderBox(
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        minZ: Float,
        maxZ: Float,
        level: Float,
        sprite: TextureAtlasSprite
    ) {
        val uMin: Double = sprite.getMinU().toDouble()
        val uMax: Double = sprite.getMaxU().toDouble()
        val vMin: Double = sprite.getMinV().toDouble()
        val vMax: Double = sprite.getMaxV().toDouble()

        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()

        val uDiff: Double = (uMax - uMin)
        val vDiff: Double = (vMax - vMin)
        val texMinX_U: Double = uMin + (minX + 0.5f) * uDiff
        val texMaxX_U: Double = uMin + (maxX + 0.5f) * uDiff
        val texMinZ_U: Double = uMin + (minZ + 0.5f) * uDiff
        val texMaxZ_U: Double = uMin + (maxZ + 0.5f) * uDiff
        val texMinX_V: Double = vMin + (minX + 0.5f) * vDiff
        val texMaxX_V: Double = vMin + (maxX + 0.5f) * vDiff
        val texMinY_V: Double = vMin + (minY + 0.5f) * vDiff
        val texMaxY_V: Double = vMin + (maxY + 0.5f) * vDiff

        // North
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(minX.toDouble(), minY.toDouble(), minZ.toDouble()).tex(texMinX_U, texMinY_V).endVertex()
        worldRenderer.pos(minX.toDouble(), maxY.toDouble(), minZ.toDouble()).tex(texMinX_U, texMaxY_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), maxY.toDouble(), minZ.toDouble()).tex(texMaxX_U, texMaxY_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), minY.toDouble(), minZ.toDouble()).tex(texMaxX_U, texMinY_V).endVertex()
        tess.draw()

        // South
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(maxX.toDouble(), minY.toDouble(), maxZ.toDouble()).tex(texMaxX_U, texMinY_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), maxY.toDouble(), maxZ.toDouble()).tex(texMaxX_U, texMaxY_V).endVertex()
        worldRenderer.pos(minX.toDouble(), maxY.toDouble(), maxZ.toDouble()).tex(texMinX_U, texMaxY_V).endVertex()
        worldRenderer.pos(minX.toDouble(), minY.toDouble(), maxZ.toDouble()).tex(texMinX_U, texMinY_V).endVertex()
        tess.draw()

        // West
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(minX.toDouble(), maxY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMaxY_V).endVertex()
        worldRenderer.pos(minX.toDouble(), minY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMinY_V).endVertex()
        worldRenderer.pos(minX.toDouble(), minY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMinY_V).endVertex()
        worldRenderer.pos(minX.toDouble(), maxY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMaxY_V).endVertex()
        tess.draw()

        // East
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(maxX.toDouble(), maxY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMaxY_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), minY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMinY_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), minY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMinY_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), maxY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMaxY_V).endVertex()
        tess.draw()

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(maxX.toDouble(), minY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMaxX_V).endVertex()
        worldRenderer.pos(minX.toDouble(), minY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMinX_V).endVertex()
        worldRenderer.pos(minX.toDouble(), minY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMinX_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), minY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMaxX_V).endVertex()
        tess.draw()

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(maxX.toDouble(), maxY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMaxX_V).endVertex()
        worldRenderer.pos(maxX.toDouble(), maxY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMaxX_V).endVertex()
        worldRenderer.pos(minX.toDouble(), maxY.toDouble(), minZ.toDouble()).tex(texMinZ_U, texMinX_V).endVertex()
        worldRenderer.pos(minX.toDouble(), maxY.toDouble(), maxZ.toDouble()).tex(texMaxZ_U, texMinX_V).endVertex()
        tess.draw()
    }

    companion object {
        private val cache = HashMap<Int, HashMap<Fluid?, Array<Int>?>?>()
        private val pullConnectorModel = arrayOfNulls<IBakedModel>(6)
    }
}
