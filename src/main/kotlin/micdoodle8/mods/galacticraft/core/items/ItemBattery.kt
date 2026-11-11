package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.world.item.ItemStack
import net.minecraft.ChatFormatting
import net.minecraft.world.item.Rarity

open class ItemBattery(assetName: String?) : ItemElectricBase(FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).stacksTo(1).rarity(Rarity.RARE)), ISortableItem {
    override val emptyStackLimit: Int = 4
    override val nameColor: ChatFormatting = ChatFormatting.BLUE
    override fun getMaxElectricityStored(itemStack: ItemStack?): Float = 15000f
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL
}
