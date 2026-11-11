package galaxyspace.systems.SolarSystem.planets.overworld.items.modules

import foundation.item.FoundationItemVariants
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack

open class SublightEngine : ItemModule() {
    override fun getName(): String? {
        return Engine_Type.SUBLIGHT_ENGINE.getName()
    }

    override fun getIcon(): ItemStack {
        return FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 6)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot? {
        return null
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 6)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?>? {
        return null
    }

    override fun getType(): Module_Type {
        return Module_Type.ROCKET
    }
}
