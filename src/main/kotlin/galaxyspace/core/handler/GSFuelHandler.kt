package galaxyspace.core.handler

import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.common.IFuelHandler

open class GSFuelHandler : IFuelHandler {
    override fun getBurnTime(fuel: ItemStack?): Int {
        //if (fuel.getItem() == GSItems.SulfurVenus) return 3200;
        return 0
    }
}
