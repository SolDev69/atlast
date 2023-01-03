package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PineTreeFeature extends AbstractTreeFeature {
   public PineTreeFeature() {
      super(false);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(5) + 7;
      int var5 = var4 - random.nextInt(2) - 3;
      int var6 = var4 - var5;
      int var7 = 1 + random.nextInt(var6 + 1);
      boolean var8 = true;
      if (pos.getY() >= 1 && pos.getY() + var4 + 1 <= 256) {
         for(int var9 = pos.getY(); var9 <= pos.getY() + 1 + var4 && var8; ++var9) {
            int var10 = 1;
            if (var9 - pos.getY() < var5) {
               var10 = 0;
            } else {
               var10 = var7;
            }

            for(int var11 = pos.getX() - var10; var11 <= pos.getX() + var10 && var8; ++var11) {
               for(int var12 = pos.getZ() - var10; var12 <= pos.getZ() + var10 && var8; ++var12) {
                  if (var9 >= 0 && var9 < 256) {
                     if (!this.canReplace(world.getBlockState(new BlockPos(var11, var9, var12)).getBlock())) {
                        var8 = false;
                     }
                  } else {
                     var8 = false;
                  }
               }
            }
         }

         if (!var8) {
            return false;
         } else {
            Block var17 = world.getBlockState(pos.down()).getBlock();
            if ((var17 == Blocks.GRASS || var17 == Blocks.DIRT) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);
               int var19 = 0;

               for(int var20 = pos.getY() + var4; var20 >= pos.getY() + var5; --var20) {
                  for(int var22 = pos.getX() - var19; var22 <= pos.getX() + var19; ++var22) {
                     int var13 = var22 - pos.getX();

                     for(int var14 = pos.getZ() - var19; var14 <= pos.getZ() + var19; ++var14) {
                        int var15 = var14 - pos.getZ();
                        if (Math.abs(var13) != var19 || Math.abs(var15) != var19 || var19 <= 0) {
                           BlockPos var16 = new BlockPos(var22, var20, var14);
                           if (!world.getBlockState(var16).getBlock().isOpaque()) {
                              this.setBlockWithMetadata(world, var16, Blocks.LEAVES, PlanksBlock.Variant.SPRUCE.getIndex());
                           }
                        }
                     }
                  }

                  if (var19 >= 1 && var20 == pos.getY() + var5 + 1) {
                     --var19;
                  } else if (var19 < var7) {
                     ++var19;
                  }
               }

               for(int var21 = 0; var21 < var4 - 1; ++var21) {
                  Block var23 = world.getBlockState(pos.up(var21)).getBlock();
                  if (var23.getMaterial() == Material.AIR || var23.getMaterial() == Material.LEAVES) {
                     this.setBlockWithMetadata(world, pos.up(var21), Blocks.LOG, PlanksBlock.Variant.SPRUCE.getIndex());
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
