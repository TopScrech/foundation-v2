package asmodeuscore.core.utils.worldengine.additions;

import asmodeuscore.core.utils.worldengine.WE_Biome;
import net.minecraft.block.state.IBlockState;

public abstract class WE_CreateChunkGen {
   public abstract void gen(WE_GeneratorData var1);

   public void setBlock(WE_GeneratorData data, IBlockState state, int x_in_chunk, int y_in_chunk, int z_in_chunk) {
      data.primer.setBlockState(x_in_chunk, y_in_chunk, z_in_chunk, state);
   }

   public IBlockState getBlock(WE_GeneratorData data, int x_in_chunk, int y_in_chunk, int z_in_chunk) {
      return data.primer.getBlockState(x_in_chunk, y_in_chunk, z_in_chunk);
   }

   public WE_Biome getBiome(WE_GeneratorData data, int x_in_chunk, int z_in_chunk) {
      return data.biomes[x_in_chunk][z_in_chunk];
   }
}
