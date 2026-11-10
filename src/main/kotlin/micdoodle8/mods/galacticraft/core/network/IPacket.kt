package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer

interface IPacket {
    fun encodeInto(buffer: ByteBuf?)

    fun decodeInto(buffer: ByteBuf?)

    fun handleClientSide(player: EntityPlayer?)

    fun handleServerSide(player: EntityPlayer?)

    fun getDimensionID(): Int
}
