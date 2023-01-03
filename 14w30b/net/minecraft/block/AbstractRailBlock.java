package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Property;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.HitResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class AbstractRailBlock extends Block {
   protected final boolean alwaysStraight;

   public static boolean isRail(World world, BlockPos pos) {
      return isRail(world.getBlockState(pos));
   }

   public static boolean isRail(BlockState block) {
      Block var1 = block.getBlock();
      return var1 == Blocks.RAIL || var1 == Blocks.POWERED_RAIL || var1 == Blocks.DETECTOR_RAIL || var1 == Blocks.ACTIVATOR_RAIL;
   }

   protected AbstractRailBlock(boolean allowCurves) {
      super(Material.DECORATION);
      this.alwaysStraight = allowCurves;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setItemGroup(ItemGroup.TRANSPORTATION);
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
   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      this.updateShape(world, pos);
      return super.rayTrace(world, pos, start, end);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      AbstractRailBlock.Shape var3 = (AbstractRailBlock.Shape)world.getBlockState(pos).get(this.getShapeProperty());
      if (var3.isAscending()) {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos.down());
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      if (!world.isClient) {
         state = this.updateShape(world, pos, state, true);
         if (this.alwaysStraight) {
            this.update(world, pos, state, this);
         }
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         AbstractRailBlock.Shape var5 = (AbstractRailBlock.Shape)state.get(this.getShapeProperty());
         boolean var6 = false;
         if (!World.hasSolidTop(world, pos.down())) {
            var6 = true;
         }

         if (var5 == AbstractRailBlock.Shape.ASCENDING_EAST && !World.hasSolidTop(world, pos.east())) {
            var6 = true;
         } else if (var5 == AbstractRailBlock.Shape.ASCENDING_WEST && !World.hasSolidTop(world, pos.west())) {
            var6 = true;
         } else if (var5 == AbstractRailBlock.Shape.ASCENDING_NORTH && !World.hasSolidTop(world, pos.north())) {
            var6 = true;
         } else if (var5 == AbstractRailBlock.Shape.ASCENDING_SOUTH && !World.hasSolidTop(world, pos.south())) {
            var6 = true;
         }

         if (var6) {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
         } else {
            this.updatePowered(world, pos, state, neighborBlock);
         }
      }
   }

   protected void updatePowered(World world, BlockPos pos, BlockState state, Block neighborBlock) {
   }

   protected BlockState updateShape(World world, BlockPos pos, BlockState state, boolean force) {
      return world.isClient ? state : new AbstractRailBlock.RailNode(world, pos, state).updateState(world.isReceivingPower(pos), force).getState();
   }

   @Override
   public int getPistonMoveBehavior() {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      super.onRemoved(world, pos, state);
      if (((AbstractRailBlock.Shape)state.get(this.getShapeProperty())).isAscending()) {
         world.updateNeighbors(pos.up(), this);
      }

      if (this.alwaysStraight) {
         world.updateNeighbors(pos, this);
         world.updateNeighbors(pos.down(), this);
      }
   }

   public abstract Property getShapeProperty();

   public class RailNode {
      private final World world;
      private final BlockPos pos;
      private final AbstractRailBlock block;
      private BlockState state;
      private final boolean alwaysStraight;
      private final List connections = Lists.newArrayList();

      public RailNode(World world, BlockPos pos, BlockState state) {
         this.world = world;
         this.pos = pos;
         this.state = state;
         this.block = (AbstractRailBlock)state.getBlock();
         AbstractRailBlock.Shape var5 = (AbstractRailBlock.Shape)state.get(AbstractRailBlock.this.getShapeProperty());
         this.alwaysStraight = this.block.alwaysStraight;
         this.updateConnections(var5);
      }

      private void updateConnections(AbstractRailBlock.Shape shape) {
         this.connections.clear();
         switch(shape) {
            case NORTH_SOUTH:
               this.connections.add(this.pos.north());
               this.connections.add(this.pos.south());
               break;
            case EAST_WEST:
               this.connections.add(this.pos.west());
               this.connections.add(this.pos.east());
               break;
            case ASCENDING_EAST:
               this.connections.add(this.pos.west());
               this.connections.add(this.pos.east().up());
               break;
            case ASCENDING_WEST:
               this.connections.add(this.pos.west().up());
               this.connections.add(this.pos.east());
               break;
            case ASCENDING_NORTH:
               this.connections.add(this.pos.north().up());
               this.connections.add(this.pos.south());
               break;
            case ASCENDING_SOUTH:
               this.connections.add(this.pos.north());
               this.connections.add(this.pos.south().up());
               break;
            case SOUTH_EAST:
               this.connections.add(this.pos.east());
               this.connections.add(this.pos.south());
               break;
            case SOUTH_WEST:
               this.connections.add(this.pos.west());
               this.connections.add(this.pos.south());
               break;
            case NORTH_WEST:
               this.connections.add(this.pos.west());
               this.connections.add(this.pos.north());
               break;
            case NORTH_EAST:
               this.connections.add(this.pos.east());
               this.connections.add(this.pos.north());
         }
      }

      private void cleanUpConnections() {
         for(int var1 = 0; var1 < this.connections.size(); ++var1) {
            AbstractRailBlock.RailNode var2 = this.getNeighborRail((BlockPos)this.connections.get(var1));
            if (var2 != null && var2.isConnected(this)) {
               this.connections.set(var1, var2.pos);
            } else {
               this.connections.remove(var1--);
            }
         }
      }

      private boolean shouldConnectTo(BlockPos pos) {
         return AbstractRailBlock.isRail(this.world, pos) || AbstractRailBlock.isRail(this.world, pos.up()) || AbstractRailBlock.isRail(this.world, pos.down());
      }

      private AbstractRailBlock.RailNode getNeighborRail(BlockPos pos) {
         BlockState var3 = this.world.getBlockState(pos);
         if (AbstractRailBlock.isRail(var3)) {
            return AbstractRailBlock.this.new RailNode(this.world, pos, var3);
         } else {
            BlockPos var2 = pos.up();
            var3 = this.world.getBlockState(var2);
            if (AbstractRailBlock.isRail(var3)) {
               return AbstractRailBlock.this.new RailNode(this.world, var2, var3);
            } else {
               var2 = pos.down();
               var3 = this.world.getBlockState(var2);
               return AbstractRailBlock.isRail(var3) ? AbstractRailBlock.this.new RailNode(this.world, var2, var3) : null;
            }
         }
      }

      private boolean isConnected(AbstractRailBlock.RailNode rail) {
         return this.isConnected(rail.pos);
      }

      private boolean isConnected(BlockPos pos) {
         for(int var2 = 0; var2 < this.connections.size(); ++var2) {
            BlockPos var3 = (BlockPos)this.connections.get(var2);
            if (var3.getX() == pos.getX() && var3.getZ() == pos.getZ()) {
               return true;
            }
         }

         return false;
      }

      protected int countConnections() {
         int var1 = 0;

         for(Direction var3 : Direction.Plane.HORIZONTAL) {
            if (this.shouldConnectTo(this.pos.offset(var3))) {
               ++var1;
            }
         }

         return var1;
      }

      private boolean canConnect(AbstractRailBlock.RailNode rail) {
         return this.isConnected(rail) || this.connections.size() != 2;
      }

      private void addConnection(AbstractRailBlock.RailNode rail) {
         this.connections.add(rail.pos);
         BlockPos var2 = this.pos.north();
         BlockPos var3 = this.pos.south();
         BlockPos var4 = this.pos.west();
         BlockPos var5 = this.pos.east();
         boolean var6 = this.isConnected(var2);
         boolean var7 = this.isConnected(var3);
         boolean var8 = this.isConnected(var4);
         boolean var9 = this.isConnected(var5);
         AbstractRailBlock.Shape var10 = null;
         if (var6 || var7) {
            var10 = AbstractRailBlock.Shape.NORTH_SOUTH;
         }

         if (var8 || var9) {
            var10 = AbstractRailBlock.Shape.EAST_WEST;
         }

         if (!this.alwaysStraight) {
            if (var7 && var9 && !var6 && !var8) {
               var10 = AbstractRailBlock.Shape.SOUTH_EAST;
            }

            if (var7 && var8 && !var6 && !var9) {
               var10 = AbstractRailBlock.Shape.SOUTH_WEST;
            }

            if (var6 && var8 && !var7 && !var9) {
               var10 = AbstractRailBlock.Shape.NORTH_WEST;
            }

            if (var6 && var9 && !var7 && !var8) {
               var10 = AbstractRailBlock.Shape.NORTH_EAST;
            }
         }

         if (var10 == AbstractRailBlock.Shape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail(this.world, var2.up())) {
               var10 = AbstractRailBlock.Shape.ASCENDING_NORTH;
            }

            if (AbstractRailBlock.isRail(this.world, var3.up())) {
               var10 = AbstractRailBlock.Shape.ASCENDING_SOUTH;
            }
         }

         if (var10 == AbstractRailBlock.Shape.EAST_WEST) {
            if (AbstractRailBlock.isRail(this.world, var5.up())) {
               var10 = AbstractRailBlock.Shape.ASCENDING_EAST;
            }

            if (AbstractRailBlock.isRail(this.world, var4.up())) {
               var10 = AbstractRailBlock.Shape.ASCENDING_WEST;
            }
         }

         if (var10 == null) {
            var10 = AbstractRailBlock.Shape.NORTH_SOUTH;
         }

         this.state = this.state.set(this.block.getShapeProperty(), var10);
         this.world.setBlockState(this.pos, this.state, 3);
      }

      private boolean hasNeighborRail(BlockPos pos) {
         AbstractRailBlock.RailNode var2 = this.getNeighborRail(pos);
         if (var2 == null) {
            return false;
         } else {
            var2.cleanUpConnections();
            return var2.canConnect(this);
         }
      }

      public AbstractRailBlock.RailNode updateState(boolean powered, boolean force) {
         BlockPos var3 = this.pos.north();
         BlockPos var4 = this.pos.south();
         BlockPos var5 = this.pos.west();
         BlockPos var6 = this.pos.east();
         boolean var7 = this.hasNeighborRail(var3);
         boolean var8 = this.hasNeighborRail(var4);
         boolean var9 = this.hasNeighborRail(var5);
         boolean var10 = this.hasNeighborRail(var6);
         AbstractRailBlock.Shape var11 = null;
         if ((var7 || var8) && !var9 && !var10) {
            var11 = AbstractRailBlock.Shape.NORTH_SOUTH;
         }

         if ((var9 || var10) && !var7 && !var8) {
            var11 = AbstractRailBlock.Shape.EAST_WEST;
         }

         if (!this.alwaysStraight) {
            if (var8 && var10 && !var7 && !var9) {
               var11 = AbstractRailBlock.Shape.SOUTH_EAST;
            }

            if (var8 && var9 && !var7 && !var10) {
               var11 = AbstractRailBlock.Shape.SOUTH_WEST;
            }

            if (var7 && var9 && !var8 && !var10) {
               var11 = AbstractRailBlock.Shape.NORTH_WEST;
            }

            if (var7 && var10 && !var8 && !var9) {
               var11 = AbstractRailBlock.Shape.NORTH_EAST;
            }
         }

         if (var11 == null) {
            if (var7 || var8) {
               var11 = AbstractRailBlock.Shape.NORTH_SOUTH;
            }

            if (var9 || var10) {
               var11 = AbstractRailBlock.Shape.EAST_WEST;
            }

            if (!this.alwaysStraight) {
               if (powered) {
                  if (var8 && var10) {
                     var11 = AbstractRailBlock.Shape.SOUTH_EAST;
                  }

                  if (var9 && var8) {
                     var11 = AbstractRailBlock.Shape.SOUTH_WEST;
                  }

                  if (var10 && var7) {
                     var11 = AbstractRailBlock.Shape.NORTH_EAST;
                  }

                  if (var7 && var9) {
                     var11 = AbstractRailBlock.Shape.NORTH_WEST;
                  }
               } else {
                  if (var7 && var9) {
                     var11 = AbstractRailBlock.Shape.NORTH_WEST;
                  }

                  if (var10 && var7) {
                     var11 = AbstractRailBlock.Shape.NORTH_EAST;
                  }

                  if (var9 && var8) {
                     var11 = AbstractRailBlock.Shape.SOUTH_WEST;
                  }

                  if (var8 && var10) {
                     var11 = AbstractRailBlock.Shape.SOUTH_EAST;
                  }
               }
            }
         }

         if (var11 == AbstractRailBlock.Shape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail(this.world, var3.up())) {
               var11 = AbstractRailBlock.Shape.ASCENDING_NORTH;
            }

            if (AbstractRailBlock.isRail(this.world, var4.up())) {
               var11 = AbstractRailBlock.Shape.ASCENDING_SOUTH;
            }
         }

         if (var11 == AbstractRailBlock.Shape.EAST_WEST) {
            if (AbstractRailBlock.isRail(this.world, var6.up())) {
               var11 = AbstractRailBlock.Shape.ASCENDING_EAST;
            }

            if (AbstractRailBlock.isRail(this.world, var5.up())) {
               var11 = AbstractRailBlock.Shape.ASCENDING_WEST;
            }
         }

         if (var11 == null) {
            var11 = AbstractRailBlock.Shape.NORTH_SOUTH;
         }

         this.updateConnections(var11);
         this.state = this.state.set(this.block.getShapeProperty(), var11);
         if (force || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

            for(int var12 = 0; var12 < this.connections.size(); ++var12) {
               AbstractRailBlock.RailNode var13 = this.getNeighborRail((BlockPos)this.connections.get(var12));
               if (var13 != null) {
                  var13.cleanUpConnections();
                  if (var13.canConnect(this)) {
                     var13.addConnection(this);
                  }
               }
            }
         }

         return this;
      }

      public BlockState getState() {
         return this.state;
      }
   }

   public static enum Shape implements StringRepresentable {
      NORTH_SOUTH(0, "north_south"),
      EAST_WEST(1, "east_west"),
      ASCENDING_EAST(2, "ascending_east"),
      ASCENDING_WEST(3, "ascending_west"),
      ASCENDING_NORTH(4, "ascending_north"),
      ASCENDING_SOUTH(5, "ascending_south"),
      SOUTH_EAST(6, "south_east"),
      SOUTH_WEST(7, "south_west"),
      NORTH_WEST(8, "north_west"),
      NORTH_EAST(9, "north_east");

      private static final AbstractRailBlock.Shape[] ALL = new AbstractRailBlock.Shape[values().length];
      private final int index;
      private final String id;

      private Shape(int index, String id) {
         this.index = index;
         this.id = id;
      }

      public int getIndex() {
         return this.index;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public boolean isAscending() {
         return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
      }

      public static AbstractRailBlock.Shape byIndex(int index) {
         if (index < 0 || index >= ALL.length) {
            index = 0;
         }

         return ALL[index];
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      static {
         for(AbstractRailBlock.Shape var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
