package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FenceGateBlock extends HorizontalFacingBlock {
   public static final BooleanProperty OPEN = BooleanProperty.of("open");
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   public static final BooleanProperty IN_WALL = BooleanProperty.of("in_wall");

   public FenceGateBlock() {
      super(Material.WOOD);
      this.setDefaultState(this.stateDefinition.any().set(OPEN, false).set(POWERED, false).set(IN_WALL, false));
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      Direction.Axis var4 = ((Direction)state.get(FACING)).getAxis();
      if (var4 == Direction.Axis.Z
            && (world.getBlockState(pos.west()).getBlock() == Blocks.COBBLESTONE_WALL || world.getBlockState(pos.east()).getBlock() == Blocks.COBBLESTONE_WALL)
         || var4 == Direction.Axis.X
            && (
               world.getBlockState(pos.north()).getBlock() == Blocks.COBBLESTONE_WALL || world.getBlockState(pos.south()).getBlock() == Blocks.COBBLESTONE_WALL
            )) {
         state = state.set(IN_WALL, true);
      }

      return state;
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return world.getBlockState(pos.down()).getBlock().getMaterial().isSolid() ? super.canSurvive(world, pos) : false;
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      if (state.get(OPEN)) {
         return null;
      } else {
         Direction.Axis var4 = ((Direction)state.get(FACING)).getAxis();
         return var4 == Direction.Axis.Z
            ? new Box(
               (double)pos.getX(),
               (double)pos.getY(),
               (double)((float)pos.getZ() + 0.375F),
               (double)(pos.getX() + 1),
               (double)((float)pos.getY() + 1.5F),
               (double)((float)pos.getZ() + 0.625F)
            )
            : new Box(
               (double)((float)pos.getX() + 0.375F),
               (double)pos.getY(),
               (double)pos.getZ(),
               (double)((float)pos.getX() + 0.625F),
               (double)((float)pos.getY() + 1.5F),
               (double)(pos.getZ() + 1)
            );
      }
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      Direction.Axis var3 = ((Direction)world.getBlockState(pos).get(FACING)).getAxis();
      if (var3 == Direction.Axis.Z) {
         this.setShape(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
      } else {
         this.setShape(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
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
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return world.getBlockState(pos).get(OPEN);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection()).set(OPEN, false).set(POWERED, false).set(IN_WALL, false);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (state.get(OPEN)) {
         world.setBlockState(pos, state.set(OPEN, false), 2);
      } else {
         Direction var9 = Direction.byRotation((double)player.yaw);
         if (state.get(FACING) == var9.getOpposite()) {
            state = state.set(FACING, var9);
         }

         world.setBlockState(pos, state.set(OPEN, true), 2);
      }

      world.doEvent(player, 1003, pos, 0);
      return true;
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         boolean var5 = world.isReceivingPower(pos);
         if (var5 || neighborBlock.isPowerSource()) {
            if (var5 && !state.get(OPEN) && !state.get(POWERED)) {
               world.setBlockState(pos, state.set(OPEN, true).set(POWERED, true), 2);
               world.doEvent(null, 1003, pos, 0);
            } else if (!var5 && state.get(OPEN) && state.get(POWERED)) {
               world.setBlockState(pos, state.set(OPEN, false).set(POWERED, false), 2);
               world.doEvent(null, 1003, pos, 0);
            } else if (var5 && state.get(OPEN) && !state.get(POWERED)) {
               world.setBlockState(pos, state.set(POWERED, true), 2);
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return true;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byIdHorizontal(metadata)).set(OPEN, (metadata & 4) != 0).set(POWERED, (metadata & 8) != 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      if (state.get(POWERED)) {
         var2 |= 8;
      }

      if (state.get(OPEN)) {
         var2 |= 4;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, OPEN, POWERED, IN_WALL);
   }
}
