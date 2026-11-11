package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class ModelEvolvedCreeper @JvmOverloads constructor(par1: Float = 0.0f) : ModelBase() {
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

    @JvmField
    var head: ModelRenderer

    @JvmField
    var field_78133_b: ModelRenderer

    @JvmField
    var body: ModelRenderer

    @JvmField
    var leg1: ModelRenderer

    @JvmField
    var leg2: ModelRenderer

    @JvmField
    var leg3: ModelRenderer

    @JvmField
    var leg4: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 64
        this.leftOxygenTank = ModelRenderer(this, 40, 20)
        this.leftOxygenTank.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, par1)
        this.leftOxygenTank.setRotationPoint(2f, 5f, 3.8f)
        this.leftOxygenTank.setTextureSize(128, 64)
        this.leftOxygenTank.mirror = true
        this.setRotation(this.leftOxygenTank, 0f, 0f, 0f)
        this.rightOxygenTank = ModelRenderer(this, 40, 20)
        this.rightOxygenTank.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, par1)
        this.rightOxygenTank.setRotationPoint(-2f, 5f, 3.8f)
        this.rightOxygenTank.setTextureSize(128, 64)
        this.rightOxygenTank.mirror = true
        this.setRotation(this.rightOxygenTank, 0f, 0f, 0f)
        this.tubeRight2 = ModelRenderer(this, 40, 30)
        this.tubeRight2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight2.setRotationPoint(-2f, 5f, 6.8f)
        this.tubeRight2.setTextureSize(128, 64)
        this.tubeRight2.mirror = true
        this.setRotation(this.tubeRight2, 0f, 0f, 0f)
        this.tubeLeft1 = ModelRenderer(this, 40, 30)
        this.tubeLeft1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft1.setRotationPoint(2f, 6f, 5.8f)
        this.tubeLeft1.setTextureSize(128, 64)
        this.tubeLeft1.mirror = true
        this.setRotation(this.tubeLeft1, 0f, 0f, 0f)
        this.tubeRight3 = ModelRenderer(this, 40, 30)
        this.tubeRight3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight3.setRotationPoint(-2f, 4f, 6.8f)
        this.tubeRight3.setTextureSize(128, 64)
        this.tubeRight3.mirror = true
        this.setRotation(this.tubeRight3, 0f, 0f, 0f)
        this.tubeRight4 = ModelRenderer(this, 40, 30)
        this.tubeRight4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight4.setRotationPoint(-2f, 3f, 6.8f)
        this.tubeRight4.setTextureSize(128, 64)
        this.tubeRight4.mirror = true
        this.setRotation(this.tubeRight4, 0f, 0f, 0f)
        this.tubeRight5 = ModelRenderer(this, 40, 30)
        this.tubeRight5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight5.setRotationPoint(-2f, 2f, 6.8f)
        this.tubeRight5.setTextureSize(128, 64)
        this.tubeRight5.mirror = true
        this.setRotation(this.tubeRight5, 0f, 0f, 0f)
        this.tubeLeft6 = ModelRenderer(this, 40, 30)
        this.tubeLeft6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft6.setRotationPoint(2f, 1f, 5.8f)
        this.tubeLeft6.setTextureSize(128, 64)
        this.tubeLeft6.mirror = true
        this.setRotation(this.tubeLeft6, 0f, 0f, 0f)
        this.tubeRight7 = ModelRenderer(this, 40, 30)
        this.tubeRight7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight7.setRotationPoint(-2f, 0f, 4.8f)
        this.tubeRight7.setTextureSize(128, 64)
        this.tubeRight7.mirror = true
        this.setRotation(this.tubeRight7, 0f, 0f, 0f)
        this.tubeRight1 = ModelRenderer(this, 40, 30)
        this.tubeRight1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight1.setRotationPoint(-2f, 6f, 5.8f)
        this.tubeRight1.setTextureSize(128, 64)
        this.tubeRight1.mirror = true
        this.setRotation(this.tubeRight1, 0f, 0f, 0f)
        this.tubeLeft2 = ModelRenderer(this, 40, 30)
        this.tubeLeft2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft2.setRotationPoint(2f, 5f, 6.8f)
        this.tubeLeft2.setTextureSize(128, 64)
        this.tubeLeft2.mirror = true
        this.setRotation(this.tubeLeft2, 0f, 0f, 0f)
        this.tubeLeft3 = ModelRenderer(this, 40, 30)
        this.tubeLeft3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft3.setRotationPoint(2f, 4f, 6.8f)
        this.tubeLeft3.setTextureSize(128, 64)
        this.tubeLeft3.mirror = true
        this.setRotation(this.tubeLeft3, 0f, 0f, 0f)
        this.tubeLeft4 = ModelRenderer(this, 40, 30)
        this.tubeLeft4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft4.setRotationPoint(2f, 3f, 6.8f)
        this.tubeLeft4.setTextureSize(128, 64)
        this.tubeLeft4.mirror = true
        this.setRotation(this.tubeLeft4, 0f, 0f, 0f)
        this.tubeLeft5 = ModelRenderer(this, 40, 30)
        this.tubeLeft5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft5.setRotationPoint(2f, 2f, 6.8f)
        this.tubeLeft5.setTextureSize(128, 64)
        this.tubeLeft5.mirror = true
        this.setRotation(this.tubeLeft5, 0f, 0f, 0f)
        this.tubeLeft7 = ModelRenderer(this, 40, 30)
        this.tubeLeft7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeLeft7.setRotationPoint(2f, 0f, 4.8f)
        this.tubeLeft7.setTextureSize(128, 64)
        this.tubeLeft7.mirror = true
        this.setRotation(this.tubeLeft7, 0f, 0f, 0f)
        this.tubeRight6 = ModelRenderer(this, 40, 30)
        this.tubeRight6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, par1)
        this.tubeRight6.setRotationPoint(-2f, 1f, 5.8f)
        this.tubeRight6.setTextureSize(128, 64)
        this.tubeRight6.mirror = true
        this.setRotation(this.tubeRight6, 0f, 0f, 0f)
        this.tubeLeft8 = ModelRenderer(this, 40, 30)
        this.tubeLeft8.addBox(0f, 0f, 0f, 1, 1, 1, par1)
        this.tubeLeft8.setRotationPoint(0f, -2f, 0f)
        this.tubeLeft8.setTextureSize(128, 64)
        this.tubeLeft8.mirror = true
        this.setRotation(this.tubeLeft8, 0f, 0f, 0f)
        this.oxygenMask = ModelRenderer(this, 40, 0)
        this.oxygenMask.addBox(-5f, -9f, -5f, 10, 10, 10, par1)
        this.oxygenMask.setRotationPoint(0f, 4f, 0f)
        this.oxygenMask.setTextureSize(128, 64)
        this.oxygenMask.mirror = true
        this.setRotation(this.oxygenMask, 0f, 0f, 0f)

        val var2: Byte = 4
        this.head = ModelRenderer(this, 0, 0)
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, par1)
        this.head.setRotationPoint(0.0f, var2.toFloat(), 0.0f)
        this.head.setTextureSize(128, 64)
        this.field_78133_b = ModelRenderer(this, 32, 0)
        this.field_78133_b.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, par1 + 0.5f)
        this.field_78133_b.setRotationPoint(0.0f, var2.toFloat(), 0.0f)
        this.field_78133_b.setTextureSize(128, 64)
        this.body = ModelRenderer(this, 16, 16)
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, par1)
        this.body.setRotationPoint(0.0f, var2.toFloat(), 0.0f)
        this.body.setTextureSize(128, 64)
        this.leg1 = ModelRenderer(this, 0, 16)
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1)
        this.leg1.setRotationPoint(-2.0f, (12 + var2).toFloat(), 4.0f)
        this.leg1.setTextureSize(128, 64)
        this.leg2 = ModelRenderer(this, 0, 16)
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1)
        this.leg2.setRotationPoint(2.0f, (12 + var2).toFloat(), 4.0f)
        this.leg2.setTextureSize(128, 64)
        this.leg3 = ModelRenderer(this, 0, 16)
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1)
        this.leg3.setRotationPoint(-2.0f, (12 + var2).toFloat(), -4.0f)
        this.leg3.setTextureSize(128, 64)
        this.leg4 = ModelRenderer(this, 0, 16)
        this.leg4.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, par1)
        this.leg4.setRotationPoint(2.0f, (12 + var2).toFloat(), -4.0f)
        this.leg4.setTextureSize(128, 64)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun render(
        par1Entity: Entity,
        par2: Float,
        par3: Float,
        par4: Float,
        par5: Float,
        par6: Float,
        par7: Float
    ) {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7)

        if (this.isChild) {
            val f6: Float = 2.0f
            GL11.glPushMatrix()
            GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6)
            GL11.glTranslatef(0.0f, 16.0f * par7, 0.0f)
            this.head.render(par7)
            this.oxygenMask.render(par7)
            GL11.glPopMatrix()
            GL11.glPushMatrix()
            GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6)
            GL11.glTranslatef(0.0f, 24.0f * par7, 0.0f)
            this.leftOxygenTank.render(par7)
            this.rightOxygenTank.render(par7)
            this.tubeRight2.render(par7)
            this.tubeLeft1.render(par7)
            this.tubeRight3.render(par7)
            this.tubeRight4.render(par7)
            this.tubeRight5.render(par7)
            this.tubeLeft6.render(par7)
            this.tubeRight7.render(par7)
            this.tubeRight1.render(par7)
            this.tubeLeft2.render(par7)
            this.tubeLeft3.render(par7)
            this.tubeLeft4.render(par7)
            this.tubeLeft5.render(par7)
            this.tubeLeft7.render(par7)
            this.tubeRight6.render(par7)
            this.tubeLeft8.render(par7)
            this.body.render(par7)
            this.leg1.render(par7)
            this.leg2.render(par7)
            this.leg3.render(par7)
            this.leg4.render(par7)
            GL11.glPopMatrix()
        } else {
            this.leftOxygenTank.render(par7)
            this.rightOxygenTank.render(par7)
            this.tubeRight2.render(par7)
            this.tubeLeft1.render(par7)
            this.tubeRight3.render(par7)
            this.tubeRight4.render(par7)
            this.tubeRight5.render(par7)
            this.tubeLeft6.render(par7)
            this.tubeRight7.render(par7)
            this.tubeRight1.render(par7)
            this.tubeLeft2.render(par7)
            this.tubeLeft3.render(par7)
            this.tubeLeft4.render(par7)
            this.tubeLeft5.render(par7)
            this.tubeLeft7.render(par7)
            this.tubeRight6.render(par7)
            this.tubeLeft8.render(par7)
            this.oxygenMask.render(par7)
            this.head.render(par7)
            this.body.render(par7)
            this.leg1.render(par7)
            this.leg2.render(par7)
            this.leg3.render(par7)
            this.leg4.render(par7)
        }
    }

    open fun setRotationAngles(par1: Float, par2: Float, par3: Float, par4: Float, par5: Float, par6: Float) {
        this.oxygenMask.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES
        this.oxygenMask.rotateAngleX = par5 / Constants.RADIANS_TO_DEGREES
        this.head.rotateAngleY = par4 / Constants.RADIANS_TO_DEGREES
        this.head.rotateAngleX = par5 / Constants.RADIANS_TO_DEGREES
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2
    }
}
