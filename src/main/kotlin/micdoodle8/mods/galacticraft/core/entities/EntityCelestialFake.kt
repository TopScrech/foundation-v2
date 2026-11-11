package micdoodle8.mods.galacticraft.core.entities

import foundation.persistence.containsTag

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.particle.Particle
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import java.util.*

open class EntityCelestialFake constructor(var1: World?) : EntityAdvancedMotion(var1), IIgnoreShift {
    private var lastShouldMove = false
    private var persistantRiderUUID: UUID? = null
    private var shouldMoveClient: Boolean? = null
    private var shouldMoveServer: Boolean? = null
    private var hasReceivedPacket = false
    private var prevData: ArrayList<*>? = null
    private var networkDataChanged = false

    init {
        this.setSize(3.0f, 1.0f)
    }

    override fun shouldSendAdvancedMotionPacket(): Boolean {
        return this.shouldMoveClient != null && this.shouldMoveServer != null
    }

    override fun canSetPositionClient(): Boolean {
        return this.shouldSendAdvancedMotionPacket()
    }

    constructor(var1: World?, var2: Double, var4: Double, var6: Double) : this(var1) {
        this.setPosition(var2, var4, var6)
    }

    constructor(player: EntityPlayerMP) : this(player.level(), player.x, player.y, player.z) {
        this.setPositionAndRotation(player.x, player.y, player.z, 0f, 0f)

        player.startRiding(this, true)
    }

    override fun onUpdate() {
        if (this.getPassengers().isEmpty()) {
            this.setDead()
            return
        }
        super.onUpdate()

        if (this.ticks < 40 && this.posY > 150) {
            if (this.getPassengers().isEmpty()) {
                val player: EntityPlayer? = this.world.getClosestPlayerToEntity(this, 5.0)

                if (player != null && player.getVehicle() == null) {
                    player.startRiding(this, true)
                }
            }
        }

        val box: AxisAlignedBB = this.getEntityBoundingBox().grow(0.2, 0.4, 0.2)

        val var15: MutableList<Entity> = this.world.getEntitiesWithinAABBExcludingEntity(this, box)

        if (var15 != null && !var15.isEmpty()) {
            for (entity: Entity in var15) {
                if (!this.getPassengers().contains(entity)) {
                    this.pushEntityAway(entity)
                }
            }
        }
    }

    private fun pushEntityAway(entityToPush: Entity) {
        if (!this.getPassengers().contains(entityToPush) && this.getRidingEntity() !== entityToPush) {
            var d0: Double = this.posX - entityToPush.x
            var d1: Double = this.posZ - entityToPush.z
            var d2: Double = MathHelper.absMax(d0, d1)

            if (d2 >= 0.009999999776482582) {
                d2 = MathHelper.sqrt(d2).toDouble()
                d0 /= d2
                d1 /= d2
                var d3: Double = 1.0 / d2

                if (d3 > 1.0) {
                    d3 = 1.0
                }

                d0 *= d3
                d1 *= d3
                d0 *= 0.05000000074505806
                d1 *= 0.05000000074505806
                d0 *= (1.0f - entityToPush.entityCollisionReduction).toDouble()
                d1 *= (1.0f - entityToPush.entityCollisionReduction).toDouble()
                entityToPush.addVelocity(-d0, 0.0, -d1)
            }
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        if (nbt.containsTag("RiderUUID_LSB")) {
            this.persistantRiderUUID = UUID(nbt.getLongOr("RiderUUID_LSB", 0L), nbt.getLongOr("RiderUUID_MSB", 0L))
        }
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        val nbttaglist: NBTTagList = NBTTagList()

        val id: UUID? = this.getOwnerUUID()

        if (id != null) {
            nbt.putLong("RiderUUID_LSB", id.getLeastSignificantBits())
            nbt.putLong("RiderUUID_MSB", id.getMostSignificantBits())
        }
    }

    override fun shouldMove(): Boolean {
        return false
    }

    override fun tickInAir() {
        if (this.world.isRemote) {
            this.motionZ = 0.0
            this.motionX = this.motionZ
            this.motionY = this.motionX

            this.lastShouldMove = false
        }
    }

    override fun getNetworkedData(): ArrayList<Any?> {
        val objList: ArrayList<Any?> = ArrayList<Any?>()

        if (this.world.isRemote) {
            this.shouldMoveClient = this.shouldMove()
            objList.add(this.shouldMoveClient)
        } else {
            this.shouldMoveServer = this.shouldMove()
            objList.add(this.shouldMoveServer)
            //Server send rider information for client to check
            objList.add(if (this.getPassengers().isEmpty()) -1 else this.getPassengers().get(0).getId())
        }

        this.networkDataChanged = objList != this.prevData
        this.prevData = objList
        return objList
    }

    override fun networkedDataChanged(): Boolean {
        return this.networkDataChanged
    }

    override fun canRiderInteract(): Boolean {
        return true
    }

    override fun getPacketTickSpacing(): Int {
        return 2
    }

    override fun getPacketSendDistance(): Double {
        return 500.0
    }

    override fun readNetworkedData(buffer: ByteBuf?) {
        try {
            if (this.world.isRemote) {
                this.hasReceivedPacket = true
                this.shouldMoveServer = (buffer)!!.readBoolean()

                //Check has correct rider on client
                val shouldBeMountedId: Int = (buffer)!!.readInt()
                if (this.getPassengers().isEmpty()) {
                    if (shouldBeMountedId > -1) {
                        val e: Entity? = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId)
                        if (e != null) {
                            e.startRiding(this, true)
                        }
                    }
                } else if (this.getPassengers().get(0).getId() != shouldBeMountedId) {
                    if (shouldBeMountedId == -1) {
                        this.removePassengers()
                    } else {
                        val e: Entity? = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId)
                        if (e != null) {
                            e.startRiding(this, true)
                        }
                    }
                }
            } else {
                this.shouldMoveClient = (buffer)!!.readBoolean()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun allowDamageSource(damageSource: DamageSource?): Boolean {
        return !(damageSource)!!.isExplosion()
    }

    override fun getItemsDropped(): MutableList<ItemStack?>? {
        return null
    }

    override fun canPlaceItem(var1: Int, var2: ItemStack): Boolean {
        return false
    }

    override fun getPacketRange(): Double {
        return 50.0
    }

    override fun getOwnerUUID(): UUID? {
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) !is EntityPlayer) {
            return null
        }

        val id: UUID

        if (!this.getPassengers().isEmpty()) {
            id = this.getPassengers().get(0).getPersistentID()

            this.persistantRiderUUID = id
        } else {
            id = this.persistantRiderUUID!!
        }

        return id
    }

    override fun pressKey(key: Int): Boolean {
        return false
    }

    override fun getContainerSize(): Int {
        return 0
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.literal("")
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun shouldSpawnParticles(): Boolean {
        return false
    }

    override fun getParticleMap(): MutableMap<Vector3?, Vector3?>? {
        return null
    }

    override fun getParticle(
        rand: Random?, x: Double, y: Double, z: Double,
        motX: Double, motY: Double, motZ: Double
    ): Particle? {
        return null
    }

    override fun tickOnGround() {
        this.tickInAir()
    }

    override fun onGroundHit() {
    }

    override fun getMotionVec(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun shouldIgnoreShiftExit(): Boolean {
        return true
    }
}
