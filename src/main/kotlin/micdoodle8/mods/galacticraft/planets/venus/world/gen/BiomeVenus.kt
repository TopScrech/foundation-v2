package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeDecorator
import net.minecraft.world.chunk.ChunkPrimer
import java.util.*

open class BiomeVenus constructor(properties: BiomeProperties?) : BiomeGenBaseGC((properties)!!, true) {
    override fun createBiomeDecorator(): BiomeDecorator {
        return BiomeDecoratorVenus()
    }

    override fun getSpawningChance(): Float {
        return 0.01f
    }

    override fun genTerrainBlocks(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        p_180622_4_: Int,
        p_180622_5_: Int,
        p_180622_6_: Double
    ) {
    }

    fun generateBiomeTerrainVenus(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        p_180628_4_: Int,
        p_180628_5_: Int,
        p_180628_6_: Double
    ) {
        val i: Int = worldIn.getSeaLevel()
        var topBlock: IBlockState? = this.topBlock
        var fillerBlock: IBlockState = this.fillerBlock
        val stoneBlock: IBlockState = (VenusBlocks.venusBlock)!!.getDefaultState()
            .withProperty(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_HARD
            )
        var j: Int = -1
        val k: Int = (p_180628_6_ / 3.0 + 3.0 + rand.nextDouble() * 0.25).toInt()
        val l: Int = p_180628_4_ and 15
        val i1: Int = p_180628_5_ and 15

        for (j1: Int in 255 downTo 0) {
            if (j1 <= rand.nextInt(5)) {
                chunkPrimerIn.setBlockState(i1, j1, l, Blocks.BEDROCK.getDefaultState())
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

    companion object {
        @JvmField
        val venusFlat: Biome = BiomeGenFlatVenus(
            BiomeProperties("Venus Flat").setRainfall(0.0f).setRainDisabled().setBaseHeight(0.5f)
                .setHeightVariation(0.4f).setTemperature(4.0f)
        )

        @JvmField
        val venusMountain: Biome = BiomeGenVenusMountain(
            BiomeProperties("Venus Mountain").setRainfall(0.0f).setRainDisabled().setBaseHeight(2.0f)
                .setHeightVariation(1.0f).setTemperature(4.0f)
        )

        @JvmField
        val venusValley: Biome = BiomeGenVenusValley(
            BiomeProperties("Venus Valley").setRainfall(0.0f).setRainDisabled().setBaseHeight(-0.4f)
                .setHeightVariation(0.2f).setTemperature(4.0f)
        )
    }
}
