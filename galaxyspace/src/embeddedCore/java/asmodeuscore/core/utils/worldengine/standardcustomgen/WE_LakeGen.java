package asmodeuscore.core.utils.worldengine.standardcustomgen;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WE_LakeGen implements IWorldGenerator {
   public IBlockState lakeBlock = Blocks.WATER.getDefaultState();
   public IBlockState lakeBlock_ice = Blocks.ICE.getDefaultState();
   public int chunksForLake = 12;
   public int minY = 0;
   public int maxY = 255;
   public int iceY = 111;
   public int random_iceY = 2;
   public boolean iceGen = true;
   public boolean underground = false;
   public boolean update = true;

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (random.nextInt(this.chunksForLake) == 0) {
         int x = chunkX + 8;
         int z = chunkZ + 8;
         int y = this.underground ? this.minY + random.nextInt(this.maxY - this.minY + 1) : world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
         if (y >= this.minY && y <= this.maxY) {
            this.lake(world, random, x, y, z);
         }
      }
   }

   public void lake(World world, Random random, int x, int y, int z) {
      x -= 8;
      z -= 8;

      while (y > 5 && world.isAirBlock(new BlockPos(x, y, z))) {
         y--;
      }

      if (y > 4) {
         y -= 4;
         boolean[] aboolean = new boolean[2048];

         for (int w = 0; w < random.nextInt(4) + 4; w++) {
            double d0 = random.nextDouble() * 6.0 + 3.0;
            double d1 = random.nextDouble() * 4.0 + 2.0;
            double d2 = random.nextDouble() * 6.0 + 3.0;
            double d3 = random.nextDouble() * (14.0 - d0) + 1.0 + d0 / 2.0;
            double d4 = random.nextDouble() * (4.0 - d1) + 2.0 + d1 / 2.0;
            double d5 = random.nextDouble() * (14.0 - d2) + 1.0 + d2 / 2.0;

            for (int bx = 1; bx < 15; bx++) {
               for (int bz = 1; bz < 15; bz++) {
                  for (int by = 1; by < 7; by++) {
                     double d6 = ((double)bx - d3) * 2.0 / d0;
                     double d7 = ((double)by - d4) * 2.0 / d1;
                     double d8 = ((double)bz - d5) * 2.0 / d2;
                     double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                     if (d9 < 1.0) {
                        aboolean[(bx * 16 + bz) * 8 + by] = true;
                     }
                  }
               }
            }
         }

         for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
               for (int byx = 0; byx < 8; byx++) {
                  if (!aboolean[(bx * 16 + bz) * 8 + byx]
                     && (
                        bx < 15 && aboolean[((bx + 1) * 16 + bz) * 8 + byx]
                           || bx > 0 && aboolean[((bx - 1) * 16 + bz) * 8 + byx]
                           || bz < 15 && aboolean[(bx * 16 + bz + 1) * 8 + byx]
                           || bz > 0 && aboolean[(bx * 16 + bz - 1) * 8 + byx]
                           || byx < 7 && aboolean[(bx * 16 + bz) * 8 + byx + 1]
                           || byx > 0 && aboolean[(bx * 16 + bz) * 8 + byx - 1]
                     )) {
                     Material material = world.getBlockState(new BlockPos(x + bx, y + byx, z + bz)).getMaterial();
                     if (byx >= 4 && material.isLiquid()) {
                        return;
                     }

                     if (byx < 4 && !material.isSolid() && world.getBlockState(new BlockPos(x + bx, y + byx, z + bz)) != this.lakeBlock) {
                        return;
                     }
                  }

                  if (aboolean[(bx * 16 + bz) * 8 + byx]) {
                     world.setBlockState(new BlockPos(x + bx, y + byx, z + bz), byx >= 4 ? Blocks.AIR.getDefaultState() : this.lakeBlock, this.update ? 3 : 2);
                  }
               }

               if (this.iceGen && y + 4 >= this.iceY + random.nextInt(this.random_iceY + 1)) {
                  world.setBlockState(new BlockPos(x + bx, y + 4, z + bz), this.lakeBlock_ice, this.update ? 3 : 2);
               }
            }
         }
      }
   }
}
