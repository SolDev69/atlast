package net.minecraft.world.gen.noise;

import java.util.Random;

public class SimplexNoiseGenerator extends NoiseGenerator {
   private int[] permutations = new int[512];
   public double x;
   public double y;
   public double z;
   private static final double[] f_49rfpnzju = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] f_63oeqtzrk = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   private static final double[] f_36caebfeo = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final double[] f_88puzzhyh = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] f_40bjmiqza = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

   public SimplexNoiseGenerator() {
      this(new Random());
   }

   public SimplexNoiseGenerator(Random random) {
      this.x = random.nextDouble() * 256.0;
      this.y = random.nextDouble() * 256.0;
      this.z = random.nextDouble() * 256.0;
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

   public final double lerp(double delta, double min, double max) {
      return min + delta * (max - min);
   }

   public final double gradient(int hash, double x, double z) {
      int var6 = hash & 15;
      return f_88puzzhyh[var6] * x + f_40bjmiqza[var6] * z;
   }

   public final double gradient(int hash, double x, double y, double z) {
      int var8 = hash & 15;
      return f_49rfpnzju[var8] * x + f_63oeqtzrk[var8] * y + f_36caebfeo[var8] * z;
   }

   public void getNoise(
      double[] noise, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double scaleExponent
   ) {
      if (sizeY == 1) {
         int var64 = 0;
         int var66 = 0;
         int var21 = 0;
         int var69 = 0;
         double var72 = 0.0;
         double var76 = 0.0;
         int var80 = 0;
         double var82 = 1.0 / scaleExponent;

         for(int var30 = 0; var30 < sizeX; ++var30) {
            double var83 = x + (double)var30 * scaleX + this.x;
            int var85 = (int)var83;
            if (var83 < (double)var85) {
               --var85;
            }

            int var34 = var85 & 0xFF;
            var83 -= (double)var85;
            double var86 = var83 * var83 * var83 * (var83 * (var83 * 6.0 - 15.0) + 10.0);

            for(int var87 = 0; var87 < sizeZ; ++var87) {
               double var89 = z + (double)var87 * scaleZ + this.z;
               int var91 = (int)var89;
               if (var89 < (double)var91) {
                  --var91;
               }

               int var92 = var91 & 0xFF;
               var89 -= (double)var91;
               double var93 = var89 * var89 * var89 * (var89 * (var89 * 6.0 - 15.0) + 10.0);
               var64 = this.permutations[var34] + 0;
               var66 = this.permutations[var64] + var92;
               var21 = this.permutations[var34 + 1] + 0;
               var69 = this.permutations[var21] + var92;
               var72 = this.lerp(var86, this.gradient(this.permutations[var66], var83, var89), this.gradient(this.permutations[var69], var83 - 1.0, 0.0, var89));
               var76 = this.lerp(
                  var86,
                  this.gradient(this.permutations[var66 + 1], var83, 0.0, var89 - 1.0),
                  this.gradient(this.permutations[var69 + 1], var83 - 1.0, 0.0, var89 - 1.0)
               );
               double var94 = this.lerp(var93, var72, var76);
               int var97 = var80++;
               noise[var97] += var94 * var82;
            }
         }
      } else {
         int var19 = 0;
         double var20 = 1.0 / scaleExponent;
         int var22 = -1;
         int var23 = 0;
         int var24 = 0;
         int var25 = 0;
         int var26 = 0;
         int var27 = 0;
         int var28 = 0;
         double var29 = 0.0;
         double var31 = 0.0;
         double var33 = 0.0;
         double var35 = 0.0;

         for(int var37 = 0; var37 < sizeX; ++var37) {
            double var38 = x + (double)var37 * scaleX + this.x;
            int var40 = (int)var38;
            if (var38 < (double)var40) {
               --var40;
            }

            int var41 = var40 & 0xFF;
            var38 -= (double)var40;
            double var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);

            for(int var44 = 0; var44 < sizeZ; ++var44) {
               double var45 = z + (double)var44 * scaleZ + this.z;
               int var47 = (int)var45;
               if (var45 < (double)var47) {
                  --var47;
               }

               int var48 = var47 & 0xFF;
               var45 -= (double)var47;
               double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0 - 15.0) + 10.0);

               for(int var51 = 0; var51 < sizeY; ++var51) {
                  double var52 = y + (double)var51 * scaleY + this.y;
                  int var54 = (int)var52;
                  if (var52 < (double)var54) {
                     --var54;
                  }

                  int var55 = var54 & 0xFF;
                  var52 -= (double)var54;
                  double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0 - 15.0) + 10.0);
                  if (var51 == 0 || var55 != var22) {
                     var22 = var55;
                     var23 = this.permutations[var41] + var55;
                     var24 = this.permutations[var23] + var48;
                     var25 = this.permutations[var23 + 1] + var48;
                     var26 = this.permutations[var41 + 1] + var55;
                     var27 = this.permutations[var26] + var48;
                     var28 = this.permutations[var26 + 1] + var48;
                     var29 = this.lerp(
                        var42, this.gradient(this.permutations[var24], var38, var52, var45), this.gradient(this.permutations[var27], var38 - 1.0, var52, var45)
                     );
                     var31 = this.lerp(
                        var42,
                        this.gradient(this.permutations[var25], var38, var52 - 1.0, var45),
                        this.gradient(this.permutations[var28], var38 - 1.0, var52 - 1.0, var45)
                     );
                     var33 = this.lerp(
                        var42,
                        this.gradient(this.permutations[var24 + 1], var38, var52, var45 - 1.0),
                        this.gradient(this.permutations[var27 + 1], var38 - 1.0, var52, var45 - 1.0)
                     );
                     var35 = this.lerp(
                        var42,
                        this.gradient(this.permutations[var25 + 1], var38, var52 - 1.0, var45 - 1.0),
                        this.gradient(this.permutations[var28 + 1], var38 - 1.0, var52 - 1.0, var45 - 1.0)
                     );
                  }

                  double var58 = this.lerp(var56, var29, var31);
                  double var60 = this.lerp(var56, var33, var35);
                  double var62 = this.lerp(var49, var58, var60);
                  int var10001 = var19++;
                  noise[var10001] += var62 * var20;
               }
            }
         }
      }
   }
}
