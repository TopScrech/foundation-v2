package asmodeuscore.core.astronomy.dimension.world.gen;

 

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase;
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient;
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.fml.common.FMLLog;



 

public abstract class ChunkProviderSpaceLakes extends ChunkProviderBase
{	
	public enum GenType
	{
		GC,
		VANILLA,
		END
	}
	
    protected static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 256;
    protected static final int CHUNK_SIZE_Z = 16;
    
    private final double TERRAIN_HEIGHT_MOD = this.getHeightModifier();
    private final double SMALL_FEATURE_HEIGHT_MOD = this.getSmallFeatureHeightModifier();
    private final double MOUNTAIN_HEIGHT_MOD = this.getMountainHeightModifier();
    private final double VALLEY_HEIGHT_MOD = this.getValleyHeightModifier();
    private final int CRATER_PROB = this.getCraterProbability();
    
    private final int MID_HEIGHT = this.getTerrainLevel();
    private static final double MAIN_FEATURE_FILTER_MOD = 4;
    private static final double LARGE_FEATURE_FILTER_MOD = 8;
    private static final double SMALL_FEATURE_FILTER_MOD = 8;
    
	protected Random rand;
	private BiomeCache myBiomeCache;
	private NoiseGeneratorOctaves noiseGen4;
	public NoiseGeneratorOctaves noiseGen5;
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves mobSpawnerNoise;
	protected World worldObj;
	private double[] stoneNoise;
	private Biome[] biomesForGeneration = this.getBiomesForGeneration();
	double[] noise3;
	double[] noise1;
	double[] noise2;
	double[] noise5;
	double[] noise6;
	float[] squareTable;
	private NoiseGeneratorOctaves field_147431_j;
	private NoiseGeneratorOctaves field_147432_k;
	private NoiseGeneratorOctaves field_147429_l;
	private NoiseGeneratorPerlin field_147430_m;
	private double[] terrainCalcs;
	private float[] parabolicField;
	double[] field_147427_d;
	double[] field_147428_e;
	double[] field_147425_f;
	double[] field_147426_g;
	int[][] field_73219_j = new int[32][32];
	private final Gradient noiseGen8;
	
    private final Gradient gcnoiseGen1;
    private final Gradient gcnoiseGen2;
    private final Gradient gcnoiseGen3;
    private final Gradient gcnoiseGen4;
    private final Gradient gcnoiseGen5;
    private final Gradient gcnoiseGen6;
    private final Gradient gcnoiseGen7;

	public static List<GenBlocks> genBlocks = new ArrayList();
	
	private List<MapGenBaseMeta> worldGenerators;

	public ChunkProviderSpaceLakes(World world, long seed, boolean flag)
	{
		super();
		this.stoneNoise = new double[256];
		this.worldObj = world;
		this.rand = new Random(seed);
		this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen8 = new Gradient(this.rand.nextLong(), 2, 0.25F);
		this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
		this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
		this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
		this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
		this.terrainCalcs = new double[825];
		this.parabolicField = new float[25];

		for (int j = -2; j <= 2; j++)
		{
			for (int k = -2; k <= 2; k++)
			{
				float f = 10.0F / MathHelper.sqrt(j * j + k * k + 0.2F);
				this.parabolicField[j + 2 + (k + 2) * 5] = f;
			}
		}
		
		this.gcnoiseGen1 = new Gradient(this.rand.nextLong(), 4, 0.25F);
        this.gcnoiseGen2 = new Gradient(this.rand.nextLong(), 4, 0.25F);
        this.gcnoiseGen3 = new Gradient(this.rand.nextLong(), 4, 0.25F);
        this.gcnoiseGen4 = new Gradient(this.rand.nextLong(), 2, 0.25F);
        this.gcnoiseGen5 = new Gradient(this.rand.nextLong(), 1, 0.25F);
        this.gcnoiseGen6 = new Gradient(this.rand.nextLong(), 1, 0.25F);
        this.gcnoiseGen7 = new Gradient(this.rand.nextLong(), 1, 0.25F);
       
       // this.setBlocks(GSBiomeGenBase.GSSpace,	Blocks.grass, (byte) 0, Blocks.dirt, (byte) 0);
        
    	
	}

	@Override
	public Chunk generateChunk(int x, int z)
	{
		ChunkPrimer primer = new ChunkPrimer();
		try {
			this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
			this.generateTerrain(x, z, primer);
			if(this.getCraterProbability() > 0)	this.createCraters(x, z, primer);
					
			this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
			this.replaceBlocksForBiome(x, z, primer, this.biomesForGeneration);
			
			if (this.worldGenerators == null)
	        {
	            this.worldGenerators = this.getWorldGenerators();
	        }
	
	        for (MapGenBaseMeta generator : this.worldGenerators)
	        {
	            generator.generate(this.worldObj, x, z, primer);
	        }
	        
	        this.onChunkProvider(x, z, primer);
		}
		catch (Exception e)
        {
            FMLLog.warning("Error caught in planetary worldgen at coords " + x + "," + z + ".");
            e.printStackTrace();
        }
		
		Chunk chunk = new Chunk(this.worldObj, primer, x, z);
		byte[] chunkBiomes = chunk.getBiomeArray();
		
		for (int i = 0; i < chunkBiomes.length; i++)
		{
			chunkBiomes[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
		}
		chunk.generateSkylightMap();
		return chunk;
	}
	
	public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer)
	{
		int seaLevel = this.getWaterLevel();
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		
		if(this.getGenType() != GenType.GC) 
		{
			this.makeLandPerBiome2(chunkX * 4, 0, chunkZ * 4);	

			for (int i = 0; i < 4; ++i) {
				int j = i * 5;
				int k = (i + 1) * 5;

				for (int l = 0; l < 4; ++l) {
					int i1 = (j + l) * 33;
					int j1 = (j + l + 1) * 33;
					int k1 = (k + l) * 33;
					int l1 = (k + l + 1) * 33;

					for (int i2 = 0; i2 < 32; ++i2) {
						double d0 = 0.125D;
						double d1 = this.terrainCalcs[i1 + i2];
						double d2 = this.terrainCalcs[j1 + i2];
						double d3 = this.terrainCalcs[k1 + i2];
						double d4 = this.terrainCalcs[l1 + i2];
						double d5 = (this.terrainCalcs[i1 + i2 + 1] - d1) * 0.125D;
						double d6 = (this.terrainCalcs[j1 + i2 + 1] - d2) * 0.125D;
						double d7 = (this.terrainCalcs[k1 + i2 + 1] - d3) * 0.125D;
						double d8 = (this.terrainCalcs[l1 + i2 + 1] - d4) * 0.125D;

						for (int j2 = 0; j2 < 8; ++j2) {
							double d9 = 0.25D;
							double d10 = d1;
							double d11 = d2;
							double d12 = (d3 - d1) * 0.25D;
							double d13 = (d4 - d2) * 0.25D;

							for (int k2 = 0; k2 < 4; ++k2) {
								double d14 = 0.25D;
								double d16 = (d11 - d10) * 0.25D;
								double lvt_45_1_ = d10 - d16;

								for (int l2 = 0; l2 < 4; ++l2) 
								{
									if ((lvt_45_1_ += d16) > 0.0D) 
									{
										primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.getStoneBlock());
							
									}
									else if (i2 * 8 + j2 < seaLevel && this.canGenerateWaterBlock())
									{
										primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.getWaterBlock());
									}									
								}
								d10 += d12;
								d11 += d13;
							}
							
							d1 += d5;
							d2 += d6;
							d3 += d7;
							d4 += d8;
						}
					}
				}
			}
		}		
		else this.makeGCland(chunkX, chunkZ, primer);
	}	
	
	public void makeGCland(int chunkX, int chunkZ, ChunkPrimer primer)
    {
    	
        this.gcnoiseGen1.setFrequency(0.015F);
        this.gcnoiseGen2.setFrequency(0.01F);
        this.gcnoiseGen3.setFrequency(0.01F);
        this.gcnoiseGen4.setFrequency(0.01F);
        this.gcnoiseGen5.setFrequency(0.01F);
        this.gcnoiseGen6.setFrequency(0.001F);
        this.gcnoiseGen7.setFrequency(0.005F);
        
        for (int x = 0; x < this.CHUNK_SIZE_X; x++)
        {
            for (int z = 0; z < this.CHUNK_SIZE_Z; z++)
            {
            	Biome biomes = this.worldObj.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
            	
                final double baseHeight = this.gcnoiseGen1.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.TERRAIN_HEIGHT_MOD + biomes.getBaseHeight();
                final double smallHillHeight = this.gcnoiseGen2.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.SMALL_FEATURE_HEIGHT_MOD;
                double mountainHeight = Math.abs(this.gcnoiseGen3.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
                double valleyHeight = Math.abs(this.gcnoiseGen4.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
                final double featureFilter = this.gcnoiseGen5.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.MAIN_FEATURE_FILTER_MOD;
                final double largeFilter = this.gcnoiseGen6.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.LARGE_FEATURE_FILTER_MOD;
                final double smallFilter = this.gcnoiseGen7.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.SMALL_FEATURE_FILTER_MOD - 0.5;
                mountainHeight = this.lerp(smallHillHeight, mountainHeight * this.MOUNTAIN_HEIGHT_MOD, this.fade(this.clamp(mountainHeight * 2, 0, 1)));
                valleyHeight = this.lerp(smallHillHeight, valleyHeight * this.VALLEY_HEIGHT_MOD - this.VALLEY_HEIGHT_MOD + 9, this.fade(this.clamp((valleyHeight + 2) * 4, 0, 1)));

                double yDev = this.lerp(valleyHeight, mountainHeight, this.fade(largeFilter));
                yDev = this.lerp(smallHillHeight, yDev, smallFilter);
                yDev = this.lerp(baseHeight, yDev, featureFilter);

                
               
                for (int y = 0; y < this.CHUNK_SIZE_Y; y++)
                {
                    if (y < this.MID_HEIGHT + yDev)
                    { 
                    	if(this.enableBiomeGenBaseBlock() && !genBlocks.isEmpty())
                    	{
                    		//idArray[this.getIndex(x, y, z)] = blockStone.get(biome.indexOf(biomes));
     		               	//metaArray[this.getIndex(x, y, z)] = metaStone.get(biome.indexOf(biomes)); 
                    		
                    		int index = -1; //array index start with 0;
                    		for(GenBlocks genBlock : genBlocks)
                    		{
                    			index++;
                    			if(this.worldObj.provider == genBlock.getWorldProvider() && biomes == genBlock.getBiome())
                    			{
                    				break;
                    			}
                    		}
                    		
                    		if(genBlocks.get(index).getStoneBlock() != null)
                    			primer.setBlockState(x, y, z, genBlocks.get(index).getStoneBlock());
                    		else
                    			primer.setBlockState(x, y, z, this.getStoneBlock());
                    		
                    		//idArray[this.getIndex(x, y, z)] = genBlocks.get(index).getStoneBlock().getBlock();
                    		//metaArray[this.getIndex(x, y, z)] = genBlocks.get(index).getStoneBlock().getMetadata();
                    	}
                    	else
                    	{
                    		primer.setBlockState(x, y, z, this.getStoneBlock());
//                          
                    		//idArray[this.getIndex(x, y, z)] = this.getStoneBlock().getBlock();
                    		//metaArray[this.getIndex(x, y, z)] = this.getStoneBlock().getMetadata();  
                    	}
		                                  	
                    }
                    
                  
                    
                }
            }
        }
    }
	
	private void makeLandPerBiome2(int x, int zero, int z)
	{
		this.field_147426_g = this.noiseGen6.generateNoiseOctaves(this.field_147426_g, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
		this.field_147427_d = this.field_147429_l.generateNoiseOctaves(this.field_147427_d, x, zero, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		this.field_147428_e = this.field_147431_j.generateNoiseOctaves(this.field_147428_e, x, zero, z, 5, 33, 5, 684.41200000000003D, 684.41200000000003D, 684.41200000000003D);
		this.field_147425_f = this.field_147432_k.generateNoiseOctaves(this.field_147425_f, x, zero, z, 5, 33, 5, 684.41200000000003D, 684.41200000000003D, 684.41200000000003D);
		int terrainIndex = 0;
		int noiseIndex = 0;

		for (int ax = 0; ax < 5; ax++)
		{
			for (int az = 0; az < 5; az++)
			{
				float totalVariation = 0.0F;
				float totalHeight = 0.0F;
				float totalFactor = 0.0F;
				byte two = 2;
				Biome biomegenbase = this.biomesForGeneration[ax + 2 + (az + 2) * 10];

				for (int ox = -two; ox <= two; ox++)
				{
					for (int oz = -two; oz <= two; oz++)
					{
						Biome biomegenbase1 = this.biomesForGeneration[ax + ox + 2 + (az + oz + 2) * 10];
						float rootHeight = biomegenbase1.getBaseHeight();
						float heightVariation = biomegenbase1.getHeightVariation();
						float heightFactor = this.parabolicField[ox + 2 + (oz + 2) * 5] / (rootHeight + 2.0F);

						if (biomegenbase1.getBaseHeight() > biomegenbase.getBaseHeight())
						{
							heightFactor /= 2.0F;
						}
						totalVariation += heightVariation * heightFactor;
						totalHeight += rootHeight * heightFactor;
						totalFactor += heightFactor;
					}
				}
				totalVariation /= totalFactor;
				totalHeight /= totalFactor;
				totalVariation = totalVariation * 0.9F + 0.1F;
				totalHeight = (totalHeight * 4.0F - 1.0F) / 8.0F;
				double terrainNoise = this.field_147426_g[noiseIndex] / 8000.0D;

				if (terrainNoise < 0.0D)
				{
					terrainNoise = -terrainNoise * 0.3D;
				}

				terrainNoise = terrainNoise * 3.0D - 2.0D;

				if (terrainNoise < 0.0D)
				{
					terrainNoise /= 2.0D;

					if (terrainNoise < -1.0D)
					{
						terrainNoise = -1.0D;
					}
					terrainNoise /= 1.4D;
					terrainNoise /= 2.0D;
				}
				else
				{
					if (terrainNoise > 1.0D)
					{
						terrainNoise = 1.0D;
					}
					terrainNoise /= 8.0D;
				}
				noiseIndex++;
				double heightCalc = totalHeight;
				double variationCalc = totalVariation * this.getHeightModifier() / 10D;
				heightCalc += terrainNoise * 0.2D;
				heightCalc = heightCalc * 8.5D / 8.0D;
				double d5 = 8.5D + heightCalc * 4.0D;

				for (int ay = 0; ay < 33; ay++)
				{
					double d6 = (ay - d5) * 12.0D * 128.0D / 256.0D / variationCalc;

					if (d6 < 0.0D)
					{
						d6 *= 4.0D;
					}
					double d7 = this.field_147428_e[terrainIndex] / 512.0D;
					double d8 = this.field_147425_f[terrainIndex] / 512.0D;
					double d9 = (this.field_147427_d[terrainIndex] / 10.0D + 1.0D) / 2.0D;
					double terrainCalc = MathHelper.clampedLerp(d7, d8, d9) - d6;

					if (ay > 29)
					{
						double d11 = (ay - 29) / 3.0F;
						terrainCalc = terrainCalc * (1.0D - d11) + -10.0D * d11;
					}
					this.terrainCalcs[terrainIndex] = terrainCalc;
					terrainIndex++;
				}
			}
		}
	}

	public void replaceBlocksForBiome(int x, int z, ChunkPrimer primer, Biome[] par4ArrayOfBiomeGenBase)
    {
        final int var5 = 20;
        final float var6 = 0.03125F;
        this.noiseGen8.setFrequency(var6 * 2);
        if(this.getGenType() != GenType.END)
        {
	        for (int var8 = 0; var8 < 16; ++var8)
	        {
	            for (int var9 = 0; var9 < 16; ++var9)
	            {
	           	
	            	ACBiome biomegenbase = (ACBiome) par4ArrayOfBiomeGenBase[var8 + var9 * 16];
	            
	                final int var12 = (int) (this.noiseGen8.getNoise(x * 16 + var8, z * 16 + var9) / 3.0D + getDirtLayerSize() + this.rand.nextDouble() * 0.25D);
	                int var13 = -1;
	           
	                IBlockState grassblock = this.getGrassBlock();
	                IBlockState dirtblock = this.getDirtBlock();
	                IBlockState stoneblock = this.getStoneBlock();
	                
	               /* Block var14;
	                byte var14m;
	                Block var15;
	                byte var15m;*/
	                /*
	                Block stone;
	                byte mStone;*/
	               
	                if(this.enableBiomeGenBaseBlock() && !genBlocks.isEmpty())
	                {
	                	//GalaxySpace.debug("Chunk: " + biome.indexOf(par4ArrayOfBiomeGenBase[var8 + var9 * 16]) + "|" + block.get(biome.indexOf(par4ArrayOfBiomeGenBase[var8 + var9 * 16]))+ "|" + block.size() + "|" + biome.indexOf(par4ArrayOfBiomeGenBase[var8 + var9 * 16]));
	                   
	                	int index = -1;
                		for(GenBlocks genBlock : genBlocks)
                		{
                			index++;
                			if(this.worldObj.provider == genBlock.getWorldProvider() && par4ArrayOfBiomeGenBase[var8 + var9 * 16] == genBlock.getBiome())
                			{
                				break;
                			}
                		}
                		//GalaxySpace.debug(index + " | " + genBlocks.get(index).getGrassBlock().getBlock());
	                	grassblock = genBlocks.get(index).getGrassBlock();	                	
	                	dirtblock = genBlocks.get(index).getDirtBlock();
	                	
	                	if(genBlocks.get(index).getStoneBlock() != null) 
	                		stoneblock = genBlocks.get(index).getStoneBlock();
	                		
	                }
	                
	                for (int var16 = 256 - 1; var16 >= 0; --var16)
	                {
	                    final int index = this.getIndex(var8, var16, var9);
	                    
	                    if(this.canGenerateIceBlock())
	                    {
	                    	if(var16 == 5 || var16 == 6 + this.rand.nextInt(3))
	                    	{
	                    		primer.setBlockState(var8, var16, var9, this.getIceBlock());
	                    	}
	                    }
	                    
	                    if (var16 <= 0 + this.rand.nextInt(5))
	                    {
	                    	primer.setBlockState(var8, var16, var9, Blocks.BEDROCK.getDefaultState());
	                    }	                   
	                    else
	                    {
	                    	IBlockState var18 = primer.getBlockState(var8, var16, var9);
	                        
	                        if (Blocks.AIR == var18)
	                        {
	                            var13 = -1;
	                        }
	                        else if (var18 == stoneblock)
	                        {
	                          	    
	                            if (var13 == -1)
	                            {
	                                if (var12 <= 0)
	                                {
	                                    grassblock = Blocks.AIR.getDefaultState();
	                                            						
	                                }
	                                else if (var16 >= var5 - -16 && var16 <= var5 + 1)
	                                {
	                                	  if(this.enableBiomeGenBaseBlock() && !genBlocks.isEmpty())
	                                      {
	                                		  grassblock = genBlocks.get(index).getGrassBlock();	                  	                
	                                		  dirtblock = genBlocks.get(index).getDirtBlock();                                    
	                                      }
	                                	  else
	                                	  {
	                                		  grassblock = this.getGrassBlock();
	                                		  dirtblock = this.getDirtBlock(); 
	                                	  }
	                                	  
	                                	     
	                                }
	                                var13 = var12;
	
	                              /*  if (var16 >= this.getWaterLevel() - 1)
	                                {
	                                	*/
	                                primer.setBlockState(var8, var16, var9, grassblock);

	                                /*}
	                                else
	                                {
	                                    arrayOfIDs[index] = var15;
	                                    arrayOfMeta[index] = var15m;
	                                }*/
	                            }
	                            else if (var13 > 0)
	                            {
	                                --var13;	                                
	                                primer.setBlockState(var8, var16, var9, dirtblock);
	                            }
	                        }
	                        else if (var16 < this.getWaterLevel() && this.getGenType() == GenType.GC && this.canGenerateWaterBlock())
	                        {       
	                        	primer.setBlockState(var8, var16, var9, this.getWaterBlock());
	                        }	                       
	                    }
	                }
	            }
	        }
        }

    }

	public void createCraters(int chunkX, int chunkZ, ChunkPrimer primer)
	{
		this.gcnoiseGen5.setFrequency(0.015F);
		for (int cx = chunkX - 2; cx <= chunkX + 2; cx++)
		{
			for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++)
			{
				for (int x = 0; x < this.CHUNK_SIZE_X; x++)
				{
					for (int z = 0; z < this.CHUNK_SIZE_Z; z++)
					{
						if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.gcnoiseGen5.getNoise(cx * 16 + x, cz * 16 + z) / this.CRATER_PROB)
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
		for (int x = 0; x < this.CHUNK_SIZE_X; x++)
		{
			for (int z = 0; z < this.CHUNK_SIZE_Z; z++)
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
					for (int y = 127; y > 0; y--)
					{
						if (Blocks.AIR != primer.getBlockState(x, y, z).getBlock() && helper <= yDev)
						{
							
							
							primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());	
							this.getCraterAdditions(primer, x, y, z);
							
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
	    
	protected int getIndex(int x, int y, int z)
    {
        return (x * 16 + z) * 256 + y;
    }
	
	@Override
	public void populate(int x, int z)
	{
		BlockFalling.fallInstantly = true;
		int posX = x << 4;
		int posZ = z << 4;
		this.worldObj.getBiome(new BlockPos(posX + 16, 0, posZ + 16));
		this.rand.setSeed(this.worldObj.getSeed());
		long var7 = this.rand.nextLong() / 2L * 2L + 1L;
		long var9 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed(x * var7 + z * var9 ^ this.worldObj.getSeed());
		
		
		this.decoratePlanet(this.worldObj, this.rand, posX, posZ);
		this.onPopulate(x, z);
		BlockFalling.fallInstantly = false;
	}

	public void decoratePlanet(World world, Random rand, int x, int z)
	{
		this.getBiomeGenerator().decorate(world, rand, x, z);
	}
/*
    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        if (creatureType == EnumCreatureType.MONSTER)
        {
            final List monsters = new ArrayList();

            for (SpawnListEntry monster : this.getMonsters())
            {
                monsters.add(monster);
            }

            return monsters;
        }
        else if (creatureType == EnumCreatureType.CREATURE)
        {
            final List creatures = new ArrayList();

            for (SpawnListEntry creature : this.getCreatures())
            {
                creatures.add(creature);
            }

            return creatures;
        }
        else if (creatureType == EnumCreatureType.WATER_CREATURE)
        {
            final List watercreatures = new ArrayList();

            for (SpawnListEntry watercreature : this.getWaterCreatures())
            {
                watercreatures.add(watercreature);
            }

            return watercreatures;
        }
        else
        {
            return null;
        }
    }*/
	
	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		Biome biomegenbase = this.worldObj.getBiome(pos);
		return biomegenbase.getSpawnableList(creatureType);
	}
	
/*
    public static void setBlocks(BiomeGenBase biomes, Block blocks, byte metas, Block blocks2, byte metas2, Block blocks3, byte metas3)
    {
    	biome.add(biomes);
    	block.add(blocks);
    	meta.add(metas);
    	block2.add(blocks2);
    	meta2.add(metas2);
    	blockStone.add(blocks3);
    	metaStone.add(metas3);
    };
    */
    public static void setBlocks(GenBlocks array)
    {
    	genBlocks.add(array);
    }
	
    protected double lerp(double d1, double d2, double t)
    {
        if (t < 0.0)
        {
            return d1;
        }
        else if (t > 1.0)
        {
            return d2;
        }
        else
        {
            return d1 + (d2 - d1) * t;
        }
    }

    protected double fade(double n)
    {
        return n * n * n * (n * (n * 6 - 15) + 10);
    }

    protected double clamp(double x, double min, double max)
    {
        if (x < min)
        {
            return min;
        }
        if (x > max)
        {
            return max;
        }
        return x;
    }
    /**
     * Do not return null
     *
     * @return The biome generator for this world, handles ore, flower, etc
     * generation. See GCBiomeDecoratorBase.
     */
    protected abstract BiomeDecoratorSpace getBiomeGenerator();
    
    /**
     * Do not return null, have at least one biome for generation
     *
     * @return Biome instance for generation
     */
    protected abstract Biome[] getBiomesForGeneration();
    
    public abstract void onChunkProvider(int cX, int cZ, ChunkPrimer primer);

    public abstract void onPopulate(int x, int z);
    
   
    /**
     * List of all world generators to use. Caves, ravines, structures, etc.
     * <p/>
     * Return an empty list for no world generators. Do not return null.
     *
     * @return
     */
    protected abstract List<MapGenBaseMeta> getWorldGenerators();
    
    /**
     * @return Base height modifier
     */
    public abstract double getHeightModifier();
    
    /**
     * @return Height modifier for small hills
     */
    public abstract double getSmallFeatureHeightModifier();

    /**
     * @return Height modifier for mountains
     */
    public abstract double getMountainHeightModifier();

    /**
     * @return Height modifier for valleys
     */
    public abstract double getValleyHeightModifier();

    /**
     * @return Base height modifier
     */
    public abstract int getWaterLevel();
    
    /**
     * @return Generate Water Block on sealevel.
     */
    public abstract boolean canGenerateWaterBlock();
    
    /**
     * @return Generate Ice Block on bedrocklevel.
     */
    public abstract boolean canGenerateIceBlock();
    
    /**
     * @return Probability that craters will be generated
     */
    public abstract int getCraterProbability();
    
    /**
     * The water block to be generated.
     *
     * @return BlockMetaPair instance containing ID and metadata for water
     * block.
     */
    protected abstract IBlockState getWaterBlock();
    protected abstract IBlockState getGrassBlock();
    protected abstract IBlockState getDirtBlock();
    protected abstract IBlockState getStoneBlock();
    
    protected abstract boolean enableBiomeGenBaseBlock();
    protected abstract GenType getGenType();
    
    public int getCraterSize()
    {
    	return 0;
    }
   
    public int getTerrainLevel()
    {
    	return this.getWaterLevel();
    }
    
    public double getDirtLayerSize()
    {
    	return 3.0D;
    }
    
    protected IBlockState getIceBlock()
    {
    	return Blocks.PACKED_ICE.getDefaultState();
    }
    
    public void getCraterAdditions(ChunkPrimer primer, int x, int y, int z) {}
    
    public class GenBlocks {
    	
    	private WorldProvider provider;
    	private Biome biome;
    	private IBlockState grass;
    	private IBlockState dirt;
    	private IBlockState customstone = null;
    	
    	public GenBlocks(WorldProvider provider, Biome biome, IBlockState blockGrass, IBlockState blockDirt)
    	{
    		this.provider = provider;	
    		this.biome = biome;
    		this.grass = blockGrass;
    		this.dirt = blockDirt;
    	}
    	
    	public GenBlocks setCustomStone(IBlockState stone) {
    		this.customstone = stone;
    		return this;
    	}
    	
    	public WorldProvider getWorldProvider()
    	{
    		return this.provider;
    	}

    	public Biome getBiome()
    	{
    		return this.biome;
    	}
    	
    	public IBlockState getGrassBlock()
    	{
    		return this.grass;
    	}
    	
    	public IBlockState getDirtBlock()
    	{
    		return this.dirt;
    	} 
    	
    	public IBlockState getStoneBlock()
    	{
    		return this.customstone;
    	}    	
    }
}