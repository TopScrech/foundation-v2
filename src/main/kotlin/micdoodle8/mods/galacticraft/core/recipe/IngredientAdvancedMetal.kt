package micdoodle8.mods.galacticraft.core.recipe

import javax.annotation.Nonnull

import com.google.gson.JsonObject
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext

open class IngredientAdvancedMetal : IIngredientFactory {
    @Nonnull
    override fun parse(context: JsonContext?, json: JsonObject): Ingredient {
        val metal: String = JsonUtils.getString(json, "metal")
        if (metal == "meteoric_iron_ingot") {
            return Ingredient.fromStacks(
                ItemStack(
                    GCItems.itemBasicMoon,
                    1,
                    0
                )
            ) // : new OreIngredient("ingotMeteoricIron");
        }
        if (metal == "meteoric_iron_plate") {
            return Ingredient.fromStacks(
                ItemStack(
                    GCItems.itemBasicMoon,
                    1,
                    1
                )
            ) // : new OreIngredient("compressedMeteoricIron");
        }
        if (metal == "desh_ingot") {
            return if (GalacticraftCore.isPlanetsLoaded) Ingredient.fromStacks(
                ItemStack(
                    MarsItems.marsItemBasic,
                    1,
                    2
                )
            ) else Ingredient.fromItem(GCItems.heavyPlatingTier1)
        }
        if (metal == "desh_plate") {
            return Ingredient.fromStacks(
                ItemStack(
                    MarsItems.marsItemBasic,
                    1,
                    5
                )
            ) // : new OreIngredient("compressedDesh");
        }
        if (metal == "titanium_ingot") {
            return Ingredient.fromStacks(
                ItemStack(
                    AsteroidsItems.basicItem,
                    1,
                    0
                )
            ) // : new OreIngredient("ingotTitanium");
        }
        if (metal == "titanium_plate") {
            return Ingredient.fromStacks(
                ItemStack(
                    AsteroidsItems.basicItem,
                    1,
                    6
                )
            ) // : new OreIngredient("compressedTitanium");
        }
        if (metal == "lead_ingot") {
            return Ingredient.fromStacks(ItemStack(VenusItems.basicItem, 1, 1)) // : new OreIngredient("ingotLead");
        }
        return Ingredient.fromItem(GCItems.infiniteBatery)
    }
}
