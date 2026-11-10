package micdoodle8.mods.galacticraft.planets.mars.entities

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import java.util.LinkedList
import java.util.Random

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.entities.EntityAIArrowAttack
import micdoodle8.mods.galacticraft.core.entities.EntityBossBase
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.IRangedAttackMob
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BossInfo
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.util.*

open class EntityCreeperBoss constructor(par1World: World?) : EntityBossBase((par1World)!!), IEntityBreathable,
    IRangedAttackMob {
    @JvmField
    protected var ticks: Long = 0

    @JvmField
    var headsRemaining: Int = 3
    private var targetEntity: Entity? = null

    init {
        this.setSize(2.0f, 7.0f)
        this.isImmuneToFire = true
        this.tasks.addTask(1, EntityAISwimming(this))
        this.tasks.addTask(2, EntityAIArrowAttack(this, 1.0, 25, 20.0f))
        this.tasks.addTask(2, EntityAIWander(this, 1.0))
        this.tasks.addTask(3, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(3, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIHurtByTarget(this, false))
        this.targetTasks.addTask(
            2,
            EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer::class.java, 0, true, false, null)
        )
    }

    override fun attackEntityFrom(damageSource: DamageSource, damage: Float): Boolean {
        if (damageSource.getDamageType() == "fireball") {
            if (this.isEntityInvulnerable(damageSource)) {
                return false
            } else if (super.attackEntityFrom(damageSource, damage)) {
                val entity: Entity? = damageSource.getTrueSource()

                if (this.getPassengers().contains(entity) && this.getRidingEntity() !== entity) {
                    if (entity !== this && entity is EntityLivingBase) {
                        this.setAttackTarget(entity)
                    }

                    return true
                } else {
                    return true
                }
            } else {
                return false
            }
        }

        return false
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(200.0f * ConfigManagerCore.dungeonBossHealthMod)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05)
    }

    override fun knockBack(par1Entity: Entity, par2: Float, par3: Double, par5: Double) {
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun getAmbientSound(): SoundEvent? {
        return null
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent? {
        this.playSound(GCSounds.bossOuch, this.getSoundVolume(), this.getSoundPitch() - 0.15f)
        return null
    }

    override fun getDeathSound(): SoundEvent? {
        return null
    }

    //    @Override
    //    protected String getHurtSound()
    //    {
    //        this.playSound(Constants.TEXTURE_PREFIX + "entity.ouch", this.getSoundVolume(), this.getSoundPitch() - 0.15F);
    //        return null;
    //    }
    //
    //    @Override
    //    protected String getDeathSound()
    //    {
    //        return null;
    //    }
    override fun onDeathUpdate() {
        super.onDeathUpdate()

        if (!this.world.isRemote) {
            if (this.deathTicks == 1) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH,
                        GCCoreUtil.getDimensionID(this.world),
                        arrayOf<Any>(getSoundPitch() - 0.1f)
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

    override fun onLivingUpdate() {
        this.ticks++

        if (this.getHealth() <= 0) {
            this.headsRemaining = 0
        } else if (this.getHealth() <= this.getMaxHealth() / 3.0) {
            this.headsRemaining = 1
        } else if (this.getHealth() <= 2 * (this.getMaxHealth() / 3.0)) {
            this.headsRemaining = 2
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

    override fun getGuaranteedLoot(rand: Random?): ItemStack {
        val stackList: MutableList<ItemStack?> = LinkedList<ItemStack?>()
        stackList.addAll(GalacticraftRegistry.getDungeonLoot(2))
        var hasT3Rocket: Boolean = false
        var hasAstroMiner: Boolean = false
        // Check if player seems to have Tier 3 rocket or Astro Miner already - in that case we don't want more
        // (we don't really want him giving powerful schematics to his friends who are still on Overworld) 
        val player: EntityPlayer? = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 20.0, false)
        if (player != null) {
            val stats: GCPlayerStats? = GCPlayerStats.get(player)
            if (stats != null) {
                for (page in (stats.getUnlockedSchematics())!!) {
                    if ((page)!!.getPageID() == ConfigManagerAsteroids.idSchematicRocketT3) {
                        hasT3Rocket = true
                    } else if ((page)!!.getPageID() == ConfigManagerAsteroids.idSchematicRocketT3 + 1) {
                        hasAstroMiner = true
                    }
                }
            }
        }
        // The following code assumes the list start is hard coded to: Cargo Rocket, T3 Rocket, Astro Miner in that order
        // (see MarsModule.init())
        //
        // Remove schematics which he already has
        if (hasT3Rocket && hasAstroMiner) {
            // (but do not remove both, otherwise the list is too short)
            if (stackList.size == 3) {
                stackList.removeAt(1 + (rand)!!.nextInt(2))
            } else {
                stackList.removeAt(2)
                stackList.removeAt(1)
            }
        } else if (hasT3Rocket) {
            stackList.removeAt(1)
        } else if (hasAstroMiner) {
            stackList.removeAt(2)
        }
        // If he does not yet have the T3 rocket, limit the list size to 2 so 50% chance of getting it
        // otherwise return the full list (note: addons could have added more schematics to the list)
        val range: Int = if (!hasT3Rocket) 2 else stackList.size
        return stackList.get((rand)!!.nextInt(range))!!.copy()
    }

    override fun attackEntityWithRangedAttack(entitylivingbase: EntityLivingBase, f: Float) {
        this.world.playEvent(null, 1024, BlockPos(this), 0)
        val d3: Double = this.posX
        val d4: Double = this.posY + 5.5
        val d5: Double = this.posZ
        val d6: Double = entitylivingbase.posX - d3
        val d7: Double = entitylivingbase.posY + entitylivingbase.getEyeHeight() * 0.5 - d4
        val d8: Double = entitylivingbase.posZ - d5
        val projectileTNT: EntityProjectileTNT = EntityProjectileTNT(this.world, this, d6 * 0.5, d7 * 0.5, d8 * 0.5)

        projectileTNT.posY = d4
        projectileTNT.posX = d3
        projectileTNT.posZ = d5
        this.world.spawnEntity(projectileTNT)
    }

    override fun getChestTier(): Int {
        return 2
    }

    override fun dropKey() {
        this.entityDropItem(ItemStack(MarsItems.key, 1, 0), 0.5f)
    }

    override fun getHealthBarColor(): BossInfo.Color {
        return BossInfo.Color.YELLOW
    }

    override fun setSwingingArms(swingingArms: Boolean) {
        // TODO Auto-generated method stub
        //TODO for 1.12.2
    }
}
