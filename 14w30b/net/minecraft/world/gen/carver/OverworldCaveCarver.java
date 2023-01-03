package net.minecraft.world.gen.carver;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.Generator;

public class OverworldCaveCarver extends Generator {
   protected void place(long seed, int chunkX, int chunkZ, BlockStateStorage blocks, double x, double y, double z) {
      this.placeBranch(seed, chunkX, chunkZ, blocks, x, y, z, 1.0F + this.random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   protected void placeBranch(
      long seed,
      int chunkX,
      int chunkZ,
      BlockStateStorage blocks,
      double x,
      double y,
      double z,
      float baseWidth,
      float yaw,
      float pitch,
      int branch,
      int branchCount,
      double widthHeightRatio
   ) {
      double var19 = (double)(chunkX * 16 + 8);
      double var21 = (double)(chunkZ * 16 + 8);
      float var23 = 0.0F;
      float var24 = 0.0F;
      Random var25 = new Random(seed);
      if (branchCount <= 0) {
         int var26 = this.range * 16 - 16;
         branchCount = var26 - var25.nextInt(var26 / 4);
      }

      boolean var57 = false;
      if (branch == -1) {
         branch = branchCount / 2;
         var57 = true;
      }

      int var27 = var25.nextInt(branchCount / 2) + branchCount / 4;

      for(boolean var28 = var25.nextInt(6) == 0; branch < branchCount; ++branch) {
         double var29 = 1.5 + (double)(MathHelper.sin((float)branch * (float) Math.PI / (float)branchCount) * baseWidth * 1.0F);
         double var31 = var29 * widthHeightRatio;
         float var33 = MathHelper.cos(pitch);
         float var34 = MathHelper.sin(pitch);
         x += (double)(MathHelper.cos(yaw) * var33);
         y += (double)var34;
         z += (double)(MathHelper.sin(yaw) * var33);
         if (var28) {
            pitch *= 0.92F;
         } else {
            pitch *= 0.7F;
         }

         pitch += var24 * 0.1F;
         yaw += var23 * 0.1F;
         var24 *= 0.9F;
         var23 *= 0.75F;
         var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2.0F;
         var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4.0F;
         if (!var57 && branch == var27 && baseWidth > 1.0F && branchCount > 0) {
            this.placeBranch(
               var25.nextLong(),
               chunkX,
               chunkZ,
               blocks,
               x,
               y,
               z,
               var25.nextFloat() * 0.5F + 0.5F,
               yaw - (float) (Math.PI / 2),
               pitch / 3.0F,
               branch,
               branchCount,
               1.0
            );
            this.placeBranch(
               var25.nextLong(),
               chunkX,
               chunkZ,
               blocks,
               x,
               y,
               z,
               var25.nextFloat() * 0.5F + 0.5F,
               yaw + (float) (Math.PI / 2),
               pitch / 3.0F,
               branch,
               branchCount,
               1.0
            );
            return;
         }

         if (var57 || var25.nextInt(4) != 0) {
            double var35 = x - var19;
            double var37 = z - var21;
            double var39 = (double)(branchCount - branch);
            double var41 = (double)(baseWidth + 2.0F + 16.0F);
            if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
               return;
            }

            if (!(x < var19 - 16.0 - var29 * 2.0)
               && !(z < var21 - 16.0 - var29 * 2.0)
               && !(x > var19 + 16.0 + var29 * 2.0)
               && !(z > var21 + 16.0 + var29 * 2.0)) {
               int var58 = MathHelper.floor(x - var29) - chunkX * 16 - 1;
               int var36 = MathHelper.floor(x + var29) - chunkX * 16 + 1;
               int var59 = MathHelper.floor(y - var31) - 1;
               int var38 = MathHelper.floor(y + var31) + 1;
               int var60 = MathHelper.floor(z - var29) - chunkZ * 16 - 1;
               int var40 = MathHelper.floor(z + var29) - chunkZ * 16 + 1;
               if (var58 < 0) {
                  var58 = 0;
               }

               if (var36 > 16) {
                  var36 = 16;
               }

               if (var59 < 1) {
                  var59 = 1;
               }

               if (var38 > 248) {
                  var38 = 248;
               }

               if (var60 < 0) {
                  var60 = 0;
               }

               if (var40 > 16) {
                  var40 = 16;
               }

               boolean var61 = false;

               for(int var42 = var58; !var61 && var42 < var36; ++var42) {
                  for(int var43 = var60; !var61 && var43 < var40; ++var43) {
                     for(int var44 = var38 + 1; !var61 && var44 >= var59 - 1; --var44) {
                        if (var44 >= 0 && var44 < 256) {
                           BlockState var45 = blocks.get(var42, var44, var43);
                           if (var45.getBlock() == Blocks.FLOWING_WATER || var45.getBlock() == Blocks.WATER) {
                              var61 = true;
                           }

                           if (var44 != var59 - 1 && var42 != var58 && var42 != var36 - 1 && var43 != var60 && var43 != var40 - 1) {
                              var44 = var59;
                           }
                        }
                     }
                  }
               }

               if (!var61) {
                  for(int var62 = var58; var62 < var36; ++var62) {
                     double var63 = ((double)(var62 + chunkX * 16) + 0.5 - x) / var29;

                     for(int var64 = var60; var64 < var40; ++var64) {
                        double var46 = ((double)(var64 + chunkZ * 16) + 0.5 - z) / var29;
                        boolean var48 = false;
                        if (var63 * var63 + var46 * var46 < 1.0) {
                           for(int var49 = var38; var49 > var59; --var49) {
                              double var50 = ((double)(var49 - 1) + 0.5 - y) / var31;
                              if (var50 > -0.7 && var63 * var63 + var50 * var50 + var46 * var46 < 1.0) {
                                 BlockState var52 = blocks.get(var62, var49, var64);
                                 BlockState var53 = (BlockState)Objects.firstNonNull(blocks.get(var62, var49 + 1, var64), Blocks.AIR.defaultState());
                                 if (var52.getBlock() == Blocks.GRASS || var52.getBlock() == Blocks.MYCELIUM) {
                                    var48 = true;
                                 }

                                 if (this.canReplace(var52, var53)) {
                                    if (var49 - 1 < 10) {
                                       blocks.set(var62, var49, var64, Blocks.LAVA.defaultState());
                                    } else {
                                       blocks.set(var62, var49, var64, Blocks.AIR.defaultState());
                                       if (var53.getBlock() == Blocks.SAND) {
                                          blocks.set(var62, var49 + 1, var64, Blocks.SANDSTONE.defaultState());
                                       }

                                       if (var48 && blocks.get(var62, var49 - 1, var64).getBlock() == Blocks.DIRT) {
                                          blocks.set(
                                             var62,
                                             var49 - 1,
                                             var64,
                                             this.world
                                                .getBiome(new BlockPos(var62 + chunkX * 16, 0, var64 + chunkZ * 16))
                                                .surfaceBlock
                                                .getBlock()
                                                .defaultState()
                                          );
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var57) {
                     break;
                  }
               }
            }
         }
      }
   }

   protected boolean canReplace(BlockState state, BlockState replacement) {
      if (state.getBlock() == Blocks.STONE) {
         return true;
      } else if (state.getBlock() == Blocks.DIRT) {
         return true;
      } else if (state.getBlock() == Blocks.GRASS) {
         return true;
      } else if (state.getBlock() == Blocks.HARDENED_CLAY) {
         return true;
      } else if (state.getBlock() == Blocks.STAINED_HARDENED_CLAY) {
         return true;
      } else if (state.getBlock() == Blocks.SANDSTONE) {
         return true;
      } else if (state.getBlock() == Blocks.MYCELIUM) {
         return true;
      } else if (state.getBlock() == Blocks.SNOW_LAYER) {
         return true;
      } else {
         return (state.getBlock() == Blocks.SAND || state.getBlock() == Blocks.GRAVEL) && replacement.getBlock().getMaterial() != Material.WATER;
      }
   }

   @Override
   protected void place(World world, int chunkX, int chunkZ, int centerChunkX, int centerChunkZ, BlockStateStorage blocks) {
      int var7 = this.random.nextInt(this.random.nextInt(this.random.nextInt(15) + 1) + 1);
      if (this.random.nextInt(7) != 0) {
         var7 = 0;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         double var9 = (double)(chunkX * 16 + this.random.nextInt(16));
         double var11 = (double)this.random.nextInt(this.random.nextInt(120) + 8);
         double var13 = (double)(chunkZ * 16 + this.random.nextInt(16));
         int var15 = 1;
         if (this.random.nextInt(4) == 0) {
            this.place(this.random.nextLong(), centerChunkX, centerChunkZ, blocks, var9, var11, var13);
            var15 += this.random.nextInt(4);
         }

         for(int var16 = 0; var16 < var15; ++var16) {
            float var17 = this.random.nextFloat() * (float) Math.PI * 2.0F;
            float var18 = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var19 = this.random.nextFloat() * 2.0F + this.random.nextFloat();
            if (this.random.nextInt(10) == 0) {
               var19 *= this.random.nextFloat() * this.random.nextFloat() * 3.0F + 1.0F;
            }

            this.placeBranch(this.random.nextLong(), centerChunkX, centerChunkZ, blocks, var9, var11, var13, var19, var17, var18, 0, 0, 1.0);
         }
      }
   }
}
