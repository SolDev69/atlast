package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class RedstoneDiodeBlock extends HorizontalFacingBlock {
   protected final boolean powered;

   protected RedstoneDiodeBlock(boolean powered) {
      super(Material.DECORATION);
      this.powered = powered;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos.down()) ? super.canSurvive(world, pos) : false;
   }

   public boolean hasSupport(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos.down());
   }

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!this.isLocked(world, pos, state)) {
         boolean var5 = this.shouldBePowered(world, pos, state);
         if (this.powered && !var5) {
            world.setBlockState(pos, this.setPowered(state), 2);
         } else if (!this.powered) {
            world.setBlockState(pos, this.setUnpowered(state), 2);
            if (!var5) {
               world.scheduleTick(pos, this.setUnpowered(state).getBlock(), this.getTickingDelay(state), -1);
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return face.getAxis() != Direction.Axis.Y;
   }

   protected boolean isPowered(BlockState state) {
      return this.powered;
   }

   @Override
   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return this.getEmittedWeakPower(world, pos, state, dir);
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      if (!this.isPowered(state)) {
         return 0;
      } else {
         return state.get(FACING) == dir ? this.getPowerLevel(world, pos, state) : 0;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (this.hasSupport(world, pos)) {
         this.updatePowered(world, pos, state);
      } else {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);

         for(Direction var8 : Direction.values()) {
            world.updateNeighbors(pos.offset(var8), this);
         }
      }
   }

   protected void updatePowered(World world, BlockPos pos, BlockState state) {
      if (!this.isLocked(world, pos, state)) {
         boolean var4 = this.shouldBePowered(world, pos, state);
         if ((this.powered && !var4 || !this.powered && var4) && !world.willTickThisTick(pos, this)) {
            byte var5 = -1;
            if (this.shouldPrioritize(world, pos, state)) {
               var5 = -3;
            } else if (this.powered) {
               var5 = -2;
            }

            world.scheduleTick(pos, this, this.getDelay(state), var5);
         }
      }
   }

   public boolean isLocked(IWorld world, BlockPos pos, BlockState state) {
      return false;
   }

   protected boolean shouldBePowered(World world, BlockPos pos, BlockState state) {
      return this.getReceivedPower(world, pos, state) > 0;
   }

   protected int getReceivedPower(World world, BlockPos pos, BlockState state) {
      Direction var4 = (Direction)state.get(FACING);
      BlockPos var5 = pos.offset(var4);
      int var6 = world.getEmittedPower(var5, var4);
      if (var6 >= 15) {
         return var6;
      } else {
         BlockState var7 = world.getBlockState(var5);
         return Math.max(var6, var7.getBlock() == Blocks.REDSTONE_WIRE ? var7.get(RedstoneWireBlock.POWER) : 0);
      }
   }

   protected int getReceivedSidePower(IWorld world, BlockPos pos, BlockState state) {
      Direction var4 = (Direction)state.get(FACING);
      Direction var5 = var4.clockwiseY();
      Direction var6 = var4.counterClockwiseY();
      return Math.max(this.getPowerFromSide(world, pos.offset(var5), var5), this.getPowerFromSide(world, pos.offset(var6), var6));
   }

   protected int getPowerFromSide(IWorld world, BlockPos pos, Direction dir) {
      BlockState var4 = world.getBlockState(pos);
      Block var5 = var4.getBlock();
      if (this.isValidSideInput(var5)) {
         return var5 == Blocks.REDSTONE_WIRE ? var4.get(RedstoneWireBlock.POWER) : world.getEmittedStrongPower(pos, dir);
      } else {
         return 0;
      }
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection().getOpposite());
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      if (this.shouldBePowered(world, pos, state)) {
         world.scheduleTick(pos, this, 1);
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.updateNeighbors(world, pos, state);
   }

   protected void updateNeighbors(World world, BlockPos x, BlockState state) {
      Direction var4 = (Direction)state.get(FACING);
      BlockPos var5 = x.offset(var4.getOpposite());
      world.updateBlock(var5, this);
      world.updateNeighborsExcept(var5, this, var4);
   }

   @Override
   public void onBroken(World world, BlockPos pos, BlockState state) {
      if (this.powered) {
         for(Direction var7 : Direction.values()) {
            world.updateNeighbors(pos.offset(var7), this);
         }
      }

      super.onBroken(world, pos, state);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   protected boolean isValidSideInput(Block block) {
      return block.isPowerSource();
   }

   protected int getPowerLevel(IWorld world, BlockPos pos, BlockState state) {
      return 15;
   }

   public static boolean isDiode(Block block) {
      return Blocks.REPEATER.isSameDiode(block) || Blocks.COMPARATOR.isSameDiode(block);
   }

   public boolean isSameDiode(Block block) {
      return block == this.setUnpowered(this.defaultState()).getBlock() || block == this.setPowered(this.defaultState()).getBlock();
   }

   public boolean shouldPrioritize(World world, BlockPos pos, BlockState state) {
      Direction var4 = ((Direction)state.get(FACING)).getOpposite();
      BlockPos var5 = pos.offset(var4);
      if (isDiode(world.getBlockState(var5).getBlock())) {
         return world.getBlockState(var5).get(FACING) != var4;
      } else {
         return false;
      }
   }

   protected int getTickingDelay(BlockState state) {
      return this.getDelay(state);
   }

   protected abstract int getDelay(BlockState state);

   protected abstract BlockState setUnpowered(BlockState state);

   protected abstract BlockState setPowered(BlockState state);

   @Override
   public boolean is(Block block) {
      return this.isSameDiode(block);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
