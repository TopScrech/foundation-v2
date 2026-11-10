package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.api.item.IModificationItem
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.EPP
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemGSOxygenTank constructor(tier: Int, assetName: String, damage: Int, epp: Boolean) :
    ItemOxygenTank(tier, assetName), IModificationItem {
    private var epp = false

    init {
        this.setMaxStackSize(1)
        this.setMaxDamage(damage)
        this.setTranslationKey(assetName)
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
        this.setNoRepair()
        this.epp = epp
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 0))
            list.add(ItemStack(this, 1, this.getMaxDamage()))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        if (epp || stack.hasTagCompound() && stack.getTagCompound()!!.hasKey("epp")) {
            list.add(EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.tank.passive_oxygen_regen.desc1"))
            list.add(EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.tank.passive_oxygen_regen.desc2"))
        }
        list.add(GCCoreUtil.translate("gui.tank.oxygen_remaining") + ": " + (stack.getMaxDamage() - stack.getItemDamage()))
    }

    override fun getType(stack: ItemStack?): Module_Type? {
        if ((stack)!!.getItem() !== GSItems.OXYGENTANK_TIER_EPP) return Module_Type.OXYGEN_TANK

        return null
    }

    override fun getAvailableModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(EPP()) as Array<ItemModule?>?))!!
    }

    override fun getModificationCount(stack: ItemStack?): Int {
        return 1
    }
}
