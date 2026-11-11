package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getStateFromMeta

import foundation.persistence.containsTag
import net.minecraftforge.fml.client.FMLClientHandler


import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.DelayTimer
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledBlockChange
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidEvent
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.RuntimeException

open class TileEntityFluidTank : TileEntityAdvanced(), IFluidHandlerWrapper {
    @JvmField
    var fluidTank: FluidTankGC? = FluidTankGC(16000, this)

    @JvmField
    var updateClient: Boolean = false
    private val delayTimer = DelayTimer(1)
    private var renderAABB: AxisAlignedBB? = null

    open fun onBreak() {
        if (fluidTank!!.getFluidAmount() > 0) {
            FluidEvent.fireEvent(FluidEvent.FluidSpilledEvent(fluidTank!!.getFluid(), world, pos))
            if (!this.world.isRemote && fluidTank!!.getFluidAmount() > 1000) {
                val b: Block? = fluidTank!!.getFluid()!!.getFluid().getBlock()
                if (!(b == null || b is BlockAir)) {
                    TickHandlerServer.Companion.scheduleNewBlockChange(
                        GCCoreUtil.getDimensionID(this.world),
                        ScheduledBlockChange(pos, b.getStateFromMeta(0), 3)
                    )
                }
            }
        }
    }

    override fun update() {
        super.update()

        if (fluidTank!!.getFluid() != null) {
            moveFluidDown()
        }

        if (!this.world.isRemote && updateClient && delayTimer.markTimeIfDelay(this.world)) {
            val packet: PacketDynamic = PacketDynamic(this)
            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                packet,
                NetworkRegistry.TargetPoint(
                    GCCoreUtil.getDimensionID(this.world),
                    getPos().getX().toDouble(),
                    getPos().getY().toDouble(),
                    getPos().getZ().toDouble(),
                    this.getPacketRange()
                )
            )
            this.updateClient = false
        }
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        if (resource == null) {
            return 0
        }

        val copy: FluidStack = resource.copy()
        var totalUsed: Int = 0
        var toFill: TileEntityFluidTank? = getLastTank()

        val fluid: FluidStack? = toFill!!.fluidTank!!.getFluid()
        if (fluid != null && fluid.amount > 0 && !fluid.isFluidEqual(copy)) {
            return 0
        }

        while (toFill != null && copy.amount > 0) {
            val used: Int = toFill.fluidTank!!.fill(copy, doFill)
            if (used > 0) {
                toFill.updateClient = true
            }
            copy.amount -= used
            totalUsed += used
            toFill = getNextTank(toFill.getPos())
        }

        return totalUsed
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (resource == null) {
            return null
        }
        val last: TileEntityFluidTank = getLastTank()
        if (!resource.isFluidEqual(last.fluidTank!!.getFluid())) {
            return null
        }
        return drain(from, resource.amount, doDrain)
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        val last: TileEntityFluidTank = getLastTank()
        val stack: FluidStack? = last.fluidTank!!.drain(maxDrain, doDrain)
        if (doDrain && stack != null && stack.amount > 0) {
            last.updateClient = true
        }
        return stack
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return fluidTank!!.getFluid() == null || fluidTank!!.getFluid()!!
            .getFluid() == null || fluid == null || fluidTank!!.getFluid()!!.getFluid() === fluid
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return fluid == null || fluidTank!!.getFluid() != null && fluidTank!!.getFluid()!!.getFluid() === fluid
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        val compositeTank: FluidTank = FluidTank(fluidTank!!.getCapacity())
        var last: TileEntityFluidTank? = getLastTank()

        if (last != null && last.fluidTank!!.getFluid() != null) {
            compositeTank.setFluid(last.fluidTank!!.getFluid()!!.copy())
        } else {
            return arrayOf<FluidTankInfo?>(compositeTank.getInfo())
        }

        var capacity: Int = last.fluidTank!!.getCapacity()
        last = getNextTank(last.getPos())

        while (last != null) {
            val fluid: FluidStack? = last.fluidTank!!.getFluid()
            if (fluid == null || fluid.amount == 0) {
            } else if (!compositeTank.getFluid()!!.isFluidEqual(fluid)) {
                break
            } else {
                compositeTank.getFluid()!!.amount += fluid.amount
            }

            capacity += last.fluidTank!!.getCapacity()
            last = getNextTank(last.getPos())
        }

        compositeTank.setCapacity(capacity)
        return arrayOf<FluidTankInfo?>(compositeTank.getInfo())
    }

    open fun moveFluidDown() {
        val next: TileEntityFluidTank? = getPreviousTank(this.getPos())
        if (next != null) {
            val used: Int = next.fluidTank!!.fill(fluidTank!!.getFluid(), true)

            if (used > 0) {
                this.updateClient = true
                next.updateClient = true
                fluidTank!!.drain(used, true)
            }
        }
    }

    open fun getLastTank(): TileEntityFluidTank {
        var lastTank: TileEntityFluidTank = this

        while (true) {
            val tank: TileEntityFluidTank? = getPreviousTank(lastTank.getPos())
            if (tank != null) {
                lastTank = tank
            } else {
                break
            }
        }

        return lastTank
    }

    open fun getNextTank(current: BlockPos): TileEntityFluidTank? {
        val above: TileEntity? = this.world.getBlockEntity(current.above())
        if (above is TileEntityFluidTank) {
            return above
        }
        return null
    }

    open fun getPreviousTank(current: BlockPos): TileEntityFluidTank? {
        val below: TileEntity? = this.world.getBlockEntity(current.below())
        if (below is TileEntityFluidTank) {
            return below
        }
        return null
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        if (nbt.containsTag("fuelTank")) {
            this.fluidTank!!.readFromNBT(nbt.getCompoundOrEmpty("fuelTank"))
        }

        this.updateClient = GCCoreUtil.getEffectiveSide() == Side.SERVER
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        if (this.fluidTank!!.getFluid() != null) {
            nbt.put("fuelTank", this.fluidTank!!.writeToNBT(NBTTagCompound()))
        }
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbt: NBTTagCompound = NBTTagCompound()
        nbt.putString("net-type", "desc-packet")
        val packet: PacketDynamic = PacketDynamic(this)
        val buf: ByteBuf = Unpooled.buffer()
        packet.encodeInto(buf)
        val bytes: ByteArray = ByteArray(buf.readableBytes())
        buf.readBytes(bytes)
        nbt.putByteArray("net-data", bytes)
        val tileUpdate: SPacketUpdateTileEntity = SPacketUpdateTileEntity(getPos(), 0, nbt)
        return tileUpdate
    }

    @OnlyIn(Dist.CLIENT)
    override fun onDataPacket(networkManager: NetworkManager, pkt: SPacketUpdateTileEntity) {
        if (!this.world.isRemote) {
            return
        }
        if (pkt.getNbtCompound() == null) {
            throw RuntimeException("[GC] Missing NBTTag compound!")
        }
        val nbt: NBTTagCompound = pkt.getNbtCompound()
        try {
            if ("desc-packet" == nbt.getStringOr("net-type", "")) {
                val bytes: ByteArray = nbt.getByteArray("net-data")
                val data: ByteBuf? = Unpooled.wrappedBuffer(bytes)
                val packet: PacketDynamic = PacketDynamic()
                packet.decodeInto(data)
                packet.handleClientSide(
                    FMLClientHandler.instance().getClientPlayerEntity()
                )
            }
        } catch (t: Throwable) {
            throw RuntimeException(
                "[GC] Failed to read a packet! (" + nbt.getTag("net-type") + ", " + nbt.getTag("net-data"),
                t
            )
        }
    }

    override fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
        if (!this.world.isRemote) {
            if (fluidTank == null) {
                (networkedList)!!.add(0)
                (networkedList)!!.add("")
                (networkedList)!!.add(0)
            } else {
                (networkedList)!!.add(fluidTank!!.getCapacity())
                (networkedList)!!.add(
                    if (fluidTank!!.getFluid() == null) "" else fluidTank!!.getFluid()!!.getFluid().getName()
                )
                (networkedList)!!.add(fluidTank!!.getFluidAmount())
            }
        }
    }

    override fun readExtraNetworkedData(buffer: ByteBuf?) {
        if (this.world.isRemote) {
            val capacity: Int = (buffer)!!.readInt()
            val fluidName: String = ByteBufUtils.readUTF8String(buffer)
            val fluidTank: FluidTankGC = FluidTankGC(capacity, this)
            val amount: Int = (buffer)!!.readInt()

            if (fluidName == "") {
                fluidTank.setFluid(null)
            } else {
                val fluid: Fluid = FluidRegistry.getFluid(fluidName)
                fluidTank.setFluid(FluidStack(fluid, amount))
            }

            this.fluidTank = fluidTank
        }
    }

    override fun getPacketRange(): Double {
        return 40.0
    }

    override fun getPacketCooldown(): Int {
        return 1
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }
        return super.getCapability<T?>(capability, facing)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 1, 1))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_MEDIUM
    }
}
