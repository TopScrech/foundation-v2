package asmodeuscore.core.utils.worldengine.standardcustomgen;

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;

public class WE_CaveGen extends WE_CreateChunkGen {
   public List<IBlockState> replaceBlocksList = new ArrayList<>();
   public IBlockState caveBlock = Blocks.AIR.getDefaultState();
   public IBlockState lavaBlock = Blocks.LAVA.getDefaultState();
   public boolean ignoreLiquid = false;
   public int lavaMaxY = 12;
   public int maxY = 120;
   public int range = 8;
   public double d6_range = 1.5;
   public int frequency = 15;
   Random rand = new Random();

   public WE_CaveGen() {
      this.replaceBlocksList.add(Blocks.STONE.getDefaultState());
      this.replaceBlocksList.add(Blocks.STONE.getStateFromMeta(3));
      this.replaceBlocksList.add(Blocks.STONE.getStateFromMeta(5));
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
            int e = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.frequency) + 1) + 1);
            if (this.rand.nextInt(7) != 0) {
               e = 0;
            }

            for (int i = 0; i < e; i++) {
               double gx = (double)cx * 16.0 + (double)this.rand.nextInt(16);
               double gy = (double)this.rand.nextInt(this.rand.nextInt(this.maxY) + 16);
               double gz = (double)cz * 16.0 + (double)this.rand.nextInt(16);
               int k = 1;
               if (this.rand.nextInt(4) == 0) {
                  this.caveGen_func(data, this.rand.nextLong(), gx, gy, gz, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
                  k += this.rand.nextInt(4);
               }

               for (int l1 = 0; l1 < k; l1++) {
                  float x = this.rand.nextFloat() * 2.0F * (float) Math.PI;
                  float x1 = (this.rand.nextFloat() - 0.5F) * 0.25F;
                  float x2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                  if (this.rand.nextInt(10) == 0) {
                     x2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                  }

                  this.caveGen_func(data, this.rand.nextLong(), gx, gy, gz, x2, x, x1, 0, 0, 1.0);
               }
            }
         }
      }
   }

   public void caveGen_func(WE_GeneratorData data, long rs, double gx, double gy, double gz, float fn1, float fn2, float fn3, int in1, int in2, double dn) {
      double cx = (double)data.chunk_X + 8.0;
      double cz = (double)data.chunk_Z + 8.0;
      float f3 = 0.0F;
      float f4 = 0.0F;
      Random r = new Random(rs);
      if (in2 <= 0) {
         int p = this.range * 16 - 16;
         in2 = p - r.nextInt(p / 4);
      }

      boolean flag2 = false;
      if (in1 == -1) {
         in1 = in2 / 2;
         flag2 = true;
      }

      int k1 = r.nextInt(in2 / 2) + in2 / 4;

      for (boolean flag = r.nextInt(6) == 0; in1 < in2; in1++) {
         double d6 = this.d6_range + (double)MathHelper.sin((float)in1 * (float) Math.PI / (float)in2) * (double)fn1;
         double d7 = d6 * dn;
         float f5 = MathHelper.cos(fn3);
         float f6 = MathHelper.sin(fn3);
         gx += (double)MathHelper.cos(fn2) * (double)f5;
         gy += (double)f6;
         gz += (double)MathHelper.sin(fn2) * (double)f5;
         if (flag) {
            fn3 *= 0.92F;
         } else {
            fn3 *= 0.7F;
         }

         fn3 += f4 * 0.1F;
         fn2 += f3 * 0.1F;
         f4 *= 0.9F;
         f3 *= 0.75F;
         f4 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 2.0F;
         f3 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 4.0F;
         if (!flag2 && in1 == k1 && fn1 > 1.0F && in2 > 0) {
            this.caveGen_func(data, r.nextLong(), gx, gy, gz, r.nextFloat() * 0.5F + 0.5F, fn2 - (float) (Math.PI / 2), fn3 / 3.0F, in1, in2, 1.0);
            this.caveGen_func(data, r.nextLong(), gx, gy, gz, r.nextFloat() * 0.5F + 0.5F, fn2 + (float) (Math.PI / 2), fn3 / 3.0F, in1, in2, 1.0);
            return;
         }

         if (flag2 || r.nextInt(4) != 0) {
            double d8 = gx - cx;
            double d9 = gz - cz;
            double d10 = (double)in2 - (double)in1;
            double d11 = (double)fn1 + 18.0;
            if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
               return;
            }

            if (gx >= cx - 16.0 - d6 * 2.0 && gz >= cz - 16.0 - d6 * 2.0 && gx <= cx + 16.0 + d6 * 2.0 && gz <= cz + 16.0 + d6 * 2.0) {
               int i4 = MathHelper.floor(gx - d6) - (int)data.chunk_X - 1;
               int l1 = MathHelper.floor(gx + d6) - (int)data.chunk_X + 1;
               int j4 = MathHelper.floor(gy - d7) - 1;
               int i2 = MathHelper.floor(gy + d7) + 1;
               int k4 = MathHelper.floor(gz - d6) - (int)data.chunk_Z - 1;
               int j2 = MathHelper.floor(gz + d6) - (int)data.chunk_Z + 1;
               if (i4 < 0) {
                  i4 = 0;
               }

               if (l1 > 16) {
                  l1 = 16;
               }

               if (j4 < 1) {
                  j4 = 1;
               }

               if (this.caveBlock.getMaterial() == Material.WATER && j4 < 10) {
                  j4 = 10;
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

               boolean flag3 = false;

               for (int k2 = i4; !flag3 && k2 < l1; k2++) {
                  for (int l2 = k4; !flag3 && l2 < j2; l2++) {
                     for (int i3 = i2 + 1; !flag3 && i3 >= j4 - 1; i3--) {
                        int j3 = (k2 * 16 + l2) * 256 + i3;
                        if (i3 >= 0 && i3 < 256) {
                           if (!this.ignoreLiquid && this.isOceanBlock(data, j3, k2, i3, l2)) {
                              flag3 = true;
                           }

                           if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1) {
                              i3 = j4;
                           }
                        }
                     }
                  }
               }

               if (!flag3) {
                  for (int var62 = i4; var62 < l1; var62++) {
                     double d13 = ((double)var62 + (double)data.chunk_X + 0.5 - gx) / d6;

                     for (int j3 = k4; j3 < j2; j3++) {
                        double d14 = ((double)j3 + (double)data.chunk_Z + 0.5 - gz) / d6;
                        int k3 = (var62 * 16 + j3) * 256 + i2;
                        if (d13 * d13 + d14 * d14 < 1.0) {
                           for (int l3 = i2 - 1; l3 >= j4; l3--) {
                              double d12 = ((double)l3 + 0.5 - gy) / d7;
                              if (d12 > -0.7 && d13 * d13 + d12 * d12 + d14 * d14 < 1.0) {
                                 this.digBlock(data, k3, var62, l3, j3);
                              }

                              k3--;
                           }
                        }
                     }
                  }

                  if (flag2) {
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
