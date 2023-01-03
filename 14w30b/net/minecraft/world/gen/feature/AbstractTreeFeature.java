package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractTreeFeature extends Feature {
   public AbstractTreeFeature(boolean bl) {
      super(bl);
   }

   protected boolean canReplace(Block block) {
      return block.getMaterial() == Material.AIR
         || block.getMaterial() == Material.LEAVES
         || block == Blocks.GRASS
         || block == Blocks.DIRT
         || block == Blocks.LOG
         || block == Blocks.LOG2
         || block == Blocks.SAPLING
         || block == Blocks.VINE;
   }

   public void placeSoil(World world, Random random, BlockPos pos) {
   }
}
