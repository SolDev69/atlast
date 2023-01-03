package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.IntArrays;

public class AddSnowLayer extends Layer {
   public AddSnowLayer(long seed, Layer parent) {
      super(seed);
      this.parent = parent;
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
            int var13 = var9[var12 + 1 + (var11 + 1) * var7];
            this.setChunkSeed((long)(var12 + x), (long)(var11 + z));
            if (var13 == 0) {
               var10[var12 + var11 * width] = 0;
            } else {
               int var14 = this.nextInt(6);
               byte var15;
               if (var14 == 0) {
                  var15 = 4;
               } else if (var14 <= 1) {
                  var15 = 3;
               } else {
                  var15 = 1;
               }

               var10[var12 + var11 * width] = var15;
            }
         }
      }

      return var10;
   }
}
