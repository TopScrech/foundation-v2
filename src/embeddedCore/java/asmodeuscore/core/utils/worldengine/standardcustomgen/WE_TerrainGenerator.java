package asmodeuscore.core.utils.worldengine.standardcustomgen;

import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;

public class WE_TerrainGenerator extends WE_CreateChunkGen {
   public IBlockState worldStoneBlock = Blocks.STONE.getDefaultState();
   public IBlockState worldSeaGenBlock = Blocks.WATER.getDefaultState();
   public boolean worldSeaGen = true;
   public int worldSeaGenMaxY = 63;

   @Override
   public void gen(WE_GeneratorData data) {
      int q = WE_Biome.getBiggestInterpolationQuality(data.chunkProvider);
      WE_Biome[][] bl = new WE_Biome[16 + q * 2][16 + q * 2];
      WE_Biome sb = null;
      boolean ni = false;

      for (int xm = 0; xm < 16 + q * 2; xm++) {
         for (int zm = 0; zm < 16 + q * 2; zm++) {
            if (xm > q - 1 && zm > q - 1 && xm < 16 + q && zm < 16 + q) {
               bl[xm][zm] = this.getBiome(data, xm - q, zm - q);
            } else {
               bl[xm][zm] = WE_Biome.getBiomeAt(data.chunkProvider, data.chunk_X - (long)q + (long)xm, data.chunk_Z - (long)q + (long)zm);
            }

            if (!ni) {
               if (sb == null) {
                  sb = bl[xm][zm];
               } else if (sb.id != bl[xm][zm].id) {
                  ni = true;
               }
            }
         }
      }

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            int n = MathHelper.floor(this.interpolatedBlock(data.chunkProvider.world.getSeed(), data.chunk_X, data.chunk_Z, x, z, bl, ni, q));

            for (int y = 0; y < 256; y++) {
               if (y != 0 && (y > 5 || data.chunkProvider.rand.nextInt(y) != 0)) {
                  if (y <= n) {
                     this.setBlock(data, this.worldStoneBlock, x, y, z);
                  } else if (this.worldSeaGen && y <= this.worldSeaGenMaxY) {
                     this.setBlock(data, this.worldSeaGenBlock, x, y, z);
                  }
               } else {
                  this.setBlock(data, Blocks.BEDROCK.getDefaultState(), x, y, z);
               }
            }
         }
      }
   }

   public double interpolatedBlock(long seed, long chunk_X, long chunk_Z, int bcx, int bcz, WE_Biome[][] biomeListOfChunk, boolean doInterpolate, int iQuality) {
      int bx = iQuality + bcx;
      int bz = iQuality + bcz;
      if (!doInterpolate) {
         PerlinNoise noise = new PerlinNoise(
            seed,
            biomeListOfChunk[bx][bz].biomePersistence,
            biomeListOfChunk[bx][bz].biomeNumberOfOctaves,
            biomeListOfChunk[bx][bz].biomeScaleX,
            biomeListOfChunk[bx][bz].biomeScaleY,
            biomeListOfChunk[bx][bz].biomeSurfaceHeight
         );
         return noise.genNoise2d((double)(chunk_X + (long)bcx), (double)(chunk_Z + (long)bcz));
      } else {
         double genPersistence = 0.0;
         double genScaleX = 0.0;
         double genScaleY = 0.0;
         int genNumberOfOctaves = 0;
         int genSurfaceHeight = 0;
         int c = 0;

         for (int i = 0; i <= 360; i++) {
            for (int i2 = 0; i2 <= biomeListOfChunk[bx][bz].biomeInterpolateQuality; i2++) {
               int cbx = MathHelper.floor((float)bx + MathHelper.cos((float)i * (float) Math.PI / 180.0F) * (float)i2);
               int cbz = MathHelper.floor((float)bz + MathHelper.sin((float)i * (float) Math.PI / 180.0F) * (float)i2);
               c++;
               genPersistence += biomeListOfChunk[cbx][cbz].biomePersistence;
               genNumberOfOctaves += biomeListOfChunk[cbx][cbz].biomeNumberOfOctaves;
               genScaleX += biomeListOfChunk[cbx][cbz].biomeScaleX;
               genScaleY += biomeListOfChunk[cbx][cbz].biomeScaleY;
               genSurfaceHeight += biomeListOfChunk[cbx][cbz].biomeSurfaceHeight;
            }
         }

         genPersistence /= (double)c;
         genScaleX /= (double)c;
         genScaleY /= (double)c;
         genNumberOfOctaves = MathHelper.floor((float)(genNumberOfOctaves / c));
         genSurfaceHeight = MathHelper.floor((float)(genSurfaceHeight / c));
         PerlinNoise noise = new PerlinNoise(seed, genPersistence, genNumberOfOctaves, genScaleX, genScaleY, genSurfaceHeight);
         return noise.genNoise2d((double)(chunk_X + (long)bcx), (double)(chunk_Z + (long)bcz));
      }
   }
}
