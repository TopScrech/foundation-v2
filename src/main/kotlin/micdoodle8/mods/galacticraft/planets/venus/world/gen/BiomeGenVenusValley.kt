package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraftforge.common.BiomeDictionary
import java.util.*

open class BiomeGenVenusValley constructor(properties: BiomeProperties?) : BiomeVenus(properties) {
    override fun registerTypes(b: Biome?) {
        BiomeDictionary.addTypes(
            b,
            BiomeDictionary.Type.HOT,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.DEAD,
            BiomeDictionary.Type.SANDY
        )
    }

    override fun genTerrainBlocks(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        p_180622_4_: Int,
        p_180622_5_: Int,
        p_180622_6_: Double
    ) {
        this.topBlock = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_SOFT
            )
        this.fillerBlock = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_SOFT
            )
        val stoneBlock: IBlockState = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_HARD
            )
        val gravelBlock: IBlockState = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_VOLCANIC_DEPOSIT
            )

        val i: Int = worldIn.getSeaLevel()
        var topBlock: IBlockState? = this.topBlock
        var fillerBlock: IBlockState = this.fillerBlock
        var j: Int = -1
        val k: Int = (p_180622_6_ / 3.0 + 3.0 + rand.nextDouble() * 0.25).toInt()
        val l: Int = p_180622_4_ and 15
        val i1: Int = p_180622_5_ and 15

        for (j1: Int in 255 downTo 0) {
            if (j1 <= rand.nextInt(5)) {
                chunkPrimerIn.setBlockState(i1, j1, l, Blocks.BEDROCK.defaultBlockState())
            } else {
                val iblockstate2: IBlockState = chunkPrimerIn.getBlockState(i1, j1, l)

                if (iblockstate2.getMaterial() === Material.AIR) {
                    j = -1
                } else if (iblockstate2.getBlock() === VenusBlocks.venusBlock) {
                    if (j == -1) {
                        if (k <= 0) {
                            topBlock = null
                            fillerBlock = stoneBlock
                        } else if (j1 >= i - 4 && j1 <= i + 1) {
                            topBlock = this.topBlock
                            fillerBlock = this.fillerBlock
                        }

                        j = k

                        if (j1 >= i - 1) {
                            chunkPrimerIn.setBlockState(i1, j1, l, topBlock)
                        } else if (j1 < i - 7 - k) {
                            topBlock = null
                            fillerBlock = stoneBlock
                            chunkPrimerIn.setBlockState(i1, j1, l, gravelBlock)
                        } else {
                            chunkPrimerIn.setBlockState(i1, j1, l, fillerBlock)
                        }
                    } else if (j > 0) {
                        --j
                        chunkPrimerIn.setBlockState(i1, j1, l, fillerBlock)
                    }
                }
            }
        }
    }
}
