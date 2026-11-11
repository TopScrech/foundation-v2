package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.legacyItemValue

import foundation.block.getMetaFromState

import foundation.level.offset

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityTier3Rocket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemTier3Rocket constructor(assetName: String?) : Item(), IHoldableItem, ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(1)
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun onItemUse(
        playerIn: EntityPlayer,
        worldIn: World,
        pos: BlockPos,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): EnumActionResult {
        var padFound: Boolean = false
        var tile: TileEntity? = null
        val stack: ItemStack = playerIn.getItemInHand(hand)

        if (worldIn.isClientSide) {
            return EnumActionResult.PASS
        } else {
            var centerX: Float = -1f
            var centerY: Float = -1f
            var centerZ: Float = -1f

            for (i: Int in -1..1) {
                for (j: Int in -1..1) {
                    val pos1: BlockPos = pos.offset(i, 0, j)
                    val state: IBlockState = worldIn.getBlockState(pos1)
                    val id: Block = state.getBlock()
                    val meta: Int = id.getMetaFromState(state)

                    if (id === GCBlocks.landingPadFull && meta == 0) {
                        padFound = true
                        tile = worldIn.getBlockEntity(pos1)

                        centerX = pos.getX() + i + 0.5f
                        centerY = pos.getY() + 0.4f
                        centerZ = pos.getZ() + j + 0.5f

                        break
                    }
                }

                if (padFound) {
                    break
                }
            }

            if (padFound) {
                if (!placeRocketOnPad(stack, worldIn, tile, centerX, centerY, centerZ)) {
                    return EnumActionResult.FAIL
                }

                if (!playerIn.capabilities.isCreativeMode) {
                    stack.shrink(1)
                }
                return EnumActionResult.SUCCESS
            } else {
                return EnumActionResult.PASS
            }
        }
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in IRocketType.EnumRocketType.entries.toTypedArray().indices) {
                list.add(ItemStack(this, 1, i))
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
        val type: IRocketType.EnumRocketType

        if (par1ItemStack.legacyItemValue < 10) {
            type = IRocketType.EnumRocketType.entries[par1ItemStack.legacyItemValue]
        } else {
            type = IRocketType.EnumRocketType.entries[par1ItemStack.legacyItemValue - 10]
        }

        if (!type.getTooltip().isEmpty()) {
            tooltip.add(type.getTooltip())
        }

        if (type.getPreFueled()) {
            tooltip.add(EnumColor.RED.toString() + "\u00a7o" + GCCoreUtil.translate("gui.creative_only.desc"))
        }

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound()!!.containsTag("RocketFuel")) {
            val rocket: EntityTier3Rocket = EntityTier3Rocket(
                FMLClientHandler.instance().getWorldClient(),
                0.0,
                0.0,
                0.0,
                IRocketType.EnumRocketType.entries[par1ItemStack.legacyItemValue]
            )
            tooltip.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound()!!
                    .getIntOr("RocketFuel", 0) + " / " + rocket.fuelTank.getCapacity()
            )
        }
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack?): String {
        return super.getTranslationKey(par1ItemStack) + ".t3Rocket"
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
        return EnumSortCategoryItem.ROCKET
    }

    companion object {
        @JvmStatic
        fun placeRocketOnPad(
            stack: ItemStack,
            worldIn: World,
            tile: TileEntity?,
            centerX: Float,
            centerY: Float,
            centerZ: Float
        ): Boolean {
            //Check whether there is already a rocket on the pad
            if (tile is TileEntityLandingPad) {
                if (tile.getDockedEntity() != null) {
                    return false
                }
            } else {
                return false
            }

            val rocket: EntityTier3Rocket = EntityTier3Rocket(
                worldIn,
                centerX.toDouble(),
                centerY.toDouble(),
                centerZ.toDouble(),
                IRocketType.EnumRocketType.entries[stack.legacyItemValue]
            )

            rocket.rotationYaw += 45f
            rocket.setPosition(rocket.posX, rocket.posY + rocket.getOnPadYOffset(), rocket.posZ)
            worldIn.addFreshEntity(rocket)

            if ((rocket.getType())!!.getPreFueled()) {
                rocket.fuelTank.fill(FluidStack(GCFluids.fluidFuel, rocket.getMaxFuel()), true)
            } else if (stack.hasTagCompound() && stack.getTagCompound()!!.containsTag("RocketFuel")) {
                rocket.fuelTank.fill(
                    FluidStack(GCFluids.fluidFuel, stack.getTagCompound()!!.getIntOr("RocketFuel", 0)),
                    true
                )
            }

            return true
        }
    }
}
