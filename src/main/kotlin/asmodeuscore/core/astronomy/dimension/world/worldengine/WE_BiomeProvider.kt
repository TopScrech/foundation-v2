package asmodeuscore.core.astronomy.dimension.world.worldengine

import asmodeuscore.core.utils.Utils
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class WE_BiomeProvider : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return Utils.WE_Biome
    }
}
