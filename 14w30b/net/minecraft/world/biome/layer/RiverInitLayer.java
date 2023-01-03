package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.IntArrays;

public class RiverInitLayer extends Layer {
   public RiverInitLayer(long seed, Layer parent) {
      super(seed);
      this.parent = parent;
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int[] var5 = this.parent.nextValues(x, z, width, length);
      int[] var6 = IntArrays.get(width * length);

      for(int var7 = 0; var7 < length; ++var7) {
         for(int var8 = 0; var8 < width; ++var8) {
            this.setChunkSeed((long)(var8 + x), (long)(var7 + z));
            var6[var8 + var7 * width] = var5[var8 + var7 * width] > 0 ? this.nextInt(299999) + 2 : 0;
         }
      }

      return var6;
   }
}
