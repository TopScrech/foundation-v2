package galaxyspace.systems.SolarSystem.moons.europa.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.BiomeProviderEuropa
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.biome.Biome

open class BiomeProviderEuropa : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
