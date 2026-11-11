package asmodeuscore.api.network

import io.netty.buffer.ByteBuf
import net.minecraft.world.entity.player.Player as EntityPlayer

interface IMessage {
    fun encodeInto(buffer: ByteBuf?)

    fun decodeInto(buffer: ByteBuf?)

    fun handleClientSide(player: EntityPlayer?)

    fun handleServerSide(player: EntityPlayer?)

    fun getDimensionID(): Int
}
