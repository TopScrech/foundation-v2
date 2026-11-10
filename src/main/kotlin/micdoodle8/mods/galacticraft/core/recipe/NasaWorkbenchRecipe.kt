package micdoodle8.mods.galacticraft.core.recipe

import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import java.util.HashMap

open class NasaWorkbenchRecipe constructor(
    private val output: ItemStack,
    private val input: HashMap<Int, ItemStack?>
) : INasaWorkbenchRecipe {
    init {
        for (entry: MutableMap.MutableEntry<Int, ItemStack?> in this.input.entries) {
            requireNotNull(entry.value) { "Recipe contains null ingredient!" }
        }
    }

    override fun matches(inventory: IInventory?): Boolean {
        for (entry: MutableMap.MutableEntry<Int, ItemStack?> in this.input.entries) {
            val stackAt: ItemStack = (inventory)!!.getStackInSlot(entry.key!!)

            if (!this.checkItemEquals(stackAt, entry.value!!)) {
                return false
            }
        }

        return true
    }

    private fun checkItemEquals(target: ItemStack, input: ItemStack): Boolean {
        if (input.isEmpty() && !target.isEmpty() || !input.isEmpty() && target.isEmpty()) {
            return false
        }
        return target.isEmpty() && input.isEmpty() || target.getItem() === input.getItem() && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage())
    }

    override fun getRecipeSize(): Int {
        return this.input.size
    }

    override fun getRecipeOutput(): ItemStack {
        return this.output.copy()
    }

    override fun getRecipeInput(): HashMap<Int, ItemStack?>? {
        return this.input
    }
}
