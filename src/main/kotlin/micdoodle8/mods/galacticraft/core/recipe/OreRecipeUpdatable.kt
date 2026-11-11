package micdoodle8.mods.galacticraft.core.recipe

import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer

import javax.annotation.Nonnull

import com.google.common.collect.Sets
import com.google.gson.JsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.JsonArray
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.recipe.IRecipeUpdatable
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraft.core.NonNullList
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.OreIngredient
import net.minecraftforge.oredict.ShapedOreRecipe

open class OreRecipeUpdatable : ShapedOreRecipe, IRecipeUpdatable {
    constructor(group: ResourceLocation?, result: Block, vararg recipe: Any?) : super(
        group,
        ItemStack(result),
        *recipe
    )

    constructor(group: ResourceLocation?, result: Item, vararg recipe: Any?) : super(
        group,
        ItemStack(result),
        *recipe
    )

    constructor(group: ResourceLocation?, @Nonnull result: ItemStack, vararg recipe: Any?) : super(
        group,
        result,
        CraftingHelper.parseShaped(*recipe)
    )

    constructor(group: ResourceLocation?, @Nonnull result: ItemStack, primer: CraftingHelper.ShapedPrimer) : super(
        group,
        result,
        primer
    )

    override fun replaceInput(inputA: ItemStack?, inputB: MutableList<ItemStack?>?) {
        if ((inputB)!!.isEmpty()) return

        for (i: Int in this.input.indices) {
            val test: Ingredient = this.input.get(i)
            if (test.apply(inputA)) {
                this.input.set(i, Ingredient.fromStacks(*(inputB)!!.toTypedArray<ItemStack?>()))
            }
        }
    }

    override fun replaceInput(inputB: ItemStack?) {
        for (i: Int in this.input.indices) {
            val test: Ingredient = this.input.get(i)
            if (test is OreIngredient && test.apply(inputB)) {
                this.input.set(i, Ingredient.fromStacks(inputB))
            }
        }
    }

    open class RecipeFactory : IRecipeFactory {
        override fun parse(context: JsonContext, json: JsonObject): IRecipe {
            return factory(context, json)
        }
    }

    companion object {
        @JvmStatic
        fun itemListContains(test: MutableList<*>, stack: ItemStack): Boolean {
            for (b: Any? in test) {
                if (b is ItemStack && ItemStack.areItemsEqual(stack, b) && RecipeUtil.areItemStackTagsEqual(
                        stack,
                        b
                    )
                ) return true
            }
            return false
        }

        @JvmStatic
        fun factory(context: JsonContext, json: JsonObject): OreRecipeUpdatable {
            val group: String = JsonUtils.getString(json, "group", "")
            val ingMap: MutableMap<Char, Ingredient> = Maps.newHashMap<Char, Ingredient>()
            for (entry: MutableMap.MutableEntry<String?, JsonElement?> in JsonUtils.getJsonObject(json, "key")
                .entrySet()) {
                if (entry.key!!.length != 1) throw JsonSyntaxException("Invalid key entry: '" + entry.key + "' is an invalid symbol (must be 1 character only).")
                if (" " == entry.key) throw JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.")

                ingMap.put(entry.key!!.toCharArray()[0], CraftingHelper.getIngredient(entry.value, context))
            }

            ingMap.put(' ', Ingredient.EMPTY)

            val patternJ: JsonArray = JsonUtils.getJsonArray(json, "pattern")

            if (patternJ.size() == 0) throw JsonSyntaxException("Invalid pattern: empty pattern not allowed")

            val pattern: Array<String> = (arrayOfNulls<String>(patternJ.size()) as Array<String>)
            for (x: Int in pattern.indices) {
                val line: String = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]")
                if (x > 0 && pattern[0].length != line.length) throw JsonSyntaxException("Invalid pattern: each row must  be the same width")
                pattern[x] = line
            }

            val primer: CraftingHelper.ShapedPrimer = CraftingHelper.ShapedPrimer()
            primer.width = pattern[0].length
            primer.height = pattern.size
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true)
            primer.input = NonNullList.withSize<Ingredient?>(primer.width * primer.height, Ingredient.EMPTY)

            val keys: MutableSet<Char> = Sets.newHashSet<Char>(ingMap.keys)
            keys.remove(' ')

            var x: Int = 0
            for (line: String in pattern) {
                for (chr: Char in line.toCharArray()) {
                    val ing: Ingredient = ingMap.get(chr)!!
                    if (ing == null) throw JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key")
                    primer.input.set(x++, ing)
                    keys.remove(chr)
                }
            }

            if (!keys.isEmpty()) throw JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys)

            val result: ItemStack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context)
            return OreRecipeUpdatable(if (group.isEmpty()) null else ResourceLocation.parse(group), result, primer)
        }
    }
}
