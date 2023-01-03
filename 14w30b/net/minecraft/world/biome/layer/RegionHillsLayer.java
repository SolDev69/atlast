package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionHillsLayer extends Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private Layer riverLayer;

   public RegionHillsLayer(long seed, Layer parent, Layer riverLayer) {
      super(seed);
      this.parent = parent;
      this.riverLayer = riverLayer;
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int[] var5 = this.parent.nextValues(x - 1, z - 1, width + 2, length + 2);
      int[] var6 = this.riverLayer.nextValues(x - 1, z - 1, width + 2, length + 2);
      int[] var7 = IntArrays.get(width * length);

      for(int var8 = 0; var8 < length; ++var8) {
         for(int var9 = 0; var9 < width; ++var9) {
            this.setChunkSeed((long)(var9 + x), (long)(var8 + z));
            int var10 = var5[var9 + 1 + (var8 + 1) * (width + 2)];
            int var11 = var6[var9 + 1 + (var8 + 1) * (width + 2)];
            boolean var12 = (var11 - 2) % 29 == 0;
            if (var10 > 255) {
               LOGGER.debug("old! " + var10);
            }

            if (var10 != 0 && var11 >= 2 && (var11 - 2) % 29 == 1 && var10 < 128) {
               if (Biome.byId(var10 + 128) != null) {
                  var7[var9 + var8 * width] = var10 + 128;
               } else {
                  var7[var9 + var8 * width] = var10;
               }
            } else if (this.nextInt(3) != 0 && !var12) {
               var7[var9 + var8 * width] = var10;
            } else {
               int var13 = var10;
               if (var10 == Biome.DESERT.id) {
                  var13 = Biome.DESERT_HILLS.id;
               } else if (var10 == Biome.FOREST.id) {
                  var13 = Biome.FOREST_HILLS.id;
               } else if (var10 == Biome.BIRCH_FOREST.id) {
                  var13 = Biome.BIRCH_FOREST_HILLS.id;
               } else if (var10 == Biome.ROOFED_FOREST.id) {
                  var13 = Biome.PLAINS.id;
               } else if (var10 == Biome.TAIGA.id) {
                  var13 = Biome.TAIGA_HILLS.id;
               } else if (var10 == Biome.MEGA_TAIGA.id) {
                  var13 = Biome.MEGA_TAIGA_HILLS.id;
               } else if (var10 == Biome.COLD_TAIGA.id) {
                  var13 = Biome.COLD_TAIGA_HILLS.id;
               } else if (var10 == Biome.PLAINS.id) {
                  if (this.nextInt(3) == 0) {
                     var13 = Biome.FOREST_HILLS.id;
                  } else {
                     var13 = Biome.FOREST.id;
                  }
               } else if (var10 == Biome.ICE_PLAINS.id) {
                  var13 = Biome.ICE_MOUNTAINS.id;
               } else if (var10 == Biome.JUNGLE.id) {
                  var13 = Biome.JUNGLE_HILLS.id;
               } else if (var10 == Biome.OCEAN.id) {
                  var13 = Biome.DEEP_OCEAN.id;
               } else if (var10 == Biome.EXTREME_HILLS.id) {
                  var13 = Biome.EXTREME_HILLS_PLUS.id;
               } else if (var10 == Biome.SAVANNA.id) {
                  var13 = Biome.SAVANNA_PLATEAU.id;
               } else if (areBiomesEqual(var10, Biome.MESA_PLATEAU_F.id)) {
                  var13 = Biome.MESA.id;
               } else if (var10 == Biome.DEEP_OCEAN.id && this.nextInt(3) == 0) {
                  int var14 = this.nextInt(2);
                  if (var14 == 0) {
                     var13 = Biome.PLAINS.id;
                  } else {
                     var13 = Biome.FOREST.id;
                  }
               }

               if (var12 && var13 != var10) {
                  if (Biome.byId(var13 + 128) != null) {
                     var13 += 128;
                  } else {
                     var13 = var10;
                  }
               }

               if (var13 == var10) {
                  var7[var9 + var8 * width] = var10;
               } else {
                  int var19 = var5[var9 + 1 + (var8 + 1 - 1) * (width + 2)];
                  int var15 = var5[var9 + 1 + 1 + (var8 + 1) * (width + 2)];
                  int var16 = var5[var9 + 1 - 1 + (var8 + 1) * (width + 2)];
                  int var17 = var5[var9 + 1 + (var8 + 1 + 1) * (width + 2)];
                  int var18 = 0;
                  if (areBiomesEqual(var19, var10)) {
                     ++var18;
                  }

                  if (areBiomesEqual(var15, var10)) {
                     ++var18;
                  }

                  if (areBiomesEqual(var16, var10)) {
                     ++var18;
                  }

                  if (areBiomesEqual(var17, var10)) {
                     ++var18;
                  }

                  if (var18 >= 3) {
                     var7[var9 + var8 * width] = var13;
                  } else {
                     var7[var9 + var8 * width] = var10;
                  }
               }
            }
         }
      }

      return var7;
   }
}
