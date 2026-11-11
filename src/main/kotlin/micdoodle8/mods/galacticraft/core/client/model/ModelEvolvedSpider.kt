package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.util.Mth as MathHelper
import kotlin.math.abs

open class ModelEvolvedSpider @JvmOverloads constructor(par1: Float = 0.0f) : ModelBase() {
    @JvmField
    var body: ModelRenderer

    @JvmField
    var rearEnd: ModelRenderer

    @JvmField
    var leg8: ModelRenderer

    @JvmField
    var leg6: ModelRenderer

    @JvmField
    var leg4: ModelRenderer

    @JvmField
    var leg2: ModelRenderer

    @JvmField
    var leg7: ModelRenderer

    @JvmField
    var leg5: ModelRenderer

    @JvmField
    var leg3: ModelRenderer

    @JvmField
    var leg1: ModelRenderer

    @JvmField
    var head: ModelRenderer

    @JvmField
    var oxygenMask: ModelRenderer

    @JvmField
    var tank1: ModelRenderer

    @JvmField
    var tank2: ModelRenderer

    @JvmField
    var tube1: ModelRenderer

    @JvmField
    var tube2: ModelRenderer

    @JvmField
    var tube3: ModelRenderer

    @JvmField
    var tube4: ModelRenderer

    @JvmField
    var tube5: ModelRenderer

    @JvmField
    var tube6: ModelRenderer

    @JvmField
    var tube7: ModelRenderer

    @JvmField
    var tube8: ModelRenderer

    @JvmField
    var tube9: ModelRenderer

    @JvmField
    var tube10: ModelRenderer

    @JvmField
    var tube11: ModelRenderer

    @JvmField
    var tube12: ModelRenderer

    @JvmField
    var tube13: ModelRenderer

    @JvmField
    var tube15: ModelRenderer

    @JvmField
    var tube14: ModelRenderer

    @JvmField
    var tube16: ModelRenderer

    @JvmField
    var tube17: ModelRenderer

    @JvmField
    var tube18: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 64

        this.body = ModelRenderer(this, 0, 0)
        this.body.addBox(-3f, -3f, -3f, 6, 6, 6, par1)
        this.body.setRotationPoint(0f, 15f, 0f)
        this.body.setTextureSize(128, 64)
        this.body.mirror = true
        this.setRotation(this.body, 0f, 0f, 0f)
        this.rearEnd = ModelRenderer(this, 0, 12)
        this.rearEnd.addBox(-5f, -4f, -6f, 10, 8, 12, par1)
        this.rearEnd.setRotationPoint(0f, 15f, 9f)
        this.rearEnd.setTextureSize(128, 64)
        this.rearEnd.mirror = true
        this.setRotation(this.rearEnd, 0f, 0f, 0f)
        this.leg8 = ModelRenderer(this, 18, 0)
        this.leg8.addBox(-1f, -1f, -1f, 16, 2, 2, par1)
        this.leg8.setRotationPoint(4f, 15f, -1f)
        this.leg8.setTextureSize(128, 64)
        this.leg8.mirror = true
        this.setRotation(this.leg8, 0f, 0.5759587f, 0.1919862f)
        this.leg6 = ModelRenderer(this, 18, 0)
        this.leg6.addBox(-1f, -1f, -1f, 16, 2, 2, par1)
        this.leg6.setRotationPoint(4f, 15f, 0f)
        this.leg6.setTextureSize(128, 64)
        this.leg6.mirror = true
        this.setRotation(this.leg6, 0f, 0.2792527f, 0.1919862f)
        this.leg4 = ModelRenderer(this, 18, 0)
        this.leg4.addBox(-1f, -1f, -1f, 16, 2, 2, par1)
        this.leg4.setRotationPoint(4f, 15f, 1f)
        this.leg4.setTextureSize(128, 64)
        this.leg4.mirror = true
        this.setRotation(this.leg4, 0f, -0.2792527f, 0.1919862f)
        this.leg2 = ModelRenderer(this, 18, 0)
        this.leg2.addBox(-1f, -1f, -1f, 16, 2, 2, par1)
        this.leg2.setRotationPoint(4f, 15f, 2f)
        this.leg2.setTextureSize(128, 64)
        this.leg2.mirror = true
        this.setRotation(this.leg2, 0f, -0.5759587f, 0.1919862f)
        this.leg7 = ModelRenderer(this, 18, 0)
        this.leg7.addBox(-15f, -1f, -1f, 16, 2, 2, par1)
        this.leg7.setRotationPoint(-4f, 15f, -1f)
        this.leg7.setTextureSize(128, 64)
        this.leg7.mirror = true
        this.setRotation(this.leg7, 0f, -0.5759587f, -0.1919862f)
        this.leg5 = ModelRenderer(this, 18, 0)
        this.leg5.addBox(-15f, -1f, -1f, 16, 2, 2, par1)
        this.leg5.setRotationPoint(-4f, 15f, 0f)
        this.leg5.setTextureSize(128, 64)
        this.leg5.mirror = true
        this.setRotation(this.leg5, 0f, -0.2792527f, -0.1919862f)
        this.leg3 = ModelRenderer(this, 18, 0)
        this.leg3.addBox(-15f, -1f, -1f, 16, 2, 2, par1)
        this.leg3.setRotationPoint(-4f, 15f, 1f)
        this.leg3.setTextureSize(128, 64)
        this.leg3.mirror = true
        this.setRotation(this.leg3, 0f, 0.2792527f, -0.1919862f)
        this.leg1 = ModelRenderer(this, 18, 0)
        this.leg1.addBox(-15f, -1f, -1f, 16, 2, 2, par1)
        this.leg1.setRotationPoint(-4f, 15f, 2f)
        this.leg1.setTextureSize(128, 64)
        this.leg1.mirror = true
        this.setRotation(this.leg1, 0f, 0.5759587f, -0.1919862f)
        this.head = ModelRenderer(this, 32, 4)
        this.head.addBox(-4f, -4f, -8f, 8, 8, 8, par1)
        this.head.setRotationPoint(0f, 15f, -3f)
        this.head.setTextureSize(128, 64)
        this.head.mirror = true
        this.setRotation(this.head, 0f, 0f, 0f)

        this.oxygenMask = ModelRenderer(this, 0, 32)
        this.oxygenMask.addBox(-5f, -5f, -9f, 10, 10, 10, par1)
        this.oxygenMask.setRotationPoint(0f, 15f, -3f)
        this.oxygenMask.setTextureSize(128, 64)
        this.oxygenMask.mirror = true
        this.setRotation(this.oxygenMask, 0f, 0f, 0f)
        this.tank1 = ModelRenderer(this, 40, 34)
        this.tank1.addBox(1f, -3f, 0f, 3, 3, 7, par1)
        this.tank1.setRotationPoint(0f, 11f, 4f)
        this.tank1.setTextureSize(128, 64)
        this.tank1.mirror = true
        this.setRotation(this.tank1, 0f, 0f, 0f)
        this.tank2 = ModelRenderer(this, 40, 34)
        this.tank2.addBox(-4f, -3f, 0f, 3, 3, 7, par1)
        this.tank2.setRotationPoint(0f, 11f, 4f)
        this.tank2.setTextureSize(128, 64)
        this.tank2.mirror = true
        this.setRotation(this.tank2, 0f, 0f, 0f)
        this.tube1 = ModelRenderer(this, 40, 32)
        this.tube1.addBox(2f, 0f, -6.5f, 1, 1, 1, par1)
        this.tube1.setRotationPoint(0f, 11f, 4f)
        this.tube1.setTextureSize(128, 64)
        this.tube1.mirror = true
        this.setRotation(this.tube1, 0f, 0f, 0f)
        this.tube2 = ModelRenderer(this, 40, 32)
        this.tube2.addBox(2f, -1f, -5.5f, 1, 1, 1, par1)
        this.tube2.setRotationPoint(0f, 11f, 4f)
        this.tube2.setTextureSize(128, 64)
        this.tube2.mirror = true
        this.setRotation(this.tube2, 0f, 0f, 0f)
        this.tube3 = ModelRenderer(this, 40, 32)
        this.tube3.addBox(2f, -1f, -4.5f, 1, 1, 1, par1)
        this.tube3.setRotationPoint(0f, 11f, 4f)
        this.tube3.setTextureSize(128, 64)
        this.tube3.mirror = true
        this.setRotation(this.tube3, 0f, 0f, 0f)
        this.tube4 = ModelRenderer(this, 40, 32)
        this.tube4.addBox(2f, -2f, -3.5f, 1, 1, 1, par1)
        this.tube4.setRotationPoint(0f, 11f, 4f)
        this.tube4.setTextureSize(128, 64)
        this.tube4.mirror = true
        this.setRotation(this.tube4, 0f, 0f, 0f)
        this.tube5 = ModelRenderer(this, 40, 32)
        this.tube5.addBox(2f, -3f, -2.5f, 1, 1, 1, par1)
        this.tube5.setRotationPoint(0f, 11f, 4f)
        this.tube5.setTextureSize(128, 64)
        this.tube5.mirror = true
        this.setRotation(this.tube5, 0f, 0f, 0f)
        this.tube6 = ModelRenderer(this, 40, 32)
        this.tube6.addBox(2f, -4f, -2.5f, 1, 1, 1, par1)
        this.tube6.setRotationPoint(0f, 11f, 4f)
        this.tube6.setTextureSize(128, 64)
        this.tube6.mirror = true
        this.setRotation(this.tube6, 0f, 0f, 0f)
        this.tube7 = ModelRenderer(this, 40, 32)
        this.tube7.addBox(2f, -5f, -1.5f, 1, 1, 1, par1)
        this.tube7.setRotationPoint(0f, 11f, 4f)
        this.tube7.setTextureSize(128, 64)
        this.tube7.mirror = true
        this.setRotation(this.tube7, 0f, 0f, 0f)
        this.tube8 = ModelRenderer(this, 40, 32)
        this.tube8.addBox(2f, -5f, -0.5f, 1, 1, 1, par1)
        this.tube8.setRotationPoint(0f, 11f, 4f)
        this.tube8.setTextureSize(128, 64)
        this.tube8.mirror = true
        this.setRotation(this.tube8, 0f, 0f, 0f)
        this.tube9 = ModelRenderer(this, 40, 32)
        this.tube9.addBox(2f, -4f, 0.5f, 1, 1, 1, par1)
        this.tube9.setRotationPoint(0f, 11f, 4f)
        this.tube9.setTextureSize(128, 64)
        this.tube9.mirror = true
        this.setRotation(this.tube9, 0f, 0f, 0f)
        this.tube10 = ModelRenderer(this, 40, 32)
        this.tube10.addBox(-3f, 0f, -6.5f, 1, 1, 1, par1)
        this.tube10.setRotationPoint(0f, 11f, 4f)
        this.tube10.setTextureSize(128, 64)
        this.tube10.mirror = true
        this.setRotation(this.tube10, 0f, 0f, 0f)
        this.tube11 = ModelRenderer(this, 40, 32)
        this.tube11.addBox(-3f, -1f, -5.5f, 1, 1, 1, par1)
        this.tube11.setRotationPoint(0f, 11f, 4f)
        this.tube11.setTextureSize(128, 64)
        this.tube11.mirror = true
        this.setRotation(this.tube11, 0f, 0f, 0f)
        this.tube12 = ModelRenderer(this, 40, 32)
        this.tube12.addBox(-3f, -1f, -4.5f, 1, 1, 1, par1)
        this.tube12.setRotationPoint(0f, 11f, 4f)
        this.tube12.setTextureSize(128, 64)
        this.tube12.mirror = true
        this.setRotation(this.tube12, 0f, 0f, 0f)
        this.tube13 = ModelRenderer(this, 40, 32)
        this.tube13.addBox(-3f, -2f, -3.5f, 1, 1, 1, par1)
        this.tube13.setRotationPoint(0f, 11f, 4f)
        this.tube13.setTextureSize(128, 64)
        this.tube13.mirror = true
        this.setRotation(this.tube13, 0f, 0f, 0f)
        this.tube15 = ModelRenderer(this, 40, 32)
        this.tube15.addBox(-3f, -4f, -2.5f, 1, 1, 1, par1)
        this.tube15.setRotationPoint(0f, 11f, 4f)
        this.tube15.setTextureSize(128, 64)
        this.tube15.mirror = true
        this.setRotation(this.tube15, 0f, 0f, 0f)
        this.tube14 = ModelRenderer(this, 40, 32)
        this.tube14.addBox(-3f, -3f, -2.5f, 1, 1, 1, par1)
        this.tube14.setRotationPoint(0f, 11f, 4f)
        this.tube14.setTextureSize(128, 64)
        this.tube14.mirror = true
        this.setRotation(this.tube14, 0f, 0f, 0f)
        this.tube16 = ModelRenderer(this, 40, 32)
        this.tube16.addBox(-3f, -5f, -1.5f, 1, 1, 1, par1)
        this.tube16.setRotationPoint(0f, 11f, 4f)
        this.tube16.setTextureSize(128, 64)
        this.tube16.mirror = true
        this.setRotation(this.tube16, 0f, 0f, 0f)
        this.tube17 = ModelRenderer(this, 40, 32)
        this.tube17.addBox(-3f, -5f, -0.5f, 1, 1, 1, par1)
        this.tube17.setRotationPoint(0f, 11f, 4f)
        this.tube17.setTextureSize(128, 64)
        this.tube17.mirror = true
        this.setRotation(this.tube17, 0f, 0f, 0f)
        this.tube18 = ModelRenderer(this, 40, 32)
        this.tube18.addBox(-3f, -4f, 0.5f, 1, 1, 1, par1)
        this.tube18.setRotationPoint(0f, 11f, 4f)
        this.tube18.setTextureSize(128, 64)
        this.tube18.mirror = true
        this.setRotation(this.tube18, 0f, 0f, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5)
        this.body.render(f5)
        this.rearEnd.render(f5)
        this.leg8.render(f5)
        this.leg6.render(f5)
        this.leg4.render(f5)
        this.leg2.render(f5)
        this.leg7.render(f5)
        this.leg5.render(f5)
        this.leg3.render(f5)
        this.leg1.render(f5)
        this.head.render(f5)
        this.oxygenMask.render(f5)
        this.tank1.render(f5)
        this.tank2.render(f5)
        this.tube1.render(f5)
        this.tube2.render(f5)
        this.tube3.render(f5)
        this.tube4.render(f5)
        this.tube5.render(f5)
        this.tube6.render(f5)
        this.tube7.render(f5)
        this.tube8.render(f5)
        this.tube9.render(f5)
        this.tube10.render(f5)
        this.tube11.render(f5)
        this.tube12.render(f5)
        this.tube13.render(f5)
        this.tube15.render(f5)
        this.tube14.render(f5)
        this.tube16.render(f5)
        this.tube17.render(f5)
        this.tube18.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    open fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        this.head.rotateAngleY = f3 / Constants.RADIANS_TO_DEGREES
        this.head.rotateAngleX = f4 / Constants.RADIANS_TO_DEGREES
        this.oxygenMask.rotateAngleY = f3 / Constants.RADIANS_TO_DEGREES
        this.oxygenMask.rotateAngleX = f4 / Constants.RADIANS_TO_DEGREES
        val var7: Float = Math.PI.toFloat() / 4f
        this.leg1.rotateAngleZ = -var7
        this.leg2.rotateAngleZ = var7
        this.leg3.rotateAngleZ = -var7 * 0.74f
        this.leg4.rotateAngleZ = var7 * 0.74f
        this.leg5.rotateAngleZ = -var7 * 0.74f
        this.leg6.rotateAngleZ = var7 * 0.74f
        this.leg7.rotateAngleZ = -var7
        this.leg8.rotateAngleZ = var7
        val var8: Float = -0.0f
        val var9: Float = 0.3926991f
        this.leg1.rotateAngleY = var9 * 2.0f + var8
        this.leg2.rotateAngleY = -var9 * 2.0f - var8
        this.leg3.rotateAngleY = var9 * 1.0f + var8
        this.leg4.rotateAngleY = -var9 * 1.0f - var8
        this.leg5.rotateAngleY = -var9 * 1.0f + var8
        this.leg6.rotateAngleY = var9 * 1.0f - var8
        this.leg7.rotateAngleY = -var9 * 2.0f + var8
        this.leg8.rotateAngleY = var9 * 2.0f - var8
        val var10: Float = -(MathHelper.cos(f * 0.6662f * 2.0f + 0.0f) * 0.4f) * f1
        val var11: Float = -(MathHelper.cos(f * 0.6662f * 2.0f + Math.PI.toFloat()) * 0.4f) * f1
        val var12: Float = -(MathHelper.cos(f * 0.6662f * 2.0f + Constants.halfPI) * 0.4f) * f1
        val var13: Float = -(MathHelper.cos(f * 0.6662f * 2.0f + 3f * Constants.halfPI) * 0.4f) * f1
        val var14: Float = abs(MathHelper.sin(f * 0.6662f + 0.0f) * 0.4f) * f1
        val var15: Float = abs(MathHelper.sin(f * 0.6662f + Math.PI.toFloat()) * 0.4f) * f1
        val var16: Float = abs(MathHelper.sin(f * 0.6662f + Constants.halfPI) * 0.4f) * f1
        val var17: Float = abs(MathHelper.sin(f * 0.6662f + 3f * Constants.halfPI) * 0.4f) * f1
        this.leg1.rotateAngleY += var10
        this.leg2.rotateAngleY += -var10
        this.leg3.rotateAngleY += var11
        this.leg4.rotateAngleY += -var11
        this.leg5.rotateAngleY += var12
        this.leg6.rotateAngleY += -var12
        this.leg7.rotateAngleY += var13
        this.leg8.rotateAngleY += -var13
        this.leg1.rotateAngleZ += var14
        this.leg2.rotateAngleZ += -var14
        this.leg3.rotateAngleZ += var15
        this.leg4.rotateAngleZ += -var15
        this.leg5.rotateAngleZ += var16
        this.leg6.rotateAngleZ += -var16
        this.leg7.rotateAngleZ += var17
        this.leg8.rotateAngleZ += -var17
    }
}
