package asmodeuscore.core.utils.worldengine.perlinnoise;

public class PerlinNoise extends GenNoiseTemplate {
   public PerlinNoise(long gSeed, double gPersistence, int numOfOctaves, double scl_x, double scl_y, double scl_z, int sum, byte interpolation) {
      super(gSeed, gPersistence, numOfOctaves, scl_x, scl_y, scl_z, sum, interpolation);
   }

   public PerlinNoise(long gSeed, double gPersistence, int numOfOctaves, double scl_xz, double scl_y, int sum) {
      super(gSeed, gPersistence, numOfOctaves, scl_xz, scl_y, sum);
   }

   @Override
   public double noiseOctave2d(double x, double z) {
      long squareStartX = (long)x;
      long squareStartZ = (long)z;
      long xs = 1L;
      long zs = 1L;
      if (Math.abs(x) != x) {
         xs = -1L;
      }

      if (Math.abs(z) != z) {
         zs = -1L;
      }

      double pointInQuadX = Math.abs(x) - (double)Math.abs(squareStartX);
      double pointInQuadZ = Math.abs(z) - (double)Math.abs(squareStartZ);
      double[] topLeft = this.rand.smartVecGen2d(squareStartX, squareStartZ);
      double[] topRight = this.rand.smartVecGen2d(squareStartX + xs, squareStartZ);
      double[] bottomLeft = this.rand.smartVecGen2d(squareStartX, squareStartZ + zs);
      double[] bottomRight = this.rand.smartVecGen2d(squareStartX + xs, squareStartZ + zs);
      double[] distanceToTopLeft = new double[]{(double)xs * pointInQuadX, (double)zs * pointInQuadZ};
      double[] distanceToTopRight = new double[]{(double)xs * pointInQuadX - (double)xs, (double)zs * pointInQuadZ};
      double[] distanceToBottomLeft = new double[]{(double)xs * pointInQuadX, (double)zs * pointInQuadZ - (double)zs};
      double[] distanceToBottomRight = new double[]{(double)xs * pointInQuadX - (double)xs, (double)zs * pointInQuadZ - (double)zs};
      double dotTopLeft = this.dot2d(topLeft, distanceToTopLeft);
      double dotTopRight = this.dot2d(topRight, distanceToTopRight);
      double dotBottomLeft = this.dot2d(bottomLeft, distanceToBottomLeft);
      double dotBottomRight = this.dot2d(bottomRight, distanceToBottomRight);
      double line1 = this.lerp3d(dotTopLeft, dotTopRight, this.autoSmooth1d(pointInQuadX));
      double line2 = this.lerp3d(dotBottomLeft, dotBottomRight, this.autoSmooth1d(pointInQuadX));
      return this.lerp3d(line1, line2, this.autoSmooth1d(pointInQuadZ));
   }

   @Override
   public float noiseOctave2f(float x, float z) {
      int squareStartX = (int)x;
      int squareStartZ = (int)z;
      int xs = 1;
      int zs = 1;
      if (Math.abs(x) != x) {
         xs = -1;
      }

      if (Math.abs(z) != z) {
         zs = -1;
      }

      float pointInQuadX = Math.abs(x) - (float)Math.abs(squareStartX);
      float pointInQuadZ = Math.abs(z) - (float)Math.abs(squareStartZ);
      float[] topLeft = this.rand.smartVecGen2f(squareStartX, squareStartZ);
      float[] topRight = this.rand.smartVecGen2f(squareStartX + xs, squareStartZ);
      float[] bottomLeft = this.rand.smartVecGen2f(squareStartX, squareStartZ + zs);
      float[] bottomRight = this.rand.smartVecGen2f(squareStartX + xs, squareStartZ + zs);
      float[] distanceToTopLeft = new float[]{(float)xs * pointInQuadX, (float)zs * pointInQuadZ};
      float[] distanceToTopRight = new float[]{(float)xs * pointInQuadX - (float)xs, (float)zs * pointInQuadZ};
      float[] distanceToBottomLeft = new float[]{(float)xs * pointInQuadX, (float)zs * pointInQuadZ - (float)zs};
      float[] distanceToBottomRight = new float[]{(float)xs * pointInQuadX - (float)xs, (float)zs * pointInQuadZ - (float)zs};
      float dotTopLeft = this.dot2f(topLeft, distanceToTopLeft);
      float dotTopRight = this.dot2f(topRight, distanceToTopRight);
      float dotBottomLeft = this.dot2f(bottomLeft, distanceToBottomLeft);
      float dotBottomRight = this.dot2f(bottomRight, distanceToBottomRight);
      float line1 = this.lerp3f(dotTopLeft, dotTopRight, this.autoSmooth1f(pointInQuadX));
      float line2 = this.lerp3f(dotBottomLeft, dotBottomRight, this.autoSmooth1f(pointInQuadX));
      return this.lerp3f(line1, line2, this.autoSmooth1f(pointInQuadZ));
   }
}
