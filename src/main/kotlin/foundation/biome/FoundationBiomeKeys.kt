package foundation.biome

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome

/** WorldEngine's game-facing biomes are loaded from data, independently of its terrain descriptors */
object FoundationBiomeKeys {
    @JvmField val WORLD_ENGINE: ResourceKey<Biome> = key("world_engine")
    @JvmField val WORLD_ENGINE_2: ResourceKey<Biome> = key("world_engine_2")
    private fun key(path: String): ResourceKey<Biome> =
        ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("asmodeuscore", path))
}
