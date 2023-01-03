package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LadderBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);

   protected LadderBlock() {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      return super.getCollisionShape(world, pos, state);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      this.updateShape(world, pos);
      return super.getOutlineShape(world, pos);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      if (var3.getBlock() == this) {
         float var4 = 0.125F;
         switch((Direction)var3.get(FACING)) {
            case NORTH:
               this.setShape(0.0F, 0.0F, 1.0F - var4, 1.0F, 1.0F, 1.0F);
               break;
            case SOUTH:
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var4);
               break;
            case WEST:
               this.setShape(1.0F - var4, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case EAST:
            default:
               this.setShape(0.0F, 0.0F, 0.0F, var4, 1.0F, 1.0F);
         }
      }
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
   public boolean canSurvive(World world, BlockPos pos) {
      if (world.getBlockState(pos.west()).getBlock().isConductor()) {
         return true;
      } else if (world.getBlockState(pos.east()).getBlock().isConductor()) {
         return true;
      } else if (world.getBlockState(pos.north()).getBlock().isConductor()) {
         return true;
      } else {
         return world.getBlockState(pos.south()).getBlock().isConductor();
      }
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      if (dir.getAxis().isHorizontal() && this.canSurvive(world, pos, dir)) {
         return this.defaultState().set(FACING, dir);
      } else {
         for(Direction var10 : Direction.Plane.HORIZONTAL) {
            if (this.canSurvive(world, pos, var10)) {
               return this.defaultState().set(FACING, var10);
            }
         }

         return this.defaultState();
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      Direction var5 = (Direction)state.get(FACING);
      if (!this.canSurvive(world, pos, var5)) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }

      super.update(world, pos, state, neighborBlock);
   }

   protected boolean canSurvive(World world, BlockPos pos, Direction facing) {
      return world.getBlockState(pos.offset(facing.getOpposite())).getBlock().isConductor();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      Direction var2 = Direction.byId(metadata);
      if (var2.getAxis() == Direction.Axis.Y) {
         var2 = Direction.NORTH;
      }

      return this.defaultState().set(FACING, var2);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((Direction)state.get(FACING)).getId();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING);
   }
}
