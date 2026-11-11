package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import foundation.logging.FoundationLog as FMLLog
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList

open class PacketDynamic : PacketBase {
    private var type = 0
    private var data: Array<Any?>? = null
    private var sendData: ArrayList<Any?>? = null
    private var payloadData: ByteBuf? = null

    constructor() : super()

    constructor(entity: Entity) : super(GCCoreUtil.getDimensionID(entity.level())) {
        assert(entity is IPacketReceiver) { "Entity does not implement " + IPacketReceiver::class.java.getSimpleName() }
        this.type = 0
        this.data = ((arrayOf<Any>(entity.getId()) as Array<Any?>?))!!
        this.sendData = ArrayList<Any?>()
        (entity as IPacketReceiver).getNetworkedData(this.sendData)
    }

    constructor(tile: TileEntity) : super(GCCoreUtil.getDimensionID(tile.getLevel())) {
        assert(tile is IPacketReceiver) { "TileEntity does not implement " + IPacketReceiver::class.java.getSimpleName() }
        this.type = 1
        this.data = ((arrayOf<Any>(tile.getBlockPos()) as Array<Any?>?))!!
        this.sendData = ArrayList<Any?>()
        (tile as IPacketReceiver).getNetworkedData(this.sendData)
    }

    open fun isEmpty(): Boolean {
        return sendData!!.isEmpty()
    }

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.type)

        when (this.type) {
            0 -> (buffer)!!.writeInt((this.data!![0] as kotlin.Int?)!!)
            1 -> {
                (buffer)!!.writeInt((this.data!![0] as BlockPos).getX())
                (buffer)!!.writeInt((this.data!![0] as BlockPos).getY())
                (buffer)!!.writeInt((this.data!![0] as BlockPos).getZ())
            }
        }

        val payloadData: ByteBuf = Unpooled.buffer()

        try {
            NetworkUtil.encodeData(payloadData, (this.sendData)!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val readableBytes: Int = payloadData.readableBytes()
        (buffer)!!.writeInt(readableBytes)
        (buffer)!!.writeBytes(payloadData)
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.type = (buffer)!!.readInt()

        val world: World? = (GalacticraftCore.proxy)!!.getWorldForID(this.getDimensionID())

        if (world == null) {
            FMLLog.severe("Failed to get world for dimension ID: " + this.getDimensionID())
        }

        var length: Int = 0
when (this.type) {
            0 -> {
                this.data = arrayOfNulls<Any>(1)
                this.data!![0] = (buffer)!!.readInt()

                length = (buffer)!!.readInt()
                payloadData = Unpooled.copiedBuffer((buffer)!!.readBytes(length))
            }

            1 -> {
                this.data = arrayOfNulls<Any>(1)
                this.data!![0] = BlockPos((buffer)!!.readInt(), (buffer)!!.readInt(), (buffer)!!.readInt())

                length = (buffer)!!.readInt()
                payloadData = Unpooled.copiedBuffer((buffer)!!.readBytes(length))
            }
        }
    }

    override fun handleClientSide(player: EntityPlayer?) {
        this.handleData(Side.CLIENT, (player)!!)
    }

    override fun handleServerSide(player: EntityPlayer?) {
        this.handleData(Side.SERVER, (player)!!)
    }

    private fun handleData(side: Side, player: EntityPlayer) {
        when (this.type) {
            0 -> {
                val entity: Entity? = player.level().getEntityByID((this.data!![0] as kotlin.Int?)!!)

                if (entity is IPacketReceiver) {
                    if (this.payloadData!!.readableBytes() > 0) {
                        (entity as IPacketReceiver).decodePacketdata(payloadData)
                    }

                    //Treat any packet received by a server from a client as an update request specifically to that client
                    if (side == Side.SERVER && player is EntityPlayerMP && entity != null) {
                        (GalacticraftCore.packetPipeline)!!.sendTo(PacketDynamic(entity), player)
                    }
                }
            }

            1 -> if (player.level().isBlockLoaded(this.data!![0] as BlockPos?, false)) {
                val tile: TileEntity? = player.level().getBlockEntity(this.data!![0] as BlockPos?)

                if (tile is IPacketReceiver) {
                    if (this.payloadData!!.readableBytes() > 0) {
                        (tile as IPacketReceiver).decodePacketdata(payloadData)
                    }

                    //Treat any packet received by a server from a client as an update request specifically to that client
                    if (side == Side.SERVER && player is EntityPlayerMP && tile != null) {
                        (GalacticraftCore.packetPipeline)!!.sendTo(PacketDynamic(tile), player)
                    }
                }
            }
        }
    }
}
