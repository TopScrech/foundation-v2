package asmodeuscore.core.astronomy.dimension.world.gen.biome

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.BiomeDictionary

open class ACBiomeGen constructor(var1: BiomeProperties?) : ACBiome(var1) {
    override fun registerTypes(b: Biome?) {
        if (!ConfigManagerCore.disableBiomeTypeRegistrations) {
            BiomeDictionary.addTypes(b, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD)
        }
    }
}
