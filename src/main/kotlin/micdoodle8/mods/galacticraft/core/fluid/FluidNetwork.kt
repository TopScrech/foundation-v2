package micdoodle8.mods.galacticraft.core.fluid

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.transmission.grid.Pathfinder
import micdoodle8.mods.galacticraft.api.transmission.grid.PathfinderChecker
import micdoodle8.mods.galacticraft.api.transmission.tile.IBufferTransmitter
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacket
import micdoodle8.mods.galacticraft.core.network.PacketFluidNetworkUpdate
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class FluidNetwork : IGridNetwork<FluidNetwork?, IBufferTransmitter<FluidStack?>?, TileEntity?> {
    @JvmField
    var acceptors: MutableMap<BlockPos?, IFluidHandler?>? = Maps.newHashMap<BlockPos?, IFluidHandler?>()

    @JvmField
    var acceptorDirections: MutableMap<BlockPos?, EnumSet<EnumFacing?>> =
        Maps.newHashMap<BlockPos?, EnumSet<EnumFacing?>>()

    @JvmField
    val pipes: MutableSet<IBufferTransmitter<FluidStack?>> = Sets.newHashSet<IBufferTransmitter<FluidStack?>>()
    private val pipesAdded: MutableSet<IBufferTransmitter<FluidStack?>?> =
        Sets.newHashSet<IBufferTransmitter<FluidStack?>?>()
    private val updateQueue: MutableSet<DelayQueue?> = Sets.newLinkedHashSet<DelayQueue?>()

    @JvmField
    var buffer: FluidStack? = null
    private var capacity = 0
    private var world: World? = null
    private var prevBufferAmount = 0
    private var needsUpdate = false

    @JvmField
    var fluidScale: Float = 0f

    @JvmField
    var refFluid: Fluid? = null

    @JvmField
    var didTransfer: Boolean = false

    @JvmField
    var prevTransfer: Boolean = false

    @JvmField
    var transferDelay: Int = 0
    private var prevTransferAmount = 0
    private var updateDelay = 0
    private var firstUpdate = true

    constructor()

    constructor(toMerge: MutableCollection<FluidNetwork?>) {
        for (network: FluidNetwork? in toMerge) {
            if (network != null) {
                if (network.buffer != null) {
                    if (this.buffer == null) {
                        this.buffer = network.buffer!!.copy()
                    } else {
                        if (buffer!!.getFluid() === network.buffer!!.getFluid()) {
                            buffer!!.amount += network.buffer!!.amount
                        } else if (network.buffer!!.amount > buffer!!.amount) {
                            this.buffer = network.buffer!!.copy()
                        }
                    }

                    network.buffer = null
                }

                this.adoptNetwork(network)
                network.unregister()
            }
        }

        this.register()
    }

    open fun adoptNetwork(network: FluidNetwork) {
        for (transmitter: IBufferTransmitter<FluidStack?> in network.pipes) {
            transmitter.setNetwork(this)
            this.pipes.add(transmitter)
            this.pipesAdded.add(transmitter)
            this.updateDelay = if (this.firstUpdate) 3 else 1
        }

        this.acceptors!!.putAll(network.acceptors!!)

        for (e in network.acceptorDirections.entries) {
            val pos: BlockPos = e.key!!

            if (this.acceptorDirections.containsKey(pos)) {
                this.acceptorDirections.get(pos)!!.addAll(e.value!!)
            } else {
                this.acceptorDirections.put(pos, e.value!!)
            }
        }
    }

    open fun register() {
        (GalacticraftCore.proxy)!!.registerNetwork(this)
    }

    open fun unregister() {
        (GalacticraftCore.proxy)!!.unregisterNetwork(this)
    }

    open fun addTransmitter(transmitter: IBufferTransmitter<FluidStack?>) {
        this.pipes.add(transmitter)
        this.pipesAdded.add(transmitter)
        this.refresh()
        this.updateDelay = if (this.firstUpdate) 20 else 1
    }

    open fun removeTransmitter(transmitter: IBufferTransmitter<FluidStack?>?) {
        this.pipes.remove(transmitter)
        this.updateCapacity()
    }

    open fun onTransmitterAdded(transmitter: IBufferTransmitter<FluidStack?>) {
        val stack: FluidStack? = transmitter.getBuffer()

        if (stack == null || stack.getFluid() == null || stack.amount == 0) {
            // Nothing to do
            return
        }

        if (buffer == null || buffer!!.getFluid() == null || buffer!!.amount == 0) {
            // Set transmitter buffer to network buffer
            buffer = stack.copy()
            stack.amount = 0
            return
        }

        if (buffer!!.isFluidEqual(stack)) {
            // Add transmitter fluid to network buffer
            buffer!!.amount += stack.amount
        }

        stack.amount = 0
    }

    open fun clamp() {
        if (buffer != null && buffer!!.amount > getCapacity()) {
            buffer!!.amount = this.capacity
        }
    }

    open fun updateCapacity() {
        this.capacity = 0

        for (transmitter: IBufferTransmitter<FluidStack?> in this.pipes) {
            this.capacity += transmitter.getCapacity()
        }
    }

    open fun getCapacity(): Int {
        return capacity
    }

    open fun getRequest(): Int {
        return getCapacity() - (if (buffer != null) buffer!!.amount else 0)
    }

    private fun emitToAcceptors(toSend: FluidStack, doTransfer: Boolean): Int {
        val available: MutableList<Pair<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>> =
            ArrayList<Pair<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>>()
        available.addAll((this.getAcceptors(toSend) as Collection<Pair<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>>))

        Collections.shuffle(available)

        var totalSend: Int = 0

        if (!available.isEmpty()) {
            val divider: Int = available.size
            var remainder: Int = toSend.amount % divider
            val each: Int = (toSend.amount - remainder) / divider

            for (pair in available) {
                var currentSend: Int = each
                val acceptors: MutableMap<EnumFacing?, IFluidHandler?> = (pair.getRight())!!
                val sides: EnumSet<EnumFacing?> = acceptorDirections.get(pair.getLeft())!!

                if (remainder > 0) {
                    currentSend++
                    remainder--
                }

                for (side: EnumFacing? in sides) {
                    val prev: Int = totalSend
                    val acceptor: IFluidHandler? = acceptors.get(side)

                    if (acceptor != null) {
                        val copy: FluidStack = toSend.copy()
                        copy.amount = currentSend
                        totalSend += acceptor.fill(copy, doTransfer)
                    }

                    if (totalSend > prev) {
                        // If fluid was sent to this handler, continue to next
                        break
                    }
                }
            }
        }

        if (doTransfer && totalSend > 0 && GCCoreUtil.getEffectiveSide().isServer()) {
            this.didTransfer = true
            this.transferDelay = 2
        }

        return totalSend
    }

    open fun emitToBuffer(toSend: FluidStack?, doTransfer: Boolean): Int {
        if (toSend == null || (buffer != null && buffer!!.getFluid() !== toSend.getFluid())) {
            return 0
        }

        val toUse: Int = min(getRequest(), toSend.amount)

        if (doTransfer) {
            if (buffer == null) {
                // Copy
                buffer = toSend.copy()
                buffer!!.amount = toUse
            } else {
                // Add
                buffer!!.amount += toUse
            }
        }

        return toUse
    }

    open fun tickEnd() {
        this.onUpdate()
    }

    open fun addUpdate(player: EntityPlayerMP) {
        this.updateQueue.add(DelayQueue(player))
    }

    private fun getAddTransmitterUpdate(): IPacket {
        val pos: BlockPos = (this.pipes.iterator().next() as TileEntity).getPos()
        return PacketFluidNetworkUpdate.getAddTransmitterUpdate(
            GCCoreUtil.getDimensionID(this.world),
            pos,
            this.firstUpdate,
            this.pipesAdded
        )
    }

    open fun onUpdate() {
        if (GCCoreUtil.getEffectiveSide().isServer()) {
            val iterator: MutableIterator<DelayQueue> = (this.updateQueue.iterator() as MutableIterator<FluidNetwork.DelayQueue>)

            try {
                while (iterator.hasNext()) {
                    val queue: DelayQueue = iterator.next()

                    if (queue.delay > 0) {
                        queue.delay--
                    } else {
                        this.pipesAdded.addAll(this.pipes)
                        (GalacticraftCore.packetPipeline)!!.sendTo(this.getAddTransmitterUpdate(), queue.player)
                        this.pipesAdded.clear()
                        iterator.remove()
                    }
                }
            } catch (e: Exception) {
            }

            if (this.updateDelay > 0) {
                this.updateDelay--

                if (this.updateDelay == 0) {
                    val pos: BlockPos = (this.pipes.iterator().next() as TileEntity).getPos()
                    (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                        this.getAddTransmitterUpdate(),
                        NetworkRegistry.TargetPoint(
                            GCCoreUtil.getDimensionID(this.world),
                            pos.getX().toDouble(),
                            pos.getY().toDouble(),
                            pos.getZ().toDouble(),
                            30.0
                        )
                    )
                    this.firstUpdate = false
                    this.pipesAdded.clear()
                    this.needsUpdate = true
                }
            }

            this.prevTransferAmount = 0

            if (this.transferDelay == 0) {
                this.didTransfer = false
            } else {
                this.transferDelay--
            }

            val stored: Int = if (buffer != null) buffer!!.amount else 0

            if (stored != this.prevBufferAmount) {
                this.needsUpdate = true
            }

            this.prevBufferAmount = stored

            if (this.didTransfer != this.prevTransfer || this.needsUpdate) {
                val pos: BlockPos = (this.pipes.iterator().next() as TileEntity).getPos()
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketFluidNetworkUpdate.getFluidUpdate(
                        GCCoreUtil.getDimensionID(
                            this.world
                        ), pos, this.buffer, this.didTransfer
                    ),
                    NetworkRegistry.TargetPoint(
                        GCCoreUtil.getDimensionID(this.world),
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        20.0
                    )
                )
                this.needsUpdate = false
            }

            this.prevTransfer = this.didTransfer

            if (buffer != null) {
                this.prevTransferAmount = this.emitToAcceptors(buffer!!, true)
                if (buffer != null) {
                    buffer!!.amount -= this.prevTransferAmount

                    if (buffer!!.amount <= 0) {
                        this.buffer = null
                    }
                }
            }
        }
    }

    open fun clientTick() {
        this.fluidScale = max(this.fluidScale, this.getScale())

        if (this.didTransfer && this.fluidScale < 1.0f) {
            this.fluidScale = max(this.getScale(), min(1f, fluidScale + 0.02f))
        } else if (!this.didTransfer && fluidScale > 0.0f) {
            this.fluidScale = this.getScale()

            if (this.fluidScale == 0.0f) {
                this.buffer = null
            }
        }
    }

    open fun getScale(): Float {
        if (this.buffer == null || this.getCapacity() == 0) {
            return 0.0f
        }

        return min(1.0f, this.buffer!!.amount / this.getCapacity().toFloat())
    }

    open fun getAcceptors(toSend: FluidStack): MutableList<Pair<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>?> {
        val toReturn: MutableList<Pair<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>?> =
            LinkedList<Pair<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>?>()

        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
            return toReturn
        }

        if (this.acceptors == null || this.acceptors!!.isEmpty()) {
            this.refreshAcceptors()
        }

        val acceptorsCopy: MutableList<BlockPos> = ArrayList<BlockPos>()
        acceptorsCopy.addAll((acceptors!!.keys as Collection<BlockPos>))

        for (coords: BlockPos in acceptorsCopy) {
            val sides: EnumSet<EnumFacing?>? = acceptorDirections.get(coords)
            if (sides == null || sides.isEmpty()) {
                continue
            }

            val tile: TileEntity? = this.world!!.getTileEntity(coords)

            if (tile == null) {
                continue
            }

            val handlers: MutableMap<EnumFacing?, IFluidHandler?> = Maps.newHashMap<EnumFacing?, IFluidHandler?>()

            var handler: IFluidHandler?
            val fluidToSend: Fluid = toSend.getFluid()
            for (side: EnumFacing? in sides) {
                handler = tile.getCapability<IFluidHandler?>(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)
                if (handler != null && handler.fill(FluidStack(fluidToSend, 1), false) > 0) {
                    handlers.put(side, handler)
                }
            }

            toReturn.add(Pair.of<BlockPos?, MutableMap<EnumFacing?, IFluidHandler?>?>(coords, handlers))
        }

        return toReturn
    }

    override fun refresh() {
        if (this.acceptors != null) {
            this.acceptors!!.clear()
        }

        try {
            val it: MutableIterator<IBufferTransmitter<FluidStack?>?> = this.pipes.iterator()

            while (it.hasNext()) {
                val transmitter: ITransmitter? = it.next()
                val tileTransmitter: TileEntity? = transmitter as TileEntity?

                if (transmitter == null) {
                    it.remove()
                    continue
                }

                transmitter.onNetworkChanged()

                if (tileTransmitter!!.isInvalid() || tileTransmitter.getWorld() == null) {
                    it.remove()
                    continue
                } else {
                    if (this.world == null) {
                        this.world = tileTransmitter.getWorld()
                    }

                    transmitter.setNetwork(this)
                }
            }

            updateCapacity()
            clamp()
        } catch (e: Exception) {
            FMLLog.severe("Failed to refresh liquid pipe network.")
            e.printStackTrace()
        }
    }

    open fun refreshAcceptors() {
        if (this.acceptors == null) {
            this.acceptors = Maps.newHashMap<BlockPos?, IFluidHandler?>()
        } else {
            this.acceptors!!.clear()
        }

        try {
            val it: MutableIterator<IBufferTransmitter<FluidStack?>?> = this.pipes.iterator()

            while (it.hasNext()) {
                val transmitter: IBufferTransmitter<FluidStack?>? = it.next()
                val tile: TileEntity? = transmitter as TileEntity?

                if (transmitter == null || tile!!.isInvalid() || tile.getWorld() == null) {
                    it.remove()
                    continue
                }

                if (!transmitter.canTransmit()) {
                    continue
                }

                var i: Int = 0
                for (acceptor: TileEntity? in (transmitter.getAdjacentConnections())!!) {
                    if (acceptor !is IBufferTransmitter<*> && acceptor != null) {
                        val facing: EnumFacing = EnumFacing.byIndex(i).getOpposite()
                        val handler: IFluidHandler? = acceptor.getCapability<IFluidHandler?>(
                            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                            facing
                        )
                        if (handler != null) {
                            val acceptorPos: BlockPos = tile.getPos().offset(facing.getOpposite())
                            var facingSet: EnumSet<EnumFacing?>? = this.acceptorDirections.get(acceptorPos)
                            if (facingSet != null) {
                                facingSet.add(facing)
                            } else {
                                facingSet = EnumSet.of<EnumFacing?>(facing)
                            }
                            this.acceptors!!.put(acceptorPos, handler)
                            this.acceptorDirections.put(acceptorPos, (facingSet)!!)
                        }
                    }
                    i++
                }
            }
        } catch (e: Exception) {
            FMLLog.severe("Failed to refresh liquid acceptors")
            e.printStackTrace()
        }
    }

    override fun getTransmitters(): ImmutableSet<IBufferTransmitter<FluidStack?>?>? {
        return ImmutableSet.copyOf<IBufferTransmitter<FluidStack?>?>(this.pipes)
    }

    override fun merge(network: FluidNetwork?): FluidNetwork {
        if (network != null && network !== this) {
            val newNetwork: FluidNetwork = FluidNetwork(Lists.newArrayList<FluidNetwork?>(this, network))
            newNetwork.refresh()
            return newNetwork
        }

        return this
    }

    override fun split(splitPoint: IBufferTransmitter<FluidStack?>?) {
        if (splitPoint is TileEntity) {
            this.pipes.remove(splitPoint)

            /**
             * Loop through the connected blocks and attempt to see if there are
             * connections between the two points elsewhere.
             */
            val connectedBlocks: Array<TileEntity?> = (splitPoint.getAdjacentConnections())!!

            for (connectedBlockA: TileEntity? in connectedBlocks) {
                if (connectedBlockA is INetworkConnection) {
                    for (connectedBlockB: TileEntity? in connectedBlocks) {
                        if (connectedBlockA !== connectedBlockB && connectedBlockB is INetworkConnection) {
                            val finder: Pathfinder = PathfinderChecker(
                                (splitPoint as TileEntity).getWorld(),
                                connectedBlockB as INetworkConnection,
                                NetworkType.FLUID,
                                splitPoint
                            )
                            finder.init(BlockVec3(connectedBlockA))

                            if ((finder.results)!!.size > 0) {
                                /**
                                 * The connections A and B are still intact
                                 * elsewhere. Set all references of wire
                                 * connection into one network.
                                 */

                                for (node in (finder.closedSet)!!) {
                                    val nodeTile: TileEntity? =
                                        (node)!!.getTileEntity((splitPoint as TileEntity).getWorld())

                                    if (nodeTile is INetworkProvider) {
                                        if (nodeTile !== splitPoint) {
                                            (nodeTile as INetworkProvider).setNetwork(this)
                                        }
                                    }
                                }
                            } else {
                                /**
                                 * The connections A and B are not connected
                                 * anymore. Give both of them a new network.
                                 */
                                val newNetwork: FluidNetwork = FluidNetwork()

                                for (node in (finder.closedSet)!!) {
                                    val nodeTile: TileEntity? =
                                        (node)!!.getTileEntity((splitPoint as TileEntity).getWorld())

                                    if (nodeTile is IBufferTransmitter<*>) {
                                        if (nodeTile !== splitPoint) {
                                            newNetwork.pipes.add(nodeTile as IBufferTransmitter<FluidStack?>)
                                            newNetwork.pipesAdded.add(nodeTile as IBufferTransmitter<FluidStack?>)
                                            newNetwork.onTransmitterAdded(nodeTile as IBufferTransmitter<FluidStack?>)
                                            this.pipes.remove(nodeTile)
                                        }
                                    }
                                }

                                newNetwork.refresh()
                                newNetwork.register()
                            }
                        }
                    }
                }
            }

            if (this.pipes.isEmpty()) {
                this.unregister()
            } else {
                this.updateCapacity()
            }
        }
    }

    override fun toString(): String {
        return "FluidNetwork[" + this.hashCode() + "|Pipes:" + this.pipes.size + "|Acceptors:" + (if (this.acceptors == null) 0 else this.acceptors!!.size) + "]"
    }

    open class DelayQueue
    constructor(@JvmField var player: EntityPlayerMP) {
        @JvmField
        var delay: Int = 5

        override fun hashCode(): Int {
            return this.player.hashCode()
        }

        override fun equals(obj: Any?): Boolean {
            return obj is DelayQueue && obj.player == this.player
        }
    }
}
