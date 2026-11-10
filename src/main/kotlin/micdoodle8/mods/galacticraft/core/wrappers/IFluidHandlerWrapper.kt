package micdoodle8.mods.galacticraft.core.wrappers

import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo

interface IFluidHandlerWrapper {
    fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int

    fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack?

    fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack?

    fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean

    fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean

    fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?>?
}
