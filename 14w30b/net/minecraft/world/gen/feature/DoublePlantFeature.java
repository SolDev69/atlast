package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DoublePlantFeature extends Feature {
   private DoublePlantBlock.Variant variant;

   public void setMetadata(DoublePlantBlock.Variant variant) {
      this.variant = variant;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      boolean var4 = false;

      for(int var5 = 0; var5 < 64; ++var5) {
         BlockPos var6 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var6) && (!world.dimension.isDark() || var6.getY() < 254) && Blocks.DOUBLE_PLANT.canSurvive(world, var6)) {
            Blocks.DOUBLE_PLANT.setVariant(world, var6, this.variant, 2);
            var4 = true;
         }
      }

      return var4;
   }
}
