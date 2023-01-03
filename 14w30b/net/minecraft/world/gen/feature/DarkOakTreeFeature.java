package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DarkOakTreeFeature extends AbstractTreeFeature {
   public DarkOakTreeFeature(boolean bl) {
      super(bl);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(3) + random.nextInt(2) + 6;
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
            Block var18 = world.getBlockState(pos.down()).getBlock();
            if ((var18 == Blocks.GRASS || var18 == Blocks.DIRT) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);
               this.setBlock(world, pos.add(1, -1, 0), Blocks.DIRT);
               this.setBlock(world, pos.add(1, -1, 1), Blocks.DIRT);
               this.setBlock(world, pos.add(0, -1, 1), Blocks.DIRT);
               Direction var19 = Direction.Plane.HORIZONTAL.pick(random);
               int var20 = var4 - random.nextInt(4);
               int var21 = 2 - random.nextInt(3);
               int var10 = pos.getX();
               int var11 = pos.getZ();
               int var12 = 0;

               for(int var13 = 0; var13 < var4; ++var13) {
                  int var14 = pos.getY() + var13;
                  if (var13 >= var20 && var21 > 0) {
                     var10 += var19.getOffsetX();
                     var11 += var19.getOffsetZ();
                     --var21;
                  }

                  BlockPos var15 = new BlockPos(var10, var14, var11);
                  Material var16 = world.getBlockState(var15).getBlock().getMaterial();
                  if (var16 == Material.AIR || var16 == Material.LEAVES) {
                     this.setBlockWithMetadata(world, var15, Blocks.LOG2, PlanksBlock.Variant.DARK_OAK.getIndex() - 4);
                     this.setBlockWithMetadata(world, var15.east(), Blocks.LOG2, PlanksBlock.Variant.DARK_OAK.getIndex() - 4);
                     this.setBlockWithMetadata(world, var15.south(), Blocks.LOG2, PlanksBlock.Variant.DARK_OAK.getIndex() - 4);
                     this.setBlockWithMetadata(world, var15.east().south(), Blocks.LOG2, PlanksBlock.Variant.DARK_OAK.getIndex() - 4);
                     var12 = var14;
                  }
               }

               for(int var22 = -2; var22 <= 0; ++var22) {
                  for(int var25 = -2; var25 <= 0; ++var25) {
                     byte var28 = -1;
                     this.placeLeaves(world, var10 + var22, var12 + var28, var11 + var25);
                     this.placeLeaves(world, 1 + var10 - var22, var12 + var28, var11 + var25);
                     this.placeLeaves(world, var10 + var22, var12 + var28, 1 + var11 - var25);
                     this.placeLeaves(world, 1 + var10 - var22, var12 + var28, 1 + var11 - var25);
                     if ((var22 > -2 || var25 > -1) && (var22 != -1 || var25 != -2)) {
                        var28 = 1;
                        this.placeLeaves(world, var10 + var22, var12 + var28, var11 + var25);
                        this.placeLeaves(world, 1 + var10 - var22, var12 + var28, var11 + var25);
                        this.placeLeaves(world, var10 + var22, var12 + var28, 1 + var11 - var25);
                        this.placeLeaves(world, 1 + var10 - var22, var12 + var28, 1 + var11 - var25);
                     }
                  }
               }

               if (random.nextBoolean()) {
                  this.placeLeaves(world, var10, var12 + 2, var11);
                  this.placeLeaves(world, var10 + 1, var12 + 2, var11);
                  this.placeLeaves(world, var10 + 1, var12 + 2, var11 + 1);
                  this.placeLeaves(world, var10, var12 + 2, var11 + 1);
               }

               for(int var23 = -3; var23 <= 4; ++var23) {
                  for(int var26 = -3; var26 <= 4; ++var26) {
                     if ((var23 != -3 || var26 != -3)
                        && (var23 != -3 || var26 != 4)
                        && (var23 != 4 || var26 != -3)
                        && (var23 != 4 || var26 != 4)
                        && (Math.abs(var23) < 3 || Math.abs(var26) < 3)) {
                        this.placeLeaves(world, var10 + var23, var12, var11 + var26);
                     }
                  }
               }

               for(int var24 = -1; var24 <= 2; ++var24) {
                  for(int var27 = -1; var27 <= 2; ++var27) {
                     if ((var24 < 0 || var24 > 1 || var27 < 0 || var27 > 1) && random.nextInt(3) <= 0) {
                        int var30 = random.nextInt(3) + 2;

                        for(int var31 = 0; var31 < var30; ++var31) {
                           this.setBlockWithMetadata(
                              world,
                              new BlockPos(pos.getX() + var24, var12 - var31 - 1, pos.getZ() + var27),
                              Blocks.LOG2,
                              PlanksBlock.Variant.DARK_OAK.getIndex() - 4
                           );
                        }

                        for(int var32 = -1; var32 <= 1; ++var32) {
                           for(int var17 = -1; var17 <= 1; ++var17) {
                              this.placeLeaves(world, var10 + var24 + var32, var12 - 0, var11 + var27 + var17);
                           }
                        }

                        for(int var33 = -2; var33 <= 2; ++var33) {
                           for(int var34 = -2; var34 <= 2; ++var34) {
                              if (Math.abs(var33) != 2 || Math.abs(var34) != 2) {
                                 this.placeLeaves(world, var10 + var24 + var33, var12 - 1, var11 + var27 + var34);
                              }
                           }
                        }
                     }
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

   private void placeLeaves(World world, int x, int y, int z) {
      Block var5 = world.getBlockState(new BlockPos(x, y, z)).getBlock();
      if (var5.getMaterial() == Material.AIR) {
         this.setBlockWithMetadata(world, new BlockPos(x, y, z), Blocks.LEAVES2, 1);
      }
   }
}
