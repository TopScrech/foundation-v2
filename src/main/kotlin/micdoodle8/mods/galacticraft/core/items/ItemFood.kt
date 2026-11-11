package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import java.util.function.Consumer

open class ItemFood(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).rarity(Rarity.RARE)
            .food(foodProperties(0))
            .component(DataComponents.ITEM_MODEL, Identifier.parse("galacticraftcore:food"))
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL
    override fun creativeMetadata(): List<Int> = listOf(0, 1, 2, 3, 9, 4, 5, 6, 7, 8)
    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val translation = if (metadata < 4) "item.basic_item.canned_food.name" else "item.food.$name.name"
        val model = if (metadata == 0) "food" else name
        return FoundationItemVariant(translation, Identifier.parse("galacticraftcore:item/$model"), ChatFormatting.BLUE)
    }

    override fun configureVariant(stack: ItemStack, metadata: Int) {
        stack.set(DataComponents.FOOD, foodProperties(metadata))
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        configureVariant(stack, FoundationItemVariants.metadata(stack))
        return super.use(level, player, hand)
    }

    fun consumptionRemainder(stack: ItemStack): ItemStack {
        if (stack.isEmpty) return ItemStack.EMPTY
        val metadata = FoundationItemVariants.metadata(stack)
        require(variant(metadata) != null) { "Unknown food variant $metadata" }
        if (metadata !in 0..3 && metadata != 9) return ItemStack.EMPTY
        val canister = FoundationItemProperties.registered(Identifier.parse("galacticraftcore:canister"))
        return FoundationItemVariants.stack(canister, 1, 0)
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (stack.isEmpty) return stack
        // Resolve the legacy dropped container before consumption so missing registrations cannot destroy it
        val remainder = consumptionRemainder(stack)
        configureVariant(stack, FoundationItemVariants.metadata(stack))
        val result = super.finishUsingItem(stack, level, entity)
        if (level is ServerLevel && !remainder.isEmpty) entity.spawnAtLocation(level, remainder, 0f)
        return result
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val metadata = FoundationItemVariants.metadata(stack)
        val key = when (metadata) {
            in 0..3 -> "item.basic_item.${names[metadata]}.name"
            8 -> "item.food.cheeseburger.desc"
            else -> return
        }
        builder.accept(Component.translatable(key).withStyle(ChatFormatting.GREEN))
    }

    companion object {
        @JvmField val names: Array<String> = arrayOf(
            "dehydrated_apple", "dehydrated_carrot", "dehydrated_melon", "dehydrated_potato", "cheese_slice",
            "burger_bun", "beef_patty_raw", "beef_patty_cooked", "cheeseburger", "canned_beef"
        )
        private val nutrition = listOf(8, 8, 4, 2, 2, 4, 2, 4, 14, 8)
        private val saturationModifiers = listOf(0.3f, 0.6f, 0.3f, 0.3f, 0.1f, 0.8f, 0.3f, 0.6f, 1f, 0.6f)
        @JvmStatic fun foodProperties(metadata: Int): FoodProperties {
            require(metadata in names.indices) { "Unknown food variant $metadata" }
            return FoodProperties.Builder().nutrition(nutrition[metadata]).saturationModifier(saturationModifiers[metadata]).build()
        }
    }
}
