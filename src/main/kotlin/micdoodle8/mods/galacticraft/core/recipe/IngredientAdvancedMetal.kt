package micdoodle8.mods.galacticraft.core.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import foundation.item.FoundationItemVariants
import foundation.recipe.FoundationStackIngredient
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer

/** Read the retained metal selectors and emit canonical component-based stack ingredients */
object IngredientAdvancedMetal {
    @JvmField val ID: Identifier = Identifier.parse("galacticraftcore:advanced_metal")
    private data class Metal(val item: Identifier, val metadata: Int)
    private val metals = mapOf(
        "meteoric_iron_ingot" to Metal(Identifier.parse("galacticraftcore:item_basic_moon"), 0),
        "meteoric_iron_plate" to Metal(Identifier.parse("galacticraftcore:item_basic_moon"), 1),
        "desh_ingot" to Metal(Identifier.parse("galacticraftplanets:item_basic_mars"), 2),
        "desh_plate" to Metal(Identifier.parse("galacticraftplanets:item_basic_mars"), 5),
        "titanium_ingot" to Metal(Identifier.parse("galacticraftplanets:item_basic_asteroids"), 0),
        "titanium_plate" to Metal(Identifier.parse("galacticraftplanets:item_basic_asteroids"), 6),
        "lead_ingot" to Metal(Identifier.parse("galacticraftplanets:basic_item_venus"), 1)
    )

    private fun decode(name: String): DataResult<FoundationStackIngredient> {
        val metal = metals[name] ?: return DataResult.error { "Unknown advanced metal $name" }
        if (!BuiltInRegistries.ITEM.containsKey(metal.item)) return DataResult.error { "Missing advanced metal item ${metal.item}" }
        val item = BuiltInRegistries.ITEM.getValue(metal.item)
        return try {
            DataResult.success(FoundationStackIngredient.of(FoundationItemVariants.stack(item, 1, metal.metadata)))
        } catch (cause: IllegalArgumentException) {
            DataResult.error { cause.message ?: "Invalid advanced metal $name" }
        }
    }

    private fun encode(ingredient: FoundationStackIngredient): DataResult<String> {
        val stack = ingredient.matchingStack()
        val identity = Metal(BuiltInRegistries.ITEM.getKey(stack.item), FoundationItemVariants.metadata(stack))
        val name = metals.entries.firstOrNull { it.value == identity }?.key
        return if (name == null) DataResult.error { "Stack is not a declared advanced metal" } else DataResult.success(name)
    }

    @JvmField val CODEC: MapCodec<FoundationStackIngredient> = Codec.STRING.fieldOf("metal").flatXmap(::decode, ::encode)
    @JvmField val SERIALIZER: IIngredientSerializer<FoundationStackIngredient> =
        IIngredientSerializer.simple(CODEC, FoundationStackIngredient.STREAM_CODEC)
}
