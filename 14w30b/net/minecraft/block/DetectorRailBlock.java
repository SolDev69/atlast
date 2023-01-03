package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DetectorRailBlock extends AbstractRailBlock {
   public static final EnumProperty SHAPE = EnumProperty.of(
      "shape",
      AbstractRailBlock.Shape.class,
      new Predicate() {
         public boolean apply(AbstractRailBlock.Shape c_10irhwhdh) {
            return c_10irhwhdh != AbstractRailBlock.Shape.NORTH_EAST
               && c_10irhwhdh != AbstractRailBlock.Shape.NORTH_WEST
               && c_10irhwhdh != AbstractRailBlock.Shape.SOUTH_EAST
               && c_10irhwhdh != AbstractRailBlock.Shape.SOUTH_WEST;
         }
      }
   );
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");

   public DetectorRailBlock() {
      super(true);
      this.setDefaultState(this.stateDefinition.any().set(POWERED, false).set(SHAPE, AbstractRailBlock.Shape.NORTH_SOUTH));
      this.setTicksRandomly(true);
   }

   @Override
   public int getTickRate(World world) {
      return 20;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!world.isClient) {
         if (!state.get(POWERED)) {
            this.updatePowered(world, pos, state);
         }
      }
   }

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient && state.get(POWERED)) {
         this.updatePowered(world, pos, state);
      }
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
         return dir == Direction.UP ? 15 : 0;
      }
   }

   private void updatePowered(World world, BlockPos pos, BlockState state) {
      boolean var4 = state.get(POWERED);
      boolean var5 = false;
      List var6 = this.getMinecarts(world, pos, MinecartEntity.class);
      if (!var6.isEmpty()) {
         var5 = true;
      }

      if (var5 && !var4) {
         world.setBlockState(pos, state.set(POWERED, true), 3);
         world.updateNeighbors(pos, this);
         world.updateNeighbors(pos.down(), this);
         world.onRegionChanged(pos, pos);
      }

      if (!var5 && var4) {
         world.setBlockState(pos, state.set(POWERED, false), 3);
         world.updateNeighbors(pos, this);
         world.updateNeighbors(pos.down(), this);
         world.onRegionChanged(pos, pos);
      }

      if (var5) {
         world.scheduleTick(pos, this, this.getTickRate(world));
      }

      world.updateComparators(pos, this);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      super.onAdded(world, pos, state);
      this.updatePowered(world, pos, state);
   }

   @Override
   public Property getShapeProperty() {
      return SHAPE;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      if (world.getBlockState(pos).get(POWERED)) {
         List var3 = this.getMinecarts(world, pos, CommandBlockMinecartEntity.class);
         if (!var3.isEmpty()) {
            return ((CommandBlockMinecartEntity)var3.get(0)).getCommandExecutor().getSuccessCount();
         }

         List var4 = this.getMinecarts(world, pos, MinecartEntity.class, EntityFilter.INVENTORY);
         if (!var4.isEmpty()) {
            return InventoryMenu.getAnalogOutput((Inventory)var4.get(0));
         }
      }

      return 0;
   }

   protected List getMinecarts(World world, BlockPos pos, Class type, Predicate... predicates) {
      Box var5 = this.getBoxForMinecarts(pos);
      return predicates.length != 1 ? world.getEntities(type, var5) : world.getEntities(type, var5, predicates[0]);
   }

   private Box getBoxForMinecarts(BlockPos pos) {
      float var2 = 0.2F;
      return new Box(
         (double)((float)pos.getX() + 0.2F),
         (double)pos.getY(),
         (double)((float)pos.getZ() + 0.2F),
         (double)((float)(pos.getX() + 1) - 0.2F),
         (double)((float)(pos.getY() + 1) - 0.2F),
         (double)((float)(pos.getZ() + 1) - 0.2F)
      );
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(SHAPE, AbstractRailBlock.Shape.byIndex(metadata & 7)).set(POWERED, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((AbstractRailBlock.Shape)state.get(SHAPE)).getIndex();
      if (state.get(POWERED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, SHAPE, POWERED);
   }
}
