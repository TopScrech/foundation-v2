package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.init.Blocks
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class Jump : ItemModule() {
    override fun getName(): String {
        return "jump"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(Blocks.PISTON, 1, 0)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.FEET
    }

    override fun isActiveModule(): Boolean {
        return true
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(Blocks.PISTON, 2, 0)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(Gravity()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }
}
