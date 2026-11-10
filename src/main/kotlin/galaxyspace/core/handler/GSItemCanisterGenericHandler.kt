package galaxyspace.core.handler

import javax.annotation.Nonnull

import galaxyspace.core.prefab.items.ItemFluidCanisterGS
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGenericHandler
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidTankProperties

open class GSItemCanisterGenericHandler constructor(@Nonnull container: ItemStack, private val amount: Int) :
    ItemCanisterGenericHandler(container) {
    override fun setFluid(fluid: FluidStack) {
        if (this.canFillFluidType(fluid)) {
            (container.getItem() as ItemCanisterGeneric).setDamage(container, this.amount - fluid.amount)
        }
    }

    override fun getTankProperties(): Array<IFluidTankProperties?> {
        if (container.getCount() > 0 && container.getItem() is ItemFluidCanisterGS) {
            return ((arrayOf<FluidTankProperties>(FluidTankProperties(getFluid(), this.amount - 1)) as Array<IFluidTankProperties?>?))!!
        }
        return (arrayOfNulls<FluidTankProperties>(0) as Array<IFluidTankProperties?>)
    }

    override fun setContainerToEmpty() {
        if (container.getCount() > 0 && container.getItem() is ItemCanisterGeneric) {
            (container.getItem() as ItemCanisterGeneric).setDamage(container, this.amount)
        }
    }
}
