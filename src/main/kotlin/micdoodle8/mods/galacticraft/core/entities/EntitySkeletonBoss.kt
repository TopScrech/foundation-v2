package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.EnumCreatureAttribute
import net.minecraft.entity.IRangedAttackMob
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIHurtByTarget
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.ai.EntityAISwimming
import net.minecraft.entity.ai.EntityAIWander
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.entity.projectile.EntityTippedArrow
import net.minecraft.world.item.Items
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.BossInfo
import net.minecraft.world.Difficulty as EnumDifficulty
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.util.Random
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

open class EntitySkeletonBoss constructor(par1World: World?) : EntityBossBase((par1World)!!), IEntityBreathable,
    IRangedAttackMob, IIgnoreShift {
    @JvmField
    protected var ticks: Long = 0

    @JvmField
    var throwTimer: Int = 0

    @JvmField
    var postThrowDelay: Int = 20

    @JvmField
    var thrownEntity: Entity? = null

    @JvmField
    var targetEntity: Entity? = null

    init {
        this.setSize(1.5f, 4.0f)
        this.isImmuneToFire = true
        this.tasks.addTask(1, EntityAISwimming(this))
        this.tasks.addTask(2, EntityAIArrowAttack(this, 1.0, 25, 10.0f))
        this.tasks.addTask(2, EntityAIWander(this, 1.0))
        this.tasks.addTask(3, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(3, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIHurtByTarget(this, false))
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, false, true)
        )
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        var difficulty: Double = 0.0
        when (this.world.getDifficulty()) {
            EnumDifficulty.HARD -> difficulty = 2.0
            EnumDifficulty.NORMAL -> difficulty = 1.0
        
else -> {}
}
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(150.0f * ConfigManagerCore.dungeonBossHealthMod)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25 + 0.075 * difficulty)
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

    override fun isInWater(): Boolean {
        return false
    }

    override fun handleWaterMovement(): Boolean {
        return false
    }

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            val offsetX: Double = sin(-this.rotationYawHead / Constants.RADIANS_TO_DEGREES_D)
            val offsetZ: Double = cos(this.rotationYawHead / Constants.RADIANS_TO_DEGREES_D)
            val offsetY: Double = 2 * cos(((this.throwTimer + this.postThrowDelay) * 0.05f).toDouble())

            passenger.setPosition(
                this.posX + offsetX,
                this.posY + this.getMountedYOffset() + passenger.getYOffset() + offsetY,
                this.posZ + offsetZ
            )
        }
    }

    override fun knockBack(par1Entity: Entity, par2: Float, par3: Double, par5: Double) {
    }

    override fun onCollideWithPlayer(par1EntityPlayer: EntityPlayer) {
        if (!this.isAIDisabled() && this.getPassengers()
                .isEmpty() && this.postThrowDelay == 0 && this.throwTimer == 0 && par1EntityPlayer == this.targetEntity && this.deathTicks == 0
        ) {
            if (!this.world.isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOSS_LAUGH,
                        GCCoreUtil.getDimensionID(this.world),
                        arrayOf<Any?>()
                    ),
                    NetworkRegistry.TargetPoint(
                        GCCoreUtil.getDimensionID(this.world),
                        this.posX,
                        this.posY,
                        this.posZ,
                        40.0
                    )
                )
                par1EntityPlayer.startRiding(this)
            }

            this.throwTimer = 40
        }

        super.onCollideWithPlayer(par1EntityPlayer)
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent? {
        this.playSound(GCSounds.bossOoh, this.getSoundVolume(), this.getSoundPitch() + 1.0f)
        return null
    }

    override fun getDeathSound(): SoundEvent? {
        return null
    }

    //    @Override
    //    @SideOnly(Side.CLIENT)
    //    public ItemStack getHeldItem()
    //    {
    //        return EntitySkeletonBoss.defaultHeldItem;
    //    }
    override fun getCreatureAttribute(): EnumCreatureAttribute {
        return EnumCreatureAttribute.UNDEAD
    }

    override fun onLivingUpdate() {
        this.ticks++

        if (!this.world.isRemote && this.getHealth() <= 150.0f * ConfigManagerCore.dungeonBossHealthMod / 2) {
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
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

        if (this.throwTimer > 0) {
            this.throwTimer--
        }

        if (this.postThrowDelay > 0) {
            this.postThrowDelay--
        }

        if (!this.getPassengers().isEmpty() && this.throwTimer == 0) {
            this.postThrowDelay = 20

            this.thrownEntity = this.getPassengers().get(0)

            if (!this.world.isRemote) {
                this.removePassengers()
            }
        }

        if (this.thrownEntity != null && this.postThrowDelay == 18) {
            var d0: Double = this.posX - this.thrownEntity!!.x
            var d1: Double

            d1 = this.posZ - this.thrownEntity!!.z
            while (d0 * d0 + d1 * d1 < 1.0E-4) {
                d0 = (Math.random() - Math.random()) * 0.01
                d1 = (Math.random() - Math.random()) * 0.01
            }


            if (!this.world.isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOW,
                        GCCoreUtil.getDimensionID(this.world),
                        arrayOf<Any?>()
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
            (this.thrownEntity as EntityPlayer).attackedAtYaw =
                atan2(d1, d0).toFloat() * Constants.RADIANS_TO_DEGREES - this.rotationYaw

            this.thrownEntity!!.isAirBorne = true
            val f: Float = MathHelper.sqrt(d0 * d0 + d1 * d1)
            val f1: Float = 2.4f
            this.thrownEntity!!.motionX /= 2.0
            this.thrownEntity!!.motionY /= 2.0
            this.thrownEntity!!.motionZ /= 2.0
            this.thrownEntity!!.motionX -= d0 / f * f1
            this.thrownEntity!!.motionY += f1.toDouble() / 5
            this.thrownEntity!!.motionZ -= d1 / f * f1

            if (this.thrownEntity!!.deltaMovement.y > 0.4000000059604645) {
                this.thrownEntity!!.motionY = 0.4000000059604645
            }
        }

        super.onLivingUpdate()
    }

    override fun getDropItem(): Item? {
        return Items.ARROW
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

    override fun dropFewItems(b: Boolean, i: Int) {
        if (this.rand.nextInt(200) - i >= 5) {
            return
        }

        if (i > 0) {
            val var2: ItemStack = ItemStack(Items.BOW)
            EnchantmentHelper.addRandomEnchantment(this.rand, var2, 5, false)
            this.entityDropItem(var2, 0.0f)
        } else {
            this.dropItem(Items.BOW, 1)
        }
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun attackEntityWithRangedAttack(target: EntityLivingBase, f: Float) {
        if (!this.getPassengers().isEmpty()) {
            return
        }

        val arrow: EntityTippedArrow = EntityTippedArrow(this.world, this)
        val d0: Double = target.x - this.posX
        val d1: Double = target.getBoundingBox().minY + (target.height / 3.0f).toDouble() - arrow.posY
        val d2: Double = target.z - this.posZ
        val d3: Double = MathHelper.sqrt(d0 * d0 + d2 * d2).toDouble()
        arrow.shoot(
            d0,
            d1 + d3 * 0.20000000298023224,
            d2,
            1.6f,
            (14 - this.world.getDifficulty().getId() * 4) as Float
        )

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f))
        this.world.spawnEntity(arrow)
    }

    override fun shouldIgnoreShiftExit(): Boolean {
        return true
    }

    override fun getGuaranteedLoot(rand: Random?): ItemStack {
        val stackList: MutableList<ItemStack?> = GalacticraftRegistry.getDungeonLoot(1)
        return stackList.get((rand)!!.nextInt(stackList.size))!!.copy()
    }

    override fun getChestTier(): Int {
        return 1
    }

    override fun dropKey() {
        this.entityDropItem(ItemStack(requireNotNull(GCItems.key)), 0.5f)
    }

    override fun getHealthBarColor(): BossInfo.Color {
        return BossInfo.Color.GREEN
    }

    override fun setSwingingArms(swingingArms: Boolean) {
        // TODO Auto-generated method stub
        //Unused in this Galacticraft entity
    }

    companion object {
        private val defaultHeldItem = ItemStack(Items.BOW, 1)
    }
}
