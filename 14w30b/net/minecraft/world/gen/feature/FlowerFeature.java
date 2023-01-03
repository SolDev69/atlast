package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlowerFeature extends Feature {
   private FlowerBlock flower;
   private BlockState state;

   public FlowerFeature(FlowerBlock flower, FlowerBlock.Type type) {
      this.set(flower, type);
   }

   public void set(FlowerBlock flower, FlowerBlock.Type type) {
      this.flower = flower;
      this.state = flower.defaultState().set(flower.getTypeProperty(), type);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var5) && (!world.dimension.isDark() || var5.getY() < 255) && this.flower.isSupported(world, var5, this.state)) {
            world.setBlockState(var5, this.state, 2);
         }
      }

      return true;
   }
}
