package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IcePatchFeature extends Feature {
   private Block ice = Blocks.PACKED_ICE;
   private int size;

   public IcePatchFeature(int size) {
      this.size = size;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      while(world.isAir(pos) && pos.getY() > 2) {
         pos = pos.down();
      }

      if (world.getBlockState(pos).getBlock() != Blocks.SNOW) {
         return false;
      } else {
         int var4 = random.nextInt(this.size - 2) + 2;
         byte var5 = 1;

         for(int var6 = pos.getX() - var4; var6 <= pos.getX() + var4; ++var6) {
            for(int var7 = pos.getZ() - var4; var7 <= pos.getZ() + var4; ++var7) {
               int var8 = var6 - pos.getX();
               int var9 = var7 - pos.getZ();
               if (var8 * var8 + var9 * var9 <= var4 * var4) {
                  for(int var10 = pos.getY() - var5; var10 <= pos.getY() + var5; ++var10) {
                     BlockPos var11 = new BlockPos(var6, var10, var7);
                     Block var12 = world.getBlockState(var11).getBlock();
                     if (var12 == Blocks.DIRT || var12 == Blocks.SNOW || var12 == Blocks.ICE) {
                        world.setBlockState(var11, this.ice.defaultState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
