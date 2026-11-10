package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemRocketModules constructor() : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(64)
        this.setTranslationKey("rocket_modules")
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        list.add(GCCoreUtil.translate("gui.module.desc"))

        val meta: Int = stack.getItemDamage()
        if (meta >= 8) list.add(GCCoreUtil.translate("gui.tier" + (meta - 4) + ".desc"))
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item." + names[par1ItemStack.getItemDamage()]
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>(
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
