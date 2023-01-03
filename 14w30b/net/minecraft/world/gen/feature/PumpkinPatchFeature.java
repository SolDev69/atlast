package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinPatchFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var5) && world.getBlockState(var5.down()).getBlock() == Blocks.GRASS && Blocks.PUMPKIN.canSurvive(world, var5)) {
            world.setBlockState(var5, Blocks.PUMPKIN.defaultState().set(PumpkinBlock.FACING, Direction.Plane.HORIZONTAL.pick(random)), 2);
         }
      }

      return true;
   }
}
