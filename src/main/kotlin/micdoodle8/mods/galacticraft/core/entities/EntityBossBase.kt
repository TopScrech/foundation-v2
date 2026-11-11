package micdoodle8.mods.galacticraft.core.entities

import net.minecraft.network.chat.Component

import java.util.Random

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.item.EntityXPOrb
import net.minecraft.entity.monster.EntityMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumParticleTypes

import net.minecraft.world.BossInfo
import net.minecraft.world.BossInfoServer
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.util.*

abstract class EntityBossBase(world: World) : EntityMob(world), IBoss {
    @JvmField
    protected var spawner: TileEntityDungeonSpawner<*>? = null

    @JvmField
    var deathTicks: Int = 0

    @JvmField
    var entitiesWithin: Int = 0

    @JvmField
    var entitiesWithinLast: Int = 0

    private val bossInfo = (BossInfoServer(this.getDisplayName(), getHealthBarColor(), BossInfo.Overlay.PROGRESS))

    abstract fun getChestTier(): Int

    abstract fun getGuaranteedLoot(rand: Random?): ItemStack

    abstract fun dropKey()

    abstract fun getHealthBarColor(): BossInfo.Color?

    override fun updateAITasks() {
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth())
        super.updateAITasks()
    }

    override fun onDeathUpdate() {
        ++this.deathTicks

        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            val x: Float = (this.rand.nextFloat() - 0.5f) * this.width
            val y: Float = (this.rand.nextFloat() - 0.5f) * (this.height / 2.0f)
            val z: Float = (this.rand.nextFloat() - 0.5f) * this.width
            this.world.spawnParticle(
                EnumParticleTypes.EXPLOSION_HUGE,
                this.posX + x,
                this.posY + 2.0 + y,
                this.posZ + z,
                0.0,
                0.0,
                0.0
            )
        }

        var i: Int
        var j: Int

        if (!this.world.isRemote) {
            if (this.deathTicks >= 180 && this.deathTicks % 5 == 0) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_PLAY_SOUND_EXPLODE,
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

            if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
                i = 30

                while (i > 0) {
                    j = EntityXPOrb.getXPSplit(i)
                    i -= j
                    this.world.spawnEntity(EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j))
                }
            }
        }

        if (this.deathTicks == 200 && !this.world.isRemote) {
            i = 20

            while (i > 0) {
                j = EntityXPOrb.getXPSplit(i)
                i -= j
                this.world.spawnEntity(EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j))
            }

            var chest: TileEntityTreasureChest? = null

            if (this.spawner != null && this.spawner!!.getChestPos() != null) {
                val chestTest: TileEntity? = this.world.getBlockEntity(this.spawner!!.getChestPos())

                if (chestTest != null && chestTest is TileEntityTreasureChest) {
                    chest = chestTest
                }
            }

            if (chest == null) {
                // Fallback to finding closest chest
                chest = TileEntityTreasureChest.findClosest(this, this.getChestTier())
            }

            if (chest != null) {
                val dist: Double = this.getDistanceSq(
                    chest.getPos().getX() + 0.5,
                    chest.getPos().getY() + 0.5,
                    chest.getPos().getZ() + 0.5
                )
                if (dist < 1000 * 1000) {
                    if (!chest.locked) {
                        chest.locked = true
                    }

                    for (k: Int in 0..<chest.getContainerSize()) {
                        chest.setItem(k, ItemStack.EMPTY)
                    }

                    chest.fillWithLoot(null)

                    //                    ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
//
//                    // Generate twice, since it's an extra special chest
//                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
//                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
                    val schematic: ItemStack = this.getGuaranteedLoot(this.rand)
                    val slot: Int = this.rand.nextInt(chest.getContainerSize())
                    chest.setItem(slot, schematic)
                }
            }

            this.dropKey()

            super.setDead()

            if (this.spawner != null) {
                //Note: spawner.isBossDefeated is true, so it's properly dead
                (this.spawner)!!.isBossDefeated = true
                (this.spawner)!!.boss = null
                (this.spawner)!!.spawned = false

                if (!this.world.isRemote) {
                    (this.spawner)!!.lastKillTime = MinecraftServer.getCurrentTimeMillis()
                }
            }
        }
    }

    override fun onLivingUpdate() {
        if (this.spawner != null) {
            val playersWithin: MutableList<EntityPlayer> = this.world.getEntitiesWithinAABB<EntityPlayer>(
                EntityPlayer::class.java,
                this.spawner!!.getRangeBounds()
            )

            this.entitiesWithin = playersWithin.size

            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0) {
                val entitiesWithin2: MutableList<EntityPlayer> = this.world.getEntitiesWithinAABB<EntityPlayer>(
                    EntityPlayer::class.java,
                    this.spawner!!.getRangeBoundsPlus11()
                )

                for (p: EntityPlayer in entitiesWithin2) {
                    p.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.skeleton_boss.message")))
                }

                this.setDead()

                //Note: spawner.isBossDefeated is false, so the boss will respawn if any player comes back inside the room
                return
            }

            this.entitiesWithinLast = this.entitiesWithin
        }

        super.onLivingUpdate()
    }

    override fun setDead() {
        if (this.spawner != null) {
            (this.spawner)!!.isBossDefeated = false
            (this.spawner)!!.boss = null
            (this.spawner)!!.spawned = false
        }

        super.setDead()
    }

    override fun onBossSpawned(spawner: TileEntityDungeonSpawner<*>?) {
        this.spawner = spawner
    }

    override fun addTrackingPlayer(player: EntityPlayerMP) {
        super.addTrackingPlayer(player)
        this.bossInfo.addPlayer(player)
    }

    override fun removeTrackingPlayer(player: EntityPlayerMP) {
        super.removeTrackingPlayer(player)
        this.bossInfo.removePlayer(player)
    }
}
