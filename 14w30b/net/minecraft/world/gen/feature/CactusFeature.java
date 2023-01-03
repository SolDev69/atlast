package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CactusFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 10; ++var4) {
         BlockPos var5 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var5)) {
            int var6 = 1 + random.nextInt(random.nextInt(3) + 1);

            for(int var7 = 0; var7 < var6; ++var7) {
               if (Blocks.CACTUS.isSupported(world, var5)) {
                  world.setBlockState(var5.up(var7), Blocks.CACTUS.defaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
