package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.layer.Layer;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BiomeSource {
   private Layer biomes;
   private Layer biomesIndex;
   private BiomeCache cache = new BiomeCache(this);
   private List biomesForSpawnpoint;
   private String generatorOptions = "";

   protected BiomeSource() {
      this.biomesForSpawnpoint = Lists.newArrayList();
      this.biomesForSpawnpoint.add(Biome.FOREST);
      this.biomesForSpawnpoint.add(Biome.PLAINS);
      this.biomesForSpawnpoint.add(Biome.TAIGA);
      this.biomesForSpawnpoint.add(Biome.TAIGA_HILLS);
      this.biomesForSpawnpoint.add(Biome.FOREST_HILLS);
      this.biomesForSpawnpoint.add(Biome.JUNGLE);
      this.biomesForSpawnpoint.add(Biome.JUNGLE_HILLS);
   }

   public BiomeSource(long seed, WorldGeneratorType generatorType, String generatorOptions) {
      this();
      this.generatorOptions = generatorOptions;
      Layer[] var5 = Layer.init(seed, generatorType, generatorOptions);
      this.biomes = var5[0];
      this.biomesIndex = var5[1];
   }

   public BiomeSource(World world) {
      this(world.getSeed(), world.getData().getGeneratorType(), world.getData().getGeneratorOptions());
   }

   public List getBiomesForSpawnpoint() {
      return this.biomesForSpawnpoint;
   }

   public Biome getBiome(BlockPos pos) {
      return this.getBiomeOrDefault(pos, null);
   }

   public Biome getBiomeOrDefault(BlockPos pos, Biome defaultValue) {
      return this.cache.getBiomeOfDefault(pos.getX(), pos.getZ(), defaultValue);
   }

   public float[] getDownfall(float[] downfall, int x, int z, int width, int length) {
      IntArrays.next();
      if (downfall == null || downfall.length < width * length) {
         downfall = new float[width * length];
      }

      int[] var6 = this.biomesIndex.nextValues(x, z, width, length);

      for(int var7 = 0; var7 < width * length; ++var7) {
         try {
            float var8 = (float)Biome.byIdOrDefault(var6[var7], Biome.DEFAULT).getScaledDownfall() / 65536.0F;
            if (var8 > 1.0F) {
               var8 = 1.0F;
            }

            downfall[var7] = var8;
         } catch (Throwable var11) {
            CrashReport var9 = CrashReport.of(var11, "Invalid Biome id");
            CashReportCategory var10 = var9.addCategory("DownfallBlock");
            var10.add("biome id", var7);
            var10.add("downfalls[] size", downfall.length);
            var10.add("x", x);
            var10.add("z", z);
            var10.add("w", width);
            var10.add("h", length);
            throw new CrashException(var9);
         }
      }

      return downfall;
   }

   @Environment(EnvType.CLIENT)
   public float getTemperature(float temperature, int precipitationHeight) {
      return temperature;
   }

   public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int length) {
      IntArrays.next();
      if (biomes == null || biomes.length < width * length) {
         biomes = new Biome[width * length];
      }

      int[] var6 = this.biomes.nextValues(x, z, width, length);

      try {
         for(int var7 = 0; var7 < width * length; ++var7) {
            biomes[var7] = Biome.byIdOrDefault(var6[var7], Biome.DEFAULT);
         }

         return biomes;
      } catch (Throwable var10) {
         CrashReport var8 = CrashReport.of(var10, "Invalid Biome id");
         CashReportCategory var9 = var8.addCategory("RawBiomeBlock");
         var9.add("biomes[] size", biomes.length);
         var9.add("x", x);
         var9.add("z", z);
         var9.add("w", width);
         var9.add("h", length);
         throw new CrashException(var8);
      }
   }

   public Biome[] getBiomes(Biome[] biomes, int x, int z, int width, int length) {
      return this.getBiomes(biomes, x, z, width, length, true);
   }

   public Biome[] getBiomes(Biome[] biomes, int x, int z, int width, int length, boolean isCached) {
      IntArrays.next();
      if (biomes == null || biomes.length < width * length) {
         biomes = new Biome[width * length];
      }

      if (isCached && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
         Biome[] var9 = this.cache.getBiomesInChunk(x, z);
         System.arraycopy(var9, 0, biomes, 0, width * length);
         return biomes;
      } else {
         int[] var7 = this.biomesIndex.nextValues(x, z, width, length);

         for(int var8 = 0; var8 < width * length; ++var8) {
            biomes[var8] = Biome.byIdOrDefault(var7[var8], Biome.DEFAULT);
         }

         return biomes;
      }
   }

   public boolean areBiomesValid(int x, int z, int range, List biomes) {
      IntArrays.next();
      int var5 = x - range >> 2;
      int var6 = z - range >> 2;
      int var7 = x + range >> 2;
      int var8 = z + range >> 2;
      int var9 = var7 - var5 + 1;
      int var10 = var8 - var6 + 1;
      int[] var11 = this.biomes.nextValues(var5, var6, var9, var10);

      try {
         for(int var12 = 0; var12 < var9 * var10; ++var12) {
            Biome var16 = Biome.byId(var11[var12]);
            if (!biomes.contains(var16)) {
               return false;
            }
         }

         return true;
      } catch (Throwable var15) {
         CrashReport var13 = CrashReport.of(var15, "Invalid Biome id");
         CashReportCategory var14 = var13.addCategory("Layer");
         var14.add("Layer", this.biomes.toString());
         var14.add("x", x);
         var14.add("z", z);
         var14.add("radius", range);
         var14.add("allowed", biomes);
         throw new CrashException(var13);
      }
   }

   public BlockPos getBiomePos(int x, int y, int z, List biomes, Random random) {
      IntArrays.next();
      int var6 = x - z >> 2;
      int var7 = y - z >> 2;
      int var8 = x + z >> 2;
      int var9 = y + z >> 2;
      int var10 = var8 - var6 + 1;
      int var11 = var9 - var7 + 1;
      int[] var12 = this.biomes.nextValues(var6, var7, var10, var11);
      BlockPos var13 = null;
      int var14 = 0;

      for(int var15 = 0; var15 < var10 * var11; ++var15) {
         int var16 = var6 + var15 % var10 << 2;
         int var17 = var7 + var15 / var10 << 2;
         Biome var18 = Biome.byId(var12[var15]);
         if (biomes.contains(var18) && (var13 == null || random.nextInt(var14 + 1) == 0)) {
            var13 = new BlockPos(var16, 0, var17);
            ++var14;
         }
      }

      return var13;
   }

   public void cleanCache() {
      this.cache.cleanUp();
   }
}
