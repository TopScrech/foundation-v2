package micdoodle8.mods.galacticraft.api.recipe

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.registries.IForgeRegistryEntry

open class ShapedRecipesGC constructor(
    @JvmField val recipeWidth: Int,
    @JvmField val recipeHeight: Int,
    @JvmField val recipeItems: Array<ItemStack>,
    private val recipeOutput: ItemStack
) : IForgeRegistryEntry.Impl<IRecipe?>(), IRecipe {
    override fun getRecipeOutput(): ItemStack {
        return this.recipeOutput
    }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        val aitemstack: NonNullList<ItemStack> =
            NonNullList.withSize<ItemStack>(inv.getSizeInventory(), ItemStack.EMPTY)

        for (i: Int in aitemstack.indices) {
            val itemstack: ItemStack = inv.getStackInSlot(i)
            aitemstack.set(i, ForgeHooks.getContainerItem(itemstack))
        }

        return aitemstack
    }

    override fun matches(inv: InventoryCrafting, worldIn: World): Boolean {
        for (i: Int in 0..3 - this.recipeWidth) {
            for (j: Int in 0..3 - this.recipeHeight) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true
                }

                if (this.checkMatch(inv, i, j, false)) {
                    return true
                }
            }
        }

        return false
    }

    private fun checkMatch(
        p_77573_1_: InventoryCrafting,
        p_77573_2_: Int,
        p_77573_3_: Int,
        p_77573_4_: Boolean
    ): Boolean {
        for (i: Int in 0..2) {
            for (j: Int in 0..2) {
                val k: Int = i - p_77573_2_
                val l: Int = j - p_77573_3_
                var itemstack: ItemStack = ItemStack.EMPTY

                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (p_77573_4_) {
                        itemstack = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth]
                    } else {
                        itemstack = this.recipeItems[k + l * this.recipeWidth]
                    }
                }

                val itemstack1: ItemStack = p_77573_1_.getStackInRowAndColumn(i, j)

                if (!itemstack1.isEmpty() || !itemstack.isEmpty()) {
                    if (itemstack1.isEmpty() && !itemstack.isEmpty() || !itemstack1.isEmpty() && itemstack.isEmpty()) {
                        return false
                    }

                    if (itemstack.getItem() !== itemstack1.getItem()) {
                        return false
                    }

                    if (itemstack.getMetadata() != 32767 && itemstack.getMetadata() != itemstack1.getMetadata()) {
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        return this.getRecipeOutput().copy()
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width >= 3 && height >= 3
    }
}
