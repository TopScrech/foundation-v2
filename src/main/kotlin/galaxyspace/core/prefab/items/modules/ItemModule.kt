package galaxyspace.core.prefab.items.modules

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.world.World

abstract class ItemModule {
    abstract fun getName(): String?
    abstract fun getIcon(): ItemStack?
    abstract fun getEquipmentSlot(): EntityEquipmentSlot?
    abstract fun isActiveModule(): Boolean
    abstract fun getItemsForModule(): Array<ItemStack?>?
    abstract fun getForrbidenModules(): Array<ItemModule?>?
    abstract fun getType(): Module_Type?

    open fun getDischargeCount(): Int {
        return 0
    }

    open fun onUpdate(world: World?, player: EntityPlayer?, itemStack: ItemStack?, enable: Boolean) {}
    fun isLoading(): Boolean {
        return true
    }
}
