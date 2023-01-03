package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TreeFeature extends AbstractTreeFeature {
   private final int baseHeight;
   private final boolean placeVines;
   private final int logVariant;
   private final int leavesVariant;

   public TreeFeature(boolean bl) {
      this(bl, 4, 0, 0, false);
   }

   public TreeFeature(boolean notifyNeighbors, int baseHeight, int logVariant, int leavesVariant, boolean placeVines) {
      super(notifyNeighbors);
      this.baseHeight = baseHeight;
      this.logVariant = logVariant;
      this.leavesVariant = leavesVariant;
      this.placeVines = placeVines;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = random.nextInt(3) + this.baseHeight;
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
            Block var19 = world.getBlockState(pos.down()).getBlock();
            if ((var19 == Blocks.GRASS || var19 == Blocks.DIRT || var19 == Blocks.FARMLAND) && pos.getY() < 256 - var4 - 1) {
               this.setBlock(world, pos.down(), Blocks.DIRT);
               byte var20 = 3;
               byte var21 = 0;

               for(int var22 = pos.getY() - var20 + var4; var22 <= pos.getY() + var4; ++var22) {
                  int var10 = var22 - (pos.getY() + var4);
                  int var11 = var21 + 1 - var10 / 2;

                  for(int var12 = pos.getX() - var11; var12 <= pos.getX() + var11; ++var12) {
                     int var13 = var12 - pos.getX();

                     for(int var14 = pos.getZ() - var11; var14 <= pos.getZ() + var11; ++var14) {
                        int var15 = var14 - pos.getZ();
                        if (Math.abs(var13) != var11 || Math.abs(var15) != var11 || random.nextInt(2) != 0 && var10 != 0) {
                           BlockPos var16 = new BlockPos(var12, var22, var14);
                           Block var17 = world.getBlockState(var16).getBlock();
                           if (var17.getMaterial() == Material.AIR || var17.getMaterial() == Material.LEAVES) {
                              this.setBlockWithMetadata(world, var16, Blocks.LEAVES, this.leavesVariant);
                           }
                        }
                     }
                  }
               }

               for(int var23 = 0; var23 < var4; ++var23) {
                  Block var26 = world.getBlockState(pos.up(var23)).getBlock();
                  if (var26.getMaterial() == Material.AIR || var26.getMaterial() == Material.LEAVES) {
                     this.setBlockWithMetadata(world, pos.up(var23), Blocks.LOG, this.logVariant);
                     if (this.placeVines && var23 > 0) {
                        if (random.nextInt(3) > 0 && world.isAir(pos.add(-1, var23, 0))) {
                           this.setBlockWithMetadata(world, pos.add(-1, var23, 0), Blocks.VINE, VineBlock.EAST_METADATA);
                        }

                        if (random.nextInt(3) > 0 && world.isAir(pos.add(1, var23, 0))) {
                           this.setBlockWithMetadata(world, pos.add(1, var23, 0), Blocks.VINE, VineBlock.WEST_METADATA);
                        }

                        if (random.nextInt(3) > 0 && world.isAir(pos.add(0, var23, -1))) {
                           this.setBlockWithMetadata(world, pos.add(0, var23, -1), Blocks.VINE, VineBlock.SOUTH_METADATA);
                        }

                        if (random.nextInt(3) > 0 && world.isAir(pos.add(0, var23, 1))) {
                           this.setBlockWithMetadata(world, pos.add(0, var23, 1), Blocks.VINE, VineBlock.NORTH_METADATA);
                        }
                     }
                  }
               }

               if (this.placeVines) {
                  for(int var24 = pos.getY() - 3 + var4; var24 <= pos.getY() + var4; ++var24) {
                     int var27 = var24 - (pos.getY() + var4);
                     int var29 = 2 - var27 / 2;

                     for(int var31 = pos.getX() - var29; var31 <= pos.getX() + var29; ++var31) {
                        for(int var33 = pos.getZ() - var29; var33 <= pos.getZ() + var29; ++var33) {
                           BlockPos var34 = new BlockPos(var31, var24, var33);
                           if (world.getBlockState(var34).getBlock().getMaterial() == Material.LEAVES) {
                              BlockPos var35 = var34.west();
                              BlockPos var36 = var34.east();
                              BlockPos var37 = var34.north();
                              BlockPos var18 = var34.south();
                              if (random.nextInt(4) == 0 && world.getBlockState(var35).getBlock().getMaterial() == Material.AIR) {
                                 this.placeVine(world, var35, VineBlock.EAST_METADATA);
                              }

                              if (random.nextInt(4) == 0 && world.getBlockState(var36).getBlock().getMaterial() == Material.AIR) {
                                 this.placeVine(world, var36, VineBlock.WEST_METADATA);
                              }

                              if (random.nextInt(4) == 0 && world.getBlockState(var37).getBlock().getMaterial() == Material.AIR) {
                                 this.placeVine(world, var37, VineBlock.SOUTH_METADATA);
                              }

                              if (random.nextInt(4) == 0 && world.getBlockState(var18).getBlock().getMaterial() == Material.AIR) {
                                 this.placeVine(world, var18, VineBlock.NORTH_METADATA);
                              }
                           }
                        }
                     }
                  }

                  if (random.nextInt(5) == 0 && var4 > 5) {
                     for(int var25 = 0; var25 < 2; ++var25) {
                        for(int var28 = 0; var28 < 4; ++var28) {
                           if (random.nextInt(4 - var25) == 0) {
                              int var30 = random.nextInt(3);
                              Direction var32 = Direction.byIdHorizontal(var28).getOpposite();
                              this.setBlockWithMetadata(
                                 world,
                                 pos.add(var32.getOffsetX(), var4 - 5 + var25, var32.getOffsetZ()),
                                 Blocks.COCOA,
                                 var30 << 2 | Direction.byIdHorizontal(var28).getIdHorizontal()
                              );
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
