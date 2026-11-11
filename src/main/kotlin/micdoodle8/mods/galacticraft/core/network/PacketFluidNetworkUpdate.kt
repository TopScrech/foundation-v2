package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.api.transmission.tile.IBufferTransmitter
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.network.ByteBufUtils

open class PacketFluidNetworkUpdate : PacketBase {
    private var type: PacketType? = null
    private var pos: BlockPos? = null

    private var stack: FluidStack? = null
    private var fluidType: Fluid? = null
    private var didTransfer = false

    private var newNetwork = false
    private var transmittersAdded: MutableCollection<IBufferTransmitter<FluidStack?>?>? = null
    private var transmittersCoords: MutableCollection<BlockPos>? = null

    constructor()

    private constructor(
        type: PacketType,
        dimensionID: Int,
        pos: BlockPos?,
        stack: FluidStack?,
        didTransfer: Boolean
    ) : super(dimensionID) {
        this.type = type
        this.pos = pos
        this.stack = stack
        this.didTransfer = didTransfer
    }

    private constructor(
        type: PacketType,
        dimensionID: Int,
        pos: BlockPos?,
        newNetwork: Boolean,
        transmittersAdded: MutableCollection<IBufferTransmitter<FluidStack?>?>?
    ) : super(dimensionID) {
        this.type = type
        this.pos = pos
        this.newNetwork = newNetwork
        this.transmittersAdded = transmittersAdded
    }

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.pos!!.getX())
        (buffer)!!.writeInt(this.pos!!.getY())
        (buffer)!!.writeInt(this.pos!!.getZ())
        (buffer)!!.writeInt(this.type!!.ordinal)

        when (this.type) {
            PacketType.ADD_TRANSMITTER -> {
                (buffer)!!.writeBoolean(this.newNetwork)
                (buffer)!!.writeInt(this.transmittersAdded!!.size)

                for (transmitter: IBufferTransmitter<*>? in this.transmittersAdded!!) {
                    val tile: TileEntity = transmitter as TileEntity
                    (buffer)!!.writeInt(tile.getBlockPos().getX())
                    (buffer)!!.writeInt(tile.getBlockPos().getY())
                    (buffer)!!.writeInt(tile.getBlockPos().getZ())
                }
            }

            PacketType.FLUID -> {
                if (this.stack != null) {
                    (buffer)!!.writeBoolean(true)
                    ByteBufUtils.writeUTF8String(buffer, FluidRegistry.getFluidName(this.stack))
                    (buffer)!!.writeInt(this.stack!!.amount)
                } else {
                    (buffer)!!.writeBoolean(false)
                }

                (buffer)!!.writeBoolean(this.didTransfer)
            }
        
else -> {}
}
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.pos = BlockPos((buffer)!!.readInt(), (buffer)!!.readInt(), (buffer)!!.readInt())
        this.type = (PacketType.entries)!![buffer.readInt()]

        when (this.type) {
            PacketType.ADD_TRANSMITTER -> {
                this.newNetwork = (buffer)!!.readBoolean()
                this.transmittersCoords = Sets.newHashSet<BlockPos?>()
                val transmitterCount: Int = (buffer)!!.readInt()

                var i: Int = 0
                while (i < transmitterCount) {
                    this.transmittersCoords!!.add(BlockPos((buffer)!!.readInt(), (buffer)!!.readInt(), (buffer)!!.readInt()))
                    ++i
                }
            }

            PacketType.FLUID -> {
                if ((buffer)!!.readBoolean()) {
                    this.fluidType = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buffer))
                    if (this.fluidType != null) {
                        this.stack = FluidStack(this.fluidType, (buffer)!!.readInt())
                    }
                } else {
                    this.fluidType = null
                    this.stack = null
                }

                this.didTransfer = (buffer)!!.readBoolean()
            }
        
else -> {}
}
    }

    override fun handleClientSide(player: EntityPlayer?) {
        val tile: TileEntity? = (player)!!.level().getBlockEntity(this.pos)

        if (tile is IBufferTransmitter<*>) {
            val transmitter: IBufferTransmitter<FluidStack?> = tile as IBufferTransmitter<FluidStack?>

            lateinit var network: FluidNetwork
when (this.type) {
                PacketType.ADD_TRANSMITTER -> {
                    network = if (transmitter.hasNetwork() && !this.newNetwork) transmitter.getNetwork() as FluidNetwork else FluidNetwork()
                    network.register()
                    transmitter.setNetwork(network)

                    for (pos: BlockPos in this.transmittersCoords!!) {
                        val transmitterTile: TileEntity? = (player)!!.level().getBlockEntity(pos)

                        if (transmitterTile is IBufferTransmitter<*>) {
                            (transmitterTile as IBufferTransmitter<*>).setNetwork(network)
                        }
                    }

                    network.updateCapacity()
                }

                PacketType.FLUID -> if (transmitter.getNetwork() != null) {
                    val network: FluidNetwork = transmitter.getNetwork() as FluidNetwork

                    if (this.fluidType != null) {
                        network.refFluid = this.fluidType
                    }

                    network.buffer = this.stack
                    network.didTransfer = this.didTransfer
                }
            
else -> {}
}
        }
    }

    override fun handleServerSide(player: EntityPlayer?) {
    }

    enum class PacketType {
        ADD_TRANSMITTER,
        FLUID,
    }

    companion object {
        @JvmStatic
        fun getFluidUpdate(
            dimensionID: Int,
            pos: BlockPos?,
            stack: FluidStack?,
            didTransfer: Boolean
        ): PacketFluidNetworkUpdate {
            return PacketFluidNetworkUpdate(PacketType.FLUID, dimensionID, pos, stack, didTransfer)
        }

        @JvmStatic
        fun getAddTransmitterUpdate(
            dimensionID: Int,
            pos: BlockPos?,
            newNetwork: Boolean,
            transmittersAdded: MutableCollection<IBufferTransmitter<FluidStack?>?>?
        ): PacketFluidNetworkUpdate {
            return PacketFluidNetworkUpdate(PacketType.ADD_TRANSMITTER, dimensionID, pos, newNetwork, transmittersAdded)
        }
    }
}
