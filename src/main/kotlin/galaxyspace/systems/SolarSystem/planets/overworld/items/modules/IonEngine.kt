package galaxyspace.systems.SolarSystem.planets.overworld.items.modules

import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class IonEngine : ItemModule() {
    override fun getName(): String? {
        return Engine_Type.ION_ENGINE.getName()
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GSItems.ROCKET_MODULES, 1, 4)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot? {
        return null
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return arrayOf<ItemStack?>(getIcon())
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(SublightEngine()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.ROCKET
    }
}
