package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
   public static final IntegerProperty POWER = IntegerProperty.of("power", 0, 15);
   private final int weight;

   protected WeightedPressurePlateBlock(String stepSound, Material material, int weight) {
      super(material);
      this.setDefaultState(this.stateDefinition.any().set(POWER, 0));
      this.weight = weight;
   }

   @Override
   protected int calculatePowerLevel(World world, BlockPos pos) {
      int var3 = Math.min(world.getEntities(Entity.class, this.createBoundingBox(pos)).size(), this.weight);
      if (var3 > 0) {
         float var4 = (float)Math.min(this.weight, var3) / (float)this.weight;
         return MathHelper.ceil(var4 * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   protected int getPowerLevel(BlockState state) {
      return state.get(POWER);
   }

   @Override
   protected BlockState setPowerLevel(BlockState state, int power) {
      return state.set(POWER, power);
   }

   @Override
   public int getTickRate(World world) {
      return 10;
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
