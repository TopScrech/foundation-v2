package micdoodle8.mods.galacticraft.planets.mars.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper

open class ModelSlimeling constructor(scale: Float) : ModelBase() {
    @JvmField
    var tail3: ModelRenderer

    @JvmField
    var tail2: ModelRenderer

    @JvmField
    var tail1: ModelRenderer

    @JvmField
    var bodyMain: ModelRenderer

    @JvmField
    var neck: ModelRenderer

    @JvmField
    var head: ModelRenderer

    init {
        this.textureWidth = 256
        this.textureHeight = 128

        this.head = ModelRenderer(this, 196, 25)
        this.head.addBox(-4.5f, -15.7f, 0.5f, 9, 9, 9)
        this.head.setRotationPoint(0f, 0f, 0f)
        this.head.setTextureSize(256, 128)
        this.head.mirror = true
        this.setRotation(this.head, 0f, 0f, 0f)
        this.tail3 = ModelRenderer(this, 0, 25)
        this.tail3.addBox(-3.5f, 1f, -17f, 7, 5, 7)
        this.tail3.setRotationPoint(0f, 0f, 0f)
        this.tail3.setTextureSize(256, 128)
        this.tail3.mirror = true
        this.setRotation(this.tail3, 0f, 0f, 0f)
        this.tail2 = ModelRenderer(this, 28, 25)
        this.tail2.addBox(-4.5f, -1f, -15f, 9, 7, 9)
        this.tail2.setRotationPoint(0f, 0f, 0f)
        this.tail2.setTextureSize(256, 128)
        this.tail2.mirror = true
        this.setRotation(this.tail2, 0f, 0f, 0f)
        this.tail1 = ModelRenderer(this, 64, 25)
        this.tail1.addBox(-5.5f, -3f, -11f, 11, 9, 10)
        this.tail1.setRotationPoint(0f, 0f, 0f)
        this.tail1.setTextureSize(256, 128)
        this.tail1.mirror = true
        this.setRotation(this.tail1, 0f, 0f, 0f)
        this.bodyMain = ModelRenderer(this, 106, 25)
        this.bodyMain.addBox(-6f, -6f, -6f, 12, 12, 12)
        this.bodyMain.setRotationPoint(0f, 0f, 0f)
        this.bodyMain.setTextureSize(256, 128)
        this.bodyMain.mirror = true
        this.setRotation(this.bodyMain, 0f, 0f, 0f)
        this.neck = ModelRenderer(this, 154, 25)
        this.neck.addBox(-5.5f, -10.5f, -3f, 11, 11, 10)
        this.neck.setRotationPoint(0f, 0f, 0f)
        this.neck.setTextureSize(256, 128)
        this.neck.mirror = true
        this.setRotation(this.neck, 0f, 0f, 0f)

        if (scale > 0) {
            this.head = ModelRenderer(this, 156, 0)
            this.head.addBox(-3.5f, -14.7f, 1.5f, 7, 7, 7)
            this.head.setRotationPoint(0f, 0f, 0f)
            this.head.setTextureSize(256, 128)
            this.head.mirror = true
            this.setRotation(this.head, 0f, 0f, 0f)
            this.neck = ModelRenderer(this, 122, 0)
            this.neck.addBox(-4.5f, -9.5f, -2f, 9, 9, 8)
            this.neck.setRotationPoint(0f, 0f, 0f)
            this.neck.setTextureSize(256, 128)
            this.neck.mirror = true
            this.setRotation(this.neck, 0f, 0f, 0f)
            this.bodyMain = ModelRenderer(this, 82, 0)
            this.bodyMain.addBox(-5f, -5f, -5f, 10, 10, 10)
            this.bodyMain.setRotationPoint(0f, 0f, 0f)
            this.bodyMain.setTextureSize(256, 128)
            this.bodyMain.mirror = true
            this.setRotation(this.bodyMain, 0f, 0f, 0f)
            this.tail1 = ModelRenderer(this, 48, 0)
            this.tail1.addBox(-4.5f, -2f, -10f, 9, 7, 8)
            this.tail1.setRotationPoint(0f, 0f, 0f)
            this.tail1.setTextureSize(256, 128)
            this.tail1.mirror = true
            this.setRotation(this.tail1, 0f, 0f, 0f)
            this.tail2 = ModelRenderer(this, 20, 0)
            this.tail2.addBox(-3.5f, 0f, -14f, 7, 5, 7)
            this.tail2.setRotationPoint(0f, 0f, 0f)
            this.tail2.setTextureSize(256, 128)
            this.tail2.mirror = true
            this.setRotation(this.tail2, 0f, 0f, 0f)
            this.tail3 = ModelRenderer(this, 0, 0)
            this.tail3.addBox(-2.5f, 2f, -16f, 5, 3, 5)
            this.tail3.setRotationPoint(0f, 0f, 0f)
            this.tail3.setTextureSize(256, 128)
            this.tail3.mirror = true
            this.setRotation(this.tail3, 0f, 0f, 0f)
        }

        this.bodyMain.addChild(this.tail1)
        this.neck.addChild(this.head)
        this.tail1.addChild(this.tail2)
        this.tail2.addChild(this.tail3)
        this.bodyMain.addChild(this.neck)
    }

    override fun render(entity: Entity, par2: Float, par3: Float, par4: Float, par5: Float, par6: Float, par7: Float) {
        super.render(entity, par2, par3, par4, par5, par6, par7)
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity)
        this.bodyMain.render(par7)
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
        par7Entity: Entity
    ) {
        this.tail1.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2
        this.tail2.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2
        this.tail3.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2
        this.tail1.offsetZ = MathHelper.cos(0.5f * par1 * 0.6662f) * 0.2f * par2
        this.tail2.offsetZ = MathHelper.cos(0.5f * par1 * 0.6662f) * 0.2f * par2
        this.tail3.offsetZ = MathHelper.cos(0.5f * par1 * 0.6662f) * 0.2f * par2
        this.neck.offsetZ = -MathHelper.cos(0.5f * par1 * 0.6662f) * 0.1f * par2
        this.head.offsetZ = -MathHelper.cos(0.5f * par1 * 0.6662f) * 0.1f * par2
    }
}
