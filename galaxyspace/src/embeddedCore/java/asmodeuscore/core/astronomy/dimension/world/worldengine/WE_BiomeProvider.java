package asmodeuscore.core.astronomy.dimension.world.worldengine;

import asmodeuscore.core.utils.Utils;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace;
import net.minecraft.world.biome.Biome;

public class WE_BiomeProvider extends BiomeProviderSpace
{
    @Override
    public Biome getBiome()
    {
        return Utils.WE_Biome;
    }
}
