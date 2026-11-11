package micdoodle8.mods.galacticraft.core.client.jei.refinery

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.world.item.ItemStack

object RefineryRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<RefineryRecipeWrapper?> {
        val recipes: MutableList<RefineryRecipeWrapper?> = ArrayList<RefineryRecipeWrapper?>()

        recipes.add(RefineryRecipeWrapper(ItemStack(GCItems.oilCanister, 1, 1), ItemStack(GCItems.fuelCanister, 1, 1)))
        recipes.add(RefineryRecipeWrapper(ItemStack(GCItems.bucketOil), ItemStack(GCItems.fuelCanister, 1, 1)))
        recipes.add(RefineryRecipeWrapper(ItemStack(GCItems.oilCanister, 1, 1), ItemStack(GCItems.bucketFuel)))
        recipes.add(RefineryRecipeWrapper(ItemStack(GCItems.bucketOil), ItemStack(GCItems.bucketFuel)))

        return recipes
    }
}
