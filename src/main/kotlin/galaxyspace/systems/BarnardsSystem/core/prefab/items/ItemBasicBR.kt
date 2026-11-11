package galaxyspace.systems.BarnardsSystem.core.prefab.items

import foundation.item.legacyItemValue

import foundation.block.getStateFromMeta

import foundation.level.offset

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
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

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
        val itemstack: ItemStack = player.getItemInHand(hand)
        val raytraceresult: RayTraceResult = this.rayTrace(world, player, true)
        if (raytraceresult == null) return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getItemInHand(hand))

        val pos: BlockPos = raytraceresult.getBlockPos()

        if (itemstack.legacyItemValue == 0) {
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
                for (water: BlockPos in BlockPos.getAllInBox(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
                    if (world.getBlockState(water).getBlock() === FluidRegistry.WATER.getBlock()) {
                        hasWater = true
                        break
                    }
                }

                val blockpos1: BlockPos = pos.above()
                val iblockstate: IBlockState = world.getBlockState(pos)

                if (hasWater && world.isEmptyBlock(blockpos1) && (iblockstate === BRBlocks.BARNARDA_C_GRASS.defaultBlockState()
                        .setValue(
                            Barnarda_C_Grass.Companion.BASIC_TYPE,
                            Barnarda_C_Grass.EnumBlockGrass.GRASS
                        ) || iblockstate === BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState()
                        .setValue(
                            Barnarda_C_Blocks.Companion.BASIC_TYPE,
                            EnumBlockBarnardaC.DIRT
                        ))
                ) {
                    world.setBlockAndUpdate(
                        blockpos1,
                        BRBlocks.BARNARDA_C_DANDELIONS.defaultBlockState()
                            .setValue(
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
        if (itemstack.legacyItemValue == 2) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (!world.isBlockModifiable(player, pos) || !player.canPlayerEdit(
                        pos.offset(raytraceresult.sideHit),
                        raytraceresult.sideHit,
                        itemstack
                    )
                ) {
                    return ActionResult<ItemStack?>(EnumActionResult.FAIL, itemstack)
                }

                val blockpos1: BlockPos = pos.above()
                val iblockstate: IBlockState = world.getBlockState(pos)

                if (iblockstate.getMaterial() === Material.WATER && (iblockstate.getValue(BlockLiquid.LEVEL) as Int) == 0 && world.isEmptyBlock(
                        blockpos1
                    )
                ) {
                    world.setBlockAndUpdate(blockpos1, BRBlocks.BARNARDA_C_WATER_GRASS.defaultBlockState())

                    world.playSound(player, pos, SoundEvents.BLOCK_WATERLILY_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, itemstack)
                }
            }
        }
        if (itemstack.legacyItemValue == 3) {
            (GalaxySpace.instance)!!.debug(pos.toString() + "")
            if (world.isEmptyBlock(pos.above())) {
                WorldGenTree_Swampland_2(
                    BRBlocks.BARNARDA_C_VIOLET_LOG.defaultBlockState()
                        .setValue(
                            Barnarda_C_Logs.Companion.LOG_AXIS,
                            Barnarda_C_Logs.EnumAxis.NONE
                        ), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0), world.random.nextInt(3)
                ).generate(world, world.random, pos.above())
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getItemInHand(hand))
            }
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getItemInHand(hand))
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item." + names[par1ItemStack.legacyItemValue]
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
