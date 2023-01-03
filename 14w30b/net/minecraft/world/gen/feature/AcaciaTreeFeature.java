package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AcaciaTreeFeature extends AbstractTreeFeature {
   public AcaciaTreeFeature(boolean bl) {
      super(bl);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(3) + random.nextInt(3) + 5;
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
            Block var20 = world.getBlockState(pos.down()).getBlock();
            if ((var20 == Blocks.GRASS || var20 == Blocks.DIRT) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);
               Direction var21 = Direction.Plane.HORIZONTAL.pick(random);
               int var22 = var4 - random.nextInt(4) - 1;
               int var23 = 3 - random.nextInt(3);
               int var10 = pos.getX();
               int var11 = pos.getZ();
               int var12 = 0;

               for(int var13 = 0; var13 < var4; ++var13) {
                  int var14 = pos.getY() + var13;
                  if (var13 >= var22 && var23 > 0) {
                     var10 += var21.getOffsetX();
                     var11 += var21.getOffsetZ();
                     --var23;
                  }

                  BlockPos var15 = new BlockPos(var10, var14, var11);
                  Material var16 = world.getBlockState(var15).getBlock().getMaterial();
                  if (var16 == Material.AIR || var16 == Material.LEAVES) {
                     this.setBlockWithMetadata(world, var15, Blocks.LOG2, PlanksBlock.Variant.ACACIA.getIndex() - 4);
                     var12 = var14;
                  }
               }

               BlockPos var27 = new BlockPos(var10, var12, var11);

               for(int var30 = -3; var30 <= 3; ++var30) {
                  for(int var33 = -3; var33 <= 3; ++var33) {
                     if (Math.abs(var30) != 3 || Math.abs(var33) != 3) {
                        this.placeLeaves(world, var27.add(var30, 0, var33));
                     }
                  }
               }

               var27 = var27.up();

               for(int var31 = -1; var31 <= 1; ++var31) {
                  for(int var34 = -1; var34 <= 1; ++var34) {
                     this.placeLeaves(world, var27.add(var31, 0, var34));
                  }
               }

               this.placeLeaves(world, var27.east(2));
               this.placeLeaves(world, var27.west(2));
               this.placeLeaves(world, var27.south(2));
               this.placeLeaves(world, var27.north(2));
               var10 = pos.getX();
               var11 = pos.getZ();
               Direction var29 = Direction.Plane.HORIZONTAL.pick(random);
               if (var29 != var21) {
                  int var32 = var22 - random.nextInt(2) - 1;
                  int var35 = 1 + random.nextInt(3);
                  var12 = 0;

                  for(int var36 = var32; var36 < var4 && var35 > 0; --var35) {
                     if (var36 >= 1) {
                        int var17 = pos.getY() + var36;
                        var10 += var29.getOffsetX();
                        var11 += var29.getOffsetZ();
                        BlockPos var18 = new BlockPos(var10, var17, var11);
                        Material var19 = world.getBlockState(var18).getBlock().getMaterial();
                        if (var19 == Material.AIR || var19 == Material.LEAVES) {
                           this.setBlockWithMetadata(world, var18, Blocks.LOG2, PlanksBlock.Variant.ACACIA.getIndex() - 4);
                           var12 = var17;
                        }
                     }

                     ++var36;
                  }

                  if (var12 > 0) {
                     BlockPos var37 = new BlockPos(var10, var12, var11);

                     for(int var39 = -2; var39 <= 2; ++var39) {
                        for(int var41 = -2; var41 <= 2; ++var41) {
                           if (Math.abs(var39) != 2 || Math.abs(var41) != 2) {
                              this.placeLeaves(world, var37.add(var39, 0, var41));
                           }
                        }
                     }

                     var37 = var37.up();

                     for(int var40 = -1; var40 <= 1; ++var40) {
                        for(int var42 = -1; var42 <= 1; ++var42) {
                           this.placeLeaves(world, var37.add(var40, 0, var42));
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

   private void placeLeaves(World world, BlockPos pos) {
      Material var3 = world.getBlockState(pos).getBlock().getMaterial();
      if (var3 == Material.AIR || var3 == Material.LEAVES) {
         this.setBlockWithMetadata(world, pos, Blocks.LEAVES2, 0);
      }
   }
}
