package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SugarcaneFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 20; ++var4) {
         BlockPos var5 = pos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
         if (world.isAir(var5)) {
            BlockPos var6 = var5.down();
            if (world.getBlockState(var6.west()).getBlock().getMaterial() == Material.WATER
               || world.getBlockState(var6.east()).getBlock().getMaterial() == Material.WATER
               || world.getBlockState(var6.north()).getBlock().getMaterial() == Material.WATER
               || world.getBlockState(var6.south()).getBlock().getMaterial() == Material.WATER) {
               int var7 = 2 + random.nextInt(random.nextInt(3) + 1);

               for(int var8 = 0; var8 < var7; ++var8) {
                  if (Blocks.REEDS.isSupported(world, var5)) {
                     world.setBlockState(var5.up(var8), Blocks.REEDS.defaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
