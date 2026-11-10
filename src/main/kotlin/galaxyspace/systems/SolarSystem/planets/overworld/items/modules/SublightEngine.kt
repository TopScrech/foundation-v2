package galaxyspace.systems.SolarSystem.planets.overworld.items.modules

import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class SublightEngine : ItemModule() {
    override fun getName(): String? {
        return Engine_Type.SUBLIGHT_ENGINE.getName()
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GSItems.ROCKET_MODULES, 1, 6)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot? {
        return null
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GSItems.ROCKET_MODULES, 1, 6)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?>? {
        return null
    }

    override fun getType(): Module_Type {
        return Module_Type.ROCKET
    }
}
