package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState

import foundation.persistence.containsTag
import micdoodle8.mods.galacticraft.api.transmission.grid.splitUntyped

import micdoodle8.mods.galacticraft.api.tile.IColorable
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidPipe
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.item.DyeColor as EnumDyeColor
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class TileEntityFluidPipe constructor() : TileEntityFluidTransmitter(100), IColorable {
    @JvmField
    var buffer: FluidTankGC = FluidTankGC(1000, this)
    private val dataRequest = false
    private var renderAABB: AxisAlignedBB? = null

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        // Do not re-create tile entity if only the pipe's color changed!
        if (oldState !== newState) {
            return oldState.getBlock() !== newState.getBlock()
        }

        return super.shouldRefresh(world, pos, oldState, newState)
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        val adjacentTile: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, (direction)!!)

        if (type == NetworkType.FLUID) {
            if (adjacentTile is IColorable) {
                val state: IBlockState = this.world.getBlockState(this.getPos())
                val adjacentTileState: IBlockState = adjacentTile.getWorld().getBlockState(adjacentTile.getPos())
                val thisCol: Byte = this.getColor(state)
                val otherCol: Byte = (adjacentTile as IColorable).getColor(adjacentTileState)
                return thisCol == otherCol || thisCol.toInt() == EnumDyeColor.WHITE.getDyeDamage() || otherCol.toInt() == EnumDyeColor.WHITE.getDyeDamage()
            }

            return true
        }

        return false
    }

    //    @Override
    //    public boolean canUpdate()
    //    {
    //        return this.world == null || !this.world.isRemote;
    //
    //    }
    override fun getPacketRange(): Double {
        return 12.0
    }

    override fun getPacketCooldown(): Int {
        return 5
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }

    override fun onLoad() {
        if (this.world.isRemote) {
            this.world.notifyLightSet(getPos())
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_REQUEST_DATA,
                    GCCoreUtil.getDimensionID(this.world),
                    arrayOf<Any>(GCCoreUtil.getDimensionID(this.world), this.getPos())
                )
            )
        }
    }

    override fun onColorUpdate() {
        if (this.world != null) {
            if (this.world.isRemote) {
                this.world.notifyLightSet(getPos())
            } else {
                (this.getNetwork())!!.splitUntyped(this)
                this.resetNetwork()
            }
        }
    }

    override fun getColor(state: IBlockState?): Byte {
        if ((state)!!.getBlock() is BlockFluidPipe) {
            return ((state)!!.getValue(BlockFluidPipe.COLOR))!!.getDyeDamage().toByte()
        }
        return 15
    }

    override fun onAdjacentColorChanged(direction: EnumFacing?) {
        val state: IBlockState = this.world.getBlockState(this.getPos())
        this.world.sendBlockUpdated(this.getPos(), state, state, 3)

        if (!this.world.isRemote) {
            this.refresh()
        }
    }

    override fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(tagCompound)
        if (this.buffer.getFluid() != null) {
            tagCompound.put("buff", this.buffer.writeToNBT(NBTTagCompound()))
        }
        return tagCompound
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun readFromNBT(tagCompound: NBTTagCompound) {
        super.readFromNBT(tagCompound)

        if (tagCompound.containsTag("pipeColor")) {
            // Backwards compatibility
            this.world.setBlockAndUpdate(
                getPos(),
                this.world.getBlockState(getPos()).setValue(
                    BlockFluidPipe.COLOR,
                    EnumDyeColor.byDyeDamage(tagCompound.getByteOr("pipeColor", 0).toInt())
                )
            )
        }

        if (tagCompound.containsTag("buff")) {
            this.buffer.readFromNBT(tagCompound.getCompoundOrEmpty("buff"))
        }
    }

    override fun getBuffer(): FluidStack? {
        return if (buffer.getFluid() == null) null else buffer.getFluid()
    }

    override fun getCapacity(): Int {
        return 200
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        val network: IGridNetwork<*, *, *>? = this.getNetwork()
        if (network is FluidNetwork) {
            val fluidNetwork: FluidNetwork = network
            return fluidNetwork.emitToBuffer(resource, doFill)
        } else {
            return this.buffer.fill(resource, doFill)
        }
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return true
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return arrayOfNulls<FluidTankInfo>(0)
    }

    open fun switchType(): Boolean {
        if (this.ticks < 10) {
            return false
        }

        val block: Block
        val currentType: Block = this.getBlockType()

        if (currentType !is BlockFluidPipe) {
            return false
        }

        when (currentType.getMode()) {
            BlockFluidPipe.EnumPipeMode.NORMAL -> block = (GCBlocks.oxygenPipePull)!!
            else -> block = (GCBlocks.oxygenPipe)!!
        }

        BlockFluidPipe.ignoreDrop = true
        this.world.setBlockAndUpdate(
            pos,
            block.getStateFromMeta(currentType.getMetaFromState(this.world.getBlockState(pos)))
        )
        BlockFluidPipe.ignoreDrop = false
        if (this.hasNetwork()) {
            this.refresh()
            (this.getNetwork())!!.refresh()
        }

        return true
    }

    override fun canTransmit(): Boolean {
        val currentType: Block = this.getBlockType()

        if (currentType !is BlockFluidPipe) {
            // Walkway blocks, etc
            return true
        }

        return currentType.getMode() != BlockFluidPipe.EnumPipeMode.PULL
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
        return 16384.0 //128 squared
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true

        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }



        return super.getCapability<T?>(capability, facing)
    }
}
