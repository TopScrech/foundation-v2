package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.entities.EntityFlag
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11
import kotlin.math.sin

open class ModelFlag constructor() : ModelBase() {
    @JvmField
    var base: ModelRenderer

    @JvmField
    var pole: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 64
        this.base = ModelRenderer(this, 4, 0)
        this.base.addBox(-1.5f, 0f, -1.5f, 3, 1, 3)
        this.base.setRotationPoint(0f, 23f, 0f)
        this.base.setTextureSize(128, 64)
        this.base.mirror = true
        this.setRotation(this.base, 0f, 0f, 0f)
        this.pole = ModelRenderer(this, 0, 0)
        this.pole.addBox(-0.5f, -40f, -0.5f, 1, 40, 1)
        this.pole.setRotationPoint(0f, 23f, 0f)
        this.pole.setTextureSize(128, 64)
        this.pole.mirror = true
        this.setRotation(this.pole, 0f, 0f, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)

        if (entity is EntityFlag) {
            val flag: EntityFlag = entity
            this.renderPole(flag, f5)
            this.renderFlag(flag, flag.ticksExisted.toFloat())
        }
    }

    open fun renderPole(entity: Entity?, f5: Float) {
        this.base.render(f5)
        this.pole.render(f5)
    }

    open fun renderFlag(entity: EntityFlag, ticks: Float) {
        if (entity.flagData != null) {
            GL11.glPushMatrix()

            GL11.glScalef(0.5f, 0.5f, 0.5f)
            GL11.glTranslatef(0.0f, -1.1f, 0.0f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_CULL_FACE)

            var windLevel: Float = 1.0f

            if (entity.world.provider is IGalacticraftWorldProvider) {
                windLevel = (entity.world.provider as IGalacticraftWorldProvider).getWindLevel()
            }

            for (i: Int in 0..<((entity.flagData)!!.getWidth())!!) {
                for (j: Int in 0..<((entity.flagData)!!.getHeight())!!) {
                    GL11.glPushMatrix()
                    GL11.glTranslatef(0f, -1.0f, 0f)
                    var offset: Float = 0.0f
                    var offsetAhead: Float = 0.0f

                    if (windLevel > 0) {
                        offset = (sin((ticks / 2.0f + i * 50 + 3).toDouble()) / 25.0f).toFloat() * i / 30.0f
                        offsetAhead =
                            (sin((ticks / 2.0f + (i + 1) * 50 + 3).toDouble()) / 25.0f).toFloat() * (i + 1) / 30.0f
                        offset *= windLevel
                        offsetAhead *= windLevel
                    }

                    val col: Vector3 = ((entity.flagData)!!.getColorAt(i, j))!!
                    GL11.glColor3f(col.floatX(), col.floatY(), col.floatZ())

                    val tess: Tessellator = Tessellator.getInstance()
                    val worldRenderer: BufferBuilder = tess.getBuffer()
                    worldRenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION)

                    worldRenderer.pos(i / 24.0f + 0.0 / 24.0f, j / 24.0f + 0.0 / 24.0f + offset, offset.toDouble())
                        .endVertex()
                    worldRenderer.pos(i / 24.0f + 0.0 / 24.0f, j / 24.0f + 1.0 / 24.0f + offset, offset.toDouble())
                        .endVertex()
                    worldRenderer.pos(
                        i / 24.0f + 1.0 / 24.0f,
                        j / 24.0f + 1.0 / 24.0f + offsetAhead,
                        offsetAhead.toDouble()
                    ).endVertex()

                    worldRenderer.pos(i / 24.0f + 0.0 / 24.0f, j / 24.0f + 0.0 / 24.0f + offset, offset.toDouble())
                        .endVertex()
                    worldRenderer.pos(
                        i / 24.0f + 1.0 / 24.0f,
                        j / 24.0f + 1.0 / 24.0f + offsetAhead,
                        offsetAhead.toDouble()
                    ).endVertex()
                    worldRenderer.pos(
                        i / 24.0f + 1.0 / 24.0f,
                        j / 24.0f + 0.0 / 24.0f + offsetAhead,
                        offsetAhead.toDouble()
                    ).endVertex()

                    tess.draw()

                    GL11.glColor3f(1f, 1f, 1f)
                    GL11.glPopMatrix()
                }
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_CULL_FACE)

            GL11.glPopMatrix()
        }
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}
