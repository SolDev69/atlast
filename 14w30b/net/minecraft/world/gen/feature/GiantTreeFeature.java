package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class GiantTreeFeature extends AbstractTreeFeature {
   protected final int baseHeight;
   protected final int logVariant;
   protected final int leavesVariant;
   protected int bonusHeight;

   public GiantTreeFeature(boolean notifyNeighbors, int baseHeight, int bonusHeight, int logVariant, int leavesVariant) {
      super(notifyNeighbors);
      this.baseHeight = baseHeight;
      this.bonusHeight = bonusHeight;
      this.logVariant = logVariant;
      this.leavesVariant = leavesVariant;
   }

   protected int getRandomHeight(Random random) {
      int var2 = random.nextInt(3) + this.baseHeight;
      if (this.bonusHeight > 1) {
         var2 += random.nextInt(this.bonusHeight);
      }

      return var2;
   }

   private boolean canGrow(World world, BlockPos pos, int height) {
      boolean var4 = true;
      if (pos.getY() >= 1 && pos.getY() + height + 1 <= 256) {
         for(int var5 = 0; var5 <= 1 + height; ++var5) {
            byte var6 = 2;
            if (var5 == 0) {
               var6 = 1;
            } else if (var5 >= 1 + height - 2) {
               var6 = 2;
            }

            for(int var7 = -var6; var7 <= var6 && var4; ++var7) {
               for(int var8 = -var6; var8 <= var6 && var4; ++var8) {
                  if (pos.getY() + var5 < 0 || pos.getY() + var5 >= 256 || !this.canReplace(world.getBlockState(pos.add(var7, var5, var8)).getBlock())) {
                     var4 = false;
                  }
               }
            }
         }

         return var4;
      } else {
         return false;
      }
   }

   private boolean checkAndPlaceDirtUnderneath(BlockPos pos, World world) {
      BlockPos var3 = pos.down();
      Block var4 = world.getBlockState(var3).getBlock();
      if ((var4 == Blocks.GRASS || var4 == Blocks.DIRT) && pos.getY() >= 2) {
         world.setBlockState(var3, Blocks.DIRT.defaultState(), 2);
         world.setBlockState(var3.east(), Blocks.DIRT.defaultState(), 2);
         world.setBlockState(var3.south(), Blocks.DIRT.defaultState(), 2);
         world.setBlockState(var3.south().east(), Blocks.DIRT.defaultState(), 2);
         return true;
      } else {
         return false;
      }
   }

   protected boolean canGrow(World world, Random random, BlockPos pos, int height) {
      return this.canGrow(world, pos, height) && this.checkAndPlaceDirtUnderneath(pos, world);
   }

   protected void placeLeavesRingStrict(World world, BlockPos pos, int radius) {
      int var4 = radius * radius;

      for(int var5 = -radius; var5 <= radius + 1; ++var5) {
         for(int var6 = -radius; var6 <= radius + 1; ++var6) {
            int var7 = var5 - 1;
            int var8 = var6 - 1;
            if (var5 * var5 + var6 * var6 <= var4
               || var7 * var7 + var8 * var8 <= var4
               || var5 * var5 + var8 * var8 <= var4
               || var7 * var7 + var6 * var6 <= var4) {
               BlockPos var9 = pos.add(var5, 0, var6);
               Material var10 = world.getBlockState(var9).getBlock().getMaterial();
               if (var10 == Material.AIR || var10 == Material.LEAVES) {
                  this.setBlockWithMetadata(world, var9, Blocks.LEAVES, this.leavesVariant);
               }
            }
         }
      }
   }

   protected void placeLeavesRing(World world, BlockPos pos, int radius) {
      int var4 = radius * radius;

      for(int var5 = -radius; var5 <= radius; ++var5) {
         for(int var6 = -radius; var6 <= radius; ++var6) {
            if (var5 * var5 + var6 * var6 <= var4) {
               BlockPos var7 = pos.add(var5, 0, var6);
               Material var8 = world.getBlockState(var7).getBlock().getMaterial();
               if (var8 == Material.AIR || var8 == Material.LEAVES) {
                  this.setBlockWithMetadata(world, var7, Blocks.LEAVES, this.leavesVariant);
               }
            }
         }
      }
   }
}
