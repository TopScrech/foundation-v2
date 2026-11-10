package asmodeuscore.core.utils.worldengine;

public class WE_PerlinNoise {
   public static double NumberNoise2D(long seed, long x, long y) {
      long n = x + y * 31L + seed * 11L;
      n = n << 13 ^ n;
      return 1.0 - (double)(n * (n * n * 15731L + 789221L) + 1376312589L & 2147483647L) / 1.0737418E9F;
   }

   public static double SmoothNoise2D(long seed, long x, long y) {
      double corners = (
            NumberNoise2D(seed, x - 1L, y - 1L)
               + NumberNoise2D(seed, x + 1L, y - 1L)
               + NumberNoise2D(seed, x - 1L, y + 1L)
               + NumberNoise2D(seed, x + 1L, y + 1L)
         )
         / 16.0;
      double sides = (NumberNoise2D(seed, x - 1L, y) + NumberNoise2D(seed, x + 1L, y) + NumberNoise2D(seed, x, y - 1L) + NumberNoise2D(seed, x, y + 1L)) / 8.0;
      double center = NumberNoise2D(seed, x, y) / 4.0;
      return corners + sides + center;
   }

   public static double CosineInterpolate(double a, double b, double x) {
      double f = x * x * x * (x * -9.0 + 10.0);
      return a * (1.0 - f) + b * f;
   }

   public static double CosineInterpolatedNoise2D(long seed, double x, double y) {
      long lx = (long)x;
      long ly = (long)y;
      double fx = Math.abs(x) - (double)Math.abs(lx);
      double fy = Math.abs(y) - (double)Math.abs(ly);
      double v1 = SmoothNoise2D(seed, lx, ly);
      double v2 = SmoothNoise2D(seed, Math.abs(x) == x ? lx + 1L : lx - 1L, ly);
      double v3 = SmoothNoise2D(seed, lx, Math.abs(y) == y ? ly + 1L : ly - 1L);
      double v4 = SmoothNoise2D(seed, Math.abs(x) == x ? lx + 1L : lx - 1L, Math.abs(y) == y ? ly + 1L : ly - 1L);
      double i1 = CosineInterpolate(v1, v2, fx);
      double i2 = CosineInterpolate(v3, v4, fx);
      return CosineInterpolate(i1, i2, fy);
   }

   public static double PerlinNoise2D(long seed, double x, double y, double persistence, int number_of_octaves) {
      double total = 0.0;
      double amplitude = 0.0;
      double frequency = 0.0;

      for (int i = 0; i <= number_of_octaves; i++) {
         frequency = (double)(1 << i);
         amplitude = Math.pow(persistence, (double)i);
         total += CosineInterpolatedNoise2D(seed, x * frequency, y * frequency) * amplitude;
      }

      return total;
   }
}
