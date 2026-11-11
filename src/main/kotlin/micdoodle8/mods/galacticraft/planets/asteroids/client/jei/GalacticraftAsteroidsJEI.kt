package micdoodle8.mods.galacticraft.planets.asteroids.client.jei

import micdoodle8.mods.galacticraft.planets.asteroids.client.jei.astrominer.AstroMinerRecipeWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.client.jei.tier3rocket.Tier3RocketRecipeWrapper

import javax.annotation.Nonnull

import mezz.jei.api.BlankModPlugin
import mezz.jei.api.IGuiHelper
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapperFactory
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.planets.asteroids.client.jei.astrominer.AstroMinerRecipeCategory
import micdoodle8.mods.galacticraft.planets.asteroids.client.jei.tier3rocket.Tier3RocketRecipeCategory
import micdoodle8.mods.galacticraft.planets.asteroids.client.jei.tier3rocket.Tier3RocketRecipeMaker
import net.minecraft.world.item.ItemStack

@JEIPlugin
open class GalacticraftAsteroidsJEI : BlankModPlugin() {
    override fun register(@Nonnull registry: IModRegistry) {
        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> Tier3RocketRecipeWrapper((recipe)!!) },
            RecipeCategories.ROCKET_T3_ID
        )
        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> AstroMinerRecipeWrapper((recipe)!!) },
            RecipeCategories.ASTRO_MINER_ID
        )

        registry.addRecipes(Tier3RocketRecipeMaker.getRecipesList(), RecipeCategories.ROCKET_T3_ID)
        registry.addRecipes(GalacticraftRegistry.getAstroMinerRecipes(), RecipeCategories.ASTRO_MINER_ID)
        registry.addRecipeCatalyst(
            ItemStack(GCBlocks.nasaWorkbench),
            RecipeCategories.ROCKET_T3_ID,
            RecipeCategories.ASTRO_MINER_ID
        )
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper: IGuiHelper = registry.getJeiHelpers().getGuiHelper()
        registry.addRecipeCategories(
            Tier3RocketRecipeCategory(guiHelper),
            AstroMinerRecipeCategory(guiHelper)
        )
    }
}
