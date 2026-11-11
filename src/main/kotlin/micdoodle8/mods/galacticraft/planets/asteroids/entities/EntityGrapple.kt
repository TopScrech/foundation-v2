package micdoodle8.mods.galacticraft.planets.asteroids.entities

import foundation.block.getMetaFromState

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.network.PacketSimpleAsteroids
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.entity.IProjectile
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.Items
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.EnumParticleTypes
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.atan2

open class EntityGrapple : Entity, IProjectile {
    private var hitVec: BlockPos? = null
    private var hitBlock: Block? = null
    private var inData = 0
    private var inGround = false

    @JvmField
    var canBePickedUp: Int = 0

    @JvmField
    var arrowShake: Int = 0

    @JvmField
    var shootingEntity: EntityPlayer? = null
    private var ticksInGround = 0
    private var ticksInAir = 0

    @JvmField
    var rotationRoll: Float = 0f

    @JvmField
    var prevRotationRoll: Float = 0f

    @JvmField
    var pullingPlayer: Boolean = false

    constructor(par1World: World) : super(par1World) {
        this.ignoreFrustumCheck = false
        //        this.yOffset = -1.5F;
        this.setSize(0.75f, 0.75f)
    }

    constructor(par1World: World, shootingEntity: EntityPlayer?, par3: Float) : super(par1World) {
        this.shootingEntity = shootingEntity
        this.setSize(0.75f, 0.75f)

        if (shootingEntity != null) {
            this.canBePickedUp = 1
            this.setLocationAndAngles(
                shootingEntity.x,
                shootingEntity.y + shootingEntity.getEyeHeight(),
                shootingEntity.z,
                shootingEntity.yRot,
                shootingEntity.xRot
            )
        }

        this.motionX =
            (-MathHelper.sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * MathHelper.cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.motionZ =
            (MathHelper.cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * MathHelper.cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.motionY = -MathHelper.sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES).toDouble()
        this.posX += this.motionX
        this.posY += this.motionY
        this.posZ += this.motionZ
        //        this.yOffset = -1.5F;
        this.setPosition(this.posX, this.posY, this.posZ)
        this.shoot(this.motionX, this.motionY, this.motionZ, par3 * 1.5f, 1.0f)
    }

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d0: Double = this.getEntityBoundingBox().getAverageEdgeLength()

        if (java.lang.Double.isNaN(d0)) {
            d0 = 1.0
        }

        d0 = d0 * 64.0 * 10.0
        return distance < d0 * d0
    }

    override fun getYOffset(): kotlin.Double {
        return -1.5
    }

    override fun entityInit() {
        this.dataManager.register<Int>(PULLING_ENTITY_ID, 0)
        this.dataManager.register<Boolean>(IS_PULLING, false)
    }

    override fun shoot(par1: kotlin.Double, par3: kotlin.Double, par5: kotlin.Double, par7: Float, par8: Float) {
        var par1: kotlin.Double = par1
        var par3: kotlin.Double = par3
        var par5: kotlin.Double = par5
        val f2: Float = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5)
        par1 /= f2.toDouble()
        par3 /= f2.toDouble()
        par5 /= f2.toDouble()
        par1 += this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1) * 0.007499999832361937 * par8
        par3 += this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1) * 0.007499999832361937 * par8
        par5 += this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1) * 0.007499999832361937 * par8
        par1 *= par7.toDouble()
        par3 *= par7.toDouble()
        par5 *= par7.toDouble()
        this.motionX = par1
        this.motionY = par3
        this.motionZ = par5
        val f3: Float = MathHelper.sqrt(par1 * par1 + par5 * par5)
        this.rotationYaw = atan2(par1, par5).toFloat() * Constants.RADIANS_TO_DEGREES
        this.prevRotationYaw = this.rotationYaw
        this.rotationPitch = atan2(par3, f3.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
        this.prevRotationPitch = this.rotationPitch
        this.ticksInGround = 0
    }

    @OnlyIn(Dist.CLIENT)
    override fun setPositionAndRotationDirect(
        x: kotlin.Double,
        y: kotlin.Double,
        z: kotlin.Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        b: Boolean
    ) {
        this.setPosition(x, y, z)
        this.setRotation(yaw, pitch)
    }

    override fun setPosition(x: kotlin.Double, y: kotlin.Double, z: kotlin.Double) {
        super.setPosition(x, y, z)
    }

    @OnlyIn(Dist.CLIENT)
    override fun setVelocity(par1: kotlin.Double, par3: kotlin.Double, par5: kotlin.Double) {
        this.motionX = par1
        this.motionY = par3
        this.motionZ = par5

        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            val f: Float = MathHelper.sqrt(par1 * par1 + par5 * par5)
            this.rotationYaw = atan2(par1, par5).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationYaw = this.rotationYaw
            this.rotationPitch = atan2(par3, f.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationPitch = this.rotationPitch
            this.prevRotationPitch = this.rotationPitch
            this.prevRotationYaw = this.rotationYaw
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch)
            this.ticksInGround = 0
        }
    }

    override fun onUpdate() {
        super.onUpdate()

        this.prevRotationRoll = this.rotationRoll

        if (!this.world.isRemote) {
            this.updateShootingEntity()

            if (this.getPullingEntity()) {
                val shootingEntity: EntityPlayer? = this.getShootingEntity()
                if (shootingEntity != null) {
                    val deltaPosition: kotlin.Double = this.getDistanceSq(shootingEntity)

                    val mot: Vector3 = Vector3(shootingEntity.deltaMovement.x, shootingEntity.deltaMovement.y, shootingEntity.deltaMovement.z)

                    if (mot.getMagnitudeSquared() < 0.01 && this.pullingPlayer) {
                        if (deltaPosition < 10) {
                            this.onCollideWithPlayer(shootingEntity)
                        }
                        this.updatePullingEntity(false)
                        this.setDead()
                    }

                    this.pullingPlayer = true
                }
            }
        } else {
            if (this.getPullingEntity()) {
                val shootingEntity: EntityPlayer? = this.getShootingEntity()
                if (shootingEntity != null) {
                    shootingEntity.setVelocity(
                        (this.posX - shootingEntity.x) / 12.0f,
                        (this.posY - shootingEntity.y) / 12.0f,
                        (this.posZ - shootingEntity.z) / 12.0f
                    )
                    if (shootingEntity.level().isRemote && shootingEntity.level().provider is IZeroGDimension) {
                        val stats: GCPlayerStatsClient? = GCPlayerStatsClient.get(shootingEntity)
                        if (stats != null) {
                            (stats.getFreefallHandler())!!.updateFreefall(shootingEntity)
                        }
                    }
                }
            }
        }

        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            val f: Float = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
            this.rotationYaw = atan2(this.motionX, this.motionZ).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationYaw = this.rotationYaw
            this.rotationPitch = atan2(this.motionY, f.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationPitch = this.rotationPitch
        }

        if (this.hitVec != null) {
            val state: IBlockState = this.world.getBlockState(this.hitVec)

            if (state.getMaterial() !== Material.AIR) {
                val axisalignedbb: AxisAlignedBB? =
                    state.getBlock().getCollisionBoundingBox(state, this.world, this.hitVec)

                if (axisalignedbb != null && axisalignedbb.contains(Vec3d(this.posX, this.posY, this.posZ))) {
                    this.inGround = true
                }
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake
        }

        if (this.inGround) {
            if (this.hitVec != null) {
                val state: IBlockState = this.world.getBlockState(this.hitVec)
                val block: Block = state.getBlock()
                val j: Int = block.getMetaFromState(state)

                if (block === this.hitBlock && j == this.inData) {
                    if (this.shootingEntity != null) {
                        this.shootingEntity!!.motionX = (this.posX - this.shootingEntity!!.x) / 16.0f
                        this.shootingEntity!!.motionY = (this.posY - this.shootingEntity!!.y) / 16.0f
                        this.shootingEntity!!.motionZ = (this.posZ - this.shootingEntity!!.z) / 16.0f
                        if (this.shootingEntity is EntityPlayerMP) (GalacticraftCore.handler)!!.preventFlyingKicks(this.shootingEntity as EntityPlayerMP)
                    }

                    if (!this.world.isRemote && this.ticksInGround < 5) {
                        this.updatePullingEntity(true)
                    }

                    ++this.ticksInGround

                    if (this.ticksInGround == 1200) {
                        this.setDead()
                    }
                } else {
                    this.inGround = false
                    this.motionX *= (this.rand.nextFloat() * 0.2f).toDouble()
                    this.motionY *= (this.rand.nextFloat() * 0.2f).toDouble()
                    this.motionZ *= (this.rand.nextFloat() * 0.2f).toDouble()
                    this.ticksInGround = 0
                    this.ticksInAir = 0
                }
            }
        } else {
            this.rotationRoll += 5f
            ++this.ticksInAir

            if (!this.world.isRemote) {
                this.updatePullingEntity(false)
            }

            if (this.shootingEntity != null && this.getDistanceSq(this.shootingEntity) >= 40 * 40) {
                this.setDead()
            }

            var vec31: Vec3d = Vec3d(this.posX, this.posY, this.posZ)
            var vec3: Vec3d = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)
            var movingobjectposition: RayTraceResult? = this.world.rayTraceBlocks(vec31, vec3, false, true, false)
            vec31 = Vec3d(this.posX, this.posY, this.posZ)
            vec3 = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)

            if (movingobjectposition != null) {
                vec3 =
                    Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z)
            }

            var entity: Entity? = null
            val list: MutableList<*> = this.world.getEntitiesWithinAABBExcludingEntity(
                this,
                this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0, 1.0, 1.0)
            )
            var d0: kotlin.Double = 0.0
            var i: Int
            val border: kotlin.Double = 0.3

            i = 0
            while (i < list.size) {
                val entity1: Entity = list.get(i) as Entity

                if (entity1.canBeCollidedWith() && (entity1 !== this.shootingEntity || this.ticksInAir >= 5)) {
                    val axisalignedbb1: AxisAlignedBB = entity1.getBoundingBox().grow(border, border, border)
                    val movingobjectposition1: RayTraceResult? = axisalignedbb1.calculateIntercept(vec31, vec3)

                    if (movingobjectposition1 != null) {
                        val d1: kotlin.Double = vec31.distanceTo(movingobjectposition1.hitVec)

                        if (d1 < d0 || d0 == 0.0) {
                            entity = entity1
                            d0 = d1
                        }
                    }
                }
                ++i
            }

            if (entity != null) {
                movingobjectposition = RayTraceResult(entity)
            }

            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit is EntityPlayer) {
                val entityplayer: EntityPlayer = movingobjectposition.entityHit as EntityPlayer

                if (entityplayer.capabilities.disableDamage || this.shootingEntity != null && !this.shootingEntity!!.canAttackPlayer(
                        entityplayer
                    )
                ) {
                    movingobjectposition = null
                }
            }

            var motion: Float

            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit == null) {
                    this.hitVec = movingobjectposition.getBlockPos()
                    val state: IBlockState = this.world.getBlockState(this.hitVec)
                    this.hitBlock = state.getBlock()
                    this.inData = state.getBlock().getMetaFromState(state)
                    this.motionX = (movingobjectposition.hitVec.x - this.posX).toFloat().toDouble()
                    this.motionY = (movingobjectposition.hitVec.y - this.posY).toFloat().toDouble()
                    this.motionZ = (movingobjectposition.hitVec.z - this.posZ).toFloat().toDouble()
                    motion =
                        MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)
                    this.posX -= this.motionX / motion * 0.05000000074505806
                    this.posY -= this.motionY / motion * 0.05000000074505806
                    this.posZ -= this.motionZ / motion * 0.05000000074505806
                    this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f))
                    this.inGround = true
                    this.arrowShake = 7

                    if (this.hitBlock!!.getMaterial(state) !== Material.AIR) {
                        ((this.hitBlock)!!.onEntityCollision(this.world, this.hitVec, state, this))!!
                    }
                }
            }

            this.posX += this.motionX
            this.posY += this.motionY
            this.posZ += this.motionZ
            motion = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
            this.rotationYaw = atan2(this.motionX, this.motionZ).toFloat() * Constants.RADIANS_TO_DEGREES
            this.rotationPitch = atan2(this.motionY, motion.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES

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
            var f3: Float = 0.99f
            val f1: Float = 0.05f

            if (this.isInWater()) {
                val f4: Float = 0.25f
                for (l: Int in 0..3) {
                    this.world.spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE,
                        this.posX - this.motionX * f4,
                        this.posY - this.motionY * f4,
                        this.posZ - this.motionZ * f4,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                    )
                }

                f3 = 0.8f
            }

            if (this.isWet()) {
                this.extinguish()
            }

            this.setPosition(this.posX, this.posY, this.posZ)
            this.doBlockCollisions()
        }

        if (!this.world.isRemote && (this.ticksInGround - 1) % 10 == 0) {
            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                PacketSimpleAsteroids(
                    PacketSimpleAsteroids.EnumSimplePacketAsteroids.C_UPDATE_GRAPPLE_POS,
                    GCCoreUtil.getDimensionID(this.world),
                    arrayOf<Any>(this.getEntityId(), Vector3(this))
                ),
                NetworkRegistry.TargetPoint(
                    GCCoreUtil.getDimensionID(this.world),
                    this.posX,
                    this.posY,
                    this.posZ,
                    150.0
                )
            )
        }
    }

    public override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
        if (this.hitVec != null) {
            par1NBTTagCompound.putShort("xTile", this.hitVec!!.getX().toShort())
            par1NBTTagCompound.putShort("yTile", this.hitVec!!.getY().toShort())
            par1NBTTagCompound.putShort("zTile", this.hitVec!!.getZ().toShort())
        }
        par1NBTTagCompound.putShort("life", this.ticksInGround.toShort())
        par1NBTTagCompound.putByte("inTile", Block.getIdFromBlock(this.hitBlock).toByte())
        par1NBTTagCompound.putByte("inData", this.inData.toByte())
        par1NBTTagCompound.putByte("shake", this.arrowShake.toByte())
        par1NBTTagCompound.putByte("inGround", (if (this.inGround) 1 else 0).toByte())
        par1NBTTagCompound.putByte("pickup", this.canBePickedUp.toByte())
    }

    public override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
        if (par1NBTTagCompound.containsTag("xTile")) {
            this.hitVec = BlockPos(
                par1NBTTagCompound.getShortOr("xTile", 0).toInt(),
                par1NBTTagCompound.getShortOr("yTile", 0).toInt(),
                par1NBTTagCompound.getShortOr("zTile", 0).toInt()
            )
        }

        this.ticksInGround = par1NBTTagCompound.getShortOr("life", 0).toInt()
        this.hitBlock = Block.getBlockById(par1NBTTagCompound.getByteOr("inTile", 0).toInt() and 255)
        this.inData = par1NBTTagCompound.getByteOr("inData", 0).toInt() and 255
        this.arrowShake = par1NBTTagCompound.getByteOr("shake", 0).toInt() and 255
        this.inGround = par1NBTTagCompound.getByteOr("inGround", 0).toInt() == 1

        if (par1NBTTagCompound.containsTag("pickup", 99)) {
            this.canBePickedUp = par1NBTTagCompound.getByteOr("pickup", 0).toInt()
        } else if (par1NBTTagCompound.containsTag("player", 99)) {
            this.canBePickedUp = if (par1NBTTagCompound.getBooleanOr("player", false)) 1 else 0
        }
    }

    override fun onCollideWithPlayer(par1EntityPlayer: EntityPlayer) {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
            var flag: Boolean =
                this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode

            if (this.canBePickedUp == 1 && !par1EntityPlayer.inventory.add(
                    ItemStack(
                        Items.STRING,
                        1
                    )
                )
            ) {
                flag = false
            }

            if (flag) {
                this.playSound(
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    0.2f,
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f
                )
                par1EntityPlayer.onItemPickup(this, 1)
                this.setDead()
            }
        }
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }


    //    @Override
    //    @OnlyIn(Dist.CLIENT)
    //    public float getShadowSize()
    //    {
    //        return 0.0F;
    //    }
    override fun canBeAttackedWithItem(): Boolean {
        return false
    }

    private fun updateShootingEntity() {
        if (this.shootingEntity != null) {
            this.dataManager.set<Int>(PULLING_ENTITY_ID, this.shootingEntity!!.getId())
        }
    }

    open fun getShootingEntity(): EntityPlayer? {
        val entity: Entity? = this.world.getEntityByID(this.dataManager.get<Int>(PULLING_ENTITY_ID))

        if (entity is EntityPlayer) {
            return entity
        }

        return null
    }

    open fun updatePullingEntity(pulling: Boolean) {
        this.dataManager.set<Boolean>(IS_PULLING, pulling)
    }

    open fun getPullingEntity(): Boolean {
        return this.dataManager.get<Boolean>(IS_PULLING)
    }

    companion object {
        private val PULLING_ENTITY_ID: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityGrapple::class.java, DataSerializers.VARINT)
        private val IS_PULLING: DataParameter<Boolean> =
            EntityDataManager.createKey<Boolean>(EntityGrapple::class.java, DataSerializers.BOOLEAN)
    }
}
