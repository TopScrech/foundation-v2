package micdoodle8.mods.galacticraft.core.recipe

import com.google.gson.JsonObject
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.JsonContext
import java.lang.IllegalStateException
import java.util.function.BooleanSupplier

open class ConditionEnabled : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject?): BooleanSupplier {
        if (JsonUtils.hasField(json, "data")) {
            val data: String = JsonUtils.getString(json, "data")
            if (data == "can_default") {
                return BooleanSupplier { !ConfigManagerCore.alternateCanisterRecipe }
            }
            if (data == "can_alt") {
                return BooleanSupplier { ConfigManagerCore.alternateCanisterRecipe }
            }
            if (data == "aa_loaded") {
                return BooleanSupplier { !CompatibilityManager.modAALoaded }
            }
            GCLog.severe("Unrecognised condition data: " + data)
        }

        throw IllegalStateException(
            "Galacticraft recipe JSON condition error in recipe for " + CraftingHelper.getItemStack(
                json,
                context
            )
        )
    }
}
