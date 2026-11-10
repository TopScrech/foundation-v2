package asmodeuscore.core.astronomy.dimension.world.gen;

import asmodeuscore.core.astronomy.dimension.world.gen.biome.ACBiomeGen;
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC;
import net.minecraft.world.biome.Biome;

public abstract class ACBiome extends BiomeGenBaseGC
{
	public static Biome ACSpace = new ACBiomeGen(new BiomeProperties("Space").setBaseHeight(1.0F).setHeightVariation(0.5F).setRainfall(0.0F));
	public static Biome ACSpaceLowPlains = new ACBiomeGen(new BiomeProperties("SpaceLowPlains").setBaseHeight(0.125F).setHeightVariation(0.5F).setRainfall(0.0F));
	public static Biome ACSpaceMidPlains = new ACBiomeGen(new BiomeProperties("SpaceMidPlains").setBaseHeight(0.2F).setHeightVariation(0.5F).setRainfall(0.0F));
	public static Biome ACSpaceLowHills = new ACBiomeGen(new BiomeProperties("SpaceLowHills").setBaseHeight(0.45F).setHeightVariation(0.5F).setRainfall(0.0F));
	public static Biome ACSpaceMidHills = new ACBiomeGen(new BiomeProperties("SpaceMidHills").setBaseHeight(1.0F).setHeightVariation(0.5F).setRainfall(0.0F));
	
	public byte topMeta;
	public byte fillerMeta;
	public byte stoneMeta;
	
	public ACBiome(BiomeProperties properties)
	{
		super(properties);		
	}

	@Override
	public float getSpawningChance()
	{
		return 0.1F;
	}	

}