package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BirchTreeFeature extends AbstractTreeFeature {
   private boolean tall;

   public BirchTreeFeature(boolean notifyNeighbors, boolean tall) {
      super(notifyNeighbors);
      this.tall = tall;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(3) + 5;
      if (this.tall) {
         var4 += random.nextInt(7);
      }

      boolean var5 = true;
      if (pos.getY() >= 1 && pos.getY() + var4 + 1 <= 256) {
         for(int var6 = pos.getY(); var6 <= pos.getY() + 1 + var4; ++var6) {
            byte var7 = 1;
            if (var6 == pos.getY()) {
               var7 = 0;
            }

            if (var6 >= pos.getY() + 1 + var4 - 2) {
               var7 = 2;
            }

            for(int var8 = pos.getX() - var7; var8 <= pos.getX() + var7 && var5; ++var8) {
               for(int var9 = pos.getZ() - var7; var9 <= pos.getZ() + var7 && var5; ++var9) {
                  if (var6 >= 0 && var6 < 256) {
                     if (!this.canReplace(world.getBlockState(new BlockPos(var8, var6, var9)).getBlock())) {
                        var5 = false;
                     }
                  } else {
                     var5 = false;
                  }
               }
            }
         }

         if (!var5) {
            return false;
         } else {
            Block var16 = world.getBlockState(pos.down()).getBlock();
            if ((var16 == Blocks.GRASS || var16 == Blocks.DIRT || var16 == Blocks.FARMLAND) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);

               for(int var17 = pos.getY() - 3 + var4; var17 <= pos.getY() + var4; ++var17) {
                  int var19 = var17 - (pos.getY() + var4);
                  int var21 = 1 - var19 / 2;

                  for(int var10 = pos.getX() - var21; var10 <= pos.getX() + var21; ++var10) {
                     int var11 = var10 - pos.getX();

                     for(int var12 = pos.getZ() - var21; var12 <= pos.getZ() + var21; ++var12) {
                        int var13 = var12 - pos.getZ();
                        if (Math.abs(var11) != var21 || Math.abs(var13) != var21 || random.nextInt(2) != 0 && var19 != 0) {
                           BlockPos var14 = new BlockPos(var10, var17, var12);
                           Block var15 = world.getBlockState(var14).getBlock();
                           if (var15.getMaterial() == Material.AIR || var15.getMaterial() == Material.LEAVES) {
                              this.setBlockWithMetadata(world, var14, Blocks.LEAVES, PlanksBlock.Variant.BIRCH.getIndex());
                           }
                        }
                     }
                  }
               }

               for(int var18 = 0; var18 < var4; ++var18) {
                  Block var20 = world.getBlockState(pos.up(var18)).getBlock();
                  if (var20.getMaterial() == Material.AIR || var20.getMaterial() == Material.LEAVES) {
                     this.setBlockWithMetadata(world, pos.up(var18), Blocks.LOG, PlanksBlock.Variant.BIRCH.getIndex());
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }
}
