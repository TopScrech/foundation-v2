package micdoodle8.mods.galacticraft.planets.asteroids.items

import net.minecraft.network.chat.Component

import foundation.inventory.mainItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.Items
import net.minecraft.sounds.SoundEvents
import net.minecraft.item.EnumAction
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBow
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.sounds.SoundSource as SoundCategory

import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemGrappleHook constructor(assetName: String?) : ItemBow(), ISortableItem {
    init {
        this.setTranslationKey(assetName)
        this.setMaxStackSize(1)
        //this.setTextureName("arrow");
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
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

        for (itemstack: ItemStack? in player.inventory.mainItems) {
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
                player.x,
                player.y,
                player.z,
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.NEUTRAL,
                1.0f,
                1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f
            )

            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(grapple)
            }

            stack.damageItem(1, player)
            grapple.canBePickedUp = if (player.capabilities.isCreativeMode) 2 else 1

            if (!player.capabilities.isCreativeMode) {
                string.shrink(1)

                if (string.isEmpty()) {
                    player.inventory.removeItem(string)
                }
            }
        } else if (worldIn.isClientSide) {
            player.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.message.grapple.fail")))
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
        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, playerIn.getItemInHand(hand))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
