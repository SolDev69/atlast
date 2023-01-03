package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class SingleBiomeSource extends BiomeSource {
   private Biome biome;
   private float downfall;

   public SingleBiomeSource(Biome biome, float downfall) {
      this.biome = biome;
      this.downfall = downfall;
   }

   @Override
   public Biome getBiome(BlockPos pos) {
      return this.biome;
   }

   @Override
   public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int length) {
      if (biomes == null || biomes.length < width * length) {
         biomes = new Biome[width * length];
      }

      Arrays.fill(biomes, 0, width * length, this.biome);
      return biomes;
   }

   @Override
   public float[] getDownfall(float[] downfall, int x, int z, int width, int length) {
      if (downfall == null || downfall.length < width * length) {
         downfall = new float[width * length];
      }

      Arrays.fill(downfall, 0, width * length, this.downfall);
      return downfall;
   }

   @Override
   public Biome[] getBiomes(Biome[] biomes, int x, int z, int width, int length) {
      if (biomes == null || biomes.length < width * length) {
         biomes = new Biome[width * length];
      }

      Arrays.fill(biomes, 0, width * length, this.biome);
      return biomes;
   }

   @Override
   public Biome[] getBiomes(Biome[] biomes, int x, int z, int width, int length, boolean isCached) {
      return this.getBiomes(biomes, x, z, width, length);
   }

   @Override
   public BlockPos getBiomePos(int x, int y, int z, List biomes, Random random) {
      return biomes.contains(this.biome) ? new BlockPos(x - z + random.nextInt(z * 2 + 1), 0, y - z + random.nextInt(z * 2 + 1)) : null;
   }

   @Override
   public boolean areBiomesValid(int x, int z, int range, List biomes) {
      return biomes.contains(this.biome);
   }
}
