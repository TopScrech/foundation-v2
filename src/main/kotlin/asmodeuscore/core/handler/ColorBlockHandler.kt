package asmodeuscore.core.handler

import asmodeuscore.core.utils.worldengine.WE_WorldProvider
import asmodeuscore.core.handler.ColorBlockHandler
import asmodeuscore.api.blocks.ICustomLeaves
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import net.minecraft.block.BlockOldLeaf
import net.minecraft.block.BlockPlanks
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.color.BlockColors
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.client.renderer.color.ItemColors
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ColorizerFoliage
import net.minecraft.world.ColorizerGrass
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeColorHelper
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.HashMap

open class ColorBlockHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun registerBlockColourHandlers(event: ColorHandlerEvent.Block) {
        val blockColors: BlockColors = event.getBlockColors()

        val grassColourHandler: IBlockColor =
            IBlockColor { state: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, tintIndex: Int ->
                if (blockAccess != null && pos != null) {
                    if (world != null) {
                        if (world!!.provider is WE_WorldProvider) {
                            val chunk: WE_ChunkProvider? = (world!!.provider as WE_WorldProvider).chunk_provider
                            if (chunk != null && (world!!.provider as WE_WorldProvider).isColorWorld()) return@IBlockColor (WE_Biome.Companion.getBiomeAt(
                                chunk,
                                pos.getX().toLong(),
                                pos.getZ().toLong()
                            ))!!.biomeBlockGrassColor
                        }
                    }
                    return@IBlockColor BiomeColorHelper.getGrassColorAtPos(blockAccess, pos)
                }
                ColorizerGrass.getGrassColor(0.5, 1.0)
            }

        for (block: IBlockState in blocks) blockColors.registerBlockColorHandler(grassColourHandler, block.getBlock())

        val leavesColourHandler: IBlockColor =
            IBlockColor { state: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, tintIndex: Int ->
                if (blockAccess != null && pos != null) {
                    if (world != null && world!!.provider is WE_WorldProvider) {
                        val chunk: WE_ChunkProvider? = (world!!.provider as WE_WorldProvider).chunk_provider

                        if (chunk != null) return@IBlockColor (WE_Biome.Companion.getBiomeAt(
                            chunk,
                            pos.getX().toLong(),
                            pos.getZ().toLong()
                        ))!!.biomeBlockLeavesColor
                    }

                    if (state!!.getBlock() === Blocks.LEAVES) {
                        val `blockplanks$enumtype`: BlockPlanks.EnumType =
                            (state)!!.getValue(BlockOldLeaf.VARIANT) as BlockPlanks.EnumType

                        if (`blockplanks$enumtype` == BlockPlanks.EnumType.SPRUCE) {
                            return@IBlockColor ColorizerFoliage.getFoliageColorPine()
                        } else if (`blockplanks$enumtype` == BlockPlanks.EnumType.BIRCH) {
                            return@IBlockColor ColorizerFoliage.getFoliageColorBirch()
                        } else {
                            return@IBlockColor BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos)
                        }
                    }

                    if ((state)!!.getBlock() === Blocks.LEAVES2) {
                        return@IBlockColor BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos)
                    }
                }
                if (state!!.getBlock() is ICustomLeaves) return@IBlockColor (state.getBlock() as ICustomLeaves).getDefaultLeavesColor()
                ColorizerFoliage.getFoliageColorBasic()
            }

        for (block: IBlockState in leaves) blockColors.registerBlockColorHandler(leavesColourHandler, block.getBlock())

        for (color_blocks: MutableMap.MutableEntry<IBlockState?,Int> in color_blocks.entries) {
            val colorHandler: IBlockColor =
                IBlockColor { state: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, tintIndex: Int -> color_blocks.value!! }
            blockColors.registerBlockColorHandler(colorHandler, color_blocks.key!!.getBlock())
        }

        val waterColourHandler: IBlockColor =
            IBlockColor { state: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, tintIndex: Int ->
                if (blockAccess != null && pos != null) {
                    if (world != null && world!!.provider is WE_WorldProvider) {
                        val chunk: WE_ChunkProvider? = (world!!.provider as WE_WorldProvider).chunk_provider

                        if (chunk != null) {
                            val biome: WE_Biome? =
                                WE_Biome.Companion.getBiomeAt(chunk, pos.getX().toLong(), pos.getZ().toLong())
                            if (biome != null) return@IBlockColor biome.biomeBlockWaterColor

                            //return WE_Biome.getBiomeAt(chunk, (long)pos.getX(), (long)pos.getZ()).biomeBlockWaterColor;
                        }
                    }

                    return@IBlockColor BiomeColorHelper.getWaterColorAtPos(blockAccess, pos)
                }
                ColorizerGrass.getGrassColor(0.5, 1.0)
            }

        for (block: IBlockState in water_blocks) blockColors.registerBlockColorHandler(
            waterColourHandler,
            block.getBlock()
        )
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun registerItemColourHandlers(event: ColorHandlerEvent.Item) {
        val blockColors: BlockColors = event.getBlockColors()
        val itemColors: ItemColors? = event.getItemColors()


        if (itemColors != null) {
            val itemBlockColourHandler: IItemColor = IItemColor { stack: ItemStack?, tintIndex: Int ->
                if (stack != null || stack != ItemStack.EMPTY) {
                    if (stack!!.getItem() is ItemBlock) {
                        val state: IBlockState =
                            (stack.getItem() as ItemBlock).getBlock().getStateFromMeta(stack.getMetadata())
                        return@IItemColor blockColors.colorMultiplier(state, null, null, tintIndex)
                    }
                    return@IItemColor itemColors.colorMultiplier(stack, tintIndex)
                }
                -1
            }

            for (block: IBlockState in blocks) itemColors.registerItemColorHandler(
                itemBlockColourHandler,
                block.getBlock()
            )

            for (block: IBlockState in leaves) itemColors.registerItemColorHandler(
                itemBlockColourHandler,
                block.getBlock()
            )
        }
    }

    companion object {
        private val blocks: MutableList<IBlockState> = ArrayList<IBlockState>()
        private val leaves: MutableList<IBlockState> = ArrayList<IBlockState>()
        private val water_blocks: MutableList<IBlockState> = ArrayList<IBlockState>()
        private val color_blocks: MutableMap<IBlockState?,Int> = HashMap<IBlockState?,Int>()

        @JvmField
        var world: World? = null

        @JvmStatic
        fun addBlock(block: IBlockState) {
            if (!blocks.contains(block)) blocks.add(block)
        }

        @JvmStatic
        fun addLeavesBlock(block: IBlockState) {
            if (!leaves.contains(block)) leaves.add(block)
        }

        @JvmStatic
        fun addBlockWithColor(block: IBlockState?, color: Int) {
            if (!color_blocks.containsKey(block)) color_blocks.put(block, color)
        }

        @JvmStatic
        fun addWaterBlock(block: IBlockState) {
            if (!water_blocks.contains(block)) water_blocks.add(block)
        }
    }
}
