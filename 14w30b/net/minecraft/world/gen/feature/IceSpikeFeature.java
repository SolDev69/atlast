package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class IceSpikeFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      while(world.isAir(pos) && pos.getY() > 2) {
         pos = pos.down();
      }

      if (world.getBlockState(pos).getBlock() != Blocks.SNOW) {
         return false;
      } else {
         pos = pos.up(random.nextInt(4));
         int var4 = random.nextInt(4) + 7;
         int var5 = var4 / 4 + random.nextInt(2);
         if (var5 > 1 && random.nextInt(60) == 0) {
            pos = pos.up(10 + random.nextInt(30));
         }

         for(int var6 = 0; var6 < var4; ++var6) {
            float var7 = (1.0F - (float)var6 / (float)var4) * (float)var5;
            int var8 = MathHelper.ceil(var7);

            for(int var9 = -var8; var9 <= var8; ++var9) {
               float var10 = (float)MathHelper.abs(var9) - 0.25F;

               for(int var11 = -var8; var11 <= var8; ++var11) {
                  float var12 = (float)MathHelper.abs(var11) - 0.25F;
                  if ((var9 == 0 && var11 == 0 || !(var10 * var10 + var12 * var12 > var7 * var7))
                     && (var9 != -var8 && var9 != var8 && var11 != -var8 && var11 != var8 || !(random.nextFloat() > 0.75F))) {
                     Block var13 = world.getBlockState(pos.add(var9, var6, var11)).getBlock();
                     if (var13.getMaterial() == Material.AIR || var13 == Blocks.DIRT || var13 == Blocks.SNOW || var13 == Blocks.ICE) {
                        this.setBlock(world, pos.add(var9, var6, var11), Blocks.PACKED_ICE);
                     }

                     if (var6 != 0 && var8 > 1) {
                        var13 = world.getBlockState(pos.add(var9, -var6, var11)).getBlock();
                        if (var13.getMaterial() == Material.AIR || var13 == Blocks.DIRT || var13 == Blocks.SNOW || var13 == Blocks.ICE) {
                           this.setBlock(world, pos.add(var9, -var6, var11), Blocks.PACKED_ICE);
                        }
                     }
                  }
               }
            }
         }

         int var15 = var5 - 1;
         if (var15 < 0) {
            var15 = 0;
         } else if (var15 > 1) {
            var15 = 1;
         }

         for(int var16 = -var15; var16 <= var15; ++var16) {
            for(int var17 = -var15; var17 <= var15; ++var17) {
               BlockPos var18 = pos.add(var16, -1, var17);
               int var19 = 50;
               if (Math.abs(var16) == 1 && Math.abs(var17) == 1) {
                  var19 = random.nextInt(5);
               }

               while(var18.getY() > 50) {
                  Block var20 = world.getBlockState(var18).getBlock();
                  if (var20.getMaterial() != Material.AIR && var20 != Blocks.DIRT && var20 != Blocks.SNOW && var20 != Blocks.ICE && var20 != Blocks.PACKED_ICE) {
                     break;
                  }

                  this.setBlock(world, var18, Blocks.PACKED_ICE);
                  var18 = var18.down();
                  if (--var19 <= 0) {
                     var18 = var18.down(random.nextInt(5) + 1);
                     var19 = random.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
