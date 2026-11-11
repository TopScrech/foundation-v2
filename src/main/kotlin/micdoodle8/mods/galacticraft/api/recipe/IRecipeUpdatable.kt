package micdoodle8.mods.galacticraft.api.recipe

import java.util.List

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.CraftingInput

interface IRecipeUpdatable : Recipe<CraftingInput> {
    /**
     * Replace all inputs which match ItemStack inputA
     * with a List<ItemStack> (probably representing OreDict output).
     *
     * @param inputA  the current simple ItemStack ingredient
     * @param inputB  the List<ItemStack> to replace it with
    </ItemStack></ItemStack> */
    fun replaceInput(ingredient: ItemStack?, replacement: MutableList<ItemStack?>?)

    /**
     * Replace all inputs which are lists containing ItemStack ingredient
     * with simple ItemStack of ingredient.
     *
     * @param ingredient
     */
    fun replaceInput(ingredient: ItemStack?)
}
