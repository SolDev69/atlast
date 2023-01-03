package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class RedstoneTorchBlock extends TorchBlock {
   private static Map BURNOUTS = Maps.newHashMap();
   private final boolean lit;

   private boolean hasBurnedOut(World world, BlockPos pos, boolean createEntry) {
      if (!BURNOUTS.containsKey(world)) {
         BURNOUTS.put(world, Lists.newArrayList());
      }

      List var4 = (List)BURNOUTS.get(world);
      if (createEntry) {
         var4.add(new RedstoneTorchBlock.BurnoutEntry(pos, world.getTime()));
      }

      int var5 = 0;

      for(int var6 = 0; var6 < var4.size(); ++var6) {
         RedstoneTorchBlock.BurnoutEntry var7 = (RedstoneTorchBlock.BurnoutEntry)var4.get(var6);
         if (var7.pos.equals(pos)) {
            if (++var5 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   protected RedstoneTorchBlock(boolean lit) {
      this.lit = lit;
      this.setTicksRandomly(true);
      this.setItemGroup(null);
   }

   @Override
   public int getTickRate(World world) {
      return 2;
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      if (this.lit) {
         for(Direction var7 : Direction.values()) {
            world.updateNeighbors(pos.offset(var7), this);
         }
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      if (this.lit) {
         for(Direction var7 : Direction.values()) {
            world.updateNeighbors(pos.offset(var7), this);
         }
      }
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return this.lit && state.get(FACING) != dir ? 15 : 0;
   }

   private boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
      Direction var4 = ((Direction)state.get(FACING)).getOpposite();
      return world.isEmittingPower(pos.offset(var4), var4);
   }

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      boolean var5 = this.shouldUnpower(world, pos, state);
      List var6 = (List)BURNOUTS.get(world);

      while(var6 != null && !var6.isEmpty() && world.getTime() - ((RedstoneTorchBlock.BurnoutEntry)var6.get(0)).time > 60L) {
         var6.remove(0);
      }

      if (this.lit) {
         if (var5) {
            world.setBlockState(pos, Blocks.UNLIT_REDSTONE_TORCH.defaultState().set(FACING, state.get(FACING)), 3);
            if (this.hasBurnedOut(world, pos, true)) {
               world.playSound(
                  (double)((float)pos.getX() + 0.5F),
                  (double)((float)pos.getY() + 0.5F),
                  (double)((float)pos.getZ() + 0.5F),
                  "random.fizz",
                  0.5F,
                  2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
               );

               for(int var7 = 0; var7 < 5; ++var7) {
                  double var8 = (double)pos.getX() + random.nextDouble() * 0.6 + 0.2;
                  double var10 = (double)pos.getY() + random.nextDouble() * 0.6 + 0.2;
                  double var12 = (double)pos.getZ() + random.nextDouble() * 0.6 + 0.2;
                  world.addParticle(ParticleType.SMOKE_NORMAL, var8, var10, var12, 0.0, 0.0, 0.0);
               }

               world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 160);
            }
         }
      } else if (!var5 && !this.hasBurnedOut(world, pos, false)) {
         world.setBlockState(pos, Blocks.REDSTONE_TORCH.defaultState().set(FACING, state.get(FACING)), 3);
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!this.canSurviveOrBreak(world, pos, state)) {
         if (this.lit == this.shouldUnpower(world, pos, state)) {
            world.scheduleTick(pos, this, this.getTickRate(world));
         }
      }
   }

   @Override
   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return dir == Direction.DOWN ? this.getEmittedWeakPower(world, pos, state, dir) : 0;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.REDSTONE_TORCH);
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (this.lit) {
         double var5 = (double)((float)pos.getX() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
         double var7 = (double)((float)pos.getY() + 0.7F) + (double)(random.nextFloat() - 0.5F) * 0.2;
         double var9 = (double)((float)pos.getZ() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
         Direction var11 = (Direction)state.get(FACING);
         if (var11.getAxis().isHorizontal()) {
            Direction var12 = var11.getOpposite();
            double var13 = 0.27F;
            var5 += 0.27F * (double)var12.getOffsetX();
            var7 += 0.22F;
            var9 += 0.27F * (double)var12.getOffsetZ();
         }

         world.addParticle(ParticleType.REDSTONE, var5, var7, var9, 0.0, 0.0, 0.0);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byBlock(Blocks.REDSTONE_TORCH);
   }

   @Override
   public boolean is(Block block) {
      return block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.REDSTONE_TORCH;
   }

   static class BurnoutEntry {
      BlockPos pos;
      long time;

      public BurnoutEntry(BlockPos pos, long time) {
         this.pos = pos;
         this.time = time;
      }
   }
}
