package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationEquipmentItem
import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level

open class ItemParaChute(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem, FoundationEquipmentItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).stacksTo(1).rarity(Rarity.RARE)
            .component(DataComponents.ITEM_MODEL, Identifier.parse("galacticraftcore:parachute"))
    )

    override val equipmentSlots: List<Int> = listOf(4)
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GEAR
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult = useEquipment(player, hand)
    override fun creativeMetadata(): List<Int> = names.indices.toList()
    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val model = if (metadata == 0) "parachute" else "parachute_$name"
        return FoundationItemVariant("item.parachute_$name.name", Identifier.parse("galacticraftcore:item/$model"), ChatFormatting.BLUE)
    }

    companion object {
        @JvmField val names: Array<String> = arrayOf(
            "plain", "black", "blue", "lime", "brown", "darkblue", "darkgray", "darkgreen",
            "gray", "magenta", "orange", "pink", "purple", "red", "teal", "yellow"
        )
        private val colors = listOf(
            DyeColor.WHITE, DyeColor.BLACK, DyeColor.LIGHT_BLUE, DyeColor.LIME, DyeColor.BROWN, DyeColor.BLUE,
            DyeColor.GRAY, DyeColor.GREEN, DyeColor.LIGHT_GRAY, DyeColor.MAGENTA, DyeColor.ORANGE,
            DyeColor.PINK, DyeColor.PURPLE, DyeColor.RED, DyeColor.CYAN, DyeColor.YELLOW
        )

        @JvmStatic fun getDyeEnumFromParachuteDamage(damage: Int): DyeColor = colors.getOrElse(damage) { DyeColor.WHITE }
        @JvmStatic fun getParachuteDamageValueFromDyeEnum(color: DyeColor): Int = colors.indexOf(color)
        // Legacy dye item damage counted in reverse order from wool / modern DyeColor IDs
        @JvmStatic fun getParachuteDamageValueFromDye(meta: Int): Int = getParachuteDamageValueFromDyeEnum(
            DyeColor.byId(if (meta in 0..15) 15 - meta else 15)
        )
    }
}
