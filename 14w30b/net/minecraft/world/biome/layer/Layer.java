package net.minecraft.world.biome.layer;

import java.util.concurrent.Callable;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.chunk.GeneratorOptions;

public abstract class Layer {
   private long localWorldSeed;
   protected Layer parent;
   private long chunkSeed;
   protected long seed;

   public static Layer[] init(long worldSeed, WorldGeneratorType generatorType, String generatorOptions) {
      IslandLayer var4 = new IslandLayer(1L);
      FuzzyZoomLayer var13 = new FuzzyZoomLayer(2000L, var4);
      AddIslandLayer var14 = new AddIslandLayer(1L, var13);
      ZoomLayer var15 = new ZoomLayer(2001L, var14);
      AddIslandLayer var16 = new AddIslandLayer(2L, var15);
      AddIslandLayer var17 = new AddIslandLayer(50L, var16);
      AddIslandLayer var18 = new AddIslandLayer(70L, var17);
      RemoveTooMuchOceanLayer var19 = new RemoveTooMuchOceanLayer(2L, var18);
      AddSnowLayer var20 = new AddSnowLayer(2L, var19);
      AddIslandLayer var21 = new AddIslandLayer(3L, var20);
      AddEdgeLayer var22 = new AddEdgeLayer(2L, var21, AddEdgeLayer.Mode.COOL_WARM);
      AddEdgeLayer var23 = new AddEdgeLayer(2L, var22, AddEdgeLayer.Mode.HEAT_ICE);
      AddEdgeLayer var24 = new AddEdgeLayer(3L, var23, AddEdgeLayer.Mode.SPECIAL);
      ZoomLayer var25 = new ZoomLayer(2002L, var24);
      ZoomLayer var26 = new ZoomLayer(2003L, var25);
      AddIslandLayer var27 = new AddIslandLayer(4L, var26);
      AddMushroomIslandLayer var28 = new AddMushroomIslandLayer(5L, var27);
      AddDeepOceanLayer var29 = new AddDeepOceanLayer(4L, var28);
      Layer var30 = ZoomLayer.zoom(1000L, var29, 0);
      GeneratorOptions var5 = null;
      int var6 = 4;
      int var7 = var6;
      if (generatorType == WorldGeneratorType.CUSTOMIZED && generatorOptions.length() > 0) {
         var5 = GeneratorOptions.Factory.fromJson(generatorOptions).create();
         var6 = var5.biomeSize;
         var7 = var5.riverSize;
      }

      if (generatorType == WorldGeneratorType.LARGE_BIOMES) {
         var6 = 6;
      }

      Layer var8 = ZoomLayer.zoom(1000L, var30, 0);
      RiverInitLayer var32 = new RiverInitLayer(100L, var8);
      BiomeInitLayer var9 = new BiomeInitLayer(200L, var30, generatorType, generatorOptions);
      Layer var37 = ZoomLayer.zoom(1000L, var9, 2);
      BiomeTransitionLayer var38 = new BiomeTransitionLayer(1000L, var37);
      Layer var10 = ZoomLayer.zoom(1000L, var32, 2);
      RegionHillsLayer var39 = new RegionHillsLayer(1000L, var38, var10);
      var8 = ZoomLayer.zoom(1000L, var32, 2);
      var8 = ZoomLayer.zoom(1000L, var8, var7);
      RiverLayer var35 = new RiverLayer(1L, var8);
      SmoothLayer var36 = new SmoothLayer(1000L, var35);
      var9 = new AddSunflowerPlainsLayer(1001L, var39);

      for(int var11 = 0; var11 < var6; ++var11) {
         var9 = new ZoomLayer((long)(1000 + var11), var9);
         if (var11 == 0) {
            var9 = new AddIslandLayer(3L, var9);
         }

         if (var11 == 1 || var6 == 1) {
            var9 = new ShoreLayer(1000L, var9);
         }
      }

      SmoothLayer var41 = new SmoothLayer(1000L, var9);
      RiverMixLayer var42 = new RiverMixLayer(100L, var41, var36);
      VoronoiZoomLayer var12 = new VoronoiZoomLayer(10L, var42);
      var42.setLocalWorldSeed(worldSeed);
      var12.setLocalWorldSeed(worldSeed);
      return new Layer[]{var42, var12, var42};
   }

   public Layer(long seed) {
      this.seed = seed;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += seed;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += seed;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += seed;
   }

   public void setLocalWorldSeed(long worldSeed) {
      this.localWorldSeed = worldSeed;
      if (this.parent != null) {
         this.parent.setLocalWorldSeed(worldSeed);
      }

      this.localWorldSeed *= this.localWorldSeed * 6364136223846793005L + 1442695040888963407L;
      this.localWorldSeed += this.seed;
      this.localWorldSeed *= this.localWorldSeed * 6364136223846793005L + 1442695040888963407L;
      this.localWorldSeed += this.seed;
      this.localWorldSeed *= this.localWorldSeed * 6364136223846793005L + 1442695040888963407L;
      this.localWorldSeed += this.seed;
   }

   public void setChunkSeed(long chunkX, long chunkZ) {
      this.chunkSeed = this.localWorldSeed;
      this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
      this.chunkSeed += chunkX;
      this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
      this.chunkSeed += chunkZ;
      this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
      this.chunkSeed += chunkX;
      this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
      this.chunkSeed += chunkZ;
   }

   protected int nextInt(int bound) {
      int var2 = (int)((this.chunkSeed >> 24) % (long)bound);
      if (var2 < 0) {
         var2 += bound;
      }

      this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
      this.chunkSeed += this.localWorldSeed;
      return var2;
   }

   public abstract int[] nextValues(int x, int z, int width, int length);

   protected static boolean areBiomesEqual(int biomeId1, int biomeId2) {
      if (biomeId1 == biomeId2) {
         return true;
      } else if (biomeId1 != Biome.MESA_PLATEAU_F.id && biomeId1 != Biome.MESA_PLATEAU.id) {
         final Biome var2 = Biome.byId(biomeId1);
         final Biome var3 = Biome.byId(biomeId2);

         try {
            return var2 != null && var3 != null ? var2.is(var3) : false;
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.of(var7, "Comparing biomes");
            CashReportCategory var6 = var5.addCategory("Biomes being compared");
            var6.add("Biome A ID", biomeId1);
            var6.add("Biome B ID", biomeId2);
            var6.add("Biome A", new Callable() {
               public String call() {
                  return String.valueOf(var2);
               }
            });
            var6.add("Biome B", new Callable() {
               public String call() {
                  return String.valueOf(var3);
               }
            });
            throw new CrashException(var5);
         }
      } else {
         return biomeId2 == Biome.MESA_PLATEAU_F.id || biomeId2 == Biome.MESA_PLATEAU.id;
      }
   }

   protected static boolean isOcean(int biomeId) {
      return biomeId == Biome.OCEAN.id || biomeId == Biome.DEEP_OCEAN.id || biomeId == Biome.FROZEN_OCEAN.id;
   }

   protected int getRandomInt(int... ints) {
      return ints[this.nextInt(ints.length)];
   }

   protected int getModeOrRandom(int x, int z, int width, int length) {
      if (z == width && width == length) {
         return z;
      } else if (x == z && x == width) {
         return x;
      } else if (x == z && x == length) {
         return x;
      } else if (x == width && x == length) {
         return x;
      } else if (x == z && width != length) {
         return x;
      } else if (x == width && z != length) {
         return x;
      } else if (x == length && z != width) {
         return x;
      } else if (z == width && x != length) {
         return z;
      } else if (z == length && x != width) {
         return z;
      } else {
         return width == length && x != z ? width : this.getRandomInt(x, z, width, length);
      }
   }
}
