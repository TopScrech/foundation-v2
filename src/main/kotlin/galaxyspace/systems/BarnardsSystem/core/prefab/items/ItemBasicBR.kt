package galaxyspace.systems.BarnardsSystem.core.prefab.items

import galaxyspace.GalaxySpace
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemBasicBR
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Logs
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Swampland_2
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.block.BlockLiquid
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBasicBR constructor() : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(64)
        this.setTranslationKey("br_basic")
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                if (names[i] != "null") list.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val itemstack: ItemStack = player.getHeldItem(hand)
        val raytraceresult: RayTraceResult = this.rayTrace(world, player, true)
        if (raytraceresult == null) return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))

        val pos: BlockPos = raytraceresult.getBlockPos()

        if (itemstack.getItemDamage() == 0) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (!world.isBlockModifiable(player, pos) || !player.canPlayerEdit(
                        pos.offset(raytraceresult.sideHit),
                        raytraceresult.sideHit,
                        itemstack
                    )
                ) {
                    return ActionResult<ItemStack?>(EnumActionResult.FAIL, itemstack)
                }

                var hasWater: Boolean = false
                for (water: BlockPos in BlockPos.getAllInBox(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
                    if (world.getBlockState(water).getBlock() === FluidRegistry.WATER.getBlock()) {
                        hasWater = true
                        break
                    }
                }

                val blockpos1: BlockPos = pos.up()
                val iblockstate: IBlockState = world.getBlockState(pos)

                if (hasWater && world.isAirBlock(blockpos1) && (iblockstate === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                        .withProperty(
                            Barnarda_C_Grass.Companion.BASIC_TYPE,
                            Barnarda_C_Grass.EnumBlockGrass.GRASS
                        ) || iblockstate === BRBlocks.BARNARDA_C_BLOCKS.getDefaultState()
                        .withProperty(
                            Barnarda_C_Blocks.Companion.BASIC_TYPE,
                            EnumBlockBarnardaC.DIRT
                        ))
                ) {
                    world.setBlockState(
                        blockpos1,
                        BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                            .withProperty(
                                Barnarda_C_Dandelions.Companion.BASIC_TYPE,
                                Barnarda_C_Dandelions.EnumBlockDandelions.REEDS
                            )
                    )
                    val soundtype: SoundType = iblockstate.getBlock().getSoundType(iblockstate, world, pos, player)
                    world.playSound(
                        player,
                        pos,
                        soundtype.getPlaceSound(),
                        SoundCategory.BLOCKS,
                        (soundtype.getVolume() + 1.0f) / 2.0f,
                        soundtype.getPitch() * 0.8f
                    )
                    itemstack.shrink(1)

                    return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemstack)
                }
            }
        }
        if (itemstack.getItemDamage() == 2) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (!world.isBlockModifiable(player, pos) || !player.canPlayerEdit(
                        pos.offset(raytraceresult.sideHit),
                        raytraceresult.sideHit,
                        itemstack
                    )
                ) {
                    return ActionResult<ItemStack?>(EnumActionResult.FAIL, itemstack)
                }

                val blockpos1: BlockPos = pos.up()
                val iblockstate: IBlockState = world.getBlockState(pos)

                if (iblockstate.getMaterial() === Material.WATER && (iblockstate.getValue(BlockLiquid.LEVEL) as Int) == 0 && world.isAirBlock(
                        blockpos1
                    )
                ) {
                    world.setBlockState(blockpos1, BRBlocks.BARNARDA_C_WATER_GRASS.getDefaultState())

                    world.playSound(player, pos, SoundEvents.BLOCK_WATERLILY_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemstack)
                }
            }
        }
        if (itemstack.getItemDamage() == 3) {
            (GalaxySpace.instance)!!.debug(pos.toString() + "")
            if (world.isAirBlock(pos.up())) {
                WorldGenTree_Swampland_2(
                    BRBlocks.BARNARDA_C_VIOLET_LOG.getDefaultState()
                        .withProperty(
                            Barnarda_C_Logs.Companion.LOG_AXIS,
                            Barnarda_C_Logs.EnumAxis.NONE
                        ), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0), world.rand.nextInt(3)
                ).generate(world, world.rand, pos.up())
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item." + names[par1ItemStack.getItemDamage()]
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>(
            "violet_reeds",
            "water_grass",
            "tree_juice",
            "onyx" //"debugger"
        )
    }
}
