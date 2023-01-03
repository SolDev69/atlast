package net.minecraft.world.gen.noise;

import java.util.Random;

public class SimplexNoiseSampler {
   private static int[][] gradient3D = new int[][]{
      {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
   };
   public static final double SQRT_THREE = Math.sqrt(3.0);
   private int[] permutations = new int[512];
   public double originX;
   public double originY;
   public double originZ;
   private static final double SKEW_FACTOR_2D = 0.5 * (SQRT_THREE - 1.0);
   private static final double UNSKEW_FACTOR_2D = (3.0 - SQRT_THREE) / 6.0;

   public SimplexNoiseSampler() {
      this(new Random());
   }

   public SimplexNoiseSampler(Random random) {
      this.originX = random.nextDouble() * 256.0;
      this.originY = random.nextDouble() * 256.0;
      this.originZ = random.nextDouble() * 256.0;
      int var2 = 0;

      while(var2 < 256) {
         this.permutations[var2] = var2++;
      }

      for(int var5 = 0; var5 < 256; ++var5) {
         int var3 = random.nextInt(256 - var5) + var5;
         int var4 = this.permutations[var5];
         this.permutations[var5] = this.permutations[var3];
         this.permutations[var3] = var4;
         this.permutations[var5 + 256] = this.permutations[var5];
      }
   }

   private static int fastFloor(double x) {
      return x > 0.0 ? (int)x : (int)x - 1;
   }

   private static double dot(int[] noise, double x, double z) {
      return (double)noise[0] * x + (double)noise[1] * z;
   }

   public double sample(double x, double z) {
      double var11 = 0.5 * (SQRT_THREE - 1.0);
      double var13 = (x + z) * var11;
      int var15 = fastFloor(x + var13);
      int var16 = fastFloor(z + var13);
      double var17 = (3.0 - SQRT_THREE) / 6.0;
      double var19 = (double)(var15 + var16) * var17;
      double var21 = (double)var15 - var19;
      double var23 = (double)var16 - var19;
      double var25 = x - var21;
      double var27 = z - var23;
      byte var29;
      byte var30;
      if (var25 > var27) {
         var29 = 1;
         var30 = 0;
      } else {
         var29 = 0;
         var30 = 1;
      }

      double var31 = var25 - (double)var29 + var17;
      double var33 = var27 - (double)var30 + var17;
      double var35 = var25 - 1.0 + 2.0 * var17;
      double var37 = var27 - 1.0 + 2.0 * var17;
      int var39 = var15 & 0xFF;
      int var40 = var16 & 0xFF;
      int var41 = this.permutations[var39 + this.permutations[var40]] % 12;
      int var42 = this.permutations[var39 + var29 + this.permutations[var40 + var30]] % 12;
      int var43 = this.permutations[var39 + 1 + this.permutations[var40 + 1]] % 12;
      double var44 = 0.5 - var25 * var25 - var27 * var27;
      double var5;
      if (var44 < 0.0) {
         var5 = 0.0;
      } else {
         var44 *= var44;
         var5 = var44 * var44 * dot(gradient3D[var41], var25, var27);
      }

      double var46 = 0.5 - var31 * var31 - var33 * var33;
      double var7;
      if (var46 < 0.0) {
         var7 = 0.0;
      } else {
         var46 *= var46;
         var7 = var46 * var46 * dot(gradient3D[var42], var31, var33);
      }

      double var48 = 0.5 - var35 * var35 - var37 * var37;
      double var9;
      if (var48 < 0.0) {
         var9 = 0.0;
      } else {
         var48 *= var48;
         var9 = var48 * var48 * dot(gradient3D[var43], var35, var37);
      }

      return 70.0 * (var5 + var7 + var9);
   }

   public void addNoise(double[] noise, double x, double z, int sizeX, int sizeZ, double scaleX, double scaleZ, double scaleExponent) {
      int var14 = 0;

      for(int var15 = 0; var15 < sizeZ; ++var15) {
         double var16 = (z + (double)var15) * scaleZ + this.originY;

         for(int var18 = 0; var18 < sizeX; ++var18) {
            double var19 = (x + (double)var18) * scaleX + this.originX;
            double var27 = (var19 + var16) * SKEW_FACTOR_2D;
            int var29 = fastFloor(var19 + var27);
            int var30 = fastFloor(var16 + var27);
            double var31 = (double)(var29 + var30) * UNSKEW_FACTOR_2D;
            double var33 = (double)var29 - var31;
            double var35 = (double)var30 - var31;
            double var37 = var19 - var33;
            double var39 = var16 - var35;
            byte var41;
            byte var42;
            if (var37 > var39) {
               var41 = 1;
               var42 = 0;
            } else {
               var41 = 0;
               var42 = 1;
            }

            double var43 = var37 - (double)var41 + UNSKEW_FACTOR_2D;
            double var45 = var39 - (double)var42 + UNSKEW_FACTOR_2D;
            double var47 = var37 - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
            double var49 = var39 - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
            int var51 = var29 & 0xFF;
            int var52 = var30 & 0xFF;
            int var53 = this.permutations[var51 + this.permutations[var52]] % 12;
            int var54 = this.permutations[var51 + var41 + this.permutations[var52 + var42]] % 12;
            int var55 = this.permutations[var51 + 1 + this.permutations[var52 + 1]] % 12;
            double var56 = 0.5 - var37 * var37 - var39 * var39;
            double var21;
            if (var56 < 0.0) {
               var21 = 0.0;
            } else {
               var56 *= var56;
               var21 = var56 * var56 * dot(gradient3D[var53], var37, var39);
            }

            double var58 = 0.5 - var43 * var43 - var45 * var45;
            double var23;
            if (var58 < 0.0) {
               var23 = 0.0;
            } else {
               var58 *= var58;
               var23 = var58 * var58 * dot(gradient3D[var54], var43, var45);
            }

            double var60 = 0.5 - var47 * var47 - var49 * var49;
            double var25;
            if (var60 < 0.0) {
               var25 = 0.0;
            } else {
               var60 *= var60;
               var25 = var60 * var60 * dot(gradient3D[var55], var47, var49);
            }

            int var10001 = var14++;
            noise[var10001] += 70.0 * (var21 + var23 + var25) * scaleExponent;
         }
      }
   }
}
