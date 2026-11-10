package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import kotlin.math.pow

open class ModelEvolvedSkeletonBoss constructor() : ModelBase() {
    private val upperHead: ModelRenderer
    private val pelvis: ModelRenderer
    private val sternum: ModelRenderer
    private val rightLeg: ModelRenderer
    private val rightArm: ModelRenderer
    private val spine: ModelRenderer
    private val leftArm: ModelRenderer
    private val leftLeg: ModelRenderer
    private val leftFrontBotRib: ModelRenderer
    private val leftFrontTopRib: ModelRenderer
    private val leftFront2ndRib: ModelRenderer
    private val leftFront3rdRib: ModelRenderer
    private val leftSideBotRib: ModelRenderer
    private val leftSide3rdRib: ModelRenderer
    private val leftSide2ndRib: ModelRenderer
    private val leftSideTopRib: ModelRenderer
    private val rightSideTopRib: ModelRenderer
    private val rightSide2ndRib: ModelRenderer
    private val rightSide3rdRib: ModelRenderer
    private val rightSideBotRib: ModelRenderer
    private val rightFrontBotRib: ModelRenderer
    private val rightFront3rdRib: ModelRenderer
    private val rightFront2ndRib: ModelRenderer
    private val rightFrontTopRib: ModelRenderer
    private val leftBackTopRib: ModelRenderer
    private val leftBack2ndRib: ModelRenderer
    private val leftBack3rdRib: ModelRenderer
    private val leftBackBotRib: ModelRenderer
    private val rightBackBotRib: ModelRenderer
    private val rightBack3rdRib: ModelRenderer
    private val rightBack2ndRib: ModelRenderer
    private val rightBackTopRib: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 128

        val halfPI: Float = Constants.halfPI

        this.upperHead = ModelRenderer(this, 0, 16)
        this.upperHead.addBox(-4f, -8f, -6f, 8, 8, 8)
        this.upperHead.setRotationPoint(0f, -24f, 6f)
        this.upperHead.setTextureSize(64, 32)
        this.upperHead.mirror = true
        this.setRotation(this.upperHead, 0.122173f, 0f, 0f)
        this.pelvis = ModelRenderer(this, 32, 19)
        this.pelvis.addBox(-6f, 0f, -3f, 12, 5, 5)
        this.pelvis.setRotationPoint(0f, -2f, 5f)
        this.pelvis.setTextureSize(64, 32)
        this.pelvis.mirror = true
        this.setRotation(this.pelvis, 0f, 0f, 0f)
        this.sternum = ModelRenderer(this, 0, 0)
        this.sternum.addBox(-1.5f, 0f, -1f, 3, 9, 1)
        this.sternum.setRotationPoint(0f, -21f, 2f)
        this.sternum.setTextureSize(64, 32)
        this.sternum.mirror = true
        this.setRotation(this.sternum, 0f, 0f, 0f)
        this.rightLeg = ModelRenderer(this, 56, 33)
        this.rightLeg.mirror = true
        this.rightLeg.addBox(-2f, 0f, -2f, 3, 26, 3)
        this.rightLeg.setRotationPoint(-5f, 0f, 5f)
        this.rightLeg.setTextureSize(64, 32)
        this.rightLeg.mirror = true
        this.setRotation(this.rightLeg, 0f, 0f, 0f)
        this.rightLeg.mirror = false
        this.rightArm = ModelRenderer(this, 56, 33)
        this.rightArm.addBox(-2f, -2f, -1.5f, 3, 24, 3)
        this.rightArm.setRotationPoint(-8f, -20f, 5f)
        this.rightArm.setTextureSize(64, 32)
        this.rightArm.mirror = true
        this.setRotation(this.rightArm, 0f, 0f, 0f)
        this.spine = ModelRenderer(this, 32, 33)
        this.spine.addBox(-1.5f, 0f, -1f, 3, 22, 2)
        this.spine.setRotationPoint(0f, -24f, 6f)
        this.spine.setTextureSize(64, 32)
        this.spine.mirror = true
        this.setRotation(this.spine, 0f, 0f, 0f)
        this.leftArm = ModelRenderer(this, 56, 33)
        this.leftArm.addBox(-1f, -2f, -1.5f, 3, 24, 3)
        this.leftArm.setRotationPoint(8f, -20f, 5f)
        this.leftArm.setTextureSize(64, 32)
        this.leftArm.mirror = true
        this.setRotation(this.leftArm, 0f, 0f, 0f)
        this.leftLeg = ModelRenderer(this, 56, 33)
        this.leftLeg.addBox(-2f, 0f, -2f, 3, 26, 3)
        this.leftLeg.setRotationPoint(6f, 0f, 5f)
        this.leftLeg.setTextureSize(64, 32)
        this.leftLeg.mirror = true
        this.setRotation(this.leftLeg, 0f, 0f, 0f)
        this.leftFrontBotRib = ModelRenderer(this, 0, 0)
        this.leftFrontBotRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftFrontBotRib.setRotationPoint(7f, -13f, 2f)
        this.leftFrontBotRib.setTextureSize(64, 32)
        this.leftFrontBotRib.mirror = true
        this.setRotation(this.leftFrontBotRib, 0f, -halfPI, 0f)
        this.leftFrontTopRib = ModelRenderer(this, 0, 0)
        this.leftFrontTopRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftFrontTopRib.setRotationPoint(7f, -22f, 2f)
        this.leftFrontTopRib.setTextureSize(64, 32)
        this.leftFrontTopRib.mirror = true
        this.setRotation(this.leftFrontTopRib, 0f, -halfPI, 0f)
        this.leftFront2ndRib = ModelRenderer(this, 0, 0)
        this.leftFront2ndRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftFront2ndRib.setRotationPoint(7f, -19f, 2f)
        this.leftFront2ndRib.setTextureSize(64, 32)
        this.leftFront2ndRib.mirror = true
        this.setRotation(this.leftFront2ndRib, 0f, -halfPI, 0f)
        this.leftFront3rdRib = ModelRenderer(this, 0, 0)
        this.leftFront3rdRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftFront3rdRib.setRotationPoint(7f, -16f, 2f)
        this.leftFront3rdRib.setTextureSize(64, 32)
        this.leftFront3rdRib.mirror = true
        this.setRotation(this.leftFront3rdRib, 0f, -halfPI, 0f)
        this.leftSideBotRib = ModelRenderer(this, 0, 0)
        this.leftSideBotRib.addBox(-1f, 0f, -6f, 1, 2, 6)
        this.leftSideBotRib.setRotationPoint(7f, -13f, 7f)
        this.leftSideBotRib.setTextureSize(64, 32)
        this.leftSideBotRib.mirror = true
        this.setRotation(this.leftSideBotRib, 0f, 0f, 0f)
        this.leftSide3rdRib = ModelRenderer(this, 0, 0)
        this.leftSide3rdRib.addBox(-1f, 0f, -6f, 1, 2, 6)
        this.leftSide3rdRib.setRotationPoint(7f, -16f, 7f)
        this.leftSide3rdRib.setTextureSize(64, 32)
        this.leftSide3rdRib.mirror = true
        this.setRotation(this.leftSide3rdRib, 0f, 0f, 0f)
        this.leftSide2ndRib = ModelRenderer(this, 0, 0)
        this.leftSide2ndRib.addBox(-1f, 0f, -6f, 1, 2, 6)
        this.leftSide2ndRib.setRotationPoint(7f, -19f, 7f)
        this.leftSide2ndRib.setTextureSize(64, 32)
        this.leftSide2ndRib.mirror = true
        this.setRotation(this.leftSide2ndRib, 0f, 0f, 0f)
        this.leftSideTopRib = ModelRenderer(this, 0, 0)
        this.leftSideTopRib.addBox(-1f, 0f, -6f, 1, 2, 6)
        this.leftSideTopRib.setRotationPoint(7f, -22f, 7f)
        this.leftSideTopRib.setTextureSize(64, 32)
        this.leftSideTopRib.mirror = true
        this.setRotation(this.leftSideTopRib, 0f, 0f, 0f)
        this.rightSideTopRib = ModelRenderer(this, 0, 0)
        this.rightSideTopRib.addBox(0f, 0f, -6f, 1, 2, 6)
        this.rightSideTopRib.setRotationPoint(-7f, -22f, 7f)
        this.rightSideTopRib.setTextureSize(64, 32)
        this.rightSideTopRib.mirror = true
        this.setRotation(this.rightSideTopRib, 0f, 0f, 0f)
        this.rightSide2ndRib = ModelRenderer(this, 0, 0)
        this.rightSide2ndRib.addBox(0f, 0f, -6f, 1, 2, 6)
        this.rightSide2ndRib.setRotationPoint(-7f, -19f, 7f)
        this.rightSide2ndRib.setTextureSize(64, 32)
        this.rightSide2ndRib.mirror = true
        this.setRotation(this.rightSide2ndRib, 0f, 0f, 0f)
        this.rightSide3rdRib = ModelRenderer(this, 0, 0)
        this.rightSide3rdRib.addBox(0f, 0f, -6f, 1, 2, 6)
        this.rightSide3rdRib.setRotationPoint(-7f, -16f, 7f)
        this.rightSide3rdRib.setTextureSize(64, 32)
        this.rightSide3rdRib.mirror = true
        this.setRotation(this.rightSide3rdRib, 0f, 0f, 0f)
        this.rightSideBotRib = ModelRenderer(this, 0, 0)
        this.rightSideBotRib.addBox(0f, 0f, -6f, 1, 2, 6)
        this.rightSideBotRib.setRotationPoint(-7f, -13f, 7f)
        this.rightSideBotRib.setTextureSize(64, 32)
        this.rightSideBotRib.mirror = true
        this.setRotation(this.rightSideBotRib, 0f, 0f, 0f)
        this.rightFrontBotRib = ModelRenderer(this, 0, 0)
        this.rightFrontBotRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightFrontBotRib.setRotationPoint(-7f, -13f, 2f)
        this.rightFrontBotRib.setTextureSize(64, 32)
        this.rightFrontBotRib.mirror = true
        this.setRotation(this.rightFrontBotRib, 0f, halfPI, 0f)
        this.rightFront3rdRib = ModelRenderer(this, 0, 0)
        this.rightFront3rdRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightFront3rdRib.setRotationPoint(-7f, -16f, 2f)
        this.rightFront3rdRib.setTextureSize(64, 32)
        this.rightFront3rdRib.mirror = true
        this.setRotation(this.rightFront3rdRib, 0f, halfPI, 0f)
        this.rightFront2ndRib = ModelRenderer(this, 0, 0)
        this.rightFront2ndRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightFront2ndRib.setRotationPoint(-7f, -19f, 2f)
        this.rightFront2ndRib.setTextureSize(64, 32)
        this.rightFront2ndRib.mirror = true
        this.setRotation(this.rightFront2ndRib, 0f, halfPI, 0f)
        this.rightFrontTopRib = ModelRenderer(this, 0, 0)
        this.rightFrontTopRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightFrontTopRib.setRotationPoint(-7f, -22f, 2f)
        this.rightFrontTopRib.setTextureSize(64, 32)
        this.rightFrontTopRib.mirror = true
        this.setRotation(this.rightFrontTopRib, 0f, halfPI, 0f)
        this.leftBackTopRib = ModelRenderer(this, 0, 0)
        this.leftBackTopRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftBackTopRib.setRotationPoint(7f, -22f, 7f)
        this.leftBackTopRib.setTextureSize(64, 32)
        this.leftBackTopRib.mirror = true
        this.setRotation(this.leftBackTopRib, 0f, -halfPI, 0f)
        this.leftBack2ndRib = ModelRenderer(this, 0, 0)
        this.leftBack2ndRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftBack2ndRib.setRotationPoint(7f, -19f, 7f)
        this.leftBack2ndRib.setTextureSize(64, 32)
        this.leftBack2ndRib.mirror = true
        this.setRotation(this.leftBack2ndRib, 0f, -halfPI, 0f)
        this.leftBack3rdRib = ModelRenderer(this, 0, 0)
        this.leftBack3rdRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftBack3rdRib.setRotationPoint(7f, -16f, 7f)
        this.leftBack3rdRib.setTextureSize(64, 32)
        this.leftBack3rdRib.mirror = true
        this.setRotation(this.leftBack3rdRib, 0f, -halfPI, 0f)
        this.leftBackBotRib = ModelRenderer(this, 0, 0)
        this.leftBackBotRib.addBox(-1f, 0f, 0f, 1, 2, 6)
        this.leftBackBotRib.setRotationPoint(7f, -13f, 7f)
        this.leftBackBotRib.setTextureSize(64, 32)
        this.leftBackBotRib.mirror = true
        this.setRotation(this.leftBackBotRib, 0f, -halfPI, 0f)
        this.rightBackBotRib = ModelRenderer(this, 0, 0)
        this.rightBackBotRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightBackBotRib.setRotationPoint(-7f, -13f, 7f)
        this.rightBackBotRib.setTextureSize(64, 32)
        this.rightBackBotRib.mirror = true
        this.setRotation(this.rightBackBotRib, 0f, halfPI, 0f)
        this.rightBack3rdRib = ModelRenderer(this, 0, 0)
        this.rightBack3rdRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightBack3rdRib.setRotationPoint(-7f, -16f, 7f)
        this.rightBack3rdRib.setTextureSize(64, 32)
        this.rightBack3rdRib.mirror = true
        this.setRotation(this.rightBack3rdRib, 0f, halfPI, 0f)
        this.rightBack2ndRib = ModelRenderer(this, 0, 0)
        this.rightBack2ndRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightBack2ndRib.setRotationPoint(-7f, -19f, 7f)
        this.rightBack2ndRib.setTextureSize(64, 32)
        this.rightBack2ndRib.mirror = true
        this.setRotation(this.rightBack2ndRib, 0f, halfPI, 0f)
        this.rightBackTopRib = ModelRenderer(this, 0, 0)
        this.rightBackTopRib.addBox(0f, 0f, 0f, 1, 2, 6)
        this.rightBackTopRib.setRotationPoint(-7f, -22f, 7f)
        this.rightBackTopRib.setTextureSize(64, 32)
        this.rightBackTopRib.mirror = true
        this.setRotation(this.rightBackTopRib, 0f, halfPI, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        this.upperHead.render(f5)
        this.pelvis.render(f5)
        this.sternum.render(f5)
        this.rightLeg.render(f5)
        this.rightArm.render(f5)
        this.spine.render(f5)
        this.leftArm.render(f5)
        this.leftLeg.render(f5)
        this.leftFrontBotRib.render(f5)
        this.leftFrontTopRib.render(f5)
        this.leftFront2ndRib.render(f5)
        this.leftFront3rdRib.render(f5)
        this.leftSideBotRib.render(f5)
        this.leftSide3rdRib.render(f5)
        this.leftSide2ndRib.render(f5)
        this.leftSideTopRib.render(f5)
        this.rightSideTopRib.render(f5)
        this.rightSide2ndRib.render(f5)
        this.rightSide3rdRib.render(f5)
        this.rightSideBotRib.render(f5)
        this.rightFrontBotRib.render(f5)
        this.rightFront3rdRib.render(f5)
        this.rightFront2ndRib.render(f5)
        this.rightFrontTopRib.render(f5)
        this.leftBackTopRib.render(f5)
        this.leftBack2ndRib.render(f5)
        this.leftBack3rdRib.render(f5)
        this.leftBackBotRib.render(f5)
        this.rightBackBotRib.render(f5)
        this.rightBack3rdRib.render(f5)
        this.rightBack2ndRib.render(f5)
        this.rightBackTopRib.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(
        par1: Float,
        par2: Float,
        par3: Float,
        par4: Float,
        par5: Float,
        par6: Float,
        e: Entity
    ) {
        val floatPI: Float = 3.1415927f

        val boss: EntitySkeletonBoss = e as EntitySkeletonBoss
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, e)
        this.upperHead.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES
        this.upperHead.rotateAngleX = par5 / Constants.RADIANS_TO_DEGREES
        this.rightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662f + floatPI) * 2.0f * par2 * 0.5f
        this.leftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.5f
        this.rightArm.rotateAngleZ = 0.0f
        this.leftArm.rotateAngleZ = 0.0f
        this.rightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2
        this.leftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662f + floatPI) * 1.4f * par2
        this.rightLeg.rotateAngleY = 0.0f
        this.leftLeg.rotateAngleY = 0.0f

        if (this.isRiding) {
            this.rightArm.rotateAngleX += -(floatPI / 5f)
            this.leftArm.rotateAngleX += -(floatPI / 5f)
            this.rightLeg.rotateAngleX = -(floatPI * 2f / 5f)
            this.leftLeg.rotateAngleX = -(floatPI * 2f / 5f)
            this.rightLeg.rotateAngleY = floatPI / 10f
            this.leftLeg.rotateAngleY = -(floatPI / 10f)
        }

        this.rightArm.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES
        this.leftArm.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES
        var var7: Float
        val var8: Float

        if (this.swingProgress > -9990.0f) {
            var7 = this.swingProgress
            this.spine.rotateAngleY = MathHelper.sin(MathHelper.sqrt(var7) * Constants.twoPI) * 0.2f

            this.rightArm.rotationPointZ = MathHelper.sin(this.spine.rotateAngleY) * 5.0f
            this.rightArm.rotationPointX = -MathHelper.cos(this.spine.rotateAngleY) * 5.0f
            this.leftArm.rotationPointZ = -MathHelper.sin(this.spine.rotateAngleY) * 5.0f
            this.leftArm.rotationPointX = MathHelper.cos(this.spine.rotateAngleY) * 5.0f
            this.rightArm.rotateAngleY += this.spine.rotateAngleY
            this.leftArm.rotateAngleY += this.spine.rotateAngleY
            this.leftArm.rotateAngleX += this.spine.rotateAngleY
            var7 = 1.0f - this.swingProgress
            var7 *= var7
            var7 *= var7
            var7 = 1.0f - var7
            var8 = MathHelper.sin(var7 * floatPI)
            val var9: Float =
                MathHelper.sin(this.swingProgress * floatPI) * -(this.upperHead.rotateAngleX - 0.7f) * 0.75f
            this.rightArm.rotateAngleX = (this.rightArm.rotateAngleX - (var8 * 1.2 + var9)).toFloat()
            this.rightArm.rotateAngleY += this.spine.rotateAngleY * 2.0f
            this.rightArm.rotateAngleZ = MathHelper.sin(this.swingProgress * floatPI) * -0.4f
        }

        val f6: Float = MathHelper.sin(this.swingProgress * floatPI)
        val f7: Float = MathHelper.sin((1.0f - (1.0f - this.swingProgress) * (1.0f - this.swingProgress)) * floatPI)
        this.rightArm.rotateAngleZ = 0.0f
        this.leftArm.rotateAngleZ = 0.0f
        this.rightArm.rotateAngleY = 0.5f * (par4 / Constants.RADIANS_TO_DEGREES) + -(0.1f - f6 * 0.6f)
        this.leftArm.rotateAngleY = 0.5f * (par4 / Constants.RADIANS_TO_DEGREES) + 0.1f - f6 * 0.6f
        this.rightArm.rotateAngleX = -(floatPI / 2f)
        this.leftArm.rotateAngleX = -(floatPI / 2f)
        this.rightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
        this.leftArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
        this.rightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f
        this.leftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f
        this.rightArm.rotateAngleX += MathHelper.sin(par3 * 0.067f) * 0.05f
        this.leftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067f) * 0.05f

        if (e.deathTicks > 0) {
            this.leftArm.rotateAngleX = -(floatPI / 2f) + (e.deathTicks.toDouble()
                .pow(2.0) / 5.0f).toFloat() / 3.0f / Constants.RADIANS_TO_DEGREES
            this.rightArm.rotateAngleX = -(floatPI / 2f) + (e.deathTicks.toDouble()
                .pow(2.0) / 5.0f).toFloat() / 2.0f / Constants.RADIANS_TO_DEGREES
        }

        if (boss.throwTimer + boss.postThrowDelay > 0) {
            this.rightArm.rotateAngleX -= MathHelper.cos((boss.throwTimer + boss.postThrowDelay) * 0.05f) * 1.2f + 0.05f
            this.leftArm.rotateAngleX -= MathHelper.cos((boss.throwTimer + boss.postThrowDelay) * 0.05f) * 1.2f + 0.05f
        }
    }

    open fun postRenderArm(scale: Float, type: ItemCameraTransforms.TransformType?) {
        if (type == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
            this.rightArm.postRender(scale)
        } else {
            this.leftArm.postRender(scale)
        }
    }
}
