package asmodeuscore.core.utils.worldengine.additions;

import asmodeuscore.core.utils.worldengine.WE_Biome;
import net.minecraft.block.state.IBlockState;

public abstract class WE_CreateChunkGen_InXZ {
   public abstract void gen(WE_GeneratorData var1);

   public void setBlock(WE_GeneratorData data, IBlockState state, int y_in_chunk) {
      data.primer.setBlockState(data.cr_x, y_in_chunk, data.cr_z, state);
   }

   public IBlockState getBlock(WE_GeneratorData data, int y_in_chunk) {
      return data.primer.getBlockState(data.cr_x, y_in_chunk, data.cr_z);
   }

   public WE_Biome getBiome(WE_GeneratorData data) {
      return data.biomes[data.cr_x][data.cr_z];
   }
}
