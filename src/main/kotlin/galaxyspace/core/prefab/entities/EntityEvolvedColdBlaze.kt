package galaxyspace.core.prefab.entities

import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import galaxyspace.core.prefab.entities.EntityIceSpike
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.ai.EntityAIHurtByTarget
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.ai.EntityAIWanderAvoidWater
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.ai.attributes.IAttributeInstance
import net.minecraft.entity.monster.EntityMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.sounds.SoundEvent
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World

open class EntityEvolvedColdBlaze constructor(world: World) : EntityMob(world), IEntityBreathable {
    private var heightOffset = 0.5f
    private var heightOffsetUpdateTime = 0

    init {
        this.setSize(0.9f, 1.3f)
        this.experienceValue = 10
    }

    override fun initEntityAI() {
        this.tasks.addTask(4, AIFireballAttack(this))
        this.tasks.addTask(5, EntityAIMoveTowardsRestriction(this, 1.0))
        this.tasks.addTask(7, EntityAIWanderAvoidWater(this, 1.0, 0.0f))
        this.tasks.addTask(8, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(8, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIHurtByTarget(this, true, *arrayOfNulls<Class<*>>(0)))
        this.targetTasks.addTask(2, EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, true))
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513)
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0)
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

    override fun onLivingUpdate() {
        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6
        }

        if (this.world.isRemote) {
            if (this.rand.nextInt(14) == 0 && !this.isSilent()) {
                this.playSound(SoundEvents.BLOCK_SNOW_STEP, 0.15f, 1.0f)
            }

            for (i: Int in 0..1) {
                this.world.spawnParticle(
                    EnumParticleTypes.SNOW_SHOVEL,
                    this.posX + (this.rand.nextDouble() - 0.5) * this.width.toDouble(),
                    this.posY + this.rand.nextDouble() * this.height.toDouble(),
                    this.posZ + (this.rand.nextDouble() - 0.5) * this.width.toDouble(),
                    0.0,
                    0.0,
                    0.0
                )
            }
        }

        super.onLivingUpdate()
    }

    override fun updateAITasks() {
        if (this.isWet()) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0f)
        }

        --this.heightOffsetUpdateTime

        if (this.heightOffsetUpdateTime <= 0) {
            this.heightOffsetUpdateTime = 100
            this.heightOffset = 0.5f + this.rand.nextGaussian().toFloat() * 3.0f
        }

        val entitylivingbase: EntityLivingBase? = this.getAttackTarget()

        if (entitylivingbase != null && entitylivingbase.y + entitylivingbase.getEyeHeight()
                .toDouble() > this.posY + this.getEyeHeight().toDouble() + this.heightOffset.toDouble()
        ) {
            this.motionY += (0.30000001192092896 - this.motionY) * 0.30000001192092896
            this.isAirBorne = true
        }

        super.updateAITasks()
    }

    override fun fall(distance: Float, damageMultiplier: Float) {
    }

    open class AIFireballAttack constructor(private val blaze: EntityEvolvedColdBlaze) : EntityAIBase() {
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
                        this.attackTime = 60
                        //this.blaze.setOnFire(true);
                    } else if (this.attackStep <= 4) {
                        this.attackTime = 6
                    } else {
                        this.attackTime = 100
                        this.attackStep = 0
                        // this.blaze.setOnFire(false);
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
                            val entitysmallfireball: EntityIceSpike = EntityIceSpike(this.blaze.world, this.blaze)
                            entitysmallfireball.posY = this.blaze.posY + (this.blaze.height / 2.0f).toDouble() + 1.2
                            entitysmallfireball.shoot(
                                this.blaze,
                                this.blaze.rotationPitch,
                                this.blaze.rotationYawHead,
                                0.0f,
                                0.8f,
                                1.0f
                            )
                            this.blaze.world.spawnEntity(entitysmallfireball)
                        }
                    }
                }

                this.blaze.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0f, 10.0f)
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

    override fun canBreath(): Boolean {
        return true
    }
}
