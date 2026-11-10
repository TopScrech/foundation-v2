package galaxyspace.systems.SolarSystem.planets.mars.items

import galaxyspace.systems.SolarSystem.planets.mars.entities.EntityMarsRover
import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemMarsRover constructor(assetName: String) : Item(), IHoldableItem, ISortableItem {
    init {
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
        this.setMaxStackSize(1)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSVehiclesTab || tab === CreativeTabs.SEARCH) {
            //for (int i = 0; i < 4; i++)
            //{
            list.add(ItemStack(this, 1, 0))
            //}
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemstack: ItemStack = playerIn.getHeldItem(hand)
        val var4: Float = 1.0f
        val var5: Float = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * var4
        val var6: Float = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * var4
        val var7: Double = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * var4
        val var9: Double = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * var4 + 1.62 - playerIn.getYOffset()
        val var11: Double = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * var4
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
                playerIn.getEntityBoundingBox().grow(var25.x * var21, var25.y * var21, var25.z * var21)
                    .expand(var27.toDouble(), var27.toDouble(), var27.toDouble())
            )
            var var29: Int

            var29 = 0
            while (var29 < var28.size) {
                val var30: Entity = var28.get(var29) as Entity

                if (var30.canBeCollidedWith()) {
                    val var31: Float = var30.getCollisionBorderSize()
                    val var32: AxisAlignedBB =
                        var30.getEntityBoundingBox().expand(var31.toDouble(), var31.toDouble(), var31.toDouble())

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

                    if (worldIn.getBlockState(BlockPos(var29, var33, var34)).getBlock() === Blocks.SNOW) {
                        --var33
                    }

                    val var35: EntityMarsRover = EntityMarsRover(
                        worldIn,
                        (var29 + 0.5f).toDouble(),
                        (var33 + 1.0f).toDouble(),
                        (var34 + 0.5f).toDouble(),
                        itemstack.getItemDamage()
                    )

                    if (!worldIn.getCollisionBoxes(var35, var35.getEntityBoundingBox().expand(-0.1, -0.1, -0.1))
                            .isEmpty()
                    ) {
                        return ActionResult<ItemStack?>(EnumActionResult.PASS, itemstack)
                    }

                    if (itemstack.hasTagCompound() && itemstack.getTagCompound()!!.hasKey("BuggyFuel")) {
                        var35.buggyFuelTank.setFluid(
                            FluidStack(
                                GCFluids.fluidFuel,
                                itemstack.getTagCompound()!!.getInteger("BuggyFuel")
                            )
                        )
                    }

                    if (!worldIn.isRemote) {
                        worldIn.spawnEntity(var35)
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1)
                    }
                }

                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemstack)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(EnumColor.DARK_RED.toString() + "[WIP] Content")

        if (par1ItemStack.getItemDamage() != 0) {
            tooltip.add(GCCoreUtil.translate("gui.buggy.storage_space") + ": " + par1ItemStack.getItemDamage() * 18)
        }

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound()!!.hasKey("BuggyFuel")) {
            tooltip.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound()!!
                    .getInteger("BuggyFuel") + " / " + EntityMarsRover.Companion.tankCapacity
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
