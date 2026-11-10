package galaxyspace.systems.BarnardsSystem.core.prefab.items

import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemFoodBR.BR_Food
import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemFoodBR
import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList

open class ItemFoodBR constructor() : ItemFood(0, false), ISortableItem {
    enum class BR_Food(private val serializedName: String, private val amount: Int, private val saturation: Float) :
        IStringSerializable {
        YELLOW_FRUITS("yellow_fruits", 4, 0.4f);

        override fun getName(): String {
            return this.serializedName
        }

        fun getAmount(): Int {
            return this.amount
        }

        fun getSaturation(): Float {
            return this.saturation
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): BR_Food? {
                return entries[meta]
            }
        }
    }

    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(64)
        this.setTranslationKey("br_foods")
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in BR_Food.entries.toTypedArray().indices) {
                if (BR_Food.byMetadata(i)!!.getName() != "null") list.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun getHealAmount(stack: ItemStack): Int {
        return BR_Food.byMetadata(stack.getItemDamage())!!.getAmount()
    }

    override fun getSaturationModifier(stack: ItemStack): Float {
        return BR_Food.byMetadata(stack.getItemDamage())!!.getSaturation()
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return "item." + BR_Food.byMetadata(stack.getItemDamage())!!.getName()
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
