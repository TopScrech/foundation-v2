package micdoodle8.mods.galacticraft.planets.mars.util

import javax.annotation.Nonnull

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.InventorySchematicAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.InventorySchematicTier3Rocket
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicTier2Rocket
import net.minecraft.world.item.ItemStack

object RecipeUtilMars {
    @JvmStatic
    @Nonnull
    fun findMatchingSpaceshipT2Recipe(inventoryRocketBench: InventorySchematicTier2Rocket?): ItemStack {
        for (recipe in GalacticraftRegistry.getRocketT2Recipes()) {
            if ((recipe)!!.matches(inventoryRocketBench)) {
                return (recipe)!!.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    @Nonnull
    fun findMatchingCargoRocketRecipe(inventoryRocketBench: InventorySchematicCargoRocket?): ItemStack {
        for (recipe in GalacticraftRegistry.getCargoRocketRecipes()) {
            if ((recipe)!!.matches(inventoryRocketBench)) {
                return (recipe)!!.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    @Nonnull
    fun findMatchingSpaceshipT3Recipe(inventoryRocketBench: InventorySchematicTier3Rocket?): ItemStack {
        for (recipe in GalacticraftRegistry.getRocketT3Recipes()) {
            if ((recipe)!!.matches(inventoryRocketBench)) {
                return (recipe)!!.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    @Nonnull
    fun findMatchingAstroMinerRecipe(craftMatrix: InventorySchematicAstroMiner?): ItemStack {
        for (recipe in GalacticraftRegistry.getAstroMinerRecipes()) {
            if ((recipe)!!.matches(craftMatrix)) {
                return (recipe)!!.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }
}
