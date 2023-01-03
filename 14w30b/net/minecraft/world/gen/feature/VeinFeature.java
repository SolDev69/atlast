package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockPredicate;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class VeinFeature extends Feature {
   private final BlockState state;
   private final int size;
   private final Predicate replacePredicate;

   public VeinFeature(BlockState ore, int size) {
      this(ore, size, BlockPredicate.of(Blocks.STONE));
   }

   public VeinFeature(BlockState state, int size, Predicate replacePredicate) {
      this.state = state;
      this.size = size;
      this.replacePredicate = replacePredicate;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      float var4 = random.nextFloat() * (float) Math.PI;
      double var5 = (double)((float)(pos.getX() + 8) + MathHelper.sin(var4) * (float)this.size / 8.0F);
      double var7 = (double)((float)(pos.getX() + 8) - MathHelper.sin(var4) * (float)this.size / 8.0F);
      double var9 = (double)((float)(pos.getZ() + 8) + MathHelper.cos(var4) * (float)this.size / 8.0F);
      double var11 = (double)((float)(pos.getZ() + 8) - MathHelper.cos(var4) * (float)this.size / 8.0F);
      double var13 = (double)(pos.getY() + random.nextInt(3) - 2);
      double var15 = (double)(pos.getY() + random.nextInt(3) - 2);

      for(int var17 = 0; var17 < this.size; ++var17) {
         float var18 = (float)var17 / (float)this.size;
         double var19 = var5 + (var7 - var5) * (double)var18;
         double var21 = var13 + (var15 - var13) * (double)var18;
         double var23 = var9 + (var11 - var9) * (double)var18;
         double var25 = random.nextDouble() * (double)this.size / 16.0;
         double var27 = (double)(MathHelper.sin((float) Math.PI * var18) + 1.0F) * var25 + 1.0;
         double var29 = (double)(MathHelper.sin((float) Math.PI * var18) + 1.0F) * var25 + 1.0;
         int var31 = MathHelper.floor(var19 - var27 / 2.0);
         int var32 = MathHelper.floor(var21 - var29 / 2.0);
         int var33 = MathHelper.floor(var23 - var27 / 2.0);
         int var34 = MathHelper.floor(var19 + var27 / 2.0);
         int var35 = MathHelper.floor(var21 + var29 / 2.0);
         int var36 = MathHelper.floor(var23 + var27 / 2.0);

         for(int var37 = var31; var37 <= var34; ++var37) {
            double var38 = ((double)var37 + 0.5 - var19) / (var27 / 2.0);
            if (var38 * var38 < 1.0) {
               for(int var40 = var32; var40 <= var35; ++var40) {
                  double var41 = ((double)var40 + 0.5 - var21) / (var29 / 2.0);
                  if (var38 * var38 + var41 * var41 < 1.0) {
                     for(int var43 = var33; var43 <= var36; ++var43) {
                        double var44 = ((double)var43 + 0.5 - var23) / (var27 / 2.0);
                        if (var38 * var38 + var41 * var41 + var44 * var44 < 1.0) {
                           BlockPos var46 = new BlockPos(var37, var40, var43);
                           if (this.replacePredicate.apply(world.getBlockState(var46))) {
                              world.setBlockState(var46, this.state, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
