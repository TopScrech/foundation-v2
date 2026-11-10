package micdoodle8.mods.galacticraft.planets.venus.client.model

import micdoodle8.mods.galacticraft.core.client.model.ModelRendererGC
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper

open class ModelSpiderQueen constructor() : ModelBase() {
    @JvmField
    var head: ModelRendererGC

    @JvmField
    var body: ModelRendererGC

    @JvmField
    var leg1: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg2: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg3: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg4: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg5: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg6: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg7: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var leg8: Array<ModelRendererGC?> = arrayOfNulls<ModelRendererGC>(3)

    @JvmField
    var rearEnd: ModelRendererGC

    @JvmField
    var rearBack: ModelRendererGC

    @JvmField
    var rearLeft: ModelRendererGC

    @JvmField
    var rearRight: ModelRendererGC

    private val legLength0: Float

    init {
        textureWidth = 64
        textureHeight = 64

        head = ModelRendererGC(this, 32, 4)
        head.addBox(-4f, -4f, -8f, 8, 8, 8)
        head.setRotationPoint(0f, 20f, -3f)
        head.setTextureSize(64, 64)
        head.mirror = true
        setRotation(head, 0f, 0f, 0f)
        body = ModelRendererGC(this, 0, 0)
        body.addBox(-3f, -3f, -3f, 6, 6, 10)
        body.setRotationPoint(0f, 20f, 0f)
        body.setTextureSize(64, 64)
        body.mirror = true
        setRotation(body, 0f, 0f, 0f)

        leg1[0] = ModelRendererGC(this, 28, 0)
        leg1[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg1[0]!!.setRotationPoint(-3f, 20f, 4f)
        leg1[0]!!.setTextureSize(64, 64)
        leg1[0]!!.mirror = true
        setRotation(leg1[0]!!, 0f, 3.631943f, -0.7330383f)
        leg1[0]!!.mirror = true
        leg1[1] = ModelRendererGC(this, 28, 0)
        leg1[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg1[1]!!.setRotationPoint(-9.7f, 13.2f, 7.6f)
        leg1[1]!!.setTextureSize(64, 64)
        leg1[1]!!.mirror = true
        setRotation(leg1[1]!!, 0f, 3.631937f, 0.3823201f)
        leg1[2] = ModelRendererGC(this, 28, 0)
        leg1[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg1[2]!!.setRotationPoint(-16.4f, 16.2f, 11.1f)
        leg1[2]!!.setTextureSize(64, 64)
        leg1[2]!!.mirror = true
        setRotation(leg1[2]!!, 0f, 3.631937f, 1.461656f)

        leg2[0] = ModelRendererGC(this, 28, 0)
        leg2[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg2[0]!!.setRotationPoint(3f, 20f, 4f)
        leg2[0]!!.setTextureSize(64, 64)
        leg2[0]!!.mirror = true
        setRotation(leg2[0]!!, 0f, -0.4903446f, -0.7330383f)
        leg2[1] = ModelRendererGC(this, 28, 0)
        leg2[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg2[1]!!.setRotationPoint(9.666667f, 13.2f, 7.6f)
        leg2[1]!!.setTextureSize(64, 64)
        leg2[1]!!.mirror = true
        setRotation(leg2[1]!!, 0f, -0.4903503f, 0.3823201f)
        leg2[2] = ModelRendererGC(this, 28, 0)
        leg2[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg2[2]!!.setRotationPoint(16.4f, 16.2f, 11.1f)
        leg2[2]!!.setTextureSize(64, 64)
        leg2[2]!!.mirror = true
        setRotation(leg2[2]!!, 0f, -0.4903503f, 1.461656f)

        leg3[0] = ModelRendererGC(this, 28, 0)
        leg3[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg3[0]!!.setRotationPoint(-3f, 20f, 2f)
        leg3[0]!!.setTextureSize(64, 64)
        leg3[0]!!.mirror = true
        setRotation(leg3[0]!!, 0f, 3.335237f, -0.7330383f)
        leg3[1] = ModelRendererGC(this, 28, 0)
        leg3[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg3[1]!!.setRotationPoint(-10.7f, 13.2f, 3.6f)
        leg3[1]!!.setTextureSize(64, 64)
        leg3[1]!!.mirror = true
        setRotation(leg3[1]!!, 0f, 3.335231f, 0.3823201f)
        leg3[1]!!.mirror = false
        leg3[2] = ModelRendererGC(this, 28, 0)
        leg3[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg3[2]!!.setRotationPoint(-17.6f, 16.2f, 4.9f)
        leg3[2]!!.setTextureSize(64, 64)
        leg3[2]!!.mirror = true
        setRotation(leg3[2]!!, 0f, 3.335231f, 1.461656f)

        leg4[0] = ModelRendererGC(this, 28, 0)
        leg4[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg4[0]!!.setRotationPoint(3f, 20f, 2f)
        leg4[0]!!.setTextureSize(64, 64)
        leg4[0]!!.mirror = true
        setRotation(leg4[0]!!, 0f, -0.1936386f, -0.7330383f)
        leg4[1] = ModelRendererGC(this, 28, 0)
        leg4[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg4[1]!!.setRotationPoint(10.66667f, 13.2f, 3.6f)
        leg4[1]!!.setTextureSize(64, 64)
        leg4[1]!!.mirror = true
        setRotation(leg4[1]!!, 0f, -0.1936443f, 0.3823201f)
        leg4[2] = ModelRendererGC(this, 28, 0)
        leg4[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg4[2]!!.setRotationPoint(17.6f, 16.2f, 4.9f)
        leg4[2]!!.setTextureSize(64, 64)
        leg4[2]!!.mirror = true
        setRotation(leg4[2]!!, 0f, -0.1936443f, 1.461656f)

        leg5[0] = ModelRendererGC(this, 28, 0)
        leg5[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg5[0]!!.setRotationPoint(-3f, 20f, 0.5f)
        leg5[0]!!.setTextureSize(64, 64)
        leg5[0]!!.mirror = true
        setRotation(leg5[0]!!, 0f, 2.7838f, -0.7330383f)
        leg5[1] = ModelRendererGC(this, 28, 0)
        leg5[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg5[1]!!.setRotationPoint(-10f, 13.2f, -2.2f)
        leg5[1]!!.setTextureSize(64, 64)
        leg5[1]!!.mirror = true
        setRotation(leg5[1]!!, 0f, 2.783794f, 0.3823201f)
        leg5[1]!!.mirror = false
        leg5[2] = ModelRendererGC(this, 28, 0)
        leg5[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg5[2]!!.setRotationPoint(-16.5f, 16.2f, -4.7f)
        leg5[2]!!.setTextureSize(64, 64)
        leg5[2]!!.mirror = true
        setRotation(leg5[2]!!, 0f, 2.783794f, 1.461656f)

        leg6[0] = ModelRendererGC(this, 28, 0)
        leg6[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg6[0]!!.setRotationPoint(3f, 20f, 0.5f)
        leg6[0]!!.setTextureSize(64, 64)
        leg6[0]!!.mirror = true
        setRotation(leg6[0]!!, 0f, 0.3648668f, -0.7330383f)
        leg6[1] = ModelRendererGC(this, 28, 0)
        leg6[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg6[1]!!.setRotationPoint(10f, 13.2f, -2.2f)
        leg6[1]!!.setTextureSize(64, 64)
        leg6[1]!!.mirror = true
        setRotation(leg6[1]!!, 0f, 0.3648611f, 0.3823201f)
        leg6[2] = ModelRendererGC(this, 28, 0)
        leg6[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg6[2]!!.setRotationPoint(16.53333f, 16.2f, -4.7f)
        leg6[2]!!.setTextureSize(64, 64)
        leg6[2]!!.mirror = true
        setRotation(leg6[2]!!, 0f, 0.3648611f, 1.461656f)

        leg7[0] = ModelRendererGC(this, 28, 0)
        leg7[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg7[0]!!.setRotationPoint(-3f, 20f, -2f)
        leg7[0]!!.setTextureSize(64, 64)
        leg7[0]!!.mirror = true
        setRotation(leg7[0]!!, 0f, 2.495821f, -0.7330383f)
        leg7[1] = ModelRendererGC(this, 28, 0)
        leg7[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg7[1]!!.setRotationPoint(-8.8f, 13.33333f, -6.466667f)
        leg7[1]!!.setTextureSize(64, 64)
        leg7[1]!!.mirror = true
        setRotation(leg7[1]!!, 0f, 2.495821f, 0.4206553f)
        leg7[2] = ModelRendererGC(this, 28, 0)
        leg7[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg7[2]!!.setRotationPoint(-14.5f, 16.66667f, -10.9f)
        leg7[2]!!.setTextureSize(64, 64)
        leg7[2]!!.mirror = true
        setRotation(leg7[2]!!, 0f, 2.495821f, 1.201406f)

        leg8[0] = ModelRendererGC(this, 28, 0)
        leg8[0]!!.addBox(0f, -1f, -1f, 10, 2, 2)
        leg8[0]!!.setRotationPoint(3f, 20f, -2f)
        leg8[0]!!.setTextureSize(64, 64)
        leg8[0]!!.mirror = true
        setRotation(leg8[0]!!, 0f, 0.6615727f, -0.7330383f)
        leg8[1] = ModelRendererGC(this, 28, 0)
        leg8[1]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg8[1]!!.setRotationPoint(8.8f, 13.33333f, -6.466667f)
        leg8[1]!!.setTextureSize(64, 64)
        leg8[1]!!.mirror = true
        setRotation(leg8[1]!!, 0f, 0.6368846f, 0.4206553f)
        leg8[2] = ModelRendererGC(this, 28, 0)
        leg8[2]!!.addBox(0f, -1f, -1f, 8, 2, 2)
        leg8[2]!!.setRotationPoint(14.53333f, 16.66667f, -10.9f)
        leg8[2]!!.setTextureSize(64, 64)
        leg8[2]!!.mirror = true
        setRotation(leg8[2]!!, 0f, 0.6615671f, 1.201406f)

        rearEnd = ModelRendererGC(this, 0, 41)
        rearEnd.addBox(-6f, -7f, -6f, 12, 11, 12)
        rearEnd.setRotationPoint(0f, 20f, 13f)
        rearEnd.setTextureSize(64, 64)
        rearEnd.mirror = true
        setRotation(rearEnd, 0f, 0f, 0f)
        rearBack = ModelRendererGC(this, 26, 31)
        rearBack.addBox(-5f, -6f, 6f, 10, 9, 1)
        rearBack.setRotationPoint(0f, 20f, 13f)
        rearBack.setTextureSize(64, 64)
        rearBack.mirror = true
        setRotation(rearBack, 0f, 0f, 0f)
        rearLeft = ModelRendererGC(this, 0, 22)
        rearLeft.addBox(6f, -6f, -5f, 1, 9, 10)
        rearLeft.setRotationPoint(0f, 20f, 13f)
        rearLeft.setTextureSize(64, 64)
        rearLeft.mirror = true
        setRotation(rearLeft, 0f, 0f, 0f)
        rearRight = ModelRendererGC(this, 0, 22)
        rearRight.addBox(-7f, -6f, -5f, 1, 9, 10)
        rearRight.setRotationPoint(0f, 20f, 13f)
        rearRight.setTextureSize(64, 64)
        rearRight.mirror = true
        setRotation(rearRight, 0f, 0f, 0f)

        for (i: Int in 0..1) {
            this.convertToChild(this.leg1[i], this.leg1[i + 1])
            this.convertToChild(this.leg2[i], this.leg2[i + 1])
            this.convertToChild(this.leg3[i], this.leg3[i + 1])
            this.convertToChild(this.leg4[i], this.leg4[i + 1])
            this.convertToChild(this.leg5[i], this.leg5[i + 1])
            this.convertToChild(this.leg6[i], this.leg6[i + 1])
            this.convertToChild(this.leg7[i], this.leg7[i + 1])
            this.convertToChild(this.leg8[i], this.leg8[i + 1])
        }

        this.legLength0 = this.leg1[0]!!.cubeList.get(0).posX2 - this.leg1[0]!!.cubeList.get(0).posX1
    }

    private fun convertToChild(parent: ModelRendererGC?, child: ModelRendererGC?) {
//        // move child rotation point to be relative to parent
//        child.rotationPointX -= parent.rotationPointX;
//        child.rotationPointY -= parent.rotationPointY;
//        child.rotationPointZ -= parent.rotationPointZ;
//        // make rotations relative to parent
//        child.rotateAngleX -= parent.rotateAngleX;
//        child.rotateAngleY -= parent.rotateAngleY;
//        child.rotateAngleZ -= parent.rotateAngleZ;
//        // create relationship
//        parent.addChild(child);
    }

    private fun setRotation(model: ModelRendererGC, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        head.render(f5)
        body.render(f5)
        for (i: Int in 0..2) {
            this.leg1[i]!!.render(f5)
            this.leg2[i]!!.render(f5)
            this.leg3[i]!!.render(f5)
            this.leg4[i]!!.render(f5)
            this.leg5[i]!!.render(f5)
            this.leg6[i]!!.render(f5)
            this.leg7[i]!!.render(f5)
            this.leg8[i]!!.render(f5)
        }
        rearEnd.render(f5)
        rearBack.render(f5)
        rearLeft.render(f5)
        rearRight.render(f5)
    }

    private fun copyLegAngles(length: Float, parent: ModelRendererGC?, child: ModelRendererGC?) {
        child!!.rotationPointX =
            parent!!.rotationPointX + length * (MathHelper.cos(parent.rotateAngleZ) * MathHelper.cos(parent.rotateAngleY))
        child.rotationPointY = parent.rotationPointY + length * (MathHelper.sin(parent.rotateAngleZ))
        child.rotationPointZ =
            parent.rotationPointZ + (length) * (-MathHelper.sin(parent.rotateAngleY) * MathHelper.cos(parent.rotateAngleZ))
    }

    private fun copyLeftToRight(left: ModelRendererGC, right: ModelRendererGC) {
        right.rotateAngleX = left.rotateAngleX
        right.rotateAngleY = -left.rotateAngleY
        right.rotateAngleZ = left.rotateAngleZ
    }

    private fun copyLeg1LeftToRight(left: ModelRendererGC?, right: ModelRendererGC?) {
        right!!.rotateAngleX = left!!.rotateAngleX
        right.rotateAngleY = (Math.PI - left.rotateAngleY).toFloat()
        right.rotateAngleZ = left.rotateAngleZ
    }

    override fun setRotationAngles(f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, entityIn: Entity) {
        var movement: Float = f1
        val increment: Float = -1.0f
        var offset: Float = -0.4903446f
        this.leg2[0]!!.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.leg2[0]!!.rotateAngleZ = -0.7330383f
        offset = -0.1936386f
        movement += increment
        this.leg4[0]!!.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.leg4[0]!!.rotateAngleZ = -0.7330383f
        offset = 0.3648668f
        movement += increment
        this.leg6[0]!!.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.leg6[0]!!.rotateAngleZ = -0.7330383f
        offset = 0.6615727f
        movement += increment
        this.leg8[0]!!.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.leg8[0]!!.rotateAngleZ = -0.7330383f

        val updist: Float = -0.5f

        // Move legs up if they are being moved forward. dx/dy of cos(movement) is -sin(movement)
        movement = f1
        if (-MathHelper.sin(movement) * 0.2 > 0.0f) {
            this.leg2[0]!!.rotateAngleZ = -0.7330383f + -MathHelper.sin(movement) * updist
        }
        movement += increment
        if (-MathHelper.sin(movement) * 0.2 > 0.0f) {
            this.leg4[0]!!.rotateAngleZ = -0.7330383f + -MathHelper.sin(movement) * updist
        }
        movement += increment
        if (-MathHelper.sin(movement) * 0.2 > 0.0f) {
            this.leg6[0]!!.rotateAngleZ = -0.7330383f + -MathHelper.sin(movement) * updist
        }
        movement += increment
        if (-MathHelper.sin(movement) * 0.2 > 0.0f) {
            this.leg8[0]!!.rotateAngleZ = -0.7330383f + -MathHelper.sin(movement) * updist
        }

        for (i: Int in 1..2) {
            this.leg1[i]!!.rotateAngleY = this.leg1[0]!!.rotateAngleY
            this.leg2[i]!!.rotateAngleY = this.leg2[0]!!.rotateAngleY
            this.leg3[i]!!.rotateAngleY = this.leg3[0]!!.rotateAngleY
            this.leg4[i]!!.rotateAngleY = this.leg4[0]!!.rotateAngleY
            this.leg5[i]!!.rotateAngleY = this.leg5[0]!!.rotateAngleY
            this.leg6[i]!!.rotateAngleY = this.leg6[0]!!.rotateAngleY
            this.leg7[i]!!.rotateAngleY = this.leg7[0]!!.rotateAngleY
            this.leg8[i]!!.rotateAngleY = this.leg8[0]!!.rotateAngleY

            this.leg1[i]!!.rotateAngleX = this.leg1[0]!!.rotateAngleX
            this.leg2[i]!!.rotateAngleX = this.leg2[0]!!.rotateAngleX
            this.leg3[i]!!.rotateAngleX = this.leg3[0]!!.rotateAngleX
            this.leg4[i]!!.rotateAngleX = this.leg4[0]!!.rotateAngleX
            this.leg5[i]!!.rotateAngleX = this.leg5[0]!!.rotateAngleX
            this.leg6[i]!!.rotateAngleX = this.leg6[0]!!.rotateAngleX
            this.leg7[i]!!.rotateAngleX = this.leg7[0]!!.rotateAngleX
            this.leg8[i]!!.rotateAngleX = this.leg8[0]!!.rotateAngleX

            //            this.leg1[i].rotateAngleZ = this.leg1[0].rotateAngleZ;
//            this.leg2[i].rotateAngleZ = this.leg2[0].rotateAngleZ;
//            this.leg3[i].rotateAngleZ = this.leg3[0].rotateAngleZ;
//            this.leg4[i].rotateAngleZ = this.leg4[0].rotateAngleZ;
//            this.leg5[i].rotateAngleZ = this.leg5[0].rotateAngleZ;
//            this.leg6[i].rotateAngleZ = this.leg6[0].rotateAngleZ;
//            this.leg7[i].rotateAngleZ = this.leg7[0].rotateAngleZ;
//            this.leg8[i].rotateAngleZ = this.leg8[0].rotateAngleZ;
        }

        for (i: Int in 0..0) {
            this.copyLeg1LeftToRight(this.leg2[i], this.leg1[i])
            this.copyLeg1LeftToRight(this.leg4[i], this.leg3[i])
            this.copyLeg1LeftToRight(this.leg6[i], this.leg5[i])
            this.copyLeg1LeftToRight(this.leg8[i], this.leg7[i])
        }
        for (i: Int in 1..1) {
            val length1a: Float = 10.0f
            val length1b: Float = 10.0f
            this.copyLegAngles(length1a, this.leg1[0], this.leg1[1])
            this.copyLegAngles(length1b, this.leg2[0], this.leg2[1])
            this.copyLegAngles(length1a, this.leg3[0], this.leg3[1])
            this.copyLegAngles(length1b, this.leg4[0], this.leg4[1])
            this.copyLegAngles(length1a, this.leg5[0], this.leg5[1])
            this.copyLegAngles(length1b, this.leg6[0], this.leg6[1])
            this.copyLegAngles(length1a, this.leg7[0], this.leg7[1])
            this.copyLegAngles(length1b, this.leg8[0], this.leg8[1])

            val length2a: Float = 8.0f
            val length2b: Float = 8.0f
            this.copyLegAngles(length2a, this.leg1[1], this.leg1[2])
            this.copyLegAngles(length2b, this.leg2[1], this.leg2[2])
            this.copyLegAngles(length2a, this.leg3[1], this.leg3[2])
            this.copyLegAngles(length2b, this.leg4[1], this.leg4[2])
            this.copyLegAngles(length2a, this.leg5[1], this.leg5[2])
            this.copyLegAngles(length2b, this.leg6[1], this.leg6[2])
            this.copyLegAngles(length2a, this.leg7[1], this.leg7[2])
            this.copyLegAngles(length2b, this.leg8[1], this.leg8[2])
        }
        //        EntityJuicer juicer = (EntityJuicer) entityIn;
//        super.setRotationAngles(f1, f2, f3, f4, f5, f6, entityIn);
//        float movement = f1;
//        float increment = -1.0F;
//        float offset = 0.5F;
//        this.legLeftFront1.rotateAngleY = MathHelper.cos(movement) * 0.2F + offset;
//        this.legLeftFront1.rotateAngleZ = (float) (-Math.PI / 3.0F);
//        this.legLeftFront2.rotateAngleY = MathHelper.cos(movement) * 0.5F + offset;
//        this.legLeftFront2.rotateAngleZ = (float) (2 * Math.PI / 3.0F);
//        offset = 0.05F;
//        movement += increment;
//        this.legLeftMidFront1.rotateAngleY = MathHelper.cos(movement) * 0.2F + offset;
//        this.legLeftMidFront1.rotateAngleZ = (float) (-Math.PI / 3.0F);
//        this.legLeftMidFront2.rotateAngleY = MathHelper.cos(movement) * 0.5F + offset;
//        this.legLeftMidFront2.rotateAngleZ = (float) (2 * Math.PI / 3.0F);
//        offset = -0.1F;
//        movement += increment;
//        this.legLeftMidBack1.rotateAngleY = MathHelper.cos(movement) * 0.2F + offset;
//        this.legLeftMidBack1.rotateAngleZ = (float) (-Math.PI / 3.0F);
//        this.legLeftMidBack2.rotateAngleY = MathHelper.cos(movement) * 0.5F + offset;
//        this.legLeftMidBack2.rotateAngleZ = (float) (2 * Math.PI / 3.0F);
//        offset = -0.5F;
//        movement += increment;
//        this.legLeftBack1.rotateAngleY = MathHelper.cos(movement) * 0.2F + offset;
//        this.legLeftBack1.rotateAngleZ = (float) (-Math.PI / 3.0F);
//        this.legLeftBack2.rotateAngleY = MathHelper.cos(movement) * 0.5F + offset;
//        this.legLeftBack2.rotateAngleZ = (float) (2 * Math.PI / 3.0F);
//
//        this.copyLegAngles(legLeftFront1, legLeftFront2);
//        this.copyLegAngles(legLeftMidFront1, legLeftMidFront2);
//        this.copyLegAngles(legLeftMidBack1, legLeftMidBack2);
//        this.copyLegAngles(legLeftBack1, legLeftBack2);
//        this.copyLegAngles(legRightFront1, legRightFront2);
//        this.copyLegAngles(legRightMidFront1, legRightMidFront2);
//        this.copyLegAngles(legRightMidBack1, legRightMidBack2);
//        this.copyLegAngles(legRightBack1, legRightBack2);
//
//        this.copyLeg1LeftToRight(legLeftFront1, legRightFront1);
//        this.copyLeg1LeftToRight(legLeftMidFront1, legRightMidFront1);
//        this.copyLeg1LeftToRight(legLeftMidBack1, legRightMidBack1);
//        this.copyLeg1LeftToRight(legLeftBack1, legRightBack1);
//        this.copyLeftToRight(legLeftFront2, legRightFront2);
//        this.copyLeftToRight(legLeftMidFront2, legRightMidFront2);
//        this.copyLeftToRight(legLeftMidBack2, legRightMidBack2);
//        this.copyLeftToRight(legLeftBack2, legRightBack2);
    }
}
