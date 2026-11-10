package galaxyspace.systems.SolarSystem.moons.titan.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.moons.titan.world.gen.BiomeProviderTitan
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.biome.Biome

open class BiomeProviderTitan : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
