package asmodeuscore.core.astronomy.sky

import javax.annotation.Nonnull

import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.CustomCloudRender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.world.entity.Entity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.nio.ByteBuffer
import java.util.Random
import java.util.function.Predicate

abstract class CustomCloudRender(layersLevels_t: FloatArray) : IRenderHandler(), ISelectiveResourceReloadListener {
    // Instance fields
    @JvmField
    protected val mc: Minecraft = Minecraft.getInstance()

    @JvmField
    protected val default_clouds: ResourceLocation = ResourceLocation.parse("textures/environment/clouds.png")

    private var displayList = -1
    private var vbo: VertexBuffer? = null
    private var layersLevels = floatArrayOf()
    private var offsetX = floatArrayOf()
    private var offsetZ = floatArrayOf()

    @JvmField
    var cloudTicks: Long = 0

    @JvmField
    var cloudTicksPrev: Long = 0
    private var color: Vec3d? = null

    private var cloudMode = -1
    private var renderDistance = -1

    private var COLOR_TEX: DynamicTexture? = null

    private var texW = 0
    private var texH = 0

    init {
        // Resource manager should always be reloadable.
        (mc.getResourceManager() as IReloadableResourceManager).registerReloadListener(this)
        layersLevels = layersLevels_t
        offsetX = FloatArray(layersLevels_t.size)
        offsetZ = FloatArray(layersLevels_t.size)
        for (i: Int in layersLevels_t.indices) {
            offsetX[i] = Random().nextInt(1000) + 100.0f
            offsetZ[i] = Random().nextInt(1000) + 100.0f
        }
    }

    private fun getScale(): Int {
        return if (cloudMode == 2) 12 else 8
    }

    private fun ceilToScale(value: Float): Float {
        val scale: Float = getScale().toFloat()
        return MathHelper.ceil(value / scale) * scale
    }

    private fun vertices(buffer: BufferBuilder) {
        val fancy: Boolean = cloudMode == 2 // Defines whether to hide all but the bottom.

        val scale: Float = getScale().toFloat()
        val CULL_DIST: Float = 2 * scale

        val bCol: Float = if (fancy) 0.7f else 1f

        val sectEnd: Float = ceilToScale(((renderDistance * 2) * 16).toFloat())
        val sectStart: Float = -sectEnd

        val sectStep: Float = ceilToScale(sectEnd * 2 / TOP_SECTIONS)
        val sectPx: Float = PX_SIZE / scale

        buffer.begin(GL11.GL_QUADS, FORMAT)

        var sectX0: Float = sectStart
        var sectX1: Float = sectX0

        while (sectX1 < sectEnd) {
            sectX1 += sectStep

            if (sectX1 > sectEnd) sectX1 = sectEnd

            var sectZ0: Float = sectStart
            var sectZ1: Float = sectZ0

            while (sectZ1 < sectEnd) {
                sectZ1 += sectStep

                if (sectZ1 > sectEnd) sectZ1 = sectEnd

                val u0: Float = sectX0 * sectPx
                val u1: Float = sectX1 * sectPx
                val v0: Float = sectZ0 * sectPx
                val v1: Float = sectZ1 * sectPx

                // Bottom
                buffer.pos(sectX0.toDouble(), 0.0, sectZ0.toDouble()).tex(u0.toDouble(), v0.toDouble())
                    .color(bCol, bCol, bCol, ALPHA).endVertex()
                buffer.pos(sectX1.toDouble(), 0.0, sectZ0.toDouble()).tex(u1.toDouble(), v0.toDouble())
                    .color(bCol, bCol, bCol, ALPHA).endVertex()
                buffer.pos(sectX1.toDouble(), 0.0, sectZ1.toDouble()).tex(u1.toDouble(), v1.toDouble())
                    .color(bCol, bCol, bCol, ALPHA).endVertex()
                buffer.pos(sectX0.toDouble(), 0.0, sectZ1.toDouble()).tex(u0.toDouble(), v1.toDouble())
                    .color(bCol, bCol, bCol, ALPHA).endVertex()

                if (fancy) {
                    // Top
                    buffer.pos(sectX0.toDouble(), HEIGHT.toDouble(), sectZ0.toDouble())
                        .tex(u0.toDouble(), v0.toDouble()).color(
                            1 * this.color!!.x.toFloat(),
                            1 * this.color!!.y.toFloat(),
                            1 * this.color!!.z.toFloat(),
                            ALPHA
                        ).endVertex()
                    buffer.pos(sectX0.toDouble(), HEIGHT.toDouble(), sectZ1.toDouble())
                        .tex(u0.toDouble(), v1.toDouble()).color(
                            1 * this.color!!.x.toFloat(),
                            1 * this.color!!.y.toFloat(),
                            1 * this.color!!.z.toFloat(),
                            ALPHA
                        ).endVertex()
                    buffer.pos(sectX1.toDouble(), HEIGHT.toDouble(), sectZ1.toDouble())
                        .tex(u1.toDouble(), v1.toDouble()).color(
                            1 * this.color!!.x.toFloat(),
                            1 * this.color!!.y.toFloat(),
                            1 * this.color!!.z.toFloat(),
                            ALPHA
                        ).endVertex()
                    buffer.pos(sectX1.toDouble(), HEIGHT.toDouble(), sectZ0.toDouble())
                        .tex(u1.toDouble(), v0.toDouble()).color(
                            1 * this.color!!.x.toFloat(),
                            1 * this.color!!.y.toFloat(),
                            1 * this.color!!.z.toFloat(),
                            ALPHA
                        ).endVertex()

                    var slice: Float
                    var sliceCoord0: Float
                    var sliceCoord1: Float

                    slice = sectX0
                    while (slice < sectX1) {
                        sliceCoord0 = slice * sectPx
                        sliceCoord1 = sliceCoord0 + PX_SIZE

                        // X sides
                        if (slice > -CULL_DIST) {
                            slice += INSET
                            buffer.pos(slice.toDouble(), 0.0, sectZ1.toDouble())
                                .tex(sliceCoord0.toDouble(), v1.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(slice.toDouble(), HEIGHT.toDouble(), sectZ1.toDouble())
                                .tex(sliceCoord1.toDouble(), v1.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(slice.toDouble(), HEIGHT.toDouble(), sectZ0.toDouble())
                                .tex(sliceCoord1.toDouble(), v0.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(slice.toDouble(), 0.0, sectZ0.toDouble())
                                .tex(sliceCoord0.toDouble(), v0.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            slice -= INSET
                        }

                        slice += scale

                        if (slice <= CULL_DIST) {
                            slice -= INSET
                            buffer.pos(slice.toDouble(), 0.0, sectZ0.toDouble())
                                .tex(sliceCoord0.toDouble(), v0.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(slice.toDouble(), HEIGHT.toDouble(), sectZ0.toDouble())
                                .tex(sliceCoord1.toDouble(), v0.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(slice.toDouble(), HEIGHT.toDouble(), sectZ1.toDouble())
                                .tex(sliceCoord1.toDouble(), v1.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(slice.toDouble(), 0.0, sectZ1.toDouble())
                                .tex(sliceCoord0.toDouble(), v1.toDouble()).color(
                                    0.9f * this.color!!.x.toFloat(),
                                    0.9f * this.color!!.y.toFloat(),
                                    0.9f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            slice += INSET
                        }
                    }

                    slice = sectZ0
                    while (slice < sectZ1) {
                        sliceCoord0 = slice * sectPx
                        sliceCoord1 = sliceCoord0 + PX_SIZE

                        // Z sides
                        if (slice > -CULL_DIST) {
                            slice += INSET
                            buffer.pos(sectX0.toDouble(), 0.0, slice.toDouble())
                                .tex(u0.toDouble(), sliceCoord0.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(sectX0.toDouble(), HEIGHT.toDouble(), slice.toDouble())
                                .tex(u0.toDouble(), sliceCoord1.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(sectX1.toDouble(), HEIGHT.toDouble(), slice.toDouble())
                                .tex(u1.toDouble(), sliceCoord1.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(sectX1.toDouble(), 0.0, slice.toDouble())
                                .tex(u1.toDouble(), sliceCoord0.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            slice -= INSET
                        }

                        slice += scale

                        if (slice <= CULL_DIST) {
                            slice -= INSET
                            buffer.pos(sectX1.toDouble(), 0.0, slice.toDouble())
                                .tex(u1.toDouble(), sliceCoord0.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(sectX1.toDouble(), HEIGHT.toDouble(), slice.toDouble())
                                .tex(u1.toDouble(), sliceCoord1.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(sectX0.toDouble(), HEIGHT.toDouble(), slice.toDouble())
                                .tex(u0.toDouble(), sliceCoord1.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            buffer.pos(sectX0.toDouble(), 0.0, slice.toDouble())
                                .tex(u0.toDouble(), sliceCoord0.toDouble()).color(
                                    0.8f * this.color!!.x.toFloat(),
                                    0.8f * this.color!!.y.toFloat(),
                                    0.8f * this.color!!.z.toFloat(),
                                    ALPHA
                                ).endVertex()
                            slice += INSET
                        }
                    }
                }

                sectZ0 = sectZ1
            }

            sectX0 = sectX1
        }
    }

    private fun dispose() {
        if (vbo != null) {
            vbo!!.deleteGlBuffers()
            vbo = null
        }
        if (displayList >= 0) {
            GLAllocation.deleteDisplayLists(displayList)
            displayList = -1
        }
    }

    private fun build() {
        val tess: Tessellator = Tessellator.getInstance()
        val buffer: BufferBuilder = tess.getBuffer()

        if (OpenGlHelper.useVbo()) vbo = VertexBuffer(FORMAT)
        else GlStateManager.glNewList(GLAllocation.generateDisplayLists(1).also { displayList = it }, GL11.GL_COMPILE)

        vertices(buffer)

        if (OpenGlHelper.useVbo()) {
            buffer.finishDrawing()
            buffer.reset()
            vbo!!.bufferData(buffer.getByteBuffer())
        } else {
            tess.draw()
            GlStateManager.glEndList()
        }
    }

    private fun fullCoord(
        coord: Double,
        scale: Int
    ): Int {   // Corrects misalignment of UV offset when on negative coords.
        return (coord.toInt() / scale) - (if (coord < 0) 1 else 0)
    }

    private fun isBuilt(): Boolean {
        return if (OpenGlHelper.useVbo()) vbo != null else displayList >= 0
    }

    fun checkSettings() {
        val newEnabled: Boolean = ForgeModContainer.forgeCloudsEnabled
                && mc.gameSettings.shouldRenderClouds() != 0 && mc.world != null && mc.world.provider.isSurfaceWorld()

        if (isBuilt()
            && (!newEnabled || mc.gameSettings.shouldRenderClouds() != cloudMode || mc.gameSettings.renderDistanceChunks != renderDistance)
        ) {
            dispose()
        }

        cloudMode = mc.gameSettings.shouldRenderClouds()
        renderDistance = mc.gameSettings.renderDistanceChunks

        if (newEnabled && !isBuilt()) {
            build()
        }
    }

    @OnlyIn(Dist.CLIENT)
    fun render(cloudTicks: Float, partialTicks: Float, offsets: FloatArray): Boolean {
        if (!isBuilt()) return false

        val entity: Entity? = mc.getRenderViewEntity()

        val totalOffset: Double = (cloudTicks + partialTicks).toDouble()

        var x: Double = (entity!!.prevPosX + (entity.x - entity.prevPosX) * partialTicks + totalOffset * 0.03)
        val y: Double = (mc.world.provider.getCloudHeight()
                - (entity.lastTickPosY + (entity.y - entity.lastTickPosY) * partialTicks)
                + 0.33)
        var z: Double = entity.prevPosZ + (entity.z - entity.prevPosZ) * partialTicks

        x += offsets[0].toDouble()
        z += offsets[2].toDouble()

        val scale: Int = getScale()
        var l: Int = 1
        if (cloudMode == 2) {
            z += 0.33 * scale
            l = 5
        }

        // Integer UVs to translate the texture matrix by.
        var offU: Int = fullCoord(x, scale)
        var offV: Int = fullCoord(z, scale)

        GlStateManager.pushMatrix()

        // Translate by the remainder after the UV offset.
        GlStateManager.translate((offU * scale) - x, y + offsets[1] * l, (offV * scale) - z)

        // Modulo to prevent texture samples becoming inaccurate at extreme offsets.
        offU = offU % texW
        offV = offV % texH

        // Translate the texture.
        GlStateManager.matrixMode(GL11.GL_TEXTURE)
        GlStateManager.translate(offU * PX_SIZE, offV * PX_SIZE, 0f)
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)

        GlStateManager.disableCull()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        )

        // Color multiplier.
        val color: Vec3d = getCloudColor(partialTicks) //mc.world.getCloudColour(partialTicks);
        var r: Float = color.x.toFloat() / 255f
        var g: Float = color.y.toFloat() / 255f
        var b: Float = color.z.toFloat() / 255f

        if (mc.gameSettings.anaglyph) {
            val tempR: Float = r * 0.3f + g * 0.59f + b * 0.11f
            val tempG: Float = r * 0.3f + g * 0.7f
            val tempB: Float = r * 0.3f + b * 0.7f
            r = tempR
            g = tempG
            b = tempB
        }

        if (COLOR_TEX == null) COLOR_TEX = DynamicTexture(1, 1)

        // Apply a color multiplier through a texture upload if shaders aren't supported.
        COLOR_TEX!!.getTextureData()[0] = (255 shl 24 or (((r * 255).toInt()) shl 16
                ) or (((g * 255).toInt()) shl 8
                ) or (b * 255).toInt())
        COLOR_TEX!!.updateDynamicTexture()

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.bindTexture(COLOR_TEX!!.getGlTextureId())
        GlStateManager.enableTexture2D()

        // Bind the clouds texture last so the shader's sampler2D is correct.
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        mc.renderEngine.bindTexture(getCloudTexture())

        val buffer: ByteBuffer = Tessellator.getInstance().getBuffer().getByteBuffer()

        // Set up pointers for the display list/VBO.
        if (OpenGlHelper.useVbo()) {
            vbo!!.bindBuffer()

            val stride: Int = FORMAT.getSize()
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0)
            GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY)
            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12)
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 20)
            GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY)
        } else {
            buffer.limit(FORMAT.getSize())
            for (i: Int in 0..<FORMAT.getElementCount()) FORMAT.getElements().get(i).getUsage()
                .preDraw(FORMAT, i, FORMAT.getSize(), buffer)
            buffer.position(0)
        }

        // Depth pass to prevent insides rendering from the outside.
        GlStateManager.colorMask(false, false, false, false)
        if (OpenGlHelper.useVbo()) vbo!!.drawArrays(GL11.GL_QUADS)
        else GlStateManager.callList(displayList)

        // Full render.
        if (!mc.gameSettings.anaglyph) {
            GlStateManager.colorMask(true, true, true, true)
        } else {
            when (EntityRenderer.anaglyphField) {
                0 -> GlStateManager.colorMask(false, true, true, true)
                1 -> GlStateManager.colorMask(true, false, false, true)
            }
        }

        if (OpenGlHelper.useVbo()) {
            vbo!!.drawArrays(GL11.GL_QUADS)
            vbo!!.unbindBuffer() // Unbind buffer and disable pointers.
        } else {
            GlStateManager.callList(displayList)
        }

        buffer.limit(0)
        for (i: Int in 0..<FORMAT.getElementCount()) FORMAT.getElements().get(i).getUsage()
            .postDraw(FORMAT, i, FORMAT.getSize(), buffer)
        buffer.position(0)

        // Disable our coloring.
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)

        // Reset texture matrix.
        GlStateManager.matrixMode(GL11.GL_TEXTURE)
        GlStateManager.loadIdentity()
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)

        GlStateManager.disableBlend()
        GlStateManager.enableCull()

        GlStateManager.popMatrix()

        return true
    }

    private fun reloadTextures() {
        if (mc.renderEngine != null) {
            mc.renderEngine.bindTexture(getCloudTexture())
            texW = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
            texH = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)
        }
    }

    override fun onResourceManagerReload(
        @Nonnull resourceManager: IResourceManager,
        @Nonnull resourcePredicate: Predicate<IResourceType>
    ) {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
            reloadTextures()
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft?) {
        this.color = Vec3d(
            getCloudColor(partialTicks).x / 255f,
            getCloudColor(partialTicks).y / 255f,
            getCloudColor(partialTicks).z / 255f
        )

        checkSettings()
        render(cloudTicks.toFloat(), partialTicks, floatArrayOf(0.0f, 0.0f, 0.0f))
        for (i: Int in layersLevels.indices) {
            render(cloudTicks.toFloat(), partialTicks, floatArrayOf(offsetX[i], layersLevels[i], offsetZ[i]))
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract fun getCloudMovementSpeed(world: WorldClient?): Float

    @OnlyIn(Dist.CLIENT)
    abstract fun getCloudTexture(): ResourceLocation?

    @OnlyIn(Dist.CLIENT)
    abstract fun getCloudColor(renderPartialTicks: Float): Vec3d

    companion object {
        // Shared constants.
        private val PX_SIZE = 1 / 256f

        // Building constants.
        private val FORMAT: VertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR
        private const val TOP_SECTIONS = 12 // Number of slices a top face will span.
        private const val HEIGHT = 4
        private const val INSET = 0.001f
        private const val ALPHA = 0.8f
    }
}
