package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FirePatchFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var5) && world.getBlockState(var5.down()).getBlock() == Blocks.NETHERRACK) {
            world.setBlockState(var5, Blocks.FIRE.defaultState(), 2);
         }
      }

      return true;
   }
}
