package micdoodle8.mods.galacticraft.planets.venus.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemDesc
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBasicVenus constructor(name: String?) : ItemDesc(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(name)
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        if (names.size > par1ItemStack.getItemDamage()) {
            return "item." + names[par1ItemStack.getItemDamage()]
        }

        return "unnamed"
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        when (meta) {
            0 -> return EnumSortCategoryItem.GEAR
            1 -> return EnumSortCategoryItem.INGOT
            else -> return EnumSortCategoryItem.GENERAL
        }
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = player.getHeldItem(hand)

        if (player is EntityPlayerMP && itemStack.getItemDamage() == 0) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(10)

            if (gear.isEmpty()) {
                (stats.getExtendedInventory())!!.setInventorySlotContents(10, itemStack.copy())
                itemStack.setCount(0)
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
    }

    override fun getShiftDescription(meta: Int): String? {
        if (meta == 0) {
            return GCCoreUtil.translate("item.shield_controller.description")
        }

        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return meta == 0
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>(
            "shield_controller",
            "ingot_lead",
            "radioisotope_core",
            "thermal_fabric",
            "solar_dust",
            "solar_module_2",
            "thin_solar_wafer"
        )
    }
}
