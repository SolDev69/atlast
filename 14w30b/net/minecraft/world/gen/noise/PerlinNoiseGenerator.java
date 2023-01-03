package net.minecraft.world.gen.noise;

import java.util.Random;

public class PerlinNoiseGenerator extends NoiseGenerator {
   private SimplexNoiseSampler[] samplers;
   private int samplerCount;

   public PerlinNoiseGenerator(Random random, int samplerCount) {
      this.samplerCount = samplerCount;
      this.samplers = new SimplexNoiseSampler[samplerCount];

      for(int var3 = 0; var3 < samplerCount; ++var3) {
         this.samplers[var3] = new SimplexNoiseSampler(random);
      }
   }

   public double getNoise(double x, double z) {
      double var5 = 0.0;
      double var7 = 1.0;

      for(int var9 = 0; var9 < this.samplerCount; ++var9) {
         var5 += this.samplers[var9].sample(x * var7, z * var7) / var7;
         var7 /= 2.0;
      }

      return var5;
   }

   public double[] getNoise(double[] noise, double x, double z, int sizeX, int sizeZ, double scaleX, double scaleZ, double scaleExponent) {
      return this.getNoise(noise, x, z, sizeX, sizeZ, scaleX, scaleZ, scaleExponent, 0.5);
   }

   public double[] getNoise(
      double[] noise, double x, double z, int sizeX, int sizeZ, double scaleX, double scaleZ, double scaleExponentX, double scaleExponentZ
   ) {
      if (noise != null && noise.length >= sizeX * sizeZ) {
         for(int var16 = 0; var16 < noise.length; ++var16) {
            noise[var16] = 0.0;
         }
      } else {
         noise = new double[sizeX * sizeZ];
      }

      double var21 = 1.0;
      double var18 = 1.0;

      for(int var20 = 0; var20 < this.samplerCount; ++var20) {
         this.samplers[var20].addNoise(noise, x, z, sizeX, sizeZ, scaleX * var18 * var21, scaleZ * var18 * var21, 0.55 / var21);
         var18 *= scaleExponentX;
         var21 *= scaleExponentZ;
      }

      return noise;
   }
}
