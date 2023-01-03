package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;

public class AddMushroomIslandLayer extends Layer {
   public AddMushroomIslandLayer(long seed, Layer parent) {
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
            int var13 = var9[var12 + 0 + (var11 + 0) * var7];
            int var14 = var9[var12 + 2 + (var11 + 0) * var7];
            int var15 = var9[var12 + 0 + (var11 + 2) * var7];
            int var16 = var9[var12 + 2 + (var11 + 2) * var7];
            int var17 = var9[var12 + 1 + (var11 + 1) * var7];
            this.setChunkSeed((long)(var12 + x), (long)(var11 + z));
            if (var17 == 0 && var13 == 0 && var14 == 0 && var15 == 0 && var16 == 0 && this.nextInt(100) == 0) {
               var10[var12 + var11 * width] = Biome.MUSHROOM_ISLAND.id;
            } else {
               var10[var12 + var11 * width] = var17;
            }
         }
      }

      return var10;
   }
}
