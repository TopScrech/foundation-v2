package micdoodle8.mods.galacticraft.planets.venus.client.model

import micdoodle8.mods.galacticraft.planets.venus.entities.EntityJuicer
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.util.Mth as MathHelper

open class ModelJuicer constructor() : ModelBase() {
    private val body1: ModelRenderer
    private val body2: ModelRenderer
    private val head: ModelRenderer
    private val legRightFront1: ModelRenderer
    private val legRightFront2: ModelRenderer
    private val legLeftFront1: ModelRenderer
    private val legLeftFront2: ModelRenderer
    private val legRightMidFront1: ModelRenderer
    private val legRightMidFront2: ModelRenderer
    private val legLeftMidFront1: ModelRenderer
    private val legLeftMidFront2: ModelRenderer
    private val legRightMidBack1: ModelRenderer
    private val legRightMidBack2: ModelRenderer
    private val legLeftMidBack1: ModelRenderer
    private val legLeftMidBack2: ModelRenderer
    private val legRightBack1: ModelRenderer
    private val legRightBack2: ModelRenderer
    private val legLeftBack1: ModelRenderer
    private val legLeftBack2: ModelRenderer
    private val back: ModelRenderer
    private val tail0: ModelRenderer
    private val tail1: ModelRenderer
    private val stinger: ModelRenderer

    private val legLength0: Float

    init {
        textureWidth = 64
        textureHeight = 32

        body1 = ModelRenderer(this, 24, 22)
        body1.addBox(-3f, -1f, -4f, 6, 2, 8)
        body1.setRotationPoint(0f, 0f, 0f)
        body1.setTextureSize(64, 32)
        body1.mirror = true
        setRotation(body1, 0f, 0f, 0f)
        body2 = ModelRenderer(this, 0, 22)
        body2.addBox(-2.5f, -1.5f, -3.5f, 5, 3, 7)
        body2.setRotationPoint(0f, 0f, 0f)
        body2.setTextureSize(64, 32)
        body2.mirror = true
        setRotation(body2, 0f, 0f, 0f)

        head = ModelRenderer(this, 52, 0)
        head.addBox(-1.5f, -1.5f, -1.5f, 3, 3, 3)
        head.setRotationPoint(0f, -1f, -4f)
        head.setTextureSize(64, 32)
        head.mirror = true
        setRotation(head, -0.3717861f, 0f, 0f)

        legRightFront1 = ModelRenderer(this, 44, 0)
        legRightFront1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legRightFront1.setRotationPoint(-2.5f, 0f, -3f)
        legRightFront1.setTextureSize(64, 32)
        legRightFront1.mirror = true
        setRotation(legRightFront1, 0f, 2.453788f, 1.115358f)
        legRightFront2 = ModelRenderer(this, 30, 0)
        legRightFront2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legRightFront2.setRotationPoint(-3.5f, -3f, -4f)
        legRightFront2.setTextureSize(64, 32)
        legRightFront2.mirror = true
        setRotation(legRightFront2, 0f, 2.825574f, -0.8551081f)

        legLeftFront1 = ModelRenderer(this, 44, 0)
        legLeftFront1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legLeftFront1.setRotationPoint(2.5f, 0f, -3f)
        legLeftFront1.setTextureSize(64, 32)
        legLeftFront1.mirror = true
        setRotation(legLeftFront1, 0f, 0.7807508f, -1.115358f)
        legLeftFront2 = ModelRenderer(this, 30, 0)
        legLeftFront2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legLeftFront2.setRotationPoint(3.5f, -3f, -4f)
        legLeftFront2.setTextureSize(64, 32)
        legLeftFront2.mirror = true
        setRotation(legLeftFront2, 0f, 0.7063936f, 0.8551081f)

        legRightMidFront1 = ModelRenderer(this, 44, 0)
        legRightMidFront1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legRightMidFront1.setRotationPoint(-2.5f, 0f, -1f)
        legRightMidFront1.setTextureSize(64, 32)
        legRightMidFront1.mirror = true
        setRotation(legRightMidFront1, 0f, 2.93711f, 1.115358f)
        legRightMidFront2 = ModelRenderer(this, 30, 0)
        legRightMidFront2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legRightMidFront2.setRotationPoint(-3.5f, -3f, -1f)
        legRightMidFront2.setTextureSize(64, 32)
        legRightMidFront2.mirror = true
        setRotation(legRightMidFront2, 0f, 3.011467f, -0.8551081f)

        legLeftMidFront1 = ModelRenderer(this, 44, 0)
        legLeftMidFront1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legLeftMidFront1.setRotationPoint(2.5f, 0f, -1f)
        legLeftMidFront1.setTextureSize(64, 32)
        legLeftMidFront1.mirror = true
        setRotation(legLeftMidFront1, 0f, 0.1858931f, -1.115358f)
        legLeftMidFront2 = ModelRenderer(this, 30, 0)
        legLeftMidFront2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legLeftMidFront2.setRotationPoint(3.5f, -3f, -1f)
        legLeftMidFront2.setTextureSize(64, 32)
        legLeftMidFront2.mirror = true
        setRotation(legLeftMidFront2, 0f, 0.3346075f, 0.8551081f)

        legRightMidBack1 = ModelRenderer(this, 44, 0)
        legRightMidBack1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legRightMidBack1.setRotationPoint(-2.5f, 0f, 1f)
        legRightMidBack1.setTextureSize(64, 32)
        legRightMidBack1.mirror = true
        setRotation(legRightMidBack1, 0f, -3.030057f, 1.115358f)
        legRightMidBack2 = ModelRenderer(this, 30, 0)
        legRightMidBack2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legRightMidBack2.setRotationPoint(-3.5f, -3f, 1f)
        legRightMidBack2.setTextureSize(64, 32)
        legRightMidBack2.mirror = true
        setRotation(legRightMidBack2, 0f, -2.974289f, -0.8551081f)

        legLeftMidBack1 = ModelRenderer(this, 44, 0)
        legLeftMidBack1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legLeftMidBack1.setRotationPoint(2.5f, 0f, 1f)
        legLeftMidBack1.setTextureSize(64, 32)
        legLeftMidBack1.mirror = true
        setRotation(legLeftMidBack1, 0f, -0.0371786f, -1.115358f)
        legLeftMidBack2 = ModelRenderer(this, 30, 0)
        legLeftMidBack2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legLeftMidBack2.setRotationPoint(3.5f, -3f, 1f)
        legLeftMidBack2.setTextureSize(64, 32)
        legLeftMidBack2.mirror = true
        setRotation(legLeftMidBack2, 0f, -0.1487144f, 0.8551081f)

        legRightBack1 = ModelRenderer(this, 44, 0)
        legRightBack1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legRightBack1.setRotationPoint(-2.5f, 0f, 3f)
        legRightBack1.setTextureSize(64, 32)
        legRightBack1.mirror = true
        setRotation(legRightBack1, 0f, -2.658271f, 1.115358f)
        legRightBack2 = ModelRenderer(this, 30, 0)
        legRightBack2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legRightBack2.setRotationPoint(-3.5f, -3f, 4f)
        legRightBack2.setTextureSize(64, 32)
        legRightBack2.mirror = true
        setRotation(legRightBack2, 0f, -2.788396f, -0.8551081f)

        legLeftBack1 = ModelRenderer(this, 44, 0)
        legLeftBack1.addBox(0f, -0.5f, -0.5f, 3, 1, 1)
        legLeftBack1.setRotationPoint(2.5f, 0f, 3f)
        legLeftBack1.setTextureSize(64, 32)
        legLeftBack1.mirror = true
        setRotation(legLeftBack1, 0f, -0.3346075f, -1.115358f)
        legLeftBack2 = ModelRenderer(this, 30, 0)
        legLeftBack2.addBox(0f, -0.5f, -0.5f, 6, 1, 1)
        legLeftBack2.setRotationPoint(3.5f, -3f, 3f)
        legLeftBack2.setTextureSize(64, 32)
        legLeftBack2.mirror = true
        setRotation(legLeftBack2, 0f, -0.5205006f, 0.8551081f)

        back = ModelRenderer(this, 0, 16)
        back.addBox(-1.5f, -0.5f, -2.5f, 3, 1, 5)
        back.setRotationPoint(0f, -1.5f, 0f)
        back.setTextureSize(64, 32)
        back.mirror = true
        setRotation(back, 0f, 0f, 0f)

        tail0 = ModelRenderer(this, 42, 17)
        tail0.addBox(-0.5f, -0.5f, 0f, 1, 1, 4)
        tail0.setRotationPoint(0f, -2f, 0f)
        tail0.setTextureSize(64, 32)
        tail0.mirror = true
        setRotation(tail0, 0.5205006f, 0f, 0f)
        tail1 = ModelRenderer(this, 42, 12)
        tail1.addBox(-0.5f, 0f, 0f, 1, 1, 4)
        tail1.setRotationPoint(0f, -3f, 3f)
        tail1.setTextureSize(64, 32)
        tail1.mirror = true
        setRotation(tail1, 2.659407f, 0f, 0f)
        stinger = ModelRenderer(this, 48, 9)
        stinger.addBox(-0.5f, -1f, -0.5f, 1, 2, 1)
        stinger.setRotationPoint(0f, -5.2f, -1.133333f)
        stinger.setTextureSize(64, 32)
        stinger.mirror = true
        setRotation(stinger, 1.487144f, 0f, 0f)

        convertToChild(legLeftFront1, legLeftFront2)
        convertToChild(legLeftMidFront1, legLeftMidFront2)
        convertToChild(legLeftMidBack1, legLeftMidBack2)
        convertToChild(legLeftBack1, legLeftBack2)
        convertToChild(legRightFront1, legRightFront2)
        convertToChild(legRightMidFront1, legRightMidFront2)
        convertToChild(legRightMidBack1, legRightMidBack2)
        convertToChild(legRightBack1, legRightBack2)

        convertToChild(tail0, tail1)
        convertToChild(tail1, stinger)

        this.legLength0 = this.legLeftFront1.cubeList.get(0).posX2 - this.legLeftFront1.cubeList.get(0).posX1
    }

    private fun convertToChild(parent: ModelRenderer, child: ModelRenderer) {
        // move child rotation point to be relative to parent
        child.rotationPointX -= parent.rotationPointX
        child.rotationPointY -= parent.rotationPointY
        child.rotationPointZ -= parent.rotationPointZ
        // make rotations relative to parent
        child.rotateAngleX -= parent.rotateAngleX
        child.rotateAngleY -= parent.rotateAngleY
        child.rotateAngleZ -= parent.rotateAngleZ
        // create relationship
        parent.addChild(child)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        body1.render(f5)
        body2.render(f5)
        head.render(f5)
        legRightMidBack1.render(f5)
        legRightFront1.render(f5)
        legRightMidFront1.render(f5)
        legRightBack1.render(f5)
        legLeftBack1.render(f5)
        legLeftMidBack1.render(f5)
        legLeftMidFront1.render(f5)
        legLeftFront1.render(f5)
        back.render(f5)
        tail0.render(f5)
    }

    private fun copyLegAngles(parent: ModelRenderer, child: ModelRenderer) {
        child.rotationPointX =
            this.legLength0 * (if (parent.rotationPointX < 0.0f) -1.0f else 1.0f) * (MathHelper.sin(parent.rotateAngleX) + MathHelper.cos(
                parent.rotateAngleY
            ))
        child.rotationPointY = 0.0f
        child.rotationPointZ = 0.0f
    }

    private fun copyLeftToRight(left: ModelRenderer, right: ModelRenderer) {
        right.rotateAngleX = left.rotateAngleX
        right.rotateAngleY = -left.rotateAngleY
        right.rotateAngleZ = left.rotateAngleZ
    }

    private fun copyLeg1LeftToRight(left: ModelRenderer, right: ModelRenderer) {
        right.rotateAngleX = left.rotateAngleX
        right.rotateAngleY = (Math.PI - left.rotateAngleY).toFloat()
        right.rotateAngleZ = -left.rotateAngleZ
    }

    override fun setRotationAngles(f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, entityIn: Entity) {
        val juicer: EntityJuicer = entityIn as EntityJuicer
        super.setRotationAngles(f1, f2, f3, f4, f5, f6, entityIn)
        var movement: Float = f1
        val increment: Float = -1.0f
        var offset: Float = 0.5f
        this.legLeftFront1.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.legLeftFront1.rotateAngleZ = (-Math.PI / 3.0f).toFloat()
        this.legLeftFront2.rotateAngleY = MathHelper.cos(movement) * 0.5f + offset
        this.legLeftFront2.rotateAngleZ = (2 * Math.PI / 3.0f).toFloat()
        offset = 0.05f
        movement += increment
        this.legLeftMidFront1.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.legLeftMidFront1.rotateAngleZ = (-Math.PI / 3.0f).toFloat()
        this.legLeftMidFront2.rotateAngleY = MathHelper.cos(movement) * 0.5f + offset
        this.legLeftMidFront2.rotateAngleZ = (2 * Math.PI / 3.0f).toFloat()
        offset = -0.1f
        movement += increment
        this.legLeftMidBack1.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.legLeftMidBack1.rotateAngleZ = (-Math.PI / 3.0f).toFloat()
        this.legLeftMidBack2.rotateAngleY = MathHelper.cos(movement) * 0.5f + offset
        this.legLeftMidBack2.rotateAngleZ = (2 * Math.PI / 3.0f).toFloat()
        offset = -0.5f
        movement += increment
        this.legLeftBack1.rotateAngleY = MathHelper.cos(movement) * 0.2f + offset
        this.legLeftBack1.rotateAngleZ = (-Math.PI / 3.0f).toFloat()
        this.legLeftBack2.rotateAngleY = MathHelper.cos(movement) * 0.5f + offset
        this.legLeftBack2.rotateAngleZ = (2 * Math.PI / 3.0f).toFloat()

        this.copyLegAngles(legLeftFront1, legLeftFront2)
        this.copyLegAngles(legLeftMidFront1, legLeftMidFront2)
        this.copyLegAngles(legLeftMidBack1, legLeftMidBack2)
        this.copyLegAngles(legLeftBack1, legLeftBack2)
        this.copyLegAngles(legRightFront1, legRightFront2)
        this.copyLegAngles(legRightMidFront1, legRightMidFront2)
        this.copyLegAngles(legRightMidBack1, legRightMidBack2)
        this.copyLegAngles(legRightBack1, legRightBack2)

        this.copyLeg1LeftToRight(legLeftFront1, legRightFront1)
        this.copyLeg1LeftToRight(legLeftMidFront1, legRightMidFront1)
        this.copyLeg1LeftToRight(legLeftMidBack1, legRightMidBack1)
        this.copyLeg1LeftToRight(legLeftBack1, legRightBack1)
        this.copyLeftToRight(legLeftFront2, legRightFront2)
        this.copyLeftToRight(legLeftMidFront2, legRightMidFront2)
        this.copyLeftToRight(legLeftMidBack2, legRightMidBack2)
        this.copyLeftToRight(legLeftBack2, legRightBack2)

        this.tail0.rotationPointY = -1.5f
        this.tail0.offsetY = 0.0f
        //        this.tail0.rotateAngleX = MathHelper.cos(movement) * 0.2F + 0.5205006F;
        this.tail0.rotateAngleX = if (juicer.attackingPlayer != null) 0.52f else 0.1f
        this.tail0.rotateAngleY = 0.0f
        this.tail0.rotateAngleZ = 0.0f
        this.tail1.rotateAngleX = if (juicer.attackingPlayer != null) 2.659407f else 2.7f
        this.tail1.rotationPointZ =
            4 * (MathHelper.sin(this.tail0.rotateAngleZ) + MathHelper.cos(this.tail0.rotateAngleY))
        this.tail1.rotationPointY = 0.5f
        this.tail1.rotationPointX = 0.0f
        this.stinger.rotateAngleX = -this.tail0.rotateAngleX - (this.tail0.rotateAngleX - 2.659407f) - 0.5205006f
        this.stinger.rotationPointZ =
            4 * (MathHelper.sin(this.tail1.rotateAngleZ) + MathHelper.cos(this.tail1.rotateAngleY))
        this.stinger.rotationPointY = 0.5f
        this.stinger.rotationPointX = 0.0f
    }
}
