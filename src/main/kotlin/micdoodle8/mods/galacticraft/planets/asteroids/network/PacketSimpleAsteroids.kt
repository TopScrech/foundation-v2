package micdoodle8.mods.galacticraft.planets.asteroids.network

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketBase
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.IOException
import java.util.Arrays

open class PacketSimpleAsteroids : PacketBase {
    enum class EnumSimplePacketAsteroids
        (private val targetSide: Side, vararg decodeAs: Class<*>?) {
        // SERVER
        S_UPDATE_ADVANCED_GUI(Side.SERVER, Int::class.java, BlockPos::class.java, Int::class.java),

        // CLIENT
        C_TELEPAD_SEND(Side.CLIENT, BlockVec3::class.java, Int::class.java),
        C_UPDATE_GRAPPLE_POS(Side.CLIENT, Int::class.java, Vector3::class.java);

        private val decodeAs: Array<Class<*>?>

        init {
            this.decodeAs = (decodeAs as Array<Class<*>?>)
        }

        fun getTargetSide(): Side {
            return this.targetSide
        }

        fun getDecodeClasses(): Array<Class<*>?>? {
            return this.decodeAs
        }
    }

    private var type: EnumSimplePacketAsteroids? = null
    private var data: MutableList<Any?>? = null

    constructor() : super()

    constructor(packetType: EnumSimplePacketAsteroids, dimID: Int, data: Array<out Any?>) : this(
        packetType,
        dimID,
        Arrays.asList<Any?>(*data)
    )

    constructor(packetType: EnumSimplePacketAsteroids, dimID: Int, data: MutableList<Any?>) : super(dimID) {
        if (packetType.getDecodeClasses()!!.size != data.size) {
            GCLog.info("Asteroids Simple Packet found data length different than packet type: " + packetType.name)
        }

        this.type = packetType
        this.data = data
    }

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.type!!.ordinal)

        try {
            NetworkUtil.encodeData((buffer)!!, (this.data)!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.type = (EnumSimplePacketAsteroids.entries)!![(buffer)!!.readInt()]

        if (this.type!!.getDecodeClasses()!!.size > 0) {
            this.data = NetworkUtil.decodeData(((this.type!!.getDecodeClasses())!! as Array<Class<*>>), (buffer)!!)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun handleClientSide(player: EntityPlayer?) {
        var playerBaseClient: EntityPlayerSP? = null

        if (player is EntityPlayerSP) {
            playerBaseClient = player
        }

        var tile: TileEntity?
        var entity: Entity? = null
when (this.type) {
            EnumSimplePacketAsteroids.C_TELEPAD_SEND -> {
                entity = playerBaseClient!!.world.getEntityByID((this.data!!.get(1) as kotlin.Int?)!!)

                if (entity != null && entity is EntityLivingBase) {
                    val pos: BlockVec3 = this.data!!.get(0) as BlockVec3
                    entity.setPosition(pos.x + 0.5, pos.y + 2.2, pos.z + 0.5)
                }
            }

            EnumSimplePacketAsteroids.C_UPDATE_GRAPPLE_POS -> {
                entity = playerBaseClient!!.world.getEntityByID((this.data!!.get(0) as kotlin.Int?)!!)
                if (entity != null && entity is EntityGrapple) {
                    val vec: Vector3 = this.data!!.get(1) as Vector3
                    entity.setPosition(vec.x, vec.y, vec.z)
                }
            }

            else -> {}
        }
    }

    override fun handleServerSide(player: EntityPlayer?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        when (this.type) {
            EnumSimplePacketAsteroids.S_UPDATE_ADVANCED_GUI -> {
                val tile: TileEntity? = player!!.world.getTileEntity(this.data!!.get(1) as BlockPos?)

                when (this.data!!.get(0) as Int?) {
                    0 -> if (tile is TileEntityShortRangeTelepad) {
                        val launchController: TileEntityShortRangeTelepad = tile
                        launchController.setAddress((this.data!!.get(2) as kotlin.Int?)!!)
                    }

                    1 -> if (tile is TileEntityShortRangeTelepad) {
                        val launchController: TileEntityShortRangeTelepad = tile
                        launchController.setTargetAddress((this.data!!.get(2) as kotlin.Int?)!!)
                    }

                    else -> {}
                }
            }

            else -> {}
        }
    }
}
