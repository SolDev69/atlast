package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TallPlantFeature extends Feature {
   private final BlockState state;

   public TallPlantFeature(TallPlantBlock.Type type) {
      this.state = Blocks.TALLGRASS.defaultState().set(TallPlantBlock.TYPE, type);
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      Block var4;
      while(((var4 = world.getBlockState(pos).getBlock()).getMaterial() == Material.AIR || var4.getMaterial() == Material.LEAVES) && pos.getY() > 0) {
         pos = pos.down();
      }

      for(int var5 = 0; var5 < 128; ++var5) {
         BlockPos var6 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if (world.isAir(var6) && Blocks.TALLGRASS.isSupported(world, var6, this.state)) {
            world.setBlockState(var6, this.state, 2);
         }
      }

      return true;
   }
}
