//- By Vamig Aliev.
//- https://vk.com/win_vista.

package asmodeuscore.core.astronomy.dimension.world.worldengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import asmodeuscore.core.utils.Utils;
import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_LakeGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient;
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.common.FMLLog;


public class WE_ChunkProviderSpace extends WE_ChunkProvider {
	private final Gradient noiseGen5;
	
	//////////////////
	//- Generators -//
	//////////////////
	public List<MapGenBaseMeta> worldGenerators = new ArrayList();
	
	private BiomeDecoratorSpace decorator;
	//////////////////////
	//- Biome Map Info -//
	//////////////////////
	public int CRATER_PROB = 0;
	//private WE_WorldProviderSpace wp;
	/////
	//=//
	/////
	
	public WE_ChunkProviderSpace(WE_WorldProviderSpace wp) {
		super();

		rand = new Random(wp.getSeed());
		world = wp.world;
		worldObj = wp.world;
		this.wp = wp;
		/////
		//=//
		/////
		
		createChunkGen_List.add(new WE_TerrainGenerator());
		createChunkGen_List.add(new WE_CaveGen         ());
		createChunkGen_List.add(new WE_RavineGen       ());
		
		WE_OreGen standardOres = new WE_OreGen();
		standardOres.add(Blocks.GOLD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4,  0,  32,  16);
		standardOres.add(Blocks.IRON_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6,  0,  64,  72);
		standardOres.add(Blocks.COAL_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 32, 128, 56);
		standardOres.add(Blocks.LAPIS_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4,  0,  16, 16);
		standardOres.add(Blocks.DIAMOND_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 3,  0,  16, 16);
		standardOres.add(Blocks.EMERALD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 2,  0,  16, 24);
		decorateChunkGen_List.add(standardOres);
		//-//
		WE_LakeGen lavaLakes = new WE_LakeGen();
		lavaLakes.lakeBlock = Blocks.LAVA.getDefaultState();
		lavaLakes.iceGen      =       false;
		lavaLakes.maxY      =          32;
		decorateChunkGen_List.add(lavaLakes);
		
		
		this.noiseGen5 = new Gradient(this.rand.nextLong(), 1, 0.25F);
		//decorator = wp.getDecorator();
		wp.genSettings(this);
	}
	
	@Override
	public Chunk generateChunk(int chunkX, int chunkZ) {
		ChunkPrimer primer = new ChunkPrimer();
		
		rand.setSeed(world.getSeed() * (long) Math.pow(chunkX, 3) + (long) Math.pow(chunkZ, 2) * 9874L + 7684053L);
		try 
		{
			this.noiseGen5.setFrequency(0.01F);
			
			long chunk_X = (long)chunkX * 16L, chunk_Z = (long)chunkZ * 16L;
			
			
			WE_Biome[][] chunkBiomes = new WE_Biome[16][16];
			for(int x = 0; x < 16; x++)
				for(int z = 0; z < 16; z++)
				{
					chunkBiomes[x][z] = WE_Biome.getBiomeAt(this, chunk_X + (long)x, chunk_Z + (long)z);	
					
				}
			
			/////
			//=//
			/////
			//-//
			
			for(int i = 0; i < createChunkGen_List.size(); i++)
				createChunkGen_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, 0, 0, 0));
		
			if(this.CRATER_PROB > 0)
				this.createCraters(chunkX, chunkZ, primer);
			
			for(int x = 0; x < 16; x++)
				for(int z = 0; z < 16; z++) 
				{
					for(int i = 0; i < createChunkGen_InXZ_List                  .size(); i++)
						createChunkGen_InXZ_List                  .get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z));
					
					for(int i = 0; i < chunkBiomes[x][z].createChunkGen_InXZ_List.size(); i++)
					{
						
						chunkBiomes[x][z].createChunkGen_InXZ_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z));
					}
						//-//
					for(int y = 255; y >= 0; y--) {
						for(int i = 0; i < createChunkGen_InXYZ_List.size(); i++)
							createChunkGen_InXYZ_List                  .get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z));
						for(int i = 0; i < chunkBiomes[x][z].createChunkGen_InXYZ_List.size(); i++)
							chunkBiomes[x][z].createChunkGen_InXYZ_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z));
					}
					
					
				}
				
			
			wp.onChunkProvider(chunkX, chunkZ, primer);
			
			for (MapGenBaseMeta generator : this.worldGenerators)
	        {
	            generator.generate(this.world, chunkX, chunkZ, primer);
	        }
			/////
			//=//
			/////
		}
		catch (Exception e)
        {
            FMLLog.warning("Error caught in planetary worldgen at coords " + chunkX + "," + chunkZ + ".");
            e.printStackTrace();
        }
		
		Chunk chunk = new Chunk(this.world, primer, chunkX, chunkZ);
		/*byte[] arraybiome = new byte[256];
		Arrays.fill(arraybiome, (byte) Biome.getIdForBiome(Utils.WE_Biome));
		chunk.setBiomeArray(arraybiome);
		*/
		chunk.generateSkylightMap();
        return chunk;
	}
	
	@Override
	public void populate(int chunkX, int chunkZ) {
		super.populate(chunkX, chunkZ);
	}

	/*
	public void decoratePlanet(World world, Random rand, int x, int z)
	{
		if(getBiomeGenerator() != null)
			this.getBiomeGenerator().decorate(world, rand, x, z);
	}
	
	public void setBiomeGenerator(BiomeDecoratorSpace decorator)
	{
		this.decorator = decorator; 
	}
	
	protected BiomeDecoratorSpace getBiomeGenerator()
	{
		return this.decorator;		
	}*/
	
	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return super.getPossibleCreatures(creatureType, pos);
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		wp.recreateStructures(chunkIn, x, z);
	}
	
	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position,
			boolean findUnexplored) {
		return null;
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}
	
	public void createCraters(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        this.noiseGen5.setFrequency(0.015F);
        for (int cx = chunkX - 2; cx <= chunkX + 2; cx++)
        {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++)
            {
                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen5.getNoise(cx * 16 + x, cz * 16 + z) / this.CRATER_PROB)
                        {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE + 15;
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer);
                        }
                    }
                }
            }
        }
    }
	
	public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, ChunkPrimer primer)
    {
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size)
                {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6;
                    yDev = 5 - yDev;
                    int helper = 0;
                    for (int y = 180; y > 50; y--)
                    {
                        if (!primer.getBlockState(x, y, z).getMaterial().isLiquid() && Blocks.AIR != primer.getBlockState(x, y, z).getBlock() && helper <= yDev)
                        {
                            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
//                            chunkArray[this.getIndex(x, y, z)] = Blocks.AIR;
//                            metaArray[this.getIndex(x, y, z)] = 0;
                            helper++;
                        }

                        if (helper > yDev)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }
	
	private double randFromPoint(int x, int z)
    {
        int n;
        n = x + z * 57;
        n = n << 13 ^ n;
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
    }
	
	

}