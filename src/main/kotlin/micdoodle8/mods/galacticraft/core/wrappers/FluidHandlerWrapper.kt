package micdoodle8.mods.galacticraft.core.wrappers

import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

open class FluidHandlerWrapper constructor(
    @JvmField var wrapper: IFluidHandlerWrapper,
    @JvmField var side: EnumFacing?
) : IFluidHandler {
    override fun getTankProperties(): Array<IFluidTankProperties?> {
        val infos: Array<FluidTankInfo>? = (wrapper.getTankInfo(side) as Array<FluidTankInfo>?)
        if (infos != null) {
            val properties: Array<FluidTankProperties?> = arrayOfNulls<FluidTankProperties>(infos.size)
            for (i: Int in infos.indices) {
                val info: FluidTankInfo = infos[i]
                properties[i] = FluidTankProperties(
                    info.fluid,
                    info.capacity,
                    this.wrapper.canFill(side, null),
                    this.wrapper.canDrain(side, null)
                )
            }
            return (properties as Array<IFluidTankProperties?>)
        }
        return arrayOf<IFluidTankProperties?>()
    }

    override fun fill(resource: FluidStack?, doFill: Boolean): Int {
        if (wrapper.canFill(side, if (resource != null) resource.getFluid() else null)) {
            return wrapper.fill(side, resource, doFill)
        }

        return 0
    }

    override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (wrapper.canDrain(side, if (resource != null) resource.getFluid() else null)) {
            return wrapper.drain(side, resource, doDrain)
        }

        return null
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (wrapper.canDrain(side, null)) {
            return wrapper.drain(side, maxDrain, doDrain)
        }

        return null
    }
}
