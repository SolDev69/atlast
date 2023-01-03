package net.minecraft.world.gen.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class OctaveNoiseGenerator extends NoiseGenerator {
   private SimplexNoiseGenerator[] samplers;
   private int samplerCount;

   public OctaveNoiseGenerator(Random random, int samplerCount) {
      this.samplerCount = samplerCount;
      this.samplers = new SimplexNoiseGenerator[samplerCount];

      for(int var3 = 0; var3 < samplerCount; ++var3) {
         this.samplers[var3] = new SimplexNoiseGenerator(random);
      }
   }

   public double[] getNoise(double[] noise, int x, int y, int z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ) {
      if (noise == null) {
         noise = new double[sizeX * sizeY * sizeZ];
      } else {
         for(int var14 = 0; var14 < noise.length; ++var14) {
            noise[var14] = 0.0;
         }
      }

      double var27 = 1.0;

      for(int var16 = 0; var16 < this.samplerCount; ++var16) {
         double var17 = (double)x * var27 * scaleX;
         double var19 = (double)y * var27 * scaleY;
         double var21 = (double)z * var27 * scaleZ;
         long var23 = MathHelper.lfloor(var17);
         long var25 = MathHelper.lfloor(var21);
         var17 -= (double)var23;
         var21 -= (double)var25;
         var23 %= 16777216L;
         var25 %= 16777216L;
         var17 += (double)var23;
         var21 += (double)var25;
         this.samplers[var16].getNoise(noise, var17, var19, var21, sizeX, sizeY, sizeZ, scaleX * var27, scaleY * var27, scaleZ * var27, var27);
         var27 /= 2.0;
      }

      return noise;
   }

   public double[] getNoise(double[] noise, int x, int y, int z, int sizeZ, double saleX, double scaleZ, double scaleExponent) {
      return this.getNoise(noise, x, 10, y, z, 1, sizeZ, saleX, 1.0, scaleZ);
   }
}
