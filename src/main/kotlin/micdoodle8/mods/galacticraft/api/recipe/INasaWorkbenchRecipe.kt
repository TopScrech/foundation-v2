package micdoodle8.mods.galacticraft.api.recipe

import javax.annotation.Nonnull

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import java.util.HashMap

interface INasaWorkbenchRecipe {
    fun matches(inventory: IInventory?): Boolean

    fun getRecipeSize(): Int

    @Nonnull
    fun getRecipeOutput(): ItemStack

    fun getRecipeInput(): HashMap<Int, ItemStack?>?
}
