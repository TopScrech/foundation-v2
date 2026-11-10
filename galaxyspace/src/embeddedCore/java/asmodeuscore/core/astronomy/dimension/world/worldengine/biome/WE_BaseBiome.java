package asmodeuscore.core.astronomy.dimension.world.worldengine.biome;

import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WE_BaseBiome extends WE_Biome {
	
	private static int[] colors = new int[] {0x00CC00, 0xFFFFFF, 0x00CC00};
	/**
	 * 
	 * @param 
	 *
	 */
	public WE_BaseBiome(double max, double persistance, int octaves, int height, int intquility, WE_BiomeLayer layers, IWorldGenerator... gens) {
		this(0.0D, max, persistance, octaves, height, intquility, layers, gens);	
		setAbsoluteValue();
	}
	
	public WE_BaseBiome(double min, double max, double persistance, int octaves, int height, int intquility, WE_BiomeLayer layers, IWorldGenerator... gens) {
		super(new BiomeProperties("we_basebiome"), colors);
				
		biomeMinValueOnMap      =  	min;
		biomeMaxValueOnMap      =   max;
		biomePersistence        =   persistance;
		biomeNumberOfOctaves    =   octaves;
		biomeScaleX             = 	280.0D;
		biomeScaleY             =   1.5D;
		biomeSurfaceHeight      =   height;
		biomeInterpolateQuality =   intquility;
		
		//-//
		decorateChunkGen_List.clear();		
		createChunkGen_InXZ_List.clear();
		spawnableMonsterList.clear();
		spawnableCaveCreatureList.clear();
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		
		if(layers != null)
			createChunkGen_InXZ_List.add(layers);		
		
		for(IWorldGenerator gen : gens)
			decorateChunkGen_List.add(gen);
	}
	
	public WE_BaseBiome setColors(int grass, int water, int leaves)
	{
		biomeBlockGrassColor = grass;
		biomeBlockWaterColor = water;
		biomeBlockLeavesColor = leaves;
		return this;
	}
	
	
	public WE_BaseBiome setSize(double x, double y)
	{
		biomeScaleX = x;
		biomeScaleY = y;
		return this;
	}
	
	public WE_BaseBiome addChunkGen(WE_CreateChunkGen_InXZ gen)
	{
		createChunkGen_InXZ_List.add(gen);
		return this;
	}
	
	public WE_BaseBiome setPrelinNoiseID(int id)
	{
		biomeNumNoise = id;
		return this;
	}
	
	
	public WE_BaseBiome setBaseSpawn()
	{
		spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEvolvedSpider.class, 10, 1, 2));
		spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEvolvedSkeleton.class, 10, 1, 2));
		spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEvolvedZombie.class, 10, 1, 2));
		spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEvolvedEnderman.class, 10, 1, 2));
		return this;
	}

}
