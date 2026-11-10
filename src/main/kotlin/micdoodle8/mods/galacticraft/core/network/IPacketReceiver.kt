package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import java.util.ArrayList

interface IPacketReceiver {
    /**
     * Note this can be called during the init constructor of the
     * entity's superclass, if this is a subclass of the IPacketReceiver
     * entity.  So make sure any fields referenced in
     * getNetworkedData() are either in the superclass, or
     * add some null checks!!
     */
    fun getNetworkedData(sendData: ArrayList<Any?>?)

    fun decodePacketdata(buffer: ByteBuf?)
}
