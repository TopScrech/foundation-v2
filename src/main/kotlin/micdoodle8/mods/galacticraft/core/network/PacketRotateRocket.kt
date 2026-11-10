package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer

open class PacketRotateRocket : PacketBase {
    private var entityID = 0
    private var entityPitch = 0f
    private var entityYaw = 0f

    constructor() : super()

    constructor(rotateableEntity: Entity) : super(GCCoreUtil.getDimensionID(rotateableEntity.world)) {
        this.entityID = rotateableEntity.getEntityId()
        this.entityPitch = rotateableEntity.rotationPitch
        this.entityYaw = rotateableEntity.rotationYaw
    }

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.entityID)
        (buffer)!!.writeFloat(this.entityPitch)
        (buffer)!!.writeFloat(this.entityYaw)
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.entityID = (buffer)!!.readInt()
        this.entityPitch = (buffer)!!.readFloat()
        this.entityYaw = (buffer)!!.readFloat()
    }

    override fun handleClientSide(player: EntityPlayer?) {
    }

    override fun handleServerSide(player: EntityPlayer?) {
        val entity: Entity? = (player)!!.world.getEntityByID(this.entityID)

        if (entity != null) {
            entity.rotationPitch = this.entityPitch
            entity.rotationYaw = this.entityYaw
        }
    }
}
