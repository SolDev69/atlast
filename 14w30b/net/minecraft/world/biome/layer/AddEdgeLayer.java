package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.IntArrays;

public class AddEdgeLayer extends Layer {
   private final AddEdgeLayer.Mode mode;

   public AddEdgeLayer(long seed, Layer parent, AddEdgeLayer.Mode mode) {
      super(seed);
      this.parent = parent;
      this.mode = mode;
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      switch(this.mode) {
         case COOL_WARM:
         default:
            return this.nextValuesCooldWarm(x, z, width, length);
         case HEAT_ICE:
            return this.nextValuesHeatIce(x, z, width, length);
         case SPECIAL:
            return this.nextValuesSpecial(x, z, width, length);
      }
   }

   private int[] nextValuesCooldWarm(int x, int z, int width, int length) {
      int var5 = x - 1;
      int var6 = z - 1;
      int var7 = 1 + width + 1;
      int var8 = 1 + length + 1;
      int[] var9 = this.parent.nextValues(var5, var6, var7, var8);
      int[] var10 = IntArrays.get(width * length);

      for(int var11 = 0; var11 < length; ++var11) {
         for(int var12 = 0; var12 < width; ++var12) {
            this.setChunkSeed((long)(var12 + x), (long)(var11 + z));
            int var13 = var9[var12 + 1 + (var11 + 1) * var7];
            if (var13 == 1) {
               int var14 = var9[var12 + 1 + (var11 + 1 - 1) * var7];
               int var15 = var9[var12 + 1 + 1 + (var11 + 1) * var7];
               int var16 = var9[var12 + 1 - 1 + (var11 + 1) * var7];
               int var17 = var9[var12 + 1 + (var11 + 1 + 1) * var7];
               boolean var18 = var14 == 3 || var15 == 3 || var16 == 3 || var17 == 3;
               boolean var19 = var14 == 4 || var15 == 4 || var16 == 4 || var17 == 4;
               if (var18 || var19) {
                  var13 = 2;
               }
            }

            var10[var12 + var11 * width] = var13;
         }
      }

      return var10;
   }

   private int[] nextValuesHeatIce(int x, int z, int width, int length) {
      int var5 = x - 1;
      int var6 = z - 1;
      int var7 = 1 + width + 1;
      int var8 = 1 + length + 1;
      int[] var9 = this.parent.nextValues(var5, var6, var7, var8);
      int[] var10 = IntArrays.get(width * length);

      for(int var11 = 0; var11 < length; ++var11) {
         for(int var12 = 0; var12 < width; ++var12) {
            int var13 = var9[var12 + 1 + (var11 + 1) * var7];
            if (var13 == 4) {
               int var14 = var9[var12 + 1 + (var11 + 1 - 1) * var7];
               int var15 = var9[var12 + 1 + 1 + (var11 + 1) * var7];
               int var16 = var9[var12 + 1 - 1 + (var11 + 1) * var7];
               int var17 = var9[var12 + 1 + (var11 + 1 + 1) * var7];
               boolean var18 = var14 == 2 || var15 == 2 || var16 == 2 || var17 == 2;
               boolean var19 = var14 == 1 || var15 == 1 || var16 == 1 || var17 == 1;
               if (var19 || var18) {
                  var13 = 3;
               }
            }

            var10[var12 + var11 * width] = var13;
         }
      }

      return var10;
   }

   private int[] nextValuesSpecial(int x, int z, int width, int length) {
      int[] var5 = this.parent.nextValues(x, z, width, length);
      int[] var6 = IntArrays.get(width * length);

      for(int var7 = 0; var7 < length; ++var7) {
         for(int var8 = 0; var8 < width; ++var8) {
            this.setChunkSeed((long)(var8 + x), (long)(var7 + z));
            int var9 = var5[var8 + var7 * width];
            if (var9 != 0 && this.nextInt(13) == 0) {
               var9 |= 1 + this.nextInt(15) << 8 & 3840;
            }

            var6[var8 + var7 * width] = var9;
         }
      }

      return var6;
   }

   public static enum Mode {
      COOL_WARM,
      HEAT_ICE,
      SPECIAL;
   }
}
