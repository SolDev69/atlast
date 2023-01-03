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
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final BooleanProperty EYE = BooleanProperty.of("eye");

   public EndPortalFrameBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(EYE, false));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void setBlockItemBounds() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      if (world.getBlockState(pos).get(EYE)) {
         this.setShape(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }

      this.setBlockItemBounds();
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection().getOpposite()).set(EYE, false);
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return world.getBlockState(pos).get(EYE) ? 15 : 0;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(EYE, (metadata & 4) != 0).set(FACING, Direction.byIdHorizontal(metadata & 3));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      if (state.get(EYE)) {
         var2 |= 4;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, EYE);
   }
}
