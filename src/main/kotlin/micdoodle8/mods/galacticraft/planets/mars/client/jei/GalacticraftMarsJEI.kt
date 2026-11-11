package micdoodle8.mods.galacticraft.planets.mars.client.jei

import micdoodle8.mods.galacticraft.planets.mars.client.jei.cargorocket.CargoRocketRecipeWrapper
import micdoodle8.mods.galacticraft.planets.mars.client.jei.tier2rocket.Tier2RocketRecipeWrapper

import javax.annotation.Nonnull

import mezz.jei.api.BlankModPlugin
import mezz.jei.api.IGuiHelper
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapperFactory
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.client.jei.cargorocket.CargoRocketRecipeCategory
import micdoodle8.mods.galacticraft.planets.mars.client.jei.cargorocket.CargoRocketRecipeMaker
import micdoodle8.mods.galacticraft.planets.mars.client.jei.gasliquefier.GasLiquefierRecipeCategory
import micdoodle8.mods.galacticraft.planets.mars.client.jei.gasliquefier.GasLiquefierRecipeMaker
import micdoodle8.mods.galacticraft.planets.mars.client.jei.gasliquefier.GasLiquefierRecipeWrapper
import micdoodle8.mods.galacticraft.planets.mars.client.jei.methanesynthesizer.MethaneSynthesizerRecipeCategory
import micdoodle8.mods.galacticraft.planets.mars.client.jei.methanesynthesizer.MethaneSynthesizerRecipeMaker
import micdoodle8.mods.galacticraft.planets.mars.client.jei.methanesynthesizer.MethaneSynthesizerRecipeWrapper
import micdoodle8.mods.galacticraft.planets.mars.client.jei.tier2rocket.Tier2RocketRecipeCategory
import micdoodle8.mods.galacticraft.planets.mars.client.jei.tier2rocket.Tier2RocketRecipeMaker
import net.minecraft.world.item.ItemStack

@JEIPlugin
open class GalacticraftMarsJEI : BlankModPlugin() {
    override fun register(@Nonnull registry: IModRegistry) {
        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> Tier2RocketRecipeWrapper((recipe)!!) },
            RecipeCategories.ROCKET_T2_ID
        )
        registry.handleRecipes<GasLiquefierRecipeWrapper?>(
            GasLiquefierRecipeWrapper::class.java,
            IRecipeWrapperFactory { recipe: GasLiquefierRecipeWrapper? -> recipe },
            RecipeCategories.GAS_LIQUEFIER_ID
        )
        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> CargoRocketRecipeWrapper((recipe)!!) },
            RecipeCategories.ROCKET_CARGO_ID
        )
        registry.handleRecipes<MethaneSynthesizerRecipeWrapper?>(
            MethaneSynthesizerRecipeWrapper::class.java,
            IRecipeWrapperFactory { recipe: MethaneSynthesizerRecipeWrapper? -> recipe },
            RecipeCategories.METHANE_SYNTHESIZER_ID
        )

        registry.addRecipes(Tier2RocketRecipeMaker.getRecipesList(), RecipeCategories.ROCKET_T2_ID)
        registry.addRecipes(GasLiquefierRecipeMaker.getRecipesList(), RecipeCategories.GAS_LIQUEFIER_ID)
        registry.addRecipes(CargoRocketRecipeMaker.getRecipesList(), RecipeCategories.ROCKET_CARGO_ID)
        registry.addRecipes(MethaneSynthesizerRecipeMaker.getRecipesList(), RecipeCategories.METHANE_SYNTHESIZER_ID)

        registry.addRecipeCatalyst(
            ItemStack(GCBlocks.nasaWorkbench),
            RecipeCategories.ROCKET_T2_ID,
            RecipeCategories.ROCKET_CARGO_ID
        )
        registry.addRecipeCatalyst(ItemStack(MarsBlocks.machineT2), RecipeCategories.GAS_LIQUEFIER_ID)
        registry.addRecipeCatalyst(ItemStack(MarsBlocks.machineT2, 1, 4), RecipeCategories.METHANE_SYNTHESIZER_ID)
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper: IGuiHelper = registry.getJeiHelpers().getGuiHelper()
        registry.addRecipeCategories(
            Tier2RocketRecipeCategory(guiHelper),
            GasLiquefierRecipeCategory(guiHelper),
            CargoRocketRecipeCategory(guiHelper),
            MethaneSynthesizerRecipeCategory(guiHelper)
        )
    }
}
