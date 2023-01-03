package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock extends AxisBlock {
   public HayBlock() {
      super(Material.GRASS);
      this.setDefaultState(this.stateDefinition.any().set(AXIS, Direction.Axis.Y));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      Direction.Axis var2 = Direction.Axis.Y;
      int var3 = metadata & 12;
      if (var3 == 4) {
         var2 = Direction.Axis.X;
      } else if (var3 == 8) {
         var2 = Direction.Axis.Z;
      }

      return this.defaultState().set(AXIS, var2);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      Direction.Axis var3 = (Direction.Axis)state.get(AXIS);
      if (var3 == Direction.Axis.X) {
         var2 |= 4;
      } else if (var3 == Direction.Axis.Z) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, AXIS);
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      return new ItemStack(Item.byBlock(this), 1, 0);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return super.getPlacementState(world, pos, dir, dx, dy, dz, metadata, entity).set(AXIS, dir.getAxis());
   }
}
