package galaxyspace.systems.SolarSystem.planets.pluto.world.gen.layers

import galaxyspace.systems.SolarSystem.planets.pluto.world.gen.layers.GenLayerPlutoBiomes
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom
import net.minecraft.world.gen.layer.GenLayerZoom

abstract class GenLayerPluto(l: Long) : GenLayer(l) {
    companion object {
        @JvmStatic
        fun createWorld(l: Long): Array<GenLayer?> {
            var biomes: GenLayer = GenLayerPlutoBiomes(l)
            biomes = GenLayerZoom(1000L, biomes)
            biomes = GenLayerZoom(1001L, biomes)
            biomes = GenLayerZoom(1002L, biomes)
            biomes = GenLayerZoom(1003L, biomes)
            biomes = GenLayerZoom(1004L, biomes)
            val genLayerVeronoiZoom: GenLayer = GenLayerVoronoiZoom(10L, biomes)

            biomes.initWorldGenSeed(l)
            genLayerVeronoiZoom.initWorldGenSeed(l)

            return ((arrayOf<GenLayer>(biomes, genLayerVeronoiZoom) as Array<GenLayer?>?))!!
        }
    }
}
