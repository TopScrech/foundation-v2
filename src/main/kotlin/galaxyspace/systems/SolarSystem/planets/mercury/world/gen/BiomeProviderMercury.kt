package galaxyspace.systems.SolarSystem.planets.mercury.world.gen

import galaxyspace.systems.SolarSystem.planets.mercury.world.gen.BiomeProviderMercury
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderMercury : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
