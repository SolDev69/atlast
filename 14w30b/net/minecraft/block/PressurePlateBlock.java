package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   private final PressurePlateBlock.ActivationRule rule;

   protected PressurePlateBlock(Material stepSound, PressurePlateBlock.ActivationRule material) {
      super(stepSound);
      this.setDefaultState(this.stateDefinition.any().set(POWERED, false));
      this.rule = material;
   }

   @Override
   protected int getPowerLevel(BlockState state) {
      return state.get(POWERED) ? 15 : 0;
   }

   @Override
   protected BlockState setPowerLevel(BlockState state, int power) {
      return state.set(POWERED, power > 0);
   }

   @Override
   protected int calculatePowerLevel(World world, BlockPos pos) {
      Box var3 = this.createBoundingBox(pos);
      List var4;
      switch(this.rule) {
         case EVERYTHING:
            var4 = world.getEntities(null, var3);
            break;
         case MOBS:
            var4 = world.getEntities(LivingEntity.class, var3);
            break;
         default:
            return 0;
      }

      if (!var4.isEmpty()) {
         for(Entity var6 : var4) {
            if (!var6.canAvoidTraps()) {
               return 15;
            }
         }
      }

      return 0;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(POWERED, metadata == 1);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(POWERED) ? 1 : 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, POWERED);
   }

   public static enum ActivationRule {
      EVERYTHING,
      MOBS;
   }
}
