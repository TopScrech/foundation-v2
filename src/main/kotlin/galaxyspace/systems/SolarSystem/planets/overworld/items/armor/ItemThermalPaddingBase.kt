package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.api.item.IItemThermal
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemThermalPaddingBase constructor(tier: Int, isFreeze: Boolean) : Item(), IItemThermal, ISortableItem {
    private var tier = 0
    private var forFreeze = false

    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(1)
        this.setTranslationKey("thermal_padding_t" + tier)
        this.tier = tier
        this.forFreeze = isFreeze
    }

    open fun isFreeze(): Boolean {
        return this.forFreeze
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSArmorTab
    }

    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(GCCoreUtil.translate("gui.tier" + this.tier + ".desc"))
        if (forFreeze) tooltip.add(GCCoreUtil.translate("gui.message.for_freeze"))
    }

    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (tab === getCreativeTab() || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                subItems.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        if (names.size > par1ItemStack.getItemDamage()) {
            return "item." + names[par1ItemStack.getItemDamage()]
        }

        return "unnamed"
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getThermalStrength(): Int {
        return tier
    }

    override fun isValidForSlot(stack: ItemStack?, armorSlot: Int): Boolean {
        return (stack)!!.getItemDamage() == armorSlot
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.ARMOR
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = player.getHeldItem(hand)

        if (player is EntityPlayerMP) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(6)
            val gear1: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(7)
            val gear2: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(8)
            val gear3: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(9)

            if (itemStack.getItemDamage() == 0) {
                if (gear.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(6, itemStack.copy())
                    itemStack.setCount(0)
                }
            } else if (itemStack.getItemDamage() == 1) {
                if (gear1.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(7, itemStack.copy())
                    itemStack.setCount(0)
                }
            } else if (itemStack.getItemDamage() == 2) {
                if (gear2.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(8, itemStack.copy())
                    itemStack.setCount(0)
                }
            } else if (itemStack.getItemDamage() == 3) {
                if (gear3.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(9, itemStack.copy())
                    itemStack.setCount(0)
                }
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
    }

    companion object {
        @JvmField
        var names: Array<String> =
            arrayOf<String>("thermal_helm", "thermal_chestplate", "thermal_leggings", "thermal_boots")
    }
}
