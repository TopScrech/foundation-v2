package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemFood constructor(assetName: String?) : ItemFood(2, 0.3f, false), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(assetName)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        if (itemStack.getItemDamage() < 4) {
            return "item.basic_item.canned_food"
        } else {
            return "item.food." + names[itemStack.getItemDamage()]
        }
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in 0..3) {
                list.add(ItemStack(this, 1, i))
            }
            list.add(ItemStack(this, 1, 9))
            for (i: Int in 4..8) {
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
        if (par1ItemStack.getItemDamage() < 4) {
            tooltip.add(EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("item.basic_item." + names[par1ItemStack.getItemDamage()] + ".name"))
        } else if (par1ItemStack.getItemDamage() == 8) {
            tooltip.add(EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("item.food.cheeseburger.desc"))
        }
    }

    override fun getHealAmount(par1ItemStack: ItemStack): Int {
        when (par1ItemStack.getItemDamage()) {
            0 -> return 8
            1 -> return 8
            2 -> return 4
            3 -> return 2
            4 -> return 2
            5 -> return 4
            6 -> return 2
            7 -> return 4
            8 -> return 14
            9 -> return 8
            else -> return 0
        }
    }

    override fun getSaturationModifier(par1ItemStack: ItemStack): Float {
        when (par1ItemStack.getItemDamage()) {
            0 -> return 0.3f
            1 -> return 0.6f
            2 -> return 0.3f
            3 -> return 0.3f
            4 -> return 0.1f
            5 -> return 0.8f
            6 -> return 0.3f
            7 -> return 0.6f
            8 -> return 1.0f
            9 -> return 0.6f
            else -> return 0.0f
        }
    }

    override fun onItemUseFinish(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase): ItemStack {
        if (entityLiving is EntityPlayer) {
            entityLiving.getFoodStats().addStats(this, stack)
        }
        worldIn.playSound(
            null,
            entityLiving.posX,
            entityLiving.posY,
            entityLiving.posZ,
            SoundEvents.ENTITY_PLAYER_BURP,
            SoundCategory.PLAYERS,
            0.5f,
            worldIn.rand.nextFloat() * 0.1f + 0.9f
        )
        if (!worldIn.isRemote && (stack.getItemDamage() < 4 || stack.getItemDamage() == 9)) {
            entityLiving.entityDropItem(ItemStack(GCItems.canister, 1, 0), 0.0f)
        }
        stack.shrink(1)
        return stack
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        val names: Array<String> = arrayOf<String>(
            "dehydrated_apple",
            "dehydrated_carrot",
            "dehydrated_melon",
            "dehydrated_potato",
            "cheese_slice",
            "burger_bun",
            "beef_patty_raw",
            "beef_patty_cooked",
            "cheeseburger",
            "canned_beef"
        )
    }
}
