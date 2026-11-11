package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationEquipmentItem
import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level

open class ItemOxygenMask(properties: Item.Properties) : Item(properties), ISortableItem, FoundationEquipmentItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName))
            .rarity(Rarity.RARE).overrideDescription("item.$assetName.name")
    )

    override val equipmentSlots: List<Int> = listOf(0)
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GEAR
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult = useEquipment(player, hand)
}
