package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FlowingLiquidBlock extends LiquidBlock {
   int adjacentSources;

   protected FlowingLiquidBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
   }

   private void convertToSource(World world, BlockPos pos, BlockState state) {
      world.setBlockState(pos, getSource(this.material).defaultState().set(LEVEL, state.get(LEVEL)), 2);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      int var5 = state.get(LEVEL);
      byte var6 = 1;
      if (this.material == Material.LAVA && !world.dimension.yeetsWater()) {
         var6 = 2;
      }

      int var7 = this.getTickRate(world);
      if (var5 > 0) {
         int var8 = -100;
         this.adjacentSources = 0;

         for(Direction var10 : Direction.Plane.HORIZONTAL) {
            var8 = this.decreaseLevel(world, pos.offset(var10), var8);
         }

         int var15 = var8 + var6;
         if (var15 >= 8 || var8 < 0) {
            var15 = -1;
         }

         if (this.getLevel(world, pos.up()) >= 0) {
            int var17 = this.getLevel(world, pos.up());
            if (var17 >= 8) {
               var15 = var17;
            } else {
               var15 = var17 + 8;
            }
         }

         if (this.adjacentSources >= 2 && this.material == Material.WATER) {
            BlockState var18 = world.getBlockState(pos.down());
            if (var18.getBlock().getMaterial().isSolid()) {
               var15 = 0;
            } else if (var18.getBlock().getMaterial() == this.material && var18.get(LEVEL) == 0) {
               var15 = 0;
            }
         }

         if (this.material == Material.LAVA && var5 < 8 && var15 < 8 && var15 > var5 && random.nextInt(4) != 0) {
            var7 *= 4;
         }

         if (var15 == var5) {
            this.convertToSource(world, pos, state);
         } else {
            var5 = var15;
            if (var15 < 0) {
               world.removeBlock(pos);
            } else {
               state = state.set(LEVEL, var15);
               world.setBlockState(pos, state, 2);
               world.scheduleTick(pos, this, var7);
               world.updateNeighbors(pos, this);
            }
         }
      } else {
         this.convertToSource(world, pos, state);
      }

      BlockState var14 = world.getBlockState(pos.down());
      if (this.canSpreadTo(world, pos.down(), var14)) {
         if (this.material == Material.LAVA && world.getBlockState(pos.down()).getBlock().getMaterial() == Material.WATER) {
            world.setBlockState(pos.down(), Blocks.STONE.defaultState());
            this.fizz(world, pos.down());
            return;
         }

         if (var5 >= 8) {
            this.spreadTo(world, pos.down(), var14, var5);
         } else {
            this.spreadTo(world, pos.down(), var14, var5 + 8);
         }
      } else if (var5 >= 0 && (var5 == 0 || this.blocksSpreading(world, pos.down(), var14))) {
         Set var16 = this.getSpreadDirections(world, pos);
         int var19 = var5 + var6;
         if (var5 >= 8) {
            var19 = 1;
         }

         if (var19 >= 8) {
            return;
         }

         for(Direction var12 : var16) {
            this.spreadTo(world, pos.offset(var12), world.getBlockState(pos.offset(var12)), var19);
         }
      }
   }

   private void spreadTo(World world, BlockPos pos, BlockState state, int level) {
      if (this.canSpreadTo(world, pos, state)) {
         if (state.getBlock() != Blocks.AIR) {
            if (this.material == Material.LAVA) {
               this.fizz(world, pos);
            } else {
               state.getBlock().dropItems(world, pos, state, 0);
            }
         }

         world.setBlockState(pos, this.defaultState().set(LEVEL, level), 3);
      }
   }

   private int getDistanceToGap(World world, BlockPos pos, int distance, Direction fromDir) {
      int var5 = 1000;

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         if (var7 != fromDir) {
            BlockPos var8 = pos.offset(var7);
            BlockState var9 = world.getBlockState(var8);
            if (!this.blocksSpreading(world, var8, var9) && (var9.getBlock().getMaterial() != this.material || var9.get(LEVEL) > 0)) {
               if (!this.blocksSpreading(world, var8.down(), var9)) {
                  return distance;
               }

               if (distance < 4) {
                  int var10 = this.getDistanceToGap(world, var8, distance + 1, var7.getOpposite());
                  if (var10 < var5) {
                     var5 = var10;
                  }
               }
            }
         }
      }

      return var5;
   }

   private Set getSpreadDirections(World world, BlockPos pos) {
      int var3 = 1000;
      EnumSet var4 = EnumSet.noneOf(Direction.class);

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         BlockPos var7 = pos.offset(var6);
         BlockState var8 = world.getBlockState(var7);
         if (!this.blocksSpreading(world, var7, var8) && (var8.getBlock().getMaterial() != this.material || var8.get(LEVEL) > 0)) {
            int var9;
            if (this.blocksSpreading(world, var7.down(), world.getBlockState(var7.down()))) {
               var9 = this.getDistanceToGap(world, var7, 1, var6.getOpposite());
            } else {
               var9 = 0;
            }

            if (var9 < var3) {
               var4.clear();
            }

            if (var9 <= var3) {
               var4.add(var6);
               var3 = var9;
            }
         }
      }

      return var4;
   }

   private boolean blocksSpreading(World world, BlockPos pos, BlockState state) {
      Block var4 = world.getBlockState(pos).getBlock();
      if (var4 == Blocks.WOODEN_DOOR || var4 == Blocks.IRON_DOOR || var4 == Blocks.STANDING_SIGN || var4 == Blocks.LADDER || var4 == Blocks.REEDS) {
         return true;
      } else {
         return var4.material == Material.PORTAL ? true : var4.material.blocksMovement();
      }
   }

   protected int decreaseLevel(World world, BlockPos pos, int level) {
      int var4 = this.getLevel(world, pos);
      if (var4 < 0) {
         return level;
      } else {
         if (var4 == 0) {
            ++this.adjacentSources;
         }

         if (var4 >= 8) {
            var4 = 0;
         }

         return level >= 0 && var4 >= level ? level : var4;
      }
   }

   private boolean canSpreadTo(World world, BlockPos pos, BlockState state) {
      Material var4 = state.getBlock().getMaterial();
      return var4 != this.material && var4 != Material.LAVA && !this.blocksSpreading(world, pos, state);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      if (!this.checkSpreadCollisions(world, pos, state)) {
         world.scheduleTick(pos, this, this.getTickRate(world));
      }
   }
}
