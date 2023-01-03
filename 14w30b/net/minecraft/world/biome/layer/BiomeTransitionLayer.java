package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;

public class BiomeTransitionLayer extends Layer {
   public BiomeTransitionLayer(long seed, Layer parent) {
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
            if (!this.compareHillyBiomes(var5, var6, var8, var7, width, var9, Biome.EXTREME_HILLS.id, Biome.EXTREME_HILLS_EDGE.id)
               && !this.compareDryBiomes(var5, var6, var8, var7, width, var9, Biome.MESA_PLATEAU_F.id, Biome.MESA.id)
               && !this.compareDryBiomes(var5, var6, var8, var7, width, var9, Biome.MESA_PLATEAU.id, Biome.MESA.id)
               && !this.compareDryBiomes(var5, var6, var8, var7, width, var9, Biome.MEGA_TAIGA.id, Biome.TAIGA.id)) {
               if (var9 == Biome.DESERT.id) {
                  int var10 = var5[var8 + 1 + (var7 + 1 - 1) * (width + 2)];
                  int var11 = var5[var8 + 1 + 1 + (var7 + 1) * (width + 2)];
                  int var12 = var5[var8 + 1 - 1 + (var7 + 1) * (width + 2)];
                  int var13 = var5[var8 + 1 + (var7 + 1 + 1) * (width + 2)];
                  if (var10 != Biome.ICE_PLAINS.id && var11 != Biome.ICE_PLAINS.id && var12 != Biome.ICE_PLAINS.id && var13 != Biome.ICE_PLAINS.id) {
                     var6[var8 + var7 * width] = var9;
                  } else {
                     var6[var8 + var7 * width] = Biome.EXTREME_HILLS_PLUS.id;
                  }
               } else if (var9 == Biome.SWAMPLAND.id) {
                  int var14 = var5[var8 + 1 + (var7 + 1 - 1) * (width + 2)];
                  int var15 = var5[var8 + 1 + 1 + (var7 + 1) * (width + 2)];
                  int var16 = var5[var8 + 1 - 1 + (var7 + 1) * (width + 2)];
                  int var17 = var5[var8 + 1 + (var7 + 1 + 1) * (width + 2)];
                  if (var14 == Biome.DESERT.id
                     || var15 == Biome.DESERT.id
                     || var16 == Biome.DESERT.id
                     || var17 == Biome.DESERT.id
                     || var14 == Biome.COLD_TAIGA.id
                     || var15 == Biome.COLD_TAIGA.id
                     || var16 == Biome.COLD_TAIGA.id
                     || var17 == Biome.COLD_TAIGA.id
                     || var14 == Biome.ICE_PLAINS.id
                     || var15 == Biome.ICE_PLAINS.id
                     || var16 == Biome.ICE_PLAINS.id
                     || var17 == Biome.ICE_PLAINS.id) {
                     var6[var8 + var7 * width] = Biome.PLAINS.id;
                  } else if (var14 != Biome.JUNGLE.id && var17 != Biome.JUNGLE.id && var15 != Biome.JUNGLE.id && var16 != Biome.JUNGLE.id) {
                     var6[var8 + var7 * width] = var9;
                  } else {
                     var6[var8 + var7 * width] = Biome.JUNGLE_EDGE.id;
                  }
               } else {
                  var6[var8 + var7 * width] = var9;
               }
            }
         }
      }

      return var6;
   }

   private boolean compareHillyBiomes(int[] biome1, int[] biome2, int x, int y, int length, int width, int id1, int id2) {
      if (!areBiomesEqual(width, id1)) {
         return false;
      } else {
         int var9 = biome1[x + 1 + (y + 1 - 1) * (length + 2)];
         int var10 = biome1[x + 1 + 1 + (y + 1) * (length + 2)];
         int var11 = biome1[x + 1 - 1 + (y + 1) * (length + 2)];
         int var12 = biome1[x + 1 + (y + 1 + 1) * (length + 2)];
         if (this.shouldTransition(var9, id1) && this.shouldTransition(var10, id1) && this.shouldTransition(var11, id1) && this.shouldTransition(var12, id1)) {
            biome2[x + y * length] = width;
         } else {
            biome2[x + y * length] = id2;
         }

         return true;
      }
   }

   private boolean compareDryBiomes(int[] biome1, int[] biome2, int x, int y, int length, int width, int id1, int id2) {
      if (width != id1) {
         return false;
      } else {
         int var9 = biome1[x + 1 + (y + 1 - 1) * (length + 2)];
         int var10 = biome1[x + 1 + 1 + (y + 1) * (length + 2)];
         int var11 = biome1[x + 1 - 1 + (y + 1) * (length + 2)];
         int var12 = biome1[x + 1 + (y + 1 + 1) * (length + 2)];
         if (areBiomesEqual(var9, id1) && areBiomesEqual(var10, id1) && areBiomesEqual(var11, id1) && areBiomesEqual(var12, id1)) {
            biome2[x + y * length] = width;
         } else {
            biome2[x + y * length] = id2;
         }

         return true;
      }
   }

   private boolean shouldTransition(int id1, int id2) {
      if (areBiomesEqual(id1, id2)) {
         return true;
      } else {
         Biome var3 = Biome.byId(id1);
         Biome var4 = Biome.byId(id2);
         if (var3 != null && var4 != null) {
            Biome.TemperatureCategory var5 = var3.getTemperatureCategory();
            Biome.TemperatureCategory var6 = var4.getTemperatureCategory();
            return var5 == var6 || var5 == Biome.TemperatureCategory.MEDIUM || var6 == Biome.TemperatureCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
