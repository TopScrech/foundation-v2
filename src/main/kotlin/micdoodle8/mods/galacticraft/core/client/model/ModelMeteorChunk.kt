package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity

open class ModelMeteorChunk constructor() : ModelBase() {
    private val boxes: Array<ModelRenderer> = (arrayOfNulls<ModelRenderer>(6) as Array<ModelRenderer>)

    init {
        textureWidth = 16
        textureHeight = 16

        boxes[0] = ModelRenderer(this, 0, 0)
        boxes[0].addBox(0f, 0f, 0f, 1, 8, 6)
        boxes[0].setRotationPoint(4f, 0.0f, -5f)
        boxes[0].setTextureSize(16, 16)
        setRotation(boxes[0], -0f, -0f, -0f)
        boxes[0].mirror = false
        boxes[1] = ModelRenderer(this, 0, 0)
        boxes[1].addBox(0f, 0f, 0f, 5, 8, 1)
        boxes[1].setRotationPoint(-3f, 1.0f, -7f)
        boxes[1].setTextureSize(16, 16)
        setRotation(boxes[1], -0f, -0f, -0f)
        boxes[1].mirror = false
        boxes[2] = ModelRenderer(this, 0, 0)
        boxes[2].addBox(0f, 0f, 0f, 1, 8, 6)
        boxes[2].setRotationPoint(-6f, -1.0f, -5f)
        boxes[2].setTextureSize(16, 16)
        setRotation(boxes[2], -0f, -0f, -0f)
        boxes[2].mirror = false
        boxes[3] = ModelRenderer(this, 0, 0)
        boxes[3].addBox(0f, 0f, 0f, 6, 1, 5)
        boxes[3].setRotationPoint(-3f, -3.0f, -4f)
        boxes[3].setTextureSize(16, 16)
        setRotation(boxes[3], -0f, -0f, -0f)
        boxes[3].mirror = false
        boxes[4] = ModelRenderer(this, 0, 0)
        boxes[4].addBox(0f, 0f, 0f, 5, 8, 1)
        boxes[4].setRotationPoint(-3f, 0.0f, 3f)
        boxes[4].setTextureSize(16, 16)
        setRotation(boxes[4], -0f, -0f, -0f)
        boxes[4].mirror = false
        boxes[5] = ModelRenderer(this, 0, 0)
        boxes[5].addBox(0f, 0f, 0f, 9, 12, 9)
        boxes[5].setRotationPoint(-5f, -2.0f, -6f)
        boxes[5].setTextureSize(16, 16)
        setRotation(boxes[5], -0f, -0f, -0f)
        boxes[5].mirror = false
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        setRotationAngles(f, f1, f2, f3, f4, f5, entity)

        for (mr: ModelRenderer in boxes) {
            mr.render(f5)
        }
    }

    private fun setRotation(model: ModelRenderer?, x: Float, y: Float, z: Float) {
        model!!.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}
