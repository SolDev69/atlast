package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class UpsideDownGlowstoneClusterFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      if (!world.isAir(pos)) {
         return false;
      } else if (world.getBlockState(pos.up()).getBlock() != Blocks.NETHERRACK) {
         return false;
      } else {
         world.setBlockState(pos, Blocks.GLOWSTONE.defaultState(), 2);

         for(int var4 = 0; var4 < 1500; ++var4) {
            BlockPos var5 = pos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
            if (world.getBlockState(var5).getBlock().getMaterial() == Material.AIR) {
               int var6 = 0;

               for(Direction var10 : Direction.values()) {
                  if (world.getBlockState(var5.offset(var10)).getBlock() == Blocks.GLOWSTONE) {
                     ++var6;
                  }

                  if (var6 > 1) {
                     break;
                  }
               }

               if (var6 == 1) {
                  world.setBlockState(var5, Blocks.GLOWSTONE.defaultState(), 2);
               }
            }
         }

         return true;
      }
   }
}
