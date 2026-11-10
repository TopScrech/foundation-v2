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
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WE_TreeGen extends WorldGenerator {
   public Block bWood = Blocks.LOG;
   public Block bLeaves = Blocks.LEAVES;
   public Block bSapling = Blocks.SAPLING;
   public Block bVine = Blocks.VINE;
   public Block bCocoa = Blocks.COCOA;
   public int minTreeHeight = 4;
   public int metaWood = 0;
   public int metaLeaves = 0;
   public boolean vinesGrow = false;
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

   public boolean isReplaceable(World world, BlockPos pos) {
      Block b = world.getBlockState(pos).getBlock();
      return b.isAir(b.getDefaultState(), world, pos) || b.isLeaves(b.getDefaultState(), world, pos) || b.isWood(world, pos) || this.cb(b);
   }

   public WE_TreeGen(boolean doBlockNotify) {
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
      int l = rand.nextInt(3) + this.minTreeHeight;
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + l + 1 <= 256) {
         for (int i1 = position.getY(); i1 <= position.getY() + 1 + l; i1++) {
            byte b0 = 1;
            if (i1 == position.getY()) {
               b0 = 0;
            }

            if (i1 >= position.getY() + 1 + l - 2) {
               b0 = 2;
            }

            for (int j1 = position.getX() - b0; j1 <= position.getX() + b0 && flag; j1++) {
               for (int k1 = position.getZ() - b0; k1 <= position.getZ() + b0 && flag; k1++) {
                  if (i1 >= 0 && i1 < 256) {
                     Block block = world.getBlockState(new BlockPos(j1, i1, k1)).getBlock();
                     if (!this.isReplaceable(world, new BlockPos(j1, i1, k1))) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            Block block2 = world.getBlockState(new BlockPos(position.down())).getBlock();
            boolean isSoil = block2.canSustainPlant(block2.getDefaultState(), world, position.down(), EnumFacing.UP, (BlockSapling)this.bSapling);
            if (isSoil && position.getY() < 256 - l - 1) {
               block2.onPlantGrow(block2.getDefaultState(), world, position.down(), position);
               byte b0x = 3;
               byte b1 = 0;

               for (int k1x = position.getY() - b0x + l; k1x <= position.getY() + l; k1x++) {
                  int i3 = k1x - position.getY() - l;
                  int l1 = b1 + 1 - i3 / 2;

                  for (int i2 = position.getX() - l1; i2 <= position.getX() + l1; i2++) {
                     int j2 = i2 - position.getX();

                     for (int k2 = position.getZ() - l1; k2 <= position.getZ() + l1; k2++) {
                        int l2 = k2 - position.getZ();
                        if (Math.abs(j2) != l1 || Math.abs(l2) != l1 || rand.nextInt(2) != 0 && i3 != 0) {
                           Block block1 = world.getBlockState(new BlockPos(i2, k1x, k2)).getBlock();
                           if (block1.isAir(block1.getDefaultState(), world, new BlockPos(i2, k1x, k2))
                              || block1.isLeaves(block1.getDefaultState(), world, new BlockPos(i2, k1x, k2))) {
                              this.setBlockAndNotifyAdequately(world, new BlockPos(i2, k1x, k2), this.bLeaves.getStateFromMeta(this.metaLeaves));
                           }
                        }
                     }
                  }
               }

               for (int var21 = 0; var21 < l; var21++) {
                  Block block = world.getBlockState(position.up(var21)).getBlock();
                  if (block.isAir(block.getDefaultState(), world, position.up(var21)) || block.isLeaves(block.getDefaultState(), world, position.up(var21))) {
                     this.setBlockAndNotifyAdequately(world, position.up(var21), this.bWood.getStateFromMeta(this.metaWood));
                     if (this.vinesGrow && var21 > 0) {
                        if (rand.nextInt(3) > 0 && world.isAirBlock(new BlockPos(position.getX() - 1, position.getY() + var21, position.getZ()))) {
                           this.setBlockAndNotifyAdequately(
                              world, new BlockPos(position.getX() - 1, position.getY() + var21, position.getZ()), this.bVine.getStateFromMeta(8)
                           );
                        }

                        if (rand.nextInt(3) > 0 && world.isAirBlock(new BlockPos(position.getX() + 1, position.getY() + var21, position.getZ()))) {
                           this.setBlockAndNotifyAdequately(
                              world, new BlockPos(position.getX() + 1, position.getY() + var21, position.getZ()), this.bVine.getStateFromMeta(2)
                           );
                        }

                        if (rand.nextInt(3) > 0 && world.isAirBlock(new BlockPos(position.getX(), position.getY() + var21, position.getZ() - 1))) {
                           this.setBlockAndNotifyAdequately(
                              world, new BlockPos(position.getX(), position.getY() + var21, position.getZ() - 1), this.bVine.getStateFromMeta(1)
                           );
                        }

                        if (rand.nextInt(3) > 0 && world.isAirBlock(new BlockPos(position.getX(), position.getY() + var21, position.getZ() + 1))) {
                           this.setBlockAndNotifyAdequately(
                              world, new BlockPos(position.getX(), position.getY() + var21, position.getZ() + 1), this.bVine.getStateFromMeta(4)
                           );
                        }
                     }
                  }
               }

               if (this.vinesGrow) {
                  for (int var22 = position.getY() - 3 + l; var22 <= position.getY() + l; var22++) {
                     int i3 = var22 - position.getY() - l;
                     int l1 = 2 - i3 / 2;

                     for (int i2 = position.getX() - l1; i2 <= position.getX() + l1; i2++) {
                        for (int j2 = position.getZ() - l1; j2 <= position.getZ() + l1; j2++) {
                           if (world.getBlockState(new BlockPos(i2, var22, j2))
                              .getBlock()
                              .isLeaves(world.getBlockState(new BlockPos(i2, var22, j2)), world, new BlockPos(i2, var22, j2))) {
                              if (rand.nextInt(4) == 0
                                 && world.getBlockState(new BlockPos(i2 - 1, var22, j2))
                                    .getBlock()
                                    .isAir(world.getBlockState(new BlockPos(i2 - 1, var22, j2)), world, new BlockPos(i2 - 1, var22, j2))) {
                                 this.growVines(world, i2 - 1, var22, j2, 8);
                              }

                              if (rand.nextInt(4) == 0
                                 && world.getBlockState(new BlockPos(i2 + 1, var22, j2))
                                    .getBlock()
                                    .isAir(world.getBlockState(new BlockPos(i2 + 1, var22, j2)), world, new BlockPos(i2 + 1, var22, j2))) {
                                 this.growVines(world, i2 + 1, var22, j2, 2);
                              }

                              if (rand.nextInt(4) == 0
                                 && world.getBlockState(new BlockPos(i2, var22, j2 - 1))
                                    .getBlock()
                                    .isAir(world.getBlockState(new BlockPos(i2, var22, j2 - 1)), world, new BlockPos(i2, var22, j2 - 1))) {
                                 this.growVines(world, i2, var22, j2 - 1, 1);
                              }

                              if (rand.nextInt(4) == 0
                                 && world.getBlockState(new BlockPos(i2, var22, j2 + 1))
                                    .getBlock()
                                    .isAir(world.getBlockState(new BlockPos(i2, var22, j2 + 1)), world, new BlockPos(i2, var22, j2 + 1))) {
                                 this.growVines(world, i2, var22, j2 + 1, 4);
                              }
                           }
                        }
                     }
                  }

                  if (rand.nextInt(5) == 0 && l > 5) {
                     for (int var23 = 0; var23 < 2; var23++) {
                        for (int i3 = 0; i3 < 4; i3++) {
                           if (rand.nextInt(4 - var23) == 0) {
                              int var28 = rand.nextInt(3);
                           }
                        }
                     }
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public void growVines(World world, int x, int y, int z, int meta) {
      this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), this.bVine.getStateFromMeta(meta));
      int i1 = 4;

      while (true) {
         y--;
         if (!world.getBlockState(new BlockPos(x, y, z)).getBlock().isAir(world.getBlockState(new BlockPos(x, y, z)), world, new BlockPos(x, y, z)) || i1 <= 0) {
            return;
         }

         this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z), this.bVine.getStateFromMeta(meta));
         i1--;
      }
   }
}
