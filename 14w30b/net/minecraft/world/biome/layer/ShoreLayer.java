package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;
import net.minecraft.world.biome.JungleBiome;
import net.minecraft.world.biome.MesaBiome;

public class ShoreLayer extends Layer {
   public ShoreLayer(long seed, Layer parent) {
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
            Biome var10 = Biome.byId(var9);
            if (var9 == Biome.MUSHROOM_ISLAND.id) {
               int var11 = var5[var8 + 1 + (var7 + 1 - 1) * (width + 2)];
               int var12 = var5[var8 + 1 + 1 + (var7 + 1) * (width + 2)];
               int var13 = var5[var8 + 1 - 1 + (var7 + 1) * (width + 2)];
               int var14 = var5[var8 + 1 + (var7 + 1 + 1) * (width + 2)];
               if (var11 != Biome.OCEAN.id && var12 != Biome.OCEAN.id && var13 != Biome.OCEAN.id && var14 != Biome.OCEAN.id) {
                  var6[var8 + var7 * width] = var9;
               } else {
                  var6[var8 + var7 * width] = Biome.MUSHROOM_ISLAND_SHORE.id;
               }
            } else if (var10 != null && var10.getType() == JungleBiome.class) {
               int var17 = var5[var8 + 1 + (var7 + 1 - 1) * (width + 2)];
               int var20 = var5[var8 + 1 + 1 + (var7 + 1) * (width + 2)];
               int var23 = var5[var8 + 1 - 1 + (var7 + 1) * (width + 2)];
               int var26 = var5[var8 + 1 + (var7 + 1 + 1) * (width + 2)];
               if (!this.isJungleEdge(var17) || !this.isJungleEdge(var20) || !this.isJungleEdge(var23) || !this.isJungleEdge(var26)) {
                  var6[var8 + var7 * width] = Biome.JUNGLE_EDGE.id;
               } else if (!isOcean(var17) && !isOcean(var20) && !isOcean(var23) && !isOcean(var26)) {
                  var6[var8 + var7 * width] = var9;
               } else {
                  var6[var8 + var7 * width] = Biome.BEACH.id;
               }
            } else if (var9 == Biome.EXTREME_HILLS.id || var9 == Biome.EXTREME_HILLS_PLUS.id || var9 == Biome.EXTREME_HILLS_EDGE.id) {
               this.createShore(var5, var6, var8, var7, width, var9, Biome.STONE_BEACH.id);
            } else if (var10 != null && var10.isSnowy()) {
               this.createShore(var5, var6, var8, var7, width, var9, Biome.COLD_BEACH.id);
            } else if (var9 == Biome.MESA.id || var9 == Biome.MESA_PLATEAU_F.id) {
               int var16 = var5[var8 + 1 + (var7 + 1 - 1) * (width + 2)];
               int var19 = var5[var8 + 1 + 1 + (var7 + 1) * (width + 2)];
               int var22 = var5[var8 + 1 - 1 + (var7 + 1) * (width + 2)];
               int var25 = var5[var8 + 1 + (var7 + 1 + 1) * (width + 2)];
               if (isOcean(var16) || isOcean(var19) || isOcean(var22) || isOcean(var25)) {
                  var6[var8 + var7 * width] = var9;
               } else if (this.isDesert(var16) && this.isDesert(var19) && this.isDesert(var22) && this.isDesert(var25)) {
                  var6[var8 + var7 * width] = var9;
               } else {
                  var6[var8 + var7 * width] = Biome.DESERT.id;
               }
            } else if (var9 != Biome.OCEAN.id && var9 != Biome.DEEP_OCEAN.id && var9 != Biome.RIVER.id && var9 != Biome.SWAMPLAND.id) {
               int var15 = var5[var8 + 1 + (var7 + 1 - 1) * (width + 2)];
               int var18 = var5[var8 + 1 + 1 + (var7 + 1) * (width + 2)];
               int var21 = var5[var8 + 1 - 1 + (var7 + 1) * (width + 2)];
               int var24 = var5[var8 + 1 + (var7 + 1 + 1) * (width + 2)];
               if (!isOcean(var15) && !isOcean(var18) && !isOcean(var21) && !isOcean(var24)) {
                  var6[var8 + var7 * width] = var9;
               } else {
                  var6[var8 + var7 * width] = Biome.BEACH.id;
               }
            } else {
               var6[var8 + var7 * width] = var9;
            }
         }
      }

      return var6;
   }

   private void createShore(int[] biome1, int[] biome2, int x, int z, int width, int length, int id) {
      if (isOcean(length)) {
         biome2[x + z * width] = length;
      } else {
         int var8 = biome1[x + 1 + (z + 1 - 1) * (width + 2)];
         int var9 = biome1[x + 1 + 1 + (z + 1) * (width + 2)];
         int var10 = biome1[x + 1 - 1 + (z + 1) * (width + 2)];
         int var11 = biome1[x + 1 + (z + 1 + 1) * (width + 2)];
         if (!isOcean(var8) && !isOcean(var9) && !isOcean(var10) && !isOcean(var11)) {
            biome2[x + z * width] = length;
         } else {
            biome2[x + z * width] = id;
         }
      }
   }

   private boolean isJungleEdge(int id) {
      if (Biome.byId(id) != null && Biome.byId(id).getType() == JungleBiome.class) {
         return true;
      } else {
         return id == Biome.JUNGLE_EDGE.id
            || id == Biome.JUNGLE.id
            || id == Biome.JUNGLE_HILLS.id
            || id == Biome.FOREST.id
            || id == Biome.TAIGA.id
            || isOcean(id);
      }
   }

   private boolean isDesert(int id) {
      return Biome.byId(id) instanceof MesaBiome;
   }
}
