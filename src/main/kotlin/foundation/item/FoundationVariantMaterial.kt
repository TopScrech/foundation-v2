package foundation.item

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

/** Component-based material families retain explicit names independently of durability */
abstract class FoundationVariantMaterial(properties: Item.Properties) : Item(properties), FoundationVariantItem {
    override fun getName(stack: ItemStack): Component {
        if (stack.get(DataComponents.ITEM_NAME) != components().get(DataComponents.ITEM_NAME)) return super.getName(stack)
        return variant(FoundationItemVariants.metadata(stack))?.name() ?: super.getName(stack)
    }

    fun creativeStacks(): List<ItemStack> = FoundationItemVariants.creativeStacks(this)
}
