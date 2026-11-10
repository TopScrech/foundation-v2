package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemCanisterOxygenInfinite constructor(assetName: String?) : Item(), IItemOxygenSupply, ISortableItem {
    init {
        this.setMaxDamage(ItemCanisterGeneric.Companion.EMPTY)
        this.setMaxStackSize(1)
        this.setNoRepair()
        this.setTranslationKey(assetName)
        this.setContainerItem(GCItems.oilCanister)
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun hasEffect(stack: ItemStack): Boolean {
        return true
    }

    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.infinite_item.desc"))
        tooltip.add(EnumColor.RED.toString() + GCCoreUtil.translate("gui.creative_only.desc"))
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon(Constants.TEXTURE_PREFIX + "oxygenCanisterInfinite");
    }*/
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getContainerItem(itemstack: ItemStack): ItemStack {
        if (super.getContainerItem(itemstack).isEmpty()) {
            return ItemStack.EMPTY
        }
        return itemstack
    }

    override fun discharge(itemStack: ItemStack?, amount: Int): Int {
        return amount
    }

    override fun getOxygenStored(par1ItemStack: ItemStack?): Int {
        return (par1ItemStack)!!.getMaxDamage()
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GEAR
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        var itemStack: ItemStack = player.getHeldItem(hand)
        if (player is EntityPlayerMP) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(2)
            val gear1: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(3)

            if (gear.isEmpty()) {
                (stats.getExtendedInventory())!!.setInventorySlotContents(2, itemStack.copy())
                itemStack = ItemStack.EMPTY
            } else if (gear1.isEmpty()) {
                (stats.getExtendedInventory())!!.setInventorySlotContents(3, itemStack.copy())
                itemStack = ItemStack.EMPTY
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
    }
}
