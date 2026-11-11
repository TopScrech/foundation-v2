package galaxyspace.core.client.jei

import java.util.LinkedList
import java.util.List
import galaxyspace.core.client.jei.assembler.AssemblerShapedRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.body.BodyRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.booster.BoosterRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.engine.EngineRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.nosecone.NoseConeRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.oxtank.OxTankRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.stabiliser.StabiliserRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.tier2rocket.RocketTier2RecipeWrapper
import galaxyspace.core.client.jei.rocketassembler.RocketAssemblerShapedRecipeWrapper
import mezz.jei.api.BlankModPlugin
import mezz.jei.api.IGuiHelper
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModRegistry
import mezz.jei.api.IRecipeRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategory

import javax.annotation.Nonnull

import galaxyspace.core.GSBlocks
import galaxyspace.core.GSItems
import galaxyspace.core.client.jei.assembler.AssemblerRecipeCategory
import galaxyspace.core.client.jei.assembler.AssemblerShapelessRecipeWrapper
import galaxyspace.core.client.jei.nasaworkbench.body.BodyRecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.body.BodyRecipeMaker
import galaxyspace.core.client.jei.nasaworkbench.booster.BoosterRecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.booster.BoosterRecipeMaker
import galaxyspace.core.client.jei.nasaworkbench.engine.EngineRecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.engine.EngineRecipeMaker
import galaxyspace.core.client.jei.nasaworkbench.nosecone.NoseConeRecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.nosecone.NoseConeRecipeMaker
import galaxyspace.core.client.jei.nasaworkbench.oxtank.OxTankRecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.oxtank.OxTankRecipeMaker
import galaxyspace.core.client.jei.nasaworkbench.stabiliser.StabiliserRecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.stabiliser.StabiliserRecipeMaker
import galaxyspace.core.client.jei.nasaworkbench.tier2rocket.RocketTier2RecipeCategory
import galaxyspace.core.client.jei.nasaworkbench.tier2rocket.RocketTier2RecipeMaker
import galaxyspace.core.client.jei.recycler.UniversalRecyclerRecipeCategory
import galaxyspace.core.client.jei.recycler.UniversalRecyclerRecipeMaker
import galaxyspace.core.client.jei.recycler.UniversalRecyclerRecipeWrapper
import galaxyspace.core.client.jei.rocketassembler.RocketAssemblerRecipeCategory
import galaxyspace.core.client.jei.rocketassembler.RocketAssemblerShapelessRecipeWrapper
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.AssemblyRecipes
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RocketAssemblyRecipes
import mezz.jei.api.*
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.IRecipeWrapperFactory
import mezz.jei.api.recipe.IStackHelper
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.item.ItemStack
import java.util.*

@JEIPlugin
open class GalaxySpaceJEI : BlankModPlugin() {
    //private static IRecipeCategory ingotCompressorCategory;
    override fun register(@Nonnull registry: IModRegistry) {
        registryCached = registry
        val stackHelper: IStackHelper = registry.getJeiHelpers().getStackHelper()

        if (GSConfigCore.enableAdvancedRocketCraft) {
            registry.handleRecipes<INasaWorkbenchRecipe?>(
                INasaWorkbenchRecipe::class.java,
                IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> NoseConeRecipeWrapper((recipe)!!) },
                GSRecipeCategories.NOSE_CONE
            )
            registry.handleRecipes<INasaWorkbenchRecipe?>(
                INasaWorkbenchRecipe::class.java,
                IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> BodyRecipeWrapper((recipe)!!) },
                GSRecipeCategories.BODY
            )
            registry.handleRecipes<INasaWorkbenchRecipe?>(
                INasaWorkbenchRecipe::class.java,
                IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> EngineRecipeWrapper((recipe)!!) },
                GSRecipeCategories.ENGINE
            )
            registry.handleRecipes<INasaWorkbenchRecipe?>(
                INasaWorkbenchRecipe::class.java,
                IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> BoosterRecipeWrapper((recipe)!!) },
                GSRecipeCategories.BOOSTER
            )
            registry.handleRecipes<INasaWorkbenchRecipe?>(
                INasaWorkbenchRecipe::class.java,
                IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> StabiliserRecipeWrapper((recipe)!!) },
                GSRecipeCategories.STABILISER
            )
        }
        registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> OxTankRecipeWrapper((recipe)!!) },
            GSRecipeCategories.OX_TANK
        )

        if (GSConfigCore.enableAdvancedRocketCraft) registry.handleRecipes<INasaWorkbenchRecipe?>(
            INasaWorkbenchRecipe::class.java,
            IRecipeWrapperFactory { recipe: INasaWorkbenchRecipe? -> RocketTier2RecipeWrapper((recipe)!!) },
            GSRecipeCategories.ROCKET_TIER_2
        )

        registry.handleRecipes<ShapedRecipesGC?>(
            ShapedRecipesGC::class.java,
            IRecipeWrapperFactory { recipe: ShapedRecipesGC? -> AssemblerShapedRecipeWrapper((recipe)!!) },
            GSRecipeCategories.ASSEMBLER
        )
        registry.handleRecipes<ShapelessOreRecipeGC?>(
            ShapelessOreRecipeGC::class.java,
            object : IRecipeWrapperFactory<ShapelessOreRecipeGC> {
                override fun getRecipeWrapper(recipe: ShapelessOreRecipeGC): IRecipeWrapper {
                    return AssemblerShapelessRecipeWrapper(stackHelper, recipe)
                }
            },
            GSRecipeCategories.ASSEMBLER
        )

        registry.handleRecipes<ShapedRecipesGC?>(
            ShapedRecipesGC::class.java,
            IRecipeWrapperFactory { recipe: ShapedRecipesGC? -> RocketAssemblerShapedRecipeWrapper((recipe)!!) },
            GSRecipeCategories.ROCKET_ASSEMBLER
        )
        registry.handleRecipes<ShapelessOreRecipeGC?>(
            ShapelessOreRecipeGC::class.java,
            object : IRecipeWrapperFactory<ShapelessOreRecipeGC> {
                override fun getRecipeWrapper(recipe: ShapelessOreRecipeGC): IRecipeWrapper {
                    return RocketAssemblerShapelessRecipeWrapper(stackHelper, recipe)
                }
            },
            GSRecipeCategories.ROCKET_ASSEMBLER
        )

        registry.handleRecipes<UniversalRecyclerRecipeWrapper?>(
            UniversalRecyclerRecipeWrapper::class.java,
            IRecipeWrapperFactory { recipe: UniversalRecyclerRecipeWrapper? -> recipe },
            GSRecipeCategories.UNIVERSAL_RECYCLER
        )

        registry.addRecipes(RocketAssemblyRecipes.getRecipeList(), GSRecipeCategories.ROCKET_ASSEMBLER)
        registry.addRecipes(AssemblyRecipes.getRecipeList(), GSRecipeCategories.ASSEMBLER)
        registry.addRecipes(UniversalRecyclerRecipeMaker.getRecipesList(), GSRecipeCategories.UNIVERSAL_RECYCLER)

        if (GSConfigCore.enableAdvancedRocketCraft) {
            registry.addRecipes(NoseConeRecipeMaker.getRecipesList(), GSRecipeCategories.NOSE_CONE)
            registry.addRecipes(BodyRecipeMaker.getRecipesList(), GSRecipeCategories.BODY)
            registry.addRecipes(EngineRecipeMaker.getRecipesList(), GSRecipeCategories.ENGINE)
            registry.addRecipes(BoosterRecipeMaker.getRecipesList(), GSRecipeCategories.BOOSTER)
            registry.addRecipes(StabiliserRecipeMaker.getRecipesList(), GSRecipeCategories.STABILISER)
            registry.addRecipes(RocketTier2RecipeMaker.getRecipesList(), GSRecipeCategories.ROCKET_TIER_2)
        }
        registry.addRecipes(OxTankRecipeMaker.getRecipesList(), GSRecipeCategories.OX_TANK)

        registry.addRecipeCatalyst(ItemStack(GSBlocks.ASSEMBLER, 1, 0), GSRecipeCategories.ASSEMBLER)
        registry.addRecipeCatalyst(ItemStack(GSBlocks.UNIVERSAL_RECYCLER, 1, 0), GSRecipeCategories.UNIVERSAL_RECYCLER)
        registry.addRecipeCatalyst(ItemStack(GSBlocks.ROCKET_ASSEMBLER, 1, 0), GSRecipeCategories.ROCKET_ASSEMBLER)
        registry.addRecipeCatalyst(
            ItemStack(GCBlocks.nasaWorkbench),
            GSRecipeCategories.ROCKET_TIER_2,
            GSRecipeCategories.NOSE_CONE,
            GSRecipeCategories.BODY,
            GSRecipeCategories.ENGINE,
            GSRecipeCategories.BOOSTER,
            GSRecipeCategories.STABILISER,
            GSRecipeCategories.OX_TANK
        )

        /*registry.addRecipes(OxygenCompressorRecipeMaker.getRecipesList(), RecipeCategories.OXYGEN_COMPRESSOR_ID);
        registry.addRecipes(RefineryRecipeMaker.getRecipesList(), RecipeCategories.REFINERY_ID);

  		registry.addRecipeCatalyst(new ItemStack(GCBlocks.nasaWorkbench), RecipeCategories.ROCKET_T1_ID, RecipeCategories.BUGGY_ID);
        registry.addRecipeCatalyst(new ItemStack(GCBlocks.machineBase2, 1, 4), RecipeCategories.CIRCUIT_FABRICATOR_ID);
        registry.addRecipeCatalyst(new ItemStack(GCBlocks.machineBase, 1, 12), RecipeCategories.INGOT_COMPRESSOR_ID);
        registry.addRecipeCatalyst(new ItemStack(GCBlocks.machineBase2, 1, 0), RecipeCategories.INGOT_COMPRESSOR_ID);
        registry.addRecipeCatalyst(new ItemStack(GCBlocks.oxygenCompressor), RecipeCategories.OXYGEN_COMPRESSOR_ID);
        registry.addRecipeCatalyst(new ItemStack(GCBlocks.refinery), RecipeCategories.REFINERY_ID);
        registry.addRecipeCatalyst(new ItemStack(GCBlocks.crafting), VanillaRecipeCategoryUid.CRAFTING);*/
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(new MagneticCraftingTransferInfo());
        registry.addIngredientInfo<ItemStack?>(
            ItemStack(GSItems.BASIC, 1, 17),
            ItemStack::class.java,
            GCCoreUtil.translate("gui.ice_bucket.desc")
        )
        this.addInformationPages(registry)

        //GCItems.hideItemsJEI(registry.getJeiHelpers().getIngredientBlacklist());
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper: IGuiHelper = registry.getJeiHelpers().getGuiHelper()

        registry.addRecipeCategories(AssemblerRecipeCategory(guiHelper))
        registry.addRecipeCategories(RocketAssemblerRecipeCategory(guiHelper))
        registry.addRecipeCategories(UniversalRecyclerRecipeCategory(guiHelper))
        registry.addRecipeCategories(OxTankRecipeCategory(guiHelper))
        if (GSConfigCore.enableAdvancedRocketCraft) {
            registry.addRecipeCategories(NoseConeRecipeCategory(guiHelper))
            registry.addRecipeCategories(BodyRecipeCategory(guiHelper))
            registry.addRecipeCategories(EngineRecipeCategory(guiHelper))
            registry.addRecipeCategories(BoosterRecipeCategory(guiHelper))
            registry.addRecipeCategories(StabiliserRecipeCategory(guiHelper))
            registry.addRecipeCategories(RocketTier2RecipeCategory(guiHelper))
        }
    }

    private fun addInformationPages(registry: IModRegistry) { /*
        registry.addIngredientInfo(new ItemStack(GCBlocks.oxygenPipe), ItemStack.class, GCCoreUtil.translate("jei.fluid_pipe.info"));
        registry.addIngredientInfo(new ItemStack(GCBlocks.fuelLoader), ItemStack.class, GCCoreUtil.translate("jei.fuel_loader.info"));
        registry.addIngredientInfo(new ItemStack(GCBlocks.oxygenCollector), ItemStack.class, GCCoreUtil.translate("jei.oxygen_collector.info"));
        registry.addIngredientInfo(new ItemStack(GCBlocks.oxygenDistributor), ItemStack.class, GCCoreUtil.translate("jei.oxygen_distributor.info"));
        registry.addIngredientInfo(new ItemStack(GCBlocks.oxygenSealer), ItemStack.class, GCCoreUtil.translate("jei.oxygen_sealer.info"));
        if (CompatibilityManager.isAppEngLoaded())
        {
            registry.addIngredientInfo(new ItemStack(GCBlocks.machineBase2), ItemStack.class, new String [] { GCCoreUtil.translate("jei.electric_compressor.info"), GCCoreUtil.translate("jei.electric_compressor.appeng.info") });
        }
        else
        {
            registry.addIngredientInfo(new ItemStack(GCBlocks.machineBase2), ItemStack.class, GCCoreUtil.translate("jei.electric_compressor.info"));
        }
        registry.addIngredientInfo(new ItemStack(GCBlocks.crafting), ItemStack.class, GCCoreUtil.translate("jei.magnetic_crafting.info"));
        registry.addIngredientInfo(new ItemStack(GCBlocks.brightLamp), ItemStack.class, GCCoreUtil.translate("jei.arc_lamp.info"));
        registry.addIngredientInfo(new ItemStack(GCItems.wrench), ItemStack.class, GCCoreUtil.translate("jei.wrench.info"));
    */
    }

    override fun onRuntimeAvailable(rt: IJeiRuntime) {
        recipesCached = rt.getRecipeRegistry()

        if (GSConfigCore.enableAdvancedRocketCraft) {
            recipesCached!!.hideRecipeCategory(RecipeCategories.ROCKET_T2_ID)
            recipesCached!!.hideRecipeCategory(RecipeCategories.ROCKET_T3_ID)
        }
    } /*
    public static void updateHidden(boolean hideSteel, boolean hideAdventure)
    {
        boolean changeHidden = false;
        if (hideSteel != hiddenSteel)
        {
            hiddenSteel = hideSteel;
            changeHidden = true;
        }
        if (hideAdventure != hiddenAdventure)
        {
            hiddenAdventure = hideAdventure;
            changeHidden = true;
        }
        if (changeHidden && recipesCached != null)
        {
            unhide();
            List<IRecipe> toHide = CompressorRecipes.getRecipeListHidden(hideSteel, hideAdventure);
            hidden.clear();
            List<IRecipeWrapper> allRW = recipesCached.getRecipeWrappers(ingotCompressorCategory);
            for (IRecipe recipe : toHide)
            {
                hidden.add(recipesCached.getRecipeWrapper(recipe, RecipeCategories.INGOT_COMPRESSOR_ID));
            }
            hide();
        }
    }
    
    private static void hide()
    {
        for (IRecipeWrapper wrapper : hidden)
        {
            recipesCached.hideRecipe(wrapper);
        }
    }

    private static void unhide()
    {
        for (IRecipeWrapper wrapper : hidden)
        {
            recipesCached.unhideRecipe(wrapper);
        }
    }*/

    companion object {
        private var registryCached: IModRegistry? = null
        private var recipesCached: IRecipeRegistry? = null

        //private static boolean hiddenSteel = false;
        //private static boolean hiddenAdventure = false;
        @JvmField
        var hidden: MutableList<IRecipeWrapper?> = LinkedList<IRecipeWrapper?>()
    }
}
