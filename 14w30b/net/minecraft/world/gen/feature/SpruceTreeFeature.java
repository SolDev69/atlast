package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpruceTreeFeature extends AbstractTreeFeature {
   public SpruceTreeFeature(boolean bl) {
      super(bl);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(4) + 6;
      int var5 = 1 + random.nextInt(2);
      int var6 = var4 - var5;
      int var7 = 2 + random.nextInt(2);
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
                     Block var13 = world.getBlockState(new BlockPos(var11, var9, var12)).getBlock();
                     if (var13.getMaterial() != Material.AIR && var13.getMaterial() != Material.LEAVES) {
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
            Block var20 = world.getBlockState(pos.down()).getBlock();
            if ((var20 == Blocks.GRASS || var20 == Blocks.DIRT || var20 == Blocks.FARMLAND) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);
               int var22 = random.nextInt(2);
               int var23 = 1;
               byte var24 = 0;

               for(int var25 = 0; var25 <= var6; ++var25) {
                  int var14 = pos.getY() + var4 - var25;

                  for(int var15 = pos.getX() - var22; var15 <= pos.getX() + var22; ++var15) {
                     int var16 = var15 - pos.getX();

                     for(int var17 = pos.getZ() - var22; var17 <= pos.getZ() + var22; ++var17) {
                        int var18 = var17 - pos.getZ();
                        if (Math.abs(var16) != var22 || Math.abs(var18) != var22 || var22 <= 0) {
                           BlockPos var19 = new BlockPos(var15, var14, var17);
                           if (!world.getBlockState(var19).getBlock().isOpaque()) {
                              this.setBlockWithMetadata(world, var19, Blocks.LEAVES, PlanksBlock.Variant.SPRUCE.getIndex());
                           }
                        }
                     }
                  }

                  if (var22 >= var23) {
                     var22 = var24;
                     var24 = 1;
                     if (++var23 > var7) {
                        var23 = var7;
                     }
                  } else {
                     ++var22;
                  }
               }

               int var26 = random.nextInt(3);

               for(int var27 = 0; var27 < var4 - var26; ++var27) {
                  Block var28 = world.getBlockState(pos.up(var27)).getBlock();
                  if (var28.getMaterial() == Material.AIR || var28.getMaterial() == Material.LEAVES) {
                     this.setBlockWithMetadata(world, pos.up(var27), Blocks.LOG, PlanksBlock.Variant.SPRUCE.getIndex());
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
