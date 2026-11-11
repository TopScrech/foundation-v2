package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.world.item.ItemStack
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

open class ItemBatteryInfinite(properties: Item.Properties) : ItemElectricBase(properties), ISortableItem {
    constructor(assetName: String?) : this(FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).stacksTo(1).rarity(Rarity.RARE))
    override val nameColor: ChatFormatting = ChatFormatting.BLUE
    override fun setMaxTransfer() { transferMax = 1000f }
    override fun getTierGC(itemStack: ItemStack?): Int = 3
    override fun isFoil(stack: ItemStack): Boolean = true
    override fun getElectricityStored(itemStack: ItemStack?): Float = Float.POSITIVE_INFINITY
    override fun getMaxElectricityStored(itemStack: ItemStack?): Float = Float.POSITIVE_INFINITY
    override fun setElectricity(itemStack: ItemStack?, joules: Float) = Unit
    override fun getTransfer(itemStack: ItemStack?): Float = 0f
    override fun recharge(itemStack: ItemStack?, energy: Float, doReceive: Boolean): Float = 0f
    override fun discharge(itemStack: ItemStack?, energy: Float, doTransfer: Boolean): Float = if (energy.isNaN() || energy <= 0f) 0f else energy
    override fun isBarVisible(stack: ItemStack): Boolean = false
    override fun getBarWidth(stack: ItemStack): Int = 13
    override fun getBarColor(stack: ItemStack): Int = 0x00ff00
    override fun creativeStacks(): List<ItemStack> = listOf(ItemStack(this))
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL
    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        builder.accept(Component.translatable("gui.infinite_item.desc").withStyle(ChatFormatting.DARK_GREEN))
        builder.accept(Component.translatable("gui.creative_only.desc").withStyle(ChatFormatting.RED))
    }
}
