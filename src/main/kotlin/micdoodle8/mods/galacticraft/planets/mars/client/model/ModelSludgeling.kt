package micdoodle8.mods.galacticraft.planets.mars.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import kotlin.math.abs

open class ModelSludgeling constructor() : ModelBase() {
    @JvmField
    var tail4: ModelRenderer

    @JvmField
    var body: ModelRenderer

    @JvmField
    var tail1: ModelRenderer

    @JvmField
    var tail2: ModelRenderer

    @JvmField
    var tail3: ModelRenderer

    init {
        this.textureWidth = 64
        this.textureHeight = 32

        this.tail4 = ModelRenderer(this, 0, 0)
        this.tail4.addBox(-0.5f, 0.3f, 4.5f, 1, 1, 1)
        this.tail4.setRotationPoint(0f, 23.5f, -2f)
        this.tail4.setTextureSize(64, 32)
        this.tail4.mirror = true
        this.setRotation(this.tail4, 0f, 0f, 0f)
        this.body = ModelRenderer(this, 4, 0)
        this.body.addBox(-1f, -0.5f, -1.5f, 2, 1, 3)
        this.body.setRotationPoint(0f, 23.5f, -2f)
        this.body.setTextureSize(64, 32)
        this.body.mirror = true
        this.setRotation(this.body, 0f, 0f, 0f)
        this.tail1 = ModelRenderer(this, 0, 0)
        this.tail1.addBox(-0.5f, -0.3f, 1.5f, 1, 1, 1)
        this.tail1.setRotationPoint(0f, 23.5f, -2f)
        this.tail1.setTextureSize(64, 32)
        this.tail1.mirror = true
        this.setRotation(this.tail1, 0f, 0f, 0f)
        this.tail2 = ModelRenderer(this, 0, 0)
        this.tail2.addBox(-0.5f, -0.1f, 2.5f, 1, 1, 1)
        this.tail2.setRotationPoint(0f, 23.5f, -2f)
        this.tail2.setTextureSize(64, 32)
        this.tail2.mirror = true
        this.setRotation(this.tail2, 0f, 0f, 0f)
        this.tail3 = ModelRenderer(this, 0, 0)
        this.tail3.addBox(-0.5f, 0.1f, 3.5f, 1, 1, 1)
        this.tail3.setRotationPoint(0f, 23.5f, -2f)
        this.tail3.setTextureSize(64, 32)
        this.tail3.mirror = true
        this.setRotation(this.tail3, 0f, 0f, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        this.body.render(f5)
        this.tail1.render(f5)
        this.tail2.render(f5)
        this.tail3.render(f5)
        this.tail4.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        this.tail1.rotateAngleY =
            MathHelper.cos(f2 * 0.3f + 0 * 0.15f * Math.PI.toFloat()) * Math.PI.toFloat() * 0.025f * (1 + abs(0 - 2))
        this.tail2.rotateAngleY =
            MathHelper.cos(f2 * 0.3f + 1 * 0.15f * Math.PI.toFloat()) * Math.PI.toFloat() * 0.025f * (1 + abs(1 - 2))
        this.tail3.rotateAngleY =
            MathHelper.cos(f2 * 0.3f + 2 * 0.15f * Math.PI.toFloat()) * Math.PI.toFloat() * 0.025f * (1 + abs(1 - 2))
        this.tail4.rotateAngleY =
            MathHelper.cos(f2 * 0.3f + 3 * 0.15f * Math.PI.toFloat()) * Math.PI.toFloat() * 0.025f * (1 + abs(1 - 2))
    }
}
