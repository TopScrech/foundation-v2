package micdoodle8.mods.galacticraft.core.client.jei

import mezz.jei.api.BlankModPlugin
import mezz.jei.api.IGuiHelper
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModRegistry
import mezz.jei.api.IRecipeRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.IRecipeWrapperFactory
import mezz.jei.api.recipe.IStackHelper
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import micdoodle8.mods.galacticraft.core.client.jei.buggy.BuggyRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.ingotcompressor.IngotCompressorShapedRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.tier1rocket.Tier1RocketRecipeWrapper
import java.util.LinkedList

import javax.annotation.Nonnull

import mezz.jei.api.*
import mezz.jei.api.recipe.*
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.jei.buggy.BuggyRecipeCategory
import micdoodle8.mods.galacticraft.core.client.jei.buggy.BuggyRecipeMaker
import micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator.CircuitFabricatorRecipeCategory
import micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator.CircuitFabricatorRecipeMaker
import micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator.CircuitFabricatorRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.ingotcompressor.IngotCompressorRecipeCategory
import micdoodle8.mods.galacticraft.core.client.jei.ingotcompressor.IngotCompressorShapelessRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor.OxygenCompressorRecipeCategory
import micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor.OxygenCompressorRecipeMaker
import micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor.OxygenCompressorRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.refinery.RefineryRecipeCategory
import micdoodle8.mods.galacticraft.core.client.jei.refinery.RefineryRecipeMaker
import micdoodle8.mods.galacticraft.core.client.jei.refinery.RefineryRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.tier1rocket.Tier1RocketRecipeCategory
import micdoodle8.mods.galacticraft.core.client.jei.tier1rocket.Tier1RocketRecipeMaker
import micdoodle8.mods.galacticraft.core.inventory.ContainerCrafting
import micdoodle8.mods.galacticraft.core.recipe.ShapedRecipeNBT
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import java.util.*

@JEIPlugin
open class GalacticraftJEI : BlankModPlugin() {
    override fun register(@Nonnull registry: IModRegistry) {
        registryCached = registry
        val stackHelper: IStackHelper = registry.getJeiHelpers().getStackHelper()

        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> Tier1RocketRecipeWrapper((recipe)!!) },
            RecipeCategories.ROCKET_T1_ID
        )
        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> BuggyRecipeWrapper((recipe)!!) },
            RecipeCategories.BUGGY_ID
        )
        registry.handleRecipes<CircuitFabricatorRecipeWrapper?>(
            CircuitFabricatorRecipeWrapper::class.java,
            IRecipeWrapperFactory { recipe: CircuitFabricatorRecipeWrapper? -> recipe },
            RecipeCategories.CIRCUIT_FABRICATOR_ID
        )
        registry.handleRecipes<ShapedRecipesGC?>(
            ShapedRecipesGC::class.java,
            IRecipeWrapperFactory { recipe: ShapedRecipesGC? -> IngotCompressorShapedRecipeWrapper((recipe)!!) },
            RecipeCategories.INGOT_COMPRESSOR_ID
        )
        registry.handleRecipes<ShapelessOreRecipeGC?>(
            ShapelessOreRecipeGC::class.java,
            object : IRecipeWrapperFactory<ShapelessOreRecipeGC> {
                override fun getRecipeWrapper(recipe: ShapelessOreRecipeGC): IRecipeWrapper {
                    return IngotCompressorShapelessRecipeWrapper(stackHelper, recipe)
                }
            },
            RecipeCategories.INGOT_COMPRESSOR_ID
        )
        registry.handleRecipes<RefineryRecipeWrapper?>(
            RefineryRecipeWrapper::class.java,
            IRecipeWrapperFactory { recipe: RefineryRecipeWrapper? -> recipe },
            RecipeCategories.REFINERY_ID
        )
        registry.handleRecipes<OxygenCompressorRecipeWrapper?>(
            OxygenCompressorRecipeWrapper::class.java,
            IRecipeWrapperFactory { recipe: OxygenCompressorRecipeWrapper? -> recipe },
            RecipeCategories.OXYGEN_COMPRESSOR_ID
        )
        registry.handleRecipes<ShapedRecipeNBT?>(
            ShapedRecipeNBT::class.java,
            IRecipeWrapperFactory { recipe: ShapedRecipeNBT? -> NBTSensitiveShapedRecipeWrapper(recipe!!) },
            VanillaRecipeCategoryUid.CRAFTING
        )

        registry.addRecipes(Tier1RocketRecipeMaker.getRecipesList(), RecipeCategories.ROCKET_T1_ID)
        registry.addRecipes(BuggyRecipeMaker.getRecipesList(), RecipeCategories.BUGGY_ID)
        registry.addRecipes(CircuitFabricatorRecipeMaker.getRecipesList(), RecipeCategories.CIRCUIT_FABRICATOR_ID)
        registry.addRecipes(CompressorRecipes.getRecipeListAll(), RecipeCategories.INGOT_COMPRESSOR_ID)
        registry.addRecipes(OxygenCompressorRecipeMaker.getRecipesList(), RecipeCategories.OXYGEN_COMPRESSOR_ID)
        registry.addRecipes(RefineryRecipeMaker.getRecipesList(), RecipeCategories.REFINERY_ID)

        registry.addRecipeCatalyst(
            ItemStack(GCBlocks.nasaWorkbench),
            RecipeCategories.ROCKET_T1_ID,
            RecipeCategories.BUGGY_ID
        )
        registry.addRecipeCatalyst(ItemStack(GCBlocks.machineBase2, 1, 4), RecipeCategories.CIRCUIT_FABRICATOR_ID)
        registry.addRecipeCatalyst(ItemStack(GCBlocks.machineBase, 1, 12), RecipeCategories.INGOT_COMPRESSOR_ID)
        registry.addRecipeCatalyst(ItemStack(GCBlocks.machineBase2, 1, 0), RecipeCategories.INGOT_COMPRESSOR_ID)
        registry.addRecipeCatalyst(ItemStack(GCBlocks.oxygenCompressor), RecipeCategories.OXYGEN_COMPRESSOR_ID)
        registry.addRecipeCatalyst(ItemStack(GCBlocks.refinery), RecipeCategories.REFINERY_ID)
        registry.addRecipeCatalyst(ItemStack(GCBlocks.crafting), VanillaRecipeCategoryUid.CRAFTING)
        registry.getRecipeTransferRegistry()
            .addRecipeTransferHandler<ContainerCrafting?>(MagneticCraftingTransferInfo())

        this.addInformationPages(registry)
        GCItems.hideItemsJEI(registry.getJeiHelpers().getIngredientBlacklist())
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper: IGuiHelper = registry.getJeiHelpers().getGuiHelper()
        ingotCompressorCategory = IngotCompressorRecipeCategory(guiHelper)
        registry.addRecipeCategories(
            Tier1RocketRecipeCategory(guiHelper),
            BuggyRecipeCategory(guiHelper),
            CircuitFabricatorRecipeCategory(guiHelper),
            ingotCompressorCategory,
            OxygenCompressorRecipeCategory(guiHelper),
            RefineryRecipeCategory(guiHelper)
        )
    }

    private fun addInformationPages(registry: IModRegistry) {
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.oxygenPipe),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.fluid_pipe.info")
        )
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.fuelLoader),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.fuel_loader.info")
        )
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.oxygenCollector),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.oxygen_collector.info")
        )
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.oxygenDistributor),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.oxygen_distributor.info")
        )
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.oxygenSealer),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.oxygen_sealer.info")
        )
        if (CompatibilityManager.isAppEngLoaded()) {
            registry.addIngredientInfo<ItemStack?>(
                ItemStack(GCBlocks.machineBase2),
                ItemStack::class.java,
                *arrayOf<String?>(
                    GCCoreUtil.translate("jei.electric_compressor.info"),
                    GCCoreUtil.translate("jei.electric_compressor.appeng.info")
                )
            )
        } else {
            registry.addIngredientInfo<ItemStack?>(
                ItemStack(GCBlocks.machineBase2),
                ItemStack::class.java,
                GCCoreUtil.translate("jei.electric_compressor.info")
            )
        }
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.crafting),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.magnetic_crafting.info")
        )
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCBlocks.brightLamp),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.arc_lamp.info")
        )
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GCItems.wrench),
            ItemStack::class.java,
            GCCoreUtil.translate("jei.wrench.info")
        )
    }

    override fun onRuntimeAvailable(rt: IJeiRuntime) {
        recipesCached = rt.getRecipeRegistry()
    }

    companion object {
        private var registryCached: IModRegistry? = null
        private var recipesCached: IRecipeRegistry? = null

        private var hiddenSteel = false
        private var hiddenAdventure = false

        @JvmField
        var hidden: MutableList<IRecipeWrapper> = LinkedList<IRecipeWrapper>()
        private var ingotCompressorCategory: IRecipeCategory<IRecipeWrapper>? = null

        @JvmStatic
        fun updateHidden(hideSteel: Boolean, hideAdventure: Boolean) {
            var changeHidden: Boolean = false
            if (hideSteel != hiddenSteel) {
                hiddenSteel = hideSteel
                changeHidden = true
            }
            if (hideAdventure != hiddenAdventure) {
                hiddenAdventure = hideAdventure
                changeHidden = true
            }
            if (changeHidden && recipesCached != null) {
                unhide()
                val toHide: MutableList<IRecipe> = CompressorRecipes.getRecipeListHidden(hideSteel, hideAdventure)
                hidden.clear()
                val allRW: MutableList<IRecipeWrapper?> =
                    (recipesCached!!.getRecipeWrappers<IRecipeWrapper?>(ingotCompressorCategory))!!
                for (recipe: IRecipe in toHide) {
                    hidden.add(
                        recipesCached!!.getRecipeWrapper(
                            recipe,
                            micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories.INGOT_COMPRESSOR_ID
                        )!!
                    )
                }
                hide()
            }
        }

        @JvmStatic
        private fun hide() {
            for (wrapper: IRecipeWrapper in hidden) {
                recipesCached!!.hideRecipe(wrapper)
            }
        }

        @JvmStatic
        private fun unhide() {
            for (wrapper: IRecipeWrapper in hidden) {
                recipesCached!!.unhideRecipe(wrapper)
            }
        }
    }
}
