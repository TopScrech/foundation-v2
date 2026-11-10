package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.IProjectile
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.network.play.server.SPacketChangeGameState
import net.minecraft.util.DamageSource
import net.minecraft.util.EntityDamageSourceIndirect
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.atan2

open class EntityMeteorChunk : Entity, IProjectile {
    private var xTile = -1
    private var yTile = -1
    private var zTile = -1
    private var inTile: Block? = null
    private var inData = 0

    @JvmField
    var canBePickedUp: Int = 0

    @JvmField
    var shootingEntity: Entity? = null
    private var ticksInGround = 0
    private var ticksInAir = 0
    private var inGround = false
    private val randYawInc: Float
    private val randPitchInc: Float

    private val knockbackStrength = 0

    @JvmField
    var isHot: Boolean = false

    constructor(world: World) : super(world) {
        this.setSize(0.25f, 0.25f)
        this.randPitchInc = rand.nextFloat() * 20 - 10
        this.randYawInc = rand.nextFloat() * 20 - 10
    }

    constructor(par1World: World, par2EntityLivingBase: EntityLivingBase, speed: Float) : super(par1World) {
        this.randPitchInc = rand.nextFloat() * 20 - 10
        this.randYawInc = rand.nextFloat() * 20 - 10
        this.shootingEntity = par2EntityLivingBase

        if (par2EntityLivingBase is EntityPlayer) {
            this.canBePickedUp = 1
        }

        this.setSize(0.5f, 0.5f)
        this.setLocationAndAngles(
            par2EntityLivingBase.posX,
            par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight(),
            par2EntityLivingBase.posZ,
            par2EntityLivingBase.rotationYaw,
            par2EntityLivingBase.rotationPitch
        )
        this.posX -= (MathHelper.cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * 0.16f).toDouble()
        this.posY -= 0.10000000149011612
        this.posZ -= (MathHelper.sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * 0.16f).toDouble()
        this.setPosition(this.posX, this.posY, this.posZ)
        this.motionX =
            (-MathHelper.sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * MathHelper.cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.motionZ =
            (MathHelper.cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES) * MathHelper.cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES)).toDouble()
        this.motionY = -MathHelper.sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES).toDouble()
        this.shoot(this.motionX, this.motionY, this.motionZ, speed * 1.5f, 1.0f)
    }

    override fun shoot(headingX: Double, headingY: Double, headingZ: Double, speed: Float, randMod: Float) {
        var headingX: Double = headingX
        var headingY: Double = headingY
        var headingZ: Double = headingZ
        val f2: Float = MathHelper.sqrt(headingX * headingX + headingY * headingY + headingZ * headingZ)
        headingX /= f2.toDouble()
        headingY /= f2.toDouble()
        headingZ /= f2.toDouble()
        headingX += this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1) * 0.007499999832361937 * randMod
        headingY += this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1) * 0.007499999832361937 * randMod
        headingZ += this.rand.nextGaussian() * (if (this.rand.nextBoolean()) -1 else 1) * 0.007499999832361937 * randMod
        headingX *= speed.toDouble()
        headingY *= speed.toDouble()
        headingZ *= speed.toDouble()
        this.motionX = headingX
        this.motionY = headingY
        this.motionZ = headingZ
        val f3: Float = MathHelper.sqrt(headingX * headingX + headingZ * headingZ)
        this.rotationYaw = atan2(headingX, headingZ).toFloat() * Constants.RADIANS_TO_DEGREES
        this.prevRotationYaw = this.rotationYaw
        this.rotationPitch = atan2(headingY, f3.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
        this.prevRotationPitch = this.rotationPitch
        this.ticksInGround = 0
    }

    @SideOnly(Side.CLIENT)
    override fun setVelocity(par1: Double, par3: Double, par5: Double) {
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

        if (this.ticksExisted > 400) {
            if (this.isHot) {
                this.isHot = false
                this.setHot(this.isHot)
            }
        } else if (!this.world.isRemote) {
            this.setHot(this.isHot)
        }

        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            val f: Float = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
            this.rotationYaw = atan2(this.motionX, this.motionZ).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationYaw = this.rotationYaw
            this.rotationPitch = atan2(this.motionY, f.toDouble()).toFloat() * Constants.RADIANS_TO_DEGREES
            this.prevRotationPitch = this.rotationPitch
        }

        val pos: BlockPos = BlockPos(this.xTile, this.yTile, this.zTile)
        val stateIn: IBlockState = this.world.getBlockState(pos)

        if (!stateIn.getBlock().isAir(this.world.getBlockState(pos), this.world, pos)) {
            val axisalignedbb: AxisAlignedBB? = stateIn.getCollisionBoundingBox(this.world, pos)

            if (axisalignedbb != null && axisalignedbb.contains(Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true
            }
        }

        if (this.inGround) {
            val j: Block = this.world.getBlockState(pos).getBlock()
            val k: Int = j.getMetaFromState(this.world.getBlockState(pos))

            if (j === this.inTile && k == this.inData) {
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
        } else {
            ++this.ticksInAir
            var vec3: Vec3d = Vec3d(this.posX, this.posY, this.posZ)
            var vec31: Vec3d = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)
            var movingobjectposition: RayTraceResult? = this.world.rayTraceBlocks(vec3, vec31, false, true, false)
            vec3 = Vec3d(this.posX, this.posY, this.posZ)
            vec31 = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)

            if (movingobjectposition != null) {
                vec31 =
                    Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z)
            }

            this.rotationPitch += 1f

            var entity: Entity? = null
            val list: MutableList<Entity> = this.world.getEntitiesWithinAABBExcludingEntity(
                this,
                this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0, 1.0, 1.0)
            )
            var d0: Double = 0.0
            var l: Int
            val f1: Float
            val border: Double = 0.3

            l = 0
            while (l < list.size) {
                val entity1: Entity = list.get(l)

                if (entity1.canBeCollidedWith() && (entity1 !== this.shootingEntity || this.ticksInAir >= 5)) {
                    val axisalignedbb1: AxisAlignedBB = entity1.getEntityBoundingBox().grow(border, border, border)
                    val movingobjectposition1: RayTraceResult? = axisalignedbb1.calculateIntercept(vec3, vec31)

                    if (movingobjectposition1 != null) {
                        val d1: Double = vec3.distanceTo(movingobjectposition1.hitVec)

                        if (d1 < d0 || d0 == 0.0) {
                            entity = entity1
                            d0 = d1
                        }
                    }
                }
                ++l
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

            var f2: Float
            var f3: Float
            val damage: Double = if (ConfigManagerCore.hardMode) 3.2 else 1.6

            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit != null) {
                    f2 =
                        MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)
                    val i1: Int = MathHelper.ceil(f2 * damage)

                    var damagesource: DamageSource? = null

                    if (this.shootingEntity == null) {
                        damagesource = EntityDamageSourceIndirect("meteor_chunk", this, this).setProjectile()
                    } else {
                        damagesource =
                            EntityDamageSourceIndirect("meteor_chunk", this, this.shootingEntity).setProjectile()
                    }

                    if (this.isBurning() && movingobjectposition.entityHit !is EntityEnderman) {
                        movingobjectposition.entityHit.setFire(2)
                    }

                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, i1.toFloat())) {
                        if (movingobjectposition.entityHit is EntityLivingBase) {
                            val entitylivingbase: EntityLivingBase = movingobjectposition.entityHit as EntityLivingBase

                            if (!this.world.isRemote) {
                                entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1)
                            }

                            if (this.knockbackStrength > 0) {
                                f3 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)

                                if (f3 > 0.0f) {
                                    movingobjectposition.entityHit.addVelocity(
                                        this.motionX * this.knockbackStrength * 0.6000000238418579 / f3,
                                        0.1,
                                        this.motionZ * this.knockbackStrength * 0.6000000238418579 / f3
                                    )
                                }
                            }

                            if (this.shootingEntity != null) {
                                EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity)
                                EnchantmentHelper.applyArthropodEnchantments(
                                    this.shootingEntity as EntityLivingBase?,
                                    entitylivingbase
                                )
                            }

                            if (this.shootingEntity != null && movingobjectposition.entityHit !== this.shootingEntity && movingobjectposition.entityHit is EntityPlayer && this.shootingEntity is EntityPlayerMP) {
                                (this.shootingEntity as EntityPlayerMP).connection.sendPacket(
                                    SPacketChangeGameState(
                                        6,
                                        0.0f
                                    )
                                )
                            }
                        }

                        if (movingobjectposition.entityHit !is EntityEnderman) {
                            this.setDead()
                        }
                    } else {
                        this.motionX *= -0.10000000149011612
                        this.motionY *= -0.10000000149011612
                        this.motionZ *= -0.10000000149011612
                        this.rotationYaw += 180.0f
                        this.prevRotationYaw += 180.0f
                        this.ticksInAir = 0
                    }
                } else {
                    this.xTile = movingobjectposition.getBlockPos().getX()
                    this.yTile = movingobjectposition.getBlockPos().getY()
                    this.zTile = movingobjectposition.getBlockPos().getZ()
                    val state: IBlockState = this.world.getBlockState(movingobjectposition.getBlockPos())
                    this.inTile = state.getBlock()
                    this.inData = this.inTile!!.getMetaFromState(state)
                    this.motionX = (movingobjectposition.hitVec.x - this.posX).toFloat().toDouble()
                    this.motionY = (movingobjectposition.hitVec.y - this.posY).toFloat().toDouble()
                    this.motionZ = (movingobjectposition.hitVec.z - this.posZ).toFloat().toDouble()
                    f2 =
                        MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)
                    this.posX -= this.motionX / f2 * 0.05000000074505806
                    this.posY -= this.motionY / f2 * 0.05000000074505806
                    this.posZ -= this.motionZ / f2 * 0.05000000074505806
                    this.inGround = true

                    val hitState: IBlockState = this.world.getBlockState(movingobjectposition.getBlockPos())
                    if (!this.inTile!!.isAir(hitState, this.world, movingobjectposition.getBlockPos())) {
                        ((this.inTile)!!.onEntityCollision(
                            this.world,
                            movingobjectposition.getBlockPos(),
                            hitState,
                            this
                        ))!!
                    }
                }
            }

            this.posX += this.motionX
            this.posY += this.motionY
            this.posZ += this.motionZ
            f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)

            if (!this.onGround) {
                this.rotationPitch += this.randPitchInc
                this.rotationYaw += this.randYawInc
            }

            var f4: Float = 0.99f
            f1 = 0.05f

            if (this.isInWater()) {
                for (j1: Int in 0..3) {
                    f3 = 0.25f
                    this.world.spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE,
                        this.posX - this.motionX * f3,
                        this.posY - this.motionY * f3,
                        this.posZ - this.motionZ * f3,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                    )
                }
                this.isHot = false
                f4 = 0.8f
            }

            this.motionX *= f4.toDouble()
            this.motionY *= f4.toDouble()
            this.motionZ *= f4.toDouble()
            this.motionY -= TransformerHooks.getGravityForEntity(this)
            this.setPosition(this.posX, this.posY, this.posZ)
            this.doBlockCollisions()
        }
    }

    override fun entityInit() {
        this.dataManager.register<Boolean>(IS_HOT, false)
    }

    open fun isHot(): Boolean {
        return this.dataManager.get<Boolean>(IS_HOT)
    }

    open fun setHot(isHot: Boolean) {
        this.dataManager.set<Boolean>(IS_HOT, isHot)
    }

    override fun readEntityFromNBT(nbttagcompound: NBTTagCompound) {
    }

    override fun writeEntityToNBT(nbttagcompound: NBTTagCompound) {
    }

    override fun onCollideWithPlayer(par1EntityPlayer: EntityPlayer) {
        if (!this.world.isRemote && this.inGround) {
            var flag: Boolean =
                this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode

            if (this.canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(
                    ItemStack(
                        GCItems.meteorChunk,
                        1,
                        0
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

    override fun canBeAttackedWithItem(): Boolean {
        return false
    }

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d0: Double = this.getEntityBoundingBox().getAverageEdgeLength()

        if (java.lang.Double.isNaN(d0)) {
            d0 = 1.0
        }

        d0 = d0 * 64.0 * 10.0
        return distance < d0 * d0
    }

    companion object {
        private val IS_HOT: DataParameter<Boolean> =
            EntityDataManager.createKey<Boolean>(EntityMeteorChunk::class.java, DataSerializers.BOOLEAN)
    }
}
