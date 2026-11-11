package galaxyspace.api.item

import foundation.persistence.containsTag

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.world.item.ItemStack

interface IModificationItem {
    fun getType(stack: ItemStack?): Module_Type?
    fun getAvailableModules(): Array<ItemModule?>?
    fun getModificationCount(stack: ItemStack?): Int

    fun hasModule(module: ItemModule, stack: ItemStack): Boolean {
        return stack.hasTagCompound() && stack.getTagCompound()!!.containsTag(module.getName())
    }
}
