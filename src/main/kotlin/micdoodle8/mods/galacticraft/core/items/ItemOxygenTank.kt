package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
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
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemOxygenTank constructor(tier: Int, assetName: String?) : Item(), ISortableItem, IClickableItem {
    init {
        this.setMaxStackSize(1)
        this.setMaxDamage(tier * 900)
        this.setTranslationKey(assetName)
        //        this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
        this.setNoRepair()
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 0))
            list.add(ItemStack(this, 1, this.getMaxDamage()))
        }
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
        tooltip.add(GCCoreUtil.translate("gui.tank.oxygen_remaining") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GEAR
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        var itemStack: ItemStack = player.getHeldItem(hand)

        if (player is EntityPlayerMP) {
            if (itemStack.getItem() is IClickableItem) {
                itemStack = ((itemStack.getItem() as IClickableItem).onItemRightClick(itemStack, worldIn, player))!!
            }

            if (itemStack.isEmpty()) {
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
    }

    override fun onItemRightClick(itemStack: ItemStack?, worldIn: World?, player: EntityPlayer?): ItemStack? {
        var itemStack: ItemStack = (itemStack)!!
        val stats: GCPlayerStats = (GCPlayerStats.get((player)!!))!!
        val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(2)
        val gear1: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(3)

        if (gear.isEmpty()) {
            (stats.getExtendedInventory())!!.setInventorySlotContents(2, itemStack.copy())
            itemStack = ItemStack.EMPTY
        } else if (gear1.isEmpty()) {
            (stats.getExtendedInventory())!!.setInventorySlotContents(3, itemStack.copy())
            itemStack = ItemStack.EMPTY
        }

        return itemStack
    }
}
