package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemIC2Compat constructor(assetName: String?) : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(false)
        this.setTranslationKey(assetName)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        var meta: Int = itemStack.legacyItemValue
        meta = 0
        return this.getTranslationKey() + "." + types[meta % 4] + "_" + names[meta / 4]
    }

    override fun getSubItems(tab: CreativeTabs, par3List: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            par3List.add(ItemStack(this, 1, 0))
        }
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        val types: Array<String?> = arrayOf<String?>("dust", "ore_purified", "ore_crushed", "dust_small")

        @JvmField
        val names: Array<String?> = arrayOf<String?>("alu", "titanium")
    }
}
