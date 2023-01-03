package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeverBlock extends Block {
   public static final EnumProperty FACING = EnumProperty.of("facing", LeverBlock.Facing.class);
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");

   protected LeverBlock() {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(FACING, LeverBlock.Facing.NORTH).set(POWERED, false));
      this.setItemGroup(ItemGroup.REDSTONE);
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
      if (dir == Direction.UP && World.hasSolidTop(world, pos.down())) {
         return true;
      } else {
         return this.canAttachTo(world, pos.offset(dir.getOpposite()));
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      if (this.canAttachTo(world, pos.west())) {
         return true;
      } else if (this.canAttachTo(world, pos.east())) {
         return true;
      } else if (this.canAttachTo(world, pos.north())) {
         return true;
      } else if (this.canAttachTo(world, pos.south())) {
         return true;
      } else if (World.hasSolidTop(world, pos.down())) {
         return true;
      } else {
         return this.canAttachTo(world, pos.up());
      }
   }

   protected boolean canAttachTo(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().isConductor();
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      BlockState var9 = this.defaultState().set(POWERED, false);
      if (this.canAttachTo(world, pos.offset(dir.getOpposite()))) {
         return var9.set(FACING, LeverBlock.Facing.byAttachmentAndFacing(dir, entity.getDirection()));
      } else {
         for(Direction var11 : Direction.Plane.HORIZONTAL) {
            if (var11 != dir && this.canAttachTo(world, pos.offset(var11.getOpposite()))) {
               return var9.set(FACING, LeverBlock.Facing.byAttachmentAndFacing(var11, entity.getDirection()));
            }
         }

         return World.hasSolidTop(world, pos.down()) ? var9.set(FACING, LeverBlock.Facing.byAttachmentAndFacing(Direction.UP, entity.getDirection())) : var9;
      }
   }

   public static int getMetadataForFacing(Direction facing) {
      switch(facing) {
         case DOWN:
            return 0;
         case UP:
            return 5;
         case NORTH:
            return 4;
         case SOUTH:
            return 3;
         case WEST:
            return 2;
         case EAST:
            return 1;
         default:
            return -1;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (this.canSurviveOrBreak(world, pos)
         && !this.canAttachTo(world, pos.offset(((LeverBlock.Facing)state.get(FACING)).getAttachmentDirection().getOpposite()))) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }
   }

   private boolean canSurviveOrBreak(World world, BlockPos pos) {
      if (this.canSurvive(world, pos)) {
         return true;
      } else {
         this.dropItems(world, pos, world.getBlockState(pos), 0);
         world.removeBlock(pos);
         return false;
      }
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      float var3 = 0.1875F;
      switch((LeverBlock.Facing)world.getBlockState(pos).get(FACING)) {
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
            break;
         case UP_Z:
         case UP_X:
            var3 = 0.25F;
            this.setShape(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 0.6F, 0.5F + var3);
            break;
         case DOWN_X:
         case DOWN_Z:
            var3 = 0.25F;
            this.setShape(0.5F - var3, 0.4F, 0.5F - var3, 0.5F + var3, 1.0F, 0.5F + var3);
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         state = state.next(POWERED);
         world.setBlockState(pos, state, 3);
         world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, state.get(POWERED) ? 0.6F : 0.5F);
         world.updateNeighbors(pos, this);
         Direction var9 = ((LeverBlock.Facing)state.get(FACING)).getAttachmentDirection();
         world.updateNeighbors(pos.offset(var9.getOpposite()), this);
         return true;
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      if (state.get(POWERED)) {
         world.updateNeighbors(pos, this);
         Direction var4 = ((LeverBlock.Facing)state.get(FACING)).getAttachmentDirection();
         world.updateNeighbors(pos.offset(var4.getOpposite()), this);
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
         return ((LeverBlock.Facing)state.get(FACING)).getAttachmentDirection() == dir ? 15 : 0;
      }
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, LeverBlock.Facing.byIndex(metadata & 7)).set(POWERED, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((LeverBlock.Facing)state.get(FACING)).getIndex();
      if (state.get(POWERED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, POWERED);
   }

   public static enum Facing implements StringRepresentable {
      DOWN_X(0, "down_x", Direction.DOWN),
      EAST(1, "east", Direction.EAST),
      WEST(2, "west", Direction.WEST),
      SOUTH(3, "south", Direction.SOUTH),
      NORTH(4, "north", Direction.NORTH),
      UP_Z(5, "up_z", Direction.UP),
      UP_X(6, "up_x", Direction.UP),
      DOWN_Z(7, "down_z", Direction.DOWN);

      private static final LeverBlock.Facing[] ALL = new LeverBlock.Facing[values().length];
      private final int index;
      private final String id;
      private final Direction attachmentDir;

      private Facing(int index, String id, Direction attachmentDir) {
         this.index = index;
         this.id = id;
         this.attachmentDir = attachmentDir;
      }

      public int getIndex() {
         return this.index;
      }

      public Direction getAttachmentDirection() {
         return this.attachmentDir;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public static LeverBlock.Facing byIndex(int index) {
         if (index < 0 || index >= ALL.length) {
            index = 0;
         }

         return ALL[index];
      }

      public static LeverBlock.Facing byAttachmentAndFacing(Direction attachmentDir, Direction facing) {
         switch(attachmentDir) {
            case DOWN:
               switch(facing.getAxis()) {
                  case X:
                     return DOWN_X;
                  case Z:
                     return DOWN_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + facing + " for facing " + attachmentDir);
               }
            case UP:
               switch(facing.getAxis()) {
                  case X:
                     return UP_X;
                  case Z:
                     return UP_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + facing + " for facing " + attachmentDir);
               }
            case NORTH:
               return NORTH;
            case SOUTH:
               return SOUTH;
            case WEST:
               return WEST;
            case EAST:
               return EAST;
            default:
               throw new IllegalArgumentException("Invalid facing: " + attachmentDir);
         }
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      static {
         for(LeverBlock.Facing var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
