package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockVeinFeature extends Feature {
   private final Block block;
   private final int size;

   public BlockVeinFeature(Block block, int size) {
      super(false);
      this.block = block;
      this.size = size;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      for(; pos.getY() > 3; pos = pos.down()) {
         if (!world.isAir(pos.down())) {
            Block var4 = world.getBlockState(pos.down()).getBlock();
            if (var4 == Blocks.GRASS || var4 == Blocks.DIRT || var4 == Blocks.STONE) {
               break;
            }
         }
      }

      if (pos.getY() <= 3) {
         return false;
      } else {
         int var12 = this.size;

         for(int var5 = 0; var12 >= 0 && var5 < 3; ++var5) {
            int var6 = var12 + random.nextInt(2);
            int var7 = var12 + random.nextInt(2);
            int var8 = var12 + random.nextInt(2);
            float var9 = (float)(var6 + var7 + var8) * 0.333F + 0.5F;

            for(BlockPos var11 : BlockPos.iterateRegion(pos.add(-var6, -var7, -var8), pos.add(var6, var7, var8))) {
               if (var11.squaredDistanceTo(pos) <= (double)(var9 * var9)) {
                  world.setBlockState(var11, this.block.defaultState(), 4);
               }
            }

            pos = pos.add(-(var12 + 1) + random.nextInt(2 + var12 * 2), 0 - random.nextInt(2), -(var12 + 1) + random.nextInt(2 + var12 * 2));
         }

         return true;
      }
   }
}
