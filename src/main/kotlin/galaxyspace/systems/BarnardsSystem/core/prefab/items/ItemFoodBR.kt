package galaxyspace.systems.BarnardsSystem.core.prefab.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.util.StringRepresentable
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item

open class ItemFoodBR(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor() : this(
        FoundationItemProperties.named("galaxyspace", "br_foods").food(foodProperties(0))
            .component(DataComponents.ITEM_MODEL, Identifier.parse("galaxyspace:br_foods"))
    )

    enum class BR_Food(private val serializedName: String, private val amount: Int, private val saturation: Float) : StringRepresentable {
        YELLOW_FRUITS("yellow_fruits", 4, 0.4f);
        override fun getSerializedName(): String = serializedName
        fun getName(): String = serializedName
        fun getAmount(): Int = amount
        fun getSaturation(): Float = saturation
        companion object {
            @JvmStatic fun byMetadata(meta: Int): BR_Food? = entries.getOrNull(meta)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL
    override fun creativeMetadata(): List<Int> = BR_Food.entries.indices.toList()
    override fun variant(metadata: Int): FoundationItemVariant? {
        val food = BR_Food.byMetadata(metadata) ?: return null
        return FoundationItemVariant("item.${food.getName()}.name", Identifier.parse("galaxyspace:item/barnarda/foods/${food.getName()}"))
    }
    override fun configureVariant(stack: net.minecraft.world.item.ItemStack, metadata: Int) {
        stack.set(DataComponents.FOOD, foodProperties(metadata))
    }
    companion object {
        @JvmStatic fun foodProperties(metadata: Int): FoodProperties {
            val food = requireNotNull(BR_Food.byMetadata(metadata)) { "Unknown Barnard food variant $metadata" }
            return FoodProperties.Builder().nutrition(food.getAmount()).saturationModifier(food.getSaturation()).build()
        }
    }
}
