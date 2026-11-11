package galaxyspace.systems.SolarSystem.planets.overworld.items.modules

import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Protection
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

open class Energy : ItemModule() {
    override fun getName(): String {
        return "energy"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage())
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot? {
        return null
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GSItems.ADVANCED_BATTERY, 1, OreDictionary.WILDCARD_VALUE)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(Protection()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.ALL
    }
}
