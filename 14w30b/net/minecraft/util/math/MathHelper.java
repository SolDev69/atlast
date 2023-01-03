package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MathHelper {
   public static final float SQRT_TWO = sqrt(2.0F);
   private static final float[] SINE_TABLE = new float[65536];
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION;

   public static float sin(float x) {
      return SINE_TABLE[(int)(x * 10430.378F) & 65535];
   }

   public static float cos(float x) {
      return SINE_TABLE[(int)(x * 10430.378F + 16384.0F) & 65535];
   }

   public static float sqrt(float x) {
      return (float)Math.sqrt((double)x);
   }

   public static float sqrt(double x) {
      return (float)Math.sqrt(x);
   }

   public static int floor(float x) {
      int var1 = (int)x;
      return x < (float)var1 ? var1 - 1 : var1;
   }

   @Environment(EnvType.CLIENT)
   public static int fastFloor(double x) {
      return (int)(x + 1024.0) - 1024;
   }

   public static int floor(double x) {
      int var2 = (int)x;
      return x < (double)var2 ? var2 - 1 : var2;
   }

   public static long lfloor(double x) {
      long var2 = (long)x;
      return x < (double)var2 ? var2 - 1L : var2;
   }

   @Environment(EnvType.CLIENT)
   public static int abs(double x) {
      return (int)(x >= 0.0 ? x : -x + 1.0);
   }

   public static float abs(float x) {
      return x >= 0.0F ? x : -x;
   }

   public static int abs(int x) {
      return x >= 0 ? x : -x;
   }

   public static int ceil(float x) {
      int var1 = (int)x;
      return x > (float)var1 ? var1 + 1 : var1;
   }

   public static int ceil(double x) {
      int var2 = (int)x;
      return x > (double)var2 ? var2 + 1 : var2;
   }

   public static int clamp(int x, int min, int max) {
      if (x < min) {
         return min;
      } else {
         return x > max ? max : x;
      }
   }

   public static float clamp(float x, float min, float max) {
      if (x < min) {
         return min;
      } else {
         return x > max ? max : x;
      }
   }

   public static double clamp(double x, double min, double max) {
      if (x < min) {
         return min;
      } else {
         return x > max ? max : x;
      }
   }

   public static double clampedLerp(double min, double max, double delta) {
      if (delta < 0.0) {
         return min;
      } else {
         return delta > 1.0 ? max : min + (max - min) * delta;
      }
   }

   public static double absMax(double a, double b) {
      if (a < 0.0) {
         a = -a;
      }

      if (b < 0.0) {
         b = -b;
      }

      return a > b ? a : b;
   }

   @Environment(EnvType.CLIENT)
   public static int floorDiv(int a, int b) {
      return a < 0 ? -((-a - 1) / b) - 1 : a / b;
   }

   @Environment(EnvType.CLIENT)
   public static boolean isEmpty(String string) {
      return string == null || string.length() == 0;
   }

   public static int nextInt(Random random, int min, int max) {
      return min >= max ? min : random.nextInt(max - min + 1) + min;
   }

   public static float nextFloat(Random random, float min, float max) {
      return min >= max ? min : random.nextFloat() * (max - min) + min;
   }

   public static double nextDouble(Random random, double min, double max) {
      return min >= max ? min : random.nextDouble() * (max - min) + min;
   }

   public static double average(long[] s) {
      long var1 = 0L;

      for(long var6 : s) {
         var1 += var6;
      }

      return (double)var1 / (double)s.length;
   }

   @Environment(EnvType.CLIENT)
   public static boolean equalsApproximate(float a, float b) {
      float var2 = abs(b - a);
      return var2 < 1.0E-5F;
   }

   @Environment(EnvType.CLIENT)
   public static int floorMod(int dividend, int divisor) {
      return (dividend % divisor + divisor) % divisor;
   }

   public static float wrapDegrees(float degrees) {
      degrees %= 360.0F;
      if (degrees >= 180.0F) {
         degrees -= 360.0F;
      }

      if (degrees < -180.0F) {
         degrees += 360.0F;
      }

      return degrees;
   }

   public static double wrapDegrees(double degrees) {
      degrees %= 360.0;
      if (degrees >= 180.0) {
         degrees -= 360.0;
      }

      if (degrees < -180.0) {
         degrees += 360.0;
      }

      return degrees;
   }

   public static int parseInt(String s, int defaultValue) {
      int var2 = defaultValue;

      try {
         var2 = Integer.parseInt(s);
      } catch (Throwable var4) {
      }

      return var2;
   }

   public static int parseInt(String s, int defaultValue, int min) {
      int var3 = defaultValue;

      try {
         var3 = Integer.parseInt(s);
      } catch (Throwable var5) {
      }

      if (var3 < min) {
         var3 = min;
      }

      return var3;
   }

   public static double parseDouble(String s, double defaultValue) {
      double var3 = defaultValue;

      try {
         var3 = Double.parseDouble(s);
      } catch (Throwable var6) {
      }

      return var3;
   }

   public static double parseDouble(String s, double defaultValue, double min) {
      double var5 = defaultValue;

      try {
         var5 = Double.parseDouble(s);
      } catch (Throwable var8) {
      }

      if (var5 < min) {
         var5 = min;
      }

      return var5;
   }

   public static int smallestEncompassingPowerOfTwo(int x) {
      int var1 = x - 1;
      var1 |= var1 >> 1;
      var1 |= var1 >> 2;
      var1 |= var1 >> 4;
      var1 |= var1 >> 8;
      var1 |= var1 >> 16;
      return var1 + 1;
   }

   private static boolean isPowerOfTwo(int x) {
      return x != 0 && (x & x - 1) == 0;
   }

   private static int log2DeBruijn(int x) {
      x = isPowerOfTwo(x) ? x : smallestEncompassingPowerOfTwo(x);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)x * 125613361L >> 27) & 31];
   }

   public static int log2(int x) {
      return log2DeBruijn(x) - (isPowerOfTwo(x) ? 0 : 1);
   }

   public static int roundUp(int x, int interval) {
      if (interval == 0) {
         return 0;
      } else if (x == 0) {
         return interval;
      } else {
         if (x < 0) {
            interval *= -1;
         }

         int var2 = x % interval;
         return var2 == 0 ? x : x + interval - var2;
      }
   }

   @Environment(EnvType.CLIENT)
   public static int packRGB(float r, float g, float b) {
      return packRGB(floor(r * 255.0F), floor(g * 255.0F), floor(b * 255.0F));
   }

   @Environment(EnvType.CLIENT)
   public static int packRGB(int r, int g, int b) {
      int var3 = (r << 8) + g;
      return (var3 << 8) + b;
   }

   @Environment(EnvType.CLIENT)
   public static int mulARGB(int argb1, int argb2) {
      int var2 = (argb1 & 0xFF0000) >> 16;
      int var3 = (argb2 & 0xFF0000) >> 16;
      int var4 = (argb1 & 0xFF00) >> 8;
      int var5 = (argb2 & 0xFF00) >> 8;
      int var6 = (argb1 & 0xFF) >> 0;
      int var7 = (argb2 & 0xFF) >> 0;
      int var8 = (int)((float)var2 * (float)var3 / 255.0F);
      int var9 = (int)((float)var4 * (float)var5 / 255.0F);
      int var10 = (int)((float)var6 * (float)var7 / 255.0F);
      return argb1 & 0xFF000000 | var8 << 16 | var9 << 8 | var10;
   }

   @Environment(EnvType.CLIENT)
   public static long hashCode(Vec3i vec) {
      return hashCode(vec.getX(), vec.getY(), vec.getZ());
   }

   @Environment(EnvType.CLIENT)
   public static long hashCode(int x, int y, int z) {
      long var3 = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
      return var3 * var3 * 42317861L + var3 * 11L;
   }

   public static UUID nextUuid(Random random) {
      long var1 = random.nextLong() & -61441L | 16384L;
      long var3 = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(var1, var3);
   }

   static {
      for(int var0 = 0; var0 < 65536; ++var0) {
         SINE_TABLE[var0] = (float)Math.sin((double)var0 * Math.PI * 2.0 / 65536.0);
      }

      MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
         0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
      };
   }
}
