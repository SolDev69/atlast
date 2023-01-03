package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SwampBiome extends Biome {
   protected SwampBiome(int i) {
      super(i);
      this.decorator.treeAttempts = 2;
      this.decorator.flowerAttempts = 1;
      this.decorator.deadBushAttempts = 1;
      this.decorator.mushroomAttempts = 8;
      this.decorator.sugarcaneAttempts = 10;
      this.decorator.clayPatchAttempts = 1;
      this.decorator.lilyPadAttempts = 4;
      this.decorator.sandPatchAttempts = 0;
      this.decorator.gravelPatchAttempts = 0;
      this.decorator.grassAttempts = 5;
      this.waterColor = 14745518;
      this.monsterEntries.add(new Biome.SpawnEntry(SlimeEntity.class, 1, 1, 1));
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      return this.swampTree;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getGrassColor(BlockPos pos) {
      double var2 = FOLIAGE_NOISE.getNoise((double)pos.getX() * 0.0225, (double)pos.getZ() * 0.0225);
      return var2 < -0.1 ? 5011004 : 6975545;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getFoliageColor(BlockPos pos) {
      return 6975545;
   }

   @Override
   public FlowerBlock.Type getRandomFlower(Random random, BlockPos pos) {
      return FlowerBlock.Type.BLUE_ORCHID;
   }

   @Override
   public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      double var8 = FOLIAGE_NOISE.getNoise((double)x * 0.25, (double)z * 0.25);
      if (var8 > 0.0) {
         int var10 = x & 15;
         int var11 = z & 15;

         for(int var12 = 255; var12 >= 0; --var12) {
            if (blocks.get(var11, var12, var10).getBlock().getMaterial() != Material.AIR) {
               if (var12 == 62 && blocks.get(var11, var12, var10).getBlock() != Blocks.WATER) {
                  blocks.set(var11, var12, var10, Blocks.WATER.defaultState());
                  if (var8 < 0.12) {
                     blocks.set(var11, var12 + 1, var10, Blocks.LILY_PAD.defaultState());
                  }
               }
               break;
            }
         }
      }

      this.populate(world, random, blocks, x, z, noise);
   }
}
