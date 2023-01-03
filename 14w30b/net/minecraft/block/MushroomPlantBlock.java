package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.HugeMushroomFeature;

public class MushroomPlantBlock extends PlantBlock implements Fertilizable {
   protected MushroomPlantBlock() {
      float var1 = 0.2F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var1 * 2.0F, 0.5F + var1);
      this.setTicksRandomly(true);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (random.nextInt(25) == 0) {
         int var5 = 5;
         boolean var6 = true;

         for(BlockPos var8 : BlockPos.iterateRegionMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
            if (world.getBlockState(var8).getBlock() == this) {
               if (--var5 <= 0) {
                  return;
               }
            }
         }

         BlockPos var9 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

         for(int var10 = 0; var10 < 4; ++var10) {
            if (world.isAir(var9) && this.isSupported(world, var9, this.defaultState())) {
               pos = var9;
            }

            var9 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
         }

         if (world.isAir(var9) && this.isSupported(world, var9, this.defaultState())) {
            world.setBlockState(var9, this.defaultState(), 2);
         }
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) && this.isSupported(world, pos, this.defaultState());
   }

   @Override
   protected boolean canPlantOn(Block block) {
      return block.isOpaque();
   }

   @Override
   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         BlockState var4 = world.getBlockState(pos.down());
         if (var4.getBlock() == Blocks.MYCELIUM) {
            return true;
         } else if (var4.getBlock() == Blocks.DIRT && var4.get(DirtBlock.VARIANT) == DirtBlock.Variant.PODZOL) {
            return true;
         } else {
            return world.getLight(pos) < 13 && this.canPlantOn(var4.getBlock());
         }
      } else {
         return false;
      }
   }

   public boolean grow(World world, BlockPos pos, BlockState state, Random random) {
      world.removeBlock(pos);
      HugeMushroomFeature var5 = null;
      if (this == Blocks.BROWN_MUSHROOM) {
         var5 = new HugeMushroomFeature(0);
      } else if (this == Blocks.RED_MUSHROOM) {
         var5 = new HugeMushroomFeature(1);
      }

      if (var5 != null && var5.place(world, random, pos)) {
         return true;
      } else {
         world.setBlockState(pos, state, 3);
         return false;
      }
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return true;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return (double)rand.nextFloat() < 0.4;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      this.grow(world, pos, state, rand);
   }
}
