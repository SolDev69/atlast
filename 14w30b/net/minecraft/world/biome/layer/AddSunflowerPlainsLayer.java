package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;

public class AddSunflowerPlainsLayer extends Layer {
   public AddSunflowerPlainsLayer(long seed, Layer parent) {
      super(seed);
      this.parent = parent;
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int[] var5 = this.parent.nextValues(x - 1, z - 1, width + 2, length + 2);
      int[] var6 = IntArrays.get(width * length);

      for(int var7 = 0; var7 < length; ++var7) {
         for(int var8 = 0; var8 < width; ++var8) {
            this.setChunkSeed((long)(var8 + x), (long)(var7 + z));
            int var9 = var5[var8 + 1 + (var7 + 1) * (width + 2)];
            if (this.nextInt(57) == 0) {
               if (var9 == Biome.PLAINS.id) {
                  var6[var8 + var7 * width] = Biome.PLAINS.id + 128;
               } else {
                  var6[var8 + var7 * width] = var9;
               }
            } else {
               var6[var8 + var7 * width] = var9;
            }
         }
      }

      return var6;
   }
}
