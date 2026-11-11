package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.biome.BiomeDecorator
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random

open class BiomeMoon constructor(properties: BiomeProperties?) : BiomeGenBaseGC((properties)!!, true) {
    override fun createBiomeDecorator(): BiomeDecorator {
        return getModdedBiomeDecorator(BiomeDecoratorMoon())
    }

    override fun getSpawningChance(): Float {
        return 0.1f
    }

    override fun genTerrainBlocks(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        x: Int,
        z: Int,
        noiseVal: Double
    ) {
        this.fillerBlock = ChunkProviderMoon.Companion.BLOCK_LOWER
        this.topBlock = ChunkProviderMoon.Companion.BLOCK_TOP
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal)
    }

    companion object {
        @JvmField
        val moonFlat: Biome =
            BiomeFlatMoon(BiomeProperties("Moon").setBaseHeight(1.5f).setHeightVariation(0.4f).setRainfall(0.0f))
    }
}
