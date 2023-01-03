package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.VeinFeature;

public class ExtremeHillsBiome extends Biome {
   private Feature monsterEggVein = new VeinFeature(Blocks.MONSTER_EGG.defaultState().set(InfestedBlock.VARIANT, InfestedBlock.Variant.STONE), 9);
   private SpruceTreeFeature spruceTree = new SpruceTreeFeature(false);
   private int normalVariant = 0;
   private int moreTreesVariant = 1;
   private int mutatedVariant = 2;
   private int variant = this.normalVariant;

   protected ExtremeHillsBiome(int id, boolean moreTrees) {
      super(id);
      if (moreTrees) {
         this.decorator.treeAttempts = 3;
         this.variant = this.moreTreesVariant;
      }
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      return (AbstractTreeFeature)(random.nextInt(3) > 0 ? this.spruceTree : super.getRandomTree(random));
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      super.decorate(world, random, pos);
      int var4 = 3 + random.nextInt(6);

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = random.nextInt(16);
         int var7 = random.nextInt(28) + 4;
         int var8 = random.nextInt(16);
         BlockPos var9 = pos.add(var6, var7, var8);
         if (world.getBlockState(var9).getBlock() == Blocks.STONE) {
            world.setBlockState(var9, Blocks.EMERALD_ORE.defaultState(), 2);
         }
      }

      for(int var10 = 0; var10 < 7; ++var10) {
         int var11 = random.nextInt(16);
         int var12 = random.nextInt(64);
         int var13 = random.nextInt(16);
         this.monsterEggVein.place(world, random, pos.add(var11, var12, var13));
      }
   }

   @Override
   public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      this.surfaceBlock = Blocks.GRASS.defaultState();
      this.subsurfaceBlock = Blocks.DIRT.defaultState();
      if ((noise < -1.0 || noise > 2.0) && this.variant == this.mutatedVariant) {
         this.surfaceBlock = Blocks.GRAVEL.defaultState();
         this.subsurfaceBlock = Blocks.GRAVEL.defaultState();
      } else if (noise > 1.0 && this.variant != this.moreTreesVariant) {
         this.surfaceBlock = Blocks.STONE.defaultState();
         this.subsurfaceBlock = Blocks.STONE.defaultState();
      }

      this.populate(world, random, blocks, x, z, noise);
   }

   private ExtremeHillsBiome mutate(Biome original) {
      this.variant = this.mutatedVariant;
      this.setColor(original.biomeColor, true);
      this.setName(original.name + " M");
      this.setHeight(new Biome.Height(original.baseHeight, original.heightVariation));
      this.setTemperatureAndDownfall(original.temperature, original.downfall);
      return this;
   }

   @Override
   protected Biome mutate(int id) {
      return new ExtremeHillsBiome(id, false).mutate(this);
   }
}
