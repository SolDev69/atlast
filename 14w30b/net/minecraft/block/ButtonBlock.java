package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class ButtonBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing");
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   private final boolean wooden;

   protected ButtonBlock(boolean wooden) {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(POWERED, false));
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.REDSTONE);
      this.wooden = wooden;
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public int getTickRate(World world) {
      return this.wooden ? 30 : 20;
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
      return world.getBlockState(pos.offset(dir.getOpposite())).getBlock().isConductor();
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      for(Direction var6 : Direction.values()) {
         if (world.getBlockState(pos.offset(var6)).getBlock().isConductor()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return world.getBlockState(pos.offset(dir.getOpposite())).getBlock().isConductor()
         ? this.defaultState().set(FACING, dir).set(POWERED, false)
         : this.defaultState().set(FACING, Direction.DOWN).set(POWERED, false);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (this.canSurviveOrBreak(world, pos, state)) {
         Direction var5 = (Direction)state.get(FACING);
         if (!world.getBlockState(pos.offset(var5.getOpposite())).getBlock().isConductor()) {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
         }
      }
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
      this.updateBoundingBox(world.getBlockState(pos));
   }

   private void updateBoundingBox(BlockState state) {
      Direction var2 = (Direction)state.get(FACING);
      boolean var3 = state.get(POWERED);
      float var4 = 0.25F;
      float var5 = 0.375F;
      float var6 = (float)(var3 ? 1 : 2) / 16.0F;
      float var7 = 0.125F;
      float var8 = 0.1875F;
      switch(var2) {
         case EAST:
            this.setShape(0.0F, 0.375F, 0.3125F, var6, 0.625F, 0.6875F);
            break;
         case WEST:
            this.setShape(1.0F - var6, 0.375F, 0.3125F, 1.0F, 0.625F, 0.6875F);
            break;
         case SOUTH:
            this.setShape(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, var6);
            break;
         case NORTH:
            this.setShape(0.3125F, 0.375F, 1.0F - var6, 0.6875F, 0.625F, 1.0F);
            break;
         case UP:
            this.setShape(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + var6, 0.625F);
            break;
         case DOWN:
            this.setShape(0.3125F, 1.0F - var6, 0.375F, 0.6875F, 1.0F, 0.625F);
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (state.get(POWERED)) {
         return true;
      } else {
         world.setBlockState(pos, state.set(POWERED, true), 3);
         world.onRegionChanged(pos, pos);
         world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
         this.updateNeighbors(world, pos, (Direction)state.get(FACING));
         world.scheduleTick(pos, this, this.getTickRate(world));
         return true;
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      if (state.get(POWERED)) {
         this.updateNeighbors(world, pos, (Direction)state.get(FACING));
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

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         if (state.get(POWERED)) {
            if (this.wooden) {
               this.updatePressed(world, pos, state);
            } else {
               world.setBlockState(pos, state.set(POWERED, false));
               this.updateNeighbors(world, pos, (Direction)state.get(FACING));
               world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
               world.onRegionChanged(pos, pos);
            }
         }
      }
   }

   @Override
   public void setBlockItemBounds() {
      float var1 = 0.1875F;
      float var2 = 0.125F;
      float var3 = 0.125F;
      this.setShape(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!world.isClient) {
         if (this.wooden) {
            if (!state.get(POWERED)) {
               this.updatePressed(world, pos, state);
            }
         }
      }
   }

   private void updatePressed(World world, BlockPos pos, BlockState state) {
      this.updateBoundingBox(state);
      List var4 = world.getEntities(
         ArrowEntity.class,
         new Box(
            (double)pos.getX() + this.minX,
            (double)pos.getY() + this.minY,
            (double)pos.getZ() + this.minZ,
            (double)pos.getX() + this.maxX,
            (double)pos.getY() + this.maxY,
            (double)pos.getZ() + this.maxZ
         )
      );
      boolean var5 = !var4.isEmpty();
      boolean var6 = state.get(POWERED);
      if (var5 && !var6) {
         world.setBlockState(pos, state.set(POWERED, true));
         this.updateNeighbors(world, pos, (Direction)state.get(FACING));
         world.onRegionChanged(pos, pos);
         world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (!var5 && var6) {
         world.setBlockState(pos, state.set(POWERED, false));
         this.updateNeighbors(world, pos, (Direction)state.get(FACING));
         world.onRegionChanged(pos, pos);
         world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      }

      if (var5) {
         world.scheduleTick(pos, this, this.getTickRate(world));
      }
   }

   private void updateNeighbors(World world, BlockPos pos, Direction dir) {
      world.updateNeighbors(pos, this);
      world.updateNeighbors(pos.offset(dir.getOpposite()), this);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      Direction var2;
      switch(metadata & 7) {
         case 0:
            var2 = Direction.DOWN;
            break;
         case 1:
            var2 = Direction.EAST;
            break;
         case 2:
            var2 = Direction.WEST;
            break;
         case 3:
            var2 = Direction.SOUTH;
            break;
         case 4:
            var2 = Direction.NORTH;
            break;
         case 5:
         default:
            var2 = Direction.UP;
      }

      return this.defaultState().set(FACING, var2).set(POWERED, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2;
      switch((Direction)state.get(FACING)) {
         case EAST:
            var2 = 1;
            break;
         case WEST:
            var2 = 2;
            break;
         case SOUTH:
            var2 = 3;
            break;
         case NORTH:
            var2 = 4;
            break;
         case UP:
         default:
            var2 = 5;
            break;
         case DOWN:
            var2 = 0;
      }

      if (state.get(POWERED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, POWERED);
   }
}
