package micdoodle8.mods.galacticraft.core.client.model.block

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity

open class ModelSolarPanel @JvmOverloads constructor(var1: Float = 0.0f) : ModelBase() {
    @JvmField
    var panelMain: ModelRenderer

    @JvmField
    var sideHorizontal0: ModelRenderer

    @JvmField
    var sideVertical0: ModelRenderer

    @JvmField
    var sideVertical2: ModelRenderer

    @JvmField
    var sideVertical1: ModelRenderer

    @JvmField
    var sideHorizontal1: ModelRenderer

    @JvmField
    var sideHorizontal3: ModelRenderer

    @JvmField
    var sideHorizontal2: ModelRenderer

    @JvmField
    var pole: ModelRenderer

    init {
        this.textureWidth = 256
        this.textureHeight = 128
        this.panelMain = ModelRenderer(this, 0, 0)
        this.panelMain.addBox(-23f, -0.5f, -23f, 46, 1, 46)
        this.panelMain.setRotationPoint(0f, 0f, 0f)
        this.panelMain.setTextureSize(256, 128)
        this.panelMain.mirror = true
        this.setRotation(this.panelMain, 0f, 0f, 0f)
        this.sideHorizontal0 = ModelRenderer(this, 0, 48)
        this.sideHorizontal0.addBox(-24f, -1.111f, -23f, 1, 1, 46)
        this.sideHorizontal0.setRotationPoint(0f, 0f, 0f)
        this.sideHorizontal0.setTextureSize(256, 128)
        this.sideHorizontal0.mirror = true
        this.setRotation(this.sideHorizontal0, 0f, 0f, 0f)
        this.sideVertical0 = ModelRenderer(this, 94, 48)
        this.sideVertical0.addBox(-24f, -1.1f, 23f, 48, 1, 1)
        this.sideVertical0.setRotationPoint(0f, 0f, 0f)
        this.sideVertical0.setTextureSize(256, 128)
        this.sideVertical0.mirror = true
        this.setRotation(this.sideVertical0, 0f, 0f, 0f)
        this.sideVertical2 = ModelRenderer(this, 94, 48)
        this.sideVertical2.addBox(-24f, -1.1f, -24f, 48, 1, 1)
        this.sideVertical2.setRotationPoint(0f, 0f, 0f)
        this.sideVertical2.setTextureSize(256, 128)
        this.sideVertical2.mirror = true
        this.setRotation(this.sideVertical2, 0f, 0f, 0f)
        this.sideVertical1 = ModelRenderer(this, 94, 48)
        this.sideVertical1.addBox(-24f, -1.1f, -0.5f, 48, 1, 1)
        this.sideVertical1.setRotationPoint(0f, 0f, 0f)
        this.sideVertical1.setTextureSize(256, 128)
        this.sideVertical1.mirror = true
        this.setRotation(this.sideVertical1, 0f, 0f, 0f)
        this.sideHorizontal1 = ModelRenderer(this, 0, 48)
        this.sideHorizontal1.addBox(-9f, -1.111f, -23f, 1, 1, 46)
        this.sideHorizontal1.setRotationPoint(0f, 0f, 0f)
        this.sideHorizontal1.setTextureSize(256, 128)
        this.sideHorizontal1.mirror = true
        this.setRotation(this.sideHorizontal1, 0f, 0f, 0f)
        this.sideHorizontal3 = ModelRenderer(this, 0, 48)
        this.sideHorizontal3.addBox(23f, -1.111f, -23f, 1, 1, 46)
        this.sideHorizontal3.setRotationPoint(0f, 0f, 0f)
        this.sideHorizontal3.setTextureSize(256, 128)
        this.sideHorizontal3.mirror = true
        this.setRotation(this.sideHorizontal3, 0f, 0f, 0f)
        this.sideHorizontal2 = ModelRenderer(this, 0, 48)
        this.sideHorizontal2.addBox(8f, -1.111f, -23f, 1, 1, 46)
        this.sideHorizontal2.setRotationPoint(0f, 0f, 0f)
        this.sideHorizontal2.setTextureSize(256, 128)
        this.sideHorizontal2.mirror = true
        this.setRotation(this.sideHorizontal2, 0f, 0f, 0f)
        this.pole = ModelRenderer(this, 94, 50)
        this.pole.addBox(-1.5f, 0.0f, -1.5f, 3, 24, 3)
        this.pole.setRotationPoint(0f, 0f, 0f)
        this.pole.setTextureSize(256, 128)
        this.pole.mirror = true
        this.setRotation(this.pole, 0f, 0f, 0f)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    open fun renderPanel() {
        this.panelMain.render(0.0625f)
        this.sideHorizontal0.render(0.0625f)
        this.sideVertical0.render(0.0625f)
        this.sideVertical2.render(0.0625f)
        this.sideVertical1.render(0.0625f)
        this.sideHorizontal1.render(0.0625f)
        this.sideHorizontal3.render(0.0625f)
        this.sideHorizontal2.render(0.0625f)
    }

    open fun renderPole() {
        this.pole.render(0.0625f)
    }

    override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, e: Entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e)
    }
}
