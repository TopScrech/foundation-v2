package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import foundation.persistence.FoundationItemStackPersistence
import foundation.recipe.FoundationStackIngredient
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.items.ItemEmergencyKit
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import java.nio.file.Files
import java.nio.file.Path

fun verifyEmergencyKitRecipe(resources: Path, lookup: HolderLookup.Provider) {
    val contents = ItemEmergencyKit.contents()
    val ops = RegistryOps.create(JsonOps.INSTANCE, lookup)
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val loose = FoundationStackIngredient.of(contents[6])
    val exact = FoundationStackIngredient.of(contents[6], matchComponents = true)
    check(loose.test(contents[7]) && !exact.test(contents[7]) && exact.test(contents[6].copyWithCount(23)))
    check(loose != exact && exact != loose)
    val anyPotion = Ingredient.of(contents[6].item)
    check(exact != anyPotion && anyPotion != exact)
    for (ingredient in listOf(loose, exact)) {
        val json = Ingredient.CODEC.encodeStart(ops, ingredient).getOrThrow()
        check(json.asJsonObject.has("match_components") == (ingredient === exact))
        val parsed = Ingredient.CODEC.parse(ops, json).getOrThrow()
        check(parsed == ingredient && ingredient == parsed)
        check(parsed.test(contents[6]) && parsed.test(contents[7]) == (ingredient === loose))
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient)
            val decoded = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)
            check(decoded.test(contents[6]) && decoded.test(contents[7]) == (ingredient === loose))
        } finally { buffer.release() }
    }
    val file = resources.resolve("data/galacticraftcore/recipe/emergency_kit.json")
    val json = JsonParser.parseString(Files.readString(file))
    val recipe = Recipe.CODEC.parse(ops, json).getOrThrow().value() as ShapedRecipe
    val pattern = ShapedRecipePattern.MAP_CODEC.codec().parse(ops, json).getOrThrow()
    check(recipe.width == 3 && recipe.height == 3)
    val layout = "EABCIDFGH".map { contents[it - 'A'].copy() }
    val normal = CraftingInput.of(3, 3, layout)
    check(pattern.matches(normal))
    val importedLayout = layout.map { stack ->
        val old = CompoundTag().also { tag ->
            tag.putString("id", BuiltInRegistries.ITEM.getKey(stack.item).toString())
            tag.putByte("Count", 1)
            tag.putShort("Damage", (if (stack.item is FoundationVariantItem) FoundationItemVariants.metadata(stack) else stack.damageValue).toShort())
            stack.get(DataComponents.POTION_CONTENTS)?.potion()?.ifPresent { potion ->
                tag.put("tag", CompoundTag().also { it.putString("Potion", potion.unwrapKey().orElseThrow().identifier().toString()) })
            }
        }
        FoundationItemStackPersistence.read(old, lookup)
    }
    check(pattern.matches(CraftingInput.of(3, 3, importedLayout)))
    val mirrored = layout.chunked(3).flatMap { it.reversed() }
    check(pattern.matches(CraftingInput.of(3, 3, mirrored)))
    check(pattern.matches(CraftingInput.of(3, 3, layout.map { it.copyWithCount(64) })))
    val result = recipe.assemble(normal)
    check(BuiltInRegistries.ITEM.getKey(result.item) == Identifier.parse("galacticraftcore:emergency_kit") && result.count == 1)
    result.shrink(1)
    check(recipe.assemble(normal).count == 1)
    for (slot in layout.indices) {
        val missing = layout.map { it.copy() }.toMutableList().also { it[slot] = ItemStack.EMPTY }
        check(!pattern.matches(CraftingInput.of(3, 3, missing)))
        val tagged = layout.map { it.copy() }
        tagged[slot].set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also { it.putInt("unexpected", 1) }))
        check(!pattern.matches(CraftingInput.of(3, 3, tagged)))
        val renamed = layout.map { it.copy() }
        renamed[slot].set(DataComponents.CUSTOM_NAME, Component.literal("Renamed input"))
        check(!pattern.matches(CraftingInput.of(3, 3, renamed)))
    }
    val alternatives = listOf(
        0 to layout[0].copy().also { it.damageValue = 1 },
        3 to layout[3].copy().also { ItemOxygenTank.setUsed(it, 1) },
        4 to FoundationItemVariants.stack(contents[8].item, 1, 0),
        6 to FoundationItemVariants.stack(contents[5].item, 1, 2),
        7 to contents[7],
        8 to contents[6]
    )
    for ((slot, stack) in alternatives) {
        val wrong = layout.map { it.copy() }.toMutableList().also { it[slot] = stack.copy() }
        check(!pattern.matches(CraftingInput.of(3, 3, wrong)))
    }
    val wire = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
    try {
        ShapedRecipe.STREAM_CODEC.encode(wire, recipe)
        val decoded = ShapedRecipe.STREAM_CODEC.decode(wire)
        check(ItemStack.matches(decoded.assemble(normal), recipe.assemble(normal)))
        for (slot in layout.indices) {
            val ingredient = decoded.ingredients[slot].orElseThrow()
            check(ingredient.test(layout[slot]))
            val bad = layout[slot].copy().also { it.set(DataComponents.CUSTOM_NAME, Component.literal("Rejected name")) }
            check(!ingredient.test(bad))
        }
        check(!decoded.ingredients[7].orElseThrow().test(contents[7]) && !decoded.ingredients[8].orElseThrow().test(contents[6]))
    } finally { wire.release() }
    println("Passed native emergency kit recipe codecs, exact component ingredient mode, count-independent matching, mirrored layouts, and rejection of wrong potions, metadata, tank usage, tool damage, names, custom data, and missing inputs")
}
