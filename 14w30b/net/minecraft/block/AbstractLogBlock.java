package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class AbstractLogBlock extends AxisBlock {
   public static final EnumProperty LOG_AXIS = EnumProperty.of("axis", AbstractLogBlock.LogAxis.class);

   public AbstractLogBlock() {
      super(Material.WOOD);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
      this.setStrength(2.0F);
      this.setSound(WOOD_SOUND);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      byte var4 = 4;
      int var5 = var4 + 1;
      if (world.isRegionLoaded(pos.add(-var5, -var5, -var5), pos.add(var5, var5, var5))) {
         for(BlockPos var7 : BlockPos.iterateRegion(pos.add(-var4, -var4, -var4), pos.add(var4, var4, var4))) {
            BlockState var8 = world.getBlockState(var7);
            if (var8.getBlock().getMaterial() == Material.LEAVES && !var8.get(AbstractLeavesBlock.CHECK_DECAY)) {
               world.setBlockState(var7, var8.set(AbstractLeavesBlock.CHECK_DECAY, true), 4);
            }
         }
      }
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return super.getPlacementState(world, pos, dir, dx, dy, dz, metadata, entity).set(LOG_AXIS, AbstractLogBlock.LogAxis.get(dir.getAxis()));
   }

   public static enum LogAxis implements StringRepresentable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      private final String id;

      private LogAxis(String id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public static AbstractLogBlock.LogAxis get(Direction.Axis axis) {
         switch(axis) {
            case X:
               return X;
            case Y:
               return Y;
            case Z:
               return Z;
            default:
               return NONE;
         }
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }
   }
}
