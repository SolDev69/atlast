package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithBlockEntity {
   public static final IntegerProperty POWER = IntegerProperty.of("power", 0, 15);

   public DaylightDetectorBlock() {
      super(Material.WOOD);
      this.setDefaultState(this.stateDefinition.any().set(POWER, 0));
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return state.get(POWER);
   }

   public void updatePower(World world, BlockPos pos) {
      if (!world.dimension.isDark()) {
         BlockState var3 = world.getBlockState(pos);
         int var4 = world.getLight(LightType.SKY, pos) - world.getAmbientDarkness();
         float var5 = world.getSunAngle(1.0F);
         float var6 = var5 < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
         var5 += (var6 - var5) * 0.2F;
         var4 = Math.round((float)var4 * MathHelper.cos(var5));
         var4 = MathHelper.clamp(var4, 0, 15);
         if (var3.get(POWER) != var4) {
            world.setBlockState(pos, var3.set(POWER, var4), 3);
         }
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new DaylightDetectorBlockEntity();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(POWER, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(POWER);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, POWER);
   }
}
