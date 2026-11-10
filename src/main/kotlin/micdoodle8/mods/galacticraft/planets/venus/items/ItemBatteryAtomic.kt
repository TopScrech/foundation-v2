package micdoodle8.mods.galacticraft.planets.venus.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBatteryAtomic constructor(assetName: String?) : ItemElectricBase(), ISortableItem {
    init {
        this.setTranslationKey(assetName)
    }

    override fun setMaxTransfer() {
        this.transferMax = 7f
    }

    override fun getTierGC(itemStack: ItemStack?): Int {
        return 2
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.infinite_item.desc"))
        tooltip.add(EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.message.low_energy_output.name"))
    }

    override fun getElectricityStored(itemStack: ItemStack?): Float {
        return this.getMaxElectricityStored(itemStack)
    }

    override fun setElectricity(itemStack: ItemStack?, joules: Float) {
    }

    override fun getMaxElectricityStored(itemStack: ItemStack?): Float {
        return Float.POSITIVE_INFINITY
    }

    override fun getTransfer(itemStack: ItemStack?): Float {
        return 0.0f
    }

    override fun recharge(theItem: ItemStack?, energy: Float, doReceive: Boolean): Float {
        return 0f
    }

    override fun discharge(theItem: ItemStack?, energy: Float, doTransfer: Boolean): Float {
        return super.discharge((theItem)!!, energy, doTransfer)
    }

    override fun getSubItems(tab: CreativeTabs, par3List: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            par3List.add(ItemStack(this, 1, 0))
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
