package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.api.item.IKeyItem
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

open class ItemTierKeysChest constructor() : Item(), IKeyItem, ISortableItem {
    init {
        this.setTranslationKey("dungeon_keys")
        this.setMaxStackSize(1)
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSItemsTab
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) for (i: Int in keys.indices) list.add(
            ItemStack(this, 1, i)
        )
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item.dungeon_keys"
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getTier(stack: ItemStack?): Int {
        return 4 + (stack)!!.getItemDamage()
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        val n: Int = 4 + stack.getItemDamage()
        tooltip.add(GCCoreUtil.translate("gui.tier" + n + ".desc"))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.KEYS
    }

    companion object {
        @JvmField
        var keys: Array<String?> = arrayOf<String?>("key_tier_4", "key_tier_5")
    }
}
