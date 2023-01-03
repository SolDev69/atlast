package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BushFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GiantJungleTreeFeature;
import net.minecraft.world.gen.feature.MelonPatchFeature;
import net.minecraft.world.gen.feature.TallPlantFeature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.VineFeature;

public class JungleBiome extends Biome {
   private boolean edge;

   public JungleBiome(int id, boolean edge) {
      super(id);
      this.edge = edge;
      if (edge) {
         this.decorator.treeAttempts = 2;
      } else {
         this.decorator.treeAttempts = 50;
      }

      this.decorator.grassAttempts = 25;
      this.decorator.flowerAttempts = 4;
      if (!edge) {
         this.monsterEntries.add(new Biome.SpawnEntry(OcelotEntity.class, 2, 1, 1));
      }

      this.passiveEntries.add(new Biome.SpawnEntry(ChickenEntity.class, 10, 4, 4));
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      if (random.nextInt(10) == 0) {
         return this.largeTree;
      } else if (random.nextInt(2) == 0) {
         return new BushFeature(PlanksBlock.Variant.JUNGLE.getIndex(), PlanksBlock.Variant.OAK.getIndex());
      } else {
         return (AbstractTreeFeature)(!this.edge && random.nextInt(3) == 0
            ? new GiantJungleTreeFeature(false, 10, 20, PlanksBlock.Variant.JUNGLE.getIndex(), PlanksBlock.Variant.JUNGLE.getIndex())
            : new TreeFeature(false, 4 + random.nextInt(7), PlanksBlock.Variant.JUNGLE.getIndex(), PlanksBlock.Variant.JUNGLE.getIndex(), true));
      }
   }

   @Override
   public Feature getRandomGrass(Random random) {
      return random.nextInt(4) == 0 ? new TallPlantFeature(TallPlantBlock.Type.FERN) : new TallPlantFeature(TallPlantBlock.Type.GRASS);
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      super.decorate(world, random, pos);
      int var4 = random.nextInt(16) + 8;
      int var5 = random.nextInt(16) + 8;
      int var6 = random.nextInt(world.getHeight(pos.add(var4, 0, var5)).getY() * 2);
      new MelonPatchFeature().place(world, random, pos.add(var4, var6, var5));
      VineFeature var9 = new VineFeature();

      for(int var10 = 0; var10 < 50; ++var10) {
         var6 = random.nextInt(16) + 8;
         boolean var7 = true;
         int var8 = random.nextInt(16) + 8;
         var9.place(world, random, pos.add(var6, 128, var8));
      }
   }
}
