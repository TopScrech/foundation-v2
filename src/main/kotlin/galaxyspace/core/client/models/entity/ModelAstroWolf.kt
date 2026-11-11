package galaxyspace.core.client.models.entity

import galaxyspace.core.prefab.entities.EntityAstroWolf
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class ModelAstroWolf constructor() : ModelBase() {
    /** main box for the wolf head  */
    @JvmField
    var wolfHeadMain: ModelRenderer

    /** The wolf's body  */
    @JvmField
    var wolfBody: ModelRenderer

    /** Wolf'se first leg  */
    @JvmField
    var wolfLeg1: ModelRenderer

    /** Wolf's second leg  */
    @JvmField
    var wolfLeg2: ModelRenderer

    /** Wolf's third leg  */
    @JvmField
    var wolfLeg3: ModelRenderer

    /** Wolf's fourth leg  */
    @JvmField
    var wolfLeg4: ModelRenderer

    /** Helmet  */
    @JvmField
    var wolfHelmet: ModelRenderer

    /** The wolf's tail  */
    @JvmField
    var wolfTail: ModelRenderer

    /** The wolf's mane  */
    @JvmField
    var wolfMane: ModelRenderer

    @JvmField
    var tube1: ModelRenderer

    @JvmField
    var tube2: ModelRenderer

    @JvmField
    var tube3: ModelRenderer

    @JvmField
    var tube1_1: ModelRenderer

    @JvmField
    var tube1_2: ModelRenderer

    @JvmField
    var tube1_3: ModelRenderer

    @JvmField
    var tube1_4: ModelRenderer

    @JvmField
    var tube1_5: ModelRenderer

    @JvmField
    var oxBallon: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 64
        val f: Float = 0.0f
        val f1: Float = 13.5f
        this.wolfHeadMain = ModelRenderer(this, 0, 0)
        this.wolfHeadMain.addBox(-2.0f, -3.0f, -2.0f, 6, 6, 4, 0.0f)
        this.wolfHeadMain.setRotationPoint(-1.0f, 13.5f, -7.0f)
        this.wolfBody = ModelRenderer(this, 18, 14)
        this.wolfBody.addBox(-3.0f, -2.0f, -3.0f, 6, 9, 6, 0.0f)
        this.wolfBody.setRotationPoint(0.0f, 14.0f, 2.0f)
        this.wolfMane = ModelRenderer(this, 21, 0)
        this.wolfMane.addBox(-3.0f, -3.0f, -3.0f, 8, 6, 7, 0.0f)
        this.wolfMane.setRotationPoint(-1.0f, 14.0f, 2.0f)
        this.wolfLeg1 = ModelRenderer(this, 0, 18)
        this.wolfLeg1.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f)
        this.wolfLeg1.setRotationPoint(-2.5f, 16.0f, 7.0f)
        this.wolfLeg2 = ModelRenderer(this, 0, 18)
        this.wolfLeg2.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f)
        this.wolfLeg2.setRotationPoint(0.5f, 16.0f, 7.0f)
        this.wolfLeg3 = ModelRenderer(this, 0, 18)
        this.wolfLeg3.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f)
        this.wolfLeg3.setRotationPoint(-2.5f, 16.0f, -4.0f)
        this.wolfLeg4 = ModelRenderer(this, 0, 18)
        this.wolfLeg4.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f)
        this.wolfLeg4.setRotationPoint(0.5f, 16.0f, -4.0f)
        this.wolfTail = ModelRenderer(this, 9, 18)
        this.wolfTail.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f)
        this.wolfTail.setRotationPoint(-1.0f, 12.0f, 8.0f)

        this.wolfHelmet = ModelRenderer(this, 51, 0)
        this.wolfHelmet.setRotationPoint(-0.0f, 12.6f, -9.2f)
        this.wolfHelmet.addBox(-5.0f, -5.0f, -5.0f, 10, 10, 10, -0.6f)
        this.setRotateAngle(wolfHelmet, 0.0f, 0.0f, 8.726646259971648E-4f)

        this.wolfHeadMain.setTextureOffset(16, 14).addBox(-2.0f, -5.0f, 0.0f, 2, 2, 1, 0.0f)
        this.wolfHeadMain.setTextureOffset(16, 14).addBox(2.0f, -5.0f, 0.0f, 2, 2, 1, 0.0f)
        this.wolfHeadMain.setTextureOffset(0, 10).addBox(-0.5f, 0.0f, -5.0f, 3, 3, 4, 0.0f)

        this.tube1 = ModelRenderer(this, 51, 20)
        this.tube1.setRotationPoint(-0.5f, 9.0f, -6.0f)
        this.tube1.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)
        this.tube2 = ModelRenderer(this, 51, 20)
        this.tube2.setRotationPoint(-0.5f, 9.3f, -0.5f)
        this.tube2.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)
        this.tube3 = ModelRenderer(this, 51, 20)
        this.tube3.setRotationPoint(-0.5f, 10.1f, 0.5f)
        this.tube3.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)

        this.tube1_1 = ModelRenderer(this, 51, 20)
        this.tube1_1.setRotationPoint(-0.5f, 9.0f, -5.0f)
        this.tube1_1.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)
        this.tube1_2 = ModelRenderer(this, 51, 20)
        this.tube1_2.setRotationPoint(-0.5f, 9.0f, -4.0f)
        this.tube1_2.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)
        this.tube1_3 = ModelRenderer(this, 51, 20)
        this.tube1_3.setRotationPoint(-0.5f, 9.0f, -3.0f)
        this.tube1_3.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)
        this.tube1_4 = ModelRenderer(this, 51, 20)
        this.tube1_4.setRotationPoint(-0.5f, 9.0f, -2.0f)
        this.tube1_4.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)
        this.tube1_5 = ModelRenderer(this, 51, 20)
        this.tube1_5.setRotationPoint(-0.5f, 9.0f, -1.0f)
        this.tube1_5.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f)

        this.oxBallon = ModelRenderer(this, 51, 20)
        this.oxBallon.setRotationPoint(-1.0f, 9.0f, 0.0f)
        this.oxBallon.addBox(0.0f, 0.0f, 0.0f, 2, 2, 5, 0.0f)
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    override fun render(
        entityIn: Entity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn)

        val entitywolf: EntityAstroWolf = entityIn as EntityAstroWolf

        if (this.isChild) {
            val f: Float = 2.0f
            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0f, 5.0f * scale, 2.0f * scale)
            this.wolfHeadMain.renderWithRotation(scale)
            GlStateManager.popMatrix()
            GlStateManager.pushMatrix()
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            GlStateManager.translate(0.0f, 24.0f * scale, 0.0f)
            this.wolfBody.render(scale)
            this.wolfLeg1.render(scale)
            this.wolfLeg2.render(scale)
            this.wolfLeg3.render(scale)
            this.wolfLeg4.render(scale)
            this.wolfTail.renderWithRotation(scale)
            this.wolfMane.render(scale)

            if (entitywolf.wolfInventory.getItem(0)
                    .getItem() === GCItems.oxMask && !OxygenUtil.isAABBInBreathableAirBlock(entitywolf)
            ) {
                this.wolfHelmet.renderWithRotation(scale)
                this.tube1.render(scale)
                this.tube2.render(scale)
                this.tube3.render(scale)
                this.tube1_1.render(scale)
                this.tube1_2.render(scale)
                this.tube1_3.render(scale)
                this.tube1_4.render(scale)
                this.tube1_5.render(scale)
            }

            if (entitywolf.wolfInventory.getItem(2).getItem() is ItemOxygenTank) this.oxBallon.render(scale)

            GlStateManager.popMatrix()
        } else {
            this.wolfHeadMain.renderWithRotation(scale)
            this.wolfBody.render(scale)
            this.wolfLeg1.render(scale)
            this.wolfLeg2.render(scale)
            this.wolfLeg3.render(scale)
            this.wolfLeg4.render(scale)
            this.wolfTail.renderWithRotation(scale)
            this.wolfMane.render(scale)
            if (entitywolf.wolfInventory.getItem(0)
                    .getItem() === GCItems.oxMask && !OxygenUtil.isAABBInBreathableAirBlock(entitywolf)
            ) {
                this.wolfHelmet.render(scale)
                this.tube1.render(scale)
                this.tube2.render(scale)
                this.tube3.render(scale)
                this.tube1_1.render(scale)
                this.tube1_2.render(scale)
                this.tube1_3.render(scale)
                this.tube1_4.render(scale)
                this.tube1_5.render(scale)
            }
            if (entitywolf.wolfInventory.getItem(2).getItem() is ItemOxygenTank) this.oxBallon.render(scale)
        }
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    override fun setLivingAnimations(
        entitylivingbaseIn: EntityLivingBase,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTickTime: Float
    ) {
        val entitywolf: EntityAstroWolf = entitylivingbaseIn as EntityAstroWolf

        if (entitywolf.isAngry()) {
            this.wolfTail.rotateAngleY = 0.0f
        } else {
            this.wolfTail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        }

        if (entitywolf.isSitting()) {
            this.wolfMane.setRotationPoint(-1.0f, 16.0f, -3.0f)
            this.wolfMane.rotateAngleX = (Math.PI.toFloat() * 2f / 5f)
            this.wolfMane.rotateAngleY = 0.0f
            this.wolfBody.setRotationPoint(0.0f, 18.0f, 0.0f)
            this.wolfBody.rotateAngleX = (Math.PI.toFloat() / 4f)
            this.wolfTail.setRotationPoint(-1.0f, 21.0f, 6.0f)
            this.wolfLeg1.setRotationPoint(-2.5f, 22.0f, 2.0f)
            this.wolfLeg1.rotateAngleX = (Math.PI.toFloat() * 3f / 2f)
            this.wolfLeg2.setRotationPoint(0.5f, 22.0f, 2.0f)
            this.wolfLeg2.rotateAngleX = (Math.PI.toFloat() * 3f / 2f)
            this.wolfLeg3.rotateAngleX = 5.811947f
            this.wolfLeg3.setRotationPoint(-2.49f, 17.0f, -4.0f)
            this.wolfLeg4.rotateAngleX = 5.811947f
            this.wolfLeg4.setRotationPoint(0.51f, 17.0f, -4.0f)

            this.tube1_1.offsetY = 0.05f
            this.tube1_2.offsetY = 0.08f
            this.tube1_3.offsetY = 0.1f
            this.tube1_4.offsetY = 0.13f
            this.tube1_5.offsetY = 0.15f
            this.tube2.offsetZ = 0.03f
            this.tube2.offsetY = 0.18f
            this.tube3.offsetY = 0.22f

            this.oxBallon.rotateAngleX = 14.95f
            this.oxBallon.rotateAngleY = 0f
            this.oxBallon.offsetX = 0f
            this.oxBallon.offsetY = 0.55f
            this.oxBallon.offsetZ = 0.25f
        } else {
            this.wolfBody.setRotationPoint(0.0f, 14.0f, 2.0f)
            this.wolfBody.rotateAngleX = (Math.PI.toFloat() / 2f)
            this.wolfMane.setRotationPoint(-1.0f, 14.0f, -3.0f)
            this.wolfMane.rotateAngleX = this.wolfBody.rotateAngleX
            this.wolfTail.setRotationPoint(-1.0f, 12.0f, 8.0f)
            this.wolfLeg1.setRotationPoint(-2.5f, 16.0f, 7.0f)
            this.wolfLeg2.setRotationPoint(0.5f, 16.0f, 7.0f)
            this.wolfLeg3.setRotationPoint(-2.5f, 16.0f, -4.0f)
            this.wolfLeg4.setRotationPoint(0.5f, 16.0f, -4.0f)
            this.wolfLeg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
            this.wolfLeg2.rotateAngleX =
                MathHelper.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
            this.wolfLeg3.rotateAngleX =
                MathHelper.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
            this.wolfLeg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount

            this.tube1_1.offsetY = 0.0f
            this.tube1_2.offsetY = 0.0f
            this.tube1_3.offsetY = 0.0f
            this.tube1_4.offsetY = 0.0f
            this.tube1_5.offsetY = 0.0f
            this.tube2.offsetZ = 0.0f
            this.tube2.offsetY = 0.0f
            this.tube3.offsetY = 0.0f

            this.oxBallon.rotateAngleX = 0f
            this.oxBallon.rotateAngleY = 3.15f
            this.oxBallon.offsetX = 0.13f
            this.oxBallon.offsetY = 0f
            this.oxBallon.offsetZ = 0.4f
        }

        this.wolfHeadMain.rotateAngleZ =
            entitywolf.getInterestedAngle(partialTickTime) + entitywolf.getShakeAngle(partialTickTime, 0.0f)
        this.wolfHelmet.rotateAngleZ =
            entitywolf.getInterestedAngle(partialTickTime) + entitywolf.getShakeAngle(partialTickTime, 0.0f)

        this.wolfMane.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.08f)
        this.wolfBody.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.16f)
        this.wolfTail.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.2f)
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    override fun setRotationAngles(
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scaleFactor: Float,
        entityIn: Entity
    ) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn)
        this.wolfHeadMain.rotateAngleX = headPitch * 0.017453292f
        this.wolfHeadMain.rotateAngleY = netHeadYaw * 0.017453292f

        this.wolfHelmet.rotateAngleX = headPitch * 0.017453292f
        this.wolfHelmet.rotateAngleY = netHeadYaw * 0.017453292f

        this.wolfTail.rotateAngleX = ageInTicks
    }

    open fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
        modelRenderer.rotateAngleX = x
        modelRenderer.rotateAngleY = y
        modelRenderer.rotateAngleZ = z
    }
}
