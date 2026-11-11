package micdoodle8.mods.galacticraft.core.items

import foundation.inventory.FoundationEquipment
import foundation.item.FoundationFrequencyModules
import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import java.util.function.Consumer

open class ItemBasic(properties: Item.Properties, private val legacyName: String = "item.basic_item") : FoundationVariantMaterial(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named(Constants.MOD_ID_CORE, requireNotNull(assetName))
            .rarity(Rarity.RARE)
            .component(DataComponents.ITEM_MODEL, Identifier.parse("galacticraftcore:basic_item"))
            .component(DataComponents.ITEM_NAME, Component.translatable("item.$assetName.solar_module_0.name").withStyle(ChatFormatting.BLUE)),
        "item.$assetName"
    )

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val translation = if (metadata in 15..18) "$legacyName.canned_food.name" else "$legacyName.$name.name"
        val model = when (metadata) { 0 -> "basic_item"; 15 -> "food"; else -> name }
        return FoundationItemVariant(translation, Identifier.parse("galacticraftcore:item/$model"), ChatFormatting.BLUE)
    }

    override fun creativeMetadata(): List<Int> = names.indices.filter { it !in 15..18 }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val meta = FoundationItemVariants.metadata(stack)
        if (meta in 15..18) builder.accept(Component.translatable("$legacyName.${names[meta]}.name").withStyle(ChatFormatting.GREEN))
        else if (meta == FREQUENCY_MODULE) {
            builder.accept(Component.translatable("gui.frequency_module.desc.0").withStyle(ChatFormatting.AQUA))
            builder.accept(Component.translatable("gui.frequency_module.desc.1").withStyle(ChatFormatting.AQUA))
        }
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val held = player.getItemInHand(hand)
        if (player is ServerPlayer && FoundationFrequencyModules.canEquip(held)) {
            val inventory = AccessInventoryGC.getGCInventoryForPlayer(player)
            if (inventory != null) FoundationEquipment.equipOne(inventory, 5, held)
        }
        return InteractionResult.FAIL
    }

    override fun onLeftClickEntity(stack: ItemStack, player: Player, entity: Entity): Boolean {
        if (!player.level().isClientSide && entity !is Player && FoundationFrequencyModules.link(stack, entity.uuid)) {
            player.sendSystemMessage(Component.translatable("gui.tracking.message"))
            return true
        }
        return false
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem = when (meta) {
        3, 4, 5 -> EnumSortCategoryItem.INGOT
        in 6..11 -> EnumSortCategoryItem.PLATE
        FREQUENCY_MODULE -> EnumSortCategoryItem.GEAR
        else -> EnumSortCategoryItem.GENERAL
    }

    // Native smelting recipes own experience; retain the declaration until those recipes are migrated
    open fun getSmeltingExperience(stack: ItemStack): Float = if (FoundationItemVariants.metadata(stack) in 6..11) 1f else -1f

    companion object {
        @JvmField
        val names: Array<String> = arrayOf(
            "solar_module_0",
            "solar_module_1",
            "raw_silicon",
            "ingot_copper",
            "ingot_tin",
            "ingot_aluminum",
            "compressed_copper",
            "compressed_tin",
            "compressed_aluminum",
            "compressed_steel",
            "compressed_bronze",
            "compressed_iron",
            "wafer_solar",
            "wafer_basic",
            "wafer_advanced",
            "dehydrated_apple",
            "dehydrated_carrot",
            "dehydrated_melon",
            "dehydrated_potato",
            "frequency_module",
            "ambient_thermal_controller"
        )
        const val WAFER_BASIC: Int = 13
        const val WAFER_ADVANCED: Int = 14
        const val FREQUENCY_MODULE: Int = 19
    }
}
