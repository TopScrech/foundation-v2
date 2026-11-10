package asmodeuscore.core.utils.worldengine.standardcustomgen;

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;

public class WE_RavineGen extends WE_CreateChunkGen {
   public List<IBlockState> replaceBlocksList = new ArrayList<>();
   public IBlockState caveBlock = Blocks.AIR.getDefaultState();
   public IBlockState lavaBlock = Blocks.LAVA.getDefaultState();
   public boolean ignoreLiquid = false;
   public int lavaMaxY = 12;
   public int range = 8;
   public float[] field_75046_d = new float[1024];
   Random rand = new Random();

   public WE_RavineGen() {
      this.replaceBlocksList.add(Blocks.STONE.getDefaultState());
   }

   @Override
   public void gen(WE_GeneratorData data) {
      this.rand.setSeed(data.chunkProvider.world.getSeed());
      long rx = this.rand.nextLong();
      long rz = this.rand.nextLong();

      for (long cx = data.chunk_X / 16L - (long)this.range; cx <= data.chunk_X / 16L + (long)this.range; cx++) {
         for (long cz = data.chunk_Z / 16L - (long)this.range; cz <= data.chunk_Z / 16L + (long)this.range; cz++) {
            long nv1 = cx * rx;
            long nv2 = cz * rz;
            this.rand.setSeed(nv1 ^ nv2 ^ data.chunkProvider.world.getSeed());
            if (this.rand.nextInt(50) == 0) {
               double gx = (double)cx * 16.0 + (double)this.rand.nextInt(16);
               double gy = (double)this.rand.nextInt(this.rand.nextInt(40) + 8) + 20.0;
               double gz = (double)cz * 16.0 + (double)this.rand.nextInt(16);
               byte b0 = 1;

               for (int i1 = 0; i1 < b0; i1++) {
                  float x = this.rand.nextFloat() * 2.0F * (float) Math.PI;
                  float x1 = (this.rand.nextFloat() - 0.5F) * 0.25F;
                  float x2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                  this.caveGen_func(data, this.rand.nextLong(), gx, gy, gz, x2, x, x1, 0, 0, 3.0);
               }
            }
         }
      }
   }

   public void caveGen_func(WE_GeneratorData data, long rs, double gx, double gy, double gz, float fn1, float fn2, float fn3, int in1, int in2, double dn) {
      double d4 = (double)data.chunk_X + 8.0;
      double d5 = (double)data.chunk_Z + 8.0;
      float f3 = 0.0F;
      float f4 = 0.0F;
      Random r = new Random(rs);
      if (in2 <= 0) {
         int p = this.range * 16 - 16;
         in2 = p - r.nextInt(p / 4);
      }

      boolean flag1 = false;
      if (in1 == -1) {
         in1 = in2 / 2;
         flag1 = true;
      }

      float f5 = 1.0F;

      for (int k1 = 0; k1 < 256; k1++) {
         if (k1 == 0 || r.nextInt(3) == 0) {
            f5 = 1.0F + r.nextFloat() * r.nextFloat() * 1.0F;
         }

         this.field_75046_d[k1] = f5 * f5;
      }

      for (; in1 < in2; in1++) {
         double d12 = 1.5 + (double)MathHelper.sin((float)in1 * (float) Math.PI / (float)in2) * (double)fn1;
         double d6 = d12 * dn;
         d12 *= (double)r.nextFloat() * 0.25 + 0.75;
         d6 *= (double)r.nextFloat() * 0.25 + 0.75;
         float f6 = MathHelper.cos(fn3);
         float f7 = MathHelper.sin(fn3);
         gx += (double)MathHelper.cos(fn2) * (double)f6;
         gy += (double)f7;
         gz += (double)MathHelper.sin(fn2) * (double)f6;
         fn3 *= 0.7F;
         fn3 += f4 * 0.05F;
         fn2 += f3 * 0.05F;
         f4 *= 0.8F;
         f3 *= 0.5F;
         f4 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 2.0F;
         f3 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 4.0F;
         if (flag1 || r.nextInt(4) != 0) {
            double d7 = gx - d4;
            double d8 = gz - d5;
            double d9 = (double)in2 - (double)in1;
            double d10 = (double)fn1 + 18.0;
            if (d7 * d7 + d8 * d8 - d9 * d9 > d10 * d10) {
               return;
            }

            if (gx >= d4 - 16.0 - d12 * 2.0 && gz >= d5 - 16.0 - d12 * 2.0 && gx <= d4 + 16.0 + d12 * 2.0 && gz <= d5 + 16.0 + d12 * 2.0) {
               int i4 = MathHelper.floor(gx - d12) - (int)data.chunk_X - 1;
               int l1 = MathHelper.floor(gx + d12) - (int)data.chunk_X + 1;
               int j4 = MathHelper.floor(gy - d6) - 1;
               int i2 = MathHelper.floor(gy + d6) + 1;
               int k4 = MathHelper.floor(gz - d12) - (int)data.chunk_Z - 1;
               int j2 = MathHelper.floor(gz + d12) - (int)data.chunk_Z + 1;
               if (i4 < 0) {
                  i4 = 0;
               }

               if (l1 > 16) {
                  l1 = 16;
               }

               if (j4 < 1) {
                  j4 = 1;
               }

               if (i2 > 248) {
                  i2 = 248;
               }

               if (k4 < 0) {
                  k4 = 0;
               }

               if (j2 > 16) {
                  j2 = 16;
               }

               boolean flag2 = false;

               for (int k2 = i4; !flag2 && k2 < l1; k2++) {
                  for (int l2 = k4; !flag2 && l2 < j2; l2++) {
                     for (int i3 = i2 + 1; !flag2 && i3 >= j4 - 1; i3--) {
                        int j3 = (k2 * 16 + l2) * 256 + i3;
                        if (i3 >= 0 && i3 < 256) {
                           if (!this.ignoreLiquid && this.isOceanBlock(data, j3, k2, i3, l2)) {
                              flag2 = true;
                           }

                           if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1) {
                              i3 = j4;
                           }
                        }
                     }
                  }
               }

               if (!flag2) {
                  for (int var64 = i4; var64 < l1; var64++) {
                     double d13 = ((double)var64 + (double)data.chunk_X + 0.5 - gx) / d12;

                     for (int j3 = k4; j3 < j2; j3++) {
                        double d14 = ((double)j3 + (double)data.chunk_Z + 0.5 - gz) / d12;
                        int k3 = (var64 * 16 + j3) * 256 + i2;
                        if (d13 * d13 + d14 * d14 < 1.0) {
                           for (int l3 = i2 - 1; l3 >= j4; l3--) {
                              double d11 = ((double)l3 + 0.5 - gy) / d6;
                              if ((d13 * d13 + d14 * d14) * (double)this.field_75046_d[l3] + d11 * d11 / 6.0 < 1.0) {
                                 this.digBlock(data, k3, var64, l3, j3);
                              }

                              k3--;
                           }
                        }
                     }
                  }

                  if (flag1) {
                     break;
                  }
               }
            }
         }
      }
   }

   public void digBlock(WE_GeneratorData data, int index, int x, int y, int z) {
      if (!(data.primer.getBlockState(x, y, z).getBlock() instanceof BlockFalling)
         || !(data.primer.getBlockState(x, y + 1, z).getBlock() instanceof BlockFalling)) {
         for (int i = 0; i < this.replaceBlocksList.size(); i++) {
            if (data.primer.getBlockState(x, y, z) == this.replaceBlocksList.get(i)) {
               if (y <= this.lavaMaxY) {
                  data.primer.setBlockState(x, y, z, this.lavaBlock);
               } else {
                  data.primer.setBlockState(x, y, z, this.caveBlock);
               }
               break;
            }
         }
      }
   }

   public boolean isOceanBlock(WE_GeneratorData data, int index, int x, int y, int z) {
      return data.primer.getBlockState(x, y, z) != null ? data.primer.getBlockState(x, y, z).getMaterial().isLiquid() : false;
   }

   public void addReplacingBlock(IBlockState block) {
      this.replaceBlocksList.add(block);
   }
}
