package micdoodle8.mods.galacticraft.planets.mars.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.world.entity.Entity

open class ModelTier2Rocket @JvmOverloads constructor(var1: Float = 0.0f) : ModelBase() {
    // Roof, Floor, Bottom
    @JvmField
    var inside: Array<ModelRenderer> = (arrayOfNulls<ModelRenderer>(3) as Array<ModelRenderer>)

    // 4 sets of 5 fin parts
    @JvmField
    var fins: Array<Array<ModelRenderer?>?> = Array<Array<ModelRenderer?>?>(4) { arrayOfNulls<ModelRenderer>(5) }

    // 8 parts to the top of the rocket, last is the long center piece
    @JvmField
    var top: Array<ModelRenderer> = (arrayOfNulls<ModelRenderer>(8) as Array<ModelRenderer>)

    // 4 sets of 3 booster parts (attachment, main, top)
    @JvmField
    var boosters: Array<Array<ModelRenderer?>?> = Array<Array<ModelRenderer?>?>(4) { arrayOfNulls<ModelRenderer>(3) }

    // 3 booster base parts, starting from bottom
    @JvmField
    var base: Array<ModelRenderer> = (arrayOfNulls<ModelRenderer>(3) as Array<ModelRenderer>)

    // 7 side pars, first four are the front, then right, and so on
    @JvmField
    var sides: Array<ModelRenderer> = (arrayOfNulls<ModelRenderer>(7) as Array<ModelRenderer>)

    init {
        this.textureWidth = 256
        this.textureHeight = 256

        val halfPI: Float = (Math.PI / 2.0).toFloat()
        val fullPI: Float = (Math.PI).toFloat()

        this.inside[0] = ModelRenderer(this, 0, 59)
        this.inside[0].addBox(-9f, -57f, -9f, 18, 1, 18, var1)
        this.inside[0].setRotationPoint(0f, 23f, 0f)
        this.inside[0].setTextureSize(256, 256)
        this.inside[0].mirror = true
        this.setStartingAngles(this.inside[0], 0f, 0f, 0f)
        this.inside[1] = ModelRenderer(this, 0, 78)
        this.inside[1].addBox(-8.5f, -16f, -8.5f, 17, 1, 17, var1)
        this.inside[1].setRotationPoint(0f, 23f, 0f)
        this.inside[1].setTextureSize(256, 256)
        this.inside[1].mirror = true
        this.setStartingAngles(this.inside[1], 0f, 0f, 0f)
        this.inside[2] = ModelRenderer(this, 0, 40)
        this.inside[2].addBox(-9f, -4f, -9f, 18, 1, 18, var1)
        this.inside[2].setRotationPoint(0f, 23f, 0f)
        this.inside[2].setTextureSize(256, 256)
        this.inside[2].mirror = true
        this.setStartingAngles(this.inside[2], 0f, 0f, 0f)

        this.fins[0]!![1] = ModelRenderer(this, 66, 0)
        this.fins[0]!![1]!!.addBox(-1f, -9f, -19.4f, 2, 8, 2, var1)
        this.fins[0]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[0]!![1]!!.setTextureSize(256, 256)
        this.fins[0]!![1]!!.mirror = true
        this.setStartingAngles(this.fins[0]!![1]!!, 0f, 0.7853982f, 0f)
        this.fins[0]!![2] = ModelRenderer(this, 66, 0)
        this.fins[0]!![2]!!.addBox(-1f, -12f, -17.4f, 2, 8, 2, var1)
        this.fins[0]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[0]!![2]!!.setTextureSize(256, 256)
        this.fins[0]!![2]!!.mirror = true
        this.setStartingAngles(this.fins[0]!![2]!!, 0f, 0.7853982f, 0f)
        this.fins[0]!![3] = ModelRenderer(this, 66, 0)
        this.fins[0]!![3]!!.addBox(-1f, -14f, -15.4f, 2, 8, 2, var1)
        this.fins[0]!![3]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[0]!![3]!!.setTextureSize(256, 256)
        this.fins[0]!![3]!!.mirror = true
        this.setStartingAngles(this.fins[0]!![3]!!, 0f, 0.7853982f, 0f)
        this.fins[0]!![4] = ModelRenderer(this, 66, 0)
        this.fins[0]!![4]!!.addBox(-1f, -15f, -13.5f, 2, 8, 2, var1)
        this.fins[0]!![4]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[0]!![4]!!.setTextureSize(256, 256)
        this.fins[0]!![4]!!.mirror = true
        this.setStartingAngles(this.fins[0]!![4]!!, 0f, 0.7853982f, 0f)

        this.fins[0]!![0] = ModelRenderer(this, 60, 0)
        this.fins[0]!![0]!!.addBox(-1f, -14f, -20.4f, 2, 15, 1, var1)
        this.fins[0]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[0]!![0]!!.setTextureSize(256, 256)
        this.fins[0]!![0]!!.mirror = true
        this.setStartingAngles(this.fins[0]!![0]!!, 0f, 0.7853982f, 0f)

        this.fins[1]!![0] = ModelRenderer(this, 74, 0)
        this.fins[1]!![0]!!.addBox(-20.4f, -14f, -1f, 1, 15, 2, var1)
        this.fins[1]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[1]!![0]!!.setTextureSize(256, 256)
        this.fins[1]!![0]!!.mirror = true
        this.setStartingAngles(this.fins[1]!![0]!!, 0f, 0.7853982f, 0f)
        this.fins[1]!![1] = ModelRenderer(this, 66, 0)
        this.fins[1]!![1]!!.addBox(-19.4f, -9f, -1f, 2, 8, 2, var1)
        this.fins[1]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[1]!![1]!!.setTextureSize(256, 256)
        this.fins[1]!![1]!!.mirror = true
        this.setStartingAngles(this.fins[1]!![1]!!, 0f, 0.7853982f, 0f)
        this.fins[1]!![2] = ModelRenderer(this, 66, 0)
        this.fins[1]!![2]!!.addBox(-17.4f, -12f, -1f, 2, 8, 2, var1)
        this.fins[1]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[1]!![2]!!.setTextureSize(256, 256)
        this.fins[1]!![2]!!.mirror = true
        this.setStartingAngles(this.fins[1]!![2]!!, 0f, 0.7853982f, 0f)
        this.fins[1]!![3] = ModelRenderer(this, 66, 0)
        this.fins[1]!![3]!!.addBox(-15.4f, -14f, -1f, 2, 8, 2, var1)
        this.fins[1]!![3]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[1]!![3]!!.setTextureSize(256, 256)
        this.fins[1]!![3]!!.mirror = true
        this.setStartingAngles(this.fins[1]!![3]!!, 0f, 0.7853982f, 0f)
        this.fins[1]!![4] = ModelRenderer(this, 66, 0)
        this.fins[1]!![4]!!.addBox(-13.5f, -15f, -1f, 2, 8, 2, var1)
        this.fins[1]!![4]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[1]!![4]!!.setTextureSize(256, 256)
        this.fins[1]!![4]!!.mirror = true
        this.setStartingAngles(this.fins[1]!![4]!!, 0f, 0.7853982f, 0f)

        this.fins[2]!![0] = ModelRenderer(this, 60, 0)
        this.fins[2]!![0]!!.addBox(-1f, -14f, 19.5f, 2, 15, 1, var1)
        this.fins[2]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[2]!![0]!!.setTextureSize(256, 256)
        this.fins[2]!![0]!!.mirror = true
        this.setStartingAngles(this.fins[2]!![0]!!, 0f, 0.7853982f, 0f)
        this.fins[2]!![1] = ModelRenderer(this, 66, 0)
        this.fins[2]!![1]!!.addBox(-1f, -9f, 17.5f, 2, 8, 2, var1)
        this.fins[2]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[2]!![1]!!.setTextureSize(256, 256)
        this.fins[2]!![1]!!.mirror = true
        this.setStartingAngles(this.fins[2]!![1]!!, 0f, 0.7853982f, 0f)
        this.fins[2]!![2] = ModelRenderer(this, 66, 0)
        this.fins[2]!![2]!!.addBox(-1f, -12f, 15.5f, 2, 8, 2, var1)
        this.fins[2]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[2]!![2]!!.setTextureSize(256, 256)
        this.fins[2]!![2]!!.mirror = true
        this.setStartingAngles(this.fins[2]!![2]!!, 0f, 0.7853982f, 0f)
        this.fins[2]!![3] = ModelRenderer(this, 66, 0)
        this.fins[2]!![3]!!.addBox(-1f, -14f, 13.5f, 2, 8, 2, var1)
        this.fins[2]!![3]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[2]!![3]!!.setTextureSize(256, 256)
        this.fins[2]!![3]!!.mirror = true
        this.setStartingAngles(this.fins[2]!![3]!!, 0f, 0.7853982f, 0f)
        this.fins[2]!![4] = ModelRenderer(this, 66, 0)
        this.fins[2]!![4]!!.addBox(-1f, -15f, 11.6f, 2, 8, 2, var1)
        this.fins[2]!![4]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[2]!![4]!!.setTextureSize(256, 256)
        this.fins[2]!![4]!!.mirror = true
        this.setStartingAngles(this.fins[2]!![4]!!, 0f, 0.7853982f, 0f)

        this.fins[3]!![0] = ModelRenderer(this, 74, 0)
        this.fins[3]!![0]!!.addBox(19.5f, -14f, -1f, 1, 15, 2, var1)
        this.fins[3]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[3]!![0]!!.setTextureSize(256, 256)
        this.fins[3]!![0]!!.mirror = true
        this.setStartingAngles(this.fins[3]!![0]!!, 0f, 0.7853982f, 0f)
        this.fins[3]!![1] = ModelRenderer(this, 66, 0)
        this.fins[3]!![1]!!.addBox(17.5f, -9f, -1f, 2, 8, 2, var1)
        this.fins[3]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[3]!![1]!!.setTextureSize(256, 256)
        this.fins[3]!![1]!!.mirror = true
        this.setStartingAngles(this.fins[3]!![1]!!, 0f, 0.7853982f, 0f)
        this.fins[3]!![2] = ModelRenderer(this, 66, 0)
        this.fins[3]!![2]!!.addBox(15.5f, -12f, -1f, 2, 8, 2, var1)
        this.fins[3]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[3]!![2]!!.setTextureSize(256, 256)
        this.fins[3]!![2]!!.mirror = true
        this.setStartingAngles(this.fins[3]!![2]!!, 0f, 0.7853982f, 0f)
        this.fins[3]!![3] = ModelRenderer(this, 66, 0)
        this.fins[3]!![3]!!.addBox(13.5f, -14f, -1f, 2, 8, 2, var1)
        this.fins[3]!![3]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[3]!![3]!!.setTextureSize(256, 256)
        this.fins[3]!![3]!!.mirror = true
        this.setStartingAngles(this.fins[3]!![3]!!, 0f, 0.7853982f, 0f)
        this.fins[3]!![4] = ModelRenderer(this, 66, 0)
        this.fins[3]!![4]!!.addBox(11.6f, -15f, -1f, 2, 8, 2, var1)
        this.fins[3]!![4]!!.setRotationPoint(0f, 24f, 0f)
        this.fins[3]!![4]!!.setTextureSize(256, 256)
        this.fins[3]!![4]!!.mirror = true
        this.setStartingAngles(this.fins[3]!![4]!!, 0f, 0.7853982f, 0f)

        this.top[0] = ModelRenderer(this, 192, 60)
        this.top[0].addBox(-8f, -60f, -8f, 16, 2, 16, var1)
        this.top[0].setRotationPoint(0f, 24f, 0f)
        this.top[0].setTextureSize(256, 256)
        this.top[0].mirror = true
        this.setStartingAngles(this.top[0], 0f, 0f, 0f)
        this.top[1] = ModelRenderer(this, 200, 78)
        this.top[1].addBox(-7f, -62f, -7f, 14, 2, 14, var1)
        this.top[1].setRotationPoint(0f, 24f, 0f)
        this.top[1].setTextureSize(256, 256)
        this.top[1].mirror = true
        this.setStartingAngles(this.top[1], 0f, 0f, 0f)
        this.top[2] = ModelRenderer(this, 208, 94)
        this.top[2].addBox(-6f, -64f, -6f, 12, 2, 12, var1)
        this.top[2].setRotationPoint(0f, 24f, 0f)
        this.top[2].setTextureSize(256, 256)
        this.top[2].mirror = true
        this.setStartingAngles(this.top[2], 0f, 0f, 0f)
        this.top[3] = ModelRenderer(this, 216, 108)
        this.top[3].addBox(-5f, -66f, -5f, 10, 2, 10, var1)
        this.top[3].setRotationPoint(0f, 24f, 0f)
        this.top[3].setTextureSize(256, 256)
        this.top[3].mirror = true
        this.setStartingAngles(this.top[3], 0f, 0f, 0f)
        this.top[4] = ModelRenderer(this, 224, 120)
        this.top[4].addBox(-4f, -68f, -4f, 8, 2, 8, var1)
        this.top[4].setRotationPoint(0f, 24f, 0f)
        this.top[4].setTextureSize(256, 256)
        this.top[4].mirror = true
        this.setStartingAngles(this.top[4], 0f, 0f, 0f)
        this.top[5] = ModelRenderer(this, 232, 130)
        this.top[5].addBox(-3f, -70f, -3f, 6, 2, 6, var1)
        this.top[5].setRotationPoint(0f, 24f, 0f)
        this.top[5].setTextureSize(256, 256)
        this.top[5].mirror = true
        this.setStartingAngles(this.top[5], 0f, 0f, 0f)
        this.top[6] = ModelRenderer(this, 240, 138)
        this.top[6].addBox(-2f, -72f, -2f, 4, 2, 4, var1)
        this.top[6].setRotationPoint(0f, 24f, 0f)
        this.top[6].setTextureSize(256, 256)
        this.top[6].mirror = true
        this.setStartingAngles(this.top[6], 0f, 0f, 0f)
        this.top[7] = ModelRenderer(this, 248, 144)
        this.top[7].addBox(-1f, -88f, -1f, 2, 18, 2, var1)
        this.top[7].setRotationPoint(0f, 24f, 0f)
        this.top[7].setTextureSize(256, 256)
        this.top[7].mirror = true
        this.setStartingAngles(this.top[7], 0f, 0f, 0f)

        this.base[0] = ModelRenderer(this, 0, 0)
        this.base[0].addBox(-7f, -1f, -7f, 14, 1, 14, var1)
        this.base[0].setRotationPoint(0f, 24f, 0f)
        this.base[0].setTextureSize(256, 256)
        this.base[0].mirror = true
        this.setStartingAngles(this.base[0], 0f, 0f, 0f)
        this.base[1] = ModelRenderer(this, 0, 15)
        this.base[1].addBox(-6f, -2f, -6f, 12, 1, 12, var1)
        this.base[1].setRotationPoint(0f, 24f, 0f)
        this.base[1].setTextureSize(256, 256)
        this.base[1].mirror = true
        this.setStartingAngles(this.base[1], 0f, 0f, 0f)
        this.base[2] = ModelRenderer(this, 0, 28)
        this.base[2].addBox(-5f, -4f, -5f, 10, 2, 10, var1)
        this.base[2].setRotationPoint(0f, 24f, 0f)
        this.base[2].setTextureSize(256, 256)
        this.base[2].mirror = true
        this.setStartingAngles(this.base[2], 0f, 0f, 0f)

        this.sides[0] = ModelRenderer(this, 85, 0)
        this.sides[0].addBox(-3.9f, -58f, -8.9f, 8, 17, 1, var1)
        this.sides[0].setRotationPoint(0f, 24f, 0f)
        this.sides[0].setTextureSize(256, 256)
        this.sides[0].mirror = true
        this.setStartingAngles(this.sides[0], 0f, 0f, 0f)
        this.sides[1] = ModelRenderer(this, 103, 0)
        this.sides[1].addBox(3.9f, -58f, -8.9f, 5, 54, 1, var1)
        this.sides[1].setRotationPoint(0f, 24f, 0f)
        this.sides[1].setTextureSize(256, 256)
        this.sides[1].mirror = true
        this.setStartingAngles(this.sides[1], 0f, 0f, 0f)
        this.sides[2] = ModelRenderer(this, 85, 18)
        this.sides[2].addBox(-3.9f, -34f, -8.9f, 8, 30, 1, var1)
        this.sides[2].setRotationPoint(0f, 24f, 0f)
        this.sides[2].setTextureSize(256, 256)
        this.sides[2].mirror = true
        this.setStartingAngles(this.sides[2], 0f, 0f, 0f)
        this.sides[3] = ModelRenderer(this, 103, 55)
        this.sides[3].addBox(-8.9f, -58f, -8.9f, 5, 54, 1, var1)
        this.sides[3].setRotationPoint(0f, 24f, 0f)
        this.sides[3].setTextureSize(256, 256)
        this.sides[3].mirror = true
        this.setStartingAngles(this.sides[3], 0f, 0f, 0f)

        this.sides[4] = ModelRenderer(this, 120, 0)
        this.sides[4].addBox(-8.9f, -58f, -7.9f, 1, 54, 16, var1)
        this.sides[4].setRotationPoint(0f, 24f, 0f)
        this.sides[4].setTextureSize(256, 256)
        this.sides[4].mirror = true
        this.setStartingAngles(this.sides[4], 0f, 0f, 0f)
        this.sides[5] = ModelRenderer(this, 120, 141)
        this.sides[5].addBox(-8.9f, -58f, 8.1f, 17, 54, 1, var1)
        this.sides[5].setRotationPoint(0f, 24f, 0f)
        this.sides[5].setTextureSize(256, 256)
        this.sides[5].mirror = true
        this.setStartingAngles(this.sides[5], 0f, 0f, 0f)
        this.sides[6] = ModelRenderer(this, 119, 70)
        this.sides[6].addBox(8.1f, -58f, -7.9f, 1, 54, 17, var1)
        this.sides[6].setRotationPoint(0f, 24f, 0f)
        this.sides[6].setTextureSize(256, 256)
        this.sides[6].mirror = true
        this.setStartingAngles(this.sides[6], 0f, 0f, 0f)

        this.boosters[0]!![0] = ModelRenderer(this, 154, 19)
        this.boosters[0]!![0]!!.addBox(-10.9f, -10f, -0.5f, 3, 5, 1, var1)
        this.boosters[0]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[0]!![0]!!.setTextureSize(256, 256)
        this.boosters[0]!![0]!!.mirror = true
        this.setStartingAngles(this.boosters[0]!![0]!!, 0f, -halfPI, 0f)
        this.boosters[0]!![1] = ModelRenderer(this, 154, 6)
        this.boosters[0]!![1]!!.addBox(-14.9f, -11f, -2.5f, 5, 8, 5, var1)
        this.boosters[0]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[0]!![1]!!.setTextureSize(256, 256)
        this.boosters[0]!![1]!!.mirror = true
        this.setStartingAngles(this.boosters[0]!![1]!!, 0f, -halfPI, 0f)
        this.boosters[0]!![2] = ModelRenderer(this, 154, 0)
        this.boosters[0]!![2]!!.addBox(-14.4f, -13f, -2f, 4, 2, 4, var1)
        this.boosters[0]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[0]!![2]!!.setTextureSize(256, 256)
        this.boosters[0]!![2]!!.mirror = true
        this.setStartingAngles(this.boosters[0]!![2]!!, 0f, -halfPI, 0f)

        this.boosters[1]!![0] = ModelRenderer(this, 154, 19)
        this.boosters[1]!![0]!!.addBox(-10.9f, -10f, -0.5f, 3, 5, 1, var1)
        this.boosters[1]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[1]!![0]!!.setTextureSize(256, 256)
        this.boosters[1]!![0]!!.mirror = true
        this.setStartingAngles(this.boosters[1]!![0]!!, 0f, 0f, 0f)
        this.boosters[1]!![1] = ModelRenderer(this, 154, 6)
        this.boosters[1]!![1]!!.addBox(-14.9f, -11f, -2.5f, 5, 8, 5, var1)
        this.boosters[1]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[1]!![1]!!.setTextureSize(256, 256)
        this.boosters[1]!![1]!!.mirror = true
        this.setStartingAngles(this.boosters[1]!![1]!!, 0f, 0f, 0f)
        this.boosters[1]!![2] = ModelRenderer(this, 154, 0)
        this.boosters[1]!![2]!!.addBox(-14.4f, -13f, -2f, 4, 2, 4, var1)
        this.boosters[1]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[1]!![2]!!.setTextureSize(256, 256)
        this.boosters[1]!![2]!!.mirror = true
        this.setStartingAngles(this.boosters[1]!![2]!!, 0f, 0f, 0f)

        this.boosters[2]!![0] = ModelRenderer(this, 154, 19)
        this.boosters[2]!![0]!!.addBox(-10.9f, -10f, -0.5f, 3, 5, 1, var1)
        this.boosters[2]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[2]!![0]!!.setTextureSize(256, 256)
        this.boosters[2]!![0]!!.mirror = true
        this.setStartingAngles(this.boosters[2]!![0]!!, 0f, halfPI, 0f)
        this.boosters[2]!![1] = ModelRenderer(this, 154, 6)
        this.boosters[2]!![1]!!.addBox(-14.9f, -11f, -2.5f, 5, 8, 5, var1)
        this.boosters[2]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[2]!![1]!!.setTextureSize(256, 256)
        this.boosters[2]!![1]!!.mirror = true
        this.setStartingAngles(this.boosters[2]!![1]!!, 0f, halfPI, 0f)
        this.boosters[2]!![2] = ModelRenderer(this, 154, 0)
        this.boosters[2]!![2]!!.addBox(-14.4f, -13f, -2f, 4, 2, 4, var1)
        this.boosters[2]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[2]!![2]!!.setTextureSize(256, 256)
        this.boosters[2]!![2]!!.mirror = true
        this.setStartingAngles(this.boosters[2]!![2]!!, 0f, halfPI, 0f)

        this.boosters[3]!![0] = ModelRenderer(this, 154, 19)
        this.boosters[3]!![0]!!.addBox(-10.9f, -10f, -0.5f, 3, 5, 1, var1)
        this.boosters[3]!![0]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[3]!![0]!!.setTextureSize(256, 256)
        this.boosters[3]!![0]!!.mirror = true
        this.setStartingAngles(this.boosters[3]!![0]!!, 0f, fullPI, 0f)
        this.boosters[3]!![1] = ModelRenderer(this, 154, 6)
        this.boosters[3]!![1]!!.addBox(-14.9f, -11f, -2.5f, 5, 8, 5, var1)
        this.boosters[3]!![1]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[3]!![1]!!.setTextureSize(256, 256)
        this.boosters[3]!![1]!!.mirror = true
        this.setStartingAngles(this.boosters[3]!![1]!!, 0f, fullPI, 0f)
        this.boosters[3]!![2] = ModelRenderer(this, 154, 0)
        this.boosters[3]!![2]!!.addBox(-14.4f, -13f, -2f, 4, 2, 4, var1)
        this.boosters[3]!![2]!!.setRotationPoint(0f, 24f, 0f)
        this.boosters[3]!![2]!!.setTextureSize(256, 256)
        this.boosters[3]!![2]!!.mirror = true
        this.setStartingAngles(this.boosters[3]!![2]!!, 0f, fullPI, 0f)
    }

    override fun render(entity: Entity, par2: Float, par3: Float, par4: Float, par5: Float, par6: Float, par7: Float) {
        for (model: ModelRenderer in this.inside) {
            model.render(par7)
        }

        for (model: ModelRenderer in this.top) {
            model.render(par7)
        }

        for (model: ModelRenderer in this.base) {
            model.render(par7)
        }

        for (model: ModelRenderer in this.sides) {
            model.render(par7)
        }

        var var1: Int = 0
        var var2: Int = 0

        var1 = 0
        while (var1 < this.fins.size) {
            var2 = 0
            while (var2 < this.fins[var1]!!.size) {
                this.fins[var1]!![var2]!!.render(par7)
                var2++
            }
            var1++
        }

        var1 = 0
        while (var1 < this.boosters.size) {
            var2 = 0
            while (var2 < this.boosters[var1]!!.size) {
                this.boosters[var1]!![var2]!!.render(par7)
                var2++
            }
            var1++
        }
    }

    private fun setStartingAngles(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(
        par1: Float,
        par2: Float,
        par3: Float,
        par4: Float,
        par5: Float,
        par6: Float,
        entity: Entity
    ) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity)
    }
}
