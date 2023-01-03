package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BlockVeinFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GiantSpruceTreeFeature;
import net.minecraft.world.gen.feature.PineTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.TallPlantFeature;

public class TaigaBiome extends Biome {
   private static final PineTreeFeature PINE_TREE = new PineTreeFeature();
   private static final SpruceTreeFeature SPRUCE_TREE = new SpruceTreeFeature(false);
   private static final GiantSpruceTreeFeature GIANT_PINE_TREE = new GiantSpruceTreeFeature(false, false);
   private static final GiantSpruceTreeFeature GIANT_SPRUCE_TREE = new GiantSpruceTreeFeature(false, true);
   private static final BlockVeinFeature MOSSY_BOULDER = new BlockVeinFeature(Blocks.MOSSY_COBBLESTONE, 0);
   private int variant;

   public TaigaBiome(int id, int variant) {
      super(id);
      this.variant = variant;
      this.passiveEntries.add(new Biome.SpawnEntry(WolfEntity.class, 8, 4, 4));
      this.decorator.treeAttempts = 10;
      if (variant != 1 && variant != 2) {
         this.decorator.grassAttempts = 1;
         this.decorator.mushroomAttempts = 1;
      } else {
         this.decorator.grassAttempts = 7;
         this.decorator.deadBushAttempts = 1;
         this.decorator.mushroomAttempts = 3;
      }
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      if ((this.variant == 1 || this.variant == 2) && random.nextInt(3) == 0) {
         return this.variant != 2 && random.nextInt(13) != 0 ? GIANT_PINE_TREE : GIANT_SPRUCE_TREE;
      } else {
         return (AbstractTreeFeature)(random.nextInt(3) == 0 ? PINE_TREE : SPRUCE_TREE);
      }
   }

   @Override
   public Feature getRandomGrass(Random random) {
      return random.nextInt(5) > 0 ? new TallPlantFeature(TallPlantBlock.Type.FERN) : new TallPlantFeature(TallPlantBlock.Type.GRASS);
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      if (this.variant == 1 || this.variant == 2) {
         int var4 = random.nextInt(3);

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = random.nextInt(16) + 8;
            int var7 = random.nextInt(16) + 8;
            BlockPos var8 = world.getHeight(pos.add(var6, 0, var7));
            MOSSY_BOULDER.place(world, random, var8);
         }
      }

      DOUBLE_PLANT.setMetadata(DoublePlantBlock.Variant.FERN);

      for(int var9 = 0; var9 < 7; ++var9) {
         int var10 = random.nextInt(16) + 8;
         int var11 = random.nextInt(16) + 8;
         int var12 = random.nextInt(world.getHeight(pos.add(var10, 0, var11)).getY() + 32);
         DOUBLE_PLANT.place(world, random, pos.add(var10, var12, var11));
      }

      super.decorate(world, random, pos);
   }

   @Override
   public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      if (this.variant == 1 || this.variant == 2) {
         this.surfaceBlock = Blocks.GRASS.defaultState();
         this.subsurfaceBlock = Blocks.DIRT.defaultState();
         if (noise > 1.75) {
            this.surfaceBlock = Blocks.DIRT.getStateFromMetadata(DirtBlock.Variant.DOARSE_DIRT.getIndex());
         } else if (noise > -0.95) {
            this.surfaceBlock = Blocks.DIRT.getStateFromMetadata(DirtBlock.Variant.PODZOL.getIndex());
         }
      }

      this.populate(world, random, blocks, x, z, noise);
   }

   @Override
   protected Biome mutate(int id) {
      return this.id == Biome.MEGA_TAIGA.id
         ? new TaigaBiome(id, 2)
            .setColor(5858897, true)
            .setName("Mega Spruce Taiga")
            .setMutatedColor(5159473)
            .setTemperatureAndDownfall(0.25F, 0.8F)
            .setHeight(new Biome.Height(this.baseHeight, this.heightVariation))
         : super.mutate(id);
   }
}
