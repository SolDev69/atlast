package net.minecraft.block;

import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class WallSignBlock extends SignBlock {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);

   public WallSignBlock() {
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      Direction var3 = (Direction)world.getBlockState(pos).get(FACING);
      float var4 = 0.28125F;
      float var5 = 0.78125F;
      float var6 = 0.0F;
      float var7 = 1.0F;
      float var8 = 0.125F;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      switch(var3) {
         case NORTH:
            this.setShape(var6, var4, 1.0F - var8, var7, var5, 1.0F);
            break;
         case SOUTH:
            this.setShape(var6, var4, 0.0F, var7, var5, var8);
            break;
         case WEST:
            this.setShape(1.0F - var8, var4, var6, 1.0F, var5, var7);
            break;
         case EAST:
            this.setShape(0.0F, var4, var6, var8, var5, var7);
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      Direction var5 = (Direction)state.get(FACING);
      if (!world.getBlockState(pos.offset(var5.getOpposite())).getBlock().getMaterial().isSolid()) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }

      super.update(world, pos, state, neighborBlock);
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
