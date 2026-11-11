package micdoodle8.mods.galacticraft.planets.venus.entities

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.IProjectile
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.effect.MobEffects
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.util.EnumParticleTypes
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class EntityWebShot : Entity, IProjectile {
    @JvmField
    var canBePickedUp: Int = 0

    @JvmField
    var arrowShake: Int = 0

    @JvmField
    var shootingEntity: Entity? = null
    private var ticksInAir = 0

    constructor(worldIn: World) : super(worldIn) {
        this.setSize(0.5f, 0.5f)
    }

    constructor(worldIn: World, x: Double, y: Double, z: Double) : super(worldIn) {
        this.setSize(0.5f, 0.5f)
        this.setPosition(x, y, z)
    }

    constructor(
        worldIn: World,
        shooter: EntityLivingBase,
        target: EntityLivingBase,
        p_i1755_4_: Float,
        p_i1755_5_: Float
    ) : super(worldIn) {
        this.shootingEntity = shooter

        if (shooter is EntityPlayer) {
            this.canBePickedUp = 1
        }

        this.posY = shooter.y + shooter.getEyeHeight().toDouble() - 0.10000000149011612
        val d0: Double = target.x - shooter.x
        val d1: Double = target.getBoundingBox().minY + (target.height / 3.0f).toDouble() - this.posY
        val d2: Double = target.z - shooter.z
        val d3: Double = MathHelper.sqrt(d0 * d0 + d2 * d2).toDouble()

        if (d3 >= 1.0E-7) {
            val f: Float = MathHelper.atan2(d2, d0).toFloat() * Constants.RADIANS_TO_DEGREES - 90.0f
            val f1: Float = MathHelper.atan2(d1, d3).toFloat() * -Constants.RADIANS_TO_DEGREES
            val d4: Double = d0 / d3
            val d5: Double = d2 / d3
            this.setLocationAndAngles(shooter.x + d4, this.posY, shooter.z + d5, f, f1)
            val f2: Float = (d3 * 0.20000000298023224).toFloat()
            this.shoot(d0, d1 + f2.toDouble(), d2, p_i1755_4_, p_i1755_5_)
        }
    }

    constructor(worldIn: World, shooter: EntityLivingBase, velocity: Float) : super(worldIn) {
        this.shootingEntity = shooter

        if (shooter is EntityPlayer) {
            this.canBePickedUp = 1
        }

        this.setSize(0.5f, 0.5f)
        this.setLocationAndAngles(
            shooter.x,
            shooter.y + shooter.getEyeHeight().toDouble(),
            shooter.z,
            shooter.yRot,
            shooter.xRot
        )
        this.posX -= (MathHelper.cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * 0.16f).toDouble()
        this.posY -= 0.10000000149011612
        this.posZ -= (MathHelper.sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * 0.16f).toDouble()
        this.setPosition(this.posX, this.posY, this.posZ)
        this.motionX =
            (-MathHelper.sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * MathHelper.cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.motionZ =
            (MathHelper.cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * MathHelper.cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.motionY = (-MathHelper.sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.shoot(this.motionX, this.motionY, this.motionZ, velocity * 1.5f, 1.0f)
    }

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d0: Double = this.getEntityBoundingBox().getAverageEdgeLength()

        if (java.lang.Double.isNaN(d0)) {
            d0 = 1.0
        }

        d0 = d0 * 64.0 * 10.0
        return distance < d0 * d0
    }

    override fun entityInit() {
    }

    override fun shoot(x: kotlin.Double, y: kotlin.Double, z: kotlin.Double, velocity: Float, inaccuracy: Float) {
        var x: kotlin.Double = x
        var y: kotlin.Double = y
        var z: kotlin.Double = z
        val f: Float = MathHelper.sqrt(x * x + y * y + z * z)
        x = x / f.toDouble()
        y = y / f.toDouble()
        z = z / f.toDouble()
        x =
            x + this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1).toDouble() * 0.007499999832361937 * inaccuracy.toDouble()
        y =
            y + this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1).toDouble() * 0.007499999832361937 * inaccuracy.toDouble()
        z =
            z + this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1).toDouble() * 0.007499999832361937 * inaccuracy.toDouble()
        x = x * velocity.toDouble()
        y = y * velocity.toDouble()
        z = z * velocity.toDouble()
        this.motionX = x
        this.motionY = y
        this.motionZ = z
        val f1: Float = MathHelper.sqrt(x * x + z * z)
        this.rotationYaw = MathHelper.atan2(x, z).toFloat() * Constants.RADIANS_TO_DEGREES
        this.prevRotationYaw = this.rotationYaw
        this.rotationPitch = MathHelper.atan2(y, f1.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
        this.prevRotationPitch = this.rotationPitch
    }

    @OnlyIn(Dist.CLIENT)
    override fun setPositionAndRotationDirect(
        x: kotlin.Double,
        y: kotlin.Double,
        z: kotlin.Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        p_180426_10_: Boolean
    ) {
        this.setPosition(x, y, z)
        this.setRotation(yaw, pitch)
    }

    @OnlyIn(Dist.CLIENT)
    override fun setVelocity(x: kotlin.Double, y: kotlin.Double, z: kotlin.Double) {
        this.motionX = x
        this.motionY = y
        this.motionZ = z

        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            val f: Float = MathHelper.sqrt(x * x + z * z)
            this.rotationYaw = MathHelper.atan2(x, z).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationYaw = this.rotationYaw
            this.rotationPitch = MathHelper.atan2(y, f.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationPitch = this.rotationPitch
            this.prevRotationPitch = this.rotationPitch
            this.prevRotationYaw = this.rotationYaw
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch)
        }
    }

    override fun onUpdate() {
        super.onUpdate()

        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            val f: Float = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
            this.rotationYaw = MathHelper.atan2(this.motionX, this.motionZ).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationYaw = this.rotationYaw
            this.rotationPitch = MathHelper.atan2(this.motionY, f.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationPitch = this.rotationPitch
        }

        if (this.arrowShake > 0) {
            --this.arrowShake
        }

        if (this.ticksExisted > 1000) {
            this.setDead()
        }

        ++this.ticksInAir
        var vec31: Vec3d = Vec3d(this.posX, this.posY, this.posZ)
        var vec3: Vec3d = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)
        var movingobjectposition: RayTraceResult? = this.world.rayTraceBlocks(vec31, vec3, false, true, false)
        vec31 = Vec3d(this.posX, this.posY, this.posZ)
        vec3 = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)

        if (movingobjectposition != null) {
            vec3 = Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z)
        }

        var entity: Entity? = null
        val list: MutableList<Entity?> = this.world.getEntitiesWithinAABBExcludingEntity(
            this,
            this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0, 1.0, 1.0)
        )
        var d0: kotlin.Double = 0.0
        val border: kotlin.Double = 0.3

        for (i: Int in list.indices) {
            val entity1: Entity = list.get(i) as Entity

            if (entity1.canBeCollidedWith() && (entity1 !== this.shootingEntity || this.ticksInAir >= 5)) {
                val axisalignedbb1: AxisAlignedBB = entity1.getBoundingBox().grow(border, border, border)
                val movingobjectposition1: RayTraceResult? = axisalignedbb1.calculateIntercept(vec31, vec3)

                if (movingobjectposition1 != null) {
                    val d1: kotlin.Double = vec31.squareDistanceTo(movingobjectposition1.hitVec)

                    if (d1 < d0 || d0 == 0.0) {
                        entity = entity1
                        d0 = d1
                    }
                }
            }
        }

        if (entity != null) {
            movingobjectposition = RayTraceResult(entity)
        }

        if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit is EntityPlayer) {
            val entityplayer: EntityPlayer = movingobjectposition.entityHit as EntityPlayer

            if (entityplayer.capabilities.disableDamage || this.shootingEntity is EntityPlayer && !(this.shootingEntity as EntityPlayer).canAttackPlayer(
                    entityplayer
                )
            ) {
                movingobjectposition = null
            }
        }

        if (movingobjectposition != null) {
            if (movingobjectposition.entityHit != null) {
                if (movingobjectposition.entityHit !== this.shootingEntity && !this.world.isRemote) {
                    if (movingobjectposition.entityHit is EntityLivingBase) {
                        (movingobjectposition.entityHit as EntityLivingBase).addEffect(
                            PotionEffect(
                                MobEffects.SLOWNESS,
                                180,
                                2,
                                true,
                                true
                            )
                        )
                        this.setDead()
                    } else {
                        this.motionX *= -0.10000000149011612
                        this.motionY *= -0.10000000149011612
                        this.motionZ *= -0.10000000149011612
                        this.rotationYaw += 180.0f
                        this.prevRotationYaw += 180.0f
                        this.ticksInAir = 0
                    }
                }
            } else {
                this.motionX = ((movingobjectposition.hitVec.x - this.posX).toFloat()).toDouble()
                this.motionY = ((movingobjectposition.hitVec.y - this.posY).toFloat()).toDouble()
                this.motionZ = ((movingobjectposition.hitVec.z - this.posZ).toFloat()).toDouble()
                val f5: Float =
                    MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)
                this.posX -= this.motionX / f5.toDouble() * 0.05000000074505806
                this.posY -= this.motionY / f5.toDouble() * 0.05000000074505806
                this.posZ -= this.motionZ / f5.toDouble() * 0.05000000074505806
                this.arrowShake = 7
                this.setDead()
            }
        }

        this.posX += this.motionX
        this.posY += this.motionY
        this.posZ += this.motionZ
        val f3: Float = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
        this.rotationYaw = MathHelper.atan2(this.motionX, this.motionZ).toFloat() * Constants.RADIANS_TO_DEGREES

        this.rotationPitch = MathHelper.atan2(this.motionY, f3.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f

        if (this.isInWater()) {
            for (i1: Int in 0..3) {
                val f8: Float = 0.25f
                this.world.spawnParticle(
                    EnumParticleTypes.WATER_BUBBLE,
                    this.posX - this.motionX * f8.toDouble(),
                    this.posY - this.motionY * f8.toDouble(),
                    this.posZ - this.motionZ * f8.toDouble(),
                    this.motionX,
                    this.motionY,
                    this.motionZ
                )
            }
        }

        if (this.isWet()) {
            this.extinguish()
        }

        this.setPosition(this.posX, this.posY, this.posZ)
        this.doBlockCollisions()
    }

    public override fun writeEntityToNBT(tagCompound: NBTTagCompound) {
        tagCompound.putByte("shake", this.arrowShake.toByte())
        tagCompound.putByte("pickup", this.canBePickedUp.toByte())
    }

    public override fun readEntityFromNBT(tagCompund: NBTTagCompound) {
        this.arrowShake = tagCompund.getByteOr("shake", 0).toInt() and 255

        if (tagCompund.containsTag("pickup", 99)) {
            this.canBePickedUp = tagCompund.getByteOr("pickup", 0).toInt()
        }
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun canBeAttackedWithItem(): Boolean {
        return false
    }

    override fun getEyeHeight(): Float {
        return 0.0f
    }
}
