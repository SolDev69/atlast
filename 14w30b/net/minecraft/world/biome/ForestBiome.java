package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ForestBiome extends Biome {
   private int variant;
   protected static final BirchTreeFeature TALL_BIRCH_TREE = new BirchTreeFeature(false, true);
   protected static final BirchTreeFeature BIRCH_TREE = new BirchTreeFeature(false, false);
   protected static final DarkOakTreeFeature DARK_OAK_TREE = new DarkOakTreeFeature(false);

   public ForestBiome(int id, int type) {
      super(id);
      this.variant = type;
      this.decorator.treeAttempts = 10;
      this.decorator.grassAttempts = 2;
      if (this.variant == 1) {
         this.decorator.treeAttempts = 6;
         this.decorator.flowerAttempts = 100;
         this.decorator.grassAttempts = 1;
      }

      this.setMutatedColor(5159473);
      this.setTemperatureAndDownfall(0.7F, 0.8F);
      if (this.variant == 2) {
         this.baseColor = 353825;
         this.biomeColor = 3175492;
         this.setTemperatureAndDownfall(0.6F, 0.6F);
      }

      if (this.variant == 0) {
         this.passiveEntries.add(new Biome.SpawnEntry(WolfEntity.class, 5, 4, 4));
      }

      if (this.variant == 3) {
         this.decorator.treeAttempts = -999;
      }
   }

   @Override
   protected Biome setColor(int color, boolean modifyBaseColor) {
      if (this.variant == 2) {
         this.baseColor = 353825;
         this.biomeColor = color;
         if (modifyBaseColor) {
            this.baseColor = (this.baseColor & 16711422) >> 1;
         }

         return this;
      } else {
         return super.setColor(color, modifyBaseColor);
      }
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      if (this.variant == 3 && random.nextInt(3) > 0) {
         return DARK_OAK_TREE;
      } else {
         return (AbstractTreeFeature)(this.variant != 2 && random.nextInt(5) != 0 ? this.tree : BIRCH_TREE);
      }
   }

   @Override
   public FlowerBlock.Type getRandomFlower(Random random, BlockPos pos) {
      if (this.variant == 1) {
         double var3 = MathHelper.clamp((1.0 + FOLIAGE_NOISE.getNoise((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
         FlowerBlock.Type var5 = FlowerBlock.Type.values()[(int)(var3 * (double)FlowerBlock.Type.values().length)];
         return var5 == FlowerBlock.Type.BLUE_ORCHID ? FlowerBlock.Type.POPPY : var5;
      } else {
         return super.getRandomFlower(random, pos);
      }
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      if (this.variant == 3) {
         for(int var4 = 0; var4 < 4; ++var4) {
            for(int var5 = 0; var5 < 4; ++var5) {
               int var6 = var4 * 4 + 1 + 8 + random.nextInt(3);
               int var7 = var5 * 4 + 1 + 8 + random.nextInt(3);
               BlockPos var8 = world.getHeight(pos.add(var6, 0, var7));
               if (random.nextInt(20) == 0) {
                  HugeMushroomFeature var9 = new HugeMushroomFeature();
                  var9.place(world, random, var8);
               } else {
                  AbstractTreeFeature var16 = this.getRandomTree(random);
                  var16.prepare();
                  if (var16.place(world, random, var8)) {
                     var16.placeSoil(world, random, var8);
                  }
               }
            }
         }
      }

      int var11 = random.nextInt(5) - 3;
      if (this.variant == 1) {
         var11 += 2;
      }

      for(int var12 = 0; var12 < var11; ++var12) {
         int var13 = random.nextInt(3);
         if (var13 == 0) {
            DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.SYRINGA);
         } else if (var13 == 1) {
            DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.ROSE);
         } else if (var13 == 2) {
            DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.PAEONIA);
         }

         for(int var14 = 0; var14 < 5; ++var14) {
            int var15 = random.nextInt(16) + 8;
            int var17 = random.nextInt(16) + 8;
            int var10 = random.nextInt(world.getHeight(pos.add(var15, 0, var17)).getY() + 32);
            if (DOUBLE_PLANT.place(world, random, new BlockPos(pos.getX() + var15, var10, pos.getZ() + var17))) {
               break;
            }
         }
      }

      super.decorate(world, random, pos);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getGrassColor(BlockPos pos) {
      int var2 = super.getGrassColor(pos);
      return this.variant == 3 ? (var2 & 16711422) + 2634762 >> 1 : var2;
   }

   @Override
   protected Biome mutate(int id) {
      if (this.id == Biome.FOREST.id) {
         ForestBiome var2 = new ForestBiome(id, 1);
         var2.setHeight(new Biome.Height(this.baseHeight, this.heightVariation + 0.2F));
         var2.setName("Flower Forest");
         var2.setColor(6976549, true);
         var2.setMutatedColor(8233509);
         return var2;
      } else {
         return this.id != Biome.BIRCH_FOREST.id && this.id != Biome.BIRCH_FOREST_HILLS.id ? new MutatedBiome(id, this) {
            @Override
            public void decorate(World world, Random random, BlockPos pos) {
               this.original.decorate(world, random, pos);
            }
         } : new MutatedBiome(id, this) {
            @Override
            public AbstractTreeFeature getRandomTree(Random random) {
               return random.nextBoolean() ? ForestBiome.TALL_BIRCH_TREE : ForestBiome.BIRCH_TREE;
            }
         };
      }
   }
}
