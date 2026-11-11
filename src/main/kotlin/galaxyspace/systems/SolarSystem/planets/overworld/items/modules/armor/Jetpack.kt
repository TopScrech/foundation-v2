package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

open class Jetpack : ItemModule() {
    override fun getName(): String {
        return "jetpack"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GSItems.JETPACK, 1, GSItems.JETPACK.getMaxDamage())
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.CHEST
    }

    override fun isActiveModule(): Boolean {
        return true
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GSItems.JETPACK, 1, OreDictionary.WILDCARD_VALUE)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?>? {
        return null
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }
}
