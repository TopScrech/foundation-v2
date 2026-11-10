package asmodeuscore.core.utils.worldengine;

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen;
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXYZ;
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_LakeGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Pre;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WE_ChunkProvider implements IChunkGenerator {
   public World world;
   @Deprecated
   public World worldObj;
   public Random rand;
   public List<WE_CreateChunkGen> createChunkGen_List = new ArrayList<>();
   public List<WE_CreateChunkGen_InXZ> createChunkGen_InXZ_List = new ArrayList<>();
   public List<WE_CreateChunkGen_InXYZ> createChunkGen_InXYZ_List = new ArrayList<>();
   public List<IWorldGenerator> decorateChunkGen_List = new ArrayList<>();
   public List<WE_Biome> biomesList = new ArrayList<>();
   public List<PerlinNoise> biomeNoise = new ArrayList<>();
   protected WE_Biome standardBiomeOnMap;
   public double biomemapPersistence = 1.0;
   public double biomemapScaleX = 1.0;
   public double biomemapScaleY = 1.0;
   public int biomemapNumberOfOctaves = 1;
   protected WE_WorldProvider wp;

   public WE_ChunkProvider() {
   }

   public WE_ChunkProvider(WE_WorldProvider wp) {
      this.rand = new Random(wp.getSeed());
      this.world = wp.world;
      this.worldObj = wp.world;
      this.wp = wp;
      this.createChunkGen_List.add(new WE_TerrainGenerator());
      this.createChunkGen_List.add(new WE_CaveGen());
      this.createChunkGen_List.add(new WE_RavineGen());
      WE_OreGen standardOres = new WE_OreGen();
      standardOres.add(Blocks.GOLD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4, 0, 32, 16);
      standardOres.add(Blocks.IRON_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 0, 64, 72);
      standardOres.add(Blocks.COAL_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 32, 128, 56);
      standardOres.add(Blocks.LAPIS_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4, 0, 16, 16);
      standardOres.add(Blocks.DIAMOND_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 3, 0, 16, 16);
      standardOres.add(Blocks.EMERALD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 2, 0, 16, 24);
      this.decorateChunkGen_List.add(standardOres);
      WE_LakeGen lavaLakes = new WE_LakeGen();
      lavaLakes.lakeBlock = Blocks.LAVA.getDefaultState();
      lavaLakes.iceGen = false;
      lavaLakes.maxY = 32;
      this.decorateChunkGen_List.add(lavaLakes);
      wp.genSettings(this);
   }

   public Chunk generateChunk(int chunkX, int chunkZ) {
      ChunkPrimer primer = new ChunkPrimer();
      this.rand.setSeed(this.world.getSeed() * (long)Math.pow((double)chunkX, 3.0) + (long)Math.pow((double)chunkZ, 2.0) * 9874L + 7684053L);

      try {
         long chunk_X = (long)chunkX * 16L;
         long chunk_Z = (long)chunkZ * 16L;
         WE_Biome[][] chunkBiomes = new WE_Biome[16][16];

         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               chunkBiomes[x][z] = WE_Biome.getBiomeAt(this, chunk_X + (long)x, chunk_Z + (long)z);
            }
         }

         for (int i = 0; i < this.createChunkGen_List.size(); i++) {
            this.createChunkGen_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, 0, 0, 0));
         }

         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               for (int i = 0; i < this.createChunkGen_InXZ_List.size(); i++) {
                  this.createChunkGen_InXZ_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z));
               }

               for (int i = 0; i < chunkBiomes[x][z].createChunkGen_InXZ_List.size(); i++) {
                  chunkBiomes[x][z].createChunkGen_InXZ_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z));
               }

               for (int y = 255; y >= 0; y--) {
                  for (int i = 0; i < this.createChunkGen_InXYZ_List.size(); i++) {
                     this.createChunkGen_InXYZ_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z));
                  }

                  for (int i = 0; i < chunkBiomes[x][z].createChunkGen_InXYZ_List.size(); i++) {
                     chunkBiomes[x][z].createChunkGen_InXYZ_List.get(i).gen(new WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z));
                  }
               }
            }
         }

         this.wp.onChunkProvider(chunkX, chunkZ, primer);
      } catch (Exception var13) {
         FMLLog.warning("Error caught in planetary worldgen at coords " + chunkX + "," + chunkZ + ".", new Object[0]);
         var13.printStackTrace();
      }

      Chunk chunk = new Chunk(this.world, primer, chunkX, chunkZ);
      chunk.generateSkylightMap();
      return chunk;
   }

   public void populate(int chunkX, int chunkZ) {
      BlockFalling.fallInstantly = true;
      int x = chunkX << 4;
      int z = chunkZ << 4;
      boolean flag = false;
      this.rand.setSeed(this.world.getSeed() * (long)Math.pow((double)chunkX, 3.0) + (long)Math.pow((double)chunkZ, 2.0) * 9874L + 7684053L);
      MinecraftForge.EVENT_BUS.post(new Pre(this, this.world, this.rand, chunkX, chunkZ, flag));

      for (int i = 0; i < this.decorateChunkGen_List.size(); i++) {
         this.decorateChunkGen_List.get(i).generate(this.rand, x, z, this.world, this, this.world.getChunkProvider());
      }

      WE_Biome b = WE_Biome.getBiomeAt(this, (long)x, (long)z);

      for (int i = 0; i < b.decorateChunkGen_List.size(); i++) {
         b.decorateChunkGen_List.get(i).generate(this.rand, x, z, this.world, this, this.world.getChunkProvider());
      }

      b.decorateBiome(this.world, this.rand, x, z);
      this.wp.onPopulate(chunkX, chunkZ);
      WorldEntitySpawner.performWorldGenSpawning(this.world, b, x + 8, z + 8, 16, 16, this.rand);
      MinecraftForge.EVENT_BUS.post(new Post(this, this.world, this.rand, chunkX, chunkZ, flag));
      BlockFalling.fallInstantly = false;
   }

   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      WE_Biome biome = WE_Biome.getBiomeAt(this, (long)pos.getX(), (long)pos.getZ());
      return biome != null ? biome.getSpawnableList(creatureType) : null;
   }

   public void recreateStructures(Chunk chunkIn, int x, int z) {
      this.wp.recreateStructures(chunkIn, x, z);
   }

   public boolean generateStructures(Chunk chunkIn, int x, int z) {
      return false;
   }

   public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
      return null;
   }

   public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
      return false;
   }
}
