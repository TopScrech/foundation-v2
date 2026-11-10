package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.init.Items
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class WaterBreathing : ItemModule() {
    override fun getName(): String {
        return "water_breathing"
    }

    override fun getIcon(): ItemStack {
        return getItemsForModule()[0]!!
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.HEAD
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(Items.FISH, 1, 3)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(SensorLens()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }
}
