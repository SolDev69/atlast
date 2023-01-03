package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;

public class RiverLayer extends Layer {
   public RiverLayer(long seed, Layer parent) {
      super(seed);
      super.parent = parent;
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int var5 = x - 1;
      int var6 = z - 1;
      int var7 = width + 2;
      int var8 = length + 2;
      int[] var9 = this.parent.nextValues(var5, var6, var7, var8);
      int[] var10 = IntArrays.get(width * length);

      for(int var11 = 0; var11 < length; ++var11) {
         for(int var12 = 0; var12 < width; ++var12) {
            int var13 = this.makeCurves(var9[var12 + 0 + (var11 + 1) * var7]);
            int var14 = this.makeCurves(var9[var12 + 2 + (var11 + 1) * var7]);
            int var15 = this.makeCurves(var9[var12 + 1 + (var11 + 0) * var7]);
            int var16 = this.makeCurves(var9[var12 + 1 + (var11 + 2) * var7]);
            int var17 = this.makeCurves(var9[var12 + 1 + (var11 + 1) * var7]);
            if (var17 == var13 && var17 == var15 && var17 == var14 && var17 == var16) {
               var10[var12 + var11 * width] = -1;
            } else {
               var10[var12 + var11 * width] = Biome.RIVER.id;
            }
         }
      }

      return var10;
   }

   private int makeCurves(int value) {
      return value >= 2 ? 2 + (value & 1) : value;
   }
}
