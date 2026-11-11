package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemPreLaunchChecklist constructor(assetName: String?) : Item(), ISortableItem {
    init {
        this.setTranslationKey(assetName)
        this.setMaxStackSize(1)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack != null && this === GCItems.heavyPlatingTier1) {
            tooltip.add(GCCoreUtil.translate("item.tier1.desc"))
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemStack: ItemStack = playerIn.getItemInHand(hand)

        if (worldIn.isClientSide) {
            playerIn.openGui(
                GalacticraftCore.instance,
                GuiIdsCore.PRE_LAUNCH_CHECKLIST,
                playerIn.level(),
                playerIn.x.toInt(),
                playerIn.y.toInt(),
                playerIn.z.toInt()
            )
            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemStack)
        }

        return ActionResult<ItemStack?>(EnumActionResult.PASS, itemStack)
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
