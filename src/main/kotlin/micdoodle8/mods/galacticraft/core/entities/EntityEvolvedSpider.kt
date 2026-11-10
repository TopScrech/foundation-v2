package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.entity.IEntityLivingData
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.EnumDifficulty
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

open class EntityEvolvedSpider constructor(par1World: World) : EntitySpider(par1World), IEntityBreathable {
    init {
        this.setSize(1.5f, 1.0f)
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(22.0)
        var difficulty: Double = 0.0
        when (this.world.getDifficulty()) {
            EnumDifficulty.HARD -> difficulty = 2.0
            EnumDifficulty.NORMAL -> difficulty = 1.0
            else -> {}
        }
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3 + 0.05 * difficulty)
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0 + difficulty)
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun onInitialSpawn(difficulty: DifficultyInstance, livingdata: IEntityLivingData?): IEntityLivingData? {
        var livingdata: IEntityLivingData? = livingdata
        livingdata = super.onInitialSpawn(difficulty, livingdata)

        if (this.world.rand.nextInt(100) == 0) {
            val entityskeleton: EntityEvolvedSkeleton = EntityEvolvedSkeleton(this.world)
            entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f)
            entityskeleton.onInitialSpawn(difficulty, null as IEntityLivingData?)
            this.world.spawnEntity(entityskeleton)
            entityskeleton.startRiding(this)
        }

        if (livingdata == null) {
            livingdata = GroupData()

            if (this.world.getDifficulty() == EnumDifficulty.HARD && this.world.rand.nextFloat() < 0.1f * difficulty.getClampedAdditionalDifficulty()) {
                livingdata.setRandomEffect(this.world.rand)
            }
        }

        if (livingdata is GroupData) {
            val potion: Potion? = livingdata.effect

            if (potion != null) {
                this.addPotionEffect(PotionEffect(potion, Int.MAX_VALUE))
            }
        }

        return livingdata
    }

    override fun jump() {
        this.motionY = 0.52 / WorldUtil.getGravityFactor(this)

        if (this.motionY < 0.26) {
            this.motionY = 0.26
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
        when (this.rand.nextInt(14)) {
            0, 1, 2 -> this.dropItem(GCItems.cheeseCurd, 1)
            3, 4, 5 -> this.dropItem(Items.FERMENTED_SPIDER_EYE, 1)
            6, 7 ->             //Oxygen tank half empty or less
                this.entityDropItem(ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f)

            8 -> this.dropItem(GCItems.oxygenGear, 1)
            9 -> this.dropItem(GCItems.oxygenConcentrator, 1)
            else -> if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                this.dropItem(Items.NETHER_WART, 1)
            }
        }
    }

    override fun dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource) {
        super.dropLoot(wasRecentlyHit, if (lootingModifier > 1) lootingModifier - 1 else 0, source)
        if (wasRecentlyHit && this.rand.nextFloat() < 0.025f + lootingModifier.toFloat() * 0.02f) {
            this.addRandomDrop()
        }
    }
}
