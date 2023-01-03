package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VineFeature extends Feature {
   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(; pos.getY() < 128; pos = pos.up()) {
         if (world.isAir(pos)) {
            for(Direction var7 : Direction.Plane.HORIZONTAL.get()) {
               if (Blocks.VINE.canPlace(world, pos, var7)) {
                  BlockState var8 = Blocks.VINE
                     .defaultState()
                     .set(VineBlock.NORTH, var7 == Direction.NORTH)
                     .set(VineBlock.EAST, var7 == Direction.EAST)
                     .set(VineBlock.SOUTH, var7 == Direction.SOUTH)
                     .set(VineBlock.WEST, var7 == Direction.WEST);
                  world.setBlockState(pos, var8, 2);
                  break;
               }
            }
         } else {
            pos = pos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
         }
      }

      return true;
   }
}
