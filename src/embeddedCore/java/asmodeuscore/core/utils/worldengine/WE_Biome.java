package asmodeuscore.core.utils.worldengine;

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXYZ;
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ;
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_GrassGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_LakeGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_SnowGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_WorldTreeGen;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WE_Biome extends Biome {
   public int id;
   public double biomeMinValueOnMap = -1.0;
   public double biomeMaxValueOnMap = 1.0;
   public double biomePersistence = 1.0;
   public double biomeScaleX = 1.0;
   public double biomeScaleY = 1.0;
   public double biomeTemerature = 0.0;
   public int biomeNumberOfOctaves = 1;
   public int biomeSurfaceHeight = 63;
   public int biomeInterpolateQuality = 16;
   public int biomeBlockGrassColor;
   public int biomeBlockWaterColor;
   public int biomeBlockLeavesColor;
   public int biomeNumNoise = 0;
   public boolean biomeAbs = false;
   public List<WE_CreateChunkGen_InXZ> createChunkGen_InXZ_List = new ArrayList<>();
   public List<WE_CreateChunkGen_InXYZ> createChunkGen_InXYZ_List = new ArrayList<>();
   public List<IWorldGenerator> decorateChunkGen_List = new ArrayList<>();

   public WE_Biome(BiomeProperties prop) {
      super(prop);
      this.setRegistryName(prop.biomeName);
   }

   public WE_Biome(BiomeProperties prop, int[] colors) {
      this(prop);
      this.spawnableCaveCreatureList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableMonsterList.clear();
      this.spawnableWaterCreatureList.clear();
      this.biomeBlockGrassColor = colors[0];
      this.biomeBlockWaterColor = colors[1];
      this.biomeBlockLeavesColor = colors[2];
      WE_BiomeLayer standardBiomeLayers = new WE_BiomeLayer();
      standardBiomeLayers.add(Blocks.DIRT.getDefaultState(), Blocks.STONE.getDefaultState(), -256, 0, -4, -2, true);
      standardBiomeLayers.add(Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), -256, 0, -256, 0, false);
      standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true);
      this.createChunkGen_InXZ_List.add(standardBiomeLayers);
      this.createChunkGen_InXZ_List.add(new WE_SnowGen());
      this.decorateChunkGen_List.add(new WE_LakeGen());
      WE_WorldTreeGen treeGen = new WE_WorldTreeGen();
      treeGen.add(
         Blocks.LOG,
         0,
         Blocks.LEAVES,
         0,
         Blocks.SAPLING,
         Blocks.VINE,
         Blocks.COCOA,
         8,
         1,
         8,
         4,
         false,
         (byte)2,
         (byte)0,
         (byte)0,
         (byte)1,
         (byte)2,
         (byte)1,
         1,
         12,
         4,
         0.618,
         0.381,
         1.0,
         1.0
      );
      this.decorateChunkGen_List.add(treeGen);
      WE_GrassGen grassGen = new WE_GrassGen();
      grassGen.add(Blocks.TALLGRASS.getStateFromMeta(1), 8, false, Blocks.GRASS.getDefaultState());
      grassGen.add(Blocks.TALLGRASS.getStateFromMeta(2), 16, false, Blocks.GRASS.getDefaultState());
      grassGen.add(Blocks.RED_FLOWER.getDefaultState(), 128, false, Blocks.GRASS.getDefaultState());
      grassGen.add(Blocks.YELLOW_FLOWER.getDefaultState(), 128, false, Blocks.GRASS.getDefaultState());
      grassGen.add(Blocks.DEADBUSH.getDefaultState(), 512, false, Blocks.GRASS.getDefaultState());
      grassGen.add(Blocks.WATERLILY.getDefaultState(), 256, false, Blocks.WATER.getDefaultState());
      this.decorateChunkGen_List.add(grassGen);
   }

   public WE_Biome(
      BiomeProperties prop,
      double minMapValue,
      double maxMapValue,
      double persistence,
      int numOctaves,
      double sx,
      double sy,
      int height,
      int interpolateQuality,
      int[] colors
   ) {
      this(prop, colors);
      this.biomeMinValueOnMap = minMapValue;
      this.biomeMaxValueOnMap = maxMapValue;
      this.biomePersistence = persistence;
      this.biomeNumberOfOctaves = numOctaves;
      this.biomeScaleX = sx;
      this.biomeScaleY = sy;
      this.biomeSurfaceHeight = height;
      this.biomeInterpolateQuality = interpolateQuality;
   }

   public WE_Biome setAbsoluteValue() {
      this.biomeAbs = true;
      return this;
   }

   public static void setBiomeMap(WE_ChunkProvider cp, double persistence, int numOctaves, double sx, double sy) {
      cp.biomemapPersistence = persistence;
      cp.biomemapNumberOfOctaves = numOctaves;
      cp.biomemapScaleX = sx;
      cp.biomemapScaleY = sy;
   }

   public static void addPrelinNoise(WE_ChunkProvider cp, PerlinNoise pn) {
      cp.biomeNoise.add(pn);
   }

   public static int addBiomeToGeneration(WE_ChunkProvider cp, WE_Biome biome) {
      if (biome.biomeMaxValueOnMap < biome.biomeMinValueOnMap) {
         double t = biome.biomeMaxValueOnMap;
         biome.biomeMaxValueOnMap = biome.biomeMinValueOnMap;
         biome.biomeMinValueOnMap = t;
      }

      cp.biomesList.add(biome);
      biome.id = cp.biomesList.size();
      if (cp.standardBiomeOnMap == null) {
         cp.standardBiomeOnMap = biome;
      }

      return biome.id;
   }

   public void decorateBiome(World world, Random rand, int x, int z) {
   }

   public static WE_Biome getBiomeAt(WE_ChunkProvider cp, long x, long z) {
      WE_Biome r = null;
      double biomeMapData = new PerlinNoise(cp.world.getSeed(), cp.biomemapPersistence, cp.biomemapNumberOfOctaves, cp.biomemapScaleX, cp.biomemapScaleY, 0)
         .genNoise2d((double)x, (double)z);

      for (int i = 0; i < cp.biomesList.size(); i++) {
         WE_Biome getBiome = cp.biomesList.get(i);
         if (getBiome.biomeAbs) {
            getBiome.biomeMinValueOnMap = -getBiome.biomeMaxValueOnMap - 0.5;
         }

         if (biomeMapData >= cp.biomesList.get(i).biomeMinValueOnMap && biomeMapData <= cp.biomesList.get(i).biomeMaxValueOnMap) {
            if (r != null) {
               if (cp.biomesList.get(i).biomeMaxValueOnMap - cp.biomesList.get(i).biomeMinValueOnMap < r.biomeMaxValueOnMap - r.biomeMinValueOnMap) {
                  r = cp.biomesList.get(i);
               }
            } else {
               r = cp.biomesList.get(i);
            }
         }
      }

      if (r == null) {
         r = cp.standardBiomeOnMap;
      }

      return r;
   }

   public static int getBiggestInterpolationQuality(WE_ChunkProvider cp) {
      int r = 0;

      for (int i = 0; i < cp.biomesList.size(); i++) {
         if (cp.biomesList.get(i).biomeInterpolateQuality > r) {
            r = cp.biomesList.get(i).biomeInterpolateQuality;
         }
      }

      return r;
   }

   protected void generateOre(World world, Random rand, BlockPos pos, WorldGenerator worldGenerator, int amountPerChunk, int minY, int maxY) {
      for (int var5x = 0; var5x < amountPerChunk; var5x++) {
         int var7x = rand.nextInt(maxY - minY) + minY;
         worldGenerator.generate(world, rand, new BlockPos(pos.getX(), var7x, pos.getZ()));
      }
   }
}
