package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.util.Mth as MathHelper
import org.lwjgl.opengl.GL11

open class ModelEvolvedZombie constructor(par1: Float, halfSizeTexture: Boolean, renderGear: Boolean) :
    ModelBiped() {
    @JvmField
    var leftOxygenTank: ModelRenderer

    @JvmField
    var rightOxygenTank: ModelRenderer

    @JvmField
    var tubeRight2: ModelRenderer

    @JvmField
    var tubeLeft1: ModelRenderer

    @JvmField
    var tubeRight3: ModelRenderer

    @JvmField
    var tubeRight4: ModelRenderer

    @JvmField
    var tubeRight5: ModelRenderer

    @JvmField
    var tubeLeft6: ModelRenderer

    @JvmField
    var tubeRight7: ModelRenderer

    @JvmField
    var tubeRight1: ModelRenderer

    @JvmField
    var tubeLeft2: ModelRenderer

    @JvmField
    var tubeLeft3: ModelRenderer

    @JvmField
    var tubeLeft4: ModelRenderer

    @JvmField
    var tubeLeft5: ModelRenderer

    @JvmField
    var tubeLeft7: ModelRenderer

    @JvmField
    var tubeRight6: ModelRenderer

    @JvmField
    var tubeLeft8: ModelRenderer

    @JvmField
    var oxygenMask: ModelRenderer
    private var saveGravity = 0f
    private val renderGear: Boolean

    constructor(renderGear: Boolean) : this(0.0f, false, renderGear)

    init {
        this.textureWidth = if (halfSizeTexture) 64 else 128
        this.textureHeight = if (halfSizeTexture) 32 else 64
        this.renderGear = renderGear
        val par2: Int = 0
        this.leftOxygenTank = ModelRenderer(this, 56, 20)
        this.leftOxygenTank.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, par1)
        this.leftOxygenTank.setRotationPoint(2f, 2f, 3.8f)
        this.leftOxygenTank.mirror = true
        this.setRotation(this.leftOxygenTank, 0f, 0f, 0f)
        this.rightOxygenTank = ModelRenderer(this, 56, 20)
        this.rightOxygenTank.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, par1)
        this.rightOxygenTank.setRotationPoint(-2f, 2f, 3.8f)
        this.rightOxygenTank.mirror = true
        this.setRotation(this.rightOxygenTank, 0f, 0f, 0f)
        this.tubeRight2 = ModelRenderer(this, 56, 30)
        this.tubeRight2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight2.setRotationPoint(-2f, 2f, 6.8f)
        this.tubeRight2.mirror = true
        this.setRotation(this.tubeRight2, 0f, 0f, 0f)
        this.tubeLeft1 = ModelRenderer(this, 56, 30)
        this.tubeLeft1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft1.setRotationPoint(2f, 3f, 5.8f)
        this.tubeLeft1.mirror = true
        this.setRotation(this.tubeLeft1, 0f, 0f, 0f)
        this.tubeRight3 = ModelRenderer(this, 56, 30)
        this.tubeRight3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight3.setRotationPoint(-2f, 1f, 6.8f)
        this.tubeRight3.mirror = true
        this.setRotation(this.tubeRight3, 0f, 0f, 0f)
        this.tubeRight4 = ModelRenderer(this, 56, 30)
        this.tubeRight4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight4.setRotationPoint(-2f, 0f, 6.8f)
        this.tubeRight4.mirror = true
        this.setRotation(this.tubeRight4, 0f, 0f, 0f)
        this.tubeRight5 = ModelRenderer(this, 56, 30)
        this.tubeRight5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight5.setRotationPoint(-2f, -1f, 6.8f)
        this.tubeRight5.mirror = true
        this.setRotation(this.tubeRight5, 0f, 0f, 0f)
        this.tubeLeft6 = ModelRenderer(this, 56, 30)
        this.tubeLeft6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft6.setRotationPoint(2f, -2f, 5.8f)
        this.tubeLeft6.mirror = true
        this.setRotation(this.tubeLeft6, 0f, 0f, 0f)
        this.tubeRight7 = ModelRenderer(this, 56, 30)
        this.tubeRight7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight7.setRotationPoint(-2f, -3f, 4.8f)
        this.tubeRight7.mirror = true
        this.setRotation(this.tubeRight7, 0f, 0f, 0f)
        this.tubeRight1 = ModelRenderer(this, 56, 30)
        this.tubeRight1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight1.setRotationPoint(-2f, 3f, 5.8f)
        this.tubeRight1.mirror = true
        this.setRotation(this.tubeRight1, 0f, 0f, 0f)
        this.tubeLeft2 = ModelRenderer(this, 56, 30)
        this.tubeLeft2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft2.setRotationPoint(2f, 2f, 6.8f)
        this.tubeLeft2.mirror = true
        this.setRotation(this.tubeLeft2, 0f, 0f, 0f)
        this.tubeLeft3 = ModelRenderer(this, 56, 30)
        this.tubeLeft3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft3.setRotationPoint(2f, 1f, 6.8f)
        this.tubeLeft3.mirror = true
        this.setRotation(this.tubeLeft3, 0f, 0f, 0f)
        this.tubeLeft4 = ModelRenderer(this, 56, 30)
        this.tubeLeft4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft4.setRotationPoint(2f, 0f, 6.8f)
        this.tubeLeft4.mirror = true
        this.setRotation(this.tubeLeft4, 0f, 0f, 0f)
        this.tubeLeft5 = ModelRenderer(this, 56, 30)
        this.tubeLeft5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft5.setRotationPoint(2f, -1f, 6.8f)
        this.tubeLeft5.mirror = true
        this.setRotation(this.tubeLeft5, 0f, 0f, 0f)
        this.tubeLeft7 = ModelRenderer(this, 56, 30)
        this.tubeLeft7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft7.setRotationPoint(2f, -3f, 4.8f)
        this.tubeLeft7.mirror = true
        this.setRotation(this.tubeLeft7, 0f, 0f, 0f)
        this.tubeRight6 = ModelRenderer(this, 56, 30)
        this.tubeRight6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight6.setRotationPoint(-2f, -2f, 5.8f)
        this.tubeRight6.mirror = true
        this.setRotation(this.tubeRight6, 0f, 0f, 0f)
        this.tubeLeft8 = ModelRenderer(this, 56, 30)
        this.tubeLeft8.addBox(0f, 0f, 0f, 1, 1, 1, par1)
        this.tubeLeft8.setRotationPoint(0f, -5f, 0f)
        this.tubeLeft8.mirror = true
        this.setRotation(this.tubeLeft8, 0f, 0f, 0f)
        this.oxygenMask = ModelRenderer(this, 56, 0)
        this.oxygenMask.addBox(-5f, -9f, -5f, 10, 10, 10, par1)
        this.oxygenMask.setRotationPoint(0f, 0f, 0f)
        this.oxygenMask.mirror = true
        this.setRotation(this.oxygenMask, 0f, 0f, 0f)
        //        this.bipedCloak = new ModelRenderer(this, 0, 0);
//        this.bipedCloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, par1);
//        this.bipedEars = new ModelRenderer(this, 24, 0);
//        this.bipedEars.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, par1);
        this.bipedHead = ModelRenderer(this, 0, 0)
        this.bipedHead.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, par1)
        this.bipedHead.setRotationPoint(0.0f, 0.0f + par2, 0.0f)
        this.bipedHeadwear = ModelRenderer(this, 32, 0)
        this.bipedHeadwear.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, par1 + 0.5f)
        this.bipedHeadwear.setRotationPoint(0.0f, 0.0f + par2, 0.0f)
        this.bipedBody = ModelRenderer(this, 16, 16)
        this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, par1)
        this.bipedBody.setRotationPoint(0.0f, 0.0f + par2, 0.0f)
        this.bipedRightArm = ModelRenderer(this, 40, 16)
        this.bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, par1)
        this.bipedRightArm.setRotationPoint(-5.0f, 2.0f + par2, 0.0f)
        this.bipedLeftArm = ModelRenderer(this, 40, 16)
        this.bipedLeftArm.mirror = true
        this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, par1)
        this.bipedLeftArm.setRotationPoint(5.0f, 2.0f + par2, 0.0f)
        this.bipedRightLeg = ModelRenderer(this, 0, 16)
        this.bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, par1)
        this.bipedRightLeg.setRotationPoint(-2.0f, 12.0f + par2, 0.0f)
        this.bipedLeftLeg = ModelRenderer(this, 0, 16)
        this.bipedLeftLeg.mirror = true
        this.bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, par1)
        this.bipedLeftLeg.setRotationPoint(2.0f, 12.0f + par2, 0.0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        //		super.render(entity, f, f1, f2, f3, f4, f5);
        this.saveGravity = WorldUtil.getGravityFactor(entity)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)

        if (this.isChild) {
            val f6: Float = 2.0f
            GL11.glPushMatrix()
            GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6)
            GL11.glTranslatef(0.0f, 16.0f * f5, 0.0f)
            this.bipedHead.render(f5)
            if (this.renderGear) {
                this.oxygenMask.render(f5)
            }
            GL11.glPopMatrix()
            GL11.glPushMatrix()
            GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6)
            GL11.glTranslatef(0.0f, 24.0f * f5, 0.0f)
            if (this.renderGear) {
                this.leftOxygenTank.render(f5)
                this.rightOxygenTank.render(f5)
                this.tubeRight2.render(f5)
                this.tubeLeft1.render(f5)
                this.tubeRight3.render(f5)
                this.tubeRight4.render(f5)
                this.tubeRight5.render(f5)
                this.tubeLeft6.render(f5)
                this.tubeRight7.render(f5)
                this.tubeRight1.render(f5)
                this.tubeLeft2.render(f5)
                this.tubeLeft3.render(f5)
                this.tubeLeft4.render(f5)
                this.tubeLeft5.render(f5)
                this.tubeLeft7.render(f5)
                this.tubeRight6.render(f5)
                this.tubeLeft8.render(f5)
            }
            this.bipedBody.render(f5)
            this.bipedRightArm.render(f5)
            this.bipedLeftArm.render(f5)
            this.bipedRightLeg.render(f5)
            this.bipedLeftLeg.render(f5)
            this.bipedHeadwear.render(f5)
            GL11.glPopMatrix()
        } else {
            if (this.renderGear) {
                this.leftOxygenTank.render(f5)
                this.rightOxygenTank.render(f5)
                this.tubeRight2.render(f5)
                this.tubeLeft1.render(f5)
                this.tubeRight3.render(f5)
                this.tubeRight4.render(f5)
                this.tubeRight5.render(f5)
                this.tubeLeft6.render(f5)
                this.tubeRight7.render(f5)
                this.tubeRight1.render(f5)
                this.tubeLeft2.render(f5)
                this.tubeLeft3.render(f5)
                this.tubeLeft4.render(f5)
                this.tubeLeft5.render(f5)
                this.tubeLeft7.render(f5)
                this.tubeRight6.render(f5)
                this.tubeLeft8.render(f5)
                this.oxygenMask.render(f5)
            }
            this.bipedHead.render(f5)
            this.bipedBody.render(f5)
            this.bipedRightArm.render(f5)
            this.bipedLeftArm.render(f5)
            this.bipedRightLeg.render(f5)
            this.bipedLeftLeg.render(f5)
            this.bipedHeadwear.render(f5)
        }
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(
        p_78087_1_: Float,
        p_78087_2_: Float,
        p_78087_3_: Float,
        p_78087_4_: Float,
        p_78087_5_: Float,
        p_78087_6_: Float,
        entityIn: Entity
    ) {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn)
        val f: Float = MathHelper.sin(this.swingProgress * Math.PI.toFloat())
        val f1: Float =
            MathHelper.sin((1.0f - (1.0f - this.swingProgress) * (1.0f - this.swingProgress)) * Math.PI.toFloat())
        this.bipedRightArm.rotateAngleZ = 0.0f
        this.bipedLeftArm.rotateAngleZ = 0.0f
        this.bipedRightArm.rotateAngleY = -(0.1f - f * 0.6f)
        this.bipedLeftArm.rotateAngleY = 0.1f - f * 0.6f
        this.bipedRightArm.rotateAngleX = -(Math.PI.toFloat() / 2f)
        this.bipedLeftArm.rotateAngleX = -(Math.PI.toFloat() / 2f)
        this.bipedRightArm.rotateAngleX -= f * 1.2f - f1 * 0.4f
        this.bipedLeftArm.rotateAngleX -= f * 1.2f - f1 * 0.4f
        this.bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09f) * 0.05f + 0.05f
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09f) * 0.05f + 0.05f
        this.bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067f) * 0.05f
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067f) * 0.05f

        copyModelAngles(this.bipedHead, this.oxygenMask)
    } //    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    //    {
    //        this.bipedHead.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES;
    //        this.bipedHead.rotateAngleX = par5 / Constants.RADIANS_TO_DEGREES;
    //        this.oxygenMask.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES;
    //        this.oxygenMask.rotateAngleX = par5 / Constants.RADIANS_TO_DEGREES;
    //        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
    //        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
    //        this.bipedRightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 2.0F * par2 * 0.5F;
    //        this.bipedLeftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
    //        this.bipedRightArm.rotateAngleZ = 0.0F;
    //        this.bipedLeftArm.rotateAngleZ = 0.0F;
    //        this.bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F / this.saveGravity) * (1.5F - this.saveGravity) * par2;
    //        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F / this.saveGravity + (float) Math.PI) * (1.5F - this.saveGravity) * par2;
    //        this.bipedRightLeg.rotateAngleY = 0.0F;
    //        this.bipedLeftLeg.rotateAngleY = 0.0F;
    //
    //        if (this.isRiding)
    //        {
    //            this.bipedRightArm.rotateAngleX += -((float) Math.PI / 5F);
    //            this.bipedLeftArm.rotateAngleX += -((float) Math.PI / 5F);
    //            this.bipedRightLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
    //            this.bipedLeftLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
    //            this.bipedRightLeg.rotateAngleY = (float) Math.PI / 10F;
    //            this.bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);
    //        }
    //
    //        this.bipedRightArm.rotateAngleY = 0.0F;
    //        this.bipedLeftArm.rotateAngleY = 0.0F;
    //        float var7;
    //        float var8;
    //
    //        if (this.onGround > -9990.0F)
    //        {
    //            var7 = this.onGround;
    //            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(var7) * (float) Math.PI * 2.0F) * 0.2F;
    //            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
    //            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
    //            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
    //            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
    //            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
    //            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
    //            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
    //            var7 = 1.0F - this.onGround;
    //            var7 *= var7;
    //            var7 *= var7;
    //            var7 = 1.0F - var7;
    //            var8 = MathHelper.sin(var7 * (float) Math.PI);
    //            final float var9 = MathHelper.sin(this.onGround * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
    //            this.bipedRightArm.rotateAngleX = (float) (this.bipedRightArm.rotateAngleX - (var8 * 1.2D + var9));
    //            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
    //            this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.onGround * (float) Math.PI) * -0.4F;
    //        }
    //
    //        this.bipedBody.rotateAngleX = 0.0F;
    //        this.bipedRightLeg.rotationPointZ = 0.0F;
    //        this.bipedLeftLeg.rotationPointZ = 0.0F;
    //        this.bipedRightLeg.rotationPointY = 12.0F;
    //        this.bipedLeftLeg.rotationPointY = 12.0F;
    //        this.bipedHead.rotationPointY = 0.0F;
    //
    //        this.bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
    //        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
    //        this.bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
    //        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
    //
    //        final float var7a = MathHelper.sin(this.onGround * (float) Math.PI);
    //        final float var8a = MathHelper.sin((1.0F - (1.0F - this.onGround) * (1.0F - this.onGround)) * (float) Math.PI);
    //
    //        this.bipedRightArm.rotateAngleZ = 0.0F;
    //        this.bipedLeftArm.rotateAngleZ = 0.0F;
    //        this.bipedRightArm.rotateAngleY = -(0.1F - var7a * 0.6F);
    //        this.bipedLeftArm.rotateAngleY = 0.1F - var7a * 0.6F;
    //        this.bipedRightArm.rotateAngleX = -(Constants.halfPI);
    //        this.bipedLeftArm.rotateAngleX = -(Constants.halfPI);
    //        this.bipedRightArm.rotateAngleX -= var7a * 1.2F - var8a * 0.4F;
    //        this.bipedLeftArm.rotateAngleX -= var7a * 1.2F - var8a * 0.4F;
    //        this.bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
    //        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
    //        this.bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
    //        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
    //    }
}
