package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;

public class RiverMixLayer extends Layer {
   private Layer biomeLayer;
   private Layer riverLayer;

   public RiverMixLayer(long seed, Layer biomeLayer, Layer riverLayer) {
      super(seed);
      this.biomeLayer = biomeLayer;
      this.riverLayer = riverLayer;
   }

   @Override
   public void setLocalWorldSeed(long worldSeed) {
      this.biomeLayer.setLocalWorldSeed(worldSeed);
      this.riverLayer.setLocalWorldSeed(worldSeed);
      super.setLocalWorldSeed(worldSeed);
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int[] var5 = this.biomeLayer.nextValues(x, z, width, length);
      int[] var6 = this.riverLayer.nextValues(x, z, width, length);
      int[] var7 = IntArrays.get(width * length);

      for(int var8 = 0; var8 < width * length; ++var8) {
         if (var5[var8] == Biome.OCEAN.id || var5[var8] == Biome.DEEP_OCEAN.id) {
            var7[var8] = var5[var8];
         } else if (var6[var8] == Biome.RIVER.id) {
            if (var5[var8] == Biome.ICE_PLAINS.id) {
               var7[var8] = Biome.FROZEN_RIVER.id;
            } else if (var5[var8] != Biome.MUSHROOM_ISLAND.id && var5[var8] != Biome.MUSHROOM_ISLAND_SHORE.id) {
               var7[var8] = var6[var8] & 0xFF;
            } else {
               var7[var8] = Biome.MUSHROOM_ISLAND_SHORE.id;
            }
         } else {
            var7[var8] = var5[var8];
         }
      }

      return var7;
   }
}
