package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlainsBiome extends Biome {
   protected boolean mutated;

   protected PlainsBiome(int i) {
      super(i);
      this.setTemperatureAndDownfall(0.8F, 0.4F);
      this.setHeight(PLAINS_HEIGHT);
      this.passiveEntries.add(new Biome.SpawnEntry(HorseBaseEntity.class, 5, 2, 6));
      this.decorator.treeAttempts = -999;
      this.decorator.flowerAttempts = 4;
      this.decorator.grassAttempts = 10;
   }

   @Override
   public FlowerBlock.Type getRandomFlower(Random random, BlockPos pos) {
      double var3 = FOLIAGE_NOISE.getNoise((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0);
      if (var3 < -0.8) {
         int var6 = random.nextInt(4);
         switch(var6) {
            case 0:
               return FlowerBlock.Type.ORANGE_TULIP;
            case 1:
               return FlowerBlock.Type.RED_TULIP;
            case 2:
               return FlowerBlock.Type.PINK_TULIP;
            case 3:
            default:
               return FlowerBlock.Type.WHITE_TULIP;
         }
      } else if (random.nextInt(3) > 0) {
         int var5 = random.nextInt(3);
         if (var5 == 0) {
            return FlowerBlock.Type.POPPY;
         } else {
            return var5 == 1 ? FlowerBlock.Type.HOUSTONIA : FlowerBlock.Type.OXEY_DAISY;
         }
      } else {
         return FlowerBlock.Type.DANDELION;
      }
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      double var4 = FOLIAGE_NOISE.getNoise((double)(pos.getX() + 8) / 200.0, (double)(pos.getZ() + 8) / 200.0);
      if (var4 < -0.8) {
         this.decorator.flowerAttempts = 15;
         this.decorator.grassAttempts = 5;
      } else {
         this.decorator.flowerAttempts = 4;
         this.decorator.grassAttempts = 10;
         DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.GRASS);

         for(int var6 = 0; var6 < 7; ++var6) {
            int var7 = random.nextInt(16) + 8;
            int var8 = random.nextInt(16) + 8;
            int var9 = random.nextInt(world.getHeight(pos.add(var7, 0, var8)).getY() + 32);
            DOUBLE_PLANT.place(world, random, pos.add(var7, var9, var8));
         }
      }

      if (this.mutated) {
         DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.SUNFLOWER);

         for(int var10 = 0; var10 < 10; ++var10) {
            int var11 = random.nextInt(16) + 8;
            int var12 = random.nextInt(16) + 8;
            int var13 = random.nextInt(world.getHeight(pos.add(var11, 0, var12)).getY() + 32);
            DOUBLE_PLANT.place(world, random, pos.add(var11, var13, var12));
         }
      }

      super.decorate(world, random, pos);
   }

   @Override
   protected Biome mutate(int id) {
      PlainsBiome var2 = new PlainsBiome(id);
      var2.setName("Sunflower Plains");
      var2.mutated = true;
      var2.setColor(9286496);
      var2.baseColor = 14273354;
      return var2;
   }
}
