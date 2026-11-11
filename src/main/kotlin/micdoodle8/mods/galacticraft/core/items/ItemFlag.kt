package micdoodle8.mods.galacticraft.core.items

import net.minecraft.network.chat.Component

import foundation.item.legacyItemValue

import foundation.inventory.mainItems
import micdoodle8.mods.galacticraft.api.item.IHoldableItemCustom
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityFlag
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult

import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemFlag constructor(assetName: String?) : Item(), IHoldableItemCustom, ISortableItem {
    @JvmField
    var placeProgress: Int = 0

    init {
        this.setMaxDamage(0)
        this.setMaxStackSize(1)
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entity: EntityLivingBase, timeLeft: Int) {
        val useTime: Int = this.getMaxItemUseDuration(stack) - timeLeft

        var placed: Boolean = false

        if (entity !is EntityPlayer) {
            return
        }

        val player: EntityPlayer = entity

        val var12: RayTraceResult = this.rayTrace(worldIn, player, true)

        var var7: Float = useTime / 20.0f
        var7 = (var7 * var7 + var7 * 2.0f) / 3.0f

        if (var7 > 1.0f) {
            var7 = 1.0f
        }

        if (var7 == 1.0f && var12 != null && var12.typeOfHit == RayTraceResult.Type.BLOCK) {
            val pos: BlockPos = var12.getBlockPos()

            if (!worldIn.isClientSide) {
                val flag: EntityFlag = EntityFlag(
                    worldIn,
                    (pos.getX() + 0.5f).toDouble(),
                    (pos.getY() + 1.0f).toDouble(),
                    (pos.getZ() + 0.5f).toDouble(),
                    (entity.yRot - 90).toInt()
                )

                if (worldIn.getEntitiesWithinAABB<EntityFlag?>(
                        EntityFlag::class.java,
                        AxisAlignedBB(
                            pos.getX().toDouble(),
                            pos.getY().toDouble(),
                            pos.getZ().toDouble(),
                            (pos.getX() + 1).toDouble(),
                            (pos.getY() + 3).toDouble(),
                            (pos.getZ() + 1).toDouble()
                        )
                    ).isEmpty()
                ) {
                    worldIn.addFreshEntity(flag)
                    flag.setType(stack.legacyItemValue)
                    flag.setOwner(PlayerUtil.getName(player))
                    worldIn.playSound(
                        null,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        SoundType.METAL.getBreakSound(),
                        SoundCategory.BLOCKS,
                        SoundType.METAL.getVolume(),
                        SoundType.METAL.getPitch() + 2.0f
                    )
                    placed = true
                } else {
                    entity.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.flag.already_placed")))
                }
            }

            if (placed) {
                val var2: Int = this.getInventorySlotContainItem(player, stack)

                if (var2 >= 0 && !player.capabilities.isCreativeMode) {
                    player.inventory.mainItems.get(var2).shrink(1)
                }
            }
        }
    }

    private fun getInventorySlotContainItem(player: EntityPlayer, stack: ItemStack): Int {
        for (var2: Int in player.inventory.mainItems.indices) {
            if (!player.inventory.mainItems.get(var2).isEmpty() && player.inventory.mainItems.get(var2)
                    .isItemEqual(stack)
            ) {
                return var2
            }
        }

        return -1
    }

    override fun getMaxItemUseDuration(par1ItemStack: ItemStack): Int {
        return 72000
    }

    override fun getItemUseAction(par1ItemStack: ItemStack): EnumAction {
        return EnumAction.NONE
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        playerIn.setActiveHand(hand)
        return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, playerIn.getItemInHand(hand))
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack?): String {
        return "item.flag"
    }

    /*@Override
    public IIcon getIconFromDamage(int damage)
    {
        return super.getIconFromDamage(damage);
    }*/
    override fun shouldHoldLeftHandUp(player: EntityPlayer?): Boolean {
        return true
    }

    override fun shouldHoldRightHandUp(player: EntityPlayer?): Boolean {
        return true
    }

    override fun getLeftHandRotation(player: EntityPlayer?): Vector3 {
        return Vector3((Math.PI.toFloat() + 1.3f).toDouble(), 0.5, (Math.PI.toFloat() / 5.0f).toDouble())
    }

    override fun getRightHandRotation(player: EntityPlayer?): Vector3 {
        return Vector3((Math.PI.toFloat() + 1.3f).toDouble(), -0.5, (Math.PI.toFloat() / 5.0f).toDouble())
    }

    override fun shouldCrouch(player: EntityPlayer?): Boolean {
        return false
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
