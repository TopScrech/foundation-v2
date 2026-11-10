package micdoodle8.mods.galacticraft.core.recipe

import javax.annotation.Nonnull

import com.google.gson.JsonObject
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.OreIngredient

open class IngredientSilicon : IIngredientFactory {
    @Nonnull
    override fun parse(context: JsonContext?, json: JsonObject?): Ingredient {
        return OreIngredient(ConfigManagerCore.otherModsSilicon)
    }
}
