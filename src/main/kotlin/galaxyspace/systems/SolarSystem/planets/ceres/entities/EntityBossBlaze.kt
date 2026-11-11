package galaxyspace.systems.SolarSystem.planets.ceres.entities

import net.minecraft.world.BossInfo.Color

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import galaxyspace.core.prefab.entities.EntityIceSpike
import galaxyspace.systems.SolarSystem.planets.ceres.entities.EntityBossBlaze
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityBossBase
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.IRangedAttackMob
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.ai.EntityAIHurtByTarget
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.ai.EntityAIWanderAvoidWater
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.ai.attributes.IAttributeInstance
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.sounds.SoundEvent
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.BossInfo
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.util.Random
import kotlin.math.min

open class EntityBossBlaze constructor(par1World: World?) : EntityBossBase((par1World)!!), IEntityBreathable,
    IRangedAttackMob {
    @JvmField
    protected var ticks: Long = 0

    @JvmField
    var targetEntity: Entity? = null

    @Suppress("INAPPLICABLE_JVM_FIELD", "VIRTUAL_MEMBER_HIDDEN")
    @JvmField
    var entitiesWithin: Int = 0

    @Suppress("INAPPLICABLE_JVM_FIELD", "VIRTUAL_MEMBER_HIDDEN")
    @JvmField
    var entitiesWithinLast: Int = 0

    private var roomCoords: Vector3? = null
    private var roomSize: Vector3? = null

    private var spawnedMinion = false

    init {
        this.setSize(2.0f, 5.0f)
        this.isImmuneToFire = true

        this.tasks.addTask(4, AIFireballAttack(this))
        this.tasks.addTask(5, EntityAIMoveTowardsRestriction(this, 1.0))
        this.tasks.addTask(7, EntityAIWanderAvoidWater(this, 1.0, 0.0f))
        this.tasks.addTask(8, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(8, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIHurtByTarget(this, true, *arrayOfNulls<Class<*>>(0)))
        this.targetTasks.addTask(2, EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, true))
    }

    override fun attackEntityFrom(ds: DamageSource, par2: Float): Boolean {
        if (this.getIsInvulnerable()) return false

        if (ds.getTrueSource() is EntityPlayer) {
            val player: EntityPlayer? = ds.getTrueSource() as EntityPlayer?
            if (player!!.getMainHandItem().isEmpty()) return false

            if (player.getMainHandItem().getItem() is ItemSword) return super.attackEntityFrom(ds, 3.0f)
        }

        if (par2 > 3.0f) {
            return super.attackEntityFrom(ds, 3.0f)
        }


        return super.attackEntityFrom(ds, 0.0f)
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(200.0f * ConfigManagerCore.dungeonBossHealthMod)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05)
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0)
    }

    constructor(world: World?, vec: Vector3) : this(world) {
        this.setPosition(vec.x, vec.y, vec.z)
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_BLAZE_AMBIENT
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_DEATH
    }

    override fun onDeathUpdate() {
        super.onDeathUpdate()

        if (!this.world.isRemote) {
            for (i: Int in 0..<min(2, this.ticksExisted)) {
                val entitysmallfireball: EntityIceSpike = EntityIceSpike(this.world, this)
                entitysmallfireball.posY = this.posY + (this.height / 2.0f).toDouble() + 1.5
                entitysmallfireball.shoot(
                    this,
                    this.rotationPitch + this.ticksExisted % 40,
                    this.rotationYawHead + rand.nextInt(360),
                    0.0f,
                    0.8f,
                    1.0f
                )
                this.world.spawnEntity(entitysmallfireball)
            }
            if (this.deathTicks == 100) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH,
                        GCCoreUtil.getDimensionID(this.world),
                        arrayOf<Any>(1.5f)
                    ),
                    NetworkRegistry.TargetPoint(
                        GCCoreUtil.getDimensionID(this.world),
                        this.posX,
                        this.posY,
                        this.posZ,
                        40.0
                    )
                )
            }
        }
    }

    override fun isInWater(): Boolean {
        return false
    }

    override fun handleWaterMovement(): Boolean {
        return false
    }

    override fun knockBack(par1Entity: Entity, par2: Float, par3: Double, par5: Double) {
    }

    override fun onLivingUpdate() {
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 1
        }

        this.ticks++

        if (this.getHealth() <= this.getMaxHealth() / 2.0 && !spawnedMinion) {
            this.spawnedMinion = true
            if (!world.isRemote) {
                for (i: Int in 0..3) {
                    val entity: Entity = EntityEvolvedColdBlaze(world)
                    entity.setPosition(
                        this.posX + this.world.rand.nextFloat(),
                        this.posY,
                        this.posZ + this.world.rand.nextFloat()
                    )
                    this.world.spawnEntity(entity)
                }
                this.setHealth(this.getHealth() + this.getMaxHealth() / 4.0f)
            }

            if (world.isRemote) {
                for (i: Int in 0..49) (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(this.posX, this.posY + rand.nextDouble(), this.posZ),
                    Vector3(
                        0.0 + (rand.nextDouble() / 5) * (if (rand.nextBoolean()) -1 else 1),
                        0.3,
                        0.0 + (rand.nextDouble() / 5) * (if (rand.nextBoolean()) -1 else 1)
                    ),
                    (arrayOf<Any>(50, 5, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
            }
        }

        val player: EntityPlayer? = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 20.0, false)

        if (player != null && player != this.targetEntity) {
            if (this.getDistanceSq(player) < 400.0) {
                this.getNavigator().getPathToEntityLiving(player)
                this.targetEntity = player
            }
        } else {
            this.targetEntity = null
        }

        if (this.world.isRemote) {
            for (i: Int in 0..4) {
                this.world.spawnParticle(
                    EnumParticleTypes.SNOW_SHOVEL,
                    this.posX + (this.rand.nextDouble() - 0.5) + (this.rand.nextDouble() * (if (this.rand.nextBoolean()) -1 else 1)) * this.width.toDouble(),
                    this.posY + this.rand.nextDouble() * this.height.toDouble(),
                    this.posZ + (this.rand.nextDouble() - 0.5) + (this.rand.nextDouble() * (if (this.rand.nextBoolean()) -1 else 1)) * this.width.toDouble(),
                    0.0,
                    0.0,
                    0.0
                )
            }
        }

        super.onLivingUpdate()
    }

    override fun dropFewItems(par1: Boolean, par2: Int) {
    }

    override fun entityDropItem(par1ItemStack: ItemStack, par2: Float): EntityItem? {
        val entityitem: EntityItem = EntityItem(this.world, this.posX, this.posY + par2, this.posZ, par1ItemStack)
        entityitem.motionY = -2.0
        entityitem.setDefaultPickupDelay()
        if (this.captureDrops) {
            this.capturedDrops.add(entityitem)
        } else {
            this.world.spawnEntity(entityitem)
        }
        return entityitem
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun getGuaranteedLoot(rand: Random?): ItemStack {
        val stackList: MutableList<ItemStack?> = GalacticraftRegistry.getDungeonLoot(4)
        return stackList.get((rand)!!.nextInt(stackList.size))!!.copy()
    }

    /*
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);

        if (this.roomCoords != null)
        {
            nbt.putDouble("roomCoordsX", this.roomCoords.x);
            nbt.putDouble("roomCoordsY", this.roomCoords.y);
            nbt.putDouble("roomCoordsZ", this.roomCoords.z);
            nbt.putDouble("roomSizeX", this.roomSize.x);
            nbt.putDouble("roomSizeY", this.roomSize.y);
            nbt.putDouble("roomSizeZ", this.roomSize.z);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.roomCoords = new Vector3();
        this.roomCoords.x = nbt.getDoubleOr("roomCoordsX", 0.0);
        this.roomCoords.y = nbt.getDoubleOr("roomCoordsY", 0.0);
        this.roomCoords.z = nbt.getDoubleOr("roomCoordsZ", 0.0);
        this.roomSize = new Vector3();
        this.roomSize.x = nbt.getDoubleOr("roomSizeX", 0.0);
        this.roomSize.y = nbt.getDoubleOr("roomSizeY", 0.0);
        this.roomSize.z = nbt.getDoubleOr("roomSizeZ", 0.0);
    }
    */
    open class AIFireballAttack constructor(private val blaze: EntityBossBlaze) : EntityAIBase() {
        private var attackStep = 0
        private var attackTime = 0

        init {
            this.setMutexBits(3)
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        override fun shouldExecute(): Boolean {
            val entitylivingbase: EntityLivingBase? = this.blaze.getAttackTarget()
            return entitylivingbase != null && entitylivingbase.isAlive()
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        override fun startExecuting() {
            this.attackStep = 0
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        /* public void resetTask()
        {
            this.blaze.setOnFire(false);
        }
*/
        /**
         * Keep ticking a continuous task that has already been started
         */
        override fun updateTask() {
            --this.attackTime
            val entitylivingbase: EntityLivingBase? = this.blaze.getAttackTarget()
            val d0: Double = this.blaze.getDistanceSq(entitylivingbase)

            if (d0 < 4.0) {
                if (this.attackTime <= 0) {
                    this.attackTime = 20
                    this.blaze.attackEntityAsMob(entitylivingbase)
                }

                this.blaze.getMoveHelper()
                    .setMoveTo(entitylivingbase!!.x, entitylivingbase.y, entitylivingbase.z, 1.0)
            } else if (d0 < this.getFollowDistance() * this.getFollowDistance()) {
                val d1: Double = entitylivingbase!!.x - this.blaze.posX
                val d2: Double =
                    entitylivingbase.getBoundingBox().minY + (entitylivingbase.height / 2.0f).toDouble() - (this.blaze.posY + (this.blaze.height / 2.0f).toDouble())
                val d3: Double = entitylivingbase.z - this.blaze.posZ

                if (this.attackTime <= 0) {
                    ++this.attackStep

                    if (this.attackStep == 1) {
                        this.attackTime = 40
                        //this.blaze.setOnFire(true);
                    } else if (this.attackStep <= 4) {
                        this.attackTime = 6
                    } else {
                        this.attackTime = 40
                        this.attackStep = 0
                        //this.blaze.setOnFire(false);
                    }

                    if (this.attackStep > 1) {
                        val f: Float = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5f
                        this.blaze.world.playEvent(
                            null as EntityPlayer?,
                            1018,
                            BlockPos(this.blaze.posX.toInt(), this.blaze.posY.toInt(), this.blaze.posZ.toInt()),
                            0
                        )

                        for (i: Int in 0..0) {
                            /*
                            EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.blaze.world, this.blaze, d1 + this.blaze.getRNG().nextGaussian() * (double)f, d2, d3 + this.blaze.getRNG().nextGaussian() * (double)f);
                            entitysmallfireball.posY = this.blaze.posY + (double)(this.blaze.height / 2.0F) + 0.5D;
                            this.blaze.world.spawnEntity(entitysmallfireball);
                            */
                            val entitysmallfireball: EntityIceSpike = EntityIceSpike(this.blaze.world, this.blaze)
                            entitysmallfireball.posY = this.blaze.posY + (this.blaze.height / 2.0f).toDouble() + 1.6
                            entitysmallfireball.shoot(
                                this.blaze,
                                this.blaze.rotationPitch,
                                this.blaze.rotationYawHead,
                                1.0f,
                                0.8f,
                                1.0f
                            )
                            this.blaze.world.spawnEntity(entitysmallfireball)
                        }
                    }
                }

                this.blaze.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0f, 60.0f)
            } else {
                this.blaze.getNavigator().clearPath()
                this.blaze.getMoveHelper()
                    .setMoveTo(entitylivingbase!!.x, entitylivingbase.y, entitylivingbase.z, 1.0)
            }

            super.updateTask()
        }

        private fun getFollowDistance(): Double {
            val iattributeinstance: IAttributeInstance =
                this.blaze.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)
            return if (iattributeinstance == null) 16.0 else iattributeinstance.getAttributeValue()
        }
    }

    override fun attackEntityWithRangedAttack(target: EntityLivingBase, distanceFactor: Float) {
    }

    override fun setSwingingArms(swingingArms: Boolean) {
    }

    override fun getChestTier(): Int {
        return 4
    }

    override fun dropKey() {
        this.entityDropItem(foundation.item.FoundationItemVariants.stack(GSItems.DUNGEON_KEYS, 1, 0), 0.5f)
    }

    override fun getHealthBarColor(): BossInfo.Color {
        return BossInfo.Color.BLUE
    }
}
