package galaxyspace.systems.SolarSystem.moons.io.recipes

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.recipes.CraftingRecipesIo
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesIo {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
        addBlockRecipes()
    }

    @JvmStatic
    private fun addBlockRecipes() {
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DUNGEON_BLOCKS, 8, 1),
            arrayOf<Any?>("XXX", "XYX", "XXX", 'X', ItemStack(GSBlocks.IO_BLOCKS, 1, 10), 'Y', Items.LAVA_BUCKET)
        )
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.IO_BLOCKS, 1, 3), ItemStack(GCItems.basicItem, 1, 3), 1.0f)
    }
}
