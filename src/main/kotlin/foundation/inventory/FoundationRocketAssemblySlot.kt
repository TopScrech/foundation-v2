package foundation.inventory

import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

open class FoundationRocketAssemblySlot(
    container: Container,
    private val assemblySlot: Int,
    x: Int,
    y: Int,
    private val parts: Item,
    private val modules: Item
) : Slot(container, assemblySlot, x, y) {
    override fun getMaxStackSize(): Int = 1

    override fun mayPlace(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        if (assemblySlot in 11..13) return stack.item === Items.CHEST
        val meta = FoundationItemVariants.metadata(stack)
        if ((stack.item as? FoundationVariantItem)?.variant(meta) == null) return false
        if (assemblySlot == 10) return stack.item === modules && meta in setOf(2, 8, 9, 10)
        if (stack.item !== parts || meta !in 0..24) return false
        val part = when (assemblySlot) {
            2 -> 0
            3, 4 -> 1
            5 -> 2
            6, 7 -> 3
            8, 9 -> 4
            else -> return false
        }
        return meta % 5 == part
    }
}
