package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MutatedBiome extends Biome {
   protected Biome original;

   public MutatedBiome(int id, Biome original) {
      super(id);
      this.original = original;
      this.setColor(original.biomeColor, true);
      this.name = original.name + " M";
      this.surfaceBlock = original.surfaceBlock;
      this.subsurfaceBlock = original.subsurfaceBlock;
      this.mutatedColor = original.mutatedColor;
      this.baseHeight = original.baseHeight;
      this.heightVariation = original.heightVariation;
      this.temperature = original.temperature;
      this.downfall = original.downfall;
      this.waterColor = original.waterColor;
      this.snowy = original.snowy;
      this.hasRain = original.hasRain;
      this.passiveEntries = Lists.newArrayList(original.passiveEntries);
      this.monsterEntries = Lists.newArrayList(original.monsterEntries);
      this.caveEntries = Lists.newArrayList(original.caveEntries);
      this.waterEntries = Lists.newArrayList(original.waterEntries);
      this.temperature = original.temperature;
      this.downfall = original.downfall;
      this.baseHeight = original.baseHeight + 0.1F;
      this.heightVariation = original.heightVariation + 0.2F;
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      this.original.decorator.decorate(world, random, this, pos);
   }

   @Override
   public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      this.original.populateChunk(world, random, blocks, x, z, noise);
   }

   @Override
   public float getSpawnChance() {
      return this.original.getSpawnChance();
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      return this.original.getRandomTree(random);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getFoliageColor(BlockPos pos) {
      return this.original.getFoliageColor(pos);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getGrassColor(BlockPos pos) {
      return this.original.getGrassColor(pos);
   }

   @Override
   public Class getType() {
      return this.original.getType();
   }

   @Override
   public boolean is(Biome biome) {
      return this.original.is(biome);
   }

   @Override
   public Biome.TemperatureCategory getTemperatureCategory() {
      return this.original.getTemperatureCategory();
   }
}
