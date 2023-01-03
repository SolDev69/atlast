package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.IntArrays;

public class ZoomLayer extends Layer {
   public ZoomLayer(long seed, Layer parent) {
      super(seed);
      super.parent = parent;
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int var5 = x >> 1;
      int var6 = z >> 1;
      int var7 = (width >> 1) + 2;
      int var8 = (length >> 1) + 2;
      int[] var9 = this.parent.nextValues(var5, var6, var7, var8);
      int var10 = var7 - 1 << 1;
      int var11 = var8 - 1 << 1;
      int[] var12 = IntArrays.get(var10 * var11);

      for(int var13 = 0; var13 < var8 - 1; ++var13) {
         int var14 = (var13 << 1) * var10;
         int var15 = 0;
         int var16 = var9[var15 + 0 + (var13 + 0) * var7];

         for(int var17 = var9[var15 + 0 + (var13 + 1) * var7]; var15 < var7 - 1; ++var15) {
            this.setChunkSeed((long)(var15 + var5 << 1), (long)(var13 + var6 << 1));
            int var18 = var9[var15 + 1 + (var13 + 0) * var7];
            int var19 = var9[var15 + 1 + (var13 + 1) * var7];
            var12[var14] = var16;
            var12[var14++ + var10] = this.getRandomInt(new int[]{var16, var17});
            var12[var14] = this.getRandomInt(new int[]{var16, var18});
            var12[var14++ + var10] = this.getModeOrRandom(var16, var18, var17, var19);
            var16 = var18;
            var17 = var19;
         }
      }

      int[] var20 = IntArrays.get(width * length);

      for(int var22 = 0; var22 < length; ++var22) {
         System.arraycopy(var12, (var22 + (z & 1)) * var10 + (x & 1), var20, var22 * width, width);
      }

      return var20;
   }

   public static Layer zoom(long seed, Layer layer, int magnification) {
      Object var4 = layer;

      for(int var5 = 0; var5 < magnification; ++var5) {
         var4 = new ZoomLayer(seed + (long)var5, (Layer)var4);
      }

      return (Layer)var4;
   }
}
