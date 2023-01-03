package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TripwireHookBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
   public static final BooleanProperty SUSPENDED = BooleanProperty.of("suspended");

   public TripwireHookBlock() {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(POWERED, false).set(ATTACHED, false).set(SUSPENDED, false));
      this.setItemGroup(ItemGroup.REDSTONE);
      this.setTicksRandomly(true);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(SUSPENDED, !World.hasSolidTop(world, pos.down()));
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
   public boolean canPlace(World world, BlockPos pos, Direction dir) {
      return dir.getAxis().isHorizontal() && world.getBlockState(pos.offset(dir.getOpposite())).getBlock().isConductor();
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         if (world.getBlockState(pos.offset(var4)).getBlock().isConductor()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      BlockState var9 = this.defaultState().set(POWERED, false).set(ATTACHED, false).set(SUSPENDED, false);
      if (dir.getAxis().isHorizontal()) {
         var9 = var9.set(FACING, dir);
      }

      return var9;
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      this.updatePowered(world, pos, state, false, false, -1, null);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (neighborBlock != this) {
         if (this.canSurviveOrBreak(world, pos, state)) {
            Direction var5 = (Direction)state.get(FACING);
            if (!world.getBlockState(pos.offset(var5.getOpposite())).getBlock().isConductor()) {
               this.dropItems(world, pos, state, 0);
               world.removeBlock(pos);
            }
         }
      }
   }

   public void updatePowered(World world, BlockPos pos, BlockState state, boolean removed, boolean updateNeighbors, int distanceToTripwire, BlockState tripwire) {
      Direction var8 = (Direction)state.get(FACING);
      boolean var9 = state.get(ATTACHED);
      boolean var10 = state.get(POWERED);
      boolean var11 = !World.hasSolidTop(world, pos.down());
      boolean var12 = !removed;
      boolean var13 = false;
      int var14 = 0;
      BlockState[] var15 = new BlockState[42];

      for(int var16 = 1; var16 < 42; ++var16) {
         BlockPos var17 = pos.offset(var8, var16);
         BlockState var18 = world.getBlockState(var17);
         if (var18.getBlock() == Blocks.TRIPWIRE_HOOK) {
            if (var18.get(FACING) == var8.getOpposite()) {
               var14 = var16;
            }
            break;
         }

         if (var18.getBlock() != Blocks.TRIPWIRE && var16 != distanceToTripwire) {
            var15[var16] = null;
            var12 = false;
         } else {
            if (var16 == distanceToTripwire) {
               var18 = (BlockState)Objects.firstNonNull(tripwire, var18);
            }

            boolean var19 = !var18.get(TripwireBlock.DISARMED);
            boolean var20 = var18.get(TripwireBlock.POWERED);
            boolean var21 = var18.get(TripwireBlock.SUSPENDED);
            var12 &= var21 == var11;
            var13 |= var19 && var20;
            var15[var16] = var18;
            if (var16 == distanceToTripwire) {
               world.scheduleTick(pos, this, this.getTickRate(world));
               var12 &= var19;
            }
         }
      }

      var12 &= var14 > 1;
      var13 &= var12;
      BlockState var24 = this.defaultState().set(ATTACHED, var12).set(POWERED, var13);
      if (var14 > 0) {
         BlockPos var25 = pos.offset(var8, var14);
         Direction var27 = var8.getOpposite();
         world.setBlockState(var25, var24.set(FACING, var27), 3);
         this.updateNeighbors(world, var25, var27);
         this.playClickSound(world, var25, var12, var13, var9, var10);
      }

      this.playClickSound(world, pos, var12, var13, var9, var10);
      if (!removed) {
         world.setBlockState(pos, var24.set(FACING, var8), 3);
         if (updateNeighbors) {
            this.updateNeighbors(world, pos, var8);
         }
      }

      if (var9 != var12) {
         for(int var26 = 1; var26 < var14; ++var26) {
            BlockPos var28 = pos.offset(var8, var26);
            BlockState var29 = var15[var26];
            if (var29 != null) {
               world.setBlockState(var28, var29.set(ATTACHED, var12), 3);
            }
         }
      }
   }

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      this.updatePowered(world, pos, state, false, true, -1, null);
   }

   private void playClickSound(World world, BlockPos x, boolean y, boolean z, boolean attached, boolean turnedOn) {
      if (z && !turnedOn) {
         world.playSound((double)x.getX() + 0.5, (double)x.getY() + 0.1, (double)x.getZ() + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!z && turnedOn) {
         world.playSound((double)x.getX() + 0.5, (double)x.getY() + 0.1, (double)x.getZ() + 0.5, "random.click", 0.4F, 0.5F);
      } else if (y && !attached) {
         world.playSound((double)x.getX() + 0.5, (double)x.getY() + 0.1, (double)x.getZ() + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!y && attached) {
         world.playSound(
            (double)x.getX() + 0.5, (double)x.getY() + 0.1, (double)x.getZ() + 0.5, "random.bowhit", 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F)
         );
      }
   }

   private void updateNeighbors(World world, BlockPos pos, Direction facing) {
      world.updateNeighbors(pos, this);
      world.updateNeighbors(pos.offset(facing.getOpposite()), this);
   }

   private boolean canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (!this.canSurvive(world, pos)) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      float var3 = 0.1875F;
      switch((Direction)world.getBlockState(pos).get(FACING)) {
         case EAST:
            this.setShape(0.0F, 0.2F, 0.5F - var3, var3 * 2.0F, 0.8F, 0.5F + var3);
            break;
         case WEST:
            this.setShape(1.0F - var3 * 2.0F, 0.2F, 0.5F - var3, 1.0F, 0.8F, 0.5F + var3);
            break;
         case SOUTH:
            this.setShape(0.5F - var3, 0.2F, 0.0F, 0.5F + var3, 0.8F, var3 * 2.0F);
            break;
         case NORTH:
            this.setShape(0.5F - var3, 0.2F, 1.0F - var3 * 2.0F, 0.5F + var3, 0.8F, 1.0F);
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      boolean var4 = state.get(ATTACHED);
      boolean var5 = state.get(POWERED);
      if (var4 || var5) {
         this.updatePowered(world, pos, state, true, false, -1, null);
      }

      if (var5) {
         world.updateNeighbors(pos, this);
         world.updateNeighbors(pos.offset(((Direction)state.get(FACING)).getOpposite()), this);
      }

      super.onRemoved(world, pos, state);
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return state.get(POWERED) ? 15 : 0;
   }

   @Override
   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      if (!state.get(POWERED)) {
         return 0;
      } else {
         return state.get(FACING) == dir ? 15 : 0;
      }
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byIdHorizontal(metadata & 3)).set(POWERED, (metadata & 8) > 0).set(ATTACHED, (metadata & 4) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      if (state.get(POWERED)) {
         var2 |= 8;
      }

      if (state.get(ATTACHED)) {
         var2 |= 4;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, POWERED, ATTACHED, SUSPENDED);
   }
}
