package micdoodle8.mods.galacticraft.planets.mars.world.gen

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random

open class BiomeMars constructor(properties: BiomeProperties?) : BiomeGenBaseGC((properties)!!, true) {
    override fun getSpawningChance(): Float {
        return 0.01f
    }

    override fun genTerrainBlocks(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        x: Int,
        z: Int,
        noiseVal: Double
    ) {
        this.fillerBlock = BLOCK_LOWER.getBlockState()
        this.topBlock = BLOCK_TOP.getBlockState()
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal)
    }

    companion object {
        @JvmField
        val marsFlat: Biome = BiomeGenFlatMars(
            BiomeProperties("Mars Flat").setBaseHeight(2.5f).setHeightVariation(0.4f).setRainfall(0.0f)
                .setRainDisabled()
        )

        @JvmField
        val BLOCK_TOP: BlockMetaPair = BlockMetaPair((MarsBlocks.marsBlock)!!, 5.toByte())

        @JvmField
        val BLOCK_FILL: BlockMetaPair = BlockMetaPair((MarsBlocks.marsBlock)!!, 6.toByte())

        @JvmField
        val BLOCK_LOWER: BlockMetaPair = BlockMetaPair((MarsBlocks.marsBlock)!!, 9.toByte())
    }
}
