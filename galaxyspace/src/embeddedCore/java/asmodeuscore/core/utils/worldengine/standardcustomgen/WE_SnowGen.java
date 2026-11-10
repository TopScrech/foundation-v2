package asmodeuscore.core.utils.worldengine.standardcustomgen;

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ;
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class WE_SnowGen extends WE_CreateChunkGen_InXZ {
   public int snowPoint = 111;
   public int randomSnowPoint = 2;
   public IBlockState snowBlock = Blocks.SNOW_LAYER.getDefaultState();
   public boolean genSnow = true;
   public IBlockState iceBlock = Blocks.ICE.getDefaultState();
   public Material freezeMaterial = Material.WATER;
   public int snowOnWaterRandom = 2;

   @Override
   public void gen(WE_GeneratorData data) {
      for (int y = 255; y >= this.snowPoint + data.chunkProvider.rand.nextInt(this.randomSnowPoint + 1); y--) {
         if (this.getBlock(data, y).getBlock() != Blocks.AIR) {
            if (this.getBlock(data, y).getMaterial() == this.freezeMaterial) {
               this.setBlock(data, this.iceBlock, y);
               if (this.genSnow && data.chunkProvider.rand.nextInt(this.snowOnWaterRandom + 1) == 0) {
                  this.setBlock(data, this.snowBlock, y + 1);
               }
            } else if (this.genSnow) {
               this.setBlock(data, this.snowBlock, y + 1);
            }
            break;
         }
      }
   }
}
