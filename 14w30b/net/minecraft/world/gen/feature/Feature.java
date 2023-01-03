package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class Feature {
   private final boolean notifyNeighbors;

   public Feature() {
      this(false);
   }

   public Feature(boolean notifyNeighbors) {
      this.notifyNeighbors = notifyNeighbors;
   }

   public abstract boolean place(World world, Random random, BlockPos pos);

   public void prepare() {
   }

   protected void setBlock(World world, BlockPos pos, Block block) {
      this.setBlockWithMetadata(world, pos, block, 0);
   }

   protected void setBlockWithMetadata(World world, BlockPos pos, Block block, int metadata) {
      this.setBlockState(world, pos, block.getStateFromMetadata(metadata));
   }

   protected void setBlockState(World world, BlockPos pos, BlockState state) {
      if (this.notifyNeighbors) {
         world.setBlockState(pos, state, 3);
      } else {
         world.setBlockState(pos, state, 2);
      }
   }
}
