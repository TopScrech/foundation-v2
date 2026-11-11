package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.Level
import net.minecraftforge.items.ItemHandlerHelper

open class ItemEmergencyKit(properties: Item.Properties) : ItemDesc(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).stacksTo(1).rarity(Rarity.RARE)
            .overrideDescription("item.$assetName.name")
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GEAR
    override fun getShiftDescription(meta: Int): String = Component.translatable("item.emergency_kit.description").string
    override fun showDescription(meta: Int): Boolean = true

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (player !is ServerPlayer) return InteractionResult.PASS
        val held = player.getItemInHand(hand)
        distribute(
            { stack ->
                val item = stack.item
                if (item is IClickableItem) requireNotNull(item.onItemRightClick(stack, level, player)) else stack
            },
            { stack -> ItemHandlerHelper.giveItemToPlayer(player, stack, 0) }
        )
        held.shrink(1)
        return InteractionResult.SUCCESS
    }

    companion object {
        private const val SIZE = 9
        private fun coreItem(name: String): Item = FoundationItemProperties.registered(Identifier.fromNamespaceAndPath("galacticraftcore", name))

        /** Resolve every content before transfer so a missing registration cannot partially unpack a kit */
        @JvmStatic fun contents(): List<ItemStack> = (0 until SIZE).map { requireNotNull(getContents(it)) }

        @JvmStatic fun distribute(equip: (ItemStack) -> ItemStack, give: (ItemStack) -> Unit) {
            val items = contents()
            for (stack in items) {
                val remaining = equip(stack)
                if (!remaining.isEmpty) give(remaining)
            }
        }

        @JvmStatic fun getContents(slot: Int): ItemStack? = when (slot) {
            0 -> ItemStack(coreItem("oxygen_mask"))
            1 -> ItemStack(coreItem("oxygen_gear"))
            2, 3 -> ItemOxygenTank.stack(coreItem("oxygen_tank_light_full"), 1, 0)
            4 -> ItemStack(coreItem("steel_pickaxe"))
            5 -> FoundationItemVariants.stack(coreItem("food"), 1, 3)
            6 -> PotionContents.createItemStack(Items.POTION, Potions.HEALING)
            7 -> PotionContents.createItemStack(Items.POTION, Potions.LONG_NIGHT_VISION)
            8 -> FoundationItemVariants.stack(coreItem("parachute"), 1, 13)
            else -> null
        }

        /** Retained until the shared NBT-sensitive recipe registration is migrated */
        @JvmStatic fun getRecipe(): Array<Any?> {
            val items = contents()
            val result = arrayOfNulls<Any>(SIZE * 2 + 3)
            result[0] = "EAB"; result[1] = "CID"; result[2] = "FGH"
            for (index in 0 until SIZE) {
                result[index * 2 + 3] = 'A' + index
                result[index * 2 + 4] = items[index]
            }
            return result
        }
    }
}
