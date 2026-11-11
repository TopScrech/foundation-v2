package galaxyspace.systems.SolarSystem.moons.callisto.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.moons.callisto.world.gen.BiomeProviderCallisto
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderCallisto : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
