package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class Gravity : ItemModule() {
    override fun getName(): String {
        return "gravity"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GSItems.COMPRESSED_PLATES, 1, 1)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.FEET
    }

    override fun isActiveModule(): Boolean {
        return true
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GSItems.COMPRESSED_PLATES, 2, 1)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(Jump()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }

    override fun getDischargeCount(): Int {
        return 2
    }
}
