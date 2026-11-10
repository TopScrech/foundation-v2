package micdoodle8.mods.galacticraft.planets.venus.entities

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import micdoodle8.mods.galacticraft.planets.venus.entities.ai.EntityMoveHelperCeiling
import micdoodle8.mods.galacticraft.planets.venus.entities.ai.PathNavigateCeiling
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureAttribute
import net.minecraft.entity.MoverType
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIAttackMelee
import net.minecraft.entity.ai.EntityAIHurtByTarget
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.ai.EntityAIWander
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.pathfinding.PathNavigate
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class EntityJuicer constructor(world: World) : EntityMob(world), IEntityBreathable {
    private var jumpTarget: BlockPos? = null
    private var timeSinceLastJump = 0

    init {
        this.moveHelper = EntityMoveHelperCeiling(this)
        this.setSize(0.95f, 0.6f)
        this.tasks.addTask(4, EntityAIAttackMelee(this, 1.0, true))
        this.tasks.addTask(5, EntityAIWander(this, 0.8))
        this.tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(6, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIHurtByTarget(this, false))
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, true)
        )
        this.timeSinceLastJump = this.rand.nextInt(200) + 50
    }

    override fun entityInit() {
        super.entityInit()
        this.dataManager.register<Boolean>(IS_FALLING, false)
        this.dataManager.register<Boolean>(IS_HANGING, false)
    }

    open fun isHanging(): Boolean {
        return this.dataManager.get<Boolean>(IS_FALLING)
    }

    open fun setHanging(hanging: Boolean) {
        this.dataManager.set<Boolean>(IS_FALLING, hanging)
    }

    open fun isFalling(): Boolean {
        return this.dataManager.get<Boolean>(IS_FALLING)
    }

    open fun setFalling(falling: Boolean) {
        this.dataManager.set<Boolean>(IS_FALLING, falling)
    }

    override fun canBreatheUnderwater(): Boolean {
        return true
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896)
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_SPIDER_AMBIENT
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_SPIDER_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_SPIDER_DEATH
    }

    override fun playStepSound(pos: BlockPos, blockIn: Block) {
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15f, 1.0f)
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun getSoundPitch(): Float {
        return super.getSoundPitch() + 0.4f
    }

    override fun getDropItem(): Item? {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun onLivingUpdate() {
        if (this.isHanging()) {
            this.onGround = true
        }

        super.onLivingUpdate()

        if (!this.world.isRemote) {
            if (this.jumpTarget == null) {
                if (this.timeSinceLastJump <= 0) {
                    val posAbove: BlockPos =
                        BlockPos(this.posX, this.posY + (if (this.isHanging()) 1.0 else -0.5), this.posZ)
                    val blockAbove: IBlockState = this.world.getBlockState(posAbove)

                    if (blockAbove.getBlock() === VenusBlocks.venusBlock && (blockAbove.getValue(
                            BlockBasicVenus.Companion.BASIC_TYPE_VENUS
                        ) == BlockBasicVenus.EnumBlockBasicVenus.DUNGEON_BRICK_2 ||
                                blockAbove.getValue(BlockBasicVenus.Companion.BASIC_TYPE_VENUS) == BlockBasicVenus.EnumBlockBasicVenus.DUNGEON_BRICK_1)
                    ) {
                        val hit: RayTraceResult? = this.world.rayTraceBlocks(
                            Vec3d(this.posX, this.posY, this.posZ),
                            Vec3d(this.posX, this.posY + (if (this.isHanging()) -10 else 10), this.posZ),
                            false,
                            true,
                            false
                        )

                        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                            val blockBelow: IBlockState = this.world.getBlockState(hit.getBlockPos())
                            if (blockBelow.getBlock() === VenusBlocks.venusBlock && (blockBelow.getValue(
                                    BlockBasicVenus.Companion.BASIC_TYPE_VENUS
                                ) == BlockBasicVenus.EnumBlockBasicVenus.DUNGEON_BRICK_2 ||
                                        blockBelow.getValue(BlockBasicVenus.Companion.BASIC_TYPE_VENUS) == BlockBasicVenus.EnumBlockBasicVenus.DUNGEON_BRICK_1)
                            ) {
                                if (this.isHanging()) {
                                    this.jumpTarget = hit.getBlockPos()
                                    this.setFalling(this.jumpTarget != null)
                                } else {
                                    this.jumpTarget = hit.getBlockPos().offset(EnumFacing.DOWN)
                                    this.setFalling(this.jumpTarget != null)
                                }
                            }
                        }
                    }
                } else {
                    this.timeSinceLastJump--
                }
            }
        }

        if (this.isHanging()) {
            this.motionY = 0.0
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
        super.move(type, x, y, z)
        if (this.isHanging()) {
            this.onGround = true
        }
    }

    override fun onUpdate() {
        super.onUpdate()

        if (this.world.isRemote) {
            this.prevLimbSwingAmount = this.limbSwingAmount
            val d1: Double = this.posX - this.prevPosX
            val d0: Double = this.posZ - this.prevPosZ
            var f2: Float = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0f

            if (f2 > 1.0f) {
                f2 = 1.0f
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4f
            this.limbSwing += this.limbSwingAmount
        } else {
            if (this.jumpTarget != null) {
                val diffX: Double = this.jumpTarget!!.getX() - this.posX + 0.5
                val diffY: Double = this.jumpTarget!!.getY() - this.posY + 0.6
                val diffZ: Double = this.jumpTarget!!.getZ() - this.posZ + 0.5
                this.motionY = if (diffY > 0) min(diffY / 2.0f, 0.2123) else max(diffY / 2.0f, -0.2123)
                this.motionX = if (diffX > 0) min(diffX / 2.0f, 0.2123) else max(diffX / 2.0f, -0.2123)
                this.motionZ = if (diffZ > 0) min(diffZ / 2.0f, 0.2123) else max(diffZ / 2.0f, -0.2123)
                if (diffY > 0.0f && abs(this.jumpTarget!!.getY() - (this.posY + this.motionY)) < 0.4f) {
                    this.setPosition(
                        this.jumpTarget!!.getX() + 0.5,
                        this.jumpTarget!!.getY() + 0.2,
                        this.jumpTarget!!.getZ() + 0.5
                    )
                    this.jumpTarget = null
                    this.setFalling(false)
                    this.timeSinceLastJump = this.rand.nextInt(180) + 60
                    this.setHanging(true)
                } else if (diffY < 0.0f && abs(this.jumpTarget!!.getY() - (this.posY + this.motionY)) < 0.8f) {
                    this.setPosition(
                        this.jumpTarget!!.getX() + 0.5,
                        this.jumpTarget!!.getY() + 1.0,
                        this.jumpTarget!!.getZ() + 0.5
                    )
                    this.jumpTarget = null
                    this.setFalling(false)
                    this.timeSinceLastJump = this.rand.nextInt(180) + 60
                    this.setHanging(false)
                } else {
                    this.setHanging(false)
                }
            }
        }
    }

    override fun isValidLightLevel(): Boolean {
        return true
    }

    override fun getCanSpawnHere(): Boolean {
        if (super.getCanSpawnHere()) {
            val var1: EntityPlayer? = this.world.getClosestPlayerToEntity(this, 5.0)
            return var1 == null
        } else {
            return false
        }
    }

    override fun getCreatureAttribute(): EnumCreatureAttribute {
        return EnumCreatureAttribute.ARTHROPOD
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun createNavigator(worldIn: World): PathNavigate {
        return PathNavigateCeiling(this, worldIn)
    }

    override fun setInWeb() {
    }

    override fun writeEntityToNBT(tagCompound: NBTTagCompound) {
        super.writeEntityToNBT(tagCompound)

        tagCompound.setInteger("timeSinceLastJump", this.timeSinceLastJump)
        tagCompound.setBoolean("jumpTargetNull", this.jumpTarget == null)
        if (this.jumpTarget != null) {
            tagCompound.setInteger("jumpTargetX", this.jumpTarget!!.getX())
            tagCompound.setInteger("jumpTargetY", this.jumpTarget!!.getY())
            tagCompound.setInteger("jumpTargetZ", this.jumpTarget!!.getZ())
        }
    }

    override fun readEntityFromNBT(tagCompund: NBTTagCompound) {
        super.readEntityFromNBT(tagCompund)

        this.timeSinceLastJump = tagCompund.getInteger("timeSinceLastJump")
        if (tagCompund.getBoolean("jumpTargetNull")) {
            this.jumpTarget = null
        } else {
            this.jumpTarget = BlockPos(
                tagCompund.getInteger("jumpTargetX"),
                tagCompund.getInteger("jumpTargetY"),
                tagCompund.getInteger("jumpTargetZ")
            )
        }

        this.setFalling(this.jumpTarget != null)
    }

    companion object {
        private val IS_FALLING: DataParameter<Boolean> =
            EntityDataManager.createKey<Boolean>(EntityJuicer::class.java, DataSerializers.BOOLEAN)
        private val IS_HANGING: DataParameter<Boolean> =
            EntityDataManager.createKey<Boolean>(EntityJuicer::class.java, DataSerializers.BOOLEAN)
    }
}
