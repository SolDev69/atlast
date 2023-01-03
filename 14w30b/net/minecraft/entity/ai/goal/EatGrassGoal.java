package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.state.BlockStatePredicate;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EatGrassGoal extends Goal {
   private static final Predicate f_72ulagvha = BlockStatePredicate.of(Blocks.TALLGRASS)
      .with(TallPlantBlock.TYPE, Predicates.equalTo(TallPlantBlock.Type.GRASS));
   private MobEntity mob;
   private World world;
   int timer;

   public EatGrassGoal(MobEntity mob) {
      this.mob = mob;
      this.world = mob.world;
      this.setControls(7);
   }

   @Override
   public boolean canStart() {
      if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
         return false;
      } else {
         BlockPos var1 = new BlockPos(this.mob.x, this.mob.y, this.mob.z);
         if (f_72ulagvha.apply(this.world.getBlockState(var1))) {
            return true;
         } else {
            return this.world.getBlockState(var1.down()).getBlock() == Blocks.GRASS;
         }
      }
   }

   @Override
   public void start() {
      this.timer = 40;
      this.world.doEntityEvent(this.mob, (byte)10);
      this.mob.getNavigation().stopCurrentNavigation();
   }

   @Override
   public void stop() {
      this.timer = 0;
   }

   @Override
   public boolean shouldContinue() {
      return this.timer > 0;
   }

   public int getTimer() {
      return this.timer;
   }

   @Override
   public void tick() {
      this.timer = Math.max(0, this.timer - 1);
      if (this.timer == 4) {
         BlockPos var1 = new BlockPos(this.mob.x, this.mob.y, this.mob.z);
         if (f_72ulagvha.apply(this.world.getBlockState(var1))) {
            if (this.world.getGameRules().getBoolean("mobGriefing")) {
               this.world.breakBlock(var1, false);
            }

            this.mob.onEatingGrass();
         } else {
            BlockPos var2 = var1.down();
            if (this.world.getBlockState(var2).getBlock() == Blocks.GRASS) {
               if (this.world.getGameRules().getBoolean("mobGriefing")) {
                  this.world.doEvent(2001, var2, Block.getRawId(Blocks.GRASS));
                  this.world.setBlockState(var2, Blocks.DIRT.defaultState(), 2);
               }

               this.mob.onEatingGrass();
            }
         }
      }
   }
}
