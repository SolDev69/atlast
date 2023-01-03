package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BushFeature extends TreeFeature {
   private int leavesVariant;
   private int logVariant;

   public BushFeature(int logVariant, int leavesVariant) {
      super(false);
      this.logVariant = logVariant;
      this.leavesVariant = leavesVariant;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      Block var4;
      while(((var4 = world.getBlockState(pos).getBlock()).getMaterial() == Material.AIR || var4.getMaterial() == Material.LEAVES) && pos.getY() > 0) {
         pos = pos.down();
      }

      Block var5 = world.getBlockState(pos).getBlock();
      if (var5 == Blocks.DIRT || var5 == Blocks.GRASS) {
         pos = pos.up();
         this.setBlockWithMetadata(world, pos, Blocks.LOG, this.logVariant);

         for(int var6 = pos.getY(); var6 <= pos.getY() + 2; ++var6) {
            int var7 = var6 - pos.getY();
            int var8 = 2 - var7;

            for(int var9 = pos.getX() - var8; var9 <= pos.getX() + var8; ++var9) {
               int var10 = var9 - pos.getX();

               for(int var11 = pos.getZ() - var8; var11 <= pos.getZ() + var8; ++var11) {
                  int var12 = var11 - pos.getZ();
                  if (Math.abs(var10) != var8 || Math.abs(var12) != var8 || random.nextInt(2) != 0) {
                     BlockPos var13 = new BlockPos(var9, var6, var11);
                     if (!world.getBlockState(var13).getBlock().isOpaque()) {
                        this.setBlockWithMetadata(world, var13, Blocks.LEAVES, this.leavesVariant);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
