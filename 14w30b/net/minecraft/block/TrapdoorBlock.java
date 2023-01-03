package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
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

public class TrapdoorBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final BooleanProperty OPEN = BooleanProperty.of("open");
   public static final EnumProperty HALF = EnumProperty.of("half", TrapdoorBlock.Half.class);

   protected TrapdoorBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(OPEN, false).set(HALF, TrapdoorBlock.Half.BOTTOM));
      float var2 = 0.5F;
      float var3 = 1.0F;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setItemGroup(ItemGroup.REDSTONE);
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
      return !world.getBlockState(pos).get(OPEN);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      this.updateShape(world, pos);
      return super.getOutlineShape(world, pos);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      return super.getCollisionShape(world, pos, state);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.updateBoundingBox(world.getBlockState(pos));
   }

   @Override
   public void setBlockItemBounds() {
      float var1 = 0.1875F;
      this.setShape(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
   }

   public void updateBoundingBox(BlockState state) {
      if (state.getBlock() == this) {
         boolean var2 = state.get(HALF) == TrapdoorBlock.Half.TOP;
         Boolean var3 = (Boolean)state.get(OPEN);
         Direction var4 = (Direction)state.get(FACING);
         float var5 = 0.1875F;
         if (var2) {
            this.setShape(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
         } else {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
         }

         if (var3) {
            if (var4 == Direction.NORTH) {
               this.setShape(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
            }

            if (var4 == Direction.SOUTH) {
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
            }

            if (var4 == Direction.WEST) {
               this.setShape(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if (var4 == Direction.EAST) {
               this.setShape(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
            }
         }
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (this.material == Material.IRON) {
         return true;
      } else {
         world.setBlockState(pos, state.next(OPEN), 2);
         world.doEvent(player, 1003, pos, 0);
         return true;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         BlockPos var5 = pos.offset(((Direction)state.get(FACING)).getOpposite());
         if (!canBePlacedOn(world.getBlockState(var5).getBlock())) {
            world.removeBlock(pos);
            this.dropItems(world, pos, state, 0);
         } else {
            boolean var6 = world.isReceivingPower(pos);
            if (var6 || neighborBlock.isPowerSource()) {
               boolean var7 = state.get(OPEN);
               if (var7 != var6) {
                  world.setBlockState(pos, state.set(OPEN, var6), 2);
                  world.doEvent(null, 1003, pos, 0);
               }
            }
         }
      }
   }

   @Override
   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      this.updateShape(world, pos);
      return super.rayTrace(world, pos, start, end);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      BlockState var9 = this.defaultState();
      if (dir.getAxis().isHorizontal()) {
         var9 = var9.set(FACING, dir).set(OPEN, false);
         var9 = var9.set(HALF, dy > 0.5F ? TrapdoorBlock.Half.TOP : TrapdoorBlock.Half.BOTTOM);
      }

      return var9;
   }

   @Override
   public boolean canPlace(World world, BlockPos pos, Direction dir) {
      return !dir.getAxis().isVertical() && canBePlacedOn(world.getBlockState(pos.offset(dir.getOpposite())).getBlock());
   }

   protected static Direction getFacingFromMetadata(int metadata) {
      switch(metadata & 3) {
         case 0:
            return Direction.NORTH;
         case 1:
            return Direction.SOUTH;
         case 2:
            return Direction.WEST;
         case 3:
         default:
            return Direction.EAST;
      }
   }

   protected static int getMetadataForFacing(Direction facing) {
      switch(facing) {
         case NORTH:
            return 0;
         case SOUTH:
            return 1;
         case WEST:
            return 2;
         case EAST:
         default:
            return 3;
      }
   }

   private static boolean canBePlacedOn(Block block) {
      return block.material.isSolidBlocking() && block.isFullCube() || block == Blocks.GLOWSTONE || block instanceof SlabBlock || block instanceof StairsBlock;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState()
         .set(FACING, getFacingFromMetadata(metadata))
         .set(OPEN, (metadata & 4) != 0)
         .set(HALF, (metadata & 8) == 0 ? TrapdoorBlock.Half.BOTTOM : TrapdoorBlock.Half.TOP);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= getMetadataForFacing((Direction)state.get(FACING));
      if (state.get(OPEN)) {
         var2 |= 4;
      }

      if (state.get(HALF) == TrapdoorBlock.Half.TOP) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, OPEN, HALF);
   }

   public static enum Half implements StringRepresentable {
      TOP("top"),
      BOTTOM("bottom");

      private final String id;

      private Half(String id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }
   }
}
