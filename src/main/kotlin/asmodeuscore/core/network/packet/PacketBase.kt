package asmodeuscore.core.network.packet

import io.netty.buffer.ByteBuf
import asmodeuscore.api.network.IMessage

abstract class PacketBase : IMessage {
    private var dimensionID: Int

    constructor() {
        this.dimensionID = INVALID_DIMENSION_ID
    }

    constructor(dimensionID: Int) {
        this.dimensionID = dimensionID
    }

    override fun encodeInto(buffer: ByteBuf?) {
        check(dimensionID != INVALID_DIMENSION_ID) { "Invalid Dimension ID! [AC]" }
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
