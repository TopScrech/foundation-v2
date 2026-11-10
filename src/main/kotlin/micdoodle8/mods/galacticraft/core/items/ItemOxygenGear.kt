package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
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

open class ItemOxygenGear constructor(assetName: String?) : Item(), ISortableItem, IClickableItem {
    init {
        this.setTranslationKey(assetName)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
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
        val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(1)

        if (gear.isEmpty()) {
            (stats.getExtendedInventory())!!.setInventorySlotContents(1, itemStack.copy())
            itemStack = ItemStack.EMPTY
        }

        return itemStack
    }
}
