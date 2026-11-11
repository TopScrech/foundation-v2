package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer

open class PacketRotateRocket : PacketBase {
    private var entityID = 0
    private var entityPitch = 0f
    private var entityYaw = 0f

    constructor() : super()

    constructor(rotateableEntity: Entity) : super(GCCoreUtil.getDimensionID(rotateableEntity.level())) {
        this.entityID = rotateableEntity.getId()
        this.entityPitch = rotateableEntity.xRot
        this.entityYaw = rotateableEntity.yRot
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
        val entity: Entity? = (player)!!.level().getEntityByID(this.entityID)

        if (entity != null) {
            entity.xRot = this.entityPitch
            entity.yRot = this.entityYaw
        }
    }
}
