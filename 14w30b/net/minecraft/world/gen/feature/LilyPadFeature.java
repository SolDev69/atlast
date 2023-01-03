package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LilyPadFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 10; ++var4) {
         int var5 = pos.getX() + random.nextInt(8) - random.nextInt(8);
         int var6 = pos.getY() + random.nextInt(4) - random.nextInt(4);
         int var7 = pos.getZ() + random.nextInt(8) - random.nextInt(8);
         if (world.isAir(new BlockPos(var5, var6, var7)) && Blocks.LILY_PAD.canSurvive(world, new BlockPos(var5, var6, var7))) {
            world.setBlockState(new BlockPos(var5, var6, var7), Blocks.LILY_PAD.defaultState(), 2);
         }
      }

      return true;
   }
}
