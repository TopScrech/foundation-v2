package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.DamageSource
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import kotlin.math.abs

open class EntityEvolvedSkeleton constructor(worldIn: World) : EntitySkeleton(worldIn), IEntityBreathable,
    ITumblable {
    private var tumbling = 0f
    private var tumbleAngle = 0f

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35)
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun jump() {
        this.motionY = 0.45 / WorldUtil.getGravityFactor(this)
        if (this.motionY < 0.24) {
            this.motionY = 0.24
        }

        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
            this.motionY += ((this.getActivePotionEffect(MobEffects.JUMP_BOOST)!!.getAmplifier() + 1) * 0.1f).toDouble()
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
        val r: Int = this.rand.nextInt(12)
        when (r) {
            0, 1, 2, 3, 4, 5 -> this.entityDropItem(ItemStack(GCBlocks.oxygenPipe), 0.0f)
            6 ->             //Oxygen tank half empty or less
                this.entityDropItem(ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f)

            7, 8 -> this.dropItem(GCItems.canister, 1)
            else -> if (ConfigManagerCore.challengeMobDropsAndSpawning) this.dropItem(Items.PUMPKIN_SEEDS, 1)
        }
    }

    override fun dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource) {
        // No loot table
        this.dropFewItems(wasRecentlyHit, lootingModifier)
        this.dropEquipment(wasRecentlyHit, lootingModifier)
    }

    override fun dropFewItems(wasRecentlyHit: Boolean, lootingModifier: Int) {
        val item: Item? = Items.BONE

        var j: Int = this.rand.nextInt(3)

        if (item != null) {
            if (lootingModifier > 0) {
                j += this.rand.nextInt(lootingModifier + 1)
            }

            for (k: Int in 1..<j) {
                this.dropItem(item, 1)
            }
        }

        j = this.rand.nextInt(3 + lootingModifier)
        if (j > 1) this.dropItem(Items.BONE, 1)

        //Drop lapis as semi-rare drop if player hit and if dropping bones
        if (wasRecentlyHit && (ConfigManagerCore.challengeMobDropsAndSpawning) && j > 1 && this.rand.nextInt(12) <= lootingModifier) this.entityDropItem(
            ItemStack(Items.DYE, 1, 4),
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
        this.tumbling = nbt.getFloat("tumbling")
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        nbt.setFloat("tumbling", this.tumbling)
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
            EntityDataManager.createKey<Float>(EntityEvolvedSkeleton::class.java, DataSerializers.FLOAT)
    }
}
