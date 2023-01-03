package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
   protected AbstractPressurePlateBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setItemGroup(ItemGroup.REDSTONE);
      this.setTicksRandomly(true);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.updateBoundingBox(world.getBlockState(pos));
   }

   protected void updateBoundingBox(BlockState state) {
      boolean var2 = this.getPowerLevel(state) > 0;
      float var3 = 0.0625F;
      if (var2) {
         this.setShape(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.setShape(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }
   }

   @Override
   public int getTickRate(World world) {
      return 20;
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return true;
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return this.canSitOnTop(world, pos.down());
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!this.canSitOnTop(world, pos.down())) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }
   }

   private boolean canSitOnTop(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos) || world.getBlockState(pos).getBlock() instanceof FenceBlock;
   }

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         int var5 = this.getPowerLevel(state);
         if (var5 > 0) {
            this.updatePowerLevel(world, pos, state, var5);
         }
      }
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!world.isClient) {
         int var5 = this.getPowerLevel(state);
         if (var5 == 0) {
            this.updatePowerLevel(world, pos, state, var5);
         }
      }
   }

   protected void updatePowerLevel(World world, BlockPos pos, BlockState state, int power) {
      int var5 = this.calculatePowerLevel(world, pos);
      boolean var6 = power > 0;
      boolean var7 = var5 > 0;
      if (power != var5) {
         state = this.setPowerLevel(state, var5);
         world.setBlockState(pos, state, 2);
         this.updateNeighbors(world, pos);
         world.onRegionChanged(pos, pos);
      }

      if (!var7 && var6) {
         world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      } else if (var7 && !var6) {
         world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (var7) {
         world.scheduleTick(pos, this, this.getTickRate(world));
      }
   }

   protected Box createBoundingBox(BlockPos pos) {
      float var2 = 0.125F;
      return new Box(
         (double)((float)pos.getX() + 0.125F),
         (double)pos.getY(),
         (double)((float)pos.getZ() + 0.125F),
         (double)((float)(pos.getX() + 1) - 0.125F),
         (double)pos.getY() + 0.25,
         (double)((float)(pos.getZ() + 1) - 0.125F)
      );
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      if (this.getPowerLevel(state) > 0) {
         this.updateNeighbors(world, pos);
      }

      super.onRemoved(world, pos, state);
   }

   protected void updateNeighbors(World world, BlockPos pos) {
      world.updateNeighbors(pos, this);
      world.updateNeighbors(pos.down(), this);
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return this.getPowerLevel(state);
   }

   @Override
   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return dir == Direction.UP ? this.getPowerLevel(state) : 0;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public void setBlockItemBounds() {
      float var1 = 0.5F;
      float var2 = 0.125F;
      float var3 = 0.5F;
      this.setShape(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
   }

   @Override
   public int getPistonMoveBehavior() {
      return 1;
   }

   protected abstract int calculatePowerLevel(World world, BlockPos pos);

   protected abstract int getPowerLevel(BlockState state);

   protected abstract BlockState setPowerLevel(BlockState state, int power);
}
