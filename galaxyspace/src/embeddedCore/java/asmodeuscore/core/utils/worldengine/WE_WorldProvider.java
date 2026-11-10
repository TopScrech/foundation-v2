package asmodeuscore.core.utils.worldengine;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class WE_WorldProvider extends WorldProvider {
   float rainfall = 0.1F;
   public WE_ChunkProvider chunk_provider;

   public void init() {
      System.out.println("////////////////////////////////////-");
      System.out.println("//#===============================//=* Version: 1.12.2-1.0.0");
      System.out.println("//#=-------| WorldEngine |-------=//=* By Vamig Aliev (vk.com/win_vista).");
      System.out.println("//#===============================//=* Port by BlesseNtumble.");
      System.out.println("////////////////////////////////////-");
   }

   public IChunkGenerator createChunkGenerator() {
      System.out.println("WorldEngine: -Starting WorldEngine...");
      this.chunk_provider = new WE_ChunkProvider(this);
      System.out.println("WorldEngine: -WorldEngine started successfully!");
      return this.chunk_provider;
   }

   public abstract void genSettings(WE_ChunkProvider var1);

   public abstract void onChunkProvider(int var1, int var2, ChunkPrimer var3);

   public abstract void onPopulate(int var1, int var2);

   public abstract void recreateStructures(Chunk var1, int var2, int var3);

   public boolean isColorWorld() {
      return false;
   }
}
