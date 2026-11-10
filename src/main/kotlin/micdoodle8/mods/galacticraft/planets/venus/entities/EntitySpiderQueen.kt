package micdoodle8.mods.galacticraft.planets.venus.entities

import com.google.common.collect.Lists
import java.util.UUID
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.entities.EntityBossBase
import micdoodle8.mods.galacticraft.core.entities.IBoss
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.EnumCreatureAttribute
import net.minecraft.entity.IRangedAttackMob
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIAttackMelee
import net.minecraft.entity.ai.EntityAIHurtByTarget
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.ai.EntityAIWander
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagLong
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.pathfinding.PathNavigate
import net.minecraft.pathfinding.PathNavigateGround
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.BossInfo
import net.minecraft.world.World
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class EntitySpiderQueen constructor(worldIn: World?) : EntityBossBase((worldIn)!!), IEntityBreathable, IBoss,
    IRangedAttackMob {
    @JvmField
    var shouldEvade: Boolean = false
    private val juicersSpawned: MutableList<EntityJuicer> = Lists.newArrayList<EntityJuicer>()
    private var spawnedPreload: MutableList<UUID?>? = null

    private var rangedAttackTime = 0
    private val minRangedAttackTime: Int
    private val maxRangedAttackTime: Int

    init {
        this.setSize(1.4f, 0.9f)
        this.tasks.addTask(4, EntityAIAttackMelee(this, 1.0, true))
        this.tasks.addTask(5, EntityAIWander(this, 0.8))
        this.tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(6, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIHurtByTarget(this, false))
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, true)
        )
        this.maxRangedAttackTime = 60
        this.minRangedAttackTime = 20
        this.ignoreFrustumCheck = true
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun getMountedYOffset(): Double {
        return (this.height * 0.5f).toDouble()
    }

    override fun createNavigator(worldIn: World): PathNavigate {
        return PathNavigateGround(this, worldIn)
    }

    override fun entityInit() {
        super.entityInit()
        this.dataManager.register<Byte>(BURROWED_COUNT, (-1).toByte())
    }

    open fun getBurrowedCount(): Byte {
        return this.dataManager.get<Byte>(BURROWED_COUNT)
    }

    open fun setBurrowedCount(count: Byte) {
        this.dataManager.set<Byte>(BURROWED_COUNT, count)
    }

    override fun onUpdate() {
        super.onUpdate()

        if (!this.world.isRemote) {
            if (!this.shouldEvade && this.deathTicks <= 0) {
                val attackTarget: EntityLivingBase? = this.getAttackTarget()

                if (attackTarget != null) {
                    val dX: Double = attackTarget.posX - this.posX
                    val dY: Double =
                        attackTarget.getEntityBoundingBox().minY + (attackTarget.height / 3.0f).toDouble() - this.posY
                    val dZ: Double = attackTarget.posZ - this.posZ

                    val distance: Float = 5.0f
                    val d0: Double = this.getDistanceSq(
                        attackTarget.posX,
                        attackTarget.getEntityBoundingBox().minY,
                        attackTarget.posZ
                    )

                    this.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0f, 30.0f)

                    if (--this.rangedAttackTime == 0) {
                        if (dX * dX + dY * dY + dZ * dZ > distance * distance) {
                            val f: Float = MathHelper.sqrt(d0) / distance
                            this.attackEntityWithRangedAttack(attackTarget, 0.0f)
                            this.rangedAttackTime =
                                MathHelper.floor(f * (this.maxRangedAttackTime - this.minRangedAttackTime).toFloat() + this.minRangedAttackTime.toFloat())
                        }
                    } else if (this.rangedAttackTime < 0) {
                        val f2: Float = MathHelper.sqrt(d0) / distance
                        this.rangedAttackTime =
                            MathHelper.floor(f2 * (this.maxRangedAttackTime - this.minRangedAttackTime).toFloat() + this.minRangedAttackTime.toFloat())
                    }
                }
            }
        }

        if (this.spawnedPreload != null) {
            for (id: UUID? in (this.spawnedPreload)!!) {
                var entity: Entity? = null
                for (e: Entity in this.world.getLoadedEntityList()) {
                    if (e.getUniqueID() == id) {
                        entity = e
                        break
                    }
                }
                if (entity is EntityJuicer) {
                    this.juicersSpawned.add(entity)
                }
            }
            if (this.juicersSpawned.size == this.spawnedPreload!!.size) {
                this.spawnedPreload!!.clear()
                this.spawnedPreload = null
            }
        }

        if (!this.world.isRemote && this.shouldEvade) {
            if (this.spawner != null) {
                val roomBounds: AxisAlignedBB = ((this.spawner)!!.getRangeBounds())!!
                val tarX: Double = (roomBounds.minX + roomBounds.maxX) / 2.0
                val tarZ: Double = (roomBounds.minZ + roomBounds.maxZ) / 2.0
                val dX: Double = tarX - this.posX
                val dY: Double = roomBounds.maxY - this.posY
                val dZ: Double = tarZ - this.posZ

                val movespeed: Double =
                    1.0 * this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()
                this.motionX = min(max(dX / 2.0f, -movespeed), movespeed)
                this.motionZ = min(max(dZ / 2.0f, -movespeed), movespeed)
                this.navigator.tryMoveToXYZ(tarX, this.posY, tarZ, movespeed)

                if (abs(dX) < 0.1 && abs(dZ) < 0.1) {
                    this.motionY = min(dY, 0.2)

                    if (abs(dY) - this.height < 1.1 && abs(this.posY - this.lastTickPosY) < 0.05) {
                        if (this.getBurrowedCount() >= 0) {
                            if (this.ticksExisted % 20 == 0) {
                                if (this.juicersSpawned.size < 6) {
                                    val juicer: EntityJuicer = EntityJuicer(this.world)
                                    val angle: Double = Math.random() * 2 * Math.PI
                                    val dist: Double = 3.0
                                    juicer.setPosition(
                                        this.posX + dist * sin(angle),
                                        this.posY + 0.2f,
                                        this.posZ + dist * cos(angle)
                                    )
                                    juicer.setHanging(true)
                                    this.world.spawnEntity(juicer)
                                    this.juicersSpawned.add(juicer)
                                }
                            }

                            if (this.getBurrowedCount() < 20) {
                                this.setBurrowedCount((this.getBurrowedCount() + 1).toByte())
                            }
                        } else {
                            this.setBurrowedCount(0.toByte())
                        }
                    }
                }
            }

            if (!this.juicersSpawned.isEmpty()) {
                var allDead: Boolean = true
                for (juicer: EntityJuicer in this.juicersSpawned) {
                    if (!juicer.isDead) {
                        allDead = false
                    }
                }
                if (allDead) {
                    this.juicersSpawned.clear()
                    this.shouldEvade = false
                    this.setBurrowedCount(((-1).toByte()).toByte())
                }
            }
        }
    }

    override fun getGuaranteedLoot(rand: Random?): ItemStack {
        val stackList: MutableList<ItemStack?> = GalacticraftRegistry.getDungeonLoot(3)
        return stackList.get((rand)!!.nextInt(stackList.size))!!.copy()
    }

    override fun knockBack(entityIn: Entity, par2: Float, par3: Double, par4: Double) {
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(150.0f * ConfigManagerCore.dungeonBossHealthMod)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.250000001192092896)
    }

    override fun getSoundPitch(): Float {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f + 0.4f
    }

    override fun getSoundVolume(): Float {
        return 5.0f
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
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.5f, 0.5f)
    }

    override fun getDropItem(): Item? {
        return Items.STRING
    }

    override fun dropFewItems(par1: Boolean, par2: Int) {
        super.dropFewItems(par1, par2)

        if (par1 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + par2) > 0)) {
            this.dropItem(Items.SPIDER_EYE, 1)
        }
    }

    override fun setInWeb() {
    }

    override fun getCreatureAttribute(): EnumCreatureAttribute {
        return EnumCreatureAttribute.ARTHROPOD
    }

    override fun isPotionApplicable(potioneffectIn: PotionEffect): Boolean {
        return potioneffectIn.getPotion() !== MobEffects.POISON && super.isPotionApplicable(potioneffectIn)
    }

    override fun getEyeHeight(): Float {
        return 0.65f
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun isEntityInvulnerable(source: DamageSource): Boolean {
        if (this.getBurrowedCount() >= 0) {
            return true
        }

        return super.isEntityInvulnerable(source)
    }

    override fun damageEntity(damageSrc: DamageSource, damageAmount: Float) {
        val healthLast: Float = this.getHealth()
        super.damageEntity(damageSrc, damageAmount)
        val health: Float = this.getHealth()

        val thirdHealth: Float = this.getMaxHealth() / 3.0f

        if (health < thirdHealth && healthLast >= thirdHealth) {
            shouldEvade = true
        } else if (health < 2 * thirdHealth && healthLast >= 2 * thirdHealth) {
            shouldEvade = true
        }
    }

    override fun attackEntityWithRangedAttack(target: EntityLivingBase, damage: Float) {
        val entityarrow: EntityWebShot = EntityWebShot(
            this.world,
            this,
            target,
            0.8f,
            (14 - this.world.getDifficulty().getId() * 4).toFloat()
        )
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f))
        this.world.spawnEntity(entityarrow)
    }

    override fun writeEntityToNBT(tagCompound: NBTTagCompound) {
        super.writeEntityToNBT(tagCompound)

        tagCompound.setBoolean("should_evade", this.shouldEvade)

        val list: NBTTagList = NBTTagList()
        for (juicer: EntityJuicer in this.juicersSpawned) {
            list.appendTag(NBTTagLong(juicer.getPersistentID().getMostSignificantBits()))
            list.appendTag(NBTTagLong(juicer.getPersistentID().getLeastSignificantBits()))
        }
        tagCompound.setTag("spawned_children", list)
    }

    override fun readEntityFromNBT(tagCompound: NBTTagCompound) {
        super.readEntityFromNBT(tagCompound)

        this.shouldEvade = tagCompound.getBoolean("should_evade")

        if (tagCompound.hasKey("spawned_children")) {
            this.spawnedPreload = Lists.newArrayList<UUID?>()
            val list: NBTTagList = tagCompound.getTagList("spawned_children", 4)
            var i: Int = 0
            while (i < list.tagCount()) {
                val tagMost: NBTTagLong = list.get(i) as NBTTagLong
                val tagLeast: NBTTagLong = list.get(i + 1) as NBTTagLong
                this.spawnedPreload!!.add(UUID(tagMost.getLong(), tagLeast.getLong()))
                i += 2
            }
        }
    }

    override fun getChestTier(): Int {
        return 3
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

    override fun dropKey() {
        this.entityDropItem(ItemStack(VenusItems.key, 1, 0), 0.5f)
    }

    override fun getHealthBarColor(): BossInfo.Color {
        return BossInfo.Color.PURPLE
    }

    override fun setSwingingArms(swingingArms: Boolean) {
        // TODO Auto-generated method stub
        //TODO for 1.12.2
    }

    companion object {
        private val BURROWED_COUNT: DataParameter<Byte> =
            EntityDataManager.createKey<Byte>(EntitySpiderQueen::class.java, DataSerializers.BYTE)
    }
}
