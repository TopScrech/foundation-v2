package foundation.inventory

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.ForgeHooks

object FoundationCraftingConsumption {
    /** NASA workbenches return containers to the player's inventory before the input slot */
    fun consume(container: Container, insert: (ItemStack) -> Unit, drop: (ItemStack) -> Unit) {
        // Capture remainders before removal, including the last item in a stack
        // Evaluate every hook first so a failed hook cannot leave partially consumed inputs
        val remainders = (0 until container.containerSize).mapNotNull { slot ->
            val input = container.getItem(slot)
            if (input.isEmpty) null else slot to ForgeHooks.getCraftingRemainingItem(input.copy())
        }
        for ((slot, remainder) in remainders) {
            container.removeItem(slot, 1)
            if (remainder.isEmpty) continue
            insert(remainder)
            if (!remainder.isEmpty) {
                if (container.getItem(slot).isEmpty) container.setItem(slot, remainder)
                else drop(remainder)
            }
        }
    }
}
