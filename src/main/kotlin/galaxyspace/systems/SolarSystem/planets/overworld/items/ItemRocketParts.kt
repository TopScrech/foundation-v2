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

open class ItemRocketParts constructor() : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(64)
        this.setTranslationKey("rocket_parts")
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
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        for (i: Int in 0..4) {
            if (stack.getItemDamage() == i) list.add(GCCoreUtil.translate("gui.tier3.desc"))
            else if (stack.getItemDamage() == i + 5) list.add(GCCoreUtil.translate("gui.tier4.desc"))
            else if (stack.getItemDamage() == i + 10) list.add(GCCoreUtil.translate("gui.tier5.desc"))
            else if (stack.getItemDamage() == i + 15) list.add(GCCoreUtil.translate("gui.tier6.desc"))
        }
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        for (k: Int in 0..4) {
            if (par1ItemStack.getItemDamage() == k * 5) return "item.rocket_cone"
            if (par1ItemStack.getItemDamage() == k * 5 + 1) return "item.rocket_body"
            if (par1ItemStack.getItemDamage() == k * 5 + 2) return "item.rocket_engine"
            if (par1ItemStack.getItemDamage() == k * 5 + 3) return "item.rocket_booster"
            if (par1ItemStack.getItemDamage() == k * 5 + 4) return "item.rocket_stabiliser"
        }
        return "unnamed"
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        var names: Array<String?> =
            arrayOf<String?>( //"rocket_cone_tier_2", "rocket_body_tier_2", "rocket_engine_tier_2", "rocket_booster_tier_2", "rocket_stabiliser_tier_2",
                "rocket_cone_tier_3",
                "rocket_body_tier_3",
                "rocket_engine_tier_3",
                "rocket_booster_tier_3",
                "rocket_stabiliser_tier_3",

                "rocket_cone_tier_4",
                "rocket_body_tier_4",
                "rocket_engine_tier_4",
                "rocket_booster_tier_4",
                "rocket_stabiliser_tier_4",

                "rocket_cone_tier_5",
                "rocket_body_tier_5",
                "rocket_engine_tier_5",
                "rocket_booster_tier_5",
                "rocket_stabiliser_tier_5",

                "rocket_cone_tier_6",
                "rocket_body_tier_6",
                "rocket_engine_tier_6",
                "rocket_booster_tier_6",
                "rocket_stabiliser_tier_6"
            )
    }
}
