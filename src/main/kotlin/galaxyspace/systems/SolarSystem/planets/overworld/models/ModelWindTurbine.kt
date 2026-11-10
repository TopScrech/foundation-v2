package galaxyspace.systems.SolarSystem.planets.overworld.models

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity

open class ModelWindTurbine constructor() : ModelBase() {
    @JvmField
    var rotor: ModelRenderer

    @JvmField
    var shape1: ModelRenderer

    @JvmField
    var rotorChild: ModelRenderer

    @JvmField
    var rotorChild_1: ModelRenderer

    @JvmField
    var rotorChild_2: ModelRenderer

    @JvmField
    var rotorChild_3: ModelRenderer

    @JvmField
    var rotorChild_4: ModelRenderer

    @JvmField
    var rotorChild_5: ModelRenderer

    @JvmField
    var rotorChild_6: ModelRenderer

    @JvmField
    var rotorChild_7: ModelRenderer

    @JvmField
    var rotorChild_8: ModelRenderer

    @JvmField
    var rotorChild_9: ModelRenderer

    @JvmField
    var rotorChild_10: ModelRenderer

    @JvmField
    var rotorChild_11: ModelRenderer

    @JvmField
    var rotorChild_12: ModelRenderer

    @JvmField
    var shape1Child: ModelRenderer

    @JvmField
    var shape1Child_1: ModelRenderer

    @JvmField
    var shape1Child_2: ModelRenderer

    @JvmField
    var shape1Child_3: ModelRenderer

    @JvmField
    var shape1Child_4: ModelRenderer

    @JvmField
    var shape1Child_5: ModelRenderer

    @JvmField
    var shape1Child_6: ModelRenderer

    @JvmField
    var shape1Child_7: ModelRenderer

    @JvmField
    var shape1Child_8: ModelRenderer

    @JvmField
    var shape1Child_9: ModelRenderer

    @JvmField
    var shape1Child_10: ModelRenderer

    @JvmField
    var shape1Child_11: ModelRenderer

    @JvmField
    var shape1Child_12: ModelRenderer

    @JvmField
    var shape1Child_13: ModelRenderer

    @JvmField
    var shape1Child_14: ModelRenderer

    @JvmField
    var shape1Child_15: ModelRenderer

    @JvmField
    var shape1Child_16: ModelRenderer

    @JvmField
    var shape1Child_17: ModelRenderer

    @JvmField
    var shape1Child_18: ModelRenderer

    @JvmField
    var shape1Child_19: ModelRenderer

    @JvmField
    var shape1Child_20: ModelRenderer

    @JvmField
    var shape1Child_21: ModelRenderer

    @JvmField
    var shape1Child_22: ModelRenderer

    @JvmField
    var shape1Child_23: ModelRenderer

    @JvmField
    var shape1Child_24: ModelRenderer

    @JvmField
    var shape1Child_25: ModelRenderer

    @JvmField
    var shape1Child_26: ModelRenderer

    init {
        this.textureWidth = 128
        this.textureHeight = 64
        this.shape1Child_17 = ModelRenderer(this, 0, 0)
        this.shape1Child_17.setRotationPoint(2.4000000953674316f, -38.400001525878906f, 2.5f)
        this.shape1Child_17.addBox(0.0f, 0.0f, 0.0f, 4, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_17, 1.5707963705062866f, 0.0f, 0.7853981852531433f)
        this.shape1Child_11 = ModelRenderer(this, 0, 0)
        this.shape1Child_11.setRotationPoint(2.4000000953674316f, -43.5f, 2.5f)
        this.shape1Child_11.addBox(0.0f, 0.0f, 0.0f, 5, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_11, 1.5707963705062866f, 0.0f, 1.5707963705062866f)
        this.rotorChild_7 = ModelRenderer(this, 13, 29)
        this.rotorChild_7.setRotationPoint(14.100000381469727f, 24.0f, 1.0f)
        this.rotorChild_7.addBox(0.0f, 0.0f, 0.0f, 5, 1, 1, 0.0f)
        this.setRotateAngle(rotorChild_7, 0.0f, 0.0f, -1.832595705986023f)
        this.shape1Child_10 = ModelRenderer(this, 0, 0)
        this.shape1Child_10.setRotationPoint(9.600000381469727f, -46.400001525878906f, 13.300000190734863f)
        this.shape1Child_10.addBox(0.0f, 0.0f, 0.20000000298023224f, 5, 10, 1, 0.0f)
        this.setRotateAngle(shape1Child_10, 0.0f, 0.0f, 0.7853981852531433f)
        this.shape1 = ModelRenderer(this, 12, 1)
        this.shape1.setRotationPoint(-7.900000095367432f, 41.0f, -8.199999809265137f)
        this.shape1.addBox(0.0f, 0.0f, -0.10000000149011612f, 16, 2, 16, 0.0f)
        this.rotorChild_1 = ModelRenderer(this, 14, 28)
        this.rotorChild_1.setRotationPoint(15.300000190734863f, -17.700000762939453f, 1.0f)
        this.rotorChild_1.addBox(0.0f, 0.0f, 0.0f, 4, 2, 1, 0.0f)
        this.setRotateAngle(rotorChild_1, 0.0f, 0.0f, 2.094395160675049f)
        this.shape1Child_18 = ModelRenderer(this, 0, 0)
        this.shape1Child_18.setRotationPoint(5.400000095367432f, -35.599998474121094f, 2.5f)
        this.shape1Child_18.addBox(0.0f, 0.0f, 0.0f, 5, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_18, 1.5707963705062866f, 0.0f, 0.0f)
        this.shape1Child_24 = ModelRenderer(this, 0, 0)
        this.shape1Child_24.setRotationPoint(9.800000190734863f, -39.0f, 12.600000381469727f)
        this.shape1Child_24.addBox(0.0f, 0.0f, 0.0f, 4, 40, 1, 0.0f)
        this.setRotateAngle(shape1Child_24, 0.0f, 0.7853981852531433f, 0.0f)
        this.shape1Child_16 = ModelRenderer(this, 0, 0)
        this.shape1Child_16.setRotationPoint(12.399999618530273f, -39.0f, 10.399999618530273f)
        this.shape1Child_16.addBox(0.0f, 0.0f, 0.0f, 5, 40, 1, 0.0f)
        this.setRotateAngle(shape1Child_16, 0.0f, 1.5707963705062866f, 0.0f)
        this.shape1Child_4 = ModelRenderer(this, 0, 0)
        this.shape1Child_4.setRotationPoint(2.5f, -42.900001525878906f, 2.299999952316284f)
        this.shape1Child_4.addBox(0.0f, 0.0f, 0.20000000298023224f, 5, 10, 1, 0.0f)
        this.setRotateAngle(shape1Child_4, 0.0f, 0.0f, -0.7853981852531433f)
        this.rotorChild_11 = ModelRenderer(this, 12, 29)
        this.rotorChild_11.setRotationPoint(13.800000190734863f, 24.5f, 1.0f)
        this.rotorChild_11.addBox(0.0f, 0.0f, 0.0f, 5, 1, 1, 0.0f)
        this.setRotateAngle(rotorChild_11, 0.0f, 0.0f, -2.356194496154785f)
        this.rotorChild_4 = ModelRenderer(this, 13, 29)
        this.rotorChild_4.setRotationPoint(15.100000381469727f, -18.799999237060547f, 1.0f)
        this.rotorChild_4.addBox(0.0f, 0.0f, 0.0f, 5, 1, 1, 0.0f)
        this.setRotateAngle(rotorChild_4, 0.0f, 0.0f, 2.356194496154785f)
        this.shape1Child_26 = ModelRenderer(this, 0, 0)
        this.shape1Child_26.setRotationPoint(9.800000190734863f, -45.70000076293945f, 2.5f)
        this.shape1Child_26.addBox(0.0f, 0.0f, 0.0f, 4, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_26, 1.5707963705062866f, 0.0f, 0.7853981852531433f)
        this.rotorChild_8 = ModelRenderer(this, 14, 28)
        this.rotorChild_8.setRotationPoint(-19.299999237060547f, 2.0f, 1.0f)
        this.rotorChild_8.addBox(0.0f, 0.0f, 0.0f, 4, 2, 1, 0.0f)
        this.shape1Child_2 = ModelRenderer(this, 0, 0)
        this.shape1Child_2.setRotationPoint(12.399999618530273f, -43.5f, 2.5f)
        this.shape1Child_2.addBox(0.0f, 0.0f, 0.0f, 5, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_2, 1.5707963705062866f, 0.0f, 1.5707963705062866f)
        this.shape1Child_3 = ModelRenderer(this, 0, 0)
        this.shape1Child_3.setRotationPoint(10.5f, -39.0f, 2.5f)
        this.shape1Child_3.addBox(0.0f, 0.0f, 0.0f, 4, 40, 1, 0.0f)
        this.setRotateAngle(shape1Child_3, 0.0f, -0.7853981852531433f, 0.0f)
        this.shape1Child = ModelRenderer(this, 0, 0)
        this.shape1Child.setRotationPoint(9.600000381469727f, -46.400001525878906f, 2.299999952316284f)
        this.shape1Child.addBox(0.0f, 0.0f, 0.20000000298023224f, 5, 10, 1, 0.0f)
        this.setRotateAngle(shape1Child, 0.0f, 0.0f, 0.7853981852531433f)
        this.rotorChild_10 = ModelRenderer(this, 12, 29)
        this.rotorChild_10.setRotationPoint(-20.5f, 2.299999952316284f, 1.0f)
        this.rotorChild_10.addBox(0.0f, 0.0f, 0.0f, 5, 1, 1, 0.0f)
        this.setRotateAngle(rotorChild_10, 0.0f, 0.0f, -0.2617993950843811f)
        this.shape1Child_15 = ModelRenderer(this, 12, 22)
        this.shape1Child_15.setRotationPoint(10.0f, -43.0f, 15.100000381469727f)
        this.shape1Child_15.addBox(0.0f, 0.0f, 0.0f, 4, 4, 1, 0.0f)
        this.setRotateAngle(shape1Child_15, 0.0f, 0.0f, 1.5707963705062866f)
        this.rotorChild = ModelRenderer(this, 12, 27)
        this.rotorChild.setRotationPoint(10.300000190734863f, 21.0f, 1.0f)
        this.rotorChild.addBox(0.0f, 0.0f, 0.0f, 20, 4, 1, 0.0f)
        this.setRotateAngle(rotorChild, 0.0f, 0.0f, -2.094395160675049f)
        this.rotorChild_12 = ModelRenderer(this, 14, 28)
        this.rotorChild_12.setRotationPoint(13.100000381469727f, 23.799999237060547f, 1.0f)
        this.rotorChild_12.addBox(0.0f, 0.0f, 0.0f, 4, 2, 1, 0.0f)
        this.setRotateAngle(rotorChild_12, 0.0f, 0.0f, -2.094395160675049f)
        this.shape1Child_20 = ModelRenderer(this, 12, 22)
        this.shape1Child_20.setRotationPoint(9.0f, -42.0f, 15.600000381469727f)
        this.shape1Child_20.addBox(0.0f, 0.0f, 0.0f, 2, 2, 3, 0.0f)
        this.setRotateAngle(shape1Child_20, 0.0f, 0.0f, 1.5707963705062866f)
        this.rotorChild_9 = ModelRenderer(this, 12, 23)
        this.rotorChild_9.setRotationPoint(4.400000095367432f, 1.5f, 1.7000000476837158f)
        this.rotorChild_9.addBox(0.0f, 0.0f, 0.0f, 3, 3, 1, 0.0f)
        this.setRotateAngle(rotorChild_9, 0.0f, 0.0f, 1.5707963705062866f)
        this.shape1Child_8 = ModelRenderer(this, 0, 0)
        this.shape1Child_8.setRotationPoint(2.5f, -42.900001525878906f, 13.300000190734863f)
        this.shape1Child_8.addBox(0.0f, 0.0f, 0.20000000298023224f, 5, 10, 1, 0.0f)
        this.setRotateAngle(shape1Child_8, 0.0f, 0.0f, -0.7853981852531433f)
        this.shape1Child_23 = ModelRenderer(this, 0, 0)
        this.shape1Child_23.setRotationPoint(5.400000095367432f, -39.0f, 2.5f)
        this.shape1Child_23.addBox(0.0f, 0.0f, 0.0f, 5, 40, 1, 0.0f)
        this.shape1Child_13 = ModelRenderer(this, 0, 0)
        this.shape1Child_13.setRotationPoint(2.5f, -39.0f, 10.300000190734863f)
        this.shape1Child_13.addBox(0.0f, 0.0f, 0.0f, 5, 40, 1, 0.0f)
        this.setRotateAngle(shape1Child_13, 0.0f, 1.5707963705062866f, 0.0f)
        this.rotorChild_2 = ModelRenderer(this, 12, 27)
        this.rotorChild_2.setRotationPoint(14.300000190734863f, -14.0f, 1.0f)
        this.rotorChild_2.addBox(0.0f, 0.0f, 0.0f, 20, 4, 1, 0.0f)
        this.setRotateAngle(rotorChild_2, 0.0f, 0.0f, 2.094395160675049f)
        this.shape1Child_12 = ModelRenderer(this, 0, 0)
        this.shape1Child_12.setRotationPoint(10.600000381469727f, -35.599998474121094f, 2.5f)
        this.shape1Child_12.addBox(0.0f, 0.0f, 0.0f, 4, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_12, 1.5707963705062866f, 0.0f, -0.7853981852531433f)
        this.shape1Child_25 = ModelRenderer(this, 0, 0)
        this.shape1Child_25.setRotationPoint(5.400000095367432f, -45.400001525878906f, 2.5f)
        this.shape1Child_25.addBox(0.0f, 0.0f, 0.0f, 5, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_25, 1.5707963705062866f, 0.0f, 0.0f)
        this.shape1Child_9 = ModelRenderer(this, 0, 0)
        this.shape1Child_9.setRotationPoint(13.199999809265137f, -43.5f, 13.5f)
        this.shape1Child_9.addBox(0.0f, 0.0f, 0.0f, 5, 10, 1, 0.0f)
        this.setRotateAngle(shape1Child_9, 0.0f, 0.0f, 1.5707963705062866f)
        this.shape1Child_6 = ModelRenderer(this, 0, 0)
        this.shape1Child_6.setRotationPoint(3.200000047683716f, -42.900001525878906f, 2.5f)
        this.shape1Child_6.addBox(0.0f, 0.0f, 0.0f, 4, 12, 1, 0.0f)
        this.setRotateAngle(shape1Child_6, 1.5707963705062866f, 0.0f, -0.7853981852531433f)
        this.rotorChild_5 = ModelRenderer(this, 13, 29)
        this.rotorChild_5.setRotationPoint(-20.299999237060547f, 2.700000047683716f, 1.0f)
        this.rotorChild_5.addBox(0.0f, 0.0f, 0.0f, 5, 1, 1, 0.0f)
        this.setRotateAngle(rotorChild_5, 0.0f, 0.0f, 0.2617993950843811f)
        this.shape1Child_22 = ModelRenderer(this, 12, 20)
        this.shape1Child_22.setRotationPoint(11.0f, -44.0f, 14.300000190734863f)
        this.shape1Child_22.addBox(0.0f, 0.0f, 0.0f, 6, 6, 1, 0.0f)
        this.setRotateAngle(shape1Child_22, 0.0f, 0.0f, 1.5707963705062866f)
        this.rotorChild_6 = ModelRenderer(this, 12, 27)
        this.rotorChild_6.setRotationPoint(-15.699999809265137f, 1.0f, 1.0f)
        this.rotorChild_6.addBox(0.0f, 0.0f, 0.0f, 20, 4, 1, 0.0f)
        this.shape1Child_7 = ModelRenderer(this, 0, 0)
        this.shape1Child_7.setRotationPoint(5.300000190734863f, -46.0f, 13.300000190734863f)
        this.shape1Child_7.addBox(0.0f, 0.0f, 0.20000000298023224f, 5, 10, 1, 0.0f)
        this.shape1Child_1 = ModelRenderer(this, 0, 0)
        this.shape1Child_1.setRotationPoint(3.200000047683716f, -39.0f, 9.699999809265137f)
        this.shape1Child_1.addBox(0.0f, 0.0f, 0.0f, 4, 40, 1, 0.0f)
        this.setRotateAngle(shape1Child_1, 0.0f, -0.7853981852531433f, 0.0f)
        this.rotorChild_3 = ModelRenderer(this, 12, 29)
        this.rotorChild_3.setRotationPoint(15.600000381469727f, -18.799999237060547f, 1.0f)
        this.rotorChild_3.addBox(0.0f, 0.0f, 0.0f, 5, 1, 1, 0.0f)
        this.setRotateAngle(rotorChild_3, 0.0f, 0.0f, 1.832595705986023f)
        this.rotor = ModelRenderer(this, 12, 19)
        this.rotor.setRotationPoint(3.0f, -3.05f, 10.2f)
        this.rotor.addBox(0.0f, 0.0f, 0.1f, 6, 6, 2, 0.0f)
        this.setRotateAngle(rotor, 0.0f, 0.0f, 1.5707963267948966f)
        this.shape1Child_14 = ModelRenderer(this, 0, 0)
        this.shape1Child_14.setRotationPoint(5.400000095367432f, -39.0f, 12.300000190734863f)
        this.shape1Child_14.addBox(0.0f, 0.0f, 0.0f, 5, 40, 1, 0.0f)
        this.shape1Child_5 = ModelRenderer(this, 0, 0)
        this.shape1Child_5.setRotationPoint(13.199999809265137f, -43.5f, 2.5f)
        this.shape1Child_5.addBox(0.0f, 0.0f, 0.0f, 5, 10, 1, 0.0f)
        this.setRotateAngle(shape1Child_5, 0.0f, 0.0f, 1.5707963705062866f)
        this.shape1Child_19 = ModelRenderer(this, 0, 0)
        this.shape1Child_19.setRotationPoint(2.5f, -39.0f, 5.300000190734863f)
        this.shape1Child_19.addBox(0.0f, 0.0f, 0.0f, 4, 40, 1, 0.0f)
        this.setRotateAngle(shape1Child_19, 0.0f, 0.7853981852531433f, 0.0f)
        this.shape1Child_21 = ModelRenderer(this, 0, 0)
        this.shape1Child_21.setRotationPoint(5.300000190734863f, -46.099998474121094f, 2.5f)
        this.shape1Child_21.addBox(0.0f, 0.0f, 0.0f, 5, 10, 1, 0.0f)
        this.shape1.addChild(this.shape1Child_17)
        this.shape1.addChild(this.shape1Child_11)
        this.rotor.addChild(this.rotorChild_7)
        this.shape1.addChild(this.shape1Child_10)
        this.rotor.addChild(this.rotorChild_1)
        this.shape1.addChild(this.shape1Child_18)
        this.shape1.addChild(this.shape1Child_24)
        this.shape1.addChild(this.shape1Child_16)
        this.shape1.addChild(this.shape1Child_4)
        this.rotor.addChild(this.rotorChild_11)
        this.rotor.addChild(this.rotorChild_4)
        this.shape1.addChild(this.shape1Child_26)
        this.rotor.addChild(this.rotorChild_8)
        this.shape1.addChild(this.shape1Child_2)
        this.shape1.addChild(this.shape1Child_3)
        this.shape1.addChild(this.shape1Child)
        this.rotor.addChild(this.rotorChild_10)
        this.shape1.addChild(this.shape1Child_15)
        this.rotor.addChild(this.rotorChild)
        this.rotor.addChild(this.rotorChild_12)
        this.shape1.addChild(this.shape1Child_20)
        this.rotor.addChild(this.rotorChild_9)
        this.shape1.addChild(this.shape1Child_8)
        this.shape1.addChild(this.shape1Child_23)
        this.shape1.addChild(this.shape1Child_13)
        this.rotor.addChild(this.rotorChild_2)
        this.shape1.addChild(this.shape1Child_12)
        this.shape1.addChild(this.shape1Child_25)
        this.shape1.addChild(this.shape1Child_9)
        this.shape1.addChild(this.shape1Child_6)
        this.rotor.addChild(this.rotorChild_5)
        this.shape1.addChild(this.shape1Child_22)
        this.rotor.addChild(this.rotorChild_6)
        this.shape1.addChild(this.shape1Child_7)
        this.shape1.addChild(this.shape1Child_1)
        this.rotor.addChild(this.rotorChild_3)
        this.shape1.addChild(this.shape1Child_14)
        this.shape1.addChild(this.shape1Child_5)
        this.shape1.addChild(this.shape1Child_19)
        this.shape1.addChild(this.shape1Child_21)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        this.shape1.render(f5)
        this.rotor.render(f5)
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    open fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
        modelRenderer.rotateAngleX = x
        modelRenderer.rotateAngleY = y
        modelRenderer.rotateAngleZ = z
    }

    open fun renderPole(f5: Float) {
        this.shape1.render(f5)
    }

    open fun renderFlares(f5: Float) {
        this.rotor.render(f5)
    }
}
