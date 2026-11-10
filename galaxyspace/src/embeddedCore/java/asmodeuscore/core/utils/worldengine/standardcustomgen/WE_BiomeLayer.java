package asmodeuscore.core.utils.worldengine.standardcustomgen;

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class WE_BiomeLayer extends WE_CreateChunkGen_InXZ {
   public List<IBlockState> layerBlock = new ArrayList<>();
   public List<IBlockState> layerReplacingBlock = new ArrayList<>();
   public List<Integer> layerStart = new ArrayList<>();
   public List<Integer> layerRandomStart = new ArrayList<>();
   public List<Integer> layerEnd = new ArrayList<>();
   public List<Integer> layerRandomEnd = new ArrayList<>();
   public List<Boolean> layerUnderWaterGen = new ArrayList<>();

   public void add(IBlockState block, IBlockState replacingBlock, int start, int r_start, int end, int r_end, boolean underWater) {
      this.layerBlock.add(block);
      this.layerReplacingBlock.add(replacingBlock);
      this.layerStart.add(start);
      this.layerRandomStart.add(r_start);
      this.layerEnd.add(end);
      this.layerRandomEnd.add(r_end);
      this.layerUnderWaterGen.add(underWater);
   }

   public void add(IBlockState block, int start, int r_start, int end, int r_end, boolean underWater) {
      this.layerBlock.add(block);
      this.layerReplacingBlock.add(null);
      this.layerStart.add(start);
      this.layerRandomStart.add(r_start);
      this.layerEnd.add(end);
      this.layerRandomEnd.add(r_end);
      this.layerUnderWaterGen.add(underWater);
   }

   @Override
   public void gen(WE_GeneratorData data) {
      for (int i = 0; i < this.layerBlock.size(); i++) {
         int startPoint = this.layerStart.get(i);
         int endPoint = this.layerEnd.get(i);
         if (this.layerStart.get(i) < 0 || this.layerEnd.get(i) < 0) {
            if (this.layerStart.get(i) < -255) {
               startPoint = Math.abs(this.layerStart.get(i) % 256);
            }

            if (this.layerEnd.get(i) < -255) {
               endPoint = Math.abs(this.layerEnd.get(i) % 256);
            }

            for (int y = 255; y >= 0; y--) {
               if (this.getBlock(data, y).getMaterial() != Material.AIR && !this.getBlock(data, y).getMaterial().isLiquid()) {
                  if (this.layerStart.get(i) < 0) {
                     startPoint += y;
                  }

                  if (this.layerEnd.get(i) < 0) {
                     endPoint += y;
                  }
                  break;
               }
            }
         }

         if (this.layerRandomStart.get(i) > 0) {
            startPoint += data.chunkProvider.rand.nextInt(this.layerRandomStart.get(i) + 1);
         } else if (this.layerRandomStart.get(i) < 0) {
            startPoint -= data.chunkProvider.rand.nextInt(Math.abs(this.layerRandomStart.get(i)) + 1);
         }

         if (this.layerRandomEnd.get(i) > 0) {
            endPoint += data.chunkProvider.rand.nextInt(this.layerRandomEnd.get(i) + 1);
         } else if (this.layerRandomEnd.get(i) < 0) {
            endPoint -= data.chunkProvider.rand.nextInt(Math.abs(this.layerRandomEnd.get(i)) + 1);
         }

         if (!this.layerUnderWaterGen.get(i)
            && this.getBlock(data, startPoint + 1) != Blocks.AIR.getDefaultState()
            && this.getBlock(data, startPoint + 1).getMaterial().isLiquid()) {
            return;
         }

         if (this.layerReplacingBlock.get(i) != null) {
            for (int yx = startPoint; yx >= endPoint; yx--) {
               if (yx >= 0 && this.getBlock(data, yx) == this.layerReplacingBlock.get(i)) {
                  this.setBlock(data, this.layerBlock.get(i), yx);
               }
            }
         } else {
            for (int yxx = startPoint; yxx >= endPoint; yxx--) {
               this.setBlock(data, this.layerBlock.get(i), yxx);
            }
         }
      }
   }
}
