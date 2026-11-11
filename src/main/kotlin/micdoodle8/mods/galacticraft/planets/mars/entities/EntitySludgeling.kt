package micdoodle8.mods.galacticraft.planets.mars.entities

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.world.level.block.Block
import net.minecraft.entity.EnumCreatureAttribute
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIAttackMelee
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.entity.monster.EntityMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Item
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.sounds.SoundEvent
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class EntitySludgeling constructor(par1World: World) : EntityMob(par1World), IEntityBreathable {
    init {
        this.setSize(0.3f, 0.2f)
        this.tasks.taskEntries.clear()
        this.targetTasks.taskEntries.clear()
        this.tasks.addTask(1, EntityAIAttackMelee(this, 0.25, true))
        this.tasks.addTask(4, EntityAIAttackMelee(this, 1.0, false))
        this.targetTasks.addTask(
            1,
            EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, 0, false, true, null)
        )
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityEvolvedZombie>(
                this,
                EntityEvolvedZombie::class.java,
                0,
                false,
                true,
                null
            )
        )
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityEvolvedSkeleton>(
                this,
                EntityEvolvedSkeleton::class.java,
                0,
                false,
                true,
                null
            )
        )
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityEvolvedSpider>(
                this,
                EntityEvolvedSpider::class.java,
                0,
                false,
                true,
                null
            )
        )
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityEvolvedCreeper>(
                this,
                EntityEvolvedCreeper::class.java,
                0,
                false,
                true,
                null
            )
        )
        this.targetTasks.addTask(
            3,
            EntityAINearestAttackableTarget<EntitySlimeling>(this, EntitySlimeling::class.java, 200, false, true, null)
        )
    }

    override fun canBreatheUnderwater(): Boolean {
        return true
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(7.0)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0)
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_SILVERFISH_AMBIENT
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_SILVERFISH_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_SILVERFISH_DEATH
    }

    open fun getClosestEntityToAttack(par1: Double, par3: Double, par5: Double, par7: Double): EntityPlayer? {
        var var9: Double = -1.0
        var var11: EntityPlayer? = null

        for (var12: Int in this.world.loadedEntityList.indices) {
            val var13: EntityPlayer = this.world.loadedEntityList.get(var12) as EntityPlayer
            val var14: Double = var13.getDistanceSq(par1, par3, par5)

            if ((par7 < 0.0 || var14 < par7 * par7) && (var9 == -1.0 || var14 < var9)) {
                var9 = var14
                var11 = var13
            }
        }

        return var11
    }

    override fun playStepSound(pos: BlockPos, block: Block) {
        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15f, 1.0f)
    }

    override fun getDropItem(): Item? {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun onUpdate() {
        this.renderYawOffset = this.rotationYaw
        super.onUpdate()
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
}
