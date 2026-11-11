package galaxyspace.systems.SolarSystem.moons.enceladus.models

import galaxyspace.systems.SolarSystem.moons.enceladus.models.ModelCrystal
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity

open class ModelCrystal constructor() : ModelBase() {
    //fields
    @JvmField
    var Shape1: ModelRenderer

    @JvmField
    var Shape2: ModelRenderer

    @JvmField
    var Shape3: ModelRenderer

    @JvmField
    var Shape5: ModelRenderer

    @JvmField
    var Shape6: ModelRenderer

    @JvmField
    var Shape7: ModelRenderer

    @JvmField
    var Shape8: ModelRenderer

    @JvmField
    var Shape9: ModelRenderer

    @JvmField
    var Shape10: ModelRenderer

    @JvmField
    var Shape11: ModelRenderer

    @JvmField
    var Shape12: ModelRenderer

    @JvmField
    var Shape13: ModelRenderer

    @JvmField
    var Shape14: ModelRenderer

    @JvmField
    var Shape15: ModelRenderer

    init {
        textureWidth = 64
        textureHeight = 32

        Shape1 = ModelRenderer(this, 20, 0)
        Shape1.addBox(0f, -16f, 0f, 3, 16, 3)
        Shape1.setRotationPoint(-1.5f, 24f, 0f)
        Shape1.setTextureSize(64, 32)
        Shape1.mirror = true
        setRotation(Shape1, 0.2845197f, -0.2792527f, 0.122173f)
        Shape2 = ModelRenderer(this, 0, 0)
        Shape2.addBox(0f, -7f, 0f, 1, 7, 1)
        Shape2.setRotationPoint(0f, 24f, 3f)
        Shape2.setTextureSize(64, 32)
        Shape2.mirror = true
        setRotation(Shape2, -0.1858931f, 0.3141593f, 0.0698132f)
        Shape3 = ModelRenderer(this, 0, 0)
        Shape3.addBox(0f, -7f, 0f, 2, 7, 2)
        Shape3.setRotationPoint(-2f, 24f, 3f)
        Shape3.setTextureSize(64, 32)
        Shape3.mirror = true
        setRotation(Shape3, -0.122173f, 0.1570796f, -0.2094395f)
        Shape5 = ModelRenderer(this, 0, 0)
        Shape5.addBox(0f, -5f, 0f, 2, 5, 2)
        Shape5.setRotationPoint(0f, 24f, 0f)
        Shape5.setTextureSize(64, 32)
        Shape5.mirror = true
        setRotation(Shape5, -0.4833219f, 2.044824f, -0.0371786f)
        Shape6 = ModelRenderer(this, 0, 0)
        Shape6.addBox(0f, -9f, 0f, 2, 9, 2)
        Shape6.setRotationPoint(-2f, 24f, 3f)
        Shape6.setTextureSize(64, 32)
        Shape6.mirror = true
        setRotation(Shape6, -0.0607251f, 3.141593f, 0.2997009f)
        Shape7 = ModelRenderer(this, 0, 0)
        Shape7.addBox(0f, -4f, 0f, 3, 4, 3)
        Shape7.setRotationPoint(-2f, 24f, 1f)
        Shape7.setTextureSize(64, 32)
        Shape7.mirror = true
        setRotation(Shape7, 0f, 2.700407f, 0.1396263f)
        Shape8 = ModelRenderer(this, 0, 0)
        Shape8.addBox(0f, -7f, 0f, 1, 7, 1)
        Shape8.setRotationPoint(-2f, 24f, -2f)
        Shape8.setTextureSize(64, 32)
        Shape8.mirror = true
        setRotation(Shape8, 0.4712389f, 0.0698132f, 0.0349066f)
        Shape8 = ModelRenderer(this, 0, 0)
        Shape8.addBox(0f, -6f, 0f, 1, 6, 1)
        Shape8.setRotationPoint(-1f, 24f, -2f)
        Shape8.setTextureSize(64, 32)
        Shape8.mirror = true
        setRotation(Shape8, 0.2443461f, 0.2792527f, 0.1919862f)
        Shape9 = ModelRenderer(this, 0, 0)
        Shape9.addBox(0f, -6f, 0f, 1, 6, 1)
        Shape9.setRotationPoint(1f, 24f, 2f)
        Shape9.setTextureSize(64, 32)
        Shape9.mirror = true
        setRotation(Shape9, 0f, -0.2617994f, 0.9599311f)
        Shape9 = ModelRenderer(this, 0, 0)
        Shape9.addBox(0f, -9f, 0f, 1, 9, 1)
        Shape9.setRotationPoint(1f, 24f, 1f)
        Shape9.setTextureSize(64, 32)
        Shape9.mirror = true
        setRotation(Shape9, 0f, 0.2094395f, 0.3665191f)
        Shape10 = ModelRenderer(this, 0, 0)
        Shape10.addBox(-1f, -5f, 0f, 1, 5, 1)
        Shape10.setRotationPoint(2f, 24f, 0f)
        Shape10.setTextureSize(64, 32)
        Shape10.mirror = true
        setRotation(Shape10, 0f, 0.4537856f, 0.7679449f)
        Shape11 = ModelRenderer(this, 0, 0)
        Shape11.addBox(0f, -6f, 0f, 1, 6, 1)
        Shape11.setRotationPoint(-3f, 24f, 3f)
        Shape11.setTextureSize(64, 32)
        Shape11.mirror = true
        setRotation(Shape11, -0.1919862f, 0f, -0.3316126f)
        Shape12 = ModelRenderer(this, 0, 0)
        Shape12.addBox(0f, -2f, 0f, 2, 2, 2)
        Shape12.setRotationPoint(1.6f, 24f, 1f)
        Shape12.setTextureSize(64, 32)
        Shape12.mirror = true
        setRotation(Shape12, 0.122173f, 0.122173f, 0.4712389f)
        Shape13 = ModelRenderer(this, 0, 0)
        Shape13.addBox(0f, -3f, 0f, 1, 3, 1)
        Shape13.setRotationPoint(1f, 24f, 1.8f)
        Shape13.setTextureSize(64, 32)
        Shape13.mirror = true
        setRotation(Shape13, -0.4886922f, 0.1047198f, 0.1919862f)
        Shape14 = ModelRenderer(this, 0, 0)
        Shape14.addBox(-1f, -3f, -1f, 1, 3, 1)
        Shape14.setRotationPoint(-0.5f, 24f, -2f)
        Shape14.setTextureSize(64, 32)
        Shape14.mirror = true
        setRotation(Shape14, 0.8028515f, -0.1396263f, -0.0174533f)
        Shape15 = ModelRenderer(this, 0, 0)
        Shape15.addBox(0f, -7f, 0f, 1, 7, 1)
        Shape15.setRotationPoint(-3f, 24f, 0f)
        Shape15.setTextureSize(64, 32)
        Shape15.mirror = true
        setRotation(Shape15, 0f, -0.3839724f, -0.2974289f)
        Shape1 = ModelRenderer(this, 33, 0)
        Shape1.addBox(0f, -16f, 0f, 4, 16, 4)
        Shape1.setRotationPoint(-2f, 24f, -1f)
        Shape1.setTextureSize(64, 32)
        Shape1.mirror = true
        setRotation(Shape1, 0.2496131f, -0.2792527f, 0.122173f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        Shape1.render(f5)
        Shape2.render(f5)
        Shape3.render(f5)
        Shape5.render(f5)
        Shape6.render(f5)
        Shape7.render(f5)
        Shape8.render(f5)
        Shape8.render(f5)
        Shape9.render(f5)
        Shape9.render(f5)
        Shape10.render(f5)
        Shape11.render(f5)
        Shape12.render(f5)
        Shape13.render(f5)
        Shape14.render(f5)
        Shape15.render(f5)
        Shape1.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
    }
}
