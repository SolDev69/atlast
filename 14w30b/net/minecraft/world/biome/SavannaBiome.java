package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;

public class SavannaBiome extends Biome {
   private static final AcaciaTreeFeature ACACIA_TREE = new AcaciaTreeFeature(false);

   protected SavannaBiome(int i) {
      super(i);
      this.passiveEntries.add(new Biome.SpawnEntry(HorseBaseEntity.class, 1, 2, 6));
      this.decorator.treeAttempts = 1;
      this.decorator.flowerAttempts = 4;
      this.decorator.grassAttempts = 20;
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      return (AbstractTreeFeature)(random.nextInt(5) > 0 ? ACACIA_TREE : this.tree);
   }

   @Override
   protected Biome mutate(int id) {
      SavannaBiome.ShatteredSavannaBiome var2 = new SavannaBiome.ShatteredSavannaBiome(id, this);
      var2.temperature = (this.temperature + 1.0F) * 0.5F;
      var2.baseHeight = this.baseHeight * 0.5F + 0.3F;
      var2.heightVariation = this.heightVariation * 0.5F + 1.2F;
      return var2;
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.GRASS);

      for(int var4 = 0; var4 < 7; ++var4) {
         int var5 = random.nextInt(16) + 8;
         int var6 = random.nextInt(16) + 8;
         int var7 = random.nextInt(world.getHeight(pos.add(var5, 0, var6)).getY() + 32);
         DOUBLE_PLANT.place(world, random, pos.add(var5, var7, var6));
      }

      super.decorate(world, random, pos);
   }

   public static class ShatteredSavannaBiome extends MutatedBiome {
      public ShatteredSavannaBiome(int i, Biome c_72robrvqq) {
         super(i, c_72robrvqq);
         this.decorator.treeAttempts = 2;
         this.decorator.flowerAttempts = 2;
         this.decorator.grassAttempts = 5;
      }

      @Override
      public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
         this.surfaceBlock = Blocks.GRASS.defaultState();
         this.subsurfaceBlock = Blocks.DIRT.defaultState();
         if (noise > 1.75) {
            this.surfaceBlock = Blocks.STONE.defaultState();
            this.subsurfaceBlock = Blocks.STONE.defaultState();
         } else if (noise > -0.5) {
            this.surfaceBlock = Blocks.DIRT.getStateFromMetadata(DirtBlock.Variant.DOARSE_DIRT.getIndex());
         }

         this.populate(world, random, blocks, x, z, noise);
      }

      @Override
      public void decorate(World world, Random random, BlockPos pos) {
         this.decorator.decorate(world, random, this, pos);
      }
   }
}
