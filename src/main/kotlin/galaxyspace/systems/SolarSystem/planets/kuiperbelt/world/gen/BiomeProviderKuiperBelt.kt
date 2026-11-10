package galaxyspace.systems.SolarSystem.planets.kuiperbelt.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.world.gen.BiomeProviderKuiperBelt
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.biome.Biome

open class BiomeProviderKuiperBelt : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
