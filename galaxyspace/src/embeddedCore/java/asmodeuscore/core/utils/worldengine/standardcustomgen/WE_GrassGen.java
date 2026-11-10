package asmodeuscore.core.utils.worldengine.standardcustomgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WE_GrassGen implements IWorldGenerator {
   public List<IBlockState> flowerList = new ArrayList<>();
   public List<Integer> blocksForFlower = new ArrayList<>();
   public List<Boolean> waterGen = new ArrayList<>();
   public List<IBlockState> surface = new ArrayList<>();

   public void add(IBlockState flower, int count, boolean onWater, IBlockState surf) {
      this.flowerList.add(flower);
      this.blocksForFlower.add(count);
      this.waterGen.add(onWater);
      this.surface.add(surf);
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      for (int i = 0; i < this.flowerList.size(); i++) {
         if (random.nextInt(this.blocksForFlower.get(i)) <= 256) {
            for (int i2 = 0; (float)i2 < (float)this.blocksForFlower.get(i).intValue(); i2++) {
               int x = chunkX + random.nextInt(16) + 8;
               int z = chunkZ + random.nextInt(16) + 8;
               BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
               if (this.waterGen.get(i)) {
                  IBlockState state = world.getBlockState(pos);
                  if (state.getBlock() == this.surface.get(i).getBlock() && world.isAirBlock(pos.up())) {
                     world.setBlockState(pos.up(), this.flowerList.get(i), 2);
                  }
               } else {
                  IBlockState state = world.getBlockState(pos.down());
                  if (state.getBlock() == this.surface.get(i).getBlock()) {
                     world.setBlockState(pos, this.flowerList.get(i), 2);
                  }
               }
            }
         }
      }
   }
}
