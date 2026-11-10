package galaxyspace.systems.SolarSystem.moons.io.world.gen

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.moons.io.world.gen.BiomeProviderIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.layers.GenLayerIoBiomes
import net.minecraft.world.World
import net.minecraft.world.WorldType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom
import net.minecraft.world.gen.layer.GenLayerZoom

open class BiomeProviderIo constructor(world: World) : BiomeProvider(world.getWorldInfo()) {
    init {
        allowedBiomes.clear()
        allowedBiomes = (SolarSystemBodies.ioJupiter)!!.getBiomes()
    }

    override fun getModdedBiomeGenerators(
        worldType: WorldType,
        seed: Long,
        original: Array<GenLayer?>
    ): Array<GenLayer?> {
        var biomes: GenLayer = GenLayerIoBiomes(seed)
        biomes = GenLayerZoom(1000L, biomes)
        biomes = GenLayerZoom(1001L, biomes)
        biomes = GenLayerZoom(1002L, biomes)
        biomes = GenLayerZoom(1003L, biomes)
        biomes = GenLayerZoom(1004L, biomes)
        val genLayerVeronoiZoom: GenLayer = GenLayerVoronoiZoom(10L, biomes)

        biomes.initWorldGenSeed(seed)
        genLayerVeronoiZoom.initWorldGenSeed(seed)

        return ((arrayOf<GenLayer>(biomes, genLayerVeronoiZoom) as Array<GenLayer?>?))!!
    }
}
