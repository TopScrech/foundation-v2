package micdoodle8.mods.galacticraft.planets.venus.items

import foundation.inventory.FoundationEquipment
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC
import micdoodle8.mods.galacticraft.core.items.ItemDesc
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.Level
import net.minecraft.world.entity.player.Player
import net.minecraft.server.level.ServerPlayer
import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity

open class ItemBasicVenus(properties: Item.Properties) : ItemDesc(properties), ISortableItem, FoundationVariantItem {
    constructor(name: String?) : this(
        FoundationItemProperties.named(Constants.MOD_ID_PLANETS, requireNotNull(name))
            .rarity(Rarity.RARE)
            .component(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(Constants.MOD_ID_PLANETS, "basic_item_venus"))
            .component(DataComponents.ITEM_NAME, Component.translatable("item.shield_controller.name").withStyle(ChatFormatting.BLUE))
    )

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val model = name
        return FoundationItemVariant("item.$name.name", Identifier.fromNamespaceAndPath(Constants.MOD_ID_PLANETS, "item/$model"), ChatFormatting.BLUE)
    }

    override fun getName(stack: ItemStack): Component {
        if (stack.get(DataComponents.ITEM_NAME) != components().get(DataComponents.ITEM_NAME)) return super.getName(stack)
        return variant(FoundationItemVariants.metadata(stack))?.name() ?: super.getName(stack)
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()

    fun creativeStacks(): List<ItemStack> = FoundationItemVariants.creativeStacks(this)

    override fun getCategory(meta: Int): EnumSortCategoryItem = when (meta) {
        0 -> EnumSortCategoryItem.GEAR
        1 -> EnumSortCategoryItem.INGOT
        else -> EnumSortCategoryItem.GENERAL
    }

    override fun getShiftDescription(meta: Int): String =
        if (meta == 0) Component.translatable("item.shield_controller.description").string else ""

    override fun showDescription(meta: Int): Boolean = meta == 0

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val held = player.getItemInHand(hand)
        if (player is ServerPlayer && FoundationItemVariants.metadata(held) == 0) {
            val inventory = AccessInventoryGC.getGCInventoryForPlayer(player)
            if (inventory != null) FoundationEquipment.equipOne(inventory, 10, held)
        }
        return InteractionResult.SUCCESS
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "shield_controller",
            "ingot_lead",
            "radioisotope_core",
            "thermal_fabric",
            "solar_dust",
            "solar_module_2",
            "thin_solar_wafer"
        )
    }
}
