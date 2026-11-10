package asmodeuscore.core.utils.worldengine.standardcustomgen.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WE_BigTreeGen extends WorldGenerator {
   public Block bWood = Blocks.LOG;
   public Block bLeaves = Blocks.LEAVES;
   public Block bSapling = Blocks.SAPLING;
   public byte[] otherCoordPairs = new byte[]{2, 0, 0, 1, 2, 1};
   public int trunkSize = 1;
   public int heightLimitLimit = 12;
   public int leafDistanceLimit = 4;
   public int metaLeaves = 0;
   public int metaWood = 0;
   public double heightAttenuation = 0.618;
   public double branchSlope = 0.381;
   public double scaleWidth = 1.0;
   public double leafDensity = 1.0;
   public List<Block> ab = new ArrayList<>();
   public List<Material> am = new ArrayList<>();

   public boolean cb(Block b) {
      for (int i = 0; i < this.ab.size(); i++) {
         if (b == this.ab.get(i)) {
            return true;
         }
      }

      for (int ix = 0; ix < this.am.size(); ix++) {
         if (b.getMaterial(b.getDefaultState()) == this.am.get(ix)) {
            return true;
         }
      }

      return false;
   }

   public boolean isReplaceable(World world, int x, int y, int z) {
      Block b = world.getBlockState(new BlockPos(x, y, z)).getBlock();
      return b.isAir(b.getDefaultState(), world, new BlockPos(x, y, z))
         || b.isLeaves(b.getDefaultState(), world, new BlockPos(x, y, z))
         || b.isWood(world, new BlockPos(x, y, z))
         || this.cb(b);
   }

   public WE_BigTreeGen(boolean doBlockNotify) {
      super(doBlockNotify);
      this.ab.add(Blocks.GRASS);
      this.ab.add(Blocks.DIRT);
      this.ab.add(Blocks.LOG);
      this.ab.add(Blocks.LOG2);
      this.ab.add(Blocks.SAPLING);
      this.ab.add(Blocks.VINE);
      this.am.add(Material.AIR);
      this.am.add(Material.LEAVES);
   }

   public boolean generate(World world, Random rand, BlockPos position) {
      int heightLimit = 5 + rand.nextInt(this.heightLimitLimit);
      int[] basePos = new int[]{position.getX(), position.getY(), position.getZ()};
      int[] _aint = new int[]{basePos[0], basePos[1], basePos[2]};
      int[] _aint1 = new int[]{basePos[0], basePos[1] + heightLimit - 1, basePos[2]};
      Block block = world.getBlockState(new BlockPos(basePos[0], basePos[1] - 1, basePos[2])).getBlock();
      boolean br;
      if (!block.canSustainPlant(
         block.getDefaultState(), world, new BlockPos(basePos[0], basePos[1], basePos[2]).down(), EnumFacing.UP, (BlockSapling)this.bSapling
      )) {
         br = false;
      } else {
         int i = this.checkBlockLine(world, _aint, _aint1);
         if (i == -1) {
            br = true;
         } else if (i < 6) {
            br = false;
         } else {
            heightLimit = i;
            br = true;
         }
      }

      if (!br) {
         return false;
      } else {
         int height = (int)((double)heightLimit * this.heightAttenuation);
         if (height >= heightLimit) {
            height = heightLimit - 1;
         }

         int ix = (int)(1.382 + Math.pow(this.leafDensity * (double)heightLimit / 13.0, 2.0));
         if (ix < 1) {
            ix = 1;
         }

         int[][] aint = new int[ix * heightLimit][4];
         int j = basePos[1] + heightLimit - this.leafDistanceLimit;
         int k = 1;
         int l = basePos[1] + height;
         int i1 = j - basePos[1];
         aint[0][0] = basePos[0];
         aint[0][1] = j;
         aint[0][2] = basePos[2];
         aint[0][3] = l;
         j--;

         while (i1 >= 0) {
            int j1 = 0;
            float f2;
            if ((double)i1 < (double)heightLimit * 0.3) {
               f2 = -1.618F;
            } else {
               float f = (float)heightLimit / 2.0F;
               float f1 = (float)heightLimit / 2.0F - (float)i1;
               if (f1 == 0.0F) {
                  f2 = f;
               } else if (Math.abs(f1) >= f) {
                  f2 = 0.0F;
               } else {
                  f2 = (float)Math.sqrt(Math.pow((double)Math.abs(f), 2.0) - Math.pow((double)Math.abs(f1), 2.0));
               }

               f2 *= 0.5F;
            }

            if (f2 < 0.0F) {
               j--;
               i1--;
            } else {
               for (double d0 = 0.5; j1 < ix; j1++) {
                  double d1 = this.scaleWidth * (double)f2 * ((double)rand.nextFloat() + 0.328);
                  double d2 = (double)rand.nextFloat() * 2.0 * Math.PI;
                  int k1 = MathHelper.floor(d1 * Math.sin(d2) + (double)basePos[0] + d0);
                  int l1 = MathHelper.floor(d1 * Math.cos(d2) + (double)basePos[2] + d0);
                  int[] aint1 = new int[]{k1, j, l1};
                  int[] aint2 = new int[]{k1, j + this.leafDistanceLimit, l1};
                  if (this.checkBlockLine(world, aint1, aint2) == -1) {
                     int[] aint3 = new int[]{basePos[0], basePos[1], basePos[2]};
                     double d3 = Math.sqrt(Math.pow((double)Math.abs(basePos[0] - aint1[0]), 2.0) + Math.pow((double)Math.abs(basePos[2] - aint1[2]), 2.0));
                     double d4 = d3 * this.branchSlope;
                     if ((double)aint1[1] - d4 > (double)l) {
                        aint3[1] = l;
                     } else {
                        aint3[1] = (int)((double)aint1[1] - d4);
                     }

                     if (this.checkBlockLine(world, aint3, aint1) == -1) {
                        aint[k][0] = k1;
                        aint[k][1] = j;
                        aint[k][2] = l1;
                        aint[k][3] = aint3[1];
                        k++;
                     }
                  }
               }

               j--;
               i1--;
            }
         }

         int[][] leafNodes = new int[k][4];
         System.arraycopy(aint, 0, leafNodes, 0, k);
         int i_1 = 0;

         for (int j_1 = leafNodes.length; i_1 < j_1; i_1++) {
            int l_2 = leafNodes[i_1][1];

            for (int i1_2 = leafNodes[i_1][1] + this.leafDistanceLimit; l_2 < i1_2; l_2++) {
               float p1 = (float)(l_2 - leafNodes[i_1][1]);
               float p2 = !(p1 >= 0.0F) || !(p1 < (float)this.leafDistanceLimit)
                  ? -1.0F
                  : (p1 != 0.0F && p1 != (float)(this.leafDistanceLimit - 1) ? 3.0F : 2.0F);
               byte b1_3 = this.otherCoordPairs[1];
               byte b2_3 = this.otherCoordPairs[4];
               int[] aint_3 = new int[]{leafNodes[i_1][0], l_2, leafNodes[i_1][2]};
               int[] aint1_3 = new int[]{0, 0, 0};
               int l_3 = (int)((double)p2 + 0.618);
               int i1_3 = -l_3;
               int j1_3 = -l_3;

               for (aint1_3[1] = aint_3[1]; i1_3 <= l_3; i1_3++) {
                  aint1_3[b1_3] = aint_3[b1_3] + i1_3;
                  j1_3 = -l_3;

                  while (j1_3 <= l_3) {
                     double d0_3 = Math.pow((double)Math.abs(i1_3) + 0.5, 2.0) + Math.pow((double)Math.abs(j1_3) + 0.5, 2.0);
                     if (d0_3 > (double)(p2 * p2)) {
                        j1_3++;
                     } else {
                        aint1_3[b2_3] = aint_3[b2_3] + j1_3;
                        Block block1 = world.getBlockState(new BlockPos(aint1_3[0], aint1_3[1], aint1_3[2])).getBlock();
                        if (!block1.isAir(block1.getDefaultState(), world, new BlockPos(aint1_3[0], aint1_3[1], aint1_3[2]))
                           && !block1.isLeaves(block1.getDefaultState(), world, new BlockPos(aint1_3[0], aint1_3[1], aint1_3[2]))) {
                           j1_3++;
                        } else {
                           BlockPos pos = new BlockPos(aint1_3[0], aint1_3[1], aint1_3[2]);
                           this.setBlockAndNotifyAdequately(world, pos, this.bLeaves.getStateFromMeta(this.metaLeaves));
                           j1_3++;
                        }
                     }
                  }
               }
            }
         }

         int[] aint_4 = new int[]{basePos[0], basePos[1], basePos[2]};
         int[] aint1_4 = new int[]{basePos[0], basePos[1] + height, basePos[2]};
         this.func_150530_a(world, aint_4, aint1_4, this.bWood);
         if (this.trunkSize == 2) {
            aint_4[0]++;
            aint1_4[0]++;
            this.func_150530_a(world, aint_4, aint1_4, this.bWood);
            aint_4[2]++;
            aint1_4[2]++;
            this.func_150530_a(world, aint_4, aint1_4, this.bWood);
            aint_4[0]--;
            aint1_4[0]--;
            this.func_150530_a(world, aint_4, aint1_4, this.bWood);
         }

         int i_5 = 0;

         for (int[] aint_5 = new int[]{basePos[0], basePos[1], basePos[2]}; i_5 < leafNodes.length; i_5++) {
            int[] aint1_5 = leafNodes[i_5];
            int[] aint2_5 = new int[]{aint1_5[0], aint1_5[1], aint1_5[2]};
            aint_5[1] = aint1_5[3];
            int k_5 = aint_5[1] - basePos[1];
            if ((double)k_5 >= (double)heightLimit * 0.2) {
               this.func_150530_a(world, aint_5, aint2_5, this.bWood);
            }
         }

         return true;
      }
   }

   public void func_150530_a(World worldObj, int[] p_150530_1_, int[] p_150530_2_, Block p_150530_3_) {
      int[] aint2 = new int[]{0, 0, 0};
      byte b0 = 0;

      byte b1;
      for (b1 = 0; b0 < 3; b0++) {
         aint2[b0] = p_150530_2_[b0] - p_150530_1_[b0];
         if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
            b1 = b0;
         }
      }

      if (aint2[b1] != 0) {
         byte b2 = this.otherCoordPairs[b1];
         byte b3 = this.otherCoordPairs[b1 + 3];
         byte b4;
         if (aint2[b1] > 0) {
            b4 = 1;
         } else {
            b4 = -1;
         }

         double d0 = (double)aint2[b2] / (double)aint2[b1];
         double d1 = (double)aint2[b3] / (double)aint2[b1];
         int[] aint3 = new int[]{0, 0, 0};
         int i = 0;

         for (int j = aint2[b1] + b4; i != j; i += b4) {
            aint3[b1] = MathHelper.floor((double)(p_150530_1_[b1] + i) + 0.5);
            aint3[b2] = MathHelper.floor((double)p_150530_1_[b2] + (double)i * d0 + 0.5);
            aint3[b3] = MathHelper.floor((double)p_150530_1_[b3] + (double)i * d1 + 0.5);
            byte b5 = 0;
            int k = Math.abs(aint3[0] - p_150530_1_[0]);
            int l = Math.abs(aint3[2] - p_150530_1_[2]);
            int i1 = Math.max(k, l);
            if (i1 > 0) {
               if (k == i1) {
                  b5 = 4;
               } else if (l == i1) {
                  b5 = 8;
               }
            }

            this.setBlockAndNotifyAdequately(worldObj, new BlockPos(aint3[0], aint3[1], aint3[2]), p_150530_3_.getStateFromMeta(b5));
         }
      }
   }

   public int checkBlockLine(World worldObj, int[] p_76496_1_, int[] p_76496_2_) {
      int[] aint2 = new int[]{0, 0, 0};
      byte b0 = 0;

      byte b1;
      for (b1 = 0; b0 < 3; b0++) {
         aint2[b0] = p_76496_2_[b0] - p_76496_1_[b0];
         if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
            b1 = b0;
         }
      }

      if (aint2[b1] == 0) {
         return -1;
      } else {
         byte b2 = this.otherCoordPairs[b1];
         byte b3 = this.otherCoordPairs[b1 + 3];
         byte b4;
         if (aint2[b1] > 0) {
            b4 = 1;
         } else {
            b4 = -1;
         }

         double d0 = (double)aint2[b2] / (double)aint2[b1];
         double d1 = (double)aint2[b3] / (double)aint2[b1];
         int[] aint3 = new int[]{0, 0, 0};
         int i = 0;

         int j;
         for (j = aint2[b1] + b4; i != j; i += b4) {
            aint3[b1] = p_76496_1_[b1] + i;
            aint3[b2] = MathHelper.floor((double)p_76496_1_[b2] + (double)i * d0);
            aint3[b3] = MathHelper.floor((double)p_76496_1_[b3] + (double)i * d1);
            Block block = worldObj.getBlockState(new BlockPos(aint3[0], aint3[1], aint3[2])).getBlock();
            if (!this.isReplaceable(worldObj, aint3[0], aint3[1], aint3[2])) {
               break;
            }
         }

         return i == j ? -1 : Math.abs(i);
      }
   }
}
