package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.world.item.ItemStack

open class ItemElectricBasicGS(name: String, private val energyMax: Float) : ItemElectricBase(FoundationItemProperties.named("galaxyspace", name).stacksTo(1)), ISortableItem {
    init { require(energyMax.isFinite() && energyMax > 0f) }
    override fun getMaxElectricityStored(itemStack: ItemStack?): Float = energyMax
    override fun creativeStacks(): List<ItemStack> = super.creativeStacks().reversed()
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL
    override fun appendHoverText(stack: ItemStack, context: net.minecraft.world.item.Item.TooltipContext, display: net.minecraft.world.item.component.TooltipDisplay, builder: java.util.function.Consumer<net.minecraft.network.chat.Component>, flag: net.minecraft.world.item.TooltipFlag) = Unit
}
