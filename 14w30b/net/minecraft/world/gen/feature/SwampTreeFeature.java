package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwampTreeFeature extends AbstractTreeFeature {
   public SwampTreeFeature() {
      super(false);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(4) + 5;

      while(world.getBlockState(pos.down()).getBlock().getMaterial() == Material.WATER) {
         pos = pos.down();
      }

      boolean var5 = true;
      if (pos.getY() >= 1 && pos.getY() + var4 + 1 <= 256) {
         for(int var6 = pos.getY(); var6 <= pos.getY() + 1 + var4; ++var6) {
            byte var7 = 1;
            if (var6 == pos.getY()) {
               var7 = 0;
            }

            if (var6 >= pos.getY() + 1 + var4 - 2) {
               var7 = 3;
            }

            for(int var8 = pos.getX() - var7; var8 <= pos.getX() + var7 && var5; ++var8) {
               for(int var9 = pos.getZ() - var7; var9 <= pos.getZ() + var7 && var5; ++var9) {
                  if (var6 >= 0 && var6 < 256) {
                     Block var10 = world.getBlockState(new BlockPos(var8, var6, var9)).getBlock();
                     if (var10.getMaterial() != Material.AIR && var10.getMaterial() != Material.LEAVES) {
                        if (var10 != Blocks.WATER && var10 != Blocks.FLOWING_WATER) {
                           var5 = false;
                        } else if (var6 > pos.getY()) {
                           var5 = false;
                        }
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
            Block var17 = world.getBlockState(pos.down()).getBlock();
            if ((var17 == Blocks.GRASS || var17 == Blocks.DIRT) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);

               for(int var18 = pos.getY() - 3 + var4; var18 <= pos.getY() + var4; ++var18) {
                  int var21 = var18 - (pos.getY() + var4);
                  int var24 = 2 - var21 / 2;

                  for(int var26 = pos.getX() - var24; var26 <= pos.getX() + var24; ++var26) {
                     int var11 = var26 - pos.getX();

                     for(int var12 = pos.getZ() - var24; var12 <= pos.getZ() + var24; ++var12) {
                        int var13 = var12 - pos.getZ();
                        if (Math.abs(var11) != var24 || Math.abs(var13) != var24 || random.nextInt(2) != 0 && var21 != 0) {
                           BlockPos var14 = new BlockPos(var26, var18, var12);
                           if (!world.getBlockState(var14).getBlock().isOpaque()) {
                              this.setBlock(world, var14, Blocks.LEAVES);
                           }
                        }
                     }
                  }
               }

               for(int var19 = 0; var19 < var4; ++var19) {
                  Block var22 = world.getBlockState(pos.up(var19)).getBlock();
                  if (var22.getMaterial() == Material.AIR || var22.getMaterial() == Material.LEAVES || var22 == Blocks.FLOWING_WATER || var22 == Blocks.WATER) {
                     this.setBlock(world, pos.up(var19), Blocks.LOG);
                  }
               }

               for(int var20 = pos.getY() - 3 + var4; var20 <= pos.getY() + var4; ++var20) {
                  int var23 = var20 - (pos.getY() + var4);
                  int var25 = 2 - var23 / 2;

                  for(int var27 = pos.getX() - var25; var27 <= pos.getX() + var25; ++var27) {
                     for(int var28 = pos.getZ() - var25; var28 <= pos.getZ() + var25; ++var28) {
                        BlockPos var29 = new BlockPos(var27, var20, var28);
                        if (world.getBlockState(var29).getBlock().getMaterial() == Material.LEAVES) {
                           BlockPos var30 = var29.west();
                           BlockPos var31 = var29.east();
                           BlockPos var15 = var29.north();
                           BlockPos var16 = var29.south();
                           if (random.nextInt(4) == 0 && world.getBlockState(var30).getBlock().getMaterial() == Material.AIR) {
                              this.placeVine(world, var30, VineBlock.EAST_METADATA);
                           }

                           if (random.nextInt(4) == 0 && world.getBlockState(var31).getBlock().getMaterial() == Material.AIR) {
                              this.placeVine(world, var31, VineBlock.WEST_METADATA);
                           }

                           if (random.nextInt(4) == 0 && world.getBlockState(var15).getBlock().getMaterial() == Material.AIR) {
                              this.placeVine(world, var15, VineBlock.SOUTH_METADATA);
                           }

                           if (random.nextInt(4) == 0 && world.getBlockState(var16).getBlock().getMaterial() == Material.AIR) {
                              this.placeVine(world, var16, VineBlock.NORTH_METADATA);
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

   private void placeVine(World world, BlockPos pos, int metadata) {
      this.setBlockWithMetadata(world, pos, Blocks.VINE, metadata);
      int var4 = 4;

      for(BlockPos var5 = pos.down(); world.getBlockState(var5).getBlock().getMaterial() == Material.AIR && var4 > 0; --var4) {
         this.setBlockWithMetadata(world, var5, Blocks.VINE, metadata);
         var5 = var5.down();
      }
   }
}
