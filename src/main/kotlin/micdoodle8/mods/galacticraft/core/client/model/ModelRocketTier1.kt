package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity

open class ModelRocketTier1 constructor() : ModelBase() {
    @JvmField
    var insideRoof: ModelRenderer

    @JvmField
    var rocketBase1: ModelRenderer

    @JvmField
    var rocketBase2: ModelRenderer

    @JvmField
    var tip: ModelRenderer

    @JvmField
    var wing4d: ModelRenderer

    @JvmField
    var wing4c: ModelRenderer

    @JvmField
    var wing4e: ModelRenderer

    @JvmField
    var wing4b: ModelRenderer

    @JvmField
    var wing4a: ModelRenderer

    @JvmField
    var wing1a: ModelRenderer

    @JvmField
    var wing1b: ModelRenderer

    @JvmField
    var wing1c: ModelRenderer

    @JvmField
    var wing1e: ModelRenderer

    @JvmField
    var wing1d: ModelRenderer

    @JvmField
    var wing2e: ModelRenderer

    @JvmField
    var wing2d: ModelRenderer

    @JvmField
    var wing2c: ModelRenderer

    @JvmField
    var wing2b: ModelRenderer

    @JvmField
    var wing2a: ModelRenderer

    @JvmField
    var wing3e: ModelRenderer

    @JvmField
    var wing3d: ModelRenderer

    @JvmField
    var wing3c: ModelRenderer

    @JvmField
    var wing3b: ModelRenderer

    @JvmField
    var wing3a: ModelRenderer

    @JvmField
    var top1: ModelRenderer

    @JvmField
    var top2: ModelRenderer

    @JvmField
    var top3: ModelRenderer

    @JvmField
    var top4: ModelRenderer

    @JvmField
    var top5: ModelRenderer

    @JvmField
    var top6: ModelRenderer

    @JvmField
    var top7: ModelRenderer

    @JvmField
    var insideBottom: ModelRenderer

    @JvmField
    var insideLeft: ModelRenderer

    @JvmField
    var insidetop: ModelRenderer

    @JvmField
    var rocketBase3: ModelRenderer

    @JvmField
    var insideRight: ModelRenderer

    @JvmField
    var insideSideLeft: ModelRenderer

    @JvmField
    var insideSideRight: ModelRenderer

    @JvmField
    var insideSideBack: ModelRenderer

    @JvmField
    var insideFloor: ModelRenderer

    init {
        this.textureWidth = 256
        this.textureHeight = 256

        this.insideRoof = ModelRenderer(this, 0, 59)
        this.insideRoof.addBox(-9f, -45f, -9f, 18, 1, 18)
        this.insideRoof.setRotationPoint(0f, 23f, 0f)
        this.insideRoof.setTextureSize(256, 256)
        this.insideRoof.mirror = true
        this.setRotation(this.insideRoof, 0f, 0f, 0f)
        this.rocketBase1 = ModelRenderer(this, 0, 0)
        this.rocketBase1.addBox(-7f, -1f, -7f, 14, 1, 14)
        this.rocketBase1.setRotationPoint(0f, 24f, 0f)
        this.rocketBase1.setTextureSize(256, 256)
        this.rocketBase1.mirror = true
        this.setRotation(this.rocketBase1, 0f, 0f, 0f)
        this.rocketBase2 = ModelRenderer(this, 0, 15)
        this.rocketBase2.addBox(-6f, -2f, -6f, 12, 1, 12)
        this.rocketBase2.setRotationPoint(0f, 24f, 0f)
        this.rocketBase2.setTextureSize(256, 256)
        this.rocketBase2.mirror = true
        this.setRotation(this.rocketBase2, 0f, 0f, 0f)
        this.tip = ModelRenderer(this, 248, 144)
        this.tip.addBox(-1f, -76f, -1f, 2, 18, 2)
        this.tip.setRotationPoint(0f, 24f, 0f)
        this.tip.setTextureSize(256, 256)
        this.tip.mirror = true
        this.setRotation(this.tip, 0f, 0f, 0f)
        this.wing4d = ModelRenderer(this, 66, 0)
        this.wing4d.addBox(11f, -14f, -1f, 2, 8, 2)
        this.wing4d.setRotationPoint(0f, 24f, 0f)
        this.wing4d.setTextureSize(256, 256)
        this.wing4d.mirror = true
        this.setRotation(this.wing4d, 0f, 0f, 0f)
        this.wing4c = ModelRenderer(this, 66, 0)
        this.wing4c.addBox(13f, -12f, -1f, 2, 8, 2)
        this.wing4c.setRotationPoint(0f, 24f, 0f)
        this.wing4c.setTextureSize(256, 256)
        this.wing4c.mirror = true
        this.setRotation(this.wing4c, 0f, 0f, 0f)
        this.wing4e = ModelRenderer(this, 66, 0)
        this.wing4e.addBox(9.1f, -15f, -1f, 2, 8, 2)
        this.wing4e.setRotationPoint(0f, 24f, 0f)
        this.wing4e.setTextureSize(256, 256)
        this.wing4e.mirror = true
        this.setRotation(this.wing4e, 0f, 0f, 0f)
        this.wing4b = ModelRenderer(this, 66, 0)
        this.wing4b.addBox(15f, -9f, -1f, 2, 8, 2)
        this.wing4b.setRotationPoint(0f, 24f, 0f)
        this.wing4b.setTextureSize(256, 256)
        this.wing4b.mirror = true
        this.setRotation(this.wing4b, 0f, 0f, 0f)
        this.wing4a = ModelRenderer(this, 74, 0)
        this.wing4a.addBox(17f, -14f, -1f, 1, 15, 2)
        this.wing4a.setRotationPoint(0f, 24f, 0f)
        this.wing4a.setTextureSize(256, 256)
        this.wing4a.mirror = true
        this.setRotation(this.wing4a, 0f, 0f, 0f)
        this.wing1a = ModelRenderer(this, 60, 0)
        this.wing1a.addBox(-1f, -14f, -18f, 2, 15, 1)
        this.wing1a.setRotationPoint(0f, 24f, 0f)
        this.wing1a.setTextureSize(256, 256)
        this.wing1a.mirror = true
        this.setRotation(this.wing1a, 0f, 0f, 0f)
        this.wing1b = ModelRenderer(this, 66, 0)
        this.wing1b.addBox(-1f, -9f, -17f, 2, 8, 2)
        this.wing1b.setRotationPoint(0f, 24f, 0f)
        this.wing1b.setTextureSize(256, 256)
        this.wing1b.mirror = true
        this.setRotation(this.wing1b, 0f, 0f, 0f)
        this.wing1c = ModelRenderer(this, 66, 0)
        this.wing1c.addBox(-1f, -12f, -15f, 2, 8, 2)
        this.wing1c.setRotationPoint(0f, 24f, 0f)
        this.wing1c.setTextureSize(256, 256)
        this.wing1c.mirror = true
        this.setRotation(this.wing1c, 0f, 0f, 0f)
        this.wing1e = ModelRenderer(this, 66, 0)
        this.wing1e.addBox(-1f, -15f, -11.1f, 2, 8, 2)
        this.wing1e.setRotationPoint(0f, 24f, 0f)
        this.wing1e.setTextureSize(256, 256)
        this.wing1e.mirror = true
        this.setRotation(this.wing1e, 0f, 0f, 0f)
        this.wing1d = ModelRenderer(this, 66, 0)
        this.wing1d.addBox(-1f, -14f, -13f, 2, 8, 2)
        this.wing1d.setRotationPoint(0f, 24f, 0f)
        this.wing1d.setTextureSize(256, 256)
        this.wing1d.mirror = true
        this.setRotation(this.wing1d, 0f, 0f, 0f)
        this.wing2e = ModelRenderer(this, 66, 0)
        this.wing2e.addBox(-11.1f, -15f, -1f, 2, 8, 2)
        this.wing2e.setRotationPoint(0f, 24f, 0f)
        this.wing2e.setTextureSize(256, 256)
        this.wing2e.mirror = true
        this.setRotation(this.wing2e, 0f, 0f, 0f)
        this.wing2d = ModelRenderer(this, 66, 0)
        this.wing2d.addBox(-13f, -14f, -1f, 2, 8, 2)
        this.wing2d.setRotationPoint(0f, 24f, 0f)
        this.wing2d.setTextureSize(256, 256)
        this.wing2d.mirror = true
        this.setRotation(this.wing2d, 0f, 0f, 0f)
        this.wing2c = ModelRenderer(this, 66, 0)
        this.wing2c.addBox(-15f, -12f, -1f, 2, 8, 2)
        this.wing2c.setRotationPoint(0f, 24f, 0f)
        this.wing2c.setTextureSize(256, 256)
        this.wing2c.mirror = true
        this.setRotation(this.wing2c, 0f, 0f, 0f)
        this.wing2b = ModelRenderer(this, 66, 0)
        this.wing2b.addBox(-17f, -9f, -1f, 2, 8, 2)
        this.wing2b.setRotationPoint(0f, 24f, 0f)
        this.wing2b.setTextureSize(256, 256)
        this.wing2b.mirror = true
        this.setRotation(this.wing2b, 0f, 0f, 0f)
        this.wing2a = ModelRenderer(this, 74, 0)
        this.wing2a.addBox(-18f, -14f, -1f, 1, 15, 2)
        this.wing2a.setRotationPoint(0f, 24f, 0f)
        this.wing2a.setTextureSize(256, 256)
        this.wing2a.mirror = true
        this.setRotation(this.wing2a, 0f, 0f, 0f)
        this.wing3e = ModelRenderer(this, 66, 0)
        this.wing3e.addBox(-1f, -15f, 9.1f, 2, 8, 2)
        this.wing3e.setRotationPoint(0f, 24f, 0f)
        this.wing3e.setTextureSize(256, 256)
        this.wing3e.mirror = true
        this.setRotation(this.wing3e, 0f, 0f, 0f)
        this.wing3d = ModelRenderer(this, 66, 0)
        this.wing3d.addBox(-1f, -14f, 11f, 2, 8, 2)
        this.wing3d.setRotationPoint(0f, 24f, 0f)
        this.wing3d.setTextureSize(256, 256)
        this.wing3d.mirror = true
        this.setRotation(this.wing3d, 0f, 0f, 0f)
        this.wing3c = ModelRenderer(this, 66, 0)
        this.wing3c.addBox(-1f, -12f, 13f, 2, 8, 2)
        this.wing3c.setRotationPoint(0f, 24f, 0f)
        this.wing3c.setTextureSize(256, 256)
        this.wing3c.mirror = true
        this.setRotation(this.wing3c, 0f, 0f, 0f)
        this.wing3b = ModelRenderer(this, 66, 0)
        this.wing3b.addBox(-1f, -9f, 15f, 2, 8, 2)
        this.wing3b.setRotationPoint(0f, 24f, 0f)
        this.wing3b.setTextureSize(256, 256)
        this.wing3b.mirror = true
        this.setRotation(this.wing3b, 0f, 0f, 0f)
        this.wing3a = ModelRenderer(this, 60, 0)
        this.wing3a.addBox(-1f, -14f, 17f, 2, 15, 1)
        this.wing3a.setRotationPoint(0f, 24f, 0f)
        this.wing3a.setTextureSize(256, 256)
        this.wing3a.mirror = true
        this.setRotation(this.wing3a, 0f, 0f, 0f)
        this.top1 = ModelRenderer(this, 192, 60)
        this.top1.addBox(-8f, -48f, -8f, 16, 2, 16)
        this.top1.setRotationPoint(0f, 24f, 0f)
        this.top1.setTextureSize(256, 256)
        this.top1.mirror = true
        this.setRotation(this.top1, 0f, 0f, 0f)
        this.top2 = ModelRenderer(this, 200, 78)
        this.top2.addBox(-7f, -50f, -7f, 14, 2, 14)
        this.top2.setRotationPoint(0f, 24f, 0f)
        this.top2.setTextureSize(256, 256)
        this.top2.mirror = true
        this.setRotation(this.top2, 0f, 0f, 0f)
        this.top3 = ModelRenderer(this, 208, 94)
        this.top3.addBox(-6f, -52f, -6f, 12, 2, 12)
        this.top3.setRotationPoint(0f, 24f, 0f)
        this.top3.setTextureSize(256, 256)
        this.top3.mirror = true
        this.setRotation(this.top3, 0f, 0f, 0f)
        this.top4 = ModelRenderer(this, 216, 108)
        this.top4.addBox(-5f, -54f, -5f, 10, 2, 10)
        this.top4.setRotationPoint(0f, 24f, 0f)
        this.top4.setTextureSize(256, 256)
        this.top4.mirror = true
        this.setRotation(this.top4, 0f, 0f, 0f)
        this.top5 = ModelRenderer(this, 224, 120)
        this.top5.addBox(-4f, -56f, -4f, 8, 2, 8)
        this.top5.setRotationPoint(0f, 24f, 0f)
        this.top5.setTextureSize(256, 256)
        this.top5.mirror = true
        this.setRotation(this.top5, 0f, 0f, 0f)
        this.top6 = ModelRenderer(this, 232, 130)
        this.top6.addBox(-3f, -58f, -3f, 6, 2, 6)
        this.top6.setRotationPoint(0f, 24f, 0f)
        this.top6.setTextureSize(256, 256)
        this.top6.mirror = true
        this.setRotation(this.top6, 0f, 0f, 0f)
        this.top7 = ModelRenderer(this, 240, 138)
        this.top7.addBox(-2f, -60f, -2f, 4, 2, 4)
        this.top7.setRotationPoint(0f, 24f, 0f)
        this.top7.setTextureSize(256, 256)
        this.top7.mirror = true
        this.setRotation(this.top7, 0f, 0f, 0f)
        this.insideBottom = ModelRenderer(this, 85, 18)
        this.insideBottom.addBox(-3.9f, -22f, -8.9f, 8, 17, 1)
        this.insideBottom.setRotationPoint(0f, 24f, 0f)
        this.insideBottom.setTextureSize(256, 256)
        this.insideBottom.mirror = true
        this.setRotation(this.insideBottom, 0f, 0f, 0f)
        this.insideLeft = ModelRenderer(this, 103, 0)
        this.insideLeft.addBox(3.9f, -46f, -8.9f, 5, 41, 1)
        this.insideLeft.setRotationPoint(0f, 24f, 0f)
        this.insideLeft.setTextureSize(256, 256)
        this.insideLeft.mirror = true
        this.setRotation(this.insideLeft, 0f, 0f, 0f)
        this.insidetop = ModelRenderer(this, 85, 0)
        this.insidetop.addBox(-3.9f, -46f, -8.9f, 8, 17, 1)
        this.insidetop.setRotationPoint(0f, 24f, 0f)
        this.insidetop.setTextureSize(256, 256)
        this.insidetop.mirror = true
        this.setRotation(this.insidetop, 0f, 0f, 0f)
        this.rocketBase3 = ModelRenderer(this, 0, 28)
        this.rocketBase3.addBox(-5f, -4f, -5f, 10, 2, 10)
        this.rocketBase3.setRotationPoint(0f, 24f, 0f)
        this.rocketBase3.setTextureSize(256, 256)
        this.rocketBase3.mirror = true
        this.setRotation(this.rocketBase3, 0f, 0f, 0f)
        this.insideRight = ModelRenderer(this, 103, 42)
        this.insideRight.addBox(-8.9f, -46f, -8.9f, 5, 41, 1)
        this.insideRight.setRotationPoint(0f, 24f, 0f)
        this.insideRight.setTextureSize(256, 256)
        this.insideRight.mirror = true
        this.setRotation(this.insideRight, 0f, 0f, 0f)
        this.insideSideLeft = ModelRenderer(this, 119, 57)
        this.insideSideLeft.addBox(8.1f, -46f, -7.9f, 1, 41, 17)
        this.insideSideLeft.setRotationPoint(0f, 24f, 0f)
        this.insideSideLeft.setTextureSize(256, 256)
        this.insideSideLeft.mirror = true
        this.setRotation(this.insideSideLeft, 0f, 0f, 0f)
        this.insideSideRight = ModelRenderer(this, 120, 0)
        this.insideSideRight.addBox(-8.9f, -46f, -7.9f, 1, 41, 16)
        this.insideSideRight.setRotationPoint(0f, 24f, 0f)
        this.insideSideRight.setTextureSize(256, 256)
        this.insideSideRight.mirror = true
        this.setRotation(this.insideSideRight, 0f, 0f, 0f)
        this.insideSideBack = ModelRenderer(this, 120, 114)
        this.insideSideBack.addBox(-8.9f, -46f, 8.1f, 17, 41, 1)
        this.insideSideBack.setRotationPoint(0f, 24f, 0f)
        this.insideSideBack.setTextureSize(256, 256)
        this.insideSideBack.mirror = true
        this.setRotation(this.insideSideBack, 0f, 0f, 0f)
        this.insideFloor = ModelRenderer(this, 0, 40)
        this.insideFloor.addBox(-9f, -4f, -9f, 18, 1, 18)
        this.insideFloor.setRotationPoint(0f, 23f, 0f)
        this.insideFloor.setTextureSize(256, 256)
        this.insideFloor.mirror = true
        this.setRotation(this.insideFloor, 0f, 0f, 0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        this.insideRoof.render(f5)
        this.rocketBase1.render(f5)
        this.rocketBase2.render(f5)
        this.tip.render(f5)
        this.wing4d.render(f5)
        this.wing4c.render(f5)
        this.wing4e.render(f5)
        this.wing4b.render(f5)
        this.wing4a.render(f5)
        this.wing1a.render(f5)
        this.wing1b.render(f5)
        this.wing1c.render(f5)
        this.wing1e.render(f5)
        this.wing1d.render(f5)
        this.wing2e.render(f5)
        this.wing2d.render(f5)
        this.wing2c.render(f5)
        this.wing2b.render(f5)
        this.wing2a.render(f5)
        this.wing3e.render(f5)
        this.wing3d.render(f5)
        this.wing3c.render(f5)
        this.wing3b.render(f5)
        this.wing3a.render(f5)
        this.top1.render(f5)
        this.top2.render(f5)
        this.top3.render(f5)
        this.top4.render(f5)
        this.top5.render(f5)
        this.top6.render(f5)
        this.top7.render(f5)
        this.insideBottom.render(f5)
        this.insideLeft.render(f5)
        this.insidetop.render(f5)
        this.rocketBase3.render(f5)
        this.insideRight.render(f5)
        this.insideSideLeft.render(f5)
        this.insideSideRight.render(f5)
        this.insideSideBack.render(f5)
        this.insideFloor.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}
