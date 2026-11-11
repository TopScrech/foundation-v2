package micdoodle8.mods.galacticraft.planets.mars.items

import foundation.item.legacyItemValue

import foundation.block.getMetaFromState

import foundation.level.offset

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityTier2Rocket
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

open class ItemTier2Rocket constructor() : Item(), IHoldableItem, ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(1)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun onItemUseFirst(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult {
        val stack: ItemStack = player.getItemInHand(hand)
        var padFound: Boolean = false
        var tile: TileEntity? = null

        if (world.isClientSide) {
            return EnumActionResult.PASS
        } else {
            var centerX: Float = -1f
            var centerY: Float = -1f
            var centerZ: Float = -1f

            for (i: Int in -1..1) {
                for (j: Int in -1..1) {
                    val pos1: BlockPos = pos.offset(i, 0, j)
                    val state: IBlockState = world.getBlockState(pos1)
                    val id: Block = state.getBlock()
                    val meta: Int = id.getMetaFromState(state)

                    if (id === GCBlocks.landingPadFull && meta == 0) {
                        padFound = true
                        tile = world.getBlockEntity(pos1)

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
                if (!placeRocketOnPad(stack, world, tile, centerX, centerY, centerZ)) {
                    return EnumActionResult.FAIL
                }

                if (!player.capabilities.isCreativeMode) {
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

            for (i: Int in 11..<10 + IRocketType.EnumRocketType.entries.size) {
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
            val rocket: EntityAutoRocket

            if (par1ItemStack.legacyItemValue < 10) {
                rocket = EntityTier2Rocket(
                    FMLClientHandler.instance().getWorldClient(),
                    0.0,
                    0.0,
                    0.0,
                    IRocketType.EnumRocketType.entries[par1ItemStack.legacyItemValue]
                )
            } else {
                rocket = EntityCargoRocket(
                    FMLClientHandler.instance().getWorldClient(),
                    0.0,
                    0.0,
                    0.0,
                    IRocketType.EnumRocketType.entries[par1ItemStack.legacyItemValue - 10]
                )
            }

            tooltip.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound()!!
                    .getIntOr("RocketFuel", 0) + " / " + rocket.fuelTank.getCapacity()
            )
        }

        if (par1ItemStack.legacyItemValue >= 10) {
            tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.requires_controller.desc"))
        }
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return super.getTranslationKey(par1ItemStack) + (if (par1ItemStack.legacyItemValue < 10) ".t2Rocket" else ".cargo_rocket")
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
            world: World,
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

            val rocket: EntityAutoRocket

            if (stack.legacyItemValue < 10) {
                rocket = EntityTier2Rocket(
                    world,
                    centerX.toDouble(),
                    centerY.toDouble(),
                    centerZ.toDouble(),
                    IRocketType.EnumRocketType.entries[stack.legacyItemValue]
                )
            } else {
                rocket = EntityCargoRocket(
                    world,
                    centerX.toDouble(),
                    centerY.toDouble(),
                    centerZ.toDouble(),
                    IRocketType.EnumRocketType.entries[stack.legacyItemValue - 10]
                )
            }

            rocket.setPosition(rocket.posX, rocket.posY + rocket.getOnPadYOffset(), rocket.posZ)
            world.addFreshEntity(rocket)

            if (((rocket as IRocketType).getType())!!.getPreFueled()) {
                if (rocket is EntityTieredRocket) {
                    rocket.fuelTank.fill(FluidStack(GCFluids.fluidFuel, rocket.getMaxFuel()), true)
                } else {
                    (rocket as EntityCargoRocket).fuelTank.fill(
                        FluidStack(GCFluids.fluidFuel, rocket.getMaxFuel()),
                        true
                    )
                }
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
