package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.model.ModelWitch
import net.minecraft.entity.Entity

open class ModelEvolvedWitch constructor() : ModelWitch(0.0f) {
    @JvmField
    var tank1: ModelRenderer

    @JvmField
    var tank2: ModelRenderer

    @JvmField
    var oxygenMask: ModelRenderer

    @JvmField
    var tube1: ModelRenderer

    @JvmField
    var tube2: ModelRenderer

    @JvmField
    var tube3: ModelRenderer

    @JvmField
    var tube4: ModelRenderer

    @JvmField
    var tube5: ModelRenderer

    @JvmField
    var tube6: ModelRenderer

    @JvmField
    var tube7: ModelRenderer

    @JvmField
    var tube8: ModelRenderer

    @JvmField
    var tube9: ModelRenderer

    @JvmField
    var tube10: ModelRenderer

    @JvmField
    var tube11: ModelRenderer

    @JvmField
    var tube12: ModelRenderer

    @JvmField
    var tube13: ModelRenderer

    @JvmField
    var tube14: ModelRenderer

    @JvmField
    var tube15: ModelRenderer

    @JvmField
    var tube16: ModelRenderer

    @JvmField
    var tube17: ModelRenderer

    @JvmField
    var tube18: ModelRenderer

    init {
        this.textureWidth = 64
        this.textureHeight = 128

        this.tank1 = ModelRenderer(this, 52, 66)
        this.tank1.addBox(0f, 0f, 0f, 3, 7, 3)
        this.tank1.setRotationPoint(0f, 4f, 3f)

        this.tank2 = ModelRenderer(this, 52, 66)
        this.tank2.addBox(0f, 0f, 0f, 3, 7, 3)
        this.tank2.setRotationPoint(-3f, 4f, 3f)

        this.oxygenMask = ModelRenderer(this, 24, 90)
        this.oxygenMask.addBox(-5f, -9f, -5f, 10, 10, 10)
        this.oxygenMask.setRotationPoint(0f, 1f, 0f)

        this.tube1 = ModelRenderer(this, 60, 76)
        this.tube1.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube1.setRotationPoint(1f, 4.5f, 6f)

        this.tube2 = ModelRenderer(this, 60, 76)
        this.tube2.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube2.setRotationPoint(1f, 3.5f, 7f)

        this.tube3 = ModelRenderer(this, 60, 76)
        this.tube3.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube3.setRotationPoint(1f, 2.5f, 7.5f)

        this.tube4 = ModelRenderer(this, 60, 76)
        this.tube4.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube4.setRotationPoint(1f, 1.5f, 7.5f)

        this.tube5 = ModelRenderer(this, 60, 76)
        this.tube5.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube5.setRotationPoint(1f, 0.5f, 7.5f)

        this.tube6 = ModelRenderer(this, 60, 76)
        this.tube6.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube6.setRotationPoint(1f, -0.5f, 7f)

        this.tube7 = ModelRenderer(this, 60, 76)
        this.tube7.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube7.setRotationPoint(1f, -1.5f, 6f)

        this.tube8 = ModelRenderer(this, 60, 76)
        this.tube8.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube8.setRotationPoint(1f, -2.5f, 5f)

        this.tube9 = ModelRenderer(this, 60, 76)
        this.tube9.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube9.setRotationPoint(1f, -3f, 4f)

        this.tube10 = ModelRenderer(this, 60, 76)
        this.tube10.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube10.setRotationPoint(-2f, 4.5f, 6f)

        this.tube11 = ModelRenderer(this, 60, 76)
        this.tube11.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube11.setRotationPoint(-2f, 3.5f, 7f)

        this.tube12 = ModelRenderer(this, 60, 76)
        this.tube12.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube12.setRotationPoint(-2f, 2.5f, 7.5f)

        this.tube13 = ModelRenderer(this, 60, 76)
        this.tube13.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube13.setRotationPoint(-2f, 1.5f, 7.5f)

        this.tube14 = ModelRenderer(this, 60, 76)
        this.tube14.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube14.setRotationPoint(-2f, 0.5f, 7.5f)

        this.tube15 = ModelRenderer(this, 60, 76)
        this.tube15.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube15.setRotationPoint(-2f, -0.5f, 7f)

        this.tube16 = ModelRenderer(this, 60, 76)
        this.tube16.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube16.setRotationPoint(-2f, -1.5f, 6f)

        this.tube17 = ModelRenderer(this, 60, 76)
        this.tube17.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube17.setRotationPoint(-2f, -2.5f, 5f)

        this.tube18 = ModelRenderer(this, 60, 76)
        this.tube18.addBox(0f, 0f, 0f, 1, 1, 1)
        this.tube18.setRotationPoint(-2f, -3f, 4f)
    }

    override fun render(
        entity: Entity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
        this.tank1.render(scale)
        this.tank2.render(scale)
        this.oxygenMask.render(scale)
        this.tube1.render(scale)
        this.tube2.render(scale)
        this.tube3.render(scale)
        this.tube4.render(scale)
        this.tube5.render(scale)
        this.tube6.render(scale)
        this.tube7.render(scale)
        this.tube8.render(scale)
        this.tube9.render(scale)
        this.tube10.render(scale)
        this.tube11.render(scale)
        this.tube12.render(scale)
        this.tube13.render(scale)
        this.tube14.render(scale)
        this.tube15.render(scale)
        this.tube16.render(scale)
        this.tube17.render(scale)
        this.tube18.render(scale)
    }

    override fun setRotationAngles(
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float,
        entity: Entity
    ) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity)
        this.oxygenMask.rotateAngleY = this.villagerHead.rotateAngleY
        this.oxygenMask.rotateAngleX = this.villagerHead.rotateAngleX
    }
}
