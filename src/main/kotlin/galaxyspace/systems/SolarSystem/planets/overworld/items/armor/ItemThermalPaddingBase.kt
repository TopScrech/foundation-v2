package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import foundation.item.legacyItemValue

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.api.item.IItemThermal
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

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

    @OnlyIn(Dist.CLIENT)
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
        if (names.size > par1ItemStack.legacyItemValue) {
            return "item." + names[par1ItemStack.legacyItemValue]
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
        return (stack)!!.legacyItemValue == armorSlot
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.ARMOR
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = player.getItemInHand(hand)

        if (player is EntityPlayerMP) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(6)
            val gear1: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(7)
            val gear2: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(8)
            val gear3: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(9)

            if (itemStack.legacyItemValue == 0) {
                if (gear.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(6, itemStack.copy())
                    itemStack.setCount(0)
                }
            } else if (itemStack.legacyItemValue == 1) {
                if (gear1.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(7, itemStack.copy())
                    itemStack.setCount(0)
                }
            } else if (itemStack.legacyItemValue == 2) {
                if (gear2.isEmpty()) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(8, itemStack.copy())
                    itemStack.setCount(0)
                }
            } else if (itemStack.legacyItemValue == 3) {
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
