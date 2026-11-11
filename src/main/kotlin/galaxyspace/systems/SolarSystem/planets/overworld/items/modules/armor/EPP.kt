package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

open class EPP : ItemModule() {
    override fun getName(): String {
        return "epp"
    }

    override fun getIcon(): ItemStack {
        return ItemOxygenTank.stack(requireNotNull(GSItems.OXYGENTANK_TIER_EPP), 1, 1)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot? {
        return null
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GSItems.OXYGENTANK_TIER_EPP, 1, OreDictionary.WILDCARD_VALUE)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?>? {
        return null
    }

    override fun getType(): Module_Type {
        return Module_Type.OXYGEN_TANK
    }
}
