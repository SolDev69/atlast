package net.minecraft.client.world.color;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BiomeColors {
   private static final BiomeColors.ColorProvider GRASS = new BiomeColors.ColorProvider() {
      @Override
      public int getColor(Biome biome, BlockPos pos) {
         return biome.getGrassColor(pos);
      }
   };
   private static final BiomeColors.ColorProvider FOLIAGE = new BiomeColors.ColorProvider() {
      @Override
      public int getColor(Biome biome, BlockPos pos) {
         return biome.getFoliageColor(pos);
      }
   };
   private static final BiomeColors.ColorProvider WATER = new BiomeColors.ColorProvider() {
      @Override
      public int getColor(Biome biome, BlockPos pos) {
         return biome.waterColor;
      }
   };

   private static int getColor(IWorld world, BlockPos pos, BiomeColors.ColorProvider provider) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;

      for(BlockPos.Mutable var7 : BlockPos.iterateRegionMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
         int var8 = provider.getColor(world.getBiome(var7), var7);
         var3 += (var8 & 0xFF0000) >> 16;
         var4 += (var8 & 0xFF00) >> 8;
         var5 += var8 & 0xFF;
      }

      return (var3 / 9 & 0xFF) << 16 | (var4 / 9 & 0xFF) << 8 | var5 / 9 & 0xFF;
   }

   public static int getGrassColor(IWorld world, BlockPos pos) {
      return getColor(world, pos, GRASS);
   }

   public static int getFoliageColor(IWorld world, BlockPos pos) {
      return getColor(world, pos, FOLIAGE);
   }

   public static int getWaterColor(IWorld world, BlockPos pos) {
      return getColor(world, pos, WATER);
   }

   @Environment(EnvType.CLIENT)
   interface ColorProvider {
      int getColor(Biome biome, BlockPos pos);
   }
}
