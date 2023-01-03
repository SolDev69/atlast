package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IntArrays;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.chunk.GeneratorOptions;

public class BiomeInitLayer extends Layer {
   private Biome[] warmBiomes = new Biome[]{Biome.DESERT, Biome.DESERT, Biome.DESERT, Biome.SAVANNA, Biome.SAVANNA, Biome.PLAINS};
   private Biome[] temperateBiomes = new Biome[]{Biome.FOREST, Biome.ROOFED_FOREST, Biome.EXTREME_HILLS, Biome.PLAINS, Biome.BIRCH_FOREST, Biome.SWAMPLAND};
   private Biome[] coolBiomes = new Biome[]{Biome.FOREST, Biome.EXTREME_HILLS, Biome.TAIGA, Biome.PLAINS};
   private Biome[] snowyBiomes = new Biome[]{Biome.ICE_PLAINS, Biome.ICE_PLAINS, Biome.ICE_PLAINS, Biome.COLD_TAIGA};
   private final GeneratorOptions options;

   public BiomeInitLayer(long seed, Layer parent, WorldGeneratorType generatorType, String generatorOptions) {
      super(seed);
      this.parent = parent;
      if (generatorType == WorldGeneratorType.DEFAULT_1_1) {
         this.warmBiomes = new Biome[]{Biome.DESERT, Biome.FOREST, Biome.EXTREME_HILLS, Biome.SWAMPLAND, Biome.PLAINS, Biome.TAIGA};
         this.options = null;
      } else if (generatorType == WorldGeneratorType.CUSTOMIZED) {
         this.options = GeneratorOptions.Factory.fromJson(generatorOptions).create();
      } else {
         this.options = null;
      }
   }

   @Override
   public int[] nextValues(int x, int z, int width, int length) {
      int[] var5 = this.parent.nextValues(x, z, width, length);
      int[] var6 = IntArrays.get(width * length);

      for(int var7 = 0; var7 < length; ++var7) {
         for(int var8 = 0; var8 < width; ++var8) {
            this.setChunkSeed((long)(var8 + x), (long)(var7 + z));
            int var9 = var5[var8 + var7 * width];
            int var10 = (var9 & 3840) >> 8;
            var9 &= -3841;
            if (this.options != null && this.options.fixedBiome >= 0) {
               var6[var8 + var7 * width] = this.options.fixedBiome;
            } else if (isOcean(var9)) {
               var6[var8 + var7 * width] = var9;
            } else if (var9 == Biome.MUSHROOM_ISLAND.id) {
               var6[var8 + var7 * width] = var9;
            } else if (var9 == 1) {
               if (var10 > 0) {
                  if (this.nextInt(3) == 0) {
                     var6[var8 + var7 * width] = Biome.MESA_PLATEAU.id;
                  } else {
                     var6[var8 + var7 * width] = Biome.MESA_PLATEAU_F.id;
                  }
               } else {
                  var6[var8 + var7 * width] = this.warmBiomes[this.nextInt(this.warmBiomes.length)].id;
               }
            } else if (var9 == 2) {
               if (var10 > 0) {
                  var6[var8 + var7 * width] = Biome.JUNGLE.id;
               } else {
                  var6[var8 + var7 * width] = this.temperateBiomes[this.nextInt(this.temperateBiomes.length)].id;
               }
            } else if (var9 == 3) {
               if (var10 > 0) {
                  var6[var8 + var7 * width] = Biome.MEGA_TAIGA.id;
               } else {
                  var6[var8 + var7 * width] = this.coolBiomes[this.nextInt(this.coolBiomes.length)].id;
               }
            } else if (var9 == 4) {
               var6[var8 + var7 * width] = this.snowyBiomes[this.nextInt(this.snowyBiomes.length)].id;
            } else {
               var6[var8 + var7 * width] = Biome.MUSHROOM_ISLAND.id;
            }
         }
      }

      return var6;
   }
}
