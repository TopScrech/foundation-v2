package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.item.EnumAction
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemGrappleHook constructor(assetName: String?) : ItemBow(), ISortableItem {
    init {
        this.setTranslationKey(assetName)
        this.setMaxStackSize(1)
        //this.setTextureName("arrow");
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entity: EntityLivingBase, timeLeft: Int) {
        if (entity !is EntityPlayer) {
            return
        }

        val player: EntityPlayer = entity

        var canShoot: Boolean = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(
            Enchantments.INFINITY,
            stack
        ) > 0
        var string: ItemStack? = null

        for (itemstack: ItemStack? in player.inventory.mainInventory) {
            if (itemstack != null && itemstack.getItem() === Items.STRING) {
                string = itemstack
                canShoot = true
            }
        }

        if (canShoot) {
            if (string == null) {
                string = ItemStack(Items.STRING, 1)
            }

            val grapple: EntityGrapple = EntityGrapple(worldIn, player, 2.0f)

            worldIn.playSound(
                null as EntityPlayer?,
                player.posX,
                player.posY,
                player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.NEUTRAL,
                1.0f,
                1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f
            )

            if (!worldIn.isRemote) {
                worldIn.spawnEntity(grapple)
            }

            stack.damageItem(1, player)
            grapple.canBePickedUp = if (player.capabilities.isCreativeMode) 2 else 1

            if (!player.capabilities.isCreativeMode) {
                string.shrink(1)

                if (string.isEmpty()) {
                    player.inventory.deleteStack(string)
                }
            }
        } else if (worldIn.isRemote) {
            player.sendMessage(TextComponentString(GCCoreUtil.translate("gui.message.grapple.fail")))
        }
    }

    override fun getMaxItemUseDuration(par1ItemStack: ItemStack): Int {
        return 72000
    }

    override fun getItemUseAction(par1ItemStack: ItemStack): EnumAction {
        return EnumAction.BOW
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        playerIn.setActiveHand(hand)
        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
