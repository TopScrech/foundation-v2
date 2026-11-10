package micdoodle8.mods.galacticraft.core.items

import javax.annotation.Nonnull

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fluids.capability.IFluidTankProperties

open class ItemCanisterGenericHandler constructor(@field:Nonnull @JvmField @param:Nonnull protected var container: ItemStack) :
    IFluidHandlerItem, ICapabilityProvider {
    @Nonnull
    override fun getContainer(): ItemStack {
        return container //never varies
    }

    open fun getFluid(): FluidStack? {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric) {
            return (container.getItem() as ItemCanisterGeneric).getFluid(container)
        }
        return null
    }

    protected open fun setFluid(fluid: FluidStack) {
        if (this.canFillFluidType(fluid)) {
            (container.getItem() as ItemCanisterGeneric).setDamage(
                container,
                ItemCanisterGeneric.Companion.EMPTY - fluid.amount
            )
        }
    }

    override fun getTankProperties(): Array<IFluidTankProperties?> {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric) {
            return ((arrayOf<FluidTankProperties>(
                FluidTankProperties(
                    getFluid(),
                    ItemCanisterGeneric.Companion.EMPTY - 1
                )
            ) as Array<IFluidTankProperties?>?))!!
        }
        return (arrayOfNulls<FluidTankProperties>(0) as Array<IFluidTankProperties?>)
    }

    override fun fill(resource: FluidStack?, doFill: Boolean): Int {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric) {
            return (container.getItem() as ItemCanisterGeneric).fill(container, resource, doFill)
        }
        return 0
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric) {
            return (container.getItem() as ItemCanisterGeneric).drain(container, maxDrain, doDrain)
        }
        return null
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if (this.canDrainFluidType(resource)) {
            return (container.getItem() as ItemCanisterGeneric).drain(container, resource.amount, doDrain)
        }
        return null
    }

    open fun canFillFluidType(fluid: FluidStack?): Boolean {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric && fluid != null && fluid.getFluid() != null) {
            return (container.getItem() as ItemCanisterGeneric).getAllowedFluid()
                .equals(fluid.getFluid().getName(), ignoreCase = true)
        }
        return false
    }

    open fun canDrainFluidType(fluid: FluidStack?): Boolean {
        return this.canFillFluidType(fluid)
    }

    protected open fun setContainerToEmpty() {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric) {
            (container.getItem() as ItemCanisterGeneric).setDamage(container, ItemCanisterGeneric.Companion.EMPTY)
        }
    }

    override fun hasCapability(@Nonnull capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
    }

    override fun <T> getCapability(@Nonnull capability: Capability<T?>, facing: EnumFacing?): T? {
        return if (capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) this as T else null
    }
}
