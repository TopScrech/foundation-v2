package asmodeuscore.core.utils.worldengine.perlinnoise;

import net.minecraft.util.math.MathHelper;

public class WhiteNoise {
   public long seed;

   public WhiteNoise(long genSeed) {
      this.seed = (long)Math.pow((double)genSeed, 11.0) * 17L + 514L;
   }

   public double gen2d(long x, long z) {
      long n = this.seed + x * 4L + z * 341L;
      n = n << 13 ^ n;
      return 1.0 - (double)(n * (n * n * 15731L + 789221L) + 1376312589L & 2147483647L) / 1.0737418E9F;
   }

   public float gen2f(int x, int z) {
      int n = (int)this.seed + x * 4 + z * 341;
      n = n << 13 ^ n;
      return 1.0F - (float)(n * (n * n * 15731 + 789221) + 1376312589 & 2147483647) / 1.0737418E9F;
   }

   public double[] vecGen2d(long x, long z) {
      double angle = this.gen2d(x, z) * Math.PI;
      return new double[]{(double)MathHelper.cos((float)angle), (double)MathHelper.sin((float)angle)};
   }

   public float[] vecGen2f(int x, int z) {
      float angle = this.gen2f(x, z) * (float) Math.PI;
      return new float[]{MathHelper.cos(angle), MathHelper.sin(angle)};
   }

   public double smartGen2d(long x, long z) {
      return this.gen2d(x, z);
   }

   public float smartGen2f(int x, int z) {
      return this.gen2f(x, z);
   }

   public double[] smartVecGen2d(long x, long z) {
      return this.vecGen2d(x, z);
   }

   public float[] smartVecGen2f(int x, int z) {
      return this.vecGen2f(x, z);
   }
}
