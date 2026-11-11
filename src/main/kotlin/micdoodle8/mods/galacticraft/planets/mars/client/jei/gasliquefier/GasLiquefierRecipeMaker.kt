package micdoodle8.mods.galacticraft.planets.mars.client.jei.gasliquefier

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.world.item.ItemStack

object GasLiquefierRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<GasLiquefierRecipeWrapper?> {
        val recipes: MutableList<GasLiquefierRecipeWrapper?> = ArrayList<GasLiquefierRecipeWrapper?>()

        recipes.add(
            GasLiquefierRecipeWrapper(
                ItemStack(AsteroidsItems.methaneCanister, 1, 1),
                ItemStack(GCItems.fuelCanister, 1, 1)
            )
        )
        recipes.add(
            GasLiquefierRecipeWrapper(
                ItemStack(AsteroidsItems.atmosphericValve),
                ItemStack(AsteroidsItems.canisterLN2, 1, 1)
            )
        )
        recipes.add(
            GasLiquefierRecipeWrapper(
                ItemStack(AsteroidsItems.atmosphericValve),
                ItemStack(AsteroidsItems.canisterLOX, 1, 1)
            )
        )
        recipes.add(
            GasLiquefierRecipeWrapper(
                ItemStack(AsteroidsItems.canisterLN2, 1, 501),
                ItemStack(AsteroidsItems.canisterLN2, 1, 1)
            )
        )
        recipes.add(
            GasLiquefierRecipeWrapper(
                ItemStack(AsteroidsItems.canisterLOX, 1, 501),
                ItemStack(AsteroidsItems.canisterLOX, 1, 1)
            )
        )

        return recipes
    }
}
