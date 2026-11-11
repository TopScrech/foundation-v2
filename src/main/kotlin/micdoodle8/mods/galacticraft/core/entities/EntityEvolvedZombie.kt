package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.monster.EntityZombie
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
import net.minecraft.world.Difficulty as EnumDifficulty
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.ForgeHooks
import kotlin.math.abs

open class EntityEvolvedZombie constructor(par1World: World) : EntityZombie(par1World), IEntityBreathable,
    ITumblable {
    private val conversionTime = 0
    private var tumbling = 0f
    private var tumbleAngle = 0f

    init {
        this.setSize(0.6f, 1.95f)
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0)
        var difficulty: Double = 0.0
        when (this.world.getDifficulty()) {
            EnumDifficulty.HARD -> difficulty = 2.0
            EnumDifficulty.NORMAL -> difficulty = 1.0
        
else -> {}
}
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26 + 0.04 * difficulty)
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0 + difficulty)
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0 + difficulty * 2.0)
    }

    override fun canBreath(): Boolean {
        return true
    }

    open fun getReinforcementsAttribute(): IAttribute? {
        return SPAWN_REINFORCEMENTS_CHANCE
    }

    override fun jump() {
        this.motionY = 0.48 / WorldUtil.getGravityFactor(this)
        if (this.motionY < 0.24) {
            this.motionY = 0.24
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

    protected open fun addRandomDrop() {
        when (this.rand.nextInt(16)) {
            0, 1, 2 ->             //Dehydrated carrot
                this.entityDropItem(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 1), 0.0f)

            3, 4 -> this.dropItem(GCItems.meteoricIronRaw, 1)
            5, 6 ->             //Dehydrated potato
                this.entityDropItem(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 3), 0.0f)

            7, 8 ->             //Oxygen tank half empty or less
                this.entityDropItem(ItemOxygenTank.stack(requireNotNull(GCItems.oxTankMedium), 1, 901 + this.rand.nextInt(900)), 0.0f)

            9 -> this.dropItem(GCItems.oxMask, 1)
            10 -> this.dropItem(GCItems.oxygenVent, 1)
            11, 12 -> this.dropItem(Items.CARROT, 1)
            13, 14, 15 -> if (ConfigManagerCore.challengeMobDropsAndSpawning) this.dropItem(Items.MELON_SEEDS, 1)
        }
    }

    override fun dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource) {
        // No loot table
        this.dropFewItems(wasRecentlyHit, lootingModifier)
        this.dropEquipment(wasRecentlyHit, lootingModifier)
    }

    override fun dropFewItems(wasRecentlyHit: Boolean, lootingModifier: Int) {
        val item: Item? = Items.ROTTEN_FLESH

        //Less rotten flesh than vanilla
        var j: Int = this.rand.nextInt(2)

        if (item != null) {
            if (lootingModifier > 0) {
                j += this.rand.nextInt(lootingModifier + 1)
            }

            for (k: Int in 0..<j) {
                this.dropItem(item, 1)
            }
        }

        //Drop copper ingot as semi-rare drop if player hit and if dropping rotten flesh (50% chance)
        if (wasRecentlyHit && (ConfigManagerCore.challengeMobDropsAndSpawning) && j > 0 && this.rand.nextInt(6) <= (lootingModifier + 1) / 2) this.entityDropItem(
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 3),
            0.0f
        )

        if (wasRecentlyHit && this.rand.nextFloat() < 0.025f + lootingModifier.toFloat() * 0.02f) {
            this.addRandomDrop()
        }
    }

    override fun setTumbling(value: Float) {
        if (value != 0f) {
            if (this.tumbling == 0f) this.tumbling = (this.world.rand.nextFloat() + 0.5f) * value
        } else this.tumbling = 0f
    }

    override fun onEntityUpdate() {
        super.onEntityUpdate()
        if (!this.isDead) {
            if (this.tumbling != 0f) {
                if (this.onGround) {
                    this.tumbling = 0f
                }
            }

            if (!this.world.isRemote) {
                this.setSpinPitch(this.tumbling)
            } else {
                this.tumbling = this.getSpinPitch()
                this.tumbleAngle -= this.tumbling
                if (this.tumbling == 0f && this.tumbleAngle != 0f) {
                    this.tumbleAngle *= 0.8f
                    if (abs(this.tumbleAngle) < 1f) this.tumbleAngle = 0f
                }
            }
        }
    }

    override fun entityInit() {
        super.entityInit()
        this.getDataManager().register<Float>(SPIN_PITCH, 0.0f)
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)
        this.tumbling = nbt.getFloatOr("tumbling", 0f)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        nbt.putFloat("tumbling", this.tumbling)
    }

    open fun getSpinPitch(): Float {
        return this.getDataManager().get<Float>(SPIN_PITCH)
    }

    open fun setSpinPitch(pitch: Float) {
        this.getDataManager().set<Float>(SPIN_PITCH, pitch)
    }

    override fun getTumbleAngle(partial: Float): Float {
        var angle: Float = this.tumbleAngle - partial * this.tumbling
        if (angle > 360f) {
            this.tumbleAngle -= 360f
            angle -= 360f
        }
        if (angle < 0f) {
            this.tumbleAngle += 360f
            angle += 360f
        }
        return angle
    }

    override fun getTumbleAxisX(): Float {
        val velocity2: Double = this.motionX * this.motionX + this.motionZ * this.motionZ
        if (velocity2 == 0.0) return 1f
        return (this.motionZ / MathHelper.sqrt(velocity2)).toFloat()
    }

    override fun getTumbleAxisZ(): Float {
        val velocity2: Double = this.motionX * this.motionX + this.motionZ * this.motionZ
        if (velocity2 == 0.0) return 0f
        return (this.motionX / MathHelper.sqrt(velocity2)).toFloat()
    }

    companion object {
        private val SPIN_PITCH: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntityEvolvedZombie::class.java, DataSerializers.FLOAT)
    }
}
