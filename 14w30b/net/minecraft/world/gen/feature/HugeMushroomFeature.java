package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HugeMushroomFeature extends Feature {
   private int variant = -1;

   public HugeMushroomFeature(int variant) {
      super(true);
      this.variant = variant;
   }

   public HugeMushroomFeature() {
      super(false);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(2);
      if (this.variant >= 0) {
         var4 = this.variant;
      }

      int var5 = random.nextInt(3) + 4;
      boolean var6 = true;
      if (pos.getY() >= 1 && pos.getY() + var5 + 1 < 256) {
         for(int var7 = pos.getY(); var7 <= pos.getY() + 1 + var5; ++var7) {
            byte var8 = 3;
            if (var7 <= pos.getY() + 3) {
               var8 = 0;
            }

            for(int var9 = pos.getX() - var8; var9 <= pos.getX() + var8 && var6; ++var9) {
               for(int var10 = pos.getZ() - var8; var10 <= pos.getZ() + var8 && var6; ++var10) {
                  if (var7 >= 0 && var7 < 256) {
                     Block var11 = world.getBlockState(new BlockPos(var9, var7, var10)).getBlock();
                     if (var11.getMaterial() != Material.AIR && var11.getMaterial() != Material.LEAVES) {
                        var6 = false;
                     }
                  } else {
                     var6 = false;
                  }
               }
            }
         }

         if (!var6) {
            return false;
         } else {
            Block var15 = world.getBlockState(pos.down()).getBlock();
            if (var15 != Blocks.DIRT && var15 != Blocks.GRASS && var15 != Blocks.MYCELIUM) {
               return false;
            } else {
               int var16 = pos.getY() + var5;
               if (var4 == 1) {
                  var16 = pos.getY() + var5 - 3;
               }

               for(int var17 = var16; var17 <= pos.getY() + var5; ++var17) {
                  int var19 = 1;
                  if (var17 < pos.getY() + var5) {
                     ++var19;
                  }

                  if (var4 == 0) {
                     var19 = 3;
                  }

                  for(int var21 = pos.getX() - var19; var21 <= pos.getX() + var19; ++var21) {
                     for(int var12 = pos.getZ() - var19; var12 <= pos.getZ() + var19; ++var12) {
                        int var13 = 5;
                        if (var21 == pos.getX() - var19) {
                           --var13;
                        }

                        if (var21 == pos.getX() + var19) {
                           ++var13;
                        }

                        if (var12 == pos.getZ() - var19) {
                           var13 -= 3;
                        }

                        if (var12 == pos.getZ() + var19) {
                           var13 += 3;
                        }

                        if (var4 == 0 || var17 < pos.getY() + var5) {
                           if ((var21 == pos.getX() - var19 || var21 == pos.getX() + var19) && (var12 == pos.getZ() - var19 || var12 == pos.getZ() + var19)) {
                              continue;
                           }

                           if (var21 == pos.getX() - (var19 - 1) && var12 == pos.getZ() - var19) {
                              var13 = 1;
                           }

                           if (var21 == pos.getX() - var19 && var12 == pos.getZ() - (var19 - 1)) {
                              var13 = 1;
                           }

                           if (var21 == pos.getX() + (var19 - 1) && var12 == pos.getZ() - var19) {
                              var13 = 3;
                           }

                           if (var21 == pos.getX() + var19 && var12 == pos.getZ() - (var19 - 1)) {
                              var13 = 3;
                           }

                           if (var21 == pos.getX() - (var19 - 1) && var12 == pos.getZ() + var19) {
                              var13 = 7;
                           }

                           if (var21 == pos.getX() - var19 && var12 == pos.getZ() + (var19 - 1)) {
                              var13 = 7;
                           }

                           if (var21 == pos.getX() + (var19 - 1) && var12 == pos.getZ() + var19) {
                              var13 = 9;
                           }

                           if (var21 == pos.getX() + var19 && var12 == pos.getZ() + (var19 - 1)) {
                              var13 = 9;
                           }
                        }

                        if (var13 == 5 && var17 < pos.getY() + var5) {
                           var13 = 0;
                        }

                        if (var13 != 0 || pos.getY() >= pos.getY() + var5 - 1) {
                           BlockPos var14 = new BlockPos(var21, var17, var12);
                           if (!world.getBlockState(var14).getBlock().isOpaque()) {
                              this.setBlockWithMetadata(world, var14, Block.byRawId(Block.getRawId(Blocks.BROWN_MUSHROOM_BLOCK) + var4), var13);
                           }
                        }
                     }
                  }
               }

               for(int var18 = 0; var18 < var5; ++var18) {
                  Block var20 = world.getBlockState(pos.up(var18)).getBlock();
                  if (!var20.isOpaque()) {
                     this.setBlockWithMetadata(world, pos.up(var18), Block.byRawId(Block.getRawId(Blocks.BROWN_MUSHROOM_BLOCK) + var4), 10);
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }
}
