package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.Energy
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack

open class Protection : ItemModule() {
    override fun getName(): String {
        return "protection"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GSItems.COBALT_CHEST, 1, 0)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot? {
        return null
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(FoundationItemVariants.stack(GSItems.INGOTS, 16, 0)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(Energy()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }

    override fun getDischargeCount(): Int {
        return 0
    }
}
