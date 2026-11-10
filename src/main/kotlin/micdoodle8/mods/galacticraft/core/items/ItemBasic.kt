package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBasic constructor(assetName: String?) : Item(), ISortableItem {
    //    protected IIcon[] icons = new IIcon[ItemBasic.names.length];
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(assetName)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        if (itemStack.getItemDamage() > 14 && itemStack.getItemDamage() < 19) {
            return this.getTranslationKey() + ".canned_food"
        }

        return this.getTranslationKey() + "." + names[itemStack.getItemDamage()]
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in 0..14) {
                list.add(ItemStack(this, 1, i))
            }
            for (i: Int in 19..<names.size) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19) {
            tooltip.add(EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate(this.getTranslationKey() + "." + names[par1ItemStack.getItemDamage()] + ".name"))
        } else if (par1ItemStack.getItemDamage() == 19) {
            tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.frequency_module.desc.0"))
            tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.frequency_module.desc.1"))
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        var itemStackIn: ItemStack = playerIn.getHeldItem(hand)
        if (itemStackIn.getItemDamage() == 19) {
            if (playerIn is EntityPlayerMP) {
                val stats: GCPlayerStats = (GCPlayerStats.get(playerIn))!!
                val gear: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(5)

                if (gear.isEmpty() && itemStackIn.getTagCompound() == null) {
                    (stats.getExtendedInventory())!!.setInventorySlotContents(5, itemStackIn.copy())
                    itemStackIn = ItemStack.EMPTY
                }
            }
        }

        return ActionResult<ItemStack?>(EnumActionResult.FAIL, itemStackIn)
    }

    override fun onLeftClickEntity(itemStack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
        if (itemStack.getItemDamage() != 19) {
            return false
        }

        //Frequency module
        if (!player.world.isRemote && entity != null && (entity !is EntityPlayer)) {
            if (itemStack.getTagCompound() == null) {
                itemStack.setTagCompound(NBTTagCompound())
            }

            itemStack.getTagCompound()!!.setLong("linkedUUIDMost", entity.getUniqueID().getMostSignificantBits())
            itemStack.getTagCompound()!!.setLong("linkedUUIDLeast", entity.getUniqueID().getLeastSignificantBits())

            player.sendMessage(TextComponentString(GCCoreUtil.translate("gui.tracking.message")))
            return true
        }
        return false
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        when (meta) {
            3, 4, 5 -> return EnumSortCategoryItem.INGOT
            6, 7, 8, 9, 10, 11 -> return EnumSortCategoryItem.PLATE
            19 -> return EnumSortCategoryItem.GEAR
        }
        return EnumSortCategoryItem.GENERAL
    }

    override fun getSmeltingExperience(item: ItemStack): Float {
        when (item.getItemDamage()) {
            6, 7, 8, 9, 10, 11 -> return 1f
        }
        return -1f
    }

    companion object {
        @JvmField
        val names: Array<String?> = arrayOf<String?>(
            "solar_module_0",
            "solar_module_1",
            "raw_silicon",
            "ingot_copper",
            "ingot_tin",
            "ingot_aluminum",
            "compressed_copper",
            "compressed_tin",
            "compressed_aluminum",
            "compressed_steel",
            "compressed_bronze",
            "compressed_iron",
            "wafer_solar",
            "wafer_basic",
            "wafer_advanced",
            "dehydrated_apple",
            "dehydrated_carrot",
            "dehydrated_melon",
            "dehydrated_potato",
            "frequency_module",
            "ambient_thermal_controller"
        )

        const val WAFER_BASIC: Int = 13

        const val WAFER_ADVANCED: Int = 14
    }
}
