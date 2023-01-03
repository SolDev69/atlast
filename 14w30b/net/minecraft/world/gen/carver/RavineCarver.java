package net.minecraft.world.gen.carver;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.Generator;

public class RavineCarver extends Generator {
   private float[] buffer = new float[1024];

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
      Random var19 = new Random(seed);
      double var20 = (double)(chunkX * 16 + 8);
      double var22 = (double)(chunkZ * 16 + 8);
      float var24 = 0.0F;
      float var25 = 0.0F;
      if (branchCount <= 0) {
         int var26 = this.range * 16 - 16;
         branchCount = var26 - var19.nextInt(var26 / 4);
      }

      boolean var55 = false;
      if (branch == -1) {
         branch = branchCount / 2;
         var55 = true;
      }

      float var27 = 1.0F;

      for(int var28 = 0; var28 < 256; ++var28) {
         if (var28 == 0 || var19.nextInt(3) == 0) {
            var27 = 1.0F + var19.nextFloat() * var19.nextFloat() * 1.0F;
         }

         this.buffer[var28] = var27 * var27;
      }

      for(; branch < branchCount; ++branch) {
         double var56 = 1.5 + (double)(MathHelper.sin((float)branch * (float) Math.PI / (float)branchCount) * baseWidth * 1.0F);
         double var30 = var56 * widthHeightRatio;
         var56 *= (double)var19.nextFloat() * 0.25 + 0.75;
         var30 *= (double)var19.nextFloat() * 0.25 + 0.75;
         float var32 = MathHelper.cos(pitch);
         float var33 = MathHelper.sin(pitch);
         x += (double)(MathHelper.cos(yaw) * var32);
         y += (double)var33;
         z += (double)(MathHelper.sin(yaw) * var32);
         pitch *= 0.7F;
         pitch += var25 * 0.05F;
         yaw += var24 * 0.05F;
         var25 *= 0.8F;
         var24 *= 0.5F;
         var25 += (var19.nextFloat() - var19.nextFloat()) * var19.nextFloat() * 2.0F;
         var24 += (var19.nextFloat() - var19.nextFloat()) * var19.nextFloat() * 4.0F;
         if (var55 || var19.nextInt(4) != 0) {
            double var34 = x - var20;
            double var36 = z - var22;
            double var38 = (double)(branchCount - branch);
            double var40 = (double)(baseWidth + 2.0F + 16.0F);
            if (var34 * var34 + var36 * var36 - var38 * var38 > var40 * var40) {
               return;
            }

            if (!(x < var20 - 16.0 - var56 * 2.0)
               && !(z < var22 - 16.0 - var56 * 2.0)
               && !(x > var20 + 16.0 + var56 * 2.0)
               && !(z > var22 + 16.0 + var56 * 2.0)) {
               int var59 = MathHelper.floor(x - var56) - chunkX * 16 - 1;
               int var35 = MathHelper.floor(x + var56) - chunkX * 16 + 1;
               int var60 = MathHelper.floor(y - var30) - 1;
               int var37 = MathHelper.floor(y + var30) + 1;
               int var61 = MathHelper.floor(z - var56) - chunkZ * 16 - 1;
               int var39 = MathHelper.floor(z + var56) - chunkZ * 16 + 1;
               if (var59 < 0) {
                  var59 = 0;
               }

               if (var35 > 16) {
                  var35 = 16;
               }

               if (var60 < 1) {
                  var60 = 1;
               }

               if (var37 > 248) {
                  var37 = 248;
               }

               if (var61 < 0) {
                  var61 = 0;
               }

               if (var39 > 16) {
                  var39 = 16;
               }

               boolean var62 = false;

               for(int var41 = var59; !var62 && var41 < var35; ++var41) {
                  for(int var42 = var61; !var62 && var42 < var39; ++var42) {
                     for(int var43 = var37 + 1; !var62 && var43 >= var60 - 1; --var43) {
                        if (var43 >= 0 && var43 < 256) {
                           BlockState var44 = blocks.get(var41, var43, var42);
                           if (var44.getBlock() == Blocks.FLOWING_WATER || var44.getBlock() == Blocks.WATER) {
                              var62 = true;
                           }

                           if (var43 != var60 - 1 && var41 != var59 && var41 != var35 - 1 && var42 != var61 && var42 != var39 - 1) {
                              var43 = var60;
                           }
                        }
                     }
                  }
               }

               if (!var62) {
                  for(int var63 = var59; var63 < var35; ++var63) {
                     double var64 = ((double)(var63 + chunkX * 16) + 0.5 - x) / var56;

                     for(int var65 = var61; var65 < var39; ++var65) {
                        double var45 = ((double)(var65 + chunkZ * 16) + 0.5 - z) / var56;
                        boolean var47 = false;
                        if (var64 * var64 + var45 * var45 < 1.0) {
                           for(int var48 = var37; var48 > var60; --var48) {
                              double var49 = ((double)(var48 - 1) + 0.5 - y) / var30;
                              if ((var64 * var64 + var45 * var45) * (double)this.buffer[var48 - 1] + var49 * var49 / 6.0 < 1.0) {
                                 BlockState var51 = blocks.get(var63, var48, var65);
                                 if (var51.getBlock() == Blocks.GRASS) {
                                    var47 = true;
                                 }

                                 if (var51.getBlock() == Blocks.STONE || var51.getBlock() == Blocks.DIRT || var51.getBlock() == Blocks.GRASS) {
                                    if (var48 - 1 < 10) {
                                       blocks.set(var63, var48, var65, Blocks.FLOWING_LAVA.defaultState());
                                    } else {
                                       blocks.set(var63, var48, var65, Blocks.AIR.defaultState());
                                       if (var47 && blocks.get(var63, var48 - 1, var65).getBlock() == Blocks.DIRT) {
                                          blocks.set(
                                             var63,
                                             var48 - 1,
                                             var65,
                                             this.world.getBiome(new BlockPos(var63 + chunkX * 16, 0, var65 + chunkZ * 16)).surfaceBlock
                                          );
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var55) {
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   protected void place(World world, int chunkX, int chunkZ, int centerChunkX, int centerChunkZ, BlockStateStorage blocks) {
      if (this.random.nextInt(50) == 0) {
         double var7 = (double)(chunkX * 16 + this.random.nextInt(16));
         double var9 = (double)(this.random.nextInt(this.random.nextInt(40) + 8) + 20);
         double var11 = (double)(chunkZ * 16 + this.random.nextInt(16));
         byte var13 = 1;

         for(int var14 = 0; var14 < var13; ++var14) {
            float var15 = this.random.nextFloat() * (float) Math.PI * 2.0F;
            float var16 = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var17 = (this.random.nextFloat() * 2.0F + this.random.nextFloat()) * 2.0F;
            this.placeBranch(this.random.nextLong(), centerChunkX, centerChunkZ, blocks, var7, var9, var11, var17, var15, var16, 0, 0, 3.0);
         }
      }
   }
}
