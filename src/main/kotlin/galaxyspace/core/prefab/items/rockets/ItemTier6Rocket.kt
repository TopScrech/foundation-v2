package galaxyspace.core.prefab.items.rockets

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.GSBlocks
import galaxyspace.core.GSFluids
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPad
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import galaxyspace.core.prefab.entities.EntityTier6Rocket
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.api.item.IModificationItem
import galaxyspace.core.prefab.items.rockets.ItemTier6Rocket
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.SublightEngine
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemTier6Rocket constructor(assetName: String) : Item(), IHoldableItem, ISortableItem,
    IModificationItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(1)
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSVehiclesTab
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
        val stack: ItemStack = playerIn.getHeldItem(hand)

        if (worldIn.isRemote) {
            return EnumActionResult.PASS
        } else {
            var centerX: Float = -1f
            var centerY: Float = -1f
            var centerZ: Float = -1f

            for (i: Int in -1..1) {
                for (j: Int in -1..1) {
                    val pos1: BlockPos = pos.add(i, 0, j)
                    val state: IBlockState = worldIn.getBlockState(pos1)
                    val id: Block = state.getBlock()
                    val meta: Int = id.getMetaFromState(state)

                    if ( /*id == GCBlocks.landingPadFull && meta == 0 ||*/id === GSBlocks.ADVANCED_LANDING_PAD && meta == 0) {
                        padFound = true
                        tile = worldIn.getTileEntity(pos1)

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
                if (!placeRocketOnPad(playerIn, stack, worldIn, tile, centerX, centerY, centerZ)) {
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
        if (tab === this.getCreativeTab() || tab === CreativeTabs.SEARCH) {
            for (i: Int in IRocketType.EnumRocketType.entries.toTypedArray().indices) {
                list.add(ItemStack(this, 1, i))
            }

            val withengine: ItemStack = ItemStack(this, 1, 0)
            if (!withengine.hasTagCompound()) withengine.setTagCompound(NBTTagCompound())
            withengine.getTagCompound()!!.setBoolean(Engine_Type.SUBLIGHT_ENGINE.getName(), true)
            withengine.getTagCompound()!!.setInteger(ItemSpaceSuit.mod_count, 0)

            list.add(withengine)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        val type: IRocketType.EnumRocketType

        tooltip.add(EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.message.need_advanced_lp.desc"))

        if (par1ItemStack.getItemDamage() < 10) {
            if (par1ItemStack.getItemDamage() >= 5) type =
                IRocketType.EnumRocketType.entries[par1ItemStack.getItemDamage() - 5]
            else type = IRocketType.EnumRocketType.entries[par1ItemStack.getItemDamage()]
        } else {
            type = IRocketType.EnumRocketType.entries[par1ItemStack.getItemDamage() - 10]
        }

        if (!type.getTooltip().isEmpty()) {
            tooltip.add(type.getTooltip())
        }

        if (type.getPreFueled()) {
            tooltip.add(EnumColor.RED.toString() + "\u00a7o" + GCCoreUtil.translate("gui.creative_only.desc"))
        }

        if (par1ItemStack.hasTagCompound()) {
            if (par1ItemStack.getTagCompound()!!.hasKey("RocketFuel")) {
                val meta: Int =
                    if (par1ItemStack.getItemDamage() >= 5) par1ItemStack.getItemDamage() - 5 else par1ItemStack.getItemDamage()

                val rocket: EntityTier6Rocket = EntityTier6Rocket(
                    FMLClientHandler.instance().getWorldClient(),
                    0.0,
                    0.0,
                    0.0,
                    IRocketType.EnumRocketType.entries[meta]
                )
                tooltip.add(
                    GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound()!!
                        .getInteger("RocketFuel") + " / " + rocket.fuelTank.getCapacity()
                )
            }

            for (engines: Engine_Type in Engine_Type.entries) {
                if (par1ItemStack.getTagCompound()!!.hasKey(engines.getName())) {
                    //int engine = par1ItemStack.getTagCompound().getInteger("engine_type");
                    val meta: Int =
                        if (par1ItemStack.getItemDamage() >= 5) par1ItemStack.getItemDamage() - 5 else par1ItemStack.getItemDamage()
                    val rocket: EntityTier6Rocket = EntityTier6Rocket(
                        FMLClientHandler.instance().getWorldClient(),
                        0.0,
                        0.0,
                        0.0,
                        IRocketType.EnumRocketType.entries[meta],
                        engines
                    )
                    tooltip.add(
                        EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.message.engine_type.name") + " " + EnumColor.WHITE + (rocket.getEngine())!!
                            .getName()
                    )
                }
            }
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
        return EnumSortCategoryItem.ROCKET
    }

    override fun getType(stack: ItemStack?): Module_Type {
        return Module_Type.ROCKET
    }

    override fun getAvailableModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(SublightEngine()) as Array<ItemModule?>?))!!
    }

    override fun getModificationCount(stack: ItemStack?): Int {
        return 1
    }

    companion object {
        @JvmStatic
        fun placeRocketOnPad(
            player: EntityPlayer,
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
            } else if (tile is TileEntityAdvLandingPad) {
                if (tile.getDockedEntity() != null) {
                    return false
                }
            } else {
                return false
            }

            val rocket: EntityTier6Rocket = EntityTier6Rocket(
                worldIn,
                centerX.toDouble(),
                centerY.toDouble(),
                centerZ.toDouble(),
                IRocketType.EnumRocketType.entries[stack.getItemDamage()]
            )

            for (engines: Engine_Type in Engine_Type.entries) if (stack.hasTagCompound() && stack.getTagCompound()!!
                    .hasKey(engines.getName())
            ) {
                //int engine = stack.getTagCompound().getInteger("engine_type");
                if (engines == Engine_Type.ION_ENGINE) {
                    if (worldIn.provider is IGalacticraftWorldProvider) {
                        if ((worldIn.provider as IGalacticraftWorldProvider).getGravity() < 0.060f) {
                            player.sendMessage(
                                TextComponentString(
                                    EnumColor.DARK_RED.toString() + GCCoreUtil.translate(
                                        "gui.message.planet_not_for_ion"
                                    )
                                )
                            )
                            return false
                        }
                    } else {
                        player.sendMessage(TextComponentString(EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.message.planet_not_for_ion")))
                        return false
                    }
                }

                rocket.setEngine(engines)
            }

            rocket.rotationYaw += 45f
            rocket.setPosition(rocket.posX, rocket.posY + rocket.getOnPadYOffset(), rocket.posZ)
            worldIn.spawnEntity(rocket)

            if ((rocket.getType())!!.getPreFueled()) {
                rocket.fuelTank.fill(
                    FluidStack(GCFluids.fluidFuel,  /*GSFluids.HeliumHydrogen*/rocket.getMaxFuel()),
                    true
                )
            } else if (stack.hasTagCompound() && stack.getTagCompound()!!.hasKey("RocketFuel")) {
                rocket.fuelTank.fill(
                    FluidStack(
                        GCFluids.fluidFuel,  /*SFluids.HeliumHydrogen*/
                        stack.getTagCompound()!!.getInteger("RocketFuel")
                    ), true
                )
            }




            return true
        }
    }
}
