package asmodeuscore.api.network

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer

interface IMessage {
    fun encodeInto(buffer: ByteBuf?)

    fun decodeInto(buffer: ByteBuf?)

    fun handleClientSide(player: EntityPlayer?)

    fun handleServerSide(player: EntityPlayer?)

    fun getDimensionID(): Int
}
