package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity

open class ModelMeteor constructor() : ModelBase() {
    @JvmField
    var shapes: Array<ModelRenderer> = (arrayOfNulls<ModelRenderer>(13) as Array<ModelRenderer>)

    init {
        this.textureWidth = 128
        this.textureHeight = 64

        this.shapes[0] = ModelRenderer(this, 0, 0)
        this.shapes[0].addBox(0f, -7f, -13f, 2, 4, 4)
        this.shapes[0].setRotationPoint(0f, 0f, 0f)
        this.shapes[0].setTextureSize(128, 64)
        this.shapes[0].mirror = true
        this.setRotation(this.shapes[0], 0f, 0f, 0f)
        this.shapes[1] = ModelRenderer(this, 0, 0)
        this.shapes[1].addBox(-10f, -10f, -10f, 20, 20, 20)
        this.shapes[1].setRotationPoint(0f, 0f, 0f)
        this.shapes[1].setTextureSize(128, 64)
        this.shapes[1].mirror = true
        this.setRotation(this.shapes[1], 0f, 0f, 0f)
        this.shapes[2] = ModelRenderer(this, 0, 0)
        this.shapes[2].addBox(-5f, -8f, -12f, 5, 9, 1)
        this.shapes[2].setRotationPoint(0f, 0f, 0f)
        this.shapes[2].setTextureSize(128, 64)
        this.shapes[2].mirror = true
        this.setRotation(this.shapes[2], 0f, 0f, 0f)
        this.shapes[3] = ModelRenderer(this, 0, 0)
        this.shapes[3].addBox(0f, -6f, 11f, 4, 13, 1)
        this.shapes[3].setRotationPoint(0f, 0f, 0f)
        this.shapes[3].setTextureSize(128, 64)
        this.shapes[3].mirror = true
        this.setRotation(this.shapes[3], 0f, 0f, 0f)
        this.shapes[4] = ModelRenderer(this, 0, 0)
        this.shapes[4].addBox(-9f, 10f, -9f, 18, 1, 18)
        this.shapes[4].setRotationPoint(0f, 0f, 0f)
        this.shapes[4].setTextureSize(128, 64)
        this.shapes[4].mirror = true
        this.setRotation(this.shapes[4], 0f, 0f, 0f)
        this.shapes[5] = ModelRenderer(this, 0, 0)
        this.shapes[5].addBox(11f, 3f, -8f, 1, 5, 5)
        this.shapes[5].setRotationPoint(0f, 0f, 0f)
        this.shapes[5].setTextureSize(128, 64)
        this.shapes[5].mirror = true
        this.setRotation(this.shapes[5], 0f, 0f, 0f)
        this.shapes[6] = ModelRenderer(this, 0, 0)
        this.shapes[6].addBox(-7f, -8f, 10f, 7, 12, 2)
        this.shapes[6].setRotationPoint(0f, 0f, 0f)
        this.shapes[6].setTextureSize(128, 64)
        this.shapes[6].mirror = true
        this.setRotation(this.shapes[6], 0f, 0f, 0f)
        this.shapes[7] = ModelRenderer(this, 0, 0)
        this.shapes[7].addBox(-9f, -9f, 10f, 18, 18, 1)
        this.shapes[7].setRotationPoint(0f, 0f, 0f)
        this.shapes[7].setTextureSize(128, 64)
        this.shapes[7].mirror = true
        this.setRotation(this.shapes[7], 0f, 0f, 0f)
        this.shapes[8] = ModelRenderer(this, 0, 0)
        this.shapes[8].addBox(-11f, -9f, -9f, 1, 18, 18)
        this.shapes[8].setRotationPoint(0f, 0f, 0f)
        this.shapes[8].setTextureSize(128, 64)
        this.shapes[8].mirror = true
        this.setRotation(this.shapes[8], 0f, 0f, 0f)
        this.shapes[9] = ModelRenderer(this, 0, 0)
        this.shapes[9].addBox(10f, -9f, -9f, 1, 18, 18)
        this.shapes[9].setRotationPoint(0f, 0f, 0f)
        this.shapes[9].setTextureSize(128, 64)
        this.shapes[9].mirror = true
        this.setRotation(this.shapes[9], 0f, 0f, 0f)
        this.shapes[10] = ModelRenderer(this, 0, 0)
        this.shapes[10].addBox(-9f, -9f, -11f, 18, 18, 1)
        this.shapes[10].setRotationPoint(0f, 0f, 0f)
        this.shapes[10].setTextureSize(128, 64)
        this.shapes[10].mirror = true
        this.setRotation(this.shapes[10], 0f, 0f, 0f)
        this.shapes[11] = ModelRenderer(this, 0, 0)
        this.shapes[11].addBox(-9f, -9f, -11f, 18, 18, 1)
        this.shapes[11].setRotationPoint(0f, 0f, 0f)
        this.shapes[11].setTextureSize(128, 64)
        this.shapes[11].mirror = true
        this.setRotation(this.shapes[11], 0f, 0f, 0f)
        this.shapes[12] = ModelRenderer(this, 0, 0)
        this.shapes[12].addBox(-9f, -11f, -9f, 18, 1, 18)
        this.shapes[12].setRotationPoint(0f, 0f, 0f)
        this.shapes[12].setTextureSize(128, 64)
        this.shapes[12].mirror = true
        this.setRotation(this.shapes[12], 0f, 0f, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)

        for (shape: ModelRenderer in this.shapes) {
            shape.render(f5)
        }
    }

    open fun renderBlock(f: Float) {
        for (shape: ModelRenderer in this.shapes) {
            shape.render(f)
        }
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}
