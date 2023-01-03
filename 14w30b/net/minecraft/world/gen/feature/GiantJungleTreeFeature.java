package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GiantJungleTreeFeature extends GiantTreeFeature {
   public GiantJungleTreeFeature(boolean bl, int i, int j, int k, int l) {
      super(bl, i, j, k, l);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = this.getRandomHeight(random);
      if (!this.canGrow(world, random, pos, var4)) {
         return false;
      } else {
         this.placeLeaves(world, pos.up(var4), 2);

         for(int var5 = pos.getY() + var4 - 2 - random.nextInt(4); var5 > pos.getY() + var4 / 2; var5 -= 2 + random.nextInt(4)) {
            float var6 = random.nextFloat() * (float) Math.PI * 2.0F;
            int var7 = pos.getX() + (int)(0.5F + MathHelper.cos(var6) * 4.0F);
            int var8 = pos.getZ() + (int)(0.5F + MathHelper.sin(var6) * 4.0F);

            for(int var9 = 0; var9 < 5; ++var9) {
               var7 = pos.getX() + (int)(1.5F + MathHelper.cos(var6) * (float)var9);
               var8 = pos.getZ() + (int)(1.5F + MathHelper.sin(var6) * (float)var9);
               this.setBlockWithMetadata(world, new BlockPos(var7, var5 - 3 + var9 / 2, var8), Blocks.LOG, this.logVariant);
            }

            int var16 = 1 + random.nextInt(2);
            int var10 = var5;

            for(int var11 = var5 - var16; var11 <= var10; ++var11) {
               int var12 = var11 - var10;
               this.placeLeavesRing(world, new BlockPos(var7, var11, var8), 1 - var12);
            }
         }

         for(int var13 = 0; var13 < var4; ++var13) {
            BlockPos var14 = pos.up(var13);
            if (this.canReplace(world.getBlockState(var14).getBlock().getMaterial())) {
               this.setBlockWithMetadata(world, var14, Blocks.LOG, this.logVariant);
               if (var13 > 0) {
                  this.placeVine(world, random, var14.west(), VineBlock.EAST_METADATA);
                  this.placeVine(world, random, var14.north(), VineBlock.SOUTH_METADATA);
               }
            }

            if (var13 < var4 - 1) {
               BlockPos var15 = var14.east();
               if (this.canReplace(world.getBlockState(var15).getBlock().getMaterial())) {
                  this.setBlockWithMetadata(world, var15, Blocks.LOG, this.logVariant);
                  if (var13 > 0) {
                     this.placeVine(world, random, var15.east(), VineBlock.WEST_METADATA);
                     this.placeVine(world, random, var15.north(), VineBlock.SOUTH_METADATA);
                  }
               }

               BlockPos var17 = var14.south().east();
               if (this.canReplace(world.getBlockState(var17).getBlock().getMaterial())) {
                  this.setBlockWithMetadata(world, var17, Blocks.LOG, this.logVariant);
                  if (var13 > 0) {
                     this.placeVine(world, random, var17.east(), VineBlock.WEST_METADATA);
                     this.placeVine(world, random, var17.south(), VineBlock.NORTH_METADATA);
                  }
               }

               BlockPos var18 = var14.south();
               if (this.canReplace(world.getBlockState(var18).getBlock().getMaterial())) {
                  this.setBlockWithMetadata(world, var18, Blocks.LOG, this.logVariant);
                  if (var13 > 0) {
                     this.placeVine(world, random, var18.west(), VineBlock.EAST_METADATA);
                     this.placeVine(world, random, var18.south(), VineBlock.NORTH_METADATA);
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean canReplace(Material material) {
      return material == Material.AIR || material == Material.LEAVES;
   }

   private void placeVine(World world, Random random, BlockPos pos, int metadata) {
      if (random.nextInt(3) > 0 && world.isAir(pos)) {
         this.setBlockWithMetadata(world, pos, Blocks.VINE, metadata);
      }
   }

   private void placeLeaves(World world, BlockPos pos, int radius) {
      byte var4 = 2;

      for(int var5 = -var4; var5 <= 0; ++var5) {
         this.placeLeavesRingStrict(world, pos.up(var5), radius + 1 - var5);
      }
   }
}
