package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClayPatchFeature extends Feature {
   private Block clay = Blocks.CLAY;
   private int size;

   public ClayPatchFeature(int size) {
      this.size = size;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      if (world.getBlockState(pos).getBlock().getMaterial() != Material.WATER) {
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
                     if (var12 == Blocks.DIRT || var12 == Blocks.CLAY) {
                        world.setBlockState(var11, this.clay.defaultState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
