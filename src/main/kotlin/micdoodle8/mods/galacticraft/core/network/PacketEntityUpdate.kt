package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import java.util.UUID
import micdoodle8.mods.galacticraft.api.vector.Vector2
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer

open class PacketEntityUpdate : PacketBase {
    private var entityID = 0
    private var position: Vector3? = null
    private var rotationYaw = 0f
    private var rotationPitch = 0f
    private var motion: Vector3? = null
    private var onGround = false

    constructor() : super()

    constructor(
        entityID: Int,
        position: Vector3?,
        rotation: Vector2,
        motion: Vector3?,
        onGround: Boolean,
        dimID: Int
    ) : super(dimID) {
        this.entityID = entityID
        this.position = position
        this.rotationYaw = rotation.x.toFloat()
        this.rotationPitch = rotation.y.toFloat()
        this.motion = motion
        this.onGround = onGround
    }

    constructor(entity: Entity) : this(
        entity.getId(),
        Vector3(entity.x, entity.y, entity.z),
        Vector2(entity.yRot.toDouble(), entity.xRot.toDouble()),
        Vector3(entity.deltaMovement.x, entity.deltaMovement.y, entity.deltaMovement.z),
        entity.onGround,
        GCCoreUtil.getDimensionID(entity.level())
    )

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.entityID)
        (buffer)!!.writeDouble(this.position!!.x)
        (buffer)!!.writeDouble(this.position!!.y)
        (buffer)!!.writeDouble(this.position!!.z)
        (buffer)!!.writeFloat(this.rotationYaw)
        (buffer)!!.writeFloat(this.rotationPitch)
        (buffer)!!.writeDouble(this.motion!!.x)
        (buffer)!!.writeDouble(this.motion!!.y)
        (buffer)!!.writeDouble(this.motion!!.z)
        (buffer)!!.writeBoolean(this.onGround)
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.entityID = (buffer)!!.readInt()
        this.position = Vector3((buffer)!!.readDouble(), (buffer)!!.readDouble(), (buffer)!!.readDouble())
        this.rotationYaw = (buffer)!!.readFloat()
        this.rotationPitch = (buffer)!!.readFloat()
        this.motion = Vector3((buffer)!!.readDouble(), (buffer)!!.readDouble(), (buffer)!!.readDouble())
        this.onGround = (buffer)!!.readBoolean()
    }

    override fun handleClientSide(player: EntityPlayer?) {
        this.setEntityData((player)!!)
    }

    override fun handleServerSide(player: EntityPlayer?) {
        this.setEntityData((player)!!)
    }

    private fun setEntityData(player: EntityPlayer) {
        val entity: Entity? = player.level().getEntityByID(this.entityID)

        if (entity is IEntityFullSync) {
            if (player.level().isRemote || player.getUUID() == (entity as IEntityFullSync).getOwnerUUID() || (entity as IEntityFullSync).getOwnerUUID() == null) {
                val controllable: IEntityFullSync = entity as IEntityFullSync
                controllable.setPositionRotationAndMotion(
                    this.position!!.x,
                    this.position!!.y,
                    this.position!!.z,
                    this.rotationYaw,
                    this.rotationPitch,
                    this.motion!!.x,
                    this.motion!!.y,
                    this.motion!!.z,
                    this.onGround
                )
            }
        }
    }

    interface IEntityFullSync {
        fun setPositionRotationAndMotion(
            x: Double,
            y: Double,
            z: Double,
            yaw: Float,
            pitch: Float,
            motX: Double,
            motY: Double,
            motZ: Double,
            onGround: Boolean
        )

        fun getOwnerUUID(): UUID?
    }
}
