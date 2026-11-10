package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeDecorator
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.common.MinecraftForge
import java.lang.RuntimeException
import java.util.Random

open class BiomeDecoratorMoon constructor() : BiomeDecorator() {
    private var world: World? = null
    private var randomGenerator: Random? = null

    private val moonDirtGenerator: WorldGenerator
    private val cheeseGen: WorldGenerator
    private val copperGen: WorldGenerator
    private val tinGen: WorldGenerator

    init {
        this.copperGen = WorldGenMinableMeta((GCBlocks.blockMoon)!!, 4, 0, true, (GCBlocks.blockMoon)!!, 4)
        this.tinGen = WorldGenMinableMeta((GCBlocks.blockMoon)!!, 4, 1, true, (GCBlocks.blockMoon)!!, 4)
        this.cheeseGen = WorldGenMinableMeta((GCBlocks.blockMoon)!!, 3, 2, true, (GCBlocks.blockMoon)!!, 4)
        this.moonDirtGenerator = WorldGenMinableMeta((GCBlocks.blockMoon)!!, 32, 3, true, (GCBlocks.blockMoon)!!, 4)
    }

    override fun decorate(worldIn: World, random: Random, p_180292_3_: Biome, pos: BlockPos) {
        if (this.world != null) {
            throw RuntimeException("Already decorating!!")
        } else {
            this.world = worldIn
            this.randomGenerator = random
            this.chunkPos = pos
            this.generateMoon()
            this.world = null
            this.randomGenerator = null
        }
    }

    private fun genStandardOre(amountPerChunk: Int, worldGenerator: WorldGenerator, minY: Int, maxY: Int) {
        for (var5: Int in 0..<amountPerChunk) {
            val blockpos: BlockPos = this.chunkPos.add(
                this.randomGenerator!!.nextInt(16),
                this.randomGenerator!!.nextInt(maxY - minY) + minY,
                this.randomGenerator!!.nextInt(16)
            )
            worldGenerator.generate(this.world, this.randomGenerator, blockpos)
        }
    }

    private fun generateMoon() {
        MinecraftForge.EVENT_BUS.post(GCCoreEventPopulate.Pre(this.world, this.randomGenerator, chunkPos))
        this.genStandardOre(20, this.moonDirtGenerator, 0, 200)
        if (!ConfigManagerCore.disableCopperMoon) {
            this.genStandardOre(26, this.copperGen, 0, 60)
        }
        if (!ConfigManagerCore.disableTinMoon) {
            this.genStandardOre(23, this.tinGen, 0, 60)
        }
        if (!ConfigManagerCore.disableCheeseMoon) {
            this.genStandardOre(14, this.cheeseGen, 0, 85)
        }
        if (!ConfigManagerCore.disableSapphireMoon) {
            val count: Int = 3 + this.randomGenerator!!.nextInt(6)
            val sapphire: IBlockState = (GCBlocks.blockMoon)!!.getDefaultState()
                .withProperty(
                    BlockBasicMoon.BASIC_TYPE_MOON,
                    BlockBasicMoon.EnumBlockBasicMoon.ORE_SAPPHIRE
                )
            for (i: Int in 0..<count) {
                val blockpos: BlockPos = this.chunkPos.add(
                    this.randomGenerator!!.nextInt(16) + 8,
                    this.randomGenerator!!.nextInt(28) + 4,
                    this.randomGenerator!!.nextInt(16) + 8
                )

                val toReplace: IBlockState = this.world!!.getBlockState(blockpos)

                if (toReplace.getBlock() === GCBlocks.blockMoon && toReplace.getBlock()
                        .isReplaceableOreGen(toReplace, this.world, blockpos, BlockMatcher.forBlock(Blocks.STONE))
                ) {
                    this.world!!.setBlockState(blockpos, sapphire, 2)
                }
            }
        }
        MinecraftForge.EVENT_BUS.post(GCCoreEventPopulate.Post(this.world, this.randomGenerator, chunkPos))
    }
}
