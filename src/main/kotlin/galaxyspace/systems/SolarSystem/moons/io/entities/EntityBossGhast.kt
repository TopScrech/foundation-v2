package galaxyspace.systems.SolarSystem.moons.io.entities

import net.minecraft.world.BossInfo.Color

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.entities.EntityFlyBossBase
import galaxyspace.systems.SolarSystem.moons.io.entities.EntityBossGhast
import galaxyspace.core.GSItems
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer
import net.minecraft.entity.ai.EntityMoveHelper
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.IMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.entity.projectile.EntityLargeFireball
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.BossInfo
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.util.Random

open class EntityBossGhast constructor(par1World: World?) : EntityFlyBossBase((par1World)!!), IMob, IEntityBreathable {
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
        this.setSize(4.0f, 3.0f)
        this.isImmuneToFire = true
        this.moveHelper = GhastMoveHelper(this)

        this.tasks.addTask(3, AIFireballAttack(this))
        this.tasks.addTask(2, AIRandomFly(this))
        //this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 25, 20.0F));
        //this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(2, AILookAround(this))
        this.targetTasks.addTask(1, EntityAIFindEntityNearestPlayer(this))
    }

    open fun setAttacking(attacking: Boolean) {
        this.dataManager.set<Boolean>(ATTACKING, attacking)
    }

    override fun attackEntityFrom(source: DamageSource, par2: Float): Boolean {
        if (this.isEntityInvulnerable(source)) {
            return false
        } else if (source.getImmediateSource() is EntityLargeFireball && source.getTrueSource() is EntityPlayer) {
            super.attackEntityFrom(source, 5.0f)

            return true
        } else return super.attackEntityFrom(source, par2)
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(300.0f * ConfigManagerCore.dungeonBossHealthMod)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05)
        //this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0F);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0)
    }

    constructor(world: World?, vec: Vector3) : this(world) {
        this.setPosition(vec.x, vec.y, vec.z)
    }

    override fun entityInit() {
        super.entityInit()
        this.dataManager.register<Boolean>(ATTACKING, false)
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_GHAST_AMBIENT
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent? {
        return SoundEvents.ENTITY_GHAST_HURT
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.ENTITY_GHAST_DEATH
    }

    override fun onDeathUpdate() {
        super.onDeathUpdate()

        if (!this.world.isRemote) {
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

    /*
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f)
    {
        this.world.playEvent(null, 1024, new BlockPos(this), 0);
        double d3 = this.posX;
        double d4 = this.posY + 5.5D;
        double d5 = this.posZ;
        double d6 = entitylivingbase.posX - d3;
        double d7 = entitylivingbase.posY + entitylivingbase.getEyeHeight() * 0.5D - d4;
        double d8 = entitylivingbase.posZ - d5;
        EntityProjectileTNT projectileTNT = new EntityProjectileTNT(this.world, this, d6 * 0.5D, d7 * 0.5D, d8 * 0.5D);

        projectileTNT.posY = d4;
        projectileTNT.posX = d3;
        projectileTNT.posZ = d5;
        this.world.spawnEntity(projectileTNT);
    }
    */
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

        /* if (this.getHealth() <= this.getMaxHealth() / 2.0 && !spawnedMinion)
        {
        	this.spawnedMinion = true;
        	if(!world.isRemote) {	        	
	        	/ *for(int i = 0; i < 4; i++)
	        	{
	        		Entity entity = new EntityEvolvedColdBlaze(world);
	        		entity.setPosition(this.posX + this.world.rand.nextFloat(), this.posY, this.posZ + this.world.rand.nextFloat());
	        		this.world.spawnEntity(entity);
	        	}
	        	//this.setHealth(this.getHealth() + this.getMaxHealth() / 4.0F);
        	}
        	
        	if(world.isRemote)
        	{
        		for(int i = 0; i < 50; i++)
        			GalaxySpace.proxy.spawnParticle("waterbubbles", new Vector3(this.posX, this.posY + rand.nextDouble(), this.posZ), new Vector3(0.0D + (rand.nextDouble() / 5) * (rand.nextBoolean() ? -1 : 1), 0.3D, 0.0D + (rand.nextDouble() / 5) * (rand.nextBoolean() ? -1 : 1)), new Object [] { 50 , 5, false, new Vector3(1.0F, 1.0F, 1.0F), 1.0D} ); 	
        	}
        }    */
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
                    EnumParticleTypes.FLAME,
                    this.posX + (this.rand.nextDouble() - 0.5) + (this.rand.nextDouble() * (if (this.rand.nextBoolean()) -1 else 1)) * this.width.toDouble(),
                    this.posY - 2 + this.rand.nextDouble() * this.height.toDouble(),
                    this.posZ + (this.rand.nextDouble() - 0.5) + (this.rand.nextDouble() * (if (this.rand.nextBoolean()) -1 else 1)) * this.width.toDouble(),
                    0.0,
                    0.1,
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
        val stackList: MutableList<ItemStack?> = GalacticraftRegistry.getDungeonLoot(5)
        return stackList.get((rand)!!.nextInt(stackList.size))!!.copy()
    }

    protected open fun shouldAttack(living: EntityLivingBase?): Boolean {
        return true
    }

    protected open fun spitFireball() {
        val vec3d: Vec3d = this.getLook(1.0f)
        val d2: Double = getAttackTarget()!!.posX - (this.posX + vec3d.x * 4.0)
        val d3: Double =
            getAttackTarget()!!.getEntityBoundingBox().minY + (getAttackTarget()!!.height / 2.0f).toDouble() - (0.5 + this.posY + (this.height / 2.0f).toDouble())
        val d4: Double = getAttackTarget()!!.posZ - (this.posZ + vec3d.z * 4.0)
        val entitylargefireball: EntityLargeFireball = EntityLargeFireball(world, this, d2, d3, d4)
        entitylargefireball.explosionPower = 2
        entitylargefireball.posX = this.posX + vec3d.x * 4.0
        entitylargefireball.posY = this.posY + (this.height / 2.0f).toDouble() + 0.5
        entitylargefireball.posZ = this.posZ + vec3d.z * 4.0
        world.spawnEntity(entitylargefireball)

        // when we attack, there is a 1-in-6 chance we decide to stop attacking
        if (rand.nextInt(6) == 0) {
            setAttackTarget(null)
        }
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
    open class AIRandomFly constructor(private val parentEntity: EntityBossGhast) : EntityAIBase() {
        init {
            this.setMutexBits(1)
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        override fun shouldExecute(): Boolean {
            val entitymovehelper: EntityMoveHelper = this.parentEntity.getMoveHelper()

            if (!entitymovehelper.isUpdating()) {
                return true //parentEntity.getAttackTarget() == null;
            } else {
                val d0: Double = entitymovehelper.getX() - this.parentEntity.posX
                val d1: Double = entitymovehelper.getY() - this.parentEntity.posY
                val d2: Double = entitymovehelper.getZ() - this.parentEntity.posZ
                val d3: Double = d0 * d0 + d1 * d1 + d2 * d2
                //return parentEntity.getAttackTarget() == null && (d3 < 1.0D || d3 > 3600.0D);
                return d3 < 1.0 || d3 > 3600.0
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        override fun shouldContinueExecuting(): Boolean {
            return false
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        override fun startExecuting() {
            val random: Random = this.parentEntity.getRNG()
            val d0: Double = this.parentEntity.posX + ((random.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            val d1: Double = this.parentEntity.posY + ((random.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            val d2: Double = this.parentEntity.posZ + ((random.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0)
        }
    }

    open class AILookAround constructor(private val parentEntity: EntityBossGhast) : EntityAIBase() {
        init {
            this.setMutexBits(2)
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        override fun shouldExecute(): Boolean {
            return true
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        override fun updateTask() {
            if (this.parentEntity.getAttackTarget() == null) {
                this.parentEntity.rotationYaw = -(MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ)
                    .toFloat()) * (180f / Math.PI.toFloat())
                this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw
            } else {
                val entitylivingbase: EntityLivingBase? = this.parentEntity.getAttackTarget()
                val d0: Double = 64.0

                if (entitylivingbase!!.getDistanceSq(this.parentEntity) < 4096.0) {
                    val d1: Double = entitylivingbase.x - this.parentEntity.posX
                    val d2: Double = entitylivingbase.z - this.parentEntity.posZ
                    this.parentEntity.rotationYaw = -(MathHelper.atan2(d1, d2).toFloat()) * (180f / Math.PI.toFloat())
                    this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw
                }
            }
        }
    }

    open class AIFireballAttack constructor(private val parentEntity: EntityBossGhast) : EntityAIBase() {
        @JvmField
        var attackTimer: Int = 0

        @JvmField
        var prevAttackTimer: Int = 0 // TF - add for renderer

        override fun shouldExecute(): Boolean {
            val entitylivingbase: EntityLivingBase? = this.parentEntity.getAttackTarget()
            if (entitylivingbase == null) {
                return false
            }

            return true

            // return this.parentEntity.getAttackTarget() != null && parentEntity.shouldAttack(parentEntity.getAttackTarget());
        }

        override fun startExecuting() {
            this.prevAttackTimer = 0
            this.attackTimer = this.prevAttackTimer
        }

        override fun resetTask() {
            this.parentEntity.setAttacking(false)
        }

        override fun updateTask() {
            val entitylivingbase: EntityLivingBase? = this.parentEntity.getAttackTarget()

            if (entitylivingbase!!.getDistanceSq(this.parentEntity) < 4096.0 && this.parentEntity.getEntitySenses()
                    .canSee(entitylivingbase)
            ) {
                val world: World? = this.parentEntity.world
                this.prevAttackTimer = attackTimer
                ++this.attackTimer

                // TF face our target at all times
                this.parentEntity.getLookHelper().setLookPositionWithEntity(
                    entitylivingbase,
                    10f,
                    this.parentEntity.getVerticalFaceSpeed().toFloat()
                )

                if (this.attackTimer == 10) {
                    parentEntity.playSound(SoundEvents.ENTITY_GHAST_WARN, 10.0f, parentEntity.getSoundPitch())
                }

                if (this.attackTimer == 20) {
                    if (this.parentEntity.shouldAttack(entitylivingbase)) {
                        // TF - call custom method
                        parentEntity.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 10.0f, parentEntity.getSoundPitch())
                        this.parentEntity.spitFireball()
                        this.prevAttackTimer = attackTimer
                    }
                    this.attackTimer = -40
                }
            } else if (this.attackTimer > 0) {
                this.prevAttackTimer = attackTimer
                --this.attackTimer
            }

            this.parentEntity.setAttacking(this.attackTimer > 10)
        }
    }

    open class GhastMoveHelper constructor(private val parentEntity: EntityBossGhast) :
        EntityMoveHelper(parentEntity) {
        private var courseChangeCooldown = 0

        override fun onUpdateMoveHelper() {
            if (this.action == Action.MOVE_TO) {
                val d0: Double = this.posX - this.parentEntity.posX
                val d1: Double = this.posY - this.parentEntity.posY
                val d2: Double = this.posZ - this.parentEntity.posZ
                var d3: Double = d0 * d0 + d1 * d1 + d2 * d2

                if (this.courseChangeCooldown-- <= 0) {
                    this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2
                    d3 = MathHelper.sqrt(d3).toDouble()

                    if (this.isNotColliding(this.posX, this.posY, this.posZ, d3)) {
                        this.parentEntity.motionX += d0 / d3 * 0.1
                        this.parentEntity.motionY += d1 / d3 * 0.1
                        this.parentEntity.motionZ += d2 / d3 * 0.1
                    } else {
                        this.action = Action.WAIT
                    }
                }
            }
        }

        /**
         * Checks if entity bounding box is not colliding with terrain
         */
        private fun isNotColliding(x: Double, y: Double, z: Double, p_179926_7_: Double): Boolean {
            val d0: Double = (x - this.parentEntity.posX) / p_179926_7_
            val d1: Double = (y - this.parentEntity.posY) / p_179926_7_
            val d2: Double = (z - this.parentEntity.posZ) / p_179926_7_
            var axisalignedbb: AxisAlignedBB = this.parentEntity.getEntityBoundingBox()

            var i: Int = 1
            while (i.toDouble() < p_179926_7_) {
                axisalignedbb = axisalignedbb.offset(d0, d1, d2)

                if (!this.parentEntity.world.getCollisionBoxes(this.parentEntity, axisalignedbb).isEmpty()) {
                    return false
                }
                ++i
            }

            return true
        }
    }

    /*
	@Override
	public void setSwingingArms(boolean swingingArms) {
		
	}
*/
    override fun getChestTier(): Int {
        return 5
    }

    override fun dropKey() {
        this.entityDropItem(foundation.item.FoundationItemVariants.stack(GSItems.DUNGEON_KEYS, 1, 1), 0.5f)
    }

    override fun getHealthBarColor(): BossInfo.Color {
        return BossInfo.Color.RED
    }

    companion object {
        private val ATTACKING: DataParameter<Boolean> =
            EntityDataManager.createKey<Boolean>(EntityBossGhast::class.java, DataSerializers.BOOLEAN)
    }
}
