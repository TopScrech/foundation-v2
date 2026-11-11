package micdoodle8.mods.galacticraft.core.tile

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.entities.IBoss
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.lang.Exception
import java.lang.reflect.Constructor
import java.util.ArrayList

open class TileEntityDungeonSpawner<E : Entity> : TileEntityAdvanced {
    @JvmField
    var bossClass: Class<E>? = null

    @JvmField
    var boss: IBoss? = null

    @JvmField
    var spawned: Boolean = false

    @JvmField
    var isBossDefeated: Boolean = false

    @JvmField
    var playerInRange: Boolean = false

    @JvmField
    var lastPlayerInRange: Boolean = false
    private var roomCoords: Vector3? = null
    private var roomSize: Vector3? = null

    @JvmField
    var lastKillTime: Long = 0
    private var chestPos: BlockPos? = null
    private var range15: AxisAlignedBB? = null
    private var rangeBounds: AxisAlignedBB? = null
    private var rangeBoundsPlus3: AxisAlignedBB? = null
    private var rangeBoundsPlus11: AxisAlignedBB? = null

    constructor()

    constructor(bossClass: Class<E>?) {
        this.bossClass = bossClass
    }

    override fun update() {
        super.update()

        if (this.roomCoords == null) {
            return
        }

        if (!this.world.isRemote) {
            if (this.range15 == null) {
                val thisVec: Vector3 = Vector3(this)
                this.range15 = AxisAlignedBB(
                    thisVec.x - 15,
                    thisVec.y - 15,
                    thisVec.z - 15,
                    thisVec.x + 15,
                    thisVec.y + 15,
                    thisVec.z + 15
                )
                this.rangeBounds = AxisAlignedBB(
                    this.roomCoords!!.intX().toDouble(),
                    this.roomCoords!!.intY().toDouble(),
                    this.roomCoords!!.intZ().toDouble(),
                    (this.roomCoords!!.intX() + this.roomSize!!.intX()).toDouble(),
                    (this.roomCoords!!.intY() + this.roomSize!!.intY()).toDouble(),
                    (this.roomCoords!!.intZ() + this.roomSize!!.intZ()).toDouble()
                )
                this.rangeBoundsPlus3 = this.rangeBounds!!.grow(3.0, 3.0, 3.0)
            }

            if (this.lastKillTime > 0 && MinecraftServer.getCurrentTimeMillis() - lastKillTime > 900000)  // 15 minutes
            {
                this.lastKillTime = 0
                this.isBossDefeated = false
                //After 15 minutes a new boss is able to be spawned 
            }

            val l: MutableList<E?> = this.world.getEntitiesWithinAABB<E?>(bossClass, this.range15)

            for (e in l) {
                if (!(e)!!.isDead) {
                    this.boss = e as IBoss
                    this.spawned = true
                    this.isBossDefeated = false
                    this.boss!!.onBossSpawned(this)
                }
            }

            val entitiesWithin: MutableList<EntityMob> =
                this.world.getEntitiesWithinAABB<EntityMob>(EntityMob::class.java, this.rangeBoundsPlus3)

            for (mob: Entity in entitiesWithin) {
                if ((this.getDisabledCreatures() as Collection<Any?>).contains(mob.javaClass)) {
                    mob.setDead()
                }
            }

            val playersWithin: MutableList<EntityPlayer> =
                this.world.getEntitiesWithinAABB<EntityPlayer>(EntityPlayer::class.java, this.rangeBounds)

            this.playerInRange = !playersWithin.isEmpty()

            if (this.playerInRange) {
                if (!this.lastPlayerInRange && !this.spawned) {
                    //Try to create a boss entity
                    if (this.boss == null && !this.isBossDefeated) {
                        try {
                            val c: Constructor<*> = this.bossClass!!.getConstructor(World::class.java)
                            this.boss = c.newInstance(this.world) as IBoss?
                            (this.boss as Entity).setPosition(
                                this.getPos().getX() + 0.5,
                                this.getPos().getY() + 1.0,
                                this.getPos().getZ() + 0.5
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    //Now spawn the boss
                    if (this.boss != null) {
                        if (this.boss is EntityLiving) {
                            val bossLiving: EntityLiving = this.boss as EntityLiving
                            bossLiving.onInitialSpawn(this.world.getDifficultyForLocation(BlockPos(bossLiving)), null)
                            this.world.spawnEntity(bossLiving)
                            this.playSpawnSound(bossLiving)
                            this.spawned = true
                        }
                    }
                }
            }

            this.lastPlayerInRange = this.playerInRange
        }
    }

    open fun playSpawnSound(entity: Entity?) {
    }

    open fun getDisabledCreatures(): MutableList<Class<out EntityLiving>?> {
        val list: MutableList<Class<out EntityLiving>?> = ArrayList<Class<out EntityLiving>?>()
        list.add(EntityEvolvedSkeleton::class.java)
        list.add(EntityEvolvedCreeper::class.java)
        list.add(EntityEvolvedZombie::class.java)
        list.add(EntityEvolvedSpider::class.java)
        return list
    }

    open fun setRoom(coords: Vector3?, size: Vector3?) {
        this.roomCoords = coords
        this.roomSize = size
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.lastPlayerInRange = nbt.getBooleanOr("playerInRange", false)
        this.playerInRange = this.lastPlayerInRange
        this.isBossDefeated = nbt.getBooleanOr("defeated", false)

        try {
            this.bossClass = Class.forName(nbt.getStringOr("bossClass", "")) as Class<E>
        } catch (e: Exception) {
            // This exception will be thrown when read is called from TileEntity.handleUpdateTag
            // but we only care if an exception is thrown on server side read
            if (!this.world.isRemote) {
                e.printStackTrace()
            }
        }

        this.roomCoords = Vector3()
        this.roomCoords!!.x = nbt.getDoubleOr("roomCoordsX", 0.0)
        this.roomCoords!!.y = nbt.getDoubleOr("roomCoordsY", 0.0)
        this.roomCoords!!.z = nbt.getDoubleOr("roomCoordsZ", 0.0)
        this.roomSize = Vector3()
        this.roomSize!!.x = nbt.getDoubleOr("roomSizeX", 0.0)
        this.roomSize!!.y = nbt.getDoubleOr("roomSizeY", 0.0)
        this.roomSize!!.z = nbt.getDoubleOr("roomSizeZ", 0.0)

        if (nbt.containsTag("lastKillTime")) {
            this.lastKillTime = nbt.getLongOr("lastKillTime", 0L)
        } else if (nbt.containsTag("lastKillTimeNew")) {
            val savedTime: Long = nbt.getLongOr("lastKillTimeNew", 0L)
            this.lastKillTime = if (savedTime == 0L) 0 else savedTime + MinecraftServer.getCurrentTimeMillis()
        }


        if (nbt.containsTag("chestPosNull") && !nbt.getBooleanOr("chestPosNull", false)) {
            this.chestPos = BlockPos(nbt.getIntOr("chestX", 0), nbt.getIntOr("chestY", 0), nbt.getIntOr("chestZ", 0))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.putBoolean("playerInRange", this.playerInRange)
        nbt.putBoolean("defeated", this.isBossDefeated)
        nbt.putString("bossClass", this.bossClass!!.getCanonicalName())

        if (this.roomCoords != null) {
            nbt.putDouble("roomCoordsX", this.roomCoords!!.x)
            nbt.putDouble("roomCoordsY", this.roomCoords!!.y)
            nbt.putDouble("roomCoordsZ", this.roomCoords!!.z)
            nbt.putDouble("roomSizeX", this.roomSize!!.x)
            nbt.putDouble("roomSizeY", this.roomSize!!.y)
            nbt.putDouble("roomSizeZ", this.roomSize!!.z)
        }

        nbt.putLong(
            "lastKillTimeNew",
            if (this.lastKillTime == 0L) 0 else this.lastKillTime - MinecraftServer.getCurrentTimeMillis()
        )

        nbt.putBoolean("chestPosNull", this.chestPos == null)
        if (this.chestPos != null) {
            nbt.putInt("chestX", this.chestPos!!.getX())
            nbt.putInt("chestY", this.chestPos!!.getY())
            nbt.putInt("chestZ", this.chestPos!!.getZ())
        }
        return nbt
    }

    override fun getPacketRange(): Double {
        return 0.0
    }

    override fun getPacketCooldown(): Int {
        return 0
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }

    open fun getChestPos(): BlockPos? {
        return chestPos
    }

    open fun setChestPos(chestPos: BlockPos?) {
        this.chestPos = chestPos
    }

    open fun getRangeBounds(): AxisAlignedBB {
        if (this.rangeBounds == null) this.rangeBounds = AxisAlignedBB(
            this.roomCoords!!.intX().toDouble(),
            this.roomCoords!!.intY().toDouble(),
            this.roomCoords!!.intZ().toDouble(),
            (this.roomCoords!!.intX() + this.roomSize!!.intX()).toDouble(),
            (this.roomCoords!!.intY() + this.roomSize!!.intY()).toDouble(),
            (this.roomCoords!!.intZ() + this.roomSize!!.intZ()).toDouble()
        )

        return this.rangeBounds!!
    }

    open fun getRangeBoundsPlus11(): AxisAlignedBB {
        if (this.rangeBoundsPlus11 == null) this.rangeBoundsPlus11 = this.getRangeBounds().grow(11.0, 11.0, 11.0)

        return this.rangeBoundsPlus11!!
    }
}
