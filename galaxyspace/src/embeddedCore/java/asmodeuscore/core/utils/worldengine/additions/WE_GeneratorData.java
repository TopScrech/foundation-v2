package asmodeuscore.core.utils.worldengine.additions;

import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider;
import net.minecraft.world.chunk.ChunkPrimer;

public class WE_GeneratorData {
   public WE_ChunkProvider chunkProvider;
   public ChunkPrimer primer;
   public long chunk_X;
   public long chunk_Z;
   public WE_Biome[][] biomes;
   public int cr_x;
   public int cr_y;
   public int cr_z;

   public WE_GeneratorData(WE_ChunkProvider cp, ChunkPrimer primer, long cx, long cz, WE_Biome[][] b, int x, int y, int z) {
      this.chunkProvider = cp;
      this.primer = primer;
      this.chunk_X = cx;
      this.chunk_Z = cz;
      this.biomes = b;
      this.cr_x = x;
      this.cr_y = y;
      this.cr_z = z;
   }
}
