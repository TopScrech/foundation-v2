package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import java.util.UUID
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIAttackMelee
import net.minecraft.entity.ai.EntityAIAvoidEntity
import net.minecraft.entity.ai.EntityAICreeperSwell
import net.minecraft.entity.ai.EntityAIHurtByTarget
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.ai.EntityAISwimming
import net.minecraft.entity.ai.EntityAIWander
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.ai.attributes.IAttributeInstance
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.passive.EntityOcelot
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.ForgeHooks

open class EntityEvolvedCreeper constructor(par1World: World) : EntityCreeper(par1World), IEntityBreathable {
    private var sizeXBase = -1.0f
    private var sizeYBase = 0f

    init {
        this.tasks.taskEntries.clear()
        this.tasks.addTask(1, EntityAISwimming(this))
        this.tasks.addTask(2, EntityAICreeperSwell(this))
        this.tasks.addTask(3, EntityAIAvoidEntity<EntityOcelot>(this, EntityOcelot::class.java, 6.0f, 1.0, 1.2))
        this.tasks.addTask(4, EntityAIAttackMelee(this, 1.0, false))
        this.tasks.addTask(5, EntityAIWander(this, 0.8))
        this.tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(6, EntityAILookIdle(this))
        this.targetTasks.addTask(
            1,
            EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, true)
        )
        this.targetTasks.addTask(2, EntityAIHurtByTarget(this, false, *arrayOfNulls<Class<*>>(0)))
        this.setSize(0.7f, 2.2f)
    }

    override fun entityInit() {
        super.entityInit()
        this.dataManager.register<Boolean>(IS_CHILD, false)
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)

        if (this.isChild()) {
            nbt.putBoolean("IsBaby", true)
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)

        if (nbt.getBooleanOr("IsBaby", false)) {
            this.setChild(true)
        }
    }

    override fun canBreath(): Boolean {
        return true
    }

    open fun setChildSize(isChild: Boolean) {
        this.setCreeperScale(if (isChild) 0.5f else 1.0f)
    }

    override fun setSize(sizeX: Float, sizeY: Float) {
        val flag: Boolean = this.sizeXBase > 0.0f && this.sizeYBase > 0.0f
        this.sizeXBase = sizeX
        this.sizeYBase = sizeY

        if (!flag) {
            this.setCreeperScale(1.0f)
        }
    }

    protected fun setCreeperScale(scale: Float) {
        super.setSize(this.sizeXBase * scale, this.sizeYBase * scale)
        //FMLLog.info("" + this.sizeYBase + " " + scale);
    }

    override fun isChild(): Boolean {
        return this.dataManager.get<Boolean>(IS_CHILD)
    }

    override fun getExperiencePoints(p_70693_1_: EntityPlayer): Int {
        if (this.isChild()) {
            this.experienceValue = (this.experienceValue * 5) / 2
        }

        return super.getExperiencePoints(p_70693_1_)
    }

    open fun setChild(isChild: Boolean) {
        this.dataManager.set<Boolean>(IS_CHILD, isChild)

        if (this.world != null && !this.world.isRemote) {
            val iattributeinstance: IAttributeInstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            iattributeinstance.removeModifier(babySpeedBoostModifier)

            if (isChild) {
                iattributeinstance.applyModifier(babySpeedBoostModifier)
            }
        }

        this.setChildSize(isChild)
    }

    override fun jump() {
        this.motionY = 0.45 / WorldUtil.getGravityFactor(this)
        if (this.motionY < 0.22) {
            this.motionY = 0.22
        }

        if (this.hasEffect(MobEffects.JUMP_BOOST)) {
            this.motionY += ((this.getEffect(MobEffects.JUMP_BOOST)!!.getAmplifier() + 1) * 0.1f).toDouble()
        }

        if (this.isSprinting()) {
            val f: Float = this.rotationYaw / Constants.RADIANS_TO_DEGREES
            this.motionX -= (MathHelper.sin(f) * 0.2f).toDouble()
            this.motionZ += (MathHelper.cos(f) * 0.2f).toDouble()
        }

        this.isAirBorne = true
        ForgeHooks.onLivingJump(this)
    }

    override fun getDropItem(): Item? {
        if (this.isBurning()) {
            return Items.BLAZE_ROD
        }
        return Items.REDSTONE
    }

    protected open fun addRandomDrop() {
        when (this.rand.nextInt(12)) {
            0, 1, 2, 3 -> this.entityDropItem(ItemStack(Blocks.SAND), 0.0f)
            4, 5 ->             //Oxygen tank half empty or less
                this.entityDropItem(ItemOxygenTank.stack(requireNotNull(GCItems.oxTankMedium), 1, 901 + this.rand.nextInt(900)), 0.0f)

            6 -> this.dropItem(GCItems.oxygenGear, 1)
            7, 8 -> this.entityDropItem(ItemStack(Blocks.ICE), 0.0f)
            else -> if (ConfigManagerCore.challengeMobDropsAndSpawning) this.dropItem(Items.REEDS, 1)
        }
    }

    override fun dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource) {
        // No loot table
        this.dropFewItems(wasRecentlyHit, lootingModifier)
        this.dropEquipment(wasRecentlyHit, lootingModifier)
    }

    override fun dropFewItems(wasRecentlyHit: Boolean, lootingModifier: Int) {
        super.dropFewItems(wasRecentlyHit, lootingModifier)

        if (wasRecentlyHit && this.rand.nextFloat() < 0.025f + lootingModifier.toFloat() * 0.02f) {
            this.addRandomDrop()
        }
    }

    companion object {
        private val IS_CHILD: DataParameter<Boolean> =
            EntityDataManager.createKey<Boolean>(EntityEvolvedCreeper::class.java, DataSerializers.BOOLEAN)
        private val babySpeedBoostUUID: UUID = UUID.fromString("ef67a435-32a4-4efd-b218-e7431438b109")
        private val babySpeedBoostModifier =
            AttributeModifier(babySpeedBoostUUID, "Baby speed boost evolved creeper", 0.5, 1)
    }
}
