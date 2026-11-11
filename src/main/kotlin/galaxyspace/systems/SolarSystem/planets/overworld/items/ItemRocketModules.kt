package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

open class ItemRocketModules(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor() : this(FoundationItemProperties.named("galaxyspace", "rocket_modules")
        .component(DataComponents.ITEM_MODEL, Identifier.parse("galaxyspace:rocket_modules"))
        .overrideDescription("item.module_lander_1.name"))

    override fun variant(metadata: Int): FoundationItemVariant? {
        val model = names.getOrNull(metadata) ?: return null
        return FoundationItemVariant("item.$model.name", Identifier.parse("galaxyspace:item/rocket_modules/$model"))
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val meta = FoundationItemVariants.metadata(stack)
        if (meta !in names.indices) return
        builder.accept(Component.translatable("gui.module.desc"))
        if (meta >= 8) builder.accept(Component.translatable("gui.tier${meta - 4}.desc"))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "module_lander_1",
            "module_lander_2",
            "module_lander_3",
            "module_small_fuelcanister",
            "ion_engine",
            "plasma_engine",
            "sublight_engine",
            "blackhole_engine",
            "license_t4",
            "license_t5",
            "license_t6"
        )
    }
}
