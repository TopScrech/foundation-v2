package micdoodle8.mods.galacticraft.planets.mars.client.model

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper

open class ModelCreeperBoss @JvmOverloads constructor(scale: Float = 0.0f) : ModelBase() {
    @JvmField
    var headMain: ModelRenderer

    @JvmField
    var bodyMain: ModelRenderer

    @JvmField
    var rightLegFront: ModelRenderer

    @JvmField
    var leftLegFront: ModelRenderer

    @JvmField
    var rightLeg: ModelRenderer

    @JvmField
    var leftLeg: ModelRenderer

    @JvmField
    var oxygenTank: ModelRenderer

    @JvmField
    var headLeft: ModelRenderer

    @JvmField
    var headRight: ModelRenderer

    @JvmField
    var neckRight: ModelRenderer

    @JvmField
    var neckLeft: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 64
        this.neckRight = ModelRenderer(this, 16, 20)
        this.neckRight.mirror = true
        this.neckRight.addBox(-2.5f, -9f, -1.5f, 5, 9, 3, scale)
        this.neckRight.setRotationPoint(-3f, 10f, 0f)
        this.neckRight.setTextureSize(128, 64)
        this.neckRight.mirror = true
        this.setRotation(this.neckRight, 0f, 0f, -1.169371f)
        this.neckRight.mirror = false
        this.neckLeft = ModelRenderer(this, 16, 20)
        this.neckLeft.addBox(-2.5f, -9f, -1.5f, 5, 9, 3, scale)
        this.neckLeft.setRotationPoint(3f, 10f, 0f)
        this.neckLeft.setTextureSize(128, 64)
        this.neckLeft.mirror = true
        this.setRotation(this.neckLeft, 0f, 0f, 1.169371f)
        this.headMain = ModelRenderer(this, 0, 0)
        this.headMain.addBox(-4f, -8f, -4f, 8, 8, 8, scale)
        this.headMain.setRotationPoint(0f, 6f, 0f)
        this.headMain.setTextureSize(128, 64)
        this.headMain.mirror = true
        this.setRotation(this.headMain, 0f, 0f, 0f)
        this.bodyMain = ModelRenderer(this, 16, 16)
        this.bodyMain.addBox(-4f, 0f, -2f, 8, 12, 4, scale)
        this.bodyMain.setRotationPoint(0f, 6f, 0f)
        this.bodyMain.setTextureSize(128, 64)
        this.bodyMain.mirror = true
        this.setRotation(this.bodyMain, 0f, 0f, 0f)
        this.rightLegFront = ModelRenderer(this, 0, 16)
        this.rightLegFront.addBox(-2f, 0f, -2f, 4, 6, 4, scale)
        this.rightLegFront.setRotationPoint(-2f, 18f, -4f)
        this.rightLegFront.setTextureSize(128, 64)
        this.rightLegFront.mirror = true
        this.setRotation(this.rightLegFront, 0f, 0f, 0f)
        this.leftLegFront = ModelRenderer(this, 0, 16)
        this.leftLegFront.addBox(-2f, 0f, -2f, 4, 6, 4, scale)
        this.leftLegFront.setRotationPoint(2f, 18f, -4f)
        this.leftLegFront.setTextureSize(128, 64)
        this.leftLegFront.mirror = true
        this.setRotation(this.leftLegFront, 0f, 0f, 0f)
        this.rightLeg = ModelRenderer(this, 0, 16)
        this.rightLeg.addBox(0f, 0f, -2f, 4, 6, 4, scale)
        this.rightLeg.setRotationPoint(-4f, 18f, 4f)
        this.rightLeg.setTextureSize(128, 64)
        this.rightLeg.mirror = true
        this.setRotation(this.rightLeg, 0f, 0f, 0f)
        this.leftLeg = ModelRenderer(this, 0, 16)
        this.leftLeg.addBox(-2f, 0f, -2f, 4, 6, 4, scale)
        this.leftLeg.setRotationPoint(2f, 18f, 4f)
        this.leftLeg.setTextureSize(128, 64)
        this.leftLeg.mirror = true
        this.setRotation(this.leftLeg, 0f, 0f, 0f)
        this.oxygenTank = ModelRenderer(this, 40, 0)
        this.oxygenTank.addBox(-5f, -9f, -5f, 10, 10, 10, scale)
        this.oxygenTank.setRotationPoint(0f, 6f, 0f)
        this.oxygenTank.setTextureSize(128, 64)
        this.oxygenTank.mirror = true
        this.setRotation(this.oxygenTank, 0f, 0f, 0f)
        this.headLeft = ModelRenderer(this, 0, 0)
        this.headLeft.addBox(1f, -9f, -4f, 8, 8, 8, scale)
        this.headLeft.setRotationPoint(3f, 6f, 0.1f)
        this.headLeft.setTextureSize(128, 64)
        this.headLeft.mirror = true
        this.setRotation(this.headLeft, 0f, 0f, 0.7853982f)
        this.headRight = ModelRenderer(this, 0, 0)
        this.headRight.addBox(-9f, -9f, -4f, 8, 8, 8, scale)
        this.headRight.setRotationPoint(-3f, 6f, -0.1f)
        this.headRight.setTextureSize(128, 64)
        this.headRight.mirror = true
        this.setRotation(this.headRight, 0f, 0f, -0.7853982f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)

        val creeper: EntityCreeperBoss = entity as EntityCreeperBoss

        if (creeper.headsRemaining > 2) {
            this.headLeft.render(f5)
            this.neckLeft.render(f5)
            this.headRight.render(f5)
            this.neckRight.render(f5)
            this.headMain.render(f5)
            this.oxygenTank.render(f5)
        } else if (creeper.headsRemaining > 1) {
            this.headRight.render(f5)
            this.neckRight.render(f5)
            this.headMain.render(f5)
            this.oxygenTank.render(f5)
        } else if (creeper.headsRemaining > 0) {
            this.headMain.render(f5)
            this.oxygenTank.render(f5)
        }

        this.bodyMain.render(f5)
        this.rightLegFront.render(f5)
        this.leftLegFront.render(f5)
        this.rightLeg.render(f5)
        this.leftLeg.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        this.headMain.rotateAngleY = f3 / Constants.RADIANS_TO_DEGREES
        this.headMain.rotateAngleX = f4 / Constants.RADIANS_TO_DEGREES
        this.oxygenTank.rotateAngleY = f3 / Constants.RADIANS_TO_DEGREES
        this.oxygenTank.rotateAngleX = f4 / Constants.RADIANS_TO_DEGREES
        this.rightLegFront.rotateAngleX = MathHelper.cos(f * 0.6662f) * 1.4f * f1
        this.leftLegFront.rotateAngleX = MathHelper.cos(f * 0.6662f + Math.PI.toFloat()) * 2f * f1
        this.leftLeg.rotateAngleX = MathHelper.cos(f * 0.6662f + Math.PI.toFloat()) * 2f * f1
        this.rightLeg.rotateAngleX = MathHelper.cos(f * 0.6662f) * 2f * f1
    }
}
