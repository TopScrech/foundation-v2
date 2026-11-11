package foundation.recipe

import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.ByteBufCodecs
import com.mojang.datafixers.util.Either
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import net.minecraft.core.HolderSet
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Item
import net.minecraft.tags.TagKey
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraftforge.common.crafting.ingredients.AbstractIngredient
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer
import java.util.Optional

// Vanilla Ingredient equality compares its holder set, even when the other ingredient is custom
// A distinct literal holder view keeps mixed item-only and variant predicates asymmetric in patterns
private class VariantItems(private val item: Holder<Item>) : HolderSet.ListBacked<Item>() {
    override fun contents(): List<Holder<Item>> = listOf(item)
    override fun isBound(): Boolean = true
    override fun unwrap(): Either<TagKey<Item>, List<Holder<Item>>> = Either.right(contents())
    override fun unwrapKey(): Optional<TagKey<Item>> = Optional.empty()
    override fun contains(value: Holder<Item>): Boolean = value == item
}

/** Retains legacy item and variant matching while leaving unrelated custom data unrestricted */
class FoundationStackIngredient private constructor(private val template: ItemStackTemplate, private val matchComponents: Boolean) :
    AbstractIngredient(VariantItems(template.item())) {
    private val exemplar = template.create()

    init {
        require(!exemplar.isEmpty) { "Ingredient requires a nonempty stack" }
        val item = exemplar.item
        if (item is FoundationVariantItem) require(item.variant(FoundationItemVariants.metadata(exemplar)) != null) {
            "Ingredient contains an undeclared item variant"
        }
    }

    override fun test(input: ItemStack): Boolean = !input.isEmpty && if (matchComponents) {
        ItemStack.isSameItemSameComponents(input, exemplar)
    } else FoundationItemVariants.sameVariant(input, exemplar)
    override fun isSimple(): Boolean = false
    fun matchingStack(): ItemStack = template.create()
    override fun display(): SlotDisplay = SlotDisplay.ItemStackSlotDisplay(template)
    override fun serializer(): IIngredientSerializer<FoundationStackIngredient> = SERIALIZER
    override fun equals(other: Any?): Boolean = other is FoundationStackIngredient && template == other.template && matchComponents == other.matchComponents
    override fun hashCode(): Int = 31 * template.hashCode() + matchComponents.hashCode()

    companion object {
        fun of(stack: ItemStack, matchComponents: Boolean = false): FoundationStackIngredient =
            FoundationStackIngredient(ItemStackTemplate.fromNonEmptyStack(stack, 1), matchComponents)

        private fun decode(template: ItemStackTemplate, matchComponents: Boolean): DataResult<FoundationStackIngredient> = try {
            DataResult.success(FoundationStackIngredient(template.withCount(1), matchComponents))
        } catch (cause: IllegalArgumentException) {
            DataResult.error { cause.message ?: "Invalid ingredient stack" }
        }

        @JvmField val CODEC: MapCodec<FoundationStackIngredient> = RecordCodecBuilder.mapCodec<Pair<ItemStackTemplate, Boolean>> { instance ->
            instance.group(
                ItemStackTemplate.CODEC.fieldOf("stack").forGetter<Pair<ItemStackTemplate, Boolean>> { it.first },
                Codec.BOOL.optionalFieldOf("match_components", false).forGetter<Pair<ItemStackTemplate, Boolean>> { it.second }
            ).apply(instance) { stack, exact -> stack to exact }
        }.flatXmap({ (stack, exact) -> decode(stack, exact) }, { DataResult.success(it.template to it.matchComponents) })
        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FoundationStackIngredient> = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, { it.template },
            ByteBufCodecs.BOOL, { it.matchComponents },
            { stack, exact -> FoundationStackIngredient(stack.withCount(1), exact) }
        )
        @JvmField val SERIALIZER: IIngredientSerializer<FoundationStackIngredient> = IIngredientSerializer.simple(CODEC, STREAM_CODEC)
    }
}
