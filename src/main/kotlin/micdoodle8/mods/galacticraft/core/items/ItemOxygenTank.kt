package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemComponents
import net.minecraft.util.Mth
import foundation.item.FoundationEquipmentItem
import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import java.util.function.Consumer

open class ItemOxygenTank(properties: Item.Properties, val capacity: Int) : Item(properties), ISortableItem, FoundationEquipmentItem {
    constructor(tier: Int, assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName))
            .stacksTo(1).rarity(Rarity.RARE)
            .overrideDescription("item.$assetName.name"),
        capacityForTier(tier)
    )

    init { require(capacity > 0) }

    override val equipmentSlots: List<Int> = listOf(2, 3)
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GEAR
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult = useEquipment(player, hand)

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        builder.accept(Component.translatable("gui.tank.oxygen_remaining").append(": ").append(remainingOxygen(stack).toString()))
    }

    fun remainingOxygen(stack: ItemStack): Int = (capacity - used(stack)).coerceIn(0, capacity)
    override fun isBarVisible(stack: ItemStack): Boolean = used(stack) > 0
    override fun getBarWidth(stack: ItemStack): Int = Math.round(13f * remainingOxygen(stack) / capacity).coerceIn(0, 13)
    override fun getBarColor(stack: ItemStack): Int = Mth.hsvToRgb(remainingOxygen(stack).toFloat() / capacity / 3f, 1f, 1f)

    open fun creativeStacks(): List<ItemStack> = listOf(stack(this, 1, 0), stack(this, 1, capacity))

    companion object {
        private fun capacityForTier(tier: Int): Int {
            require(tier in 1..3) { "Unknown oxygen tank tier $tier" }
            return tier * 900
        }

        @JvmStatic fun capacityOf(item: Item): Int {
            require(item is ItemOxygenTank) { "Expected an oxygen tank" }
            return item.capacity
        }
        @JvmStatic fun capacityOf(stack: ItemStack): Int = (stack.item as? ItemOxygenTank)?.capacity ?: stack.maxDamage
        @JvmStatic fun used(stack: ItemStack): Int = if (stack.item is ItemOxygenTank) stack.getOrDefault(FoundationItemComponents.OXYGEN_USED, 0) else stack.damageValue
        @JvmStatic fun setUsed(stack: ItemStack, amount: Int) {
            val bounded = amount.coerceIn(0, capacityOf(stack))
            if (stack.item is ItemOxygenTank) {
                if (bounded == 0) stack.remove(FoundationItemComponents.OXYGEN_USED) else stack.set(FoundationItemComponents.OXYGEN_USED, bounded)
            } else stack.damageValue = bounded
        }

        /** Resource usage never adds native tool damage or repair components */
        @JvmStatic
        fun stack(item: Item, count: Int, usedOxygen: Int): ItemStack {
            require(item is ItemOxygenTank) { "Expected an oxygen tank" }
            require(usedOxygen in 0..item.capacity) { "Invalid oxygen usage $usedOxygen" }
            return if (count <= 0) ItemStack.EMPTY else ItemStack(item, count).also { setUsed(it, usedOxygen) }
        }
    }
}
