package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBiped
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class ModelEvolvedEnderman constructor() : ModelBiped(0.0f, -14.0f, 64, 64) {
    @JvmField
    var isCarrying: Boolean = false

    @JvmField
    var isAttacking: Boolean = false

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
    var tube14: ModelRenderer

    init {
        val f1: Float = -14.0f
        this.bipedHeadwear = ModelRenderer(this, 0, 16)
        this.bipedHeadwear.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.0f - 0.5f)
        this.bipedHeadwear.setRotationPoint(0.0f, 0.0f + f1, 0.0f)
        this.bipedBody = ModelRenderer(this, 32, 16)
        this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.0f)
        this.bipedBody.setRotationPoint(0.0f, 0.0f + f1, 0.0f)
        this.bipedRightArm = ModelRenderer(this, 56, 0)
        this.bipedRightArm.addBox(-1.0f, -2.0f, -1.0f, 2, 30, 2, 0.0f)
        this.bipedRightArm.setRotationPoint(-3.0f, 2.0f + f1, 0.0f)
        this.bipedLeftArm = ModelRenderer(this, 56, 0)
        this.bipedLeftArm.mirror = true
        this.bipedLeftArm.addBox(-1.0f, -2.0f, -1.0f, 2, 30, 2, 0.0f)
        this.bipedLeftArm.setRotationPoint(5.0f, 2.0f + f1, 0.0f)
        this.bipedRightLeg = ModelRenderer(this, 56, 0)
        this.bipedRightLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 30, 2, 0.0f)
        this.bipedRightLeg.setRotationPoint(-2.0f, 12.0f + f1, 0.0f)
        this.bipedLeftLeg = ModelRenderer(this, 56, 0)
        this.bipedLeftLeg.mirror = true
        this.bipedLeftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 30, 2, 0.0f)
        this.bipedLeftLeg.setRotationPoint(2.0f, 12.0f + f1, 0.0f)

        this.oxygenMask = ModelRenderer(this, 0, 44)
        this.oxygenMask.addBox(-5f, -9f, -5f, 10, 10, 10)
        this.oxygenMask.setRotationPoint(0f, -14f, 0f)

        this.tank1 = ModelRenderer(this, 52, 54)
        this.tank1.addBox(0f, 0f, 0f, 3, 7, 3)
        this.tank1.setRotationPoint(0f, -11f, 2f)

        this.tank2 = ModelRenderer(this, 52, 54)
        this.tank2.addBox(0f, 0f, 0f, 3, 7, 3)
        this.tank2.setRotationPoint(-3f, -11f, 2f)

        this.tube1 = ModelRenderer(this, 44, 62)
        this.tube1.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube1.setRotationPoint(-2f, -10.5f, 5f)

        this.tube2 = ModelRenderer(this, 44, 62)
        this.tube2.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube2.setRotationPoint(-2f, -11f, 6f)

        this.tube3 = ModelRenderer(this, 44, 62)
        this.tube3.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube3.setRotationPoint(-2f, -12f, 7f)

        this.tube4 = ModelRenderer(this, 44, 62)
        this.tube4.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube4.setRotationPoint(-2f, -13f, 7f)

        this.tube5 = ModelRenderer(this, 44, 62)
        this.tube5.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube5.setRotationPoint(-2f, -14f, 7f)

        this.tube6 = ModelRenderer(this, 44, 62)
        this.tube6.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube6.setRotationPoint(-2f, -15f, 6f)

        this.tube7 = ModelRenderer(this, 44, 62)
        this.tube7.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube7.setRotationPoint(-2f, -16f, 5f)

        this.tube8 = ModelRenderer(this, 44, 62)
        this.tube8.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube8.setRotationPoint(1f, -10.5f, 5f)

        this.tube9 = ModelRenderer(this, 44, 62)
        this.tube9.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube9.setRotationPoint(1f, -11f, 6f)

        this.tube10 = ModelRenderer(this, 44, 62)
        this.tube10.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube10.setRotationPoint(1f, -12f, 7f)

        this.tube11 = ModelRenderer(this, 44, 62)
        this.tube11.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube11.setRotationPoint(1f, -13f, 7f)

        this.tube12 = ModelRenderer(this, 44, 62)
        this.tube12.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube12.setRotationPoint(1f, -14f, 7f)

        this.tube13 = ModelRenderer(this, 44, 62)
        this.tube13.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube13.setRotationPoint(1f, -15f, 6f)

        this.tube14 = ModelRenderer(this, 44, 62)
        this.tube14.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube14.setRotationPoint(1f, -16f, 5f)
    }

    override fun setRotationAngles(
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float,
        entity: Entity
    ) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity)
        this.bipedHead.showModel = true
        val f6: Float = -14.0f
        this.bipedBody.rotateAngleX = 0.0f
        this.bipedBody.rotationPointY = f6
        this.bipedBody.rotationPointZ = -0.0f
        this.bipedRightLeg.rotateAngleX -= 0.0f
        this.bipedLeftLeg.rotateAngleX -= 0.0f
        this.bipedRightArm.rotateAngleX = (this.bipedRightArm.rotateAngleX * 0.5).toFloat()
        this.bipedLeftArm.rotateAngleX = (this.bipedLeftArm.rotateAngleX * 0.5).toFloat()
        this.bipedRightLeg.rotateAngleX = (this.bipedRightLeg.rotateAngleX * 0.5).toFloat()
        this.bipedLeftLeg.rotateAngleX = (this.bipedLeftLeg.rotateAngleX * 0.5).toFloat()
        val f7: Float = 0.4f

        if (this.bipedRightArm.rotateAngleX > f7) {
            this.bipedRightArm.rotateAngleX = f7
        }
        if (this.bipedLeftArm.rotateAngleX > f7) {
            this.bipedLeftArm.rotateAngleX = f7
        }
        if (this.bipedRightArm.rotateAngleX < -f7) {
            this.bipedRightArm.rotateAngleX = -f7
        }
        if (this.bipedLeftArm.rotateAngleX < -f7) {
            this.bipedLeftArm.rotateAngleX = -f7
        }
        if (this.bipedRightLeg.rotateAngleX > f7) {
            this.bipedRightLeg.rotateAngleX = f7
        }
        if (this.bipedLeftLeg.rotateAngleX > f7) {
            this.bipedLeftLeg.rotateAngleX = f7
        }
        if (this.bipedRightLeg.rotateAngleX < -f7) {
            this.bipedRightLeg.rotateAngleX = -f7
        }
        if (this.bipedLeftLeg.rotateAngleX < -f7) {
            this.bipedLeftLeg.rotateAngleX = -f7
        }

        if (this.isCarrying) {
            this.bipedRightArm.rotateAngleX = -0.5f
            this.bipedLeftArm.rotateAngleX = -0.5f
            this.bipedRightArm.rotateAngleZ = 0.05f
            this.bipedLeftArm.rotateAngleZ = -0.05f
        }

        this.bipedRightArm.rotationPointZ = 0.0f
        this.bipedLeftArm.rotationPointZ = 0.0f
        this.bipedRightLeg.rotationPointZ = 0.0f
        this.bipedLeftLeg.rotationPointZ = 0.0f
        this.bipedRightLeg.rotationPointY = 9.0f + f6
        this.bipedLeftLeg.rotationPointY = 9.0f + f6
        this.bipedHead.rotationPointZ = -0.0f
        this.bipedHead.rotationPointY = f6 + 1.0f
        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ

        this.oxygenMask.rotationPointX = this.bipedHead.rotationPointX
        this.oxygenMask.rotationPointY = this.bipedHead.rotationPointY
        this.oxygenMask.rotationPointZ = this.bipedHead.rotationPointZ
        this.oxygenMask.rotateAngleX = this.bipedHead.rotateAngleX
        this.oxygenMask.rotateAngleY = this.bipedHead.rotateAngleY
        this.oxygenMask.rotateAngleZ = this.bipedHead.rotateAngleZ

        if (this.isAttacking) {
            val f8: Float = 1.0f
            this.bipedHead.rotationPointY -= f8 * 5.0f
            this.oxygenMask.rotationPointY -= f8 * 5.0f
        }
    }

    override fun render(
        entity: Entity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity)
        this.oxygenMask.render(scale)
        this.tank1.render(scale)
        this.tank2.render(scale)
        this.tube1.render(scale)
        this.tube2.render(scale)
        this.tube3.render(scale)
        this.tube4.render(scale)
        this.tube5.render(scale)
        this.tube6.render(scale)
        this.tube7.render(scale)
        this.tube8.render(scale)
        this.tube9.render(scale)
        this.tube10.render(scale)
        this.tube11.render(scale)
        this.tube12.render(scale)
        this.tube13.render(scale)
        this.tube14.render(scale)
    }
}
