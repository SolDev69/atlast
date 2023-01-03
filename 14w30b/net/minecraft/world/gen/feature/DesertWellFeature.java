package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DesertWellFeature extends Feature {
   private static final BlockStatePredicate REPLACE_PREDICATE = BlockStatePredicate.of(Blocks.SAND)
      .with(SandBlock.VARIANT, Predicates.equalTo(SandBlock.Variant.SAND));
   private final BlockState wellBlock = Blocks.STONE_SLAB
      .defaultState()
      .set(StoneSlabBlock.VARIANT, StoneSlabBlock.Variant.SAND)
      .set(SlabBlock.HALF, SlabBlock.Half.BOTTOM);
   private final BlockState wellSlab = Blocks.SANDSTONE.defaultState();
   private final BlockState wellLiquid = Blocks.FLOWING_WATER.defaultState();

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      while(world.isAir(pos) && pos.getY() > 2) {
         pos = pos.down();
      }

      if (!REPLACE_PREDICATE.apply(world.getBlockState(pos))) {
         return false;
      } else {
         for(int var4 = -2; var4 <= 2; ++var4) {
            for(int var5 = -2; var5 <= 2; ++var5) {
               if (world.isAir(pos.add(var4, -1, var5)) && world.isAir(pos.add(var4, -2, var5))) {
                  return false;
               }
            }
         }

         for(int var7 = -1; var7 <= 0; ++var7) {
            for(int var12 = -2; var12 <= 2; ++var12) {
               for(int var6 = -2; var6 <= 2; ++var6) {
                  world.setBlockState(pos.add(var12, var7, var6), this.wellSlab, 2);
               }
            }
         }

         world.setBlockState(pos, this.wellLiquid, 2);

         for(Direction var13 : Direction.Plane.HORIZONTAL) {
            world.setBlockState(pos.offset(var13), this.wellLiquid, 2);
         }

         for(int var9 = -2; var9 <= 2; ++var9) {
            for(int var14 = -2; var14 <= 2; ++var14) {
               if (var9 == -2 || var9 == 2 || var14 == -2 || var14 == 2) {
                  world.setBlockState(pos.add(var9, 1, var14), this.wellSlab, 2);
               }
            }
         }

         world.setBlockState(pos.add(2, 1, 0), this.wellBlock, 2);
         world.setBlockState(pos.add(-2, 1, 0), this.wellBlock, 2);
         world.setBlockState(pos.add(0, 1, 2), this.wellBlock, 2);
         world.setBlockState(pos.add(0, 1, -2), this.wellBlock, 2);

         for(int var10 = -1; var10 <= 1; ++var10) {
            for(int var15 = -1; var15 <= 1; ++var15) {
               if (var10 == 0 && var15 == 0) {
                  world.setBlockState(pos.add(var10, 4, var15), this.wellSlab, 2);
               } else {
                  world.setBlockState(pos.add(var10, 4, var15), this.wellBlock, 2);
               }
            }
         }

         for(int var11 = 1; var11 <= 3; ++var11) {
            world.setBlockState(pos.add(-1, var11, -1), this.wellSlab, 2);
            world.setBlockState(pos.add(-1, var11, 1), this.wellSlab, 2);
            world.setBlockState(pos.add(1, var11, -1), this.wellSlab, 2);
            world.setBlockState(pos.add(1, var11, 1), this.wellSlab, 2);
         }

         return true;
      }
   }
}
