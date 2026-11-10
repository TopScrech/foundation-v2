package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf

abstract class PacketBase : IPacket {
    private var dimensionID: Int

    constructor() {
        this.dimensionID = INVALID_DIMENSION_ID
    }

    constructor(dimensionID: Int) {
        this.dimensionID = dimensionID
    }

    override fun encodeInto(buffer: ByteBuf?) {
        check(dimensionID != INVALID_DIMENSION_ID) { "Invalid Dimension ID! [GC]" }
        (buffer)!!.writeInt(this.dimensionID)
    }

    override fun decodeInto(buffer: ByteBuf?) {
        this.dimensionID = (buffer)!!.readInt()
    }

    override fun getDimensionID(): Int {
        return dimensionID
    }

    companion object {
        @JvmField
        val INVALID_DIMENSION_ID: Int = Int.MIN_VALUE + 12
    }
}
