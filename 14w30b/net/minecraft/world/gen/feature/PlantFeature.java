package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlantFeature extends Feature {
   private PlantBlock plant;

   public PlantFeature(PlantBlock plant) {
      this.plant = plant;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var5) && (!world.dimension.isDark() || var5.getY() < 255) && this.plant.isSupported(world, var5, this.plant.defaultState())) {
            world.setBlockState(var5, this.plant.defaultState(), 2);
         }
      }

      return true;
   }
}
