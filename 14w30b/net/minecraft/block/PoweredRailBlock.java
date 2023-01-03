package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
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

   protected PoweredRailBlock() {
      super(true);
      this.setDefaultState(this.stateDefinition.any().set(SHAPE, AbstractRailBlock.Shape.NORTH_SOUTH).set(POWERED, false));
   }

   protected boolean isPoweredByConnectedRails(World world, BlockPos pos, BlockState state, boolean towardsNegative, int depth) {
      if (depth >= 8) {
         return false;
      } else {
         int var6 = pos.getX();
         int var7 = pos.getY();
         int var8 = pos.getZ();
         boolean var9 = true;
         AbstractRailBlock.Shape var10 = (AbstractRailBlock.Shape)state.get(SHAPE);
         switch(var10) {
            case NORTH_SOUTH:
               if (towardsNegative) {
                  ++var8;
               } else {
                  --var8;
               }
               break;
            case EAST_WEST:
               if (towardsNegative) {
                  --var6;
               } else {
                  ++var6;
               }
               break;
            case ASCENDING_EAST:
               if (towardsNegative) {
                  --var6;
               } else {
                  ++var6;
                  ++var7;
                  var9 = false;
               }

               var10 = AbstractRailBlock.Shape.EAST_WEST;
               break;
            case ASCENDING_WEST:
               if (towardsNegative) {
                  --var6;
                  ++var7;
                  var9 = false;
               } else {
                  ++var6;
               }

               var10 = AbstractRailBlock.Shape.EAST_WEST;
               break;
            case ASCENDING_NORTH:
               if (towardsNegative) {
                  ++var8;
               } else {
                  --var8;
                  ++var7;
                  var9 = false;
               }

               var10 = AbstractRailBlock.Shape.NORTH_SOUTH;
               break;
            case ASCENDING_SOUTH:
               if (towardsNegative) {
                  ++var8;
                  ++var7;
                  var9 = false;
               } else {
                  --var8;
               }

               var10 = AbstractRailBlock.Shape.NORTH_SOUTH;
         }

         if (this.isPoweredByRail(world, new BlockPos(var6, var7, var8), towardsNegative, depth, var10)) {
            return true;
         } else {
            return var9 && this.isPoweredByRail(world, new BlockPos(var6, var7 - 1, var8), towardsNegative, depth, var10);
         }
      }
   }

   protected boolean isPoweredByRail(World world, BlockPos pos, boolean towardsNegative, int depth, AbstractRailBlock.Shape shape) {
      BlockState var6 = world.getBlockState(pos);
      if (var6.getBlock() != this) {
         return false;
      } else {
         AbstractRailBlock.Shape var7 = (AbstractRailBlock.Shape)var6.get(SHAPE);
         if (shape != AbstractRailBlock.Shape.EAST_WEST
            || var7 != AbstractRailBlock.Shape.NORTH_SOUTH
               && var7 != AbstractRailBlock.Shape.ASCENDING_NORTH
               && var7 != AbstractRailBlock.Shape.ASCENDING_SOUTH) {
            if (shape != AbstractRailBlock.Shape.NORTH_SOUTH
               || var7 != AbstractRailBlock.Shape.EAST_WEST && var7 != AbstractRailBlock.Shape.ASCENDING_EAST && var7 != AbstractRailBlock.Shape.ASCENDING_WEST
               )
             {
               if (!var6.get(POWERED)) {
                  return false;
               } else {
                  return world.isReceivingPower(pos) ? true : this.isPoweredByConnectedRails(world, pos, var6, towardsNegative, depth + 1);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   protected void updatePowered(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      boolean var5 = state.get(POWERED);
      boolean var6 = world.isReceivingPower(pos)
         || this.isPoweredByConnectedRails(world, pos, state, true, 0)
         || this.isPoweredByConnectedRails(world, pos, state, false, 0);
      if (var6 != var5) {
         world.setBlockState(pos, state.set(POWERED, var6), 3);
         world.updateNeighbors(pos.down(), this);
         if (((AbstractRailBlock.Shape)state.get(SHAPE)).isAscending()) {
            world.updateNeighbors(pos.up(), this);
         }
      }
   }

   @Override
   public Property getShapeProperty() {
      return SHAPE;
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
