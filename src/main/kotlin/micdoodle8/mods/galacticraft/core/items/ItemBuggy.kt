package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.util.math.*
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBuggy constructor(assetName: String?) : Item(), IHoldableItem, ISortableItem {
    init {
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
        this.setMaxStackSize(1)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in 0..3) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemstack: ItemStack = playerIn.getItemInHand(hand)
        val var4: Float = 1.0f
        val var5: Float = playerIn.xRotO + (playerIn.xRot - playerIn.xRotO) * var4
        val var6: Float = playerIn.yRotO + (playerIn.yRot - playerIn.yRotO) * var4
        val var7: Double = playerIn.prevPosX + (playerIn.x - playerIn.prevPosX) * var4
        val var9: Double = playerIn.prevPosY + (playerIn.y - playerIn.prevPosY) * var4 + 1.62 - playerIn.getYOffset()
        val var11: Double = playerIn.prevPosZ + (playerIn.z - playerIn.prevPosZ) * var4
        val var13: Vec3d = Vec3d(var7, var9, var11)
        val var14: Float = MathHelper.cos(-var6 / Constants.RADIANS_TO_DEGREES - Math.PI.toFloat())
        val var15: Float = MathHelper.sin(-var6 / Constants.RADIANS_TO_DEGREES - Math.PI.toFloat())
        val var16: Float = -MathHelper.cos(-var5 / Constants.RADIANS_TO_DEGREES)
        val var17: Float = MathHelper.sin(-var5 / Constants.RADIANS_TO_DEGREES)
        val var18: Float = var15 * var16
        val var20: Float = var14 * var16
        val var21: Double = 5.0
        val var23: Vec3d = var13.add(var18 * var21, var17 * var21, var20 * var21)
        val var24: RayTraceResult? = worldIn.rayTraceBlocks(var13, var23, true)

        if (var24 == null) {
            return ActionResult<ItemStack?>(EnumActionResult.PASS, itemstack)
        } else {
            val var25: Vec3d = playerIn.getLook(var4)
            var var26: Boolean = false
            val var27: Float = 1.0f
            val var28: MutableList<*> = worldIn.getEntitiesWithinAABBExcludingEntity(
                playerIn,
                playerIn.getBoundingBox().grow(var25.x * var21, var25.y * var21, var25.z * var21)
                    .expand(var27.toDouble(), var27.toDouble(), var27.toDouble())
            )
            var var29: Int

            var29 = 0
            while (var29 < var28.size) {
                val var30: Entity = var28.get(var29) as Entity

                if (var30.canBeCollidedWith()) {
                    val var31: Float = var30.getCollisionBorderSize()
                    val var32: AxisAlignedBB =
                        var30.getBoundingBox().expand(var31.toDouble(), var31.toDouble(), var31.toDouble())

                    if (var32.contains(var13)) {
                        var26 = true
                    }
                }
                ++var29
            }

            if (var26) {
                return ActionResult<ItemStack?>(EnumActionResult.PASS, itemstack)
            } else {
                if (var24.typeOfHit == RayTraceResult.Type.BLOCK) {
                    var29 = var24.getBlockPos().getX()
                    var var33: Int = var24.getBlockPos().getY()
                    val var34: Int = var24.getBlockPos().getZ()

                    if (worldIn.getBlockState(BlockPos(var29, var33, var34)) === Blocks.SNOW) {
                        --var33
                    }

                    val var35: EntityBuggy = EntityBuggy(
                        worldIn,
                        (var29 + 0.5f).toDouble(),
                        (var33 + 1.0f).toDouble(),
                        (var34 + 0.5f).toDouble(),
                        itemstack.legacyItemValue
                    )

                    if (!worldIn.getCollisionBoxes(var35, var35.getEntityBoundingBox().expand(-0.1, -0.1, -0.1))
                            .isEmpty()
                    ) {
                        return ActionResult<ItemStack?>(EnumActionResult.PASS, itemstack)
                    }

                    if (itemstack.hasTagCompound() && itemstack.getTagCompound()!!.containsTag("BuggyFuel")) {
                        var35.buggyFuelTank.setFluid(
                            FluidStack(
                                GCFluids.fluidFuel,
                                itemstack.getTagCompound()!!.getIntOr("BuggyFuel", 0)
                            )
                        )
                    }

                    if (!worldIn.isClientSide) {
                        worldIn.addFreshEntity(var35)
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1)
                    }
                }

                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemstack)
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.legacyItemValue != 0) {
            tooltip.add(GCCoreUtil.translate("gui.buggy.storage_space") + ": " + par1ItemStack.legacyItemValue * 18)
        }

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound()!!.containsTag("BuggyFuel")) {
            tooltip.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound()!!
                    .getIntOr("BuggyFuel", 0) + " / " + EntityBuggy.tankCapacity
            )
        }
    }

    override fun shouldHoldLeftHandUp(player: EntityPlayer?): Boolean {
        return true
    }

    override fun shouldHoldRightHandUp(player: EntityPlayer?): Boolean {
        return true
    }

    override fun shouldCrouch(player: EntityPlayer?): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
