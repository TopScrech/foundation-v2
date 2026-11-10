package asmodeuscore.core.utils.worldengine.standardcustomgen;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WE_OreGen implements IWorldGenerator {
   public List<Integer> fieldMinY = new ArrayList<>();
   public List<Integer> fieldMaxY = new ArrayList<>();
   public List<Integer> fieldMinRadius = new ArrayList<>();
   public List<Integer> fieldMaxRadius = new ArrayList<>();
   public List<Integer> oreInMaxFieldDensity = new ArrayList<>();
   public List<Integer> chunksForField = new ArrayList<>();
   public List<Integer> blockCount = new ArrayList<>();
   public List<WorldGenMinable> oreGen = new ArrayList<>();

   public void add(IBlockState oreBlock, IBlockState replacingBlock, int blockCount, int minY, int maxY, int chunksForField) {
      this.oreGen.add(new WorldGenMinable(oreBlock, blockCount, new WE_OreGen.BlockPredicate(replacingBlock)));
      this.fieldMinY.add(minY);
      this.fieldMaxY.add(maxY);
      this.blockCount.add(blockCount);
      this.chunksForField.add(chunksForField);
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      for (int i = 0; i < this.oreGen.size(); i++) {
         for (int j = 0; j < this.chunksForField.get(i); j++) {
            int sy = this.fieldMinY.get(i) + random.nextInt(this.fieldMaxY.get(i) - this.fieldMinY.get(i));
            int sx = chunkX + random.nextInt(16);
            int sz = chunkZ + random.nextInt(16);
            this.oreGen.get(i).generate(world, random, new BlockPos(sx, sy, sz));
         }
      }
   }

   public static class BlockPredicate implements Predicate<IBlockState> {
      List<IBlockState> states = new ArrayList<>();

      public BlockPredicate(IBlockState... state) {
         for (IBlockState st : state) {
            this.states.add(st);
         }
      }

      public boolean apply(IBlockState p_apply_1_) {
         return p_apply_1_ != null && this.states.contains(p_apply_1_) ? p_apply_1_.isNormalCube() : false;
      }
   }
}
