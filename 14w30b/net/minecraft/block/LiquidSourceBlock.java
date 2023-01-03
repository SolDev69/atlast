package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LiquidSourceBlock extends LiquidBlock {
   protected LiquidSourceBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setTicksRandomly(false);
      if (c_57ywipuwq == Material.LAVA) {
         this.setTicksRandomly(true);
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!this.checkSpreadCollisions(world, pos, state)) {
         this.convertToFlowing(world, pos, state);
      }
   }

   private void convertToFlowing(World world, BlockPos pos, BlockState state) {
      FlowingLiquidBlock var4 = getFlowing(this.material);
      world.setBlockState(pos, var4.defaultState().set(LEVEL, state.get(LEVEL)), 2);
      world.scheduleTick(pos, var4, this.getTickRate(world));
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (this.material == Material.LAVA) {
         if (world.getGameRules().getBoolean("doFireTick")) {
            int var5 = random.nextInt(3);
            if (var5 > 0) {
               BlockPos var6 = pos;

               for(int var7 = 0; var7 < var5; ++var7) {
                  var6 = var6.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                  Block var8 = world.getBlockState(var6).getBlock();
                  if (var8.material == Material.AIR) {
                     if (this.hasFlammableNeighbor(world, var6)) {
                        world.setBlockState(var6, Blocks.FIRE.defaultState());
                        return;
                     }
                  } else if (var8.material.blocksMovement()) {
                     return;
                  }
               }
            } else {
               for(int var9 = 0; var9 < 3; ++var9) {
                  BlockPos var10 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                  if (world.isAir(var10.up()) && this.isFlammable(world, var10)) {
                     world.setBlockState(var10.up(), Blocks.FIRE.defaultState());
                  }
               }
            }
         }
      }
   }

   protected boolean hasFlammableNeighbor(World world, BlockPos pos) {
      for(Direction var6 : Direction.values()) {
         if (this.isFlammable(world, pos.offset(var6))) {
            return true;
         }
      }

      return false;
   }

   private boolean isFlammable(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().getMaterial().isFlammable();
   }
}
