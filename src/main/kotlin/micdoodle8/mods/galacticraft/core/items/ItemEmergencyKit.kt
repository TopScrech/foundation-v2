package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.init.PotionTypes
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionUtils
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemHandlerHelper

open class ItemEmergencyKit constructor(assetName: String?) : ItemDesc(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(false)
        this.setMaxStackSize(1)
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
        val itemStack: ItemStack = player.getHeldItem(hand)
        if (player is EntityPlayerMP) {
            val stats: GCPlayerStats? = GCPlayerStats.get(player)

            for (i: Int in 0..<SIZE) {
                var newGear: ItemStack? = getContents(i)
                if (newGear!!.getItem() is IClickableItem) {
                    newGear = (newGear.getItem() as IClickableItem).onItemRightClick(newGear, worldIn, player)
                }
                if (newGear!!.getCount() >= 1) {
                    ItemHandlerHelper.giveItemToPlayer(player, newGear, 0)
                }
            }

            itemStack.setCount(0)
            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate("item.emergency_kit.description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    companion object {
        private const val SIZE = 9

        @JvmStatic
        fun getContents(slot: Int): ItemStack? {
            when (slot) {
                0 -> return ItemStack(GCItems.oxMask)
                1 -> return ItemStack(GCItems.oxygenGear)
                2 -> return ItemStack(GCItems.oxTankLight)
                3 -> return ItemStack(GCItems.oxTankLight)
                4 -> return ItemStack(GCItems.steelPickaxe)
                5 -> return ItemStack(GCItems.foodItem, 1, 3)
                6 -> return PotionUtils.addPotionToItemStack(ItemStack(Items.POTIONITEM), PotionTypes.HEALING)
                7 -> return PotionUtils.addPotionToItemStack(ItemStack(Items.POTIONITEM), PotionTypes.LONG_NIGHT_VISION)
                8 -> return ItemStack(GCItems.parachute, 1, 13)
                else -> return null
            }
        }

        @JvmStatic
        fun getRecipe(): Array<Any?> {
            val result: Array<Any?> = arrayOf<Any?>(
                "EAB",
                "CID",
                "FGH",
                'A',
                null,
                'B',
                null,
                'C',
                null,
                'D',
                null,
                'E',
                null,
                'F',
                null,
                'G',
                null,
                'H',
                null,
                'I',
                null
            )
            for (i: Int in 0..<SIZE) {
                result[i * 2 + 4] = getContents(i)
            }
            return result
        }
    }
}
