package micdoodle8.mods.galacticraft.planets.mars.client.jei.methanesynthesizer

import java.util.ArrayList

import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.item.ItemStack

object MethaneSynthesizerRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<MethaneSynthesizerRecipeWrapper?> {
        val recipes: MutableList<MethaneSynthesizerRecipeWrapper?> = ArrayList<MethaneSynthesizerRecipeWrapper?>()

        recipes.add(
            MethaneSynthesizerRecipeWrapper(
                ItemStack(AsteroidsItems.atmosphericValve),
                ItemStack(AsteroidsItems.methaneCanister, 1, 1)
            )
        )
        recipes.add(
            MethaneSynthesizerRecipeWrapper(
                ItemStack(MarsItems.carbonFragments, 25, 0),
                ItemStack(AsteroidsItems.methaneCanister, 1, 1)
            )
        )

        return recipes
    }
}
