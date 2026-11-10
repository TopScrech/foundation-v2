package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class ModelAluminumWire constructor() : ModelBase() {
    // fields
    @JvmField
    var middle: ModelRenderer

    @JvmField
    var right: ModelRenderer

    @JvmField
    var left: ModelRenderer

    @JvmField
    var back: ModelRenderer

    @JvmField
    var front: ModelRenderer

    @JvmField
    var top: ModelRenderer

    @JvmField
    var bottom: ModelRenderer

    init {
        this.textureWidth = 64
        this.textureHeight = 32
        this.middle = ModelRenderer(this, 0, 0)
        this.middle.addBox(-1f, -1f, -1f, 4, 4, 4)
        this.middle.setRotationPoint(-1f, 15f, -1f)
        this.middle.setTextureSize(64, 32)
        this.middle.mirror = true
        this.setRotation(this.middle, 0f, 0f, 0f)
        this.right = ModelRenderer(this, 21, 0)
        this.right.addBox(0f, 0f, 0f, 6, 4, 4)
        this.right.setRotationPoint(2f, 14f, -2f)
        this.right.setTextureSize(64, 32)
        this.right.mirror = true
        this.setRotation(this.right, 0f, 0f, 0f)
        this.left = ModelRenderer(this, 21, 0)
        this.left.addBox(0f, 0f, 0f, 6, 4, 4)
        this.left.setRotationPoint(-8f, 14f, -2f)
        this.left.setTextureSize(64, 32)
        this.left.mirror = true
        this.setRotation(this.left, 0f, 0f, 0f)
        this.back = ModelRenderer(this, 0, 11)
        this.back.addBox(0f, 0f, 0f, 4, 4, 6)
        this.back.setRotationPoint(-2f, 14f, 2f)
        this.back.setTextureSize(64, 32)
        this.back.mirror = true
        this.setRotation(this.back, 0f, 0f, 0f)
        this.front = ModelRenderer(this, 0, 11)
        this.front.addBox(0f, 0f, 0f, 4, 4, 6)
        this.front.setRotationPoint(-2f, 14f, -8f)
        this.front.setTextureSize(64, 32)
        this.front.mirror = true
        this.setRotation(this.front, 0f, 0f, 0f)
        this.top = ModelRenderer(this, 21, 11)
        this.top.addBox(0f, 0f, 0f, 4, 6, 4)
        this.top.setRotationPoint(-2f, 8f, -2f)
        this.top.setTextureSize(64, 32)
        this.top.mirror = true
        this.setRotation(this.top, 0f, 0f, 0f)
        this.bottom = ModelRenderer(this, 21, 11)
        this.bottom.addBox(0f, 0f, 0f, 4, 6, 4)
        this.bottom.setRotationPoint(-2f, 18f, -2f)
        this.bottom.setTextureSize(64, 32)
        this.bottom.mirror = true
        this.setRotation(this.bottom, 0f, 0f, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        this.renderMiddle()
        this.renderBottom()
        this.renderTop()
        this.renderLeft()
        this.renderRight()
        this.renderBack()
        this.renderFront()
    }

    open fun renderMiddle() {
        this.middle.render(0.0625f)
    }

    open fun renderBottom() {
        this.bottom.render(0.0625f)
    }

    open fun renderTop() {
        this.top.render(0.0625f)
    }

    open fun renderLeft() {
        this.left.render(0.0625f)
    }

    open fun renderRight() {
        this.right.render(0.0625f)
    }

    open fun renderBack() {
        this.back.render(0.0625f)
    }

    open fun renderFront() {
        // this.Front.render(0.0625F);
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(x: Float, y: Float, z: Float, f3: Float, f4: Float, f5: Float, entity: Entity) {
        super.setRotationAngles(x, y, z, f3, f4, f5, entity)
    }
}
